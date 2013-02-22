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

package rainwarrior.mt100.client;

import java.io.InputStream;
import java.io.DataInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import rainwarrior.mt100.*;

public class PstFont
{
	public IntBuffer texture;

	int charsize;
	public int width;
	public int height;
	public int length;
	public int lShift;
	int textureWidth;
	int textureHeight;
	public String fontFile = null;
	boolean debug = false;

	public PstFont()
	{
		charsize = 1;
		width = 1;
		height = 1;
		length = 1;
		lShift=0;
		textureWidth = 1;
		textureHeight = 1;
		ByteBuffer textureBuffer = BufferUtils.createByteBuffer(1);
		texture = BufferUtils.createIntBuffer(1);
		GL11.glGenTextures(texture);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.get(0));
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0x0, GL11.GL_ALPHA, textureWidth, textureHeight, 0x0, GL11.GL_ALPHA, GL11.GL_UNSIGNED_BYTE, textureBuffer);
	}

	public PstFont(String fontFile)
	{
		this.fontFile = fontFile;
		boolean hasUni;
		boolean psf1;
		try
		{
			InputStream str = this.getClass().getResourceAsStream(fontFile);
			if(str != null)
			{
				GZIPInputStream stream = new GZIPInputStream(str);
				DataInputStream file = new DataInputStream(stream);
				byte[] b = new byte[4];
				for(int i=0; i < 4; i++) b[i] = file.readByte();
				int hoffset;
				if(b[0] == (byte)0x36 && b[1] == (byte)04) // psf1 magic
				{
					psf1 = true;
					width = 8;
					height = charsize = b[3];
					length = ((b[2] & 0x01) != 0) ? 512 : 256;
					lShift = 4;
					hasUni = ((b[2] & 0x02) != 0);
					hoffset = 0;
				}
				else if(b[0] == (byte)0x72 && b[1] == (byte)0xB5 && b[2] == (byte)0x4A && b[3] == (byte)0x86) // psf2 magic
				{
					psf1 = false;
					ByteBuffer header = ByteBuffer.allocate(28);
					file.readFully(header.array(), 0, 28);
					header.order(ByteOrder.LITTLE_ENDIAN);
					int version = header.getInt();
					hoffset = header.getInt() - 32;
					MT100.logger.info("hoffset: " + hoffset);
					int flags = header.getInt();
					hasUni = ((flags & 0x01) != 0);
					length = header.getInt();
					if(length != 256 && length != 512)
					{
						throw new RuntimeException("PSF2 file of unsupported length:" + length);
					}
					lShift = 4;
					charsize = header.getInt();
					height = header.getInt();
					width = header.getInt();
				}
				else
				{
					MT100.logger.severe("File " + fontFile + " is not a PSf file: " + b[0] + " " + b[1] + " " + b[2] + " " + b[3]);
					throw new java.io.IOException();
				}
				MT100.logger.info("width: " + width);
				MT100.logger.info("height: " + height);
				MT100.logger.info("charsize: " + charsize);
				MT100.logger.info("length: " + length);
				ByteBuffer chars = ByteBuffer.allocate(length * charsize);
				chars.order(ByteOrder.LITTLE_ENDIAN);
				file.skipBytes(hoffset);
				file.readFully(chars.array(), 0, length * charsize);

				int roundWidth = charsize / height; // bytes
				textureWidth = 1;
				while(textureWidth < (1 << lShift) * width)
				{
					textureWidth <<= 1;
				}
				textureHeight = 1;
				while(textureHeight < (length >> lShift) * height)
				{
					textureHeight <<= 1;
				}

				ByteBuffer textureBuffer = BufferUtils.createByteBuffer(textureWidth * textureHeight);

				byte bt;
				for(int i=0; i < (1 << lShift); i++)
				{
//					MT100.logger.info("i: " + i + ", ^i: "+ ((i >> 4) + (i & 0xF) * (length >> 4)));
					for(int j=0; j < (length >> lShift); j++)
					{
//						MT100.logger.info("j: " + j + " " + (j * roundWidth) + " " + (i * charsize + j * roundWidth) + " " + ((i >> 4) * width + (i & 0xF) * 16 * width * height + j * 16 * width));
						for(int k=0; k < height; k++)
						{
//							MT100.logger.info("k: " + k);
							for(int l=0; l < width; l++)
							{
								bt = (byte)(((chars.get((i * (length >> lShift) + j) * charsize + k * roundWidth + (l >> 3)) & (1 << (7 - (l & 7)))) != 0) ? 0xFF : 0x00);
								textureBuffer.put(i * width + (j * height + k) * textureWidth + l, bt);
							}
						}
					}
				}

				texture = BufferUtils.createIntBuffer(1);
				GL11.glGenTextures(texture);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.get(0));
				GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0x0, GL11.GL_ALPHA, textureWidth, textureHeight, 0x0, GL11.GL_ALPHA, GL11.GL_UNSIGNED_BYTE, textureBuffer);

				int ss = (psf1 ? 0xFFFE : 0xFE);
				int term = (psf1 ? 0xFFFF : 0xFF);
				if(hasUni)
				{
					int c = 0;
					int sep;
					int i = 0;
					boolean leading = true;
					do
					{
						c = 0;
						sep = file.readByte();
						if(sep < 0) sep += 0x100;
						if(psf1)
						{
							int sep2 = file.readByte();
							if(sep2 < 0) sep2 += 0x100;
							sep |= (sep2 << 8);
						}
						if(sep < 0) sep += (psf1 ? 0x10000 : 0x100);
						if(sep == ss)
						{
							leading = false;
						}
						else if(sep == term)
						{
							leading = true;
							i++;
							c = 0;
						}
						else // real char
						{
							if(!psf1) // parse utf8; TODO: maybe more compact?
							{
								int shift;
								if(sep >= 0xFE)
								{
									throw new RuntimeException("Illegal UTF8 start byte");
								}
								if(sep >= 0xFC)
								{
									shift = 5;
									c |= ((sep & 0x01) << 30);
								}
								else if(sep >= 0xF8)
								{
									shift = 4;
									c |= ((sep & 0x03) << 24);
								}
								else if(sep >= 0xF0)
								{
									shift = 3;
									c |= ((sep & 0x07) << 18);
								}
								else if(sep >= 0xE0)
								{
									shift = 2;
									c |= ((sep & 0x0F) << 12);
								}
								else if(sep >= 0xC0)
								{
									shift = 1;
									c |= ((sep & 0x1F) << 6);
								}
								else if(sep >= 0x80)
								{
									throw new RuntimeException("Illegal UTF8 start byte: " + sep);
								}
								else
								{
									shift = 0;
									c = sep;
								}
								MT100.logger.info("c: " + c + ", sep: " + sep + ", shift: " + shift);
								while(shift-- > 0)
								{
									sep = file.readByte();
									if(sep < 0)  sep += 0x100;
									if(sep >= 0xC0 || sep < 0x80)
									{
										throw new RuntimeException("Illegal UTF8 intermediate byte" + sep);
									}
									c |= (sep & 0x3F) << (shift * 6);
								}
							}
							else
							{
								c = sep;
							}
							if(leading)
							{
								PstFontRegistry.fontMap.put(c, this);
								PstFontRegistry.indexMap.put(c, i);
								MT100.logger.info("c: " + c + ", sep: " + sep + ", i: " + i);
							}
						}
					}
					while(i < length);
				}
				else
				{
					for(int i=0; i < length; i++)
					{
						PstFontRegistry.fontMap.put(i, this);
						PstFontRegistry.indexMap.put(i, i);
					}
				}
//				System.out.println(file);
			}
			else
			{
				MT100.logger.info("No font file!");
			}
		}
		catch(java.io.IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	public void bindFontTexture()
	{
		GL11.glLoadIdentity();
		GL11.glScalef(1F/textureWidth, 1F/textureHeight, 1F);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.get(0));
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
	}
}
