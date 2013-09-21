package org.myschool.dagucar.simulator.beginner.compiler;

import java.io.File;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.myschool.dagucar.simulator.beginner.controller.SimContext;

public class SimJavaCompiler implements Runnable {
	
	
	public static final SimJavaCompiler javaCompiler= new SimJavaCompiler();
	
	enum State {
		NOT_READY,
		READY,
		RUNNING,
	}
	
	private State state;
	private SimContext context;
	
	private SimJavaCompiler() {
		this.state=State.NOT_READY;
	}
	
	public void setContext(SimContext context) {
		this.context = context;
	}
	
	@Override
	public void run() {
		JavaCompiler compiler= ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<JavaFileObject>();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnosticCollector, SimContext.local, SimContext.charset);
		Iterable<? extends JavaFileObject> filesToCompile=fileManager.getJavaFileObjects(context.getSourcefile());
		CompilationTask compTask = compiler.getTask(null, fileManager, diagnosticCollector, null, null, filesToCompile);
		Boolean result = compTask.call();
		
		for (Diagnostic<? extends JavaFileObject> d: diagnosticCollector.getDiagnostics() ) {
			report(d);
		}
		
		if(result == true) {
			System.out.println("Compilation has succeeded");
			setClassFileToContext();
		} else {
			System.out.println("Compilation fails.");
		}
	}
	
	private void setClassFileToContext() {
		String filename = context.getSourcefile().getPath().replace(".java", ".class");
		this.context.setClassfile(new File(filename));
		
	}

		private void report(Diagnostic<? extends JavaFileObject> diagnostic) {
			System.out.println("Code->" +  diagnostic.getCode());
			System.out.println("; Column Number->" + diagnostic.getColumnNumber());
			System.out.println("; End Position->" + diagnostic.getEndPosition());
			System.out.println("; Kind->" + diagnostic.getKind());
			System.out.println("; Line Number->" + diagnostic.getLineNumber());
			System.out.println("; Message->"+ diagnostic.getMessage(SimContext.local));
			System.out.println("; Position->" + diagnostic.getPosition());
			System.out.println("; Source" + diagnostic.getSource());
			System.out.println("; Start Position->" + diagnostic.getStartPosition());
			System.out.println("\n");
	}		
		
}
	
	

