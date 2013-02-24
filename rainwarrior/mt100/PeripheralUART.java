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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import lombok.Delegate;

import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.FMLCommonHandler;

import dan200.computer.api.IHostedPeripheral;
import dan200.computer.api.IComputerAccess;

import rainwarrior.mt100.Sym.C0;
import rainwarrior.mt100.Sym.C1;
import rainwarrior.mt100.Sym.CS;

public class PeripheralUART implements IHostedPeripheral, ISender, IReceiver, ITicker
{
	@Delegate(types=ISender.class)
	QueueBuffer output = new QueueBuffer(); // CC -> UART
	@Delegate(types=IReceiver.class)
	QueueBuffer input = new QueueBuffer(); // UART -> CC

	volatile IComputerAccess computer = null;
	static final AtomicReferenceFieldUpdater<PeripheralUART, IComputerAccess> updater = AtomicReferenceFieldUpdater.newUpdater(PeripheralUART.class, IComputerAccess.class, "computer");

	Object callLock = new Object();

	TileEntityMT100 te;

	static final String[] methodNames = new String[]{
		"write",
		"clear",
		"clearLine",
		"getCursorPos",
		"setCursorPos",
		"setCursorBlink",
		"isColor",
		"isColour",
		"getSize",
		"scroll",
//		"redirect", TODO: do i need this?
//		"restore",
		"setTextColor",
		"setTextColour",
		"setBackgroundColor",
		"setBackgroundColour",
	};

	public PeripheralUART(TileEntityMT100 te)
	{
		MT100.logger.info("Per created!");
		this.te = te;
//		te.connect(this.input);
//		this.output.connect(te);
	}

	// IHostedPeripheral
	@Override
	public void readFromNBT(NBTTagCompound cmp)
	{
		// TODO
		boolean isServer = FMLCommonHandler.instance().getEffectiveSide().isServer();
		MT100.logger.info("UART readFromNBT, isServer: " + isServer);
	}

	@Override
	public void writeToNBT(NBTTagCompound cmp)
	{
		// TODO
		boolean isServer = FMLCommonHandler.instance().getEffectiveSide().isServer();
		MT100.logger.info("UART writeToNBT, isServer: " + isServer);
	}

	// IPeripheral
	@Override
	public String getType()
	{
		return "monitor";
	}
    
	@Override
	public String[] getMethodNames()
	{
		return methodNames;
	}
    
