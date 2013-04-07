/*

Copyright © 2012, 2013 RainWarrior

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

import java.util.ArrayList;
import java.util.Iterator;
import java.nio.channels.SocketChannel;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.io.IOException;

import lombok.Delegate;

import cpw.mods.fml.common.FMLCommonHandler;

public class SocketUART implements ISender, IReceiver, ITicker
{
	@Delegate(types=ISender.class)
	NioBuffer output = new NioBuffer(); // Socket -> UART

	@Delegate(types=IReceiver.class)
	NioBuffer input = new NioBuffer(); // UART -> Socket

	TileEntityMT100 te;
	SocketChannel channel;

	public SocketUART(TileEntityMT100 te, String host, int port)
	{
		MT100.logger.info("Per created!");
		this.te = te;

		output.buffer.flip();

		try
		{
			channel = SocketChannel.open();
			channel.configureBlocking(false);
			channel.connect(new InetSocketAddress(host, port));
			channel.finishConnect();
//			String init = "\0rainwarrior\0rainwarrior\0vt100/115200\0";
//			ByteBuffer data = ByteBuffer.allocate(48);
//			data.clear();
//			data.put(init.getBytes());
//			data.flip();
//			while(data.hasRemaining())
//			{
//				channel.write(data);
//			}
			MT100.logger.info("Socket created!");
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}

//		te.connect(this.input);
//		this.output.connect(te);
	}

	// ITicker
	@Override
	public void update()
	{
//		MT100.logger.info("Per.update");
//		quota = Reference.CC_EVENT_QUOTA;

		try
		{
			input.buffer.flip();
			channel.write(input.buffer);
			input.buffer.compact();

			output.buffer.compact();
			channel.read(output.buffer);
			output.buffer.flip();
			output.update();
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}

	}
}
