package org.myschool.dagucar.plugin;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.myschool.dagucar.plugin.actor.CarAction;
import org.myschool.dagucar.plugin.actor.DaguCarActor;
import org.myschool.dagucar.plugin.actor.GoalActor;
import org.myschool.dagucar.plugin.actor.WallActor;
import org.myschool.dagucar.plugin.remote.DaguCarRemote;
import org.myschool.dagucar.plugin.window.DaguCarPluginWindow;

import ch.aplu.jgamegrid.GameGrid;

public class PluginContext {
	public static GameGrid gamegrid;
	public static DaguCarPluginWindow window;
	public static PluginContext executedContext;

	public PluginContext() {
	}
	public final ConcurrentLinkedQueue<CarAction> acts=new ConcurrentLinkedQueue<CarAction>();
	public DaguCarActor actor;
	public DaguCarRemote remote;
	public DaguCar dagucar;
	public final LinkedBlockingQueue<Character> keys= new LinkedBlockingQueue<Character>();

	public void closeContext() {
		if (PluginContext.gamegrid!=null) {
			PluginContext.gamegrid.doPause();
			PluginContext.gamegrid.removeActors(DaguCarActor.class);
			PluginContext.gamegrid.removeActors(GoalActor.class);
			PluginContext.gamegrid.removeActors(WallActor.class);
			PluginContext.gamegrid.getBg().clear();
			PluginContext.gamegrid.refresh();
		}
		if (this.dagucar!=null) {
			this.dagucar.setStateText("Stopped!");
		}
		this.keys.clear();
		if (this.remote!=null) {
			this.remote.close();
		}
	}
}
