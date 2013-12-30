package org.myschool.dagucar.plugin.window;

import java.util.HashMap;
import java.util.Map;


public enum HelpObject {
	car(UiText.i_car),
	tastatur(UiText.i_tastatur),
	_this(UiText.i_this),
	no(UiText.i_no);

	private final String label;
	private final String help;
	private final String methods[];
	private final Map<String, String> methodsHelp = new HashMap<String, String>();


	HelpObject(int index) {
		this.label=UiText.objects[index];
		this.help=UiText.objects_help[index];
		String methodsDummy[]= UiText.methods.get(index);
		if (methodsDummy==null) {
			this.methods = new String[]{};
		} else {
			this.methods = methodsDummy;
		}
		String[] methodsHelpArr = UiText.methods_help.get(index);
		if (methodsHelpArr != null) {
			for (int i=0; i<methodsHelpArr.length; i++) {
				String method = this.methods[i];
				String methodHelp = methodsHelpArr[i];
				this.methodsHelp.put(method,methodHelp);
			}
		}
	}

	@Override
	public String toString() {
		return this.label;
	}


	public String getHelp() {
		return this.help;
	}


	public String[] getMethods() {
		return this.methods;
	}


	public String getMethodHelp(String method) {
		return this.methodsHelp.get(method);
	}

	public static HelpObject getFromString(String string) {
		for (HelpObject next:values()) {
			if (next.toString().equals(string)) {
				return next;
			}
		}
		throw new IllegalArgumentException("Der Name " + string + " entspricht keinem Objektnamen.");

	}

}
