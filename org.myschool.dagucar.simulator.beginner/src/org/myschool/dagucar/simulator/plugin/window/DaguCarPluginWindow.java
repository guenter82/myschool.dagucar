package org.myschool.dagucar.simulator.plugin.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.myschool.dagucar.simulator.beginner.actor.sim.DefaultDaguCar;
import org.myschool.dagucar.simulator.beginner.controller.SimContext;
import org.myschool.dagucar.simulator.beginner.controller.SimController;
import org.myschool.dagucar.simulator.beginner.editor.Listeners;
import org.myschool.dagucar.simulator.beginner.editor.help.HelpObject;

import ch.aplu.jgamegrid.GameGrid;
import ch.aplu.jgamegrid.Location;
import ch.aplu.jgamegrid.TextActor;

import com.jgoodies.looks.HeaderStyle;
import com.jgoodies.looks.Options;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.DesertGreen;

public class DaguCarPluginWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private ImageIcon expandIcon= this.createImageIcon("/image/toggle_expand16.png", "Expand");
	private SimContext context = SimContext.context;
	private SimController controller = SimContext.controller;


	int cells=20;
	int cellWidth=33;
	int gameGridWidth = this.cells * this.cellWidth;
	Dimension gridDim=new Dimension(this.gameGridWidth,this.gameGridWidth);

	Border hintsBorder = BorderFactory.createBevelBorder(BevelBorder.LOWERED,new Color(250,253,255), new Color(160,160,190));
	Border hintsPadding = new EmptyBorder(2, 4, 2, 2);

	String icon_car_actor ="small_car_32px.png";
	GameGrid gameGrid;
	JToolBar toolbar;
	JList methodList;
	JComboBox<HelpObject> comboBox;
	JTextArea methodHelp;
	JTextArea objectHelp;



	public DaguCarPluginWindow() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		ToolTipManager.sharedInstance().setInitialDelay(0);
		ToolTipManager.sharedInstance().setDismissDelay(60000);
		this.context.setPluginWindow(this);
		// Activates a special Look and Feel
		//this.activateLookAndFeel();
		// Create visible components;
		this.organizeComponentsInWindow();
		// Expand to Fullscreen
		this.setLocation(0, 0);
		this.pack();
		// Calculates Size of components; must be called before Actors are added
		// Add Numbers
		this.createColumnNumbers(this.gameGrid);
		//after layout work
		this.populateSimulationWorld() ;
	}


	private void populateSimulationWorld() {
		String imgLocation="/image/"+this.icon_car_actor;
		URL imageURL = this.getClass().getResource(imgLocation);
		if (imageURL == null) { // image found
			throw new IllegalArgumentException("Resource not found: " + imageURL);
		}

		BufferedImage in;
		try {
			in = ImageIO.read(imageURL);
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}

		DefaultDaguCar car = new DefaultDaguCar(true, in);

		this.context.setSimDaguCarActor(car);
		this.gameGrid.addActor(car, new Location(1,1));
	}


	private void activateLookAndFeel() {
		// UIManager.setLookAndFeel(new
		// com.jgoodies.looks.windows.WindowsLookAndFeel());
		PlasticLookAndFeel.setPlasticTheme(new DesertGreen());
		try {
			UIManager.setLookAndFeel(new com.jgoodies.looks.windows.WindowsLookAndFeel());
		} catch (Exception e) {
			this.context.log("Could not provide look-and-feel: " + e.getMessage(), e);
		}
	}


	/*
	 * Organize the Components (Toolbar, Split Pane with editor and game grid and debug bar ... ) in the window
	 */
	private void organizeComponentsInWindow() {
		// Content Window
		JPanel window=new JPanel(new BorderLayout());
		// Toolbar
		//JToolBar toolbar = this.createToolBar();
		this.gameGrid = this.createGameGrid();

		//window.add(toolbar, BorderLayout.NORTH);
		window.add(this.gameGrid, BorderLayout.CENTER);

		// Configure the visualization of the window
		this.configureMainWindow(window);
	}



	private void configureMainWindow(JPanel window) {
		this.setContentPane(window);
		this.setTitle("DaguCar - Steuerzentrale");
		//this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		// Set default Window-Location to center
		this.setLocationRelativeTo(null);
		this.setIconImage(new ImageIcon(this.getClass().getResource("/image/dagucar32.png")).getImage());
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
		GameGrid gameGrid = new GameGrid();
		gameGrid.setNbHorzCells(this.cells);
		gameGrid.setNbVertCells(this.cells);
		gameGrid.setCellSize(this.cellWidth);
		gameGrid.setGridColor(Color.LIGHT_GRAY);
		gameGrid.setBgColor(Color.white);
		gameGrid.getPanel().setPaintColor(Color.gray);
		gameGrid.setMinimumSize(this.gridDim);
		gameGrid.setMaximumSize(this.gridDim);
		return gameGrid;
	}





	private JToolBar createToolBar() {
		this.toolbar = new JToolBar(JToolBar.HORIZONTAL);
		this.toolbar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.SINGLE);
		this.toolbar.setFloatable(false);
		this.toolbar.setRollover(true);

		JLabel empty2=new JLabel();
		empty2.setText("            ");
		empty2.setFocusable(false);
		empty2.setBackground(this.toolbar.getBackground());

		this.objectHelp = new JTextArea("Das DaguCar. Der Startzustand ist in der Simulation dargestellt. Du kannst folgende Befehle ausführen lassen, um den Zustand zu ändern.");
		this.objectHelp.setPreferredSize(new Dimension(150, 60));
		this.objectHelp.setWrapStyleWord(true);
		this.objectHelp.setLineWrap(true);
		this.objectHelp.setEditable(false);
		this.objectHelp.setBorder(new EmptyBorder(20, 20, 5, 0));

		JLabel objectLabel = new JLabel("Objekte");
		this.comboBox = new JComboBox<HelpObject>(HelpObject.values());
		this.comboBox.addActionListener(Listeners.objectSelectionListener);
		this.comboBox.setSelectedIndex(0);


		JPanel wrapComboBox=new JPanel(new BorderLayout());
		wrapComboBox.add(this.comboBox, BorderLayout.NORTH);

		JPanel labelCombo =new JPanel(new BorderLayout());
		labelCombo.add(objectLabel, BorderLayout.NORTH);
		labelCombo.add(wrapComboBox, BorderLayout.CENTER);


		JPanel labelComboAndHelp =new JPanel(new BorderLayout());
		labelComboAndHelp.add(labelCombo, BorderLayout.WEST);
		labelComboAndHelp.add(this.objectHelp, BorderLayout.CENTER);
		labelComboAndHelp.setBorder(new EmptyBorder(0, 60, 0, 20));


		this.methodHelp = new JTextArea();
		this.methodHelp.setPreferredSize(new Dimension(300, 80));
		this.methodHelp.setWrapStyleWord(true);
		this.methodHelp.setLineWrap(true);
		this.methodHelp.setEditable(false);
		this.methodHelp.setBorder(new EmptyBorder(20, 20, 5, 5));

		JLabel commandLabel = new JLabel("Befehle:");
		JScrollPane hintScroller = this.createToolBarMethod();

		JPanel labelAndCommands=new JPanel(new BorderLayout());
		labelAndCommands.add(commandLabel, BorderLayout.NORTH);
		labelAndCommands.add(hintScroller, BorderLayout.CENTER);
		labelAndCommands.setBorder(new EmptyBorder(0,0,5,0));



		this.toolbar.add(empty2);
		this.toolbar.add(labelComboAndHelp);
		this.toolbar.add(labelAndCommands);
		this.toolbar.add(this.methodHelp);
		return this.toolbar;
	}



	private JScrollPane createToolBarMethod() {

		DefaultListModel<String> listModel = new DefaultListModel<String>();
		for (String method:HelpObject.car.getMethods()) {
			listModel.addElement("<html>"+HelpObject.car.toString()+".<font color=blue>"+method+"</font>();</html>");
		}
		this.methodHelp.setText(HelpObject.car.getMethodHelp(HelpObject.car.getMethods()[0]));

		this.methodList = new JList<String>(listModel);
		this.methodList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.methodList.setVisibleRowCount(-1);
		this.methodList.setBorder(this.hintsPadding);
		this.methodList.setSelectedIndex(0);
		this.methodList.addMouseListener(Listeners.autoFillMouseListener);
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
}