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

package rainwarrior.mt100;

public class Reference
{
	public static final String MOD_ID = "MT100";
	public static final String MOD_NAME = "MT100 Terminal Emulator";
	public static final String CHANNEL_NAME = MOD_ID;
	public static final String SERVER_PROXY_CLASS = "rainwarrior.mt100.CommonProxy";
	public static final String CLIENT_PROXY_CLASS = "rainwarrior.mt100.client.ClientProxy";
	public static final int QUEUE_SIZE = 4096;
	public static final int PACKET_SIZE = 16384;
	public static final int QUOTA = 115200;
	public static final int NET_QUOTA = 115200;
	public static final int SCREEN_UPDATE_QUOTA = 115200;
	public static final byte PACKET_CONNECT = 0;
	public static final byte PACKET_TILE_REQUEST = 1;
	public static final byte PACKET_TILE_RESPONSE = 2;
	public static final byte PACKET_DATA = 1;
	public static final long TYPEMATIC_DELAY = 500;
	public static final long TYPEMATIC_SPEED = 20;
}
