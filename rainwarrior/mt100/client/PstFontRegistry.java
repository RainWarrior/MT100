/*

Copyright © 2012, 2013 RainWarrior

This file is part of MT100.

MT100 is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

MT100 is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with MT100. If not, see <http://www.gnu.org/licenses/>.

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
//		loadFont("gr928-8x16-thin.psfu.gz");
//		loadFont("gr737b-9x16-medieval.psfu.gz");
	}
}
