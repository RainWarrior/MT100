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
import rainwarrior.mt100.client.PstFontRenderer;

public class TileEntityMT100 extends TileEntity implements IReceiver, ISender // ITicker
{
	static int nextId = 0;
	public int test;
	@SideOnly(Side.CLIENT)
	public GuiMT100 curGui;
	public Screen screen;
	boolean isServer;
//	@SideOnly(Side.SERVER)
	QueueBuffer input;
	public QueueBuffer netInput;
	QueueBuffer netOutput;
	DropBuffer d;
	public int tick = 0;
	@SideOnly(Side.CLIENT)
	public PstFontRenderer pstFontRenderer;

	public TileEntityMT100()
	{
		this(FMLCommonHandler.instance().getEffectiveSide().isServer());
	}
	public TileEntityMT100(boolean isServer)
	{
		screen = new Screen(40, 12);
		netInput = new QueueBuffer(Reference.PACKET_SIZE, true);
		netOutput = new QueueBuffer(Reference.PACKET_SIZE * 2, Reference.NET_QUOTA, true);
		this.isServer = isServer;
//		isServer = FMLCommonHandler.instance().getEffectiveSide().isServer();
		if(isServer)
		{
			input = new QueueBuffer();
			input.connect(screen);
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
			d.connect(screen);
//			this.pstFontRenderer = new PstFontRenderer("Lat15-VGA8.psf.gz");
//			this.pstFontRenderer = new PstFontRenderer("Lat15-Fixed16.psf.gz");
//			this.pstFontRenderer = new PstFontRenderer("Lat15-VGA32x16.psf.gz");
			this.pstFontRenderer = new PstFontRenderer("Uni2-Terminus16.psf.gz");
//			this.pstFontRenderer = new PstFontRenderer("Lat15-Terminus14.psf.gz");
//			this.pstFontRenderer = new PstFontRenderer("Lat15-Terminus20x10.psf.gz");
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
		screen.update();
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
