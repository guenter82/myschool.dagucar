package org.myschool.dagucar.simulator.beginner.editor;

import java.awt.*;

import javax.swing.*;

import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;

import ch.aplu.jgamegrid.GameGrid;

public class SimEditorMainWindow extends JFrame {

   private static final long serialVersionUID = 1L;

   public SimEditorMainWindow() {

      JPanel cp = new JPanel(new GridBagLayout());

      RSyntaxTextArea textArea = new RSyntaxTextArea(20, 60);
      textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
      textArea.setCodeFoldingEnabled(true);
      textArea.setAntiAliasingEnabled(true);
      RTextScrollPane sp = new RTextScrollPane(textArea);
      sp.setFoldIndicatorEnabled(true);
      
      
      GridBagConstraints cTextArea = new GridBagConstraints();
      cTextArea.fill = GridBagConstraints.BOTH;
      cTextArea.weightx = 1.0;
      cTextArea.weighty = 1.0;
      cTextArea.anchor = GridBagConstraints.WEST;
      cp.add(sp, cTextArea);

      
      GameGrid gg = new GameGrid();
      gg.setCellSize(40);
      gg.setGridColor(Color.red);
      setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
     
     
      GridBagConstraints cSim = new GridBagConstraints();
      cSim.fill = GridBagConstraints.VERTICAL;
      cSim.weightx = 0.0;
      cSim.anchor = GridBagConstraints.EAST;
      cp.add(gg);
      
      setContentPane(cp);
      setTitle("Simulation Demo");
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      pack();// Must be called before actors are added!
      setLocationRelativeTo(null); //set to center

   }

}