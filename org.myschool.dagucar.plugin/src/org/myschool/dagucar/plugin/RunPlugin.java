package org.myschool.dagucar.plugin;

import java.awt.event.KeyEvent;
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

		level2();

		//testKeyCodes();

		//goS(3);
		//goS(2);


		//test2();
		//testKeys();


		//Thread.sleep(5000);
		//System.exit(0);

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
		DaguCar car = new DaguCar(0);
		for (int i=0; i<10; i++) {
			char x=car.waitOnNextKey();
			if (x == 'w'|| Character.getNumericValue(x) == KeyEvent.VK_UP) {
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
			if (x == 'q' || x == 0) {
				System.exit(0);
			}
		}
	}

	private static void testKeyCodes() {
		DaguCar car = new DaguCar(0);
		for (int i=0; i<10; i++) {
			int x=car.waitOnNextKeyCode();
			if (x == KeyEvent.VK_W || x == KeyEvent.VK_UP) {
				car.forward();
			}
			if (x == KeyEvent.VK_Q || x == 0) {
				System.exit(0);
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



}
