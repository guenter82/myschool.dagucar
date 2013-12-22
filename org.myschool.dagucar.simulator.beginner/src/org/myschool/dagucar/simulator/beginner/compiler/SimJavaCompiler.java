package org.myschool.dagucar.simulator.beginner.compiler;

import java.io.File;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.myschool.dagucar.simulator.beginner.controller.SimContext;

public class SimJavaCompiler implements Runnable {


	public static final SimJavaCompiler javaCompiler= new SimJavaCompiler();

	private SimContext context;

	private SimJavaCompiler() {
	}

	public void setContext(SimContext context) {
		this.context = context;
	}

	@Override
	public void run() {
		assert this.context!=null;
		if ( this.context.getSourcefile() == null) {
			System.out.println("No source file provided");
			return;
		}


		//-Dfile.encoding=UTF-8
		//-Xbootclasspath/p:./lib/jdk-lib/tools.jar
		JavaCompiler compiler= ToolProvider.getSystemJavaCompiler();
		if (compiler == null) {
			throw new IllegalStateException("No compiler found. Copy tools.jar to the folder <JRE>/lib!");
		}
		//System.setProperty("java.home",javaHome);
		DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<JavaFileObject>();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnosticCollector, SimContext.local, SimContext.charset);
		Iterable<? extends JavaFileObject> filesToCompile=fileManager.getJavaFileObjects(this.context.getSourcefile());
		CompilationTask compTask = compiler.getTask(null, fileManager, diagnosticCollector, null, null, filesToCompile);
		compTask.setLocale(SimContext.local);
		Boolean result = compTask.call();

		this.context.setMessages(diagnosticCollector.getDiagnostics());

		if(result == true) {
			System.out.println("Compilation was succeeded");
			this.setClassFileToContext();
		} else {
			System.out.println("Compilation fails.");
			this.context.setCompilerException();
		}
	}

	private void setClassFileToContext() {
		String classpath = this.context.getSourcefile().getPath().replace(".java", ".class");
		this.context.setClassname(this.context.getSourcefile().getName().replace(".java", ""));
		this.context.setClassfile(new File(classpath));


	}



}



