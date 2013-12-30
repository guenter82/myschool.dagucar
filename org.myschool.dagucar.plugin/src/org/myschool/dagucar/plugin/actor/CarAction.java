package org.myschool.dagucar.plugin.actor;

import org.myschool.dagucar.plugin.window.UiText;

public enum CarAction {
	forward(true, UiText.method_car_vor),
	back(true, UiText.method_car_back),
	left(true, UiText.method_car_left),
	right(true, UiText.method_car_right),
	check(false, UiText.method_car_check);

	private String label;
	private boolean showInPopup;
	private CarAction(boolean showInPopup, String label) {
		this.label = label;
		this.showInPopup = showInPopup;
	}

	@Override
	public String toString() {
		return this.label;
	}

	public boolean isShownInPopup() {
		return this.showInPopup;
	}
}
