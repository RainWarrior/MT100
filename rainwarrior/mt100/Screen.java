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

import java.lang.StringBuffer;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.nbt.NBTTagCompound;

public class Screen implements IReceiver, ITicker
{
	public enum State
	{
		GROUND,
		ESCAPE,
		ESCAPE_INTERMEDIATE,
		CSI_ENTRY,
		CSI_PARAM,
		CSI_INTERMEDIATE,
		CSI_IGNORE,
		DCS_ENTRY,
		DCS_PARAM,
		DCS_INTERMEDIATE,
		DCS_PASSTHROUGH,
		DCS_IGNORE,
		OSC_STRING
	}

	QueueBuffer buffer = new QueueBuffer(false);
	public int[] screen;
	int x; int y;
	public int width;
	public int height;
	boolean wrapx = true;
	boolean wrapy = true;
	Byte cur;
	State curState;

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

	public Screen(int width, int height)
	{
		screen = new int[width * height];
		for(int i=0; i < width * height; i++)
		{
			screen[i] = (i & 0xFF);
		}
		this.width = width;
		this.height = height;
		curState = State.GROUND;
	}

	public void readFromNBT(NBTTagCompound cmp)
	{
		
	}

	public void writeToNBT(NBTTagCompound cmp)
	{
		
	}

	@Override
	public void update()
	{
		int quota = Reference.SCREEN_UPDATE_QUOTA;
//		MT100.logger.info("Supdate: '" + buffer.buffer.size() + "', side: " + FMLCommonHandler.instance().getEffectiveSide());
		while(!buffer.buffer.isEmpty() && quota != 0)
		{
			Byte c = buffer.buffer.poll();
			parse(c);
			if(quota > 0) quota--;
		}
	}

	public void parse(byte b)
	{ // here we go...
/*		switch(b)
		{
			case Sym.ESC:
				curState = State.ESCAPE;
				return;
			case 
		}*/
//		MT100.logger.info("parse: '" + b + "', side: " + FMLCommonHandler.instance().getEffectiveSide());
		switch(curState)
		{
			case GROUND:
				if(b >= 0x20 && b < 0x7F)
				{
					//char c = (char)(b);
					if(x >= 0 && x < width && y >= 0 && y < width)
					{
						screen[x + y * width] = b;
					}
					x++;
					if(x >= width && wrapx)
					{
						x = 0;
						y++;
					}
					if(y >= height && wrapy)
					{
						y = 0;
					}
				}
			break;
		}
	}
}

