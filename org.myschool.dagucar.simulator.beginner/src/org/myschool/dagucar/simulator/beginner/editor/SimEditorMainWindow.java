package org.myschool.dagucar.simulator.beginner.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
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

	JPanel debug=new JPanel();
	RSyntaxTextArea textArea;
	GameGrid gameGrid;
	JToolBar toolbar;

	ActionListener startAction=new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			SimEditorMainWindow.this.context.setStarteMethodSource(SimEditorMainWindow.this.textArea.getText());
			SimCompilationResult result=SimEditorMainWindow.this.controller.startAction();
			result.report();
		}
	};

	//executed by other threads
	public void appendInfoLabel(final String message) {
		final String shortMessage=SimEditorMainWindow.this.generateShortDebugMessage(message);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SimEditorMainWindow.this.appendDebugLabel(message, shortMessage, null);
			}
		});
	}

	public void appendErrorLabel(final String message, final Throwable e) {
		String stackTrace = this.context.getStackTrace(e);
		final String fullMessage = message + "\r\n" + stackTrace;
		final String shortMessage=SimEditorMainWindow.this.generateShortDebugMessage(fullMessage);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SimEditorMainWindow.this.appendDebugLabel(fullMessage, shortMessage, Color.red);
			}
		});

	}


	public void appendDebugLabel(final String message,
			final String shortMessage, final Color c) {
		JLabel label=new JLabel();
		if (c!=null) {
			label.setForeground(c);
		}
		if (shortMessage==null) {
			label.setText(message);
		} else {
			label.setText(shortMessage);
			label.setToolTipText(message);
		}
		this.debug.add(label);
		this.debug.getParent().validate();
	}

	public String generateShortDebugMessage(String message) {
		if (message==null || message.length()==0) {
			return null;
		}
		if (message.length() <= 80) {
			return null;
		}
		String firstLine=null;
		int line=message.indexOf("\r\n");
		if (line>-1) {
			firstLine=message.substring(0, Math.max(line, 30));
		} else {
			firstLine=message.substring(0, Math.max(message.length(), 30));
		}
		firstLine="<html>"+firstLine+" <b><font color=blue>...</font></b>";
		return firstLine;

	}

	public SimEditorMainWindow() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		ToolTipManager.sharedInstance().setInitialDelay(0);
		ToolTipManager.sharedInstance().setDismissDelay(60000);
		this.context.setWindow(this);
		// Activates a special Look and Feel
		this.activateLookAndFeel();
		// Content Window
		JPanel window=new JPanel(new BorderLayout());
		// Toolbar
		JToolBar toolbar = this.createToolBar();
		// Text Editor
		RTextScrollPane editorScrollPanel = this.createEditorPane();
		// Game Grid
		this.gameGrid = this.createGameGrid();
		// Split pane holding editor and game grid
		JSplitPane splitPane = this.createSplitPanel(editorScrollPanel, this.gameGrid);
		// Organize the Components (Toolbar, Split Pane ... ) in the window
		this.organizeComponentsInWindow(window, toolbar, splitPane);
		// Configure the visualization of the window
		this.configureMainWindow(window);
		// Calculates Size of components; must be called before Actors are added
		this.pack();
		// Add Numbers
		this.createColumnNumbers(this.gameGrid);
		// Expand to Fullscreen
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);

		this.populateSimulationWorld() ;
	}


	private void populateSimulationWorld() {
		String imgLocation="/image/"+this.icon_car_actor;
		URL imageURL = this.getClass().getResource(imgLocation);
		if (imageURL == null) { // image found
			throw new IllegalArgumentException("Resource not found: " + imageURL);
		}
		DefaultDaguCar car = new DefaultDaguCar(true, imageURL.getPath());

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
		int gameGridWidth = 20 * 33;
		GameGrid gameGrid = new GameGrid();
		gameGrid.setNbHorzCells(this.cells);
		gameGrid.setNbVertCells(this.cells);
		gameGrid.setCellSize(this.cellWidth);
		gameGrid.setGridColor(Color.LIGHT_GRAY);
		gameGrid.setBgColor(Color.white);
		gameGrid.getPanel().setPaintColor(Color.gray);
		Dimension dim=new Dimension(gameGridWidth,gameGridWidth);
		gameGrid.setMinimumSize(dim);
		gameGrid.setMaximumSize(dim);
		return gameGrid;
	}



	private RTextScrollPane createEditorPane() {
		this.textArea = new RSyntaxTextArea();
		RTextScrollPane editorScrollPanel = new RTextScrollPane(this.textArea);
		editorScrollPanel.setFoldIndicatorEnabled(true);
		editorScrollPanel.getViewport().setBackground(Color.white);
		this.textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
		this.textArea.setCodeFoldingEnabled(false);
		this.textArea.setAntiAliasingEnabled(true);
		this.textArea.setAutoIndentEnabled(true);
		editorScrollPanel.setMinimumSize( new Dimension(200, 0));
		return editorScrollPanel;
	}



	private JToolBar createToolBar() {
		this.toolbar = new JToolBar(JToolBar.HORIZONTAL);
		this.toolbar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.SINGLE);
		this.toolbar.setFloatable(false);
		this.toolbar.setRollover(true);
		this.toolbar.setBackground(Color.white);

		JLabel emptyLeft=new JLabel();
		emptyLeft.setText("            ");
		emptyLeft.setFocusable(false);
		emptyLeft.setBackground(this.toolbar.getBackground());

		JButton start=this.makeButton(this.icon_start, "Übersetzen und Ausführen", "Übersetzen und Ausführen", "Los ...", this.startAction);

		JButton open=this.makeButton(this.icon_open, "Öffne Datei", "Öffne Datei", "Los ...", null);


		JPanel allhints=new JPanel();
		allhints.setLayout(new BoxLayout(allhints, BoxLayout.Y_AXIS));


		JLabel hint1=new JLabel();

		hint1.setText("<html><font color=grey>auto.</font><font color=blue>fahreVor</font>();</html>");
		hint1.setToolTipText("Das DaguCar fährt einen Schritt nach vorne und stoppt.");
		hint1.setEnabled(true);
		hint1.setFocusable(true);
		hint1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						SimEditorMainWindow.this.textArea.append("auto.fahreVor();\r\n");
					}
				});
			}
		});
		JLabel hint2=new JLabel();
		hint2.setText("<html><font color=grey>auto.</font><font color=blue>fahreHalbeLinksKurve</font>();</html>");
		JLabel hint3=new JLabel();
		hint3.setText("<html><font color=grey>auto.</font><font color=blue>fahreHalbeRechtsKurve</font>();</html>");
		JLabel hint4=new JLabel();
		hint4.setText("<html><font color=grey>auto.</font><font color=blue>fahreZurueck</font>();</html>");
		JLabel hint5=new JLabel();
		hint5.setText("<html><font color=grey>auto.</font><font color=blue>fahreZurueck</font>();</html>");

		allhints.add(hint1);
		allhints.add(hint2);
		allhints.add(hint3);
		allhints.add(hint4);
		allhints.add(hint5);



		JScrollPane hintScroller = new JScrollPane(allhints, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		hintScroller.setPreferredSize(new Dimension(250, 64));

		this.debug.setLayout(new BoxLayout(this.debug, BoxLayout.Y_AXIS));
		JScrollPane debugScroller = new JScrollPane(this.debug, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		debugScroller.setPreferredSize(new Dimension(400, 64));

		JPanel help = new JPanel();
		help.setLayout(new BorderLayout());
		JSplitPane docs = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,hintScroller, debugScroller);
		docs.setDividerLocation(250);
		help.add(docs);

		this.toolbar.add(emptyLeft);
		this.toolbar.add(open);
		this.toolbar.add(start);
		this.toolbar.add(help);
		//		Border b=toolbar.getBorder();
		//		Insets i=b.getBorderInsets(toolbar);
		//		i.set(5, 20, 5, 20);
		//		toolbar.setBorderPainted(true);
		return this.toolbar;
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