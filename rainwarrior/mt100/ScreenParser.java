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

import java.lang.StringBuffer;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.nbt.NBTTagCompound;
import rainwarrior.mt100.Sym.C0;
import rainwarrior.mt100.Sym.C1;
import rainwarrior.mt100.Sym.CS;

public class ScreenParser implements IReceiver, ITicker
{
	public enum State
	{
		GROUND,
		ESCAPE,
		ESCAPE_INTER,
		CSI_PARAM,
		CSI_INTER,
		CSI_FINAL,
		CSI_IGNORE,
		DCS_ENTRY,
		DCS_PARAM,
		DCS_INTER,
		DCS_PASSTHROUGH,
		DCS_IGNORE,
		OSC_STRING
	}

	QueueBuffer buffer = new QueueBuffer(false);
	Screen screen;
	int c = 0;
	int shift = 0;
	State curState;
	long csiInters = 0;
	int csiINum = 0;
	StringBuffer csiParams = new StringBuffer();

	@Override
	public int capacity()
	{
		return buffer.capacity();
	}

	@Override
	public int receive(Iterator<Byte> data)
	{
		int ret = buffer.receive(data);
//		MT100.logger.info("4 " + FMLCommonHandler.instance().getEffectiveSide() + ret + " " + buffer.buffer.size());
		return ret;
	}

	public ScreenParser(Screen screen)
	{
		this.screen = screen;
		curState = State.GROUND;
	}

	public void readFromNBT(NBTTagCompound cmp)
	{
		// TODO
	}

	public void writeToNBT(NBTTagCompound cmp)
	{
		// TODO
	}

	@Override
	public void update()
	{
		int quota = Reference.SCREEN_UPDATE_QUOTA;
//		MT100.logger.info("Supdate: '" + buffer.buffer.size() + "', side: " + FMLCommonHandler.instance().getEffectiveSide());
		while(!buffer.buffer.isEmpty() && quota != 0)
		{
			Byte b = buffer.buffer.poll();
			parse(b);
			if(quota > 0) quota--;
		}
	}

	/*
	 * UTF-8 to UCS
	 */
	public void parse(byte b)
	{
		int sep = b;
		if(sep < 0)  sep += 0x100;
		if(shift > 0)
		{
			shift--;
			if(sep >= 0xC0 || sep < 0x80)
			{
				throw new RuntimeException("Illegal UTF8 intermediate byte" + sep);
			}
			c |= (sep & 0x3F) << (shift * 6);
		}
		else if(sep >= 0xFE) // 1st octet
		{
			throw new RuntimeException("Illegal UTF8 start byte");
		}
		else if(sep >= 0xFC)
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
		if(shift == 0) // fully read character
		{
			parseChar();
			c = 0;
		}
//		MT100.logger.info("c: " + c + ", sep: " + sep + ", shift: " + shift);
	}