	@Override
	public synchronized Object[] callMethod(IComputerAccess computer, int method, Object[] args) throws Exception
	{
		if(computer != updater.get(this)) return null;
		String str = null;
		if(methodNames[method] == "write")
		{
			MT100.logger.info("Per.write: " + args[0]);
			str = (String)args[0];
			for(int i=0; i < str.length(); i++)
			{
				if((int)str.charAt(i) > 0x100) MT100.logger.info("Per.write, long character: " + str.charAt(i));
			}
		}
		else if(methodNames[method] == "clear")
		{
			str = "" + 2 + CS.ED;
			MT100.logger.info("Per.clear");
		}
		else if(methodNames[method] == "clearLine")
		{
			str = "" + 2 + CS.EL;
			MT100.logger.info("Per.clearLine");
		}
		else if(methodNames[method] == "getCursorPos")
		{
			// TODO maybe switch to DSR and blocking, or store local copy
			int y = te.screen.y - te.screen.scroll;
			if(y < 0) y += te.screen.height;
			MT100.logger.info("Per.getCursorPos: " + te.screen.x + " " + y);
			return new Object[]{ te.screen.x, y};
		}
		else if(methodNames[method] == "setCursorPos")
		{
			int x = ((Double)args[0]).intValue();
			int y = ((Double)args[1]).intValue();
			str = "" + (char)C1.CSI + (x + 1) + ";" + (y + 1) + (char)CS.HVP;
			MT100.logger.info("Per.setCursorPos: " + x + " " + y);
		}
		else if(methodNames[method] == "setCursorBlink")
		{
			// TODO do i need this?
			MT100.logger.info("Per.setCursorBlink");
		}
		else if(methodNames[method] == "isColor" || methodNames[method] == "isColour")
		{
			return new Object[]{ true };
		}
		else if(methodNames[method] == "getSize")
		{
			return new Object[]{ te.screen.width, te.screen.height };
		}
		else if(methodNames[method] == "scroll")
		{
			int p = ((Double)args[0]).intValue();
			if(p > 0) str = "" + (char)C1.CSI +   p  + (char)CS.SD;
			if(p < 0) str = "" + (char)C1.CSI + (-p) + (char)CS.SU;
			MT100.logger.info("Per.scroll");
		}
		else if(methodNames[method] == "setTextColor" || methodNames[method] == "setTextColour"
		|| methodNames[method] == "setBackgroundColor" || methodNames[method] == "setBackgroundColour")
		{
			int c = ((Double)args[0]).intValue();
			boolean b = false;
			if(c >= 0x100)
			{
				b = true;
				c >>= 8;
			}
			int rc = 1;
			while((c >>= 1) != 0) rc <<= 1;
			boolean f = (methodNames[method].charAt(3) == 'T');
			if(f)
			{
				str = "" + (char)C1.CSI + (rc + 30) + ";" + (b ? 1 : 22) + (char)CS.SGR;
			}
			else
			{
				str = "" + (char)C1.CSI + (rc + 40) + (char)CS.SGR;
			}
		}
		else if(methodNames[method] == "setBackgroundColor" || methodNames[method] == "setBackgroundColour")
		{
			int c = ((Double)args[0]).intValue();
		}
		else
		{
			MT100.logger.warning("Per, unimplemented method: " + methodNames[method]);
		}
		if(str != null)
		{
			synchronized(te.updateLock)
			{
				byte[] b = str.getBytes("UTF-8");
				ArrayList<Byte> data = new ArrayList<Byte>(b.length);
				for(int i=0; i < b.length; i++)
				{
					data.add(b[i]);
				}
				this.output.receive(data.iterator());
				this.output.update();
				// hack, because otherwise state will be incorrect
				this.te.input.update();
				this.te.screenParser.update();
			}
		}
		return null;
	}
    
	@Override
	public boolean canAttachToSide(int side)
	{
		boolean ret = updater.get(this) == null;
		MT100.logger.info("CanAttach: " + ret + FMLCommonHandler.instance().getEffectiveSide());
		return ret;
	}

	@Override
	public void attach(IComputerAccess computer)
	{
		if(!updater.compareAndSet(this, null, computer))
		{
			MT100.logger.warning("Synchronization issue with CC, on attach");
		}
	}

	@Override
	public void detach(IComputerAccess computer)
	{
		if(!updater.compareAndSet(this, updater.get(this), null))
		{
			MT100.logger.warning("Synchronization issue with CC, on detach");
		}
	}

	// ITicker
	@Override
	public void update()
	{
//		MT100.logger.info("Per.update");
		output.update();
		if(computer != null && !input.buffer.isEmpty())
		{
			try
			{
				int quota = Reference.CC_EVENT_QUOTA, ret = 0;
				Object[] t;
				byte b;
				Integer k;
				Character c;
				while(!input.buffer.isEmpty() && quota != 0)
				{ // directly passing UTF-8 to Lua
					b = input.buffer.take();
					t = Sym.ASCIIToLWJGL(b);
					k = (Integer)t[0];
					c = (Character)t[1];
					MT100.logger.info("Update: k:" + k + ", c: " + c);
					if(c != 0)
					{
						computer.queueEvent("char", new Object[]{ "" + c });
					}
					if(k != 0)
					{
						computer.queueEvent("key", new Object[]{ k });
					}
				}
			}
			catch(InterruptedException e)
			{
				MT100.logger.severe("InterruptedException in Per.update: " + e);
			}
		}
	}
}

