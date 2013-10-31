package org.myschool.dagucar.simulator.beginner.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
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

	public SimEditorMainWindow window;

	//singleton
	private SimContext() {
		this.userhome = System.getProperty("user.home");
		this.datahome = this.userhome+"/mein-dagucar";
		this.datahomegen = this.datahome+"/system";
		File dirs=new File(this.datahomegen);
		if (!dirs.exists()) {
			dirs.mkdirs();
		}
	}


	public String getClassname() {
		return this.classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public File getSourcefile() {
		return this.sourcefile;
	}

	public void setSourcefile(File sourcefile) {
		this.sourcefile = sourcefile;
	}

	public File getClassfile() {
		return this.classfile;
	}

	public void setClassfile(File classfile) {
		this.classfile = classfile;
	}

	public List<Diagnostic<? extends JavaFileObject>> getMessages() {
		return this.messages;
	}

	public void setMessages(List<Diagnostic<? extends JavaFileObject>> messages) {
		this.messages = messages;
	}

	public void setStarteMethodSource(String text) {
		this.starteMethodSource = text;
	}

	public String getStarteMethodSource() {
		return this.starteMethodSource;
	}

	public void setSimDaguCarActor(DefaultDaguCar car) {
		this.simactor = car;
		actorController.addActor(this.simactor);
	}

	public void addError(String string, IOException e) {
		this.errors.add(new ErrorMessage(string, e));
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
		return this.userhome;
	}

	public String getDatahome() {
		return this.datahome;
	}
	public String getDatahomegen() {
		return this.datahomegen;
	}

	public void log(String message) {
		System.out.println(message);
		if (this.window!=null) {
			this.window.appendInfoLabel(message);
		}
	}

	//seems to be error
	public void log(String message, Throwable e) {
		System.out.println(message);
		e.printStackTrace();
		if (this.window!=null) {
			this.window.appendErrorLabel(message, e);
		}
	}


	public void setWindow(SimEditorMainWindow simEditorMainWindow) {
		this.window=simEditorMainWindow;
	}

	public String getStackTrace(Throwable aThrowable) {
		Writer result = new StringWriter();
		PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	}
}
