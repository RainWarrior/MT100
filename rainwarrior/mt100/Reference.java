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

public class Reference
{
	public static final String MOD_ID               = "MT100";
	public static final String MOD_NAME             = "MT100 Terminal Emulator";
	public static final String CHANNEL_NAME         = MOD_ID;
	public static final String SERVER_PROXY_CLASS   = "rainwarrior.mt100.CommonProxy";
	public static final String CLIENT_PROXY_CLASS   = "rainwarrior.mt100.client.ClientProxy";
	public static final int    QUEUE_SIZE           = 4096;
	public static final int    PACKET_SIZE          = 16384;
	public static final int    QUOTA                = 115200 / 20;
	public static final int    NET_QUOTA            = 115200 / 20;
	public static final int    PARSER_UPDATE_QUOTA  = 115200 / 20;
	public static final int    CC_EVENT_QUOTA       = 115200 / 20;
	public static final byte   PACKET_CONNECT       = 0;
	public static final byte   PACKET_TILE_REQUEST  = 1;
	public static final byte   PACKET_TILE_RESPONSE = 2;
	public static final byte   PACKET_DATA          = 1;
	public static final long   TYPEMATIC_DELAY      = 500;
	public static final long   TYPEMATIC_SPEED      = 20;
}
