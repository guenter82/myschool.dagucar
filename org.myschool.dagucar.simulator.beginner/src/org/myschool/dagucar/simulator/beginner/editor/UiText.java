package org.myschool.dagucar.simulator.beginner.editor;

import java.util.Arrays;
import java.util.List;


public abstract class UiText {
	public static int i_car = 0;
	public static int i_tastatur = 1;
	public static int i_this = 2;
	public static int i_no = 3;
	public static String object_car = "auto";
	public static String object_tastatur = "tastatur";
	public static String object_this = "this";
	public static String object_no = "--Kein Objekt--";
	public static String objects[] = {object_car,  object_tastatur , object_this, object_no};

	public static String object_car_help = "Mit diesem Objekt kannst du den Zustand des Modellautos abfragen und ändern. Du siehst den aktuellen Zustand auch in der Simulation.";
	public static String object_tastatur_help = "Dieses Objekt liefert dir Methoden, um Tastatureingaben abzufragen";
	public static String object_this_help = "Mit diesem Objekt kannst du eigene Methoden aufrufen, von der Klasse die du gerade bearbeitest.";
	public static String object_no_help = "Wenn du kein Objekt verwendest, kannst du die Java Sprachbefehle aufrufen. Verwende dies für Bedingungen und Schleifen.";
	public static String objects_help[] = {object_car_help,  object_tastatur_help , object_this, object_no_help};


	public static String method_car_vor = "fahreVor";
	public static String method_car_left = "fahreHalbeLinksKurve";
	public static String method_car_right = "fahreHalbeRechtsKurve";
	public static String method_car_back = "fahreZurueck";

	public static String method_car_vor_help = "Das Modellauto fährt genau einen Schritt nach vorn.";
	public static String method_car_left_help = "Das Modellauto fährt zwei Schritt vor und einen nach links und dreht dabei 45°.";
	public static String method_car_right_help = "Das Modellauto fährt zwei Schritt vor und einen nach rechts und dreht dabei 45°.";
	public static String method_car_back_help = "Das Modellauto fährt genau einen Schritt rückwärts.";


	public static String method_tastatur_next = "naechsteGedrueckteTaste";
	public static String method_tastatur_next_help = "Liefert einen String der, die nächste gedrückte Taste angibt. Der darauf folgende Aufruf gibt, wieder die nächste gedrückte Taste aus usw. Die Strings \"links\", \"rechts\", \"oben\" und \"unten\" werden für die Richtungspfeile verwendet.";

	public static String methods_car[] = {method_car_vor,  method_car_left , method_car_right, method_car_back};
	public static String methods_tastatur[] = {method_tastatur_next};

	public static String methods_car_help[] = {method_car_vor_help,  method_car_left_help , method_car_right_help, method_car_back_help};
	public static String methods_tastatur_help[] = {method_tastatur_next_help};

	public static List<String[]> methods = Arrays.asList(methods_car, methods_tastatur, null, null);
	public static List<String[]> methods_help = Arrays.asList(methods_car_help, methods_tastatur_help, null, null);


}
