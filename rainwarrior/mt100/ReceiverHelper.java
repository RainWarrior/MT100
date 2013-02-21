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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import rainwarrior.mt100.MT100;
import cpw.mods.fml.common.FMLCommonHandler;

public class ReceiverHelper
{
	public static int receiveIntoQueue(BlockingQueue<Byte> output, Iterator<Byte> data, int quota)
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
	}
}
