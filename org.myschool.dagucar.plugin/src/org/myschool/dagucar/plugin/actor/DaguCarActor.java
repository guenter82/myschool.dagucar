package org.myschool.dagucar.plugin.actor;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.myschool.dagucar.plugin.PluginContext;
import org.myschool.dagucar.plugin.level.Level;

import ch.aplu.jgamegrid.Actor;

public class DaguCarActor extends Actor {

	public final ConcurrentLinkedQueue<CarAction> acts;
	public DaguCarActor(boolean changeDirection, BufferedImage... img) {
		super (changeDirection, img);
		this.acts = PluginContext.executedContext.acts;
	}
	@Override
	public void act() {
		CarAction act = this.acts.poll();
		this.act(act);
	}
	public void goStraight() {
		Point p1 = this.getPixelLocation();
		this.move(2);
		Point p2 = this.getPixelLocation();
		this.getBackground().setPaintColor(Color.LIGHT_GRAY);
		this.getBackground().drawLine(p1, p2);
	}
	public void goRight() {
		Point p1 = this.getPixelLocation();
		this.move(1);
		Point p2 = this.getPixelLocation();
		this.getBackground().setPaintColor(Color.LIGHT_GRAY);
		this.getBackground().drawLine(p1, p2);
		p1 = this.getPixelLocation();
		this.setDirection(this.getDirection() + 45);
		this.move(1);
		p2 = this.getPixelLocation();
		this.getBackground().setPaintColor(Color.LIGHT_GRAY);
		this.getBackground().drawLine(p1, p2);
	}
	public void goLeft() {
		Point p1 = this.getPixelLocation();
		this.move(1);
		Point p2 = this.getPixelLocation();
		this.getBackground().setPaintColor(Color.LIGHT_GRAY);
		this.getBackground().drawLine(p1, p2);
		p1 = this.getPixelLocation();
		this.setDirection(this.getDirection() - 45);
		this.move(1);
		p2 = this.getPixelLocation();
		this.getBackground().setPaintColor(Color.LIGHT_GRAY);
		this.getBackground().drawLine(p1, p2);
	}
	public void goBack() {
		Point p1 = this.getPixelLocation();
		this.move(-2);
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
