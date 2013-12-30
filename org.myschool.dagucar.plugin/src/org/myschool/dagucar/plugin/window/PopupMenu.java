package org.myschool.dagucar.plugin.window;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.myschool.dagucar.plugin.PluginContext;
import org.myschool.dagucar.plugin.actor.CarAction;
import org.myschool.dagucar.plugin.actor.DaguCarActor;

public class PopupMenu extends JPopupMenu {

	private static final long serialVersionUID = 24627352699497041L;

	public PopupMenu(){
		for (CarAction action:CarAction.values()) {
			final CarAction finalAction = action;
			JMenuItem anItem = new JMenuItem(action.toString());
			anItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					DaguCarActor car = PluginContext.executedContext.actor;
					car.act(finalAction);
				}
			});
			this.add(anItem);
		}
	}
}
