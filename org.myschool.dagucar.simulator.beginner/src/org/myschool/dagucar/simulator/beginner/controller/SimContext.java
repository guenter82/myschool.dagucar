package org.myschool.dagucar.simulator.beginner.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.myschool.dagucar.simulator.beginner.actor.sim.DefaultDaguCar;
import org.myschool.dagucar.simulator.beginner.actor.wrapper.DaguCarControllerWrapper;
import org.myschool.dagucar.simulator.beginner.compiler.SimJavaCompiler;
import org.myschool.dagucar.simulator.beginner.editor.SimEditorMainWindow;

//Singleton
public class SimContext {
	
	public enum State {
		READY,
		COMPILER_EXCEPTION
	}
	
	public static final String CLASS_NAME = "MeineDaguCarSteuerung";
	public static final String USER_FILE_STARTE = "StarteMethode.txt";
	public static final String USER_FILE_METHODS = "AndereMethoden.txt";
	public static final SimContext context = new SimContext();
	public static final DaguCarControllerWrapper actorController = new DaguCarControllerWrapper();
	public static final SimJavaCompiler javaCompiler=SimJavaCompiler.javaCompiler; 
	public static final Locale local = Locale.GERMANY;
	public static final Charset charset = Charset.forName("UTF-8");

	
	//set on start up
	public static SimController controller;
	public static SimEditorMainWindow view;
	
	private final String userhome;
	private final String datahome;
	private final String datahomegen;

	private String classname;
	private File sourcefile;
	private File classfile;
	private List<Diagnostic<? extends JavaFileObject>> messages;
	private List<ErrorMessage> errors;
	private String starteMethodSource;
	private DefaultDaguCar simactor;
	private State state=State.READY;

	//singleton
	private SimContext() {
		this.userhome = System.getProperty("user.home");
		this.datahome = userhome+"/mein-dagucar";
		this.datahomegen = this.datahome+"/system";
		File dirs=new File(datahomegen);
		if (!dirs.exists()) {
			dirs.mkdirs();
		}
		addClasspath();
	}

	private void addClasspath() {
		ClassLoader currentThreadClassLoader
		 = Thread.currentThread().getContextClassLoader();

		URLClassLoader urlClassLoader;
		try {
			urlClassLoader = new URLClassLoader(new URL[]{Paths.get(this.datahomegen+"/").toUri().toURL()},
			                      currentThreadClassLoader);

			Thread.currentThread().setContextClassLoader(urlClassLoader);
		} catch (MalformedURLException e) {
			throw new IllegalStateException("Could not add user directory to classpath.", e);
		}
	}
	
	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public File getSourcefile() {
		return sourcefile;
	}

	public void setSourcefile(File sourcefile) {
		this.sourcefile = sourcefile;
	}

	public File getClassfile() {
		return classfile;
	}

	public void setClassfile(File classfile) {
		this.classfile = classfile;
	}
	
	public List<Diagnostic<? extends JavaFileObject>> getMessages() {
		return messages;
	}

	public void setMessages(List<Diagnostic<? extends JavaFileObject>> messages) {
		this.messages = messages;
	}

	public void setStarteMethodSource(String text) {
		this.starteMethodSource = text;		
	}
	
	public String getStarteMethodSource() {
		return starteMethodSource;
	}

	public void setSimDaguCarActor(DefaultDaguCar car) {
		this.simactor = car;
		actorController.addActor(this.simactor);
	}

	public void addError(String string, IOException e) {
		errors.add(new ErrorMessage(string, e));
	}

	public void setCompilerException() {
		this.state = State.COMPILER_EXCEPTION;
	}
	
	public boolean isCompilerException() {
		return this.state == State.COMPILER_EXCEPTION;
	}

	public void removeCompilerException() {
		this.state = State.READY;
	}

	public String getUserhome() {
		return userhome;
	}

	public String getDatahome() {
		return datahome;
	}
	public String getDatahomegen() {
		return datahomegen;
	}
}
