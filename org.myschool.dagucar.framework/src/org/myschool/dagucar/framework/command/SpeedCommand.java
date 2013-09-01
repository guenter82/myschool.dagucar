package org.myschool.dagucar.framework.command;
public enum SpeedCommand {
        STOP( (byte) 0x00 ), //-- doesn't move
//        SLOWEST( (byte) 0x01), //-- doesn't move
//        WAY_WAY_SLOW( (byte) 0x02), //-- doesn't move
//        WAY_SLOW( (byte) 0x03), //-- doesn't move
//        LESS_WAY_SLOW( (byte) 0x04), //-- doesn't move
        SLOW( (byte) 0x05),
        EASY_GOING( (byte) 0x06),
        CRUISING( (byte) 0x07),
        MOVING_RIGHT_ALONG( (byte) 0x08),
        MOVING_QUICK( (byte) 0x09),
        MOVING_QUICKER( (byte) 0x0A),
        MOVING_PRETTY_DARN_QUICK( (byte) 0x0B),
        FAST( (byte) 0x0C),
        FASTER( (byte) 0x0D),
        FASTEST( (byte) 0x0E),
        VERY_FASTEST( (byte) 0x0F);

    public final byte code;

    SpeedCommand(byte code2) {
        this.code=code2;
     }

    static SpeedCommand getCommandFromByte(byte code ) {
    	byte filteredCode = (byte) (code & 0x0F);
    	SpeedCommand[] commands=SpeedCommand.values();
		for (SpeedCommand c:commands) {
			if (c.code == filteredCode) return c;
		}
    	throw new IllegalStateException("Speed command with code: '" + filteredCode + "' note supported.");
    }
}