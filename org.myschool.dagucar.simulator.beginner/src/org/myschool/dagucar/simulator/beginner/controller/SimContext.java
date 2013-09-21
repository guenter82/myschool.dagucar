package org.myschool.dagucar.simulator.beginner.controller;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Locale;

import org.myschool.dagucar.simulator.beginner.compiler.SimJavaCompiler;

public class SimContext {
	

	public File getClassname() {
		return classname;
	}

	public void setClassname(File classname) {
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

	public static final SimJavaCompiler javaCompiler=SimJavaCompiler.javaCompiler; 
	public static final Locale local = Locale.GERMANY;
	public static final Charset charset = Charset.forName("UTF-8");
	
	private File classname;
	private File sourcefile;
	private File classfile;
	
	public SimContext() {
		
	}
}
