package org.myschool.dagucar.simulator.beginner.editor;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;

import org.myschool.dagucar.simulator.beginner.controller.SimContext;

public class Listeners {
	public static final String toggleParentName = "toggleparent";

	private static SimContext context = SimContext.context;

	public static MouseListener toggle = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			Container collapseable = findCollapseableParent(e.getComponent());
			JComponent top = (JComponent) collapseable.getComponent(0);
			JComponent center = (JComponent) collapseable.getComponent(1);
			if (top!=null && center!=null) {
				if (top.isVisible()) {
					top.setVisible(false);
					center.setVisible(true);
				} else {
					top.setVisible(true);
					center.setVisible(false);
				}
				collapseable.validate();
				//context.window.setExtendedState(JFrame.MAXIMIZED_BOTH);
				context.window.pack();
			} else {
				throw new IllegalStateException("Beide Komponenten müssen esixtieren: NORTH (index: 0) " + top + " und CENTER (index: 1) " + center);
			}
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


}
