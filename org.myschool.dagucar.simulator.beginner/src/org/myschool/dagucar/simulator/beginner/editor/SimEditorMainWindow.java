package org.myschool.dagucar.simulator.beginner.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.myschool.dagucar.simulator.beginner.actor.sim.DefaultDaguCar;
import org.myschool.dagucar.simulator.beginner.compiler.SimCompilationResult;
import org.myschool.dagucar.simulator.beginner.controller.SimContext;
import org.myschool.dagucar.simulator.beginner.controller.SimController;

import ch.aplu.jgamegrid.GameGrid;
import ch.aplu.jgamegrid.Location;
import ch.aplu.jgamegrid.TextActor;

import com.jgoodies.looks.HeaderStyle;
import com.jgoodies.looks.Options;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.DesertGreen;

;

public class SimEditorMainWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private SimContext context = SimContext.context;
	private SimController controller = SimContext.controller;
	int cells=20;
	int cellWidth=33;
	String icon_open = "folder_page_64px.png";
	String icon_start ="control_play_blue_64px.png";
	String icon_car_actor ="small_car_32px.png";
	
	RSyntaxTextArea textArea;
	GameGrid gameGrid; 
	
	ActionListener startAction=new ActionListener() {			
		@Override
		public void actionPerformed(ActionEvent e) {
			context.setStarteMethodSource(textArea.getText());
			SimCompilationResult result=controller.startAction();
			result.report();
		}
	};

	public SimEditorMainWindow() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			UnsupportedLookAndFeelException {
		// Activates a special Look and Feel
		activateLookAndFeel();
		// Content Window
		JPanel window=new JPanel(new BorderLayout());
		// Toolbar
		JToolBar toolbar = createToolBar();
		// Text Editor
		RTextScrollPane editorScrollPanel = createEditorPane();
		// Game Grid
		gameGrid = createGameGrid();
		// Split pane holding editor and game grid
		JSplitPane splitPane = createSplitPanel(editorScrollPanel, gameGrid);
		// Organize the Components (Toolbar, Split Pane ... ) in the window
		organizeComponentsInWindow(window, toolbar, splitPane);
		// Configure the visualization of the window
		configureMainWindow(window); 
		// Calculates Size of components; must be called before Actors are added
		pack(); 
		// Add Numbers
		createColumnNumbers(gameGrid);
		// Expand to Fullscreen
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		populateSimulationWorld() ;
	}
	
	
	private void populateSimulationWorld() {
		String imgLocation="/image/"+icon_car_actor;
		URL imageURL = this.getClass().getResource(imgLocation);
		if (imageURL == null) { // image found
			throw new IllegalArgumentException("Resource not found: " + imageURL);
		}
		DefaultDaguCar car = new DefaultDaguCar(true, imageURL.getPath());
		
		context.setSimDaguCarActor(car);
		this.gameGrid.addActor(car, new Location(1,1));
	}


	private void activateLookAndFeel() {
		// UIManager.setLookAndFeel(new
		// com.jgoodies.looks.windows.WindowsLookAndFeel());
		PlasticLookAndFeel.setPlasticTheme(new DesertGreen());
		try {
			UIManager.setLookAndFeel(new com.jgoodies.looks.windows.WindowsLookAndFeel());
		} catch (Exception e) {
			System.out.println("Could not provide look-and-feel: " + e.getMessage());
			e.printStackTrace(System.out);
		}
	}



	private void organizeComponentsInWindow(JPanel window, JToolBar toolbar,
			JSplitPane splitPane) {
		window.add(toolbar, BorderLayout.NORTH);
		window.add(splitPane, BorderLayout.CENTER);
	}



	private JSplitPane createSplitPanel(RTextScrollPane editorScrollPanel,
			GameGrid gameGrid) {
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				editorScrollPanel, gameGrid);
		splitPane.setResizeWeight(1);
		return splitPane;
	}



	private void configureMainWindow(JPanel window) {
		this.setContentPane(window);
		this.setTitle("DaguCar - Steuerzentrale");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		// Set default Window-Location to center
		this.setLocationRelativeTo(null);
	}



	private void createColumnNumbers(GameGrid gameGrid) {
		Font font = new Font("Arial", Font.PLAIN, 13);
		Point locationOffset = new Point(-13, -5);
		Color numberColor = Color.lightGray;
		for (int i=0; i<cells; i++ ) {
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
		int gameGridWidth = 20 * 33;
		GameGrid gameGrid = new GameGrid();
		gameGrid.setNbHorzCells(cells);
		gameGrid.setNbVertCells(cells);
		gameGrid.setCellSize(cellWidth);
		gameGrid.setGridColor(Color.LIGHT_GRAY);
		gameGrid.setBgColor(Color.white);
		gameGrid.getPanel().setPaintColor(Color.gray);
		Dimension dim=new Dimension(gameGridWidth,gameGridWidth);
		gameGrid.setMinimumSize(dim);
		gameGrid.setMaximumSize(dim);
		return gameGrid;
	}



	private RTextScrollPane createEditorPane() {
		textArea = new RSyntaxTextArea();
		RTextScrollPane editorScrollPanel = new RTextScrollPane(textArea);
		editorScrollPanel.setFoldIndicatorEnabled(true);
		editorScrollPanel.getViewport().setBackground(Color.white);
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
		textArea.setCodeFoldingEnabled(false);
		textArea.setAntiAliasingEnabled(true);
		textArea.setAutoIndentEnabled(true);
		editorScrollPanel.setMinimumSize( new Dimension(200, 0));
		return editorScrollPanel;
	}



	private JToolBar createToolBar() {
		JToolBar toolbar = new JToolBar(JToolBar.HORIZONTAL);
		toolbar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.SINGLE);
		toolbar.setFloatable(false);
		toolbar.setRollover(true);
		toolbar.setBackground(Color.white);		
	
		JLabel emptyLeft=new JLabel();
		emptyLeft.setText("            ");
		emptyLeft.setFocusable(false);
		emptyLeft.setBackground(toolbar.getBackground());
		
		JButton start=makeButton(icon_start, "Übersetzen und Ausführen", "Übersetzen und Ausführen", "Los ...", startAction);
		
		JButton open=makeButton(icon_open, "Öffne Datei", "Öffne Datei", "Los ...", null);
		
		toolbar.add(emptyLeft);
		toolbar.add(open);
		toolbar.add(start);
//		Border b=toolbar.getBorder();
//		Insets i=b.getBorderInsets(toolbar);
//		i.set(5, 20, 5, 20);
//		toolbar.setBorderPainted(true);
		return toolbar;
	}

	
	private JButton makeButton(String imageName,
			String actionCommand, String toolTipText, String altText, ActionListener buttonAction) {
		// Look for the image.
		String imgLocation = "/image/"+ imageName;
	
		// Create and initialize the button.
		JButton button = new JButton();
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(buttonAction);
		button.setMargin(new Insets(10, 10, 10, 10));
		button.setBackground(Color.white);
		URL imageURL = this.getClass().getResource(imgLocation);
		if (imageURL != null) { // image found
			button.setIcon(new ImageIcon(imageURL, altText));
		} else { // no image found
			button.setText(altText);
			System.err.println("Resource not found: " + imgLocation);
		}
		button.setFocusable(false);
		return button;
	}
}