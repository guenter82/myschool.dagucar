package org.myschool.dagucar.android.bridge.command;

public abstract class CommandUtils {
	public static String commandByteToString(byte code) {
		SpeedCommand low = SpeedCommand.getCommandFromByte(code);
		DirectionCommand high = DirectionCommand.getCommandFromByte(code);
		return high.name() + ": "+low.name();
	}

	public static String commandBytesToString(byte[] codes) {
		if (codes == null || codes.length == 0) return "";
		StringBuilder b=new StringBuilder();
		for (byte code:codes) {
			b.append(commandByteToString(code));
			b.append("\r\n");
		}
		return b.toString();
	}
}
