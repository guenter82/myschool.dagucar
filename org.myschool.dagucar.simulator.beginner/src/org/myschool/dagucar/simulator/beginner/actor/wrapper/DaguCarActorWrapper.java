package org.myschool.dagucar.simulator.beginner.actor.wrapper;

import java.util.List;

import org.myschool.dagucar.simulator.beginner.actor.DaguCarActor;
import org.myschool.dagucar.simulator.beginner.spi.DaguCar;

public class DaguCarActorWrapper implements DaguCar {
	private final List<DaguCarActor> actors;
	public DaguCarActorWrapper(List<DaguCarActor> actors) {
		this.actors = actors;
	}
	@Override
	public void fahreVor() {
		for (DaguCarActor actor:actors) {
			actor.goAhead();	
		}
	}
	@Override
	public void fahreHalbeLinksKurve() {
		for (DaguCarActor actor:actors) {
			actor.goTurnHalfLeft();;	
		}
	}
	@Override
	public void fahreHalbeRechtsKurve() {
		for (DaguCarActor actor:actors) {
			actor.goTurnHalfRight();	
		}
	}
	@Override
	public void fahreZurueck() {
		for (DaguCarActor actor:actors) {
			actor.goBack();	
		}
	}
}
