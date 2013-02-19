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
