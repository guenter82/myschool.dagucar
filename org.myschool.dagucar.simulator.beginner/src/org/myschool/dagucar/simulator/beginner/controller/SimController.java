package org.myschool.dagucar.simulator.beginner.controller;

import javax.swing.SwingUtilities;

import org.myschool.dagucar.simulator.beginner.editor.SimEditorMainWindow;

public class SimController implements Runnable{
	public final SimContext context = new SimContext();
	
	public enum State {
		NOT_READY,
		READY,
		RUNNING,
	}
	
	public State state;
	
	
	public SimController() {
		this.state=State.READY;
	}
	
	@Override
	public void run() {
		startGUI();
		this.state=State.RUNNING;
	}
	
	
	/* Start all Swing applications on the EDT. */
	private void startGUI() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new SimEditorMainWindow().setVisible(true);
			}
		});
	}

	




	
	
}
