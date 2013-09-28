package org.myschool.dagucar.simulator.beginner.compiler;

import java.util.ArrayList;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.myschool.dagucar.simulator.beginner.controller.SimContext;

public class SimCompilationResult {

	private List<Diagnostic<? extends JavaFileObject>> compilerDiagnostic = new ArrayList<Diagnostic<? extends JavaFileObject>>();
	
	public boolean isSuccessful() {
		return compilerDiagnostic.isEmpty();
	}

	public void setExecutionError(String string, ReflectiveOperationException e) {
		// TODO Auto-generated method stub
		
	}
	
	public void addAll(List<Diagnostic<? extends JavaFileObject>> compilerDiagnostic) {
		this.compilerDiagnostic.addAll(compilerDiagnostic);
	}
	
	public void report() {
		for (Diagnostic<? extends JavaFileObject> diagnostic:this.compilerDiagnostic) {
			System.out.println("Code->" +  diagnostic.getCode());
			System.out.println("Column Number->" + diagnostic.getColumnNumber());
			System.out.println("End Position->" + diagnostic.getEndPosition());
			System.out.println("Kind->" + diagnostic.getKind());
			System.out.println("Line Number->" + diagnostic.getLineNumber());
			System.out.println("Message->"+ diagnostic.getMessage(SimContext.local));
			System.out.println("Position->" + diagnostic.getPosition());
			System.out.println("Source" + diagnostic.getSource());
			System.out.println("Start Position->" + diagnostic.getStartPosition());
			System.out.println("\n");
		}
	}

}
