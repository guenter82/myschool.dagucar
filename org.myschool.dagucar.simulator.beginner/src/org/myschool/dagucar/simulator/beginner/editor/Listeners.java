package org.myschool.dagucar.simulator.beginner.editor;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.myschool.dagucar.simulator.beginner.controller.SimContext;
import org.myschool.dagucar.simulator.beginner.editor.help.HelpObject;

public class Listeners {
	public static final String toggleParentName = "toggleparent";

	private static SimContext context = SimContext.context;

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
					HelpObject obj=(HelpObject) context.window.comboBox.getSelectedItem();
					String method = obj.getMethods()[index];
					text = obj.getMethodHelp(method);

				}
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						context.window.methodHelp.setText(text);
					}
				});
			}
		}
	};

	public static ActionListener objectSelectionListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			@SuppressWarnings("unchecked")
			JComboBox<HelpObject> cb = (JComboBox<HelpObject>)e.getSource();
			final HelpObject obj = (HelpObject)cb.getSelectedItem();
			final String help;
			if (obj!=null) {
				help = obj.getHelp();
			} else {
				help = "-- Kein Objekt ausgewählt --";
			}
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					context.window.objectHelp.setText(help);
					// update methods list
					@SuppressWarnings("unchecked")
					DefaultListModel<String> listModel = (DefaultListModel<String>) context.window.methodList.getModel();
					listModel.removeAllElements();
					for (String method:obj.getMethods()) {
						listModel.addElement("<html>"+obj+".<font color=blue>"+method+"</font>();</html>");
					}
					if (listModel.size()>0) {
						context.window.methodList.setSelectedIndex(0);
						context.window.methodHelp.setText(obj.getMethodHelp(obj.getMethods()[0]));
					}
				}
			});

		}
	};

	public static MouseListener autoFillMouseListener = new MouseAdapter()  {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				return;
			}
			final HelpObject obj=(HelpObject) context.window.comboBox.getSelectedItem();
			@SuppressWarnings("unchecked")
			JList<String> list = (JList<String>) e.getSource();
			final String method = obj.getMethods()[list.getSelectedIndex()];
			if (UiText.method_car_vor.equals(method)) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						context.window.mainText.append(obj.toString() +"."+method.toString()+"( );\r\n");
					}
				});
			}
		}
	};

}
