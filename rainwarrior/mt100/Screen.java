/*

Copyright © 2012 RainWarrior

This file is part of MT100.

MT100 is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

MT100 is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with MT100. If not, see <http://www.gnu.org/licenses/>.

*/

package rainwarrior.mt100;

import java.lang.StringBuffer;
import java.lang.Math;
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
	public byte[] curBColor = new byte[4];
	public byte[] curFColor = new byte[4];
	public boolean curBBold = false;
	public boolean curFBold = false;
	protected boolean hasColor;
	static byte[] ansiColor;
	private int x=0;
	private int y=0;
	int scroll=0;
	public int width;
	public int height;
	boolean wrapx = false, wrapy = false;
	boolean doScroll = false;
	static boolean debug = false;

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
			screen[i] = 0;
//			screen[i] = (i & 0xFF);
		}
		resetColors_ARGB(0xFF000000, 0xFFFFFFFF);
	}

	public void setAnsiBColor(int c)
	{
		int sh = (curBBold ? 0x20 : 0) + c * 4;
		setCurBColor(ansiColor[sh + 0], ansiColor[sh + 1], ansiColor[sh + 2], ansiColor[sh + 3]);
	}

	public void setDefBColor()
	{
		setAnsiBColor(0);
	}

	public void setCurBColor_ARGB(int bColor)
	{
		setCurBColor((bColor >> 16) & 0xFF, (bColor >> 8) & 0xFF, bColor & 0xFF, (bColor >> 24) & 0xFF);
	}

	public void setCurBColor(int bR, int bG, int bB, int bA)
	{
		curBColor[0] = (byte)bR;
		curBColor[1] = (byte)bG;
		curBColor[2] = (byte)bB;
		curBColor[3] = (byte)bA;
	}

	public void setAnsiFColor(int c)
	{
		int sh = (curFBold ? 0x20 : 0) + c * 4;
		setCurFColor(ansiColor[sh + 0], ansiColor[sh + 1], ansiColor[sh + 2], ansiColor[sh + 3]);
	}

	public void setDefFColor()
	{
		setAnsiFColor(7);
	}

	public void setCurFColor_ARGB(int fColor)
	{
		setCurFColor((fColor >> 16) & 0xFF, (fColor >> 8) & 0xFF, fColor & 0xFF, (fColor >> 24) & 0xFF);
	}

	public void setCurFColor(int fR, int fG, int fB, int fA)
	{
		curFColor[0] = (byte)fR;
		curFColor[1] = (byte)fG;
		curFColor[2] = (byte)fB;
		curFColor[3] = (byte)fA;
	}

	public void resetColors_ARGB(int bColor, int fColor)
	{
		resetColors((bColor >> 16) & 0xFF, (bColor >> 8) & 0xFF, bColor & 0xFF, (bColor >> 24) & 0xFF,
		            (fColor >> 16) & 0xFF, (fColor >> 8) & 0xFF, fColor & 0xFF, (fColor >> 24) & 0xFF);
	}

	public void resetColors(int bR, int bG, int bB, int bA, int fR, int fG, int fB, int fA)
	{
		curBColor[0] = (byte)bR;
		curBColor[1] = (byte)bG;
		curBColor[2] = (byte)bB;
		curBColor[3] = (byte)bA;
		curFColor[0] = (byte)fR;
		curFColor[1] = (byte)fG;
		curFColor[2] = (byte)fB;
		curFColor[3] = (byte)fA;
		if(hasColor)
		{
			for(int i=0; i < width * height * 8; i+=8)
			{
/*				for(int j=0; j < 4; j++)
				{
					color[i + j] = ansiColor[(i & 0x38) >> 1 | j];
//					color[i + j] = (byte)0x00;
				}
//				color[i + 3] = (byte)0xFF;
				for(int j=0; j < 4; j++)
				{
					color[i + 4 + j] = ansiColor[(((i + 24) & 0x38) >> 1 | j) + 0x20];
				}*/
/*				color[i + 0] = (byte)(i & 0xFF);
				color[i + 1] = (byte)(i & 0xFF);
				color[i + 2] = (byte)(i & 0xFF);
				color[i + 3] = (byte)(0xFF);
				color[i + 4] = (byte)((i + 0x8F) & 0xFF);
				color[i + 5] = (byte)((i + 0x8F) & 0xFF);
				color[i + 6] = (byte)((i + 0x8F) & 0xFF);
				color[i + 7] = (byte)(0xFF);*/
				color[i + 0] = (byte)bR;
				color[i + 1] = (byte)bG;
				color[i + 2] = (byte)bB;
				color[i + 3] = (byte)bA;
				color[i + 4] = (byte)fR;
				color[i + 5] = (byte)fG;
				color[i + 6] = (byte)fB;
				color[i + 7] = (byte)fA;
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

	public void clearLine(int p)
	{
		clearLine(p, y);
	}

	public void clearLine(int p, int Y)
	{
		if(p != 0) for(int i = 0; i <= Math.min(x, width - 1); i++) erase(i, Y);
		if(p != 1) for(int i = Math.max(0, x); i < width; i++) erase(i, Y);
	}

	public void clearScreen(int p)
	{
		MT100.logger.info("clearScreen: " + p);
		clearLine(p);
		int i = scroll;
		boolean passed = false;
		do
		{
			if(i == y)
			{
				passed = true;
			}
			else
			{
				if(p != 0 && !passed) clearLine(2, i);
				if(p != 1 && passed) clearLine(2, i);
			}
			i++;
			if(i >= height) i -= height;
		}
		while(i != scroll);
	}

	public void scrollUp(int p)
	{
		while(p-- > 0)
		{
			scroll--;
			y--;
			if(scroll < 0) scroll += height;
			if(y < 0) y += height;
			clearLine(2, scroll);
		}
	}

	public void scrollDown(int p)
	{
		while(p-- > 0)
		{
			clearLine(2, scroll);
			scroll++;
			y++;
			if(scroll >= height) scroll -= height;
			if(y >= height) y -= height;
		}
	}

	public void readFromNBT(NBTTagCompound cmp)
	{
		// TODO
	}

	public void writeToNBT(NBTTagCompound cmp)
	{
		// TODO
	}

	public void writeWithCheck(int b)
	{
		if(x >= 0 && x < width && y >= 0 && y < height)
		{
			write(b);
		}
	}

	public void write(int b)
	{
		if(hasColor)
		{
			for(int i=0; i < 4; i++)
			{
				color[8 * (x + y * width) + i] = curBColor[i];
			}
			for(int i=0; i < 4; i++)
			{
				color[8 * (x + y * width) + 4 + i] = curFColor[i];
			}
		}
		screen[x + y * width] = b;
	}

	public void erase()
	{
		erase(x, y);
	}

	public void erase(int X, int Y)
	{
		for(int i=0; i < 4; i++)
		{
			color[8 * (X + Y * width) + i] = ansiColor[i];
		}
		for(int i=0; i < 4; i++)
		{
			color[8 * (X + Y * width) + 4 + i] = ansiColor[0x20 + i];
		}
		screen[X + Y * width] = 0;
	}

	public void writeWithShift(int b)
	{
		writeWithCheck(b);
		moveRightWithShift();
	}

	public void moveRightWithShift()
	{
		x++;
		if(x == width && wrapx)
		{
			x -= width;
			moveDownWithShift();
		}
	}

	public void moveDownWithShift()
	{
		y++;
		if(y == height && wrapy) y -= height;
		if(doScroll && y == scroll)
		{
			scroll++;
			clearLine(2);
			if(scroll == height) scroll -= height;
		}
	}

	public void moveLeftWithShift()
	{
		x--;
		if(x == -1 && wrapx)
		{
			x += width;
			moveUpWithShift();
		}
	}

	public void moveUpWithShift()
	{
		if(doScroll && y == scroll)
		{
			scroll--;
			clearLine(2);
			if(scroll == -1) scroll += height;
		}
		y--;
		if(y == -1 && wrapy) y += height;
	}

	public void setX(int X)
	{
		x = X;
		if(x < 0) x = 0;
		if(x >= width) x = width - 1;
	}

	public void setY(int Y)
	{
		if(Y < 0) Y = 0;
		if(Y >= height) Y = height;
		y = Y + scroll;
		if(y >= height) y -= height;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		int ret = y - scroll;
		if(ret < 0) ret += height;
		return ret;
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
		double eps = 1D/128D;
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
						if(I == this.y && j == this.x) // cursor
						{
							setColorToTesselator(tes, k + 4);
							tes.addVertex(x, y + cy * .9, eps);
							tes.addVertex(x, y + cy, eps);
							tes.addVertex(x + cx, y + cy, eps);
							tes.addVertex(x + cx, y + cy * .9, eps);
						}
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
							if(I == this.y && j == this.x) // cursor
							{
								setColorToTesselator(tes, k - 4);
								tes.addVertexWithUV(x, y + cy * .9, eps, tx + eps, ty + eps);
								tes.addVertexWithUV(x, y + cy, eps, tx + eps, ty + curFont.height - eps);
								tes.addVertexWithUV(x + cx, y + cy, eps, tx + curFont.width - eps, ty + curFont.height - eps);
								tes.addVertexWithUV(x + cx, y + cy * .9, eps, tx + curFont.width - eps, ty + eps);
							}
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

