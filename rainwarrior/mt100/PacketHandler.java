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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.server.management.PlayerInstance;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PacketHandler implements IPacketHandler
{
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player)
	{
		try
		{
			DataInputStream str = new DataInputStream(new ByteArrayInputStream(packet.data));
			byte id = str.readByte();
			assert packet.channel.equals(Reference.CHANNEL_NAME);
			switch(id)
			{
/*				case Reference.PACKET_CONNECT:
					MT100.logger.info("connect");
					handlerId = str.readInt();
					int x = str.readInt();
					int y = str.readInt();
					int z = str.readInt();
					assert FMLCommonHandler.instance().getEffectiveSide().isClient();
					if(handlers.containsKey(handlerId))
					{
						MT100.logger.severe("duplicate connect packet");
						return;
					}
					NetworkByteHandler handler = new NetworkByteHandler(handlerId);
					handlers.put(handlerId, handler);
					TileEntityMT100 te = (TileEntityMT100)((EntityPlayer)player).worldObj.getBlockTileEntity(x, y, z);
					if(te == null)
					{
						MT100.logger.severe("connect with unknown entity");
						return;
					}
					te.handler = handler;
					if(!handler.connect(te.screen))
					{
						MT100.logger.severe("cannot connect handler and screen");
					}
				break;*/

/*				case Reference.PACKET_TILE_REQUEST:
					assert FMLCommonHandler.instance().getEffectiveSide().isServer();
					int x = str.readInt();
					int y = str.readInt();
					int z = str.readInt();
					TileEntityMT100 te = (TileEntityMT100)((EntityPlayer)player).worldObj.getBlockTileEntity(x, y, z);
					sendPacket();
				case Reference.PACKET_TILE_RESPONSE:
					assert FMLCommonHandler.instance().getEffectiveSide().isClient();
					int x = str.readInt();
					int y = str.readInt();
					int z = str.readInt();
					TileEntityMT100 te = (TileEntityMT100)((EntityPlayer)player).worldObj.getBlockTileEntity(x, y, z);
					NBTTagCompound cmp = new NBTTagCompound();
					cmp = (NBTTagCompound)cmp.readNamedTag(str);
					te.readFromNBT(cmp);
				break;*/

				case Reference.PACKET_DATA:
//					MT100.logger.info("1 " + FMLCommonHandler.instance().getEffectiveSide());
					int x = str.readInt();
					int y = str.readInt();
					int z = str.readInt();
					TileEntityMT100 te = (TileEntityMT100)((EntityPlayer)player).worldObj.getBlockTileEntity(x, y, z);
					if(te == null)
					{
						MT100.logger.severe("No handler for packet");
						return;
					}
					short size = str.readShort();
					ArrayList<Byte> data = new ArrayList<Byte>(size);
					for(int i=0; i < size; i++)
					{
						data.add(str.readByte());
					}
					int ret = te.netInput.receive(data.iterator());
					if(ret < data.size())
					{
						MT100.logger.severe("Dropping on packet receiving");
					}
//					MT100.logger.info("1 " + FMLCommonHandler.instance().getEffectiveSide() + ret);
				break;

				default:
					MT100.logger.severe("Received bad packet: " + id);
				break;
			}
		}
		catch(IOException e)
		{
			MT100.logger.severe("IOException on packet receiving: " + e);
		}
	}

	public static Packet getPacket(Object... args)
	{
		try
		{
			boolean isServer = FMLCommonHandler.instance().getEffectiveSide().isServer();
			int i = 0;
			Byte id = (Byte)args[i++];
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			DataOutputStream str = new DataOutputStream(buf);
			str.writeByte(id);
			switch(id)
			{
/*				case Reference.PACKET_CONNECT:
					assert isServer;
					str.writeInt((Integer)args[i++]); // handler id
					TileEntityMT100 te = (TileEntityMT100)args[i++];
					str.writeInt(te.xCoord);
					str.writeInt(te.yCoord);
					str.writeInt(te.zCoord);
				break;*/
/*				case Reference.PACKET_TILE_RESPONSE:
					assert FMLCommonHandler.instance().getEffectiveSide().isServer();
					TileEntityMT100 te = (TileEntityMT100)args[i++];
					str.writeInt(te.xCoord);
					str.writeInt(te.yCoord);
					str.writeInt(te.zCoord);
					NBTTagCompound cmp = new NBTTagCompound();
					te.writeToNBT(cmp);
					cmp.writeNamedTag(cmp, str);
				break;*/
				case Reference.PACKET_DATA:
					TileEntityMT100 te = (TileEntityMT100)args[i++];
					str.writeInt(te.xCoord);
					str.writeInt(te.yCoord);
					str.writeInt(te.zCoord);
					Collection<Byte> data = (Collection<Byte>) args[i++];
					str.writeShort(data.size());
					for(Byte b : data)
					{
						str.writeByte(b);
					}
				break;
				default:
					MT100.logger.severe("Sending bad packet: " + id);
				break;
			}
			Packet250CustomPayload packet = new Packet250CustomPayload(Reference.CHANNEL_NAME, buf.toByteArray());
			if(i != args.length)
			{
				MT100.logger.severe("getPacket argument count mismatch");
			}
			return packet;
		}
		catch(IOException e)
		{
			MT100.logger.severe("IOException on packet receiving: " + e);
		}
		return null;
	}

	public static void sendTileData(TileEntityMT100 te, Object data)
	{
		Packet packet = getPacket(Reference.PACKET_DATA, te, data);
//		MT100.logger.info("2 " + FMLCommonHandler.instance().getEffectiveSide());
		if(FMLCommonHandler.instance().getEffectiveSide().isServer())
		{
//			MT100.logger.info("World: " + te.worldObj);
			PlayerInstance players = ((WorldServer)te.worldObj).getPlayerManager().getOrCreateChunkWatcher(te.xCoord >> 4, te.zCoord >> 4, false);
			if(players != null)
			{
				players.sendToAllPlayersWatchingChunk(packet);
			}
		}
		else
		{
			PacketDispatcher.sendPacketToServer(packet);
		}
	}

	@SideOnly(Side.CLIENT)
	public static void sendToServer(Object... args)
	{
		PacketDispatcher.sendPacketToServer(getPacket(args));
	}

	@SideOnly(Side.SERVER)
	public static void sendToAllWatchingTile(Object... args)
	{
//		fs.setAccessible(true);
//		List acc = (List)World.class.getDeclaredField("worldAccesses").get(world);
//		for(int i=0; i < acc.size(); i++)
//		{
//			WorldServer s = (WorldServer)WorldManager.class.getDeclaredField("theWorldServer").get(acc.get(i));
			
//		}
	}

	@SideOnly(Side.SERVER)
	public static void sendToPlayer(Player player, Object... args)
	{
		PacketDispatcher.sendPacketToPlayer(getPacket(args), player);
	}
}
