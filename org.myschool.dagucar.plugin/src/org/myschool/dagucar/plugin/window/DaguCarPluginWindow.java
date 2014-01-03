package org.myschool.dagucar.plugin.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.myschool.dagucar.plugin.PluginContext;

import ch.aplu.jgamegrid.GameGrid;
import ch.aplu.jgamegrid.Location;
import ch.aplu.jgamegrid.TextActor;

public class DaguCarPluginWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private ImageIcon expandIcon= this.createImageIcon("/image/toggle_expand16.png", "Expand");
	private ImageIcon collapseIcon= this.createImageIcon("/image/toggle16.png", "Collapse");


	public static final int cells=20;
	public static final int cellWidth=33;
	public static final int gameGridWidth = cells * cellWidth;
	Dimension gridDim=new Dimension(gameGridWidth, gameGridWidth);

	Border hintsBorder = BorderFactory.createBevelBorder(BevelBorder.LOWERED,new Color(250,253,255), new Color(160,160,190));
	Border hintsPadding = new EmptyBorder(2, 4, 2, 2);

	GameGrid gameGrid;
	JToolBar toolbar;
	JList<String> methodList;
	JComboBox<HelpObject> comboBox;
	JLabel methodHelp;
	JTextArea objectHelp;
	JLabel stateText;


	public DaguCarPluginWindow(PluginContext context) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		PluginContext.window = this;
		JPanel panel=this.organizeComponentsInWindow();
		// Configure the visualization of the window
		this.configureMainWindow(panel);
		this.pack();
		// Calculates Size of components; must be called before Actors are added
		// Add Numbers
		this.createColumnNumbers(this.gameGrid);
		PluginContext.gamegrid=this.gameGrid;
	}



	/*
	 * Organize the Components (Toolbar, Split Pane with editor and game grid and debug bar ... ) in the window
	 */
	private JPanel organizeComponentsInWindow() {
		// Content Window
		JPanel window=new JPanel(new BorderLayout());
		// Toolbar
		this.toolbar = this.createToolBar();
		this.toolbar.setVisible(false);
		this.gameGrid = this.createGameGrid();

		JPanel wrapGameGrid = new JPanel(new BorderLayout());
		wrapGameGrid.add(this.gameGrid, BorderLayout.CENTER);

		JLabel collapsedToolbar = new JLabel(this.expandIcon);
		collapsedToolbar.setText("Dokumentation für DaguCar Befehle ...");
		collapsedToolbar.addMouseListener(Listeners.toggle);

		JPanel wrap=new JPanel(new BorderLayout());
		wrap.add(collapsedToolbar, BorderLayout.WEST);

		JPanel expandable=new JPanel(new BorderLayout());
		expandable.add(wrap, BorderLayout.NORTH);
		expandable.add(this.toolbar, BorderLayout.CENTER);
		expandable.setName(Listeners.toggleParentName);

		JPanel statusZeileWrap=new JPanel(new BorderLayout());
		JLabel label=new JLabel("Statusnachricht: ");
		this.stateText=new JLabel("");
		statusZeileWrap.add(label, BorderLayout.WEST);
		statusZeileWrap.add(this.stateText, BorderLayout.CENTER);


		window.add(expandable, BorderLayout.NORTH);
		window.add(this.gameGrid, BorderLayout.CENTER);
		window.add(statusZeileWrap, BorderLayout.SOUTH);

		return window;

	}



	private void configureMainWindow(JPanel window) {
		this.setContentPane(window);
		this.setTitle("DaguCar - Simulation");
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		int half_x=(int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2);
		this.setLocation(0, 0);

		this.setIconImage(new ImageIcon(this.getClass().getResource("/image/dagucar32.png")).getImage());

		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent arg0) {
				PluginContext.executedContext.closeContext();
				PluginContext.executedContext.dagucar.level.initWorld();
				JOptionPane.showMessageDialog(DaguCarPluginWindow.this, "Die Simulation wurde zurück gesetzt.", "Nachricht", JOptionPane.INFORMATION_MESSAGE);
			}

		});
	}



	private void createColumnNumbers(GameGrid gameGrid) {
		Font font = new Font("Arial", Font.PLAIN, 13);
		Point locationOffset = new Point(-13, -5);
		Color numberColor = Color.lightGray;
		for (int i=0; i<this.cells; i++ ) {
			if (i==0) {
				TextActor corner=new TextActor("0/0", numberColor, Color.white, font);
				corner.setActEnabled(false);
				gameGrid.addActor(corner, new Location(0, 0));
				corner.setLocationOffset(locationOffset);
			} else {
				TextActor axesX=new TextActor(Integer.toString(i), numberColor, Color.white, font);
				axesX.setActEnabled(false);
				gameGrid.addActor(axesX, new Location(i, 0));
				axesX.setLocationOffset(locationOffset);
				TextActor axesY=new TextActor(Integer.toString(i), numberColor, Color.white, font);
				axesY.setActEnabled(false);
				gameGrid.addActor(axesY, new Location(0, i));
				axesY.setLocationOffset(locationOffset);
			}
		}
	}



	private GameGrid createGameGrid() {
		GameGrid gameGrid = new GameGrid(true);
		gameGrid.setNbHorzCells(this.cells);
		gameGrid.setNbVertCells(this.cells);
		gameGrid.setCellSize(this.cellWidth);
		gameGrid.setGridColor(Color.LIGHT_GRAY);
		gameGrid.setBgColor(Color.white);
		gameGrid.getPanel().setPaintColor(Color.gray);
		gameGrid.setMinimumSize(this.gridDim);
		gameGrid.setMaximumSize(this.gridDim);
		gameGrid.setSimulationPeriod(50);
		return gameGrid;
	}





	private JToolBar createToolBar() {
		this.toolbar = new JToolBar(JToolBar.HORIZONTAL);
		this.toolbar.setFloatable(false);
		this.toolbar.setRollover(true);

		/* not used */
		//		this.objectHelp = new JTextArea("Das DaguCar. Der Startzustand ist in der Simulation dargestellt. Du kannst folgende Befehle ausführen lassen, um den Zustand zu ändern.");
		//		this.objectHelp.setPreferredSize(new Dimension(150, 60));
		//		this.objectHelp.setWrapStyleWord(true);
		//		this.objectHelp.setLineWrap(true);
		//		this.objectHelp.setEditable(false);
		//		this.objectHelp.setBorder(new EmptyBorder(20, 20, 5, 0));
		//
		//		JLabel objectLabel = new JLabel("Objekte");
		//		this.comboBox = new JComboBox<HelpObject>(HelpObject.values());
		//		//	this.comboBox.addActionListener(Listeners.objectSelectionListener);
		//		this.comboBox.setSelectedIndex(0);
		//
		//
		//		JPanel wrapComboBox=new JPanel(new BorderLayout());
		//		wrapComboBox.add(this.comboBox, BorderLayout.NORTH);
		//
		//		JPanel labelCombo =new JPanel(new BorderLayout());
		//		labelCombo.add(objectLabel, BorderLayout.NORTH);
		//		labelCombo.add(wrapComboBox, BorderLayout.CENTER);
		//
		//
		//		JPanel labelComboAndHelp =new JPanel(new BorderLayout());
		//		labelComboAndHelp.add(labelCombo, BorderLayout.WEST);
		//		labelComboAndHelp.add(this.objectHelp, BorderLayout.CENTER);
		//		labelComboAndHelp.setBorder(new EmptyBorder(0, 60, 0, 20));
		/* not used */

		this.methodHelp = new JLabel();
		this.methodHelp.setPreferredSize(new Dimension(300, 100));
		//		this.methodHelp.setWrapStyleWord(true);
		//		this.methodHelp.setLineWrap(true);
		//		this.methodHelp.setEditable(false);
		this.methodHelp.setBorder(new EmptyBorder(20, 20, 5, 5));

		JLabel commandLabel = new JLabel(this.collapseIcon, SwingConstants.LEFT);
		commandLabel.setText( "Dokumentation der Befehle:" );
		commandLabel.addMouseListener(Listeners.toggle);
		JScrollPane hintScroller = this.createToolBarMethod();

		JPanel labelAndCommands=new JPanel(new BorderLayout());
		labelAndCommands.add(commandLabel, BorderLayout.NORTH);
		labelAndCommands.add(hintScroller, BorderLayout.CENTER);
		labelAndCommands.setBorder(new EmptyBorder(0,0,5,0));



		//		this.toolbar.add(empty2);
		//		this.toolbar.add(labelComboAndHelp);
		this.toolbar.add(labelAndCommands);
		this.toolbar.add(this.methodHelp);
		return this.toolbar;
	}



	private JScrollPane createToolBarMethod() {

		DefaultListModel<String> listModel = new DefaultListModel<String>();
		for (String method:HelpObject.car.getMethods()) {
			listModel.addElement("<html>"+method+"</html>");
		}
		this.methodHelp.setText(HelpObject.car.getMethodHelp(HelpObject.car.getMethods()[0]));

		this.methodList = new JList<String>(listModel);
		this.methodList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.methodList.setVisibleRowCount(-1);
		this.methodList.setBorder(this.hintsPadding);
		this.methodList.setSelectedIndex(0);
		this.methodList.addListSelectionListener(Listeners.methodsSelectionListener);

		JScrollPane methodListScroller = new JScrollPane(this.methodList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		methodListScroller.setMaximumSize(new Dimension(150, 60));
		methodListScroller.setPreferredSize(new Dimension(150, 60));
		methodListScroller.setBorder(this.hintsBorder);
		return methodListScroller;
	}


	/** Returns an ImageIcon, or null if the path was invalid. */
	private ImageIcon createImageIcon(String path,
			String description) {
		URL imgURL = this.getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Resource not found: " + path);
			return null;
		}
	}

	public void setStateText(String text) {
		this.stateText.setText(text);
	}

}