	public void parseChar()
	{ // here we go...
		boolean repeat;
		do
		{
			repeat = false;
			if(c < 0x20) // C0
			{
				// TODO
				MT100.logger.info("parseChar, C0: " + c);
				switch(c)
				{
					case C0.BS:
						screen.moveLeftWithShift();
						break;
					case C0.LF:
						screen.moveDownWithShift();
					case C0.CR:
						screen.x = 0;
					case C0.ESC:
						curState = State.ESCAPE;
						break;
				}
			}
			else if(c >= 0x80 && c <= 0xA0) // C1
			{
				// TODO
				MT100.logger.info("parseChar, C1: " + c);
				switch(c)
				{
					case C1.NEL:
						screen.moveDownWithShift();
						screen.x = 0;
					case C1.RI:
						screen.moveUpWithShift();
					case C1.CSI:
						curState = State.CSI_PARAM;
						csiInters = 0;
						csiINum = 0;
						csiParams.delete(0, csiParams.length());
						break;
				}
			}
	//		MT100.logger.info("parse: '" + b + "', side: " + FMLCommonHandler.instance().getEffectiveSide());
			else
			{
				switch(curState)
				{
					case ESCAPE:
						MT100.logger.info("parseChar, ESC2: " + c);
						if(c < 0x20 || c >= 0x7F) // illegal char
						{
							MT100.logger.warning("parseChar, illegal char in escape sequence, ignoring: " + c);
							curState = State.GROUND;
						}
						else if(c < 0x30) // nF
						{
							// TODO
							curState = State.GROUND;
						}
						else if(c < 0x40) // Fp, Private CF
						{
							// TODO
							curState = State.GROUND;
						}
						else if(c < 0x60) // ESC Fe, C1
						{
							c += 0x40;
							curState = State.GROUND;
							repeat = true;
						}
						else             // Fs, Single CF
						{
							// TODO
							curState = State.GROUND;
						}
						break;
					case GROUND:
						if(c >= 0x20 && c < 0x7F || c >= 0xA0)
						{
							screen.writeWithShift(c);
						}
						else if(c == 7F) // DELETE
						{
						}
						break;
					case CSI_PARAM:
						if(c >= 0x30 && c < 0x40)
						{
							csiParams.append((char)c);
						}
						else
						{
							curState = State.CSI_INTER;
							repeat = true;
						}
						break;
					case CSI_INTER:
						if(c >= 0x20 && c < 0x30)
						{
							csiInters <<= 4;
							csiInters |= (c & 0xF);
							csiINum++;
						}
						else
						{
							curState = State.CSI_FINAL;
							repeat = true;
						}
						break;
					case CSI_FINAL:
						if(c >= 0x40 && c < 0x7F)
						{
							// TODO dispatch CSI
							MT100.logger.info("CSI: P: " + csiParams + ", I: " + csiInters + ", F: " + c);
							switch(c) // TODO move by n, not by 1
							{
								case CS.CUU: // CURSOR UP
									screen.moveUpWithShift();
									break;
								case CS.CUD: // CURSOR DOWN
									screen.moveDownWithShift();
									break;
								case CS.CUF: // CURSOR RIGHT
									screen.moveRightWithShift();
									break;
								case CS.CUB: // CURSOR LEFT
									screen.moveLeftWithShift();
									break;
								case CS.ED:  // ERASE IN PAGE
									screen.clearScreen(parseCSIParams(new int[]{0})[0]);
									break;
								case CS.EL:  // ERASE IN LINE
									screen.clearLine(parseCSIParams(new int[]{0})[0]);
									break;
								case CS.SU:  // SCROLL UP
									screen.scrollUp(parseCSIParams(new int[]{1})[0]);
									break;
								case CS.SD:  // SCROLL DOWN
									screen.scrollDown(parseCSIParams(new int[]{1})[0]);
									break;
								case CS.CUP: // CURSOR POSITION
								case CS.HVP: // CHARACTER AND LINE POSITION
									int[] coords = parseCSIParams(new int[]{ 1, 1 });
									// FIXME move to screen
									screen.x = coords[0] - 1;
									while(screen.x < 0) screen.x += screen.width;
									while(screen.x >= screen.width) screen.x -= screen.width;
									screen.y = coords[1] - 1 + screen.scroll;
									while(screen.y < 0) screen.y += screen.height;
									while(screen.y >= screen.height) screen.y -= screen.height;
									break;
								case CS.SGR: // SELECT GRAPHIC RENDITION
									String[] ps = splitCSIParams();
									for(int i=0; i < ps.length; i++)
									{
										try
										{
											int p = Integer.parseInt(ps[i]);
											MT100.logger.warning("p: " + p);
											if(p >= 30 && p < 38)
											{
												screen.setAnsiFColor(p - 30);
												MT100.logger.warning("AnsiF: " + (p - 30));
											}
											if(p >= 40 && p < 48)
											{
												screen.setAnsiBColor(p - 40);
												MT100.logger.warning("AnsiF: " + (p - 40));
											}
											switch(p)
											{
												case 0: // default
													screen.setDefBColor();
													screen.setDefFColor();
													screen.curBBold = false;
													screen.curFBold = false;
													break;
												case 1:
													screen.curFBold = true;
													break;
												case 2:
													screen.curFBold = false; // TODO faint maybe?
													break;
												case 22:
													screen.setDefBColor();
													screen.setDefFColor();
													screen.curFBold = false;
													screen.curBBold = false;
													break;
												case 38: // T.416 TODO
													break;
												case 39:
													screen.setDefFColor();
													break;
												case 49:
													screen.setDefBColor();
													break;
											}
										}
										catch(NumberFormatException e)
										{
											MT100.logger.warning("Ignoring SGR Parameter: " + ps[i]);
										}
									}
									break;
							}
							curState = State.GROUND;
						}
						else
						{
							MT100.logger.warning("parseChar, illegal char in CSI sequence, ignoring: " + c);
							curState = State.GROUND;
						}
						break;
				}
			}
		}
		while(repeat);
	}
	String[] splitCSIParams()
	{
		return csiParams.toString().split(String.valueOf((char)0x3B)); // ;
	}

	int[] parseCSIParams(int[] defs)
	{
		String[] ps = splitCSIParams();
		int[] ret = new int[defs.length];
		try
		{
			for(int i=0; i < ps.length; i++)
			{
				ret[i] = ((ps[i] == "") ? defs[i] : Integer.parseInt(ps[i]));
			}
			for(int i = ps.length; i < defs.length; i++)
			{
				ret[i] = defs[i];
			}
			return ret;
		}
		catch(NumberFormatException e)
		{
			MT100.logger.warning("Error parsing CSI Parameters");
			return null;
		}
	}
}

