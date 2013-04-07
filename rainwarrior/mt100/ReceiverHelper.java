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
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.nio.ByteBuffer;
import rainwarrior.mt100.MT100;
import cpw.mods.fml.common.FMLCommonHandler;

public class ReceiverHelper
{
/*	public static int receiveIntoQueue(BlockingQueue<Byte> output, Iterator<Byte> data, int quota)
	{
		int ret = 0;
		while(output.remainingCapacity() > 0 && data.hasNext() && quota != 0)
		{
			if(!output.offer(data.next()))
			{
				throw new RuntimeException("queue offer fail");
			}
			if(quota > 0) quota--;
			ret++;
		}
		return ret;
	}

	// Buffer must be in write mode
	public static int receiveIntoByteBuffer(ByteBuffer output, Iterator<Byte> data, int quota)
	{
		int ret = 0;
		while(output.hasRemaining() && data.hasNext() && quota != 0)
		{
			byte b = data.next();
//			MT100.logger.info("RECV: " + b);
			output.put(b);
			if(quota > 0) quota--;
			ret++;
		}
		return ret;
	}

	public static int receiveIntoQueue(BlockingQueue<Byte> output, Iterator<Byte> data)
	{
		return receiveIntoQueue(output, data, Reference.QUOTA);
	}

	public static void updateFromQueue(Collection<IReceiver> recs, BlockingQueue<Byte> input)
	{
		if(recs.size() == 0)
		{
			throw new RuntimeException("BAD");
		}
		int m = input.size();
		for(IReceiver rec : recs)
		{
			int c = rec.capacity();
			if(m > c) m = c;
//			MT100.logger.info("@");
		}
//		MT100.logger.info("3 " + FMLCommonHandler.instance().getEffectiveSide() + m + " " + recs.size());
		ArrayList<Byte> buf = new ArrayList<Byte>(m);
		int ret = input.drainTo(buf, m);
		assert ret == m;
		for(IReceiver rec : recs)
		{
			ret = rec.receive(buf.iterator());
			if(ret != m)
			{
				MT100.logger.severe("Receiver can't handle it's capacity");
			}
//			MT100.logger.info("#");
		}
	}*/

	// buffer must be in read mode
	public static void updateFromByteBuffer(Collection<IReceiver> recs, ByteBuffer input)
	{
		input.flip();
		if(recs.size() == 0)
		{
			throw new RuntimeException("BAD");
		}
		int m = input.remaining();
		int oldLimit = input.limit();
		int oldPos = input.position();
		for(IReceiver rec : recs)
		{
			int c = rec.capacity();
			if(m > c) m = c;
//			MT100.logger.info("@");
		}
//		MT100.logger.info("3 " + FMLCommonHandler.instance().getEffectiveSide() + m + " " + recs.size());
		input.limit(oldPos + m);
//		MT100.logger.info("SEND: " + oldPos + " " + oldLimit + " " + m);
		int ret;
		for(IReceiver rec : recs)
		{
			ret = rec.receive(input);
//			MT100.logger.info("SEND2: " + input.position() + " " + ret);
			input.position(oldPos);
			if(ret != m)
			{
				MT100.logger.severe("Receiver can't handle it's capacity");
			}
//			MT100.logger.info("#");
		}
		input.position(oldPos + m);
		input.limit(oldLimit);
		input.compact();
	}
}
