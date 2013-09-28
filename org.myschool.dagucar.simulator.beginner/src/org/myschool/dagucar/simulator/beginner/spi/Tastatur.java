package org.myschool.dagucar.simulator.beginner.spi;

import com.sun.glass.events.KeyEvent;

public interface Tastatur {
	char KEY_UP_CHAR = KeyEvent.VK_UP;
	char KEY_DOWN_CHAR = KeyEvent.VK_DOWN;
	char KEY_LEFT_CHAR = KeyEvent.VK_LEFT;
	char KEY_RIGHT_CHAR = KeyEvent.VK_RIGHT;
	public char liefereNaechsteTaste();
}
