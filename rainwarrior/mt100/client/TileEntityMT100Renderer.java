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

import net.minecraft.tileentity.TileEntity;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.Tessellator;
import rainwarrior.mt100.*;

import org.lwjgl.opengl.GL11;

public class TileEntityMT100Renderer extends TileEntitySpecialRenderer 
{
	void addSquareX(Tessellator tes, double x, double y1, double y2, double z1, double z2)
	{
		tes.addVertex(x, y1, z1);
		tes.addVertex(x, y2, z1);
		tes.addVertex(x, y2, z2);
		tes.addVertex(x, y1, z2);
	}

	void addSquareY(Tessellator tes, double x1, double x2, double y, double z1, double z2)
	{
		tes.addVertex(x1, y, z1);
		tes.addVertex(x1, y, z2);
		tes.addVertex(x2, y, z2);
		tes.addVertex(x2, y, z1);
	}

	void addSquareZ(Tessellator tes, double x1, double x2, double y1, double y2, double z)
	{
		tes.addVertex(x1, y1, z);
		tes.addVertex(x2, y1, z);
		tes.addVertex(x2, y2, z);
		tes.addVertex(x1, y2, z);
	}

	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTick)
	{
		TileEntityMT100 te = (TileEntityMT100)tile;
		if(te == null) return;
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glScaled(-1D/16D, -1D/16D, -1D/16D);
		GL11.glTranslatef(-15, -14, 0);
		te.screen.render(14, 12);
/*		GL11.glTranslatef(7, 6, -8);
		Tessellator tes = Tessellator.instance;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		tes.startDrawingQuads();
		tes.setColorRGBA(0xB4, 0x8E, 0x4F, 0xFF);
		int w1 = 6;
		int w2 = 3;
		addSquareZ(tes, -7, 8, 8, 6, 8);
		addSquareZ(tes, 7, 8, 6, -8, 8);
		addSquareZ(tes, -8, 7, -6, -8, 8);
		addSquareZ(tes, -8, -7, 8, -6, 8);

		addSquareZ(tes, -w1, 8, w1, 8, w2);
		addSquareZ(tes, w1, 8, -8, w1, w2);
		addSquareZ(tes, -8, w1, -8, -w1, w2);
		addSquareZ(tes, -8, -w1, -w1, 8, w2);

		addSquareZ(tes, -w1, -w1, w1, w1, -8);

		addSquareX(tes, -8, -8, 8, w2, 8);
		addSquareX(tes, 8, 8, -8, w2, 8);
		addSquareY(tes, -8, 8, -8, w2, 8);
		addSquareY(tes, 8, -8, 8, w2, 8);

		addSquareX(tes, -w1, -w1, w1, -8, w2);
		addSquareX(tes, w1, w1, -w1, -8, w2);
		addSquareY(tes, -w1, w1, -w1, -8, w2);
		addSquareY(tes, w1, -w1, w1, -8, w2);

		tes.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		this.bindTextureByName("/rainwarrior/mt100/client/2/rr2-" + te.tick + ".png");
		tes.startDrawingQuads();
		tes.addVertexWithUV(-w1, -w1, -8, 0, 0);
		tes.addVertexWithUV(w1, -w1, -8, 1, 0);
		tes.addVertexWithUV(w1, w1, -8, 1, 1);
		tes.addVertexWithUV(-w1, w1, -8, 0, 1);
		tes.draw();*/
		
		GL11.glPopMatrix();
	}
}
