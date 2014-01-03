package org.myschool.dagucar.plugin;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import org.myschool.dagucar.plugin.remote.DaguCarRemote;
import org.myschool.dagucar.plugin.remote.DirectionCommand;
import org.myschool.dagucar.plugin.remote.SpeedCommand;


public class RunPlugin {

	public static void main(String[] args) throws InterruptedException, IOException {


		DaguCar car = new DaguCar(0);
		Thread.sleep(1000);
		PluginContext.window.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent arg0) {
				System.exit(0);
			}

		});

		//testKeys();
		allWorlds();
	}

	public static void allWorlds() {
		for (int i=0; i<9; i++) {
			DaguCar car = new DaguCar(0, i);
			System.out.println("Level: " + i);
			car.waitOnNextKey();
		}

	}

	public static void goS(int carnumber) {
		DaguCar car = new DaguCar(carnumber, 0);

		car.right();
		car.left();
		car.left();
		car.right();

	}
	public static void goBack4(int carnumber) {
		DaguCar car = new DaguCar(carnumber, 0);
		for (int i=0; i<4; i++) {
			car.back();
		}
	}

	public static void go10(int carnumber) {
		DaguCar car = new DaguCar(carnumber, 0);
		for (int i=0; i < 10; i++) {
			car.forward();
		}
	}

	public static void test2() throws InterruptedException {
		DaguCar car = new DaguCar(0);
		car.forward();
		car.forward();
		car.left();
		car.left();
		car.left();
		car.left();

		Thread.sleep(2000);

		car = new DaguCar(0);
		car.forward();
		car.forward();
		car.left();
		car.left();
		car.left();
		car.left();


	}

	private static void testKeys() {
		DaguCar car = new DaguCar(1, 2);
		//		car.setCirclesDone(3/5.0);
		//		car.setForwardFactor(2);
		char x=' ';
		for (int i=0; i<500 && x!='q' && x!=0; i++) {
			x=car.waitOnNextKey();
			if (x == 'w') {
				car.forward();
			}
			if (x == 's') {
				car.back();
			}
			if (x == 'a') {
				car.left();
			}
			if (x == 'd') {
				car.right();
			}
		}
	}

	private static void level2() {
		DaguCar car = new DaguCar(0,2);
		car.left();
		car.right();
		car.right();
		car.left();
		car.forward();
		car.left();
		car.left();
		for (int i=0; i<3; i++) {
			car.back();
		}
		car.check();
	}

	private static void testRemote() throws IOException, InterruptedException {
		DaguCarRemote remote=new DaguCarRemote(3);
		remote.sendNextState(DirectionCommand.NORTH, SpeedCommand.VERY_FASTEST, 1000);
		remote.sendNextState(DirectionCommand.SOUTH, SpeedCommand.STOP, 1000);
	}

	public static void level3() {
		DaguCar car = new DaguCar(0,3);
		car.forward();
		car.forward();
		car.forward();

		gotoRight(car);
		goLeft(car);
		gotoRight(car);
		goLeft(car);
		gotoRight(car);
		goLeft(car);
		for (int i=1; i<=15; i++) {
			car.forward();
		}
	}

	public static void goLeft(DaguCar car) {
		for (int i=1; i<=12; i++) {
			car.forward();
		}
		car.left();
		car.left();
		car.back();
		car.back();
		car.back();
		car.left();
		car.left();
	}

	public static void gotoRight(DaguCar car) {
		for (int i=1; i<=12; i++) {
			car.forward();
		}
		car.right();
		car.right();
		car.back();
		car.back();
		car.back();
		car.right();
		car.right();
	}

	public static void level1() {
		DaguCar car = new DaguCar(0,1);
	}



}
