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

import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.common.FMLCommonHandler;

import dan200.computer.api.IPeripheral;
import dan200.computer.api.IPeripheralHandler;
import dan200.computer.api.IHostedPeripheral;
import dan200.computer.api.IComputerAccess;

public class PeripheralHandler implements IPeripheralHandler
{
	public IHostedPeripheral getPeripheral(TileEntity te)
	{
		if(!FMLCommonHandler.instance().getEffectiveSide().isServer())
		{
			throw new RuntimeException("cleint peripheral calls");
		}
		if(te instanceof TileEntityMT100)
		{
			TileEntityMT100 t = (TileEntityMT100)te;
			if(t.uart == null)
			{
				t.uart = new PeripheralUART(t.screen);
				t.connect(t.uart);
				t.uart.connect(t);
			}
			return t.uart;
		}
		return null;
	}
}
