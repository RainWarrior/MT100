/*

Copyright © 2012 RainWarrior

This file is part of MT100.

MT100 is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

MT100 is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with MT100. If not, see <http://www.gnu.org/licenses/>.

Additional permission under GNU GPL version 3 section 7

If you modify this Program, or any covered work, by linking or combining it
with Minecraft and/or MinecraftForge (or a modified version of Minecraft
and/or Minecraft Forge), containing parts covered by the terms of
Minecraft Terms of Use and/or Minecraft Forge Public Licence, the licensors
of this Program grant you additional permission to convey the resulting work.

*/

package rainwarrior.mt100;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.HashSet;
import lombok.Delegate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.network.INetworkManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.relauncher.Side;

import rainwarrior.mt100.client.GuiMT100;
import rainwarrior.mt100.client.PstFontRegistry;

public class TileEntityMT100 extends TileEntity implements IReceiver, ISender // ITicker
{
	static int nextId = 0;
	public int test;
	@SideOnly(Side.CLIENT)
	public GuiMT100 curGui;
	public Screen screen;
	public Parser parser;
	boolean isServer;
	public PeripheralUART uart = null;

	@Delegate(types=IReceiver.class)
	public QueueBuffer input;
	@Delegate(types=ISender.class)
	public QueueBuffer netInput;
	QueueBuffer netOutput;
	DropBuffer d;

	public Object updateLock = new Object();

	public boolean backlight = true;
	@SideOnly(Side.CLIENT)

	public TileEntityMT100()
	{
		this(FMLCommonHandler.instance().getEffectiveSide().isServer());
	}
	public TileEntityMT100(boolean isServer)
	{
		screen = new Screen(40, 24, true);
		parser = new Parser(new ScreenConsumer(screen));
		netInput = new QueueBuffer(Reference.PACKET_SIZE, true);
		netOutput = new QueueBuffer(Reference.PACKET_SIZE * 2, Reference.NET_QUOTA, true);
		this.isServer = isServer;
//		isServer = FMLCommonHandler.instance().getEffectiveSide().isServer();
		if(isServer)
		{
			input = new QueueBuffer();
			input.connect(parser);
			input.connect(netOutput);
			// ECHO
//			d = new DropBuffer(true);
//			netInput.connect(d);
//			d.connect(input);
		}
		else
		{
			netInput.connect(parser);
		}
//		MT100.logger.info("new TileEntityMT100, side: " + FMLCommonHandler.instance().getEffectiveSide());
	}

	@Override
	public Packet getDescriptionPacket()
	{
		assert FMLCommonHandler.instance().getEffectiveSide().isServer();
		NBTTagCompound cmp = new NBTTagCompound();
		writeToNBT(cmp);
		MT100.logger.info("getPacket, world: " + this.worldObj);
//		cmp.setInteger("handlerId", handler.id);
		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 255, cmp);
	}

	@Override
	public void onDataPacket(INetworkManager netManager, Packet132TileEntityData packet)
	{
		assert FMLCommonHandler.instance().getEffectiveSide().isClient();
		readFromNBT(packet.customParam1);
//		int handlerId = packet.customParam1.getInteger("handlerId");
//		this.handler = new NetworkByteHandler(handlerId);
	}

	@Override
	public void readFromNBT(NBTTagCompound cmp)
	{
		super.readFromNBT(cmp);
		this.test = cmp.getInteger("test") + 1;
		this.screen.readFromNBT(cmp);
		MT100.logger.info("readFromNBT: (" + xCoord + "," + yCoord + "," + zCoord + "), test: " + test + ", side: " + FMLCommonHandler.instance().getEffectiveSide());
	}

	@Override
	public void writeToNBT(NBTTagCompound cmp)
	{
		super.writeToNBT(cmp);
		cmp.setInteger("test", this.test);
		this.screen.writeToNBT(cmp);
	}

	@SideOnly(Side.CLIENT)
	public GuiMT100 openGui()
	{
		this.curGui = new GuiMT100(this, this.screen);
		curGui.connect(netOutput);
		return this.curGui;
	}

	@Override
	public void updateEntity()
	{
		synchronized(updateLock)
		{
//			MT100.logger.info("update!: " + FMLCommonHandler.instance().getEffectiveSide() + netInput.buffer.size() + " " + netOutput.buffer.size() + " " + screen.buffer.buffer.size());
			netInput.update();
			if(isServer)
			{
//				MT100.logger.info(" " + input.buffer.size());
//				if(uart != null) uart.update();
				input.update();
//				MT100.logger.info(" " + input.buffer.size());
			}
			else
			{
				if(curGui != null)
				{
					curGui.update();
				}
			}
			parser.update();
			if(!netOutput.buffer.isEmpty())
			{
				PacketHandler.sendTileData(this, netOutput.buffer);
				netOutput.buffer.clear();
			}
//			MT100.logger.info("update_2!: " + FMLCommonHandler.instance().getEffectiveSide() + netInput.buffer.size() + " " + netOutput.buffer.size() + " " + screen.buffer.buffer.size());
		}
	}
}
