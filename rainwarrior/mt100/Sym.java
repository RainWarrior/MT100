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

import org.lwjgl.input.Keyboard;

public enum Sym
{
	NUL(0x00),
	SOH(0x01), // (start of heading)
	STX(0x02), // (start of text)
	ETX(0x03), // (end of text)
	EOT(0x04), // (end of transmission)
	ENQ(0x05), // (enquiry)
	ACK(0x06), // (acknowledge)
	BEL(0x07), // '\a' (bell)
	BS(0x08), //  '\b' (backspace)
	HT(0x09), //  '\t' (horizontal tab)
	LF(0x0A), //  '\n' (new line)
	VT(0x0B), //  '\v' (vertical tab)
	FF(0x0C), //  '\f' (form feed)
	CR(0x0D), //  '\r' (carriage ret)
	SO(0x0E), //  (shift out)
	SI(0x0F), //  (shift in)
	DLE(0x10), // (data link escape)
	DC1(0x11), // (device control 1)
	DC2(0x12), // (device control 2)
	DC3(0x13), // (device control 3)
	DC4(0x14), // (device control 4)
	NAK(0x15), // (negative ack.)
	SYN(0x16), // (synchronous idle)
	ETB(0x17), // (end of trans. blk)
	CAN(0x18), // (cancel)
	EM(0x19), //  (end of medium)
	SUB(0x1A), // (substitute)
	ESC(0x1B), // (escape)
	FS(0x1C), //  (file separator)
	GS(0x1D), //  (group separator)
	RS(0x1E), //  (record separator)
	US(0x1F), //  (unit separator)
	SPACE(0x20),
	DEL(0x7F);
	byte val;
	Sym(int val)
	{
		this.val = (byte)val;
	}
	public int compareTo(int b)
	{
		return b - val;
	}
	public int compareTo(byte b)
	{
		return b - val;
	}
	public static int LWJGLToASCII(int key, boolean shift, boolean ctrl)
	{
		switch(key)
		{
			case Keyboard.KEY_RETURN:		return 0x0D;
			case Keyboard.KEY_BACK:			return 0x08;
			case Keyboard.KEY_TAB:			return 0x09;
			case Keyboard.KEY_SPACE:		return ctrl ? 0x00 : 0x20;
			case Keyboard.KEY_ESCAPE:		return 0x1B;

			// numeric
			case Keyboard.KEY_0:			return shift ? 0x28 : 0x30;
			case Keyboard.KEY_1:			return shift ? 0x21 : 0x31;
			case Keyboard.KEY_2:			return shift ? 0x32 : 0x32; // !
			case Keyboard.KEY_3:			return shift ? 0x23 : 0x33;
			case Keyboard.KEY_4:			return shift ? 0x24 : 0x34;
			case Keyboard.KEY_5:			return shift ? 0x25 : 0x35;
			case Keyboard.KEY_6:			return shift ? 0x36 : 0x36; // !
			case Keyboard.KEY_7:			return shift ? 0x26 : 0x37;
			case Keyboard.KEY_8:			return shift ? 0x2A : 0x38;
			case Keyboard.KEY_9:			return shift ? 0x28 : 0x39;

			case Keyboard.KEY_COMMA:		return shift ? 0x3C : 0x2C;
			case Keyboard.KEY_PERIOD:		return shift ? 0x3E : 0x2E;

			// letters
			case Keyboard.KEY_A:			return 0x01 + (ctrl ? 0x00 : (shift ? 0x40 : 0x60));
			case Keyboard.KEY_B:			return 0x02 + (ctrl ? 0x00 : (shift ? 0x40 : 0x60));
			case Keyboard.KEY_C:			return 0x03 + (ctrl ? 0x00 : (shift ? 0x40 : 0x60));
			case Keyboard.KEY_D:			return 0x04 + (ctrl ? 0x00 : (shift ? 0x40 : 0x60));
			case Keyboard.KEY_E:			return 0x05 + (ctrl ? 0x00 : (shift ? 0x40 : 0x60));
			case Keyboard.KEY_F:			return 0x06 + (ctrl ? 0x00 : (shift ? 0x40 : 0x60));
			case Keyboard.KEY_G:			return 0x07 + (ctrl ? 0x00 : (shift ? 0x40 : 0x60));
			case Keyboard.KEY_H:			return 0x08 + (ctrl ? 0x00 : (shift ? 0x40 : 0x60));
			case Keyboard.KEY_I:			return 0x09 + (ctrl ? 0x00 : (shift ? 0x40 : 0x60));
			case Keyboard.KEY_J:			return 0x0A + (ctrl ? 0x00 : (shift ? 0x40 : 0x60));
			case Keyboard.KEY_K:			return 0x0B + (ctrl ? 0x00 : (shift ? 0x40 : 0x60));
			case Keyboard.KEY_L:			return 0x0C + (ctrl ? 0x00 : (shift ? 0x40 : 0x60));
			case Keyboard.KEY_M:			return 0x0D + (ctrl ? 0x00 : (shift ? 0x40 : 0x60));
			case Keyboard.KEY_N:			return 0x0E + (ctrl ? 0x00 : (shift ? 0x40 : 0x60));
			case Keyboard.KEY_O:			return 0x0F + (ctrl ? 0x00 : (shift ? 0x40 : 0x60));
			case Keyboard.KEY_P:			return 0x10 + (ctrl ? 0x00 : (shift ? 0x40 : 0x60));
			case Keyboard.KEY_Q:			return 0x11 + (ctrl ? 0x00 : (shift ? 0x40 : 0x60));
			case Keyboard.KEY_R:			return 0x12 + (ctrl ? 0x00 : (shift ? 0x40 : 0x60));
			case Keyboard.KEY_S:			return 0x13 + (ctrl ? 0x00 : (shift ? 0x40 : 0x60));
			case Keyboard.KEY_T:			return 0x14 + (ctrl ? 0x00 : (shift ? 0x40 : 0x60));
			case Keyboard.KEY_U:			return 0x15 + (ctrl ? 0x00 : (shift ? 0x40 : 0x60));
			case Keyboard.KEY_V:			return 0x16 + (ctrl ? 0x00 : (shift ? 0x40 : 0x60));
			case Keyboard.KEY_W:			return 0x17 + (ctrl ? 0x00 : (shift ? 0x40 : 0x60));
			case Keyboard.KEY_X:			return 0x18 + (ctrl ? 0x00 : (shift ? 0x40 : 0x60));
			case Keyboard.KEY_Y:			return 0x19 + (ctrl ? 0x00 : (shift ? 0x40 : 0x60));
			case Keyboard.KEY_Z:			return 0x1A + (ctrl ? 0x00 : (shift ? 0x40 : 0x60));

			case Keyboard.KEY_LBRACKET:		return ctrl ? 0x1B : (shift ? 0x7B : 0x5B);
			case Keyboard.KEY_BACKSLASH:	return ctrl ? 0x1C : (shift ? 0x7C : 0x5C);
			case Keyboard.KEY_RBRACKET:		return ctrl ? 0x1D : (shift ? 0x7D : 0x5D);
			case Keyboard.KEY_GRAVE:		return ctrl ? 0x1E : (shift ? 0x7E : 0x60);
			case Keyboard.KEY_SLASH:		return ctrl ? 0x1F : (shift ? 0x3F : 0x2F);

			case Keyboard.KEY_APOSTROPHE:	return shift ? 0x22 : 0x27;
			case Keyboard.KEY_EQUALS:		return shift ? 0x2B : 0x3D;

			// TODO check:
			case Keyboard.KEY_COLON:		return 0x3A;
			case Keyboard.KEY_SEMICOLON:	return 0x3B;
			case Keyboard.KEY_MINUS:		return 0x2D;
			case Keyboard.KEY_UNDERLINE:	return 0x5F;
			case Keyboard.KEY_AT:			return 0x40;
			case Keyboard.KEY_CIRCUMFLEX:	return 0x5E;

			/* Arrow keys, dealt with outside:
			case Keyboard.KEY_UP:			return 0x;
			case Keyboard.KEY_DOWN:			return 0x;
			case Keyboard.KEY_RIGHT:		return 0x;
			case Keyboard.KEY_LEFT:			return 0x; */

			/* Unknown:
			case Keyboard.KEY_AX:			return 0x;
			case Keyboard.KEY_CAPITAL:		return 0x;
			case Keyboard.KEY_CONVERT:		return 0x;
			case Keyboard.KEY_DECIMAL:		return 0x;
			case Keyboard.KEY_KANA:			return 0x;
			case Keyboard.KEY_KANJI:		return 0x;
			case Keyboard.KEY_NOCONVERT:	return 0x;
			case Keyboard.KEY_NONE:			return 0x;
			case Keyboard.KEY_STOP:			return 0x;
			case Keyboard.KEY_UNLABELED:	return 0x;
			case Keyboard.KEY_YEN:			return 0x; */

			/* TODO & unused:
			case Keyboard.KEY_INSERT:		return 0x;
			case Keyboard.KEY_DELETE:		return 0x;
			case Keyboard.KEY_HOME:			return 0x;
			case Keyboard.KEY_END:			return 0x;
			case Keyboard.KEY_PRIOR:		return 0x;
			case Keyboard.KEY_NEXT:			return 0x;

			case Keyboard.KEY_SYSRQ:		return 0x;
			case Keyboard.KEY_SCROLL:		return 0x;
			case Keyboard.KEY_PAUSE:		return 0x;

			case Keyboard.KEY_NUMLOCK:		return 0x;
			case Keyboard.KEY_NUMPAD0:		return 0x;
			case Keyboard.KEY_NUMPAD1:		return 0x;
			case Keyboard.KEY_NUMPAD2:		return 0x;
			case Keyboard.KEY_NUMPAD3:		return 0x;
			case Keyboard.KEY_NUMPAD4:		return 0x;
			case Keyboard.KEY_NUMPAD5:		return 0x;
			case Keyboard.KEY_NUMPAD6:		return 0x;
			case Keyboard.KEY_NUMPAD7:		return 0x;
			case Keyboard.KEY_NUMPAD8:		return 0x;
			case Keyboard.KEY_NUMPAD9:		return 0x;
			case Keyboard.KEY_NUMPADCOMMA:	return 0x;
			case Keyboard.KEY_NUMPADENTER:	return 0x;
			case Keyboard.KEY_NUMPADEQUALS:	return 0x;
			case Keyboard.KEY_DIVIDE:		return 0x;
			case Keyboard.KEY_MULTIPLY:		return 0x;
			case Keyboard.KEY_SUBTRACT:		return 0x;
			case Keyboard.KEY_ADD:			return 0x2B;

			case Keyboard.KEY_F1:			return 0x;
			case Keyboard.KEY_F2:			return 0x;
			case Keyboard.KEY_F3:			return 0x;
			case Keyboard.KEY_F4:			return 0x;
			case Keyboard.KEY_F5:			return 0x;
			case Keyboard.KEY_F6:			return 0x;
			case Keyboard.KEY_F7:			return 0x;
			case Keyboard.KEY_F8:			return 0x;
			case Keyboard.KEY_F9:			return 0x;
			case Keyboard.KEY_F10:			return 0x;
			case Keyboard.KEY_F11:			return 0x;
			case Keyboard.KEY_F12:			return 0x;
			case Keyboard.KEY_F13:			return 0x;
			case Keyboard.KEY_F14:			return 0x;
			case Keyboard.KEY_F15:			return 0x;

			case Keyboard.KEY_APPS:			return 0x;
			case Keyboard.KEY_POWER:		return 0x;
			case Keyboard.KEY_SLEEP:		return 0x;

			/* Meta, unused here:
			case Keyboard.KEY_LCONTROL:		return 0x;
			case Keyboard.KEY_LMENU:		return 0x;
			case Keyboard.KEY_LMETA:		return 0x;
			case Keyboard.KEY_LSHIFT:		return 0x;
			case Keyboard.KEY_LWIN:			return 0x;
			case Keyboard.KEY_RCONTROL:		return 0x;
			case Keyboard.KEY_RMENU:		return 0x;
			case Keyboard.KEY_RMETA:		return 0x;
			case Keyboard.KEY_RSHIFT:		return 0x;
			case Keyboard.KEY_RWIN:			return 0x; */

		}
		return 0;
	}
}
