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
		assert context!=null;
		if ( context.getSourcefile() == null) {
			System.out.println("No source file provided");
			return;
		}
		
		JavaCompiler compiler= ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<JavaFileObject>();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnosticCollector, SimContext.local, SimContext.charset);
		Iterable<? extends JavaFileObject> filesToCompile=fileManager.getJavaFileObjects(context.getSourcefile());
		CompilationTask compTask = compiler.getTask(null, fileManager, diagnosticCollector, null, null, filesToCompile);
		Boolean result = compTask.call();
		
		context.setMessages(diagnosticCollector.getDiagnostics());
		
		if(result == true) {
			System.out.println("Compilation was succeeded");
			setClassFileToContext();
		} else {
			System.out.println("Compilation fails.");
			this.context.setCompilerException();
		}
	}
	
	private void setClassFileToContext() {
		String classpath = context.getSourcefile().getPath().replace(".java", ".class");
		this.context.setClassname(context.getSourcefile().getName().replace(".java", ""));
		this.context.setClassfile(new File(classpath));
		
		
	}

			
		
}
	
	

