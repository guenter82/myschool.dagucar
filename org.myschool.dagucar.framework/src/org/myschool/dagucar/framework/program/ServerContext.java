package org.myschool.dagucar.framework.program;

import java.util.HashMap;
import java.util.Map;

import org.myschool.dagucar.framework.bluetooth.DaguCar;

public class ServerContext {
	public static final ServerContext context= new ServerContext();
	
	public static final String dagucarDeviceName = "DaguCar";
	public static final String dagucarDevice1 = "201304231621";
	public static final String dagucarDevice2 = "201304231670";
	public static final String dagucarDevice3 = "201304231728";
	public static final String dagucarDevice4 = "001210240245";
	public static final String arg0 = "use_default";
	
	/*
	 * Send protocoll: 
	 *  1. Byte firstHandshakeByte
	 *  2. Byte secondHandshakeByte
	 *  3. Byte serviceByte
	 *  folowing service protocol bytes:
	 *
	 * service handleCommand protocol:
	 *  4. Byte DaguCar Number
	 *  5. Byte DaguCar Command
	 *  6.-9. Bytes (4 Bytes for int, highest last) ... sleeptime in ms
	 * 
	 * 
	 */
	public static final byte firstHandshakeByte = (byte)0xF0;
	public static final byte secondHandshakeByte =  (byte)0x0F;
	public static final byte handleCommandServiceByte =  (byte)0x03;

	public static final Map<Integer, DaguCar> dagucars = new HashMap<Integer, DaguCar>();
	
	public static void cleanUpDaguCars() {
		for (DaguCar daguCar:dagucars.values()) {
			daguCar.cleanup();
		}
	}
	
	private ServerContext(){};
}
