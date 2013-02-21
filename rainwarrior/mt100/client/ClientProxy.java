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

package rainwarrior.mt100.client;

import net.minecraftforge.client.MinecraftForgeClient;
import rainwarrior.mt100.*;
import rainwarrior.mt100.client.PstFontRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.util.Timer;

public class ClientProxy extends CommonProxy
{
	@Override
	public void registerStuff()
	{
		super.registerStuff();
		PstFontRegistry.init();
		for(int i=0; i < 27; i++)
		{
			MinecraftForgeClient.preloadTexture("/rainwarrior/mt100/client/2/rr2-" + i + ".png");
		}
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMT100.class, new TileEntityMT100Renderer());
/*		try
		{
			Timer.class.getDeclaredField("worldAccesses").setAccessible(true);
		}
		catch(Exception e)
		{
			MT100.logger.severe("Failed to change access to timer");
		}*/
	}
}
