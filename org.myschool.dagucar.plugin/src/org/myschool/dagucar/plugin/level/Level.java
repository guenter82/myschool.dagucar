package org.myschool.dagucar.plugin.level;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.myschool.dagucar.plugin.PluginContext;
import org.myschool.dagucar.plugin.actor.DaguCarActor;
import org.myschool.dagucar.plugin.actor.GoalActor;
import org.myschool.dagucar.plugin.window.Listeners;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.GGActorCollisionListener;
import ch.aplu.jgamegrid.GGMouse;
import ch.aplu.jgamegrid.Location;

public enum Level {
	level0("Aufgabe 0",new Location(1,9), 0, new Location(1,7), 180),
	level1("Aufgabe 1", new Location(1,9), 0, new Location(1,7), 180),
	level2("Aufgabe 2", new Location(1,9), 0, new Location(14,12), 270)
	;

	private final static String icon_car_actor ="dagucar-actor48.png";
	private final static String icon_car_actor_grey ="dagucar-actor48-grau.png";
	private final static String icon_car_actor_fire ="dagucar-actor48-fire.png";
	private final static String icon_wall ="wall.png";

	private static BufferedImage img=null;
	private static BufferedImage img_grey=null;
	private static BufferedImage img_wall=null;
	private static BufferedImage img_fire=null;
	private final String label;
	private final Location startLocation;
	private final Location endLocation;
	private final int startDirection;
	private final int endDirection;
	private Location[] wallLocations;

	private Level(String label, Location startLocation,int startDirection,Location endLocation,int endDirection) {
		this.label = label;
		this.startDirection = startDirection;
		this.startLocation = startLocation;
		this.endLocation = endLocation;
		this.endDirection = endDirection;
		switch (this.ordinal()) {
		case 0:
			this.wallLocations = new Location[]{ };
			break;
		case 1:
			this.wallLocations = new Location[] {
					new Location(12,6),
					new Location(12,7),
					new Location(12,8),
					new Location(12,9),
					new Location(12,10),
					new Location(12,11),
					new Location(12,12)};
			break;
		case 2:
			this.wallLocations = new Location[] {
					new Location(4,9),
					new Location(5,9),
					new Location(6,9),
					new Location(13,10),
					new Location(13,11),
					new Location(13,12),
					new Location(13,13),
					new Location(13,14),
					new Location(15,10),
					new Location(15,11),
					new Location(15,12),
					new Location(15,13),
					new Location(15,14),
					new Location(14,14)
			};
			break;
		default:
			this.wallLocations = new Location[0];
			break;
		}
	}

	@Override
	public String toString() {
		return this.label;
	}

	public void initWorld() {
		Level.img = this.createImage(Level.img, Level.icon_car_actor);
		Level.img_grey = this.createImage(Level.img_grey, Level.icon_car_actor_grey);
		Level.img_fire = this.createImage(Level.img_fire, Level.icon_car_actor_fire);
		Level.img_wall = this.createImage(Level.img_wall, Level.icon_wall);
		DaguCarActor car = new DaguCarActor(true, img, img_fire);
		PluginContext.executedContext.actor=car;
		car.setSlowDown(4);
		Actor goal=new GoalActor(true, img_grey) ;
		for (Location l:this.wallLocations) {
			Actor wall=new GoalActor(false, img_wall) ;
			PluginContext.gamegrid.addActor(wall, l);
			car.addCollisionActor(wall);
		}

		PluginContext.gamegrid.addActor(goal, this.endLocation);
		PluginContext.gamegrid.addActor(car, this.startLocation);
		if (this.endDirection==180) {
			goal.setVertMirror(true);
		}
		goal.setDirection(this.endDirection);
		car.setDirection(this.startDirection);
		car.setCollisionRectangle(new Point(0,0), 48, 32);
		GGActorCollisionListener collisionHandler=new GGActorCollisionListener() {
			@Override
			public int collide(Actor active, Actor passive) {
				DaguCarActor car=null;
				if (active instanceof DaguCarActor) {
					car=(DaguCarActor) active;
				}
				if (car==null) {
					return 0;
				}
				car.showNextSprite();
				if (PluginContext.gamegrid.isRunning()) {
					System.out.println("-- Kollision! --");
				}
				PluginContext.executedContext.dagucar.collision = true;
				PluginContext.gamegrid.refresh();
				PluginContext.gamegrid.doPause();
				return 0;
			}
		};
		car.addActorCollisionListener(collisionHandler);

		//PluginContext.gamegrid.p
		car.addMouseTouchListener(Listeners.contextMenuListener, GGMouse.rClick);
		PluginContext.window.setVisible(true);
		PluginContext.gamegrid.doRun();
		//PluginContext.executedContext.dagucar.setStateText("Running ...");
	}

	public BufferedImage createImage(BufferedImage in, String name) {
		if (in==null) {
			String imgLocation="/image/"+name;
			URL imageURL = this.getClass().getResource(imgLocation);
			if (imageURL == null) { // image found
				throw new IllegalArgumentException("Resource not found: " + imageURL);
			}

			try {
				in = ImageIO.read(imageURL);
			} catch (IOException e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		}
		return in;
	}

	public boolean checkWorld(DaguCarActor actor) {
		if (actor == null ) {
			return false;
		}
		return actor.getLocation().equals(this.endLocation) && actor.getDirection() == this.endDirection;

	}

}
