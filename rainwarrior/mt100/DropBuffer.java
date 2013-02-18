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
import cpw.mods.fml.common.FMLLog;

public class DropBuffer implements ISender, IUnsafeSender, IReceiver
{
	IReceiver rec;
	SafeSenderAdapter safe;
	boolean noisy;

	public DropBuffer(boolean noisy)
	{
		this.noisy = noisy;
		safe = new SafeSenderAdapter(this);
	}

	@Override
	public int capacity()
	{
		return Integer.MAX_VALUE;
	}

	@Override
	public int receive(Iterator<Byte> data)
	{
		int ret = rec.receive(data);
//		MT100.logger.info("DropBuffer!!!!");
		if(noisy && data.hasNext())
		{
			MT100.logger.warning("DropBuffer dropping");
		}
		return ret;
	}

	@Override
	public boolean connected(IReceiver rec)
	{
		return this.rec == rec;
	}

	@Override
	public void doConnect(IReceiver rec)
	{
		this.rec = rec;
	}

	@Override
	public void doDisconnect(IReceiver rec)
	{
		if(this.rec == rec) this.rec = null;
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

}
