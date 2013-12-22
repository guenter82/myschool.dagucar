package org.myschool.dagucar.simulator.plugin.runner;

import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;

import org.myschool.dagucar.simulator.plugin.window.DaguCarPluginWindow;

public class RunPluginWindow {

	public static void main(String[] args) {
		startGUI();
	}


	private static void startGUI() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					new DaguCarPluginWindow().setVisible(true);
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
						| UnsupportedLookAndFeelException e) {
					System.out.println("Konnte die DaguCar Steuerzentrale nicht starten: " + e.getMessage());
					e.printStackTrace();
				}

			}
		});
	}

}
