package org.myschool.dagucar.simulator.beginner.actor;

import org.myschool.dagucar.simulator.beginner.spi.DaguCar;

public interface DaguCarActor {
	public void goAhead();
	public void goBack();
	public void goTurnHalfLeft();
	public void goTurnHalfRight();
}
