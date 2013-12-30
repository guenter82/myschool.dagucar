package org.myschool.dagucar.plugin;

import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;

import org.myschool.dagucar.plugin.actor.CarAction;
import org.myschool.dagucar.plugin.level.Level;
import org.myschool.dagucar.plugin.remote.DaguCarRemote;
import org.myschool.dagucar.plugin.window.DaguCarPluginWindow;
import org.myschool.dagucar.plugin.window.Listeners;

public class DaguCar {

	public Object monitor=new Object();
	public final PluginContext context;
	public final ConcurrentLinkedQueue<CarAction> acts;
	public final Level level;

	public boolean collision = false;


	public DaguCar(int number) {
		this(number, 1);
	}

	public DaguCar(int number, int level) {
		this.level = Level.values()[level];
		this.context = new PluginContext();
		this.context.dagucar = this;
		this.acts = this.context.acts;
		if (PluginContext.executedContext==null) {
			PluginContext.executedContext = this.context;
			this.startGUI();
		} else {
			PluginContext.executedContext.closeContext();
			PluginContext.executedContext = this.context;
			this.level.initWorld();
		}
		if (number>0) {
			this.context.remote=new DaguCarRemote(number);
		} else {
			this.context.remote=null;
		}
	}

	public void check() {
		this.acts.offer(CarAction.check);
	}

	public void setCirclesDone(double circlesDone) {
		this.context.remote.circlesDone=circlesDone;
	}

	public void forward() {
		this.acts.offer(CarAction.forward);
	}

	public void left() {
		this.acts.offer(CarAction.left);
	}

	public void right() {
		this.acts.offer(CarAction.right);
	}

	public void back() {
		this.acts.offer(CarAction.back);
	}

	private void startGUI() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					DaguCarPluginWindow window=new DaguCarPluginWindow(DaguCar.this.context);
					window.setVisible(true);
					window.addWindowListener(new WindowAdapter() {
						@Override
						public void windowOpened(WindowEvent arg0) {
							DaguCar.this.level.initWorld();
						}

					});
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
						| UnsupportedLookAndFeelException e) {
					throw new IllegalStateException("Konnte das DaguCar Simulationsfenster nicht öffnen.", e);
				}

			}
		});
	}

	public char waitOnNextKey() {
		KeyEvent key=this.waitOnNextKeyEvent();
		if (key==null) {
			return 0;
		} else {
			return key.getKeyChar();
		}
	}

	public int waitOnNextKeyCode() {
		KeyEvent key=this.waitOnNextKeyEvent();
		if (key==null) {
			return 0;
		} else {
			return key.getKeyCode();
		}
	}


	private KeyEvent waitOnNextKeyEvent() {
		if (this.collision) {
			throw new IllegalStateException("Keine Eingabe mehr möglich, da schon eine Kollision passierte!");
		}
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(Listeners.keyEventProcessor);
		try {
			KeyEvent key = null;
			for (int i=0;i<10;i++) {
				if (this.collision) {
					throw new IllegalStateException("Keine Eingabe mehr möglich, da schon eine Kollision passierte!");
				}
				key = PluginContext.executedContext.keys.poll(6000, TimeUnit.MILLISECONDS);
				if (key!=null) {
					return key;
				}
			}
			PluginContext.executedContext.closeContext();
			throw new IllegalStateException("Timeout beim Warten: 5 Minuten keine Taste gedrückt. Die Simulation wird beendet.");
		} catch (InterruptedException e) {
			return null;
		} finally {
			KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventPostProcessor(Listeners.keyEventProcessor);
		}
	}

	public void setStateText(final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				for (int  i=0; i<5 && PluginContext.window==null; i++) {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
					}
				}
				PluginContext.window.setStateText(text);
			}
		});
	}

	public static void closeSimulation() {
		PluginContext.executedContext.closeContext();
		PluginContext.gamegrid.dispose();
		PluginContext.window.dispose();
	}


	@Override
	protected void finalize() throws Throwable
	{
		//System.out.println("DaguCar destroyed");
		super.finalize(); //not necessary if extending Object.
	}




}
