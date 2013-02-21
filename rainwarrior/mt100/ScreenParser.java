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
import rainwarrior.mt100.Sym.C0;
import rainwarrior.mt100.Sym.C1;

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
	boolean wrapx = true, wrapy = true;
	boolean doScroll = true;
	long csiInters = 0;
	long csiParams = 0;

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
					case C1.CSI:
						curState = State.CSI_PARAM;
						csiInters = 0;
						csiParams = 0;
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
							screen.writeWithShift(c, wrapx, wrapy, doScroll);
						}
						else if(c == 7F) // DELETE
						{
						}
						break;
					case CSI_PARAM:
						if(c >= 0x30 && c < 0x40)
						{
							csiParams <<= 4;
							csiParams |= (c & 0xF);
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
}

