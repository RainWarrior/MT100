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

import java.lang.StringBuffer;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;
import rainwarrior.mt100.client.PstFontRegistry;
import rainwarrior.mt100.client.PstFont;

public class Screen
{
	public int[] screen;
	public byte[] color; // RGBA background, RGBA foreground
	protected boolean hasColor;
	static byte[] ansiColor;
	int x=0; int y=0;
	int scroll=0;
	public int width;
	public int height;
	static boolean debug = true;

	static
	{ // probably can generate by simple loop
		ansiColor = new byte[]{ // i kinda hate java
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0xFF,
			(byte)0xAA, (byte)0x00, (byte)0x00, (byte)0xFF,
			(byte)0x00, (byte)0xAA, (byte)0x00, (byte)0xFF,
			(byte)0xAA, (byte)0xAA, (byte)0x00, (byte)0xFF,
			(byte)0x00, (byte)0x00, (byte)0xAA, (byte)0xFF,
			(byte)0xAA, (byte)0x00, (byte)0xAA, (byte)0xFF,
			(byte)0x00, (byte)0xAA, (byte)0xAA, (byte)0xFF,
			(byte)0xAA, (byte)0xAA, (byte)0xAA, (byte)0xFF,
			(byte)0x55, (byte)0x55, (byte)0x55, (byte)0xFF,
			(byte)0xFF, (byte)0x55, (byte)0x55, (byte)0xFF,
			(byte)0x55, (byte)0xFF, (byte)0x55, (byte)0xFF,
			(byte)0xFF, (byte)0xFF, (byte)0x55, (byte)0xFF,
			(byte)0x55, (byte)0x55, (byte)0xFF, (byte)0xFF,
			(byte)0xFF, (byte)0x55, (byte)0xFF, (byte)0xFF,
			(byte)0x55, (byte)0xFF, (byte)0xFF, (byte)0xFF,
			(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF
		};
	}

	public Screen(int width, int height, boolean hasColor)
	{
		screen = new int[width * height];
		this.hasColor = hasColor;
		this.width = width;
		this.height = height;
		if(hasColor)
		{
			color = new byte[width * height * 8];
		}
		else
		{
			color = new byte[8];
		}
		for(int i=0; i < width * height; i++)
		{
			screen[i] = (i & 0xFF);
		}
		resetColors_ARGB(0xFF000000, 0xFFFFFFFF);
	}

	public void resetColors_ARGB(int bColor, int fColor)
	{
		resetColors((bColor >> 16) & 0xFF, (bColor >> 8) & 0xFF, bColor & 0xFF, (bColor >> 24) & 0xFF,
		            (fColor >> 16) & 0xFF, (fColor >> 8) & 0xFF, fColor & 0xFF, (fColor >> 24) & 0xFF);
	}

	public void resetColors(int bR, int bG, int bB, int bA, int fR, int fG, int fB, int fA)
	{
		if(hasColor)
		{
			for(int i=0; i < width * height * 8; i+=8)
			{
				for(int j=0; j < 4; j++)
				{
					color[i + j] = ansiColor[(i & 0x38) >> 1 | j];
//					color[i + j] = (byte)0x00;
				}
//				color[i + 3] = (byte)0xFF;
				for(int j=0; j < 4; j++)
				{
					color[i + 4 + j] = ansiColor[(((i + 24) & 0x38) >> 1 | j) + 0x20];
				}
/*				color[i + 0] = (byte)(i & 0xFF);
				color[i + 1] = (byte)(i & 0xFF);
				color[i + 2] = (byte)(i & 0xFF);
				color[i + 3] = (byte)(0xFF);
				color[i + 4] = (byte)((i + 0x8F) & 0xFF);
				color[i + 5] = (byte)((i + 0x8F) & 0xFF);
				color[i + 6] = (byte)((i + 0x8F) & 0xFF);
				color[i + 7] = (byte)(0xFF);*/
/*				color[i + 0] = (byte)bR;
				color[i + 1] = (byte)bG;
				color[i + 2] = (byte)bB;
				color[i + 3] = (byte)bA;
				color[i + 4] = (byte)fR;
				color[i + 5] = (byte)fG;
				color[i + 6] = (byte)fB;
				color[i + 7] = (byte)fA;*/
			}
		}
		else
		{
			color[0] = (byte)bR;
			color[1] = (byte)bG;
			color[2] = (byte)bB;
			color[3] = (byte)bA;
			color[4] = (byte)fR;
			color[5] = (byte)fG;
			color[6] = (byte)fB;
			color[7] = (byte)fA;
		}
	}

	public void setColorToTesselator(Tessellator tes, int k)
	{
		int R = color[k + 0];
		int G = color[k + 1];
		int B = color[k + 2];
		int A = color[k + 3];
		if(R < 0) R += 0x100; // I hate java
		if(G < 0) G += 0x100; // I hate java
		if(B < 0) B += 0x100; // I hate java
		if(A < 0) A += 0x100; // I hate java
		tes.setColorRGBA(R, G, B, A);
	}

	public void readFromNBT(NBTTagCompound cmp)
	{
		// TODO
	}

	public void writeToNBT(NBTTagCompound cmp)
	{
		// TODO
	}

	public void writeWithShift(int b, boolean wrapx, boolean wrapy, boolean doScroll)
	{
		if(x >= 0 && x < width && y >= 0 && y < width)
		{
			screen[x + y * width] = b;
		}
		x++;
		if(x == width && wrapx)
		{
			x = 0;
			y++;
			if(y == height && wrapy) y = 0;
			if(doScroll && y == scroll)
			{
				this.scroll++;
				if(scroll >= height) scroll -= height;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void render(float cw, float ch)
	{
		Tessellator tes = Tessellator.instance;
		GL11.glMatrixMode(GL11.GL_TEXTURE);
		GL11.glPushMatrix();
		PstFont curFont = PstFontRegistry.nullFont, tmpFont;
		curFont.bindFontTexture();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		double cx = cw/width;
		double cy = ch/height;
		double tx, ty;
		double x, y;
		double eps = 1D/8D;
		Integer c;
		// First pass - background, second pass - foreground
		for(int pass=0; pass < 2; pass++)
		{
			tes.startDrawingQuads();
			if(pass == 0)
			{
				GL11.glDisable(GL11.GL_TEXTURE_2D);
			}
			else
			{
				GL11.glEnable(GL11.GL_TEXTURE_2D);
			}
			for(int i=0, I=scroll,k=pass * 4 + scroll * width * 8; i < height; i++, I++)
			{
				if(I >= height) I -= height;
				if(k >= width * height * 8) k -= width * height * 8;
				for(int j=0; j < width; j++, k+=8)
				{
					x = j * cx;
					y = i * cy;
					if(debug) MT100.logger.info("k: " + k + " " + color[k + 0] + " " + color[k + 1] + " " + color[k + 2] + " " + color[k + 3]);
					if(pass == 0)
					{
						setColorToTesselator(tes, k);
						tes.addVertex(x, y, 0F);
						tes.addVertex(x, y + cy, 0F);
						tes.addVertex(x + cx, y + cy, 0F);
						tes.addVertex(x + cx, y, 0F);
					}
					else
					{
						c = screen[I * width + j];
						if(debug) MT100.logger.info("c: " + c);
						if(debug) MT100.logger.info("font: " + curFont.fontFile);
						tmpFont = PstFontRegistry.getFont(c);
						if(tmpFont != curFont)
						{
							curFont = tmpFont;
							if(debug) MT100.logger.info("switching!");
							tes.draw();
							tes.startDrawingQuads();
							GL11.glMatrixMode(GL11.GL_TEXTURE);
							curFont.bindFontTexture();
							GL11.glMatrixMode(GL11.GL_MODELVIEW);
						}
						setColorToTesselator(tes, k);
						c = PstFontRegistry.getIndex(c);
						if(debug) MT100.logger.info("c2: " + c);
						if(c != null)
						{
							tx = c / (curFont.length >> curFont.lShift) * curFont.width;
							if(debug) MT100.logger.info("tx: " + (c / (curFont.length >> curFont.lShift)));
							ty = c % (curFont.length >> curFont.lShift) * curFont.height;
							if(debug) MT100.logger.info("ty: " + (c % (curFont.length >> curFont.lShift)));
							tes.addVertexWithUV(x, y, 0F, tx + eps, ty + eps);
							tes.addVertexWithUV(x, y + cy, 0F, tx + eps, ty + curFont.height - eps);
							tes.addVertexWithUV(x + cx, y + cy, 0F, tx + curFont.width - eps, ty + curFont.height - eps);
							tes.addVertexWithUV(x + cx, y, 0F, tx + curFont.width - eps, ty + eps);
/*							GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
							GL11.glTexCoord2d(tx, ty);
							GL11.glVertex3d(x, y, 0.F);
							GL11.glTexCoord2d(tx, ty + curFont.height);
							GL11.glVertex3d(x, y + cy, 0.F);
							GL11.glTexCoord2d(tx + curFont.width, ty);
							GL11.glVertex3d(x + cx, y, 0.F);
							GL11.glTexCoord2d(tx + curFont.width, ty + curFont.height);
							GL11.glVertex3d(x + cx, y + cy, 0.F);
							GL11.glEnd();*/
						}
					}
				}
			}
			tes.draw();
		}
		GL11.glMatrixMode(GL11.GL_TEXTURE);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		if(debug)
		{
			MT100.logger.info("cx: " + cx);
			MT100.logger.info("cy: " + cy);
		}
		debug = false;
	}
}

