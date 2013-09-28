package org.myschool.dagucar.simulator.beginner.codetemplate;

import org.myschool.dagucar.simulator.beginner.controller.SimContext;

public abstract class DaguCarSteuerungTemplate {
	private static final String STARTE_CODE = "@STARTE_CODE";
	private static final String METHODS_CODE = "@METHODS_CODE";

	private static final String template = 
"import org.myschool.dagucar.simulator.beginner.spi.DaguCar;\r\n" + 
"import org.myschool.dagucar.simulator.beginner.spi.DaguCarSteuerung;\r\n" + 
"import org.myschool.dagucar.simulator.beginner.spi.DateiSpeicher;\r\n" + 
"import org.myschool.dagucar.simulator.beginner.spi.Handbuch;\r\n" + 
"import org.myschool.dagucar.simulator.beginner.spi.Tastatur;\r\n" + 
"\r\n" + 	
"\r\n" + 
"public class "+SimContext.CLASS_NAME+" implements DaguCarSteuerung {\r\n" + 
"\tpublic void starte(DaguCar auto, Tastatur tastatur, Handbuch handbuch, DateiSpeicher dateiSpeicher)\r\n" +  
"\t{\r\n" + 
"@STARTE_CODE" +
"\t}\r\n"+
"@METHODS_CODE" +
"}\r\n";
	
	public static String createSourceCode(String starteCode, String methodsCode) {
		return template.replace(STARTE_CODE, starteCode).replace(METHODS_CODE, methodsCode);
	}
}
