package org.myschool.dagucar.simulator.beginner.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;

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
	private DaguCarControllerWrapper actorController = SimContext.actorController;


	public SimController() {
		SimContext.controller = this;
	}

	@Override
	public void run() {
		this.startGUI();
	}


	/* Start all Swing applications on the EDT. */
	private void startGUI() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					new SimEditorMainWindow().setVisible(true);
					SimController.this.context.window.setPreferredSize(SimController.this.context.window.getSize());
					//SimController.this.context.window.setResizable(false);
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
			this.provideSourceFiles();
			this.compileClass();
			result = this.generateResult();
			if (!this.context.isCompilerException()) {
				DaguCarSteuerung robot = this.loadActorRoboter();
				this.actorController.setActorSteuerung(robot);

				PrintStream newout = new PrintStream(result.getOut());
				PrintStream out=System.out;
				System.setOut(newout);
				this.actorController.run();
				newout.flush();
				System.setOut(out);

			} else {
				this.context.removeCompilerException();
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
		URLClassLoader classloader=this.createExtendedClassloader();
		@SuppressWarnings("unchecked")
		Class<? extends DaguCarSteuerung> mycarClass = (Class<? extends DaguCarSteuerung>) classloader.loadClass(this.context.getClassname());
		DaguCarSteuerung robot=mycarClass.newInstance();
		return robot;
	}

	private URLClassLoader createExtendedClassloader() {
		try {
			URL[] urls = new URL[]{Paths.get(this.context.getDatahomegen()+"/").toUri().toURL()};
			URLClassLoader urlClassLoader = new URLClassLoader(urls,
					Thread.currentThread().getContextClassLoader());
			return urlClassLoader;
		} catch (MalformedURLException e) {
			throw new IllegalStateException("Could not add user directory to classpath.", e);
		}
	}

	private SimCompilationResult generateResult() {
		SimCompilationResult result = new SimCompilationResult();
		result.addAll(this.context.getMessages());
		return result;
	}

	private void compileClass() {
		this.compiler.setContext(this.context);
		this.compiler.run();
	}

	private void provideSourceFiles() {
		String starteMethod = this.context.getStarteMethodSource();
		String code=DaguCarSteuerungTemplate.createSourceCode(null, starteMethod, "");
		this.log(code);
		File file=null;
		try {
			this.createSourceFile(this.context.getDatahome()+"/"+SimContext.USER_FILE_STARTE, starteMethod);
			file = this.createSourceFile(this.context.getDatahomegen()+"/"+SimContext.CLASS_NAME+".java", code);
		} catch (IOException e) {
			this.context.addError("Could not generate source file: " + SimContext.CLASS_NAME+".java", e);
			throw new IllegalStateException("Could not generate source file: " + SimContext.CLASS_NAME+".java", e);
		}
		this.context.setSourcefile(file);
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
