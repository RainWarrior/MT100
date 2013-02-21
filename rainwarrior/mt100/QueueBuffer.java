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
	public void update()
	{
		if(!buffer.isEmpty())
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
	public int receive(Iterator<Byte> data)
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
