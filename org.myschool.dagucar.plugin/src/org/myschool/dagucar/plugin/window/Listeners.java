package org.myschool.dagucar.plugin.window;

import java.awt.Component;
import java.awt.Container;
import java.awt.KeyEventPostProcessor;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.myschool.dagucar.plugin.PluginContext;

import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.GGMouse;
import ch.aplu.jgamegrid.GGMouseTouchListener;

public class Listeners {
	public static final String toggleParentName = "toggleparent";
	public static final PopupMenu popup=new PopupMenu();


	public static KeyEventPostProcessor keyEventProcessor = new KeyEventPostProcessor() {

		@Override
		public boolean postProcessKeyEvent(KeyEvent e) {
			if (!PluginContext.window.isActive()) {
				return false;
			}

			if (e.getID() != KeyEvent.KEY_TYPED) {
				return false;
			}
			//System.out.println("Key " + e.getKeyChar());
			PluginContext.executedContext.keys.add(e.getKeyChar());
			return false;
		}
	};

	public static GGMouseTouchListener contextMenuListener= new GGMouseTouchListener() {

		@Override
		public void mouseTouched(Actor car, GGMouse mouse, Point point) {
			popup.show(PluginContext.gamegrid, mouse.getX(), mouse.getY());
		}

	};


	public static MouseListener toggle = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			Container collapseable = findCollapseableParent(e.getComponent());
			JComponent top = (JComponent) collapseable.getComponent(0);
			JComponent center = (JComponent) collapseable.getComponent(1);
			final JComponent visible;
			final JComponent hidden;
			if (top!=null && center!=null) {
				if (top.isVisible()) {
					visible = center;
					hidden = top;
				} else {
					visible = top;
					hidden = center;
				}
			} else {
				throw new IllegalStateException("Beide Komponenten müssen existieren: NORTH (index: 0) " + top + " und CENTER (index: 1) " + center);
			}

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					visible.setVisible(true);
					hidden.setVisible(false);
				}
			});
		}
	};
	public static Container findCollapseableParent(Component c) {
		if (c!=null) {
			Container parent = c.getParent();
			while (parent !=null) {
				String name=parent.getName();
				if (toggleParentName.equals(name)) {
					return parent;
				} else {
					parent = parent.getParent();
				}
			}
		}
		throw new IllegalStateException("Keine 'collapseable' Vaterknoten gefunden.");
	}

	public static ListSelectionListener methodsSelectionListener = new ListSelectionListener() {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting() == false) {
				@SuppressWarnings("unchecked")
				JList<String> list = (JList<String>) e.getSource();
				int index = list.getSelectedIndex();
				final String text;
				if (index == -1) {  //No selection
					text = "-- Kein Befehl ausgewählt ---";
				} else {  //Selection
					//					HelpObject obj=(HelpObject) PluginContext.window.comboBox.getSelectedItem();
					HelpObject obj = HelpObject.car;
					String method = obj.getMethods()[index];
					text = obj.getMethodHelp(method);

				}
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						PluginContext.window.methodHelp.setText(text);
					}
				});
			}
		}
	};


}
