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

import java.util.EnumSet;
import java.util.Set;
import java.util.HashSet;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TickHandler implements ITickHandler
{
	public final boolean isServer;
	public Set<ITicker> tickers = new HashSet<ITicker>();

	public TickHandler(boolean isServer)
	{
		this.isServer = isServer;
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
//		MT100.logger.info("Tick:");
		if(type.contains(TickType.CLIENT))
		{
//			MT100.logger.info("CLIENT");
			assert !isServer;
		}
		if(type.contains(TickType.SERVER))
		{
//			MT100.logger.info("SERVER");
			assert isServer;
		}
		for(ITicker t : tickers)
		{
			t.update();
		}
	}

	@Override
	public EnumSet<TickType> ticks()
	{
		return EnumSet.of(TickType.CLIENT, TickType.SERVER);
	}

	@Override
	public String getLabel()
	{
		return "MT100 Tick Handler";
	}
}
