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

import java.util.Iterator;

public class SafeSenderAdapter implements ISender
{
	IUnsafeSender instance;
	boolean noisy;

	public SafeSenderAdapter(IUnsafeSender instance, boolean noisy)
	{
		this.instance = instance;
		this.noisy = noisy;
	}

	public SafeSenderAdapter(IUnsafeSender instance)
	{
		this(instance, true);
	}

//	@Override
	public boolean connect(IReceiver rec)
	{
		if(!instance.connected(rec))
		{
			instance.doConnect(rec);
			return true;
		}
		if(noisy)
		{
			MT100.logger.warning("SenderAdapter double connecting");
		}
		return false;
	}

//	@Override
	public boolean disconnect(IReceiver rec)
	{
		if(instance.connected(rec))
		{
			instance.doDisconnect(rec);
			return true;
		}
		if(noisy)
		{
			MT100.logger.warning("SenderAdapter bad disconnect");
		}
		return false;
	}

	@Override
	public boolean connected(IReceiver rec)
	{
		return instance.connected(rec);
	}
}
