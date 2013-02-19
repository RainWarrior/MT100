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

package rainwarrior.mt100.client;

import java.util.ArrayList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import rainwarrior.mt100.*;

public class GuiMT100 extends GuiScreen implements ISender, ITicker
{
	QueueBuffer buffer = new QueueBuffer();
	TileEntityMT100 te;
	Screen screen;
	long ot = 0;
	long pt = 0;
	long wt = Reference.TYPEMATIC_DELAY;
	boolean isRepeating = false;
	boolean shift = false;
	boolean ctrl = false;
	boolean alt = false;
	int key;
	int asc;

	public GuiMT100(TileEntityMT100 te, Screen screen)
	{
		this.te = te;
		this.screen = screen;
		this.ot = System.nanoTime() / 1000000L;
		this.allowUserInput = false;
	}

	@Override
	public void initGui()
	{
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void onGuiClosed()
	{
		te.curGui = null;
		buffer.disconnectAll();
	}

	@Override
	public void drawScreen(int mousex, int mousey, float parTick)
	{
		
		//int tex = mc.renderEngine.getTexture(type.guiTexture);
		updateTimer();
		super.drawScreen(mousex, mousey, parTick);
		/*
//		drawRect(width/2 - 100, height/2 - 100, width/2 + 100, height/2 + 100, 0xFFFF00FF);
//		GL11.glBindTexture(GL11.GL_TEXTURE_2D, te.pstFontRenderer.texture.get(0));
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.entityRenderer.lightmapTexture);
//		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(1.F, 1.F, 1.F, 1.F);
//		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
//		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
		GL11.glTexCoord2f(0F, 0F);
		//OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0.F, 0.F);
		GL11.glVertex3f(0, 0, 0.F);
		GL11.glTexCoord2f(0F, 1F);
		//OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0.F, 1.F);
		GL11.glVertex3f(0, 100, 0.F);
		GL11.glTexCoord2f(1F, 0F);
		//OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 1.F, 0.F);
		GL11.glVertex3f(100, 0, 0.F);
		GL11.glTexCoord2f(1F, 1F);
		//OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 1.F, 1.F);
		GL11.glVertex3f(100, 100, 0.F);
		GL11.glEnd();
		*/
//		GL11.glEnable(GL11.GL_TEXTURE_2D);
//		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
/*		GL11.glBindTexture(GL11.GL_TEXTURE_2D, te.pstFontRenderer.texture.get(0));
		GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
		GL11.glTexCoord2f(0F, 0F);
		GL11.glVertex3f(0, 0, 0.F);
		GL11.glTexCoord2f(0F, 1F);
		GL11.glVertex3f(0, 100, 0.F);
		GL11.glTexCoord2f(1F, 0F);
		GL11.glVertex3f(100, 0, 0.F);
		GL11.glTexCoord2f(1F, 1F);
		GL11.glVertex3f(100, 100, 0.F);
		GL11.glEnd();*/
		GL11.glPushMatrix();
		//GL11.glScalef(width / 2F, height / 2F, 1F);
		//GL11.glScalef(2F/width, 2F/height, 1F);

		float cw = 320;
		float ch = 240;
		GL11.glTranslatef((width - cw)/ 2F, (height - ch)/ 2F, 0F);
		screen.render(cw, ch);
		GL11.glPopMatrix();
//		this.fontRenderer.drawStringWithShadow(new String(screen.screen), 50, 50, 0xFFFF0000);
	}

	@Override
	public void updateScreen()
	{
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	public void updateTimer()
	{
		long nt = System.nanoTime() / 1000000L;

		pt +=  nt - ot;

		if(pt > wt)
		{
			if(key != 0) sendKey(false);
		}

		ot = nt;
	}

	@Override
	public void handleKeyboardInput()
	{
		{
			boolean state = Keyboard.getEventKeyState();
			shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
			ctrl = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
			alt = Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU);
			key = Keyboard.getEventKey();
			char ch = Keyboard.getEventCharacter();
//			MT100.logger.info("keyEvent: '" + key + "', '" + ch + "', state: " + state);
			if(state)
			{
				switch(key)
				{
					case(Keyboard.KEY_HOME):
						this.mc.displayGuiScreen((GuiScreen)null);
						this.mc.setIngameFocus();
					break;
					case(Keyboard.KEY_F11):
						this.mc.toggleFullscreen();
					break;
				}
				asc = Sym.LWJGLToASCII(key, shift, ctrl);
//				if(asc >= 0x20 && asc < 0x7F)
//				{
					sendKey(true);
//				}
			}
			else
			{
				key = 0;
			}
			updateTimer();
		}
	}

	public void sendKey(boolean now)
	{
		if(now)
		{
			wt = Reference.TYPEMATIC_DELAY;
		}
		else
		{
			wt = Reference.TYPEMATIC_SPEED;
		}
		pt = 0;

		// generate symbol based on keys. TODO
		ArrayList data;
		switch(key)
		{
			case Keyboard.KEY_UP:
				data = new ArrayList<Byte>(3);
				data.add((byte)0x1B); // ESC
				data.add((byte)0x5B); // [
				data.add((byte)0x41); // A
			case Keyboard.KEY_DOWN:
				data = new ArrayList<Byte>(3);
				data.add((byte)0x1B); // ESC
				data.add((byte)0x5B); // [
				data.add((byte)0x42); // B
			case Keyboard.KEY_RIGHT:
				data = new ArrayList<Byte>(3);
				data.add((byte)0x1B); // ESC
				data.add((byte)0x5B); // [
				data.add((byte)0x43); // C
			case Keyboard.KEY_LEFT:
				data = new ArrayList<Byte>(3);
				data.add((byte)0x1B); // ESC
				data.add((byte)0x5B); // [
				data.add((byte)0x44); // D
			default:
				data = new ArrayList<Byte>(1);
				data.add((byte)asc);
		}
		buffer.receive(data.iterator());
	}

	@Override
	public void update()
	{
		buffer.update();
	}

	@Override
	public boolean connected(IReceiver rec)
	{
		return buffer.connected(rec);
	}

	@Override
	public boolean connect(IReceiver rec)
	{
		return buffer.connect(rec);
	}

	@Override
	public boolean disconnect(IReceiver rec)
	{
		return buffer.disconnect(rec);
	}
}
