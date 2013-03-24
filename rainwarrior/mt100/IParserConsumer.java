/*

Copyright © 2012 RainWarrior

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

package rainwarrior.mt100;

public interface IParserConsumer
{
	public void G0(int c); // Graphical character, 0x20 - 0x7E, 0xA0 - 0x10FFFF
	public void C0(int c);
	public void C1(int c);
	public void Fs(int c);
	public void NormalCS(int c, Integer[] p); // null means default value
	/*
	 * gets called when failed to construct params for previous method
	 */
	public void RawCS(int c, String p);
}
