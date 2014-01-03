package org.myschool.dagucar.plugin.actor;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.myschool.dagucar.plugin.PluginContext;
import org.myschool.dagucar.plugin.level.Level;
import org.myschool.dagucar.plugin.window.DaguCarPluginWindow;

import ch.aplu.jgamegrid.Actor;

public class DaguCarActor extends Actor {

	public final ConcurrentLinkedQueue<CarAction> acts;
	public boolean collided=false;
	public DaguCarActor(boolean changeDirection, BufferedImage... img) {
		super (changeDirection, img);
		this.acts = PluginContext.executedContext.acts;
	}
	@Override
	public void act() {
		if (this.collided) {
			return;
		}
		CarAction act = this.acts.poll();
		this.act(act);
	}
	public void goStraight() {
		Point p1 = this.getPixelLocation();
		this.move(1);
		Point p2 = this.getPixelLocation();
		this.getBackground().setPaintColor(Color.LIGHT_GRAY);
		this.getBackground().drawLine(p1, p2);
	}
	public void goRight() {
		double r = Math.sqrt(DaguCarPluginWindow.cellWidth*DaguCarPluginWindow.cellWidth*9) + 2;
		double startDir = this.getDirection();
		Point p1 = this.getPixelLocation();
		int r45 = (int)(Math.sqrt(r*r/2)) - 4;
		if (startDir==0.0) {
			p1.y=p1.y+(int)(r);
			p1.x=p1.x;
		} else if (startDir==45.0) {
			p1.y=p1.y+r45;
			p1.x=p1.x-r45;
		} else if (startDir==135.0) {
			p1.y=p1.y-r45;
			p1.x=p1.x-r45;
		} else if (startDir==225.0) {
			p1.y=p1.y-r45;
			p1.x=p1.x+r45;
		} else if (startDir==315.0) {
			p1.y=p1.y+r45;
			p1.x=p1.x+r45;
		} else if (startDir==90.0) {
			p1.y=p1.y;
			p1.x=p1.x-(int)(r);
		} else if (startDir==180.0) {
			p1.y=p1.y-(int)(r);
			p1.x=p1.x;
		} else if (startDir==270.0) {
			p1.y=p1.y;
			p1.x=p1.x+(int)(r);
		}

		this.move(1);
		this.setDirection(this.getDirection() + 45);
		this.move(1);

		this.getBackground().setPaintColor(Color.LIGHT_GRAY);
		this.getBackground().drawArc(p1, (int)r, -startDir+95, - 50);
	}
	public void goLeft() {
		double r = Math.sqrt(DaguCarPluginWindow.cellWidth*DaguCarPluginWindow.cellWidth*9) + 2;
		double startDir = this.getDirection();
		Point p1 = this.getPixelLocation();
		int r45 = (int)(Math.sqrt(r*r/2)) - 4;

		if (startDir==0.0) {
			p1.y=p1.y-(int)(r);
			p1.x=p1.x;
		} else if (startDir==45.0) {
			p1.y=p1.y-r45;
			p1.x=p1.x+r45;
		} else if (startDir==135.0) {
			p1.y=p1.y+r45;
			p1.x=p1.x+r45;
		} else if (startDir==225.0) {
			p1.y=p1.y+r45;
			p1.x=p1.x-r45;
		} else if (startDir==315.0) {
			p1.y=p1.y-r45;
			p1.x=p1.x-r45;
		} else if (startDir==90.0) {
			p1.y=p1.y;
			p1.x=p1.x+(int)(r);
		} else if (startDir==180.0) {
			p1.y=p1.y+(int)(r);
			p1.x=p1.x;
		} else if (startDir==270.0) {
			p1.y=p1.y;
			p1.x=p1.x-(int)(r);
		}

		this.move(1);
		this.setDirection(this.getDirection() - 45);
		this.move(1);

		this.getBackground().setPaintColor(Color.LIGHT_GRAY);
		this.getBackground().drawArc(p1, (int)r, - startDir - 95.0, +50.0);
	}
	public void goBack() {
		Point p1 = this.getPixelLocation();
		this.move(-1);
		Point p2 = this.getPixelLocation();

		this.getBackground().setPaintColor(Color.LIGHT_GRAY);
		this.getBackground().drawLine(p1, p2);
	}

	@Override
	protected void finalize() throws Throwable
	{
		//System.out.println("DaguCar Actor destroyed");
		super.finalize(); //not necessary if extending Object.
	}
	public void act(CarAction act) {
		if (act == null) {
			return;
		}
		if (PluginContext.executedContext.remote!=null) {
			PluginContext.executedContext.remote.act(act);
		} else {
			System.out.println("Sim " + act);
		}
		switch (act) {
		case back:
			this.goBack();
			break;
		case left:
			this.goLeft();
			break;
		case right:
			this.goRight();
			break;
		case forward:
			this.goStraight();
			break;
		case check:
			Level l=PluginContext.executedContext.dagucar.level;
			boolean isSolved=PluginContext.executedContext.dagucar.level.checkWorld(this);
			if (isSolved) {
				PluginContext.executedContext.dagucar.setStateText("Check: Super, du hast die " + l + " gelöst.");
			} else {
				PluginContext.executedContext.dagucar.setStateText("Check: Leider ist die Position oder Richtung des DaguCars noch nicht richtig.");
			}
		default:
			break;

		}
		if (this.getDirection() == 180) {
			this.setVertMirror(true);
		} else {
			this.setVertMirror(false);
		}

	}




}
