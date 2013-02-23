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
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import cpw.mods.fml.common.FMLLog;

public class QueueBuffer implements ISender, IUnsafeSender, IReceiver, ITicker
{
	public BlockingQueue<Byte> buffer;
	public HashSet<IReceiver> recs = new HashSet<IReceiver>();
	SafeSenderAdapter safe;
	public int quota;
	public int size;
	boolean noisy;

	/*
	 * Quota affects receiving speed
	 */
	public QueueBuffer(int size, int quota, boolean noisy)
	{
//		MT100.registerTicker(this);
		this.quota = quota;
		this.buffer = new ArrayBlockingQueue<Byte>(size);
		safe = new SafeSenderAdapter(this);
	}

	public QueueBuffer(int size, boolean noisy)
	{
		this(size, Reference.QUOTA, noisy);
	}

	public QueueBuffer(boolean noisy)
	{
		this(Reference.QUEUE_SIZE, Reference.QUOTA, noisy);
	}

	public QueueBuffer()
	{
		this(Reference.QUEUE_SIZE, Reference.QUOTA, true);
	}

	@Override
	public synchronized void update()
	{
		if(!buffer.isEmpty() && !recs.isEmpty())
		{
			ReceiverHelper.updateFromQueue(recs, buffer);
		}
	}

	@Override
	public int capacity()
	{
		return buffer.remainingCapacity();
	}

	@Override
	public synchronized int receive(Iterator<Byte> data)
	{
		int ret = ReceiverHelper.receiveIntoQueue(buffer, data, quota);
		//update();
		return ret;
	}

	@Override
	public boolean connected(IReceiver rec)
	{
		return recs.contains(rec);
	}

	@Override
	public void doConnect(IReceiver rec)
	{
		recs.add(rec);
	}

	@Override
	public void doDisconnect(IReceiver rec)
	{
		recs.remove(rec);
	}

	@Override
	public boolean connect(IReceiver rec)
	{
		return safe.connect(rec);
	}

	@Override
	public boolean disconnect(IReceiver rec)
	{
		return safe.disconnect(rec);
	}

	public void disconnectAll()
	{
		recs.clear();
	}
}
