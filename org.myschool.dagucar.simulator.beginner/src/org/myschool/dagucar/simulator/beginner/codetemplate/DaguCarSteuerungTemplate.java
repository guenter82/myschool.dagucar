package org.myschool.dagucar.simulator.beginner.codetemplate;

import org.myschool.dagucar.simulator.beginner.controller.SimContext;

public abstract class DaguCarSteuerungTemplate {
	private static final String ADDIMPORT_CODE = "@ADDIMPORT_CODE";
	private static final String STARTE_CODE = "@STARTE_CODE";
	private static final String METHODS_CODE = "@METHODS_CODE";

	private static final String templateImport =
			"import org.myschool.dagucar.simulator.beginner.spi.DaguCar;\r\n" +
					"import org.myschool.dagucar.simulator.beginner.spi.DaguCarSteuerung;\r\n" +
					"import org.myschool.dagucar.simulator.beginner.spi.DateiSpeicher;\r\n" +
					"import org.myschool.dagucar.simulator.beginner.spi.Handbuch;\r\n" +
					"import org.myschool.dagucar.simulator.beginner.spi.Tastatur;\r\n" +
					"import java.util.*;\r\n" +
					"import java.io.*;\r\n" +
					"import java.net.*;\r\n";

	private static final String templateClassStart =
			"public class "+SimContext.CLASS_NAME+" implements DaguCarSteuerung {";
	private static final String templateClassEnd =
			"}";
	private static final String templateStarteMethodStart =
			"\tpublic void starte(DaguCar auto, Tastatur tastatur, Handbuch handbuch, DateiSpeicher dateiSpeicher)\r\n" +
					"\t{";
	private static final String templateStarteMethodEnd =
			"\t}";

	private static final String template =
			templateImport
			+ ADDIMPORT_CODE + "\r\n"
			+ templateClassStart +"\r\n"
			+ templateStarteMethodStart
			+ "\r\n" + STARTE_CODE
			+ templateStarteMethodEnd
			+ "\r\n" + METHODS_CODE
			+ templateClassEnd +"\r\n";

	public static String createSourceCode(String addImports, String starteCode, String methodsCode) {
		return template.replace(ADDIMPORT_CODE, (addImports!= null?addImports:"")).replace(STARTE_CODE, starteCode).replace(METHODS_CODE, methodsCode);
	}

	public static String createSourceCodeShort(String addImports, String starteCode, String methodsCode) {
		return template.replace(ADDIMPORT_CODE, (addImports!= null?addImports:"")).replace(STARTE_CODE, starteCode).replace(METHODS_CODE, methodsCode);
	}

	public static String createImportsCode(String addImports) {
		return templateImport+(addImports!= null?addImports:"")+"\r\n";
	}
	public static String createStartClassCode() {
		return templateClassStart;
	}
	public static String createStarteMethodCode() {
		return templateStarteMethodStart;
	}
	public static String createStarteMethodEndsCode() {
		return templateStarteMethodEnd;
	}
	public static String createClassEndsCode() {
		return templateClassEnd;
	}
}
