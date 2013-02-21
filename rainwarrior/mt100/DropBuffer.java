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
