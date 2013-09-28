package org.myschool.dagucar.simulator.beginner.actor.sim;

import org.myschool.dagucar.simulator.beginner.actor.DaguCarActor;

import ch.aplu.jgamegrid.Actor;

public class DefaultDaguCar extends Actor implements DaguCarActor{

	public DefaultDaguCar(boolean isRotatable, String... filenames) {
		super(isRotatable, filenames);
	}
	
	@Override
	public void goAhead() {
		this.move(1);
		//this.gameGrid.repaint();
	}

	@Override
	public void goBack() {
		this.move(-1);
		this.act();
	}

	@Override
	public void goTurnHalfLeft() {
		this.move(1);
		this.setDirection(this.getDirection() - 45);
		this.move(1);
	}

	@Override
	public void goTurnHalfRight() {
		this.move(1);
		this.setDirection(this.getDirection() + 45);
		this.move(1);
	}

}
