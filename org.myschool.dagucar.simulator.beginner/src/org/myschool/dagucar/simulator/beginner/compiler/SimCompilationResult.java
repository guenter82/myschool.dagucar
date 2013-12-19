package org.myschool.dagucar.simulator.beginner.compiler;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.myschool.dagucar.simulator.beginner.controller.SimContext;
import org.myschool.dagucar.simulator.beginner.editor.SimEditorMainWindow;

public class SimCompilationResult {

	private List<Diagnostic<? extends JavaFileObject>> compilerDiagnostic = new ArrayList<Diagnostic<? extends JavaFileObject>>();

	ByteArrayOutputStream out =  new ByteArrayOutputStream();

	public boolean isSuccessful() {
		return this.compilerDiagnostic.isEmpty();
	}

	public void setExecutionError(String string, ReflectiveOperationException e) {
		// TODO Auto-generated method stub

	}

	public void addAll(List<Diagnostic<? extends JavaFileObject>> compilerDiagnostic) {
		this.compilerDiagnostic.addAll(compilerDiagnostic);
	}

	public void report(SimEditorMainWindow view) {
		int prefixLines = 12;
		for (Diagnostic<? extends JavaFileObject> diagnostic:this.compilerDiagnostic) {
			StringBuilder builder=new StringBuilder("Fehler im Quelltext: \r\n");
			builder.append("Zeilennummer->" + (diagnostic.getLineNumber()-prefixLines) + " \r\n");
			//builder.append("Code->" +  diagnostic.getCode() + "\r\n");
			builder.append("Position in Zeile->" + diagnostic.getColumnNumber() + " \r\n");
			//	builder.append("End-Position->" + diagnostic.getEndPosition() + " \r\n");
			builder.append("Fehler-Art->" + diagnostic.getKind() + " \r\n");
			builder.append("Fehlermeldung->"+ diagnostic.getMessage(SimContext.local) + " \r\n");
			//	builder.append("Position->" + diagnostic.getPosition() + " \r\n");
			//builder.append("Quelle" + diagnostic.getSource() + " \r\n");
			//	builder.append("Start-Position->" + diagnostic.getStartPosition() + " \r\n");
			builder.append("\n");
			System.out.print(builder.toString());
		}
	}

	public ByteArrayOutputStream getOut() {
		return this.out;
	}

}
