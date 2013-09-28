package org.myschool.dagucar.simulator.beginner.actor.wrapper;

import java.util.ArrayList;
import java.util.List;

import org.myschool.dagucar.simulator.beginner.actor.DaguCarActor;
import org.myschool.dagucar.simulator.beginner.spi.DaguCarSteuerung;

public class DaguCarControllerWrapper implements Runnable{

	private final List<DaguCarActor> actors = new ArrayList<DaguCarActor>();
	private DaguCarSteuerung steuerung;
	
	public void addActor(DaguCarActor actor) {
		actors.add(actor);
	}
	
	public void setActorSteuerung(DaguCarSteuerung robot) {
		this.steuerung = robot;		
	}

	@Override
	public void run() {
		DaguCarActorWrapper wrapper = new DaguCarActorWrapper(actors);
		steuerung.starte(wrapper, new KeyboardWrapper(), new HandbookWrapper(), new FileStorageWrapper());
	}
	

}
