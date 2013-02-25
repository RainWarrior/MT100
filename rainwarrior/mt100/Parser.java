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
import lombok.Delegate;
import rainwarrior.mt100.Sym.C0;
import rainwarrior.mt100.Sym.C1;
import rainwarrior.mt100.Sym.CS;

public class Parser implements IReceiver, ITicker
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

	@Delegate(types=IReceiver.class)
	QueueBuffer buffer = new QueueBuffer(false);
	IParserConsumer consumer;
	int c = 0;
	int shift = 0;
	State curState;
	long csiInters = 0;
	int csiINum = 0;
	StringBuffer csiParams = new StringBuffer();

	public Parser(IParserConsumer consumer)
	{
		this.consumer = consumer;
		curState = State.GROUND;
	}

	@Override
	public void update()
	{
		int quota = Reference.PARSER_UPDATE_QUOTA;
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
				if(c == C0.ESC)
				{
					curState = State.ESCAPE;
				}
				consumer.C0(c);
			}
			else if(c >= 0x80 && c <= 0xA0) // C1
			{
				if(c == C1.CSI)
				{
					curState = State.CSI_PARAM;
					csiInters = 0;
					csiINum = 0;
					csiParams.delete(0, csiParams.length());
				}
				consumer.C1(c);
			}
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
							consumer.Fs(c);
							curState = State.GROUND;
						}
						break;
					case GROUND:
						if(c >= 0x20 && c < 0x7F || c >= 0xA0)
						{
							consumer.G0(c);
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
							String[] ps = splitCSIParams(csiParams.toString());
							Integer[] params = new Integer[ps.length];
							try
							{
								for(int i=0; i < ps.length; i++)
								{
									params[i] = ((ps[i] == "") ? null : Integer.parseInt(ps[i]));
								}
								consumer.NormalCS(c, params);
							}
							catch(NumberFormatException e)
							{
								consumer.RawCS(c, csiParams.toString());
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
	public static String[] splitCSIParams(String p)
	{
		return ((p.length() == 0) ? new String[0] : p.split(String.valueOf((char)0x3B))); // ;
	}
}

