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

import java.util.Arrays;

import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.common.FMLCommonHandler;
import rainwarrior.mt100.CommonProxy;
import rainwarrior.mt100.MT100;
import rainwarrior.mt100.TileEntityMT100;

public class BlockMT100 extends BlockContainer
{
	public BlockMT100(int id, int texture, Material material)
	{
		super(id, texture, material);
		setHardness(.5f);
		setStepSound(Block.soundGravelFootstep);
		setBlockName("BlockMT100");
		setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return new TileEntityMT100(!world.isRemote);
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public int getRenderType()
	{
//		return MT100.proxy.renderer.renderId;
		return -1;
	}

/*	@Override
	public String getTextureFile()
	{
		return CommonProxy.BLOCK_PNG;
	}*/

/*	public int getBlockTextureFromSideAndMetadata(int side, int metadata)
	{
		return 32 + metadata;
	}*/

	public void onBlockAdded(World world, int x, int y, int z)
	{
		MT100.logger.info("onBlockAdded: side: " + FMLCommonHandler.instance().getEffectiveSide());
//		super.onBlockAdded(world, x, y, z);
//		onNeighborBlockChange(world, x, y, z, this.blockID);
//		world.setBlockMetadata(x, y, z, 13);
	}

	public void breakBlock(World world, int x, int y, int z, int id, int metadata)
	{
//		TileEntity t = world.getBlockTileEntity(x, y, z);
//		if(t instanceof TileEntityProxy)
//		{
//			((TileEntityProxy)t).invalidate();
//		}
		super.breakBlock(world, x, y, z, id, metadata);
//		MT100.logger.info(id);
//		MT100.logger.info(metadata);
//		world.notifyBlocksOfNeighborChange(x, y, z, this.blockID);
	}
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float dx, float dy, float dz)
	{
		MT100.logger.info("onBlockActivated: (" + x + "," + y + "," + z + "), side:" + FMLCommonHandler.instance().getEffectiveSide());
		if(FMLCommonHandler.instance().getEffectiveSide().isClient())
		{
			TileEntityMT100 te = (TileEntityMT100)world.getBlockTileEntity(x, y, z);
			if(te == null)
			{
				throw new RuntimeException("no tile entity!");
			}
			FMLCommonHandler.instance().showGuiScreen(te.openGui());
		}
		MT100.logger.info("done!");
		return true;
	}
}
