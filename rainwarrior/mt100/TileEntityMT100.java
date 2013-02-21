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

Linking this software statically or dynamically with other modules is
making a combined work based on this software.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this software give you
permission to link this software with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this software.  If you modify this software, you may extend
this exception to your version of the software, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version.

*/

package rainwarrior.mt100;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.HashSet;
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
	public ScreenParser screenParser;
	boolean isServer;
//	@SideOnly(Side.SERVER)
	QueueBuffer input;
	public QueueBuffer netInput;
	QueueBuffer netOutput;
	DropBuffer d;
	public int tick = 0;
	@SideOnly(Side.CLIENT)

	public TileEntityMT100()
	{
		this(FMLCommonHandler.instance().getEffectiveSide().isServer());
	}
	public TileEntityMT100(boolean isServer)
	{
		screen = new Screen(80, 48, true);
		screenParser = new ScreenParser(screen);
		netInput = new QueueBuffer(Reference.PACKET_SIZE, true);
		netOutput = new QueueBuffer(Reference.PACKET_SIZE * 2, Reference.NET_QUOTA, true);
		this.isServer = isServer;
//		isServer = FMLCommonHandler.instance().getEffectiveSide().isServer();
		if(isServer)
		{
			input = new QueueBuffer();
			input.connect(screenParser);
			input.connect(netOutput);
			// ECHO
			d = new DropBuffer(true);
			netInput.connect(d);
			d.connect(input);
		}
		else
		{
			d = new DropBuffer(true);
			netInput.connect(d);
			d.connect(screenParser);
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
		this.screenParser.readFromNBT(cmp);
		MT100.logger.info("readFromNBT: (" + xCoord + "," + yCoord + "," + zCoord + "), test: " + test + ", side: " + FMLCommonHandler.instance().getEffectiveSide());
	}

	@Override
	public void writeToNBT(NBTTagCompound cmp)
	{
		super.writeToNBT(cmp);
		cmp.setInteger("test", this.test);
		this.screen.writeToNBT(cmp);
		this.screenParser.writeToNBT(cmp);
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
//		MT100.logger.info("update!: " + FMLCommonHandler.instance().getEffectiveSide() + netInput.buffer.size() + " " + netOutput.buffer.size() + " " + screen.buffer.buffer.size());
		if(tick++ >= 26) tick = 0;
		netInput.update();
		if(isServer)
		{
//			MT100.logger.info(" " + input.buffer.size());
			input.update();
//			MT100.logger.info(" " + input.buffer.size());
		}
		else
		{
			if(curGui != null)
			{
				curGui.update();
			}
		}
		screenParser.update();
		if(!netOutput.buffer.isEmpty())
		{
			PacketHandler.sendTileData(this, netOutput.buffer);
			netOutput.buffer.clear();
		}
//		MT100.logger.info("update_2!: " + FMLCommonHandler.instance().getEffectiveSide() + netInput.buffer.size() + " " + netOutput.buffer.size() + " " + screen.buffer.buffer.size());
	}

	@Override
	public int capacity()
	{
		return input.capacity();
	}

	@Override
	public int receive(Iterator<Byte> data)
	{
		return input.receive(data);
	}

	@Override
	public boolean connected(IReceiver rec)
	{
		return netInput.connected(rec);
	}

	@Override
	public boolean connect(IReceiver rec)
	{
		return netInput.connect(rec);
	}

	@Override
	public boolean disconnect(IReceiver rec)
	{
		return netInput.disconnect(rec);
	}
}
