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

import java.util.logging.Logger;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import rainwarrior.mt100.Reference;
import rainwarrior.mt100.BlockMT100;
import rainwarrior.mt100.TileEntityMT100;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = "0.01")
@NetworkMod(channels = {Reference.CHANNEL_NAME}, clientSideRequired = true, serverSideRequired = false, packetHandler = PacketHandler.class)
//@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class MT100 // extends BaseMod
{
	public static final int  blockId = 500;

	@Instance(Reference.MOD_ID/*"rainwarrior_MT100"*/)
	public static MT100 instance;

	@SidedProxy(clientSide=Reference.CLIENT_PROXY_CLASS, serverSide=Reference.SERVER_PROXY_CLASS)
	public static CommonProxy proxy;

	public static final Block blockMT100 = new BlockMT100(blockId, 16, Material.ground);

	public static final Logger logger = Logger.getLogger(Reference.MOD_ID);
//	public static Logger logger2;

	public static final TickHandler clientTickHandler = new TickHandler(false);
	public static final TickHandler serverTickHandler = new TickHandler(true);

//	public HashMap<Integer, NetworkByteHandler> handlers = new HashMap<Integer, NetworkByteHandler>();

	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
//		logger2 = event.getModLog();
//		logger.info("first:  " + logger);
//		logger.info("second: " + logger2);
	}

	@Init
	public void load(FMLInitializationEvent event)
	{
		GameRegistry.addRecipe(new ItemStack(Item.pickaxeDiamond), new Object[]
			{
				"XXX", "XXX", "XXX", 'X', Block.dirt // LOL
			});
		LanguageRegistry.addName(blockMT100, "MT100 Block");
		MinecraftForge.setBlockHarvestLevel(blockMT100, "shovel", 0);
		GameRegistry.registerBlock(blockMT100, "MT100_Block");
		GameRegistry.registerTileEntity(TileEntityMT100.class, "MT100_TileEntity");
		proxy.registerStuff();

		TickRegistry.registerTickHandler(clientTickHandler, Side.CLIENT);
		TickRegistry.registerTickHandler(serverTickHandler, Side.SERVER);
	}

	@PostInit
	public void postInit(FMLPostInitializationEvent event)
	{
		MT100.logger.info("Copyright (C) 2012 RainWarrior");
		MT100.logger.info("License GPLv3+: GNU GPL version 3 or later <http://gnu.org/licenses/gpl.html>.");
		MT100.logger.info("MT100 is free software: you are free to change and redistribute it.");
		MT100.logger.info("There is NO WARRANTY, to the extent permitted by law.");
	}

	public static void registerTicker(ITicker t)
	{
		(FMLCommonHandler.instance().getEffectiveSide().isClient() ? clientTickHandler : serverTickHandler).tickers.add(t);
	}
}
