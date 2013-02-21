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

public class PstFontRegistry
{
	public static PstFont nullFont = new PstFont();
	public static HashMap<Integer, PstFont> fontMap = new HashMap<Integer, PstFont>();
	public static HashMap<Integer, Integer> indexMap = new HashMap<Integer, Integer>();
	public static void loadFont(String name)
	{
		PstFont font = new PstFont(name);
	}

	public static PstFont getFont(Integer c)
	{
		PstFont font = fontMap.get(c);
		return (font == null) ? nullFont : font;
	}

	public static Integer getIndex(Integer c)
	{
		Integer index = indexMap.get(c);
		return (index == null) ? 0 : index;
	}

	static void init()
	{
		loadFont("Lat15-VGA8.psf.gz");
//		loadFont("Lat15-Fixed16.psf.gz");
//		loadFont("Lat15-VGA32x16.psf.gz");
//		loadFont("Uni2-Terminus16.psf.gz");
//		loadFont("Lat15-Terminus14.psf.gz");
//		loadFont("Lat15-Terminus20x10.psf.gz");
	}
}
