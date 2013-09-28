package org.myschool.dagucar.simulator.beginner.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;

import org.myschool.dagucar.simulator.beginner.actor.wrapper.DaguCarControllerWrapper;
import org.myschool.dagucar.simulator.beginner.codetemplate.DaguCarSteuerungTemplate;
import org.myschool.dagucar.simulator.beginner.compiler.SimCompilationResult;
import org.myschool.dagucar.simulator.beginner.compiler.SimJavaCompiler;
import org.myschool.dagucar.simulator.beginner.editor.SimEditorMainWindow;
import org.myschool.dagucar.simulator.beginner.spi.DaguCarSteuerung;

public class SimController implements Runnable{
	
	private SimContext context = SimContext.context;
	private SimJavaCompiler compiler = SimJavaCompiler.javaCompiler;
	private DaguCarControllerWrapper actorController = new DaguCarControllerWrapper();
	

	public SimController() {
		SimContext.controller = this;
	}
	
	@Override
	public void run() {
		startGUI();
	}
	
	
	/* Start all Swing applications on the EDT. */
	private void startGUI() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
					try {
						new SimEditorMainWindow().setVisible(true);
					} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
							| UnsupportedLookAndFeelException e) {
						System.out.println("Konnte die DaguCar Steuerzentrale nicht starten: " + e.getMessage());
						e.printStackTrace();
					}
				
			}
		});
	}
	
	public SimCompilationResult startAction() {
		SimCompilationResult result = null;
		try {
			provideSourceFiles();
			compileClass();
			result = generateResult();
			if (!context.isCompilerException()) {
				DaguCarSteuerung robot = loadActorRoboter();
				actorController.setActorSteuerung(robot);
				actorController.run();
			} else {
				context.removeCompilerException();
			}
		}
		catch ( ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			System.out.println("Could not load class.");
			e.printStackTrace();
			result.setExecutionError("Could not load class.", e);
		}
		catch (Throwable e) {
			System.out.println("Error while compiling and executing.");
			e.printStackTrace();
		}
		return result;
	}

	private DaguCarSteuerung loadActorRoboter() throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		@SuppressWarnings("unchecked")
		Class<? extends DaguCarSteuerung> mycarClass = (Class<? extends DaguCarSteuerung>) Class.forName(context.getClassname());
		DaguCarSteuerung robot=mycarClass.newInstance();
		return robot;
	}

	private SimCompilationResult generateResult() {
		SimCompilationResult result = new SimCompilationResult();
		result.addAll(context.getMessages());
		return result;
	}

	private void compileClass() {
		compiler.setContext(context);
		compiler.run();
	}

	private void provideSourceFiles() {
		String starteMethod = context.getStarteMethodSource();
		String code=DaguCarSteuerungTemplate.createSourceCode(starteMethod, "");
		log(code);
		File file=null;
		try {
			createSourceFile(context.getDatahome()+"/"+SimContext.USER_FILE_STARTE, starteMethod);
			file = createSourceFile(context.getDatahomegen()+"/"+SimContext.CLASS_NAME+".java", code);
		} catch (IOException e) {
			this.context.addError("Could not generate source file: " + SimContext.CLASS_NAME+".java", e);
			throw new IllegalStateException("Could not generate source file: " + SimContext.CLASS_NAME+".java", e);
		}
		context.setSourcefile(file);
	}

	private File createSourceFile(String path, String code) throws IOException {
		File file=new File(path);
		FileWriter fileWriter=new FileWriter(file, false);
		fileWriter.write(code);
		fileWriter.close();
		return file;
	}

	private void log(String code) {
		System.out.println("------------------- Quellcode: ------------ \n\r");
		System.out.println(code);
	}

	




	
	
}
