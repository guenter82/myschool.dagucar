package org.myschool.dagucar.simulator.beginner.actor.wrapper;

import java.util.List;

import org.myschool.dagucar.simulator.beginner.actor.DaguCarActor;
import org.myschool.dagucar.simulator.beginner.controller.SimContext;
import org.myschool.dagucar.simulator.beginner.spi.DaguCar;

public class DaguCarActorWrapper implements DaguCar {
	private SimContext context = SimContext.context;
	private final List<DaguCarActor> actors;
	public DaguCarActorWrapper(List<DaguCarActor> actors) {
		this.actors = actors;
	}
	@Override
	public void fahreVor() {
		this.context.log("Fahre vor");
		for (DaguCarActor actor:this.actors) {
			actor.goAhead();
		}
	}
	@Override
	public void fahreHalbeLinksKurve() {
		this.context.log("Fahre halbe Linkskurve");
		for (DaguCarActor actor:this.actors) {
			actor.goTurnHalfLeft();
		}
	}
	@Override
	public void fahreHalbeRechtsKurve() {
		this.context.log("Fahre halbe Rechtskurve");
		for (DaguCarActor actor:this.actors) {
			actor.goTurnHalfRight();
		}
	}
	@Override
	public void fahreZurueck() {
		this.context.log("Fahre zurück");
		for (DaguCarActor actor:this.actors) {
			actor.goBack();
		}
	}
}
