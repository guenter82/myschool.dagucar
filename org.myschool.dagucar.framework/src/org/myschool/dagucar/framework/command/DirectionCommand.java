package org.myschool.dagucar.framework.command;
public enum DirectionCommand {
    NORTH ( (byte) 0x10),
    SOUTH ( (byte) 0x20),
   // WEST  ( (byte) 0x30), -- doesn't move 
   // EAST  ( (byte) 0x40), -- doesn't move
    NORTHWEST ( (byte) 0x50),
    NORTHEAST ( (byte) 0x60),
    SOUTHWEST ( (byte) 0x70),
    SOUTHEAST( (byte) 0x80);

    public final byte code;

    DirectionCommand(byte code2) {
        this.code=code2;
    }

    public static DirectionCommand getCommandFromByte(byte code ) {
		byte filteredCode = (byte) (code & 0xF0);
    	DirectionCommand[] commands=DirectionCommand.values();
		for (DirectionCommand c:commands) {
			if (c.code == filteredCode) return c;
		}
    	throw new IllegalStateException("Direction command with code: '" + filteredCode + "' note supported.");
    }
}