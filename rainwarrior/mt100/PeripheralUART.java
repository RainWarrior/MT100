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

public class PeripheralUART implements IHostedPeripheral, ISender, IReceiver, ITicker
{
	@Delegate(types=ISender.class)
	QueueBuffer input = new QueueBuffer(); // cc -> UART
	@Delegate(types=IReceiver.class)
	QueueBuffer output = new QueueBuffer(); // UART -> CC

	volatile IComputerAccess computer = null;
	static final AtomicReferenceFieldUpdater<PeripheralUART, IComputerAccess> updater = AtomicReferenceFieldUpdater.newUpdater(PeripheralUART.class, IComputerAccess.class, "computer");

	Screen screen;

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

	public PeripheralUART(Screen screen)
	{
		MT100.logger.info("Per created!");
		this.screen = screen;
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
    
	/**
	 * This is called when a lua program on an attached computer calls peripheral.call() with
	 * one of the methods exposed by getMethodNames().<br>
	 * <br>
	 * Be aware that this will be called from the ComputerCraft Lua thread, and must be thread-safe
	 * when interacting with minecraft objects.
	 * @param 	computer	The interface to the computer that is making the call. Remember that multiple
	 *						computers can be attached to a peripheral at once.
	 * @param	method		An integer identifying which of the methods from getMethodNames() the computer
	 *						wishes to call. The integer indicates the index into the getMethodNames() table
	 *						that corresponds to the string passed into peripheral.call()
	 * @param	arguments	An array of objects, representing the arguments passed into peripheral.call().<br>
	 *						Lua values of type "string" will be represented by Object type String.<br>
	 *						Lua values of type "number" will be represented by Object type Double.<br>
	 *						Lua values of type "boolean" will be represented by Object type Boolean.<br>
	 *						Lua values of any other type will be represented by a null object.<br>
	 *						This array will be empty if no arguments are passed.
	 * @return 	An array of objects, representing values you wish to return to the lua program.<br>
	 *			Integers, Doubles, Floats, Strings, Booleans and null be converted to their corresponding lua type.<br>
	 *			All other types will be converted to nil.<br>
	 *			You may return null to indicate no values should be returned.
	 * @throws	Exception	If you throw any exception from this function, a lua error will be raised with the
	 *						same message as your exception. Use this to throw appropriate errors if the wrong
	 *						arguments are supplied to your method.
	 * @see 	#getMethodNames
	 */
	@Override
	public Object[] callMethod(IComputerAccess computer, int method, Object[] args) throws Exception
	{
		if(computer != updater.get(this)) return null;
		if(methodNames[method] == "write")
		{
			MT100.logger.info("Per.write: " + args[0]);
			String str = (String)args[0];
			ArrayList<Byte> data = new ArrayList<Byte>(str.length());
			for(int i=0; i < str.length(); i++)
			{
				if((int)str.charAt(i) > 0x100) MT100.logger.info("Per.write, long character: " + str.charAt(i));
				data.add((byte) str.charAt(i));
			}
			input.receive(data.iterator());
		}
		else if(methodNames[method] == "clear")
		{
			// TODO send
			MT100.logger.info("Per.clear");
		}
		else if(methodNames[method] == "clearLine")
		{
			// TODO send
			MT100.logger.info("Per.clearLine");
		}
		else if(methodNames[method] == "getCursorPos")
		{
			MT100.logger.info("Per.getCursorPos");
			return new Object[]{ screen.x, screen.y };
		}
		else if(methodNames[method] == "setCursorPos")
		{
//			screen.x = (Integer)args[0];
//			screen.y = (Integer)args[1];
			// TODO send, should go to both screens
			MT100.logger.info("Per.setCursorPos");
		}
		else if(methodNames[method] == "setCursorBlink")
		{
			// TODO send
			MT100.logger.info("Per.setCursorBlink");
		}
		else if(methodNames[method] == "isColor" || methodNames[method] == "isColour")
		{
			return new Object[]{ true };
		}
		else if(methodNames[method] == "getSize")
		{
			return new Object[]{ screen.width, screen.height };
		}
		else if(methodNames[method] == "scroll")
		{
			// TODO send
			MT100.logger.info("Per.scroll");
		}
		else if(methodNames[method] == "setTextColor" || methodNames[method] == "setTextColour")
		{
			// TODO send
		}
		else if(methodNames[method] == "setBackgroundColor" || methodNames[method] == "setBackgroundColour")
		{
			// TODO send
		}
		else
		{
			MT100.logger.warning("Per, unimplemented method: " + methodNames[method]);
		}
		return null;
	}
    
	@Override
	public synchronized boolean canAttachToSide(int side)
	{
		boolean ret = updater.get(this) == null;
		MT100.logger.info("CanAttach: " + ret);
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
		input.update();
		if(computer != null && !output.buffer.isEmpty())
		{
			try
			{
				int quota = Reference.CC_EVENT_QUOTA, ret = 0;
				Object[] t;
				byte b;
				Integer k;
				Character c;
				while(!output.buffer.isEmpty() && quota != 0)
				{ // directly passing UTF-8 to Lua
					b = output.buffer.take();
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

