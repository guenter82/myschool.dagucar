package org.myschool.dagucar.simulator.beginner.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.myschool.dagucar.simulator.beginner.actor.sim.DefaultDaguCar;
import org.myschool.dagucar.simulator.beginner.codetemplate.DaguCarSteuerungTemplate;
import org.myschool.dagucar.simulator.beginner.compiler.SimCompilationResult;
import org.myschool.dagucar.simulator.beginner.controller.SimContext;
import org.myschool.dagucar.simulator.beginner.controller.SimController;
import org.myschool.dagucar.simulator.beginner.editor.help.HelpObject;

import ch.aplu.jgamegrid.GameGrid;
import ch.aplu.jgamegrid.Location;
import ch.aplu.jgamegrid.TextActor;

import com.jgoodies.looks.HeaderStyle;
import com.jgoodies.looks.Options;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.DesertGreen;

public class SimEditorMainWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private ImageIcon expandIcon= this.createImageIcon("/image/toggle_expand16.png", "Expand");
	private SimContext context = SimContext.context;
	private SimController controller = SimContext.controller;

	Color toolbarBackground= new Color(240,245,255);
	Color buttonBackground = this.toolbarBackground;
	Color hintsTextBackground = new Color(245,255,255);
	Color debugTextBackground = new Color(245,255,255);

	Border hintsBorder = BorderFactory.createBevelBorder(BevelBorder.LOWERED,new Color(250,253,255), new Color(160,160,190));
	Border hintsPadding = new EmptyBorder(2, 4, 2, 2);
	Border debugBorder = BorderFactory.createBevelBorder(BevelBorder.LOWERED,new Color(250,253,255), new Color(160,160,190));
	Border debugPadding = new EmptyBorder(2, 4, 2, 2);

	int cells=20;
	int cellWidth=33;
	int gameGridWidth = this.cells * this.cellWidth;
	Dimension gridDim=new Dimension(this.gameGridWidth,this.gameGridWidth);

	String icon_open = "folder_page_64px.png";
	String icon_start ="control_play_blue_64px.png";
	String icon_car_actor ="small_car_32px.png";
	String icon_debug = "infocard_32px.png";

	String starteComment="/* Hier kannst du deine Befehle für das DaguCar eingeben. */\r\n"
			+ "auto.fahreVor();\r\n"
			+ "\r\n";
	String methodsComment="/* Hier kannst du zusätzliche Methoden angeben. zB: void fahreKreise(int anzahl) { ...} */";

	JPanel debug=new JPanel();
	RSyntaxTextArea importText;
	RSyntaxTextArea mainText;
	RSyntaxTextArea methodText;
	GameGrid gameGrid;
	JToolBar toolbar;
	JList methodList;
	JComboBox<HelpObject> comboBox;
	JTextArea methodHelp;
	JTextArea objectHelp;

	ActionListener startAction=new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			SimEditorMainWindow.this.context.setStarteMethodSource(SimEditorMainWindow.this.mainText.getText());

			SimCompilationResult result=SimEditorMainWindow.this.controller.startAction();
			SimEditorMainWindow.this.appendInfoLabel(result.getOut().toString());

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream newout = new PrintStream(baos);
			PrintStream out=System.out;
			System.setOut(newout);

			result.report(SimEditorMainWindow.this);
			newout.flush();
			System.setOut(out);
			SimEditorMainWindow.this.appendErrorLabel(baos.toString(), null);
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
			label.setText("<html>"+message.replace("\r\n", "<br/>")+"</html>");
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
		if (message.length() <= 100) {
			return null;
		}
		String firstLine=null;
		int line=message.indexOf("\r\n");
		if (line>-1) {
			firstLine=message.substring(0, Math.max(line, 50));
		} else {
			firstLine=message.substring(0, Math.max(message.length(), 50));
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
		// Create visible components;
		this.organizeComponentsInWindow();
		// Expand to Fullscreen
		this.setLocation(0, 0);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
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
		DefaultDaguCar car = new DefaultDaguCar(true, null);

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
		JToolBar toolbar = this.createToolBar();
		// Text Editor
		JComponent editorScrollPanel = this.createEditorPane();
		// Game Grid
		this.gameGrid = this.createGameGrid();
		// Split pane holding editor and game grid
		JComponent debugBar = this.createDebugBar();
		JSplitPane splitPane = this.createSplitPanel(editorScrollPanel, this.gameGrid);
		window.add(toolbar, BorderLayout.NORTH);
		window.add(splitPane, BorderLayout.CENTER);
		window.add(debugBar, BorderLayout.SOUTH);

		// Configure the visualization of the window
		this.configureMainWindow(window);
	}



	private JSplitPane createSplitPanel(JComponent editorScrollPanel,
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



	private JComponent createEditorPane() {

		JPanel collapseablePretext = this.createEditorPretext();
		this.mainText = new RSyntaxTextArea();
		this.mainText.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
		this.mainText.setCodeFoldingEnabled(false);
		this.mainText.setAntiAliasingEnabled(true);
		this.mainText.setAutoIndentEnabled(true);
		this.mainText.setText(this.starteComment);
		this.mainText.setFont(this.mainText.getFont().deriveFont((float) 18.0));
		this.mainText.setBorder(new EmptyBorder(0, 25, 0, 0));

		RTextScrollPane editorScrollPanel = new RTextScrollPane(this.mainText);
		editorScrollPanel.setFoldIndicatorEnabled(true);
		editorScrollPanel.getViewport().setBackground(Color.white);
		editorScrollPanel.setMinimumSize(new Dimension(250, 18));

		// upper panel with imports and class definition
		JPanel topAndMiddle=new JPanel(new BorderLayout());
		topAndMiddle.add(collapseablePretext, BorderLayout.NORTH);
		topAndMiddle.add(editorScrollPanel, BorderLayout.CENTER);
		topAndMiddle.setBorder(null);

		JPanel endPanel = this.createdEditorPosttext();

		JPanel allEditor = new JPanel(new BorderLayout());
		allEditor.add(topAndMiddle, BorderLayout.CENTER);
		allEditor.add(endPanel, BorderLayout.SOUTH);
		allEditor.setBorder(null);
		return allEditor;

	}

	private JPanel createEditorPretext() {
		this.importText=new RSyntaxTextArea();
		this.importText.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
		this.importText.setCodeFoldingEnabled(false);
		this.importText.setEnabled(false);
		this.importText.setFocusable(false);
		this.importText.setHighlightCurrentLine(false);
		this.importText.setBackground(new Color(250, 250, 252));
		this.importText.setText(DaguCarSteuerungTemplate.createImportsCode("")
				+DaguCarSteuerungTemplate.createStartClassCode()
				+"\r\n"+DaguCarSteuerungTemplate.createStarteMethodCode());
		this.importText.setCaretPosition(0);
		this.importText.setBorder(null);
		this.importText.addMouseListener(Listeners.toggle);
		this.importText.setVisible(false);
		this.importText.setToolTipText("Klicke den Text an um die Java Klassendefinition auszublenden / einzublenden ...");




		JLabel collapsedPretext= new JLabel("Java Klassendefinition: " + DaguCarSteuerungTemplate.createStartClassCode() + " ...",
				this.expandIcon,
				JLabel.LEFT);
		collapsedPretext.setToolTipText("Klicke den Text an um die Java Klassendefinition einzublenden / auszublenden  ...");
		collapsedPretext.setPreferredSize(new Dimension(250, 20));
		collapsedPretext.addMouseListener(Listeners.toggle);


		//collapsedPretext.addMouseListener(Listeners.toggle);

		JPanel collapseablePretext=new JPanel(new BorderLayout());
		collapseablePretext.add(collapsedPretext, BorderLayout.NORTH);
		collapseablePretext.add(this.importText, BorderLayout.CENTER);
		collapseablePretext.setName(Listeners.toggleParentName);
		return collapseablePretext;
	}

	private JPanel createdEditorPosttext() {
		RSyntaxTextArea endStartMethod=new RSyntaxTextArea();
		endStartMethod.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
		endStartMethod.setCodeFoldingEnabled(false);
		endStartMethod.setEnabled(false);
		endStartMethod.setFocusable(false);
		endStartMethod.setHighlightCurrentLine(false);
		endStartMethod.setBackground(new Color(250, 250, 252));
		endStartMethod.setText(DaguCarSteuerungTemplate.createStarteMethodEndsCode());
		endStartMethod.setCaretPosition(0);
		endStartMethod.setMinimumSize(new Dimension(250, 18));
		endStartMethod.setPreferredSize(new Dimension(250, 18));
		endStartMethod.setBorder(null);
		endStartMethod.addMouseListener(Listeners.toggle);

		this.methodText=new RSyntaxTextArea();
		this.methodText.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
		this.methodText.setCodeFoldingEnabled(false);
		this.methodText.setHighlightCurrentLine(false);
		this.methodText.setText(this.methodsComment);
		this.methodText.setCaretPosition(0);
		this.methodText.setFont(this.methodText.getFont().deriveFont((float) 14.0));
		this.methodText.setBorder(null);
		this.methodText.addMouseListener(Listeners.toggle);

		RTextScrollPane methodsScrollPanel = new RTextScrollPane(this.methodText);
		methodsScrollPanel.setFoldIndicatorEnabled(true);
		methodsScrollPanel.setLineNumbersEnabled(true);
		methodsScrollPanel.setMinimumSize(new Dimension(250, 18));
		methodsScrollPanel.setPreferredSize(new Dimension(250, 18*30));
		methodsScrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		RSyntaxTextArea endClass=new RSyntaxTextArea();
		endClass.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
		endClass.setCodeFoldingEnabled(false);
		endClass.setEnabled(false);
		endClass.setFocusable(false);
		endClass.setHighlightCurrentLine(false);
		endClass.setBackground(new Color(250, 250, 252));
		endClass.setText(DaguCarSteuerungTemplate.createClassEndsCode());
		endClass.setCaretPosition(0);
		endClass.setPreferredSize(new Dimension(1000, 18));
		endClass.setBorder(null);
		endClass.addMouseListener(Listeners.toggle);

		JPanel endPanel = new JPanel();
		endPanel.setLayout(new BoxLayout(endPanel, BoxLayout.Y_AXIS));
		endPanel.add(endStartMethod);
		endPanel.add(methodsScrollPanel);
		endPanel.add(endClass);
		endPanel.setBorder(null);
		endPanel.setVisible(false);

		JLabel collapsedPosttext= new JLabel("Java Methodendefinitionen: Hier kannst du zusätzliche Methoden definieren ...",
				this.expandIcon,
				JLabel.LEFT);
		collapsedPosttext.setToolTipText("Klicke den Text an um die Java Methodendefinitionen einzublenden / auszublenden  ...");
		collapsedPosttext.setPreferredSize(new Dimension(250, 20));
		collapsedPosttext.addMouseListener(Listeners.toggle);

		JPanel toggleablePosttext=new JPanel(new BorderLayout());
		toggleablePosttext.add(collapsedPosttext, BorderLayout.NORTH);
		toggleablePosttext.add(endPanel, BorderLayout.CENTER);
		toggleablePosttext.setName(Listeners.toggleParentName);
		return toggleablePosttext;

	}



	private JToolBar createToolBar() {
		this.toolbar = new JToolBar(JToolBar.HORIZONTAL);
		this.toolbar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.SINGLE);
		this.toolbar.setFloatable(false);
		this.toolbar.setRollover(true);
		this.toolbar.setBackground(this.toolbarBackground);

		JLabel emptyLeft=new JLabel();
		emptyLeft.setText("            ");
		emptyLeft.setFocusable(false);
		emptyLeft.setBackground(this.toolbar.getBackground());

		JLabel empty2=new JLabel();
		empty2.setText("            ");
		empty2.setFocusable(false);
		empty2.setBackground(this.toolbar.getBackground());

		JButton start=this.makeButton(this.icon_start, "Übersetzen und Ausführen", "Übersetzen und Ausführen", "Los ...", this.startAction);

		JButton open=this.makeButton(this.icon_open, "Öffne Datei", "Öffne Datei", "Los ...", null);


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




		//JComponent help = this.createToolBarTextSplit(hintScroller, debugScroller);

		this.toolbar.add(emptyLeft);
		this.toolbar.add(open);
		this.toolbar.add(start);
		this.toolbar.add(empty2);
		this.toolbar.add(labelComboAndHelp);
		this.toolbar.add(labelAndCommands);
		this.toolbar.add(this.methodHelp);
		return this.toolbar;
	}


	private JComponent createDebugBar() {
		this.debug.setLayout(new BoxLayout(this.debug, BoxLayout.Y_AXIS));
		this.debug.setBackground(this.debugTextBackground);
		this.debug.setBorder(this.debugPadding);
		JScrollPane debugScroller = new JScrollPane(this.debug, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		debugScroller.setBackground(null);
		debugScroller.setBorder(this.debugBorder);

		JLabel message=new JLabel("Meldungen:");

		JPanel wrap = new JPanel(new BorderLayout());
		wrap.add(message, BorderLayout.NORTH);

		JPanel debugPanel = new JPanel(new BorderLayout());
		debugPanel.setPreferredSize(new Dimension(this.getWidth(), 88));
		debugPanel.add(wrap, BorderLayout.WEST);
		debugPanel.add(debugScroller, BorderLayout.CENTER);
		debugPanel.setBorder(new EmptyBorder(10, 10, 5, 100));


		return debugPanel;
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
		this.methodList.setBackground(this.hintsTextBackground);
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
		button.setBackground(this.buttonBackground);
		ImageIcon imageURL = this.createImageIcon(imgLocation, altText);
		if (imageURL != null) { // image found
			button.setIcon(imageURL);
		} else { // no image found
			button.setText(altText);
		}
		button.setFocusable(false);
		return button;
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