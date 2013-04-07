/*

Copyright © 2012, 2013 RainWarrior

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
import rainwarrior.mt100.Sym.C0;
import rainwarrior.mt100.Sym.C1;
import rainwarrior.mt100.Sym.CS;

public class ScreenConsumer implements IParserConsumer
{
	Screen screen;

	public ScreenConsumer(Screen screen)
	{
		this.screen = screen;
	}

	@Override
	public void G0(int c)
	{
		screen.writeWithShift(c);
	}

	@Override
	public void C0(int c)
	{
		MT100.logger.info("parse, C0: " + c);
		switch(c)
		{
			case C0.BS:
				screen.moveLeftWithShift();
				break;
			case C0.LF:
				screen.moveDownWithShift();
				break;
			case C0.CR:
				screen.setX(0);
				break;
		}
	}

	@Override
	public void C1(int c)
	{
		MT100.logger.info("parse, C1: " + c);
		switch(c)
		{
			case C1.NEL:
				screen.moveDownWithShift();
				screen.setX(0);
				break;
			case C1.RI:
				screen.moveUpWithShift();
				break;
		}
	}

	@Override
	public void Fs(int c)
	{
	}

	public int get(Integer[] p, int i, int d)
	{
		if(p.length <= i || p[i] == null) return d;
		return p[i];
	}

	@Override
	public void NormalCS(int c, Integer[] p)
	{
		MT100.logger.info("NormalCS: c: " + c +  ", p: " + p);
		int i;
		switch(c)
		{
			case CS.CUU: // CURSOR UP
				i = get(p, 0, 1);
				while(i-- > 0) screen.moveUpWithShift();
				break;
			case CS.CUD: // CURSOR DOWN
				i = get(p, 0, 1);
				while(i-- > 0) screen.moveDownWithShift();
				break;
			case CS.CUF: // CURSOR RIGHT
				i = get(p, 0, 1);
				while(i-- > 0) screen.moveRightWithShift();
				break;
			case CS.CUB: // CURSOR LEFT
				i = get(p, 0, 1);
				while(i-- > 0) screen.moveLeftWithShift();
				break;
			case CS.ED:  // ERASE IN PAGE
				screen.clearScreen(get(p, 0, 0));
				break;
			case CS.EL:  // ERASE IN LINE
				screen.clearLine(get(p, 0, 0));
				break;
			case CS.SU:  // SCROLL UP
				screen.scrollUp(get(p, 0, 1));
				break;
			case CS.SD:  // SCROLL DOWN
				screen.scrollDown(get(p, 0, 1));
				break;
			case CS.CUP: // CURSOR POSITION
			case CS.HVP: // CHARACTER AND LINE POSITION
				screen.setX(get(p, 0, 1) - 1);
				screen.setY(get(p, 1 ,1) - 1);
				break;
			case CS.SGR: // SELECT GRAPHIC RENDITION
				for(Integer j : p)
				{
					if(j != null) SGR(j);
				}
				break;
		}
	}

	@Override
	public void RawCS(int c, String p)
	{
		MT100.logger.info("RawCS: c: " + c + ", p: " + p);
		switch(c)
		{
			case CS.SGR: // SELECT GRAPHIC RENDITION
				String[] ps = Parser.splitCSIParams(p);
				boolean done;
				for(int i=0; i < ps.length; i++)
				{
					try
					{
						SGR(Integer.parseInt(ps[i]));
					}
					catch(NumberFormatException e)
					{
						MT100.logger.warning("Ignoring SGR Parameter: " + ps[i]);
						SGR(ps[i]);
					}
				}
				break;
		}
	}

	public void SGR(int p)
	{
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

	public void SGR(String p)
	{
	}
}
