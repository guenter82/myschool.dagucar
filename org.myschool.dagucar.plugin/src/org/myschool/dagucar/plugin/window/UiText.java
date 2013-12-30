package org.myschool.dagucar.plugin.window;

import java.util.Arrays;
import java.util.List;


public abstract class UiText {
	public static int i_car = 0;
	public static int i_tastatur = 1;
	public static int i_this = 2;
	public static int i_no = 3;
	public static String object_car = "car";
	public static String object_tastatur = "tastatur";
	public static String object_this = "this";
	public static String object_no = "--Kein Objekt--";
	public static String objects[] = {object_car,  object_tastatur , object_this, object_no};

	public static String object_car_help = "Mit diesem Objekt kannst du den Zustand des Modellautos abfragen und ändern. Du siehst den aktuellen Zustand auch in der Simulation.";
	public static String object_tastatur_help = "Dieses Objekt liefert dir Methoden, um Tastatureingaben abzufragen";
	public static String object_this_help = "Mit diesem Objekt kannst du eigene Methoden aufrufen, von der Klasse die du gerade bearbeitest.";
	public static String object_no_help = "Wenn du kein Objekt verwendest, kannst du die Java Sprachbefehle aufrufen. Verwende dies für Bedingungen und Schleifen.";
	public static String objects_help[] = {object_car_help,  object_tastatur_help , object_this, object_no_help};


	public static String method_car_constructor = "DaguCar(Nummer)";
	public static String method_car_vor = "forward()";
	public static String method_car_left = "left()";
	public static String method_car_right = "right()";
	public static String method_car_back = "back()";
	public static String method_car_check = "check()";

	public static String method_car_constructor_help = "<html>Erzeugt das Dagu-Objekt.<ul><li>Nummer ist 0: es wird keine Bluetooth-Verbindung aufgebaut.</li><li>Nummer zwischen 1,2 oder 3: es wird versucht eine Bluetooth-Verbindung zum DaguCar zu erstellen.</li></ul></html>";
	public static String method_car_vor_help = "<html><p>Das simulierte Modellauto fährt genau zwei Schritte nach vorn.</p><p>Die Seitenlänge eines Kästchen entspricht bei vollem Akku ca. 17 cm.</p></html>";
	public static String method_car_left_help = "<html>Um die Bewegung des DaguCars nachzustellen, fährt das simulierte Modellauto <ul><li>zwei Schritt vor</li><li>einen nach links</li> <li>und dreht dabei 45° nach links</li></ul></html>";
	public static String method_car_right_help = "<html>Um die Bewegung des DaguCars nachzustellen, fährt das simulierte Modellauto <ul><li>zwei Schritt vor</li><li>einen nach rechts</li> <li>und dreht dabei 45° nach rechts</li></ul></html>";
	public static String method_car_back_help = "<html><p>Das simulierte Modellauto fährt genau zwei Schritte zurück.</p><p>Die Seitenlänge eines Kästchen entspricht bei vollem Akku ca. 17 cm.</p></html>";
	public static String method_car_check_help ="<html><p>Testet ob Position und Richtung des DaguCars mit dem grau eingzeichneten Zielzustand übereinstimmen.</p></html>";

	public static String method_tastatur_next = "naechsteGedrueckteTaste";
	public static String method_tastatur_next_help = "Liefert einen String der, die nächste gedrückte Taste angibt. Der darauf folgende Aufruf gibt, wieder die nächste gedrückte Taste aus usw. Die Strings \"links\", \"rechts\", \"oben\" und \"unten\" werden für die Richtungspfeile verwendet.";

	public static String methods_car[] = {method_car_constructor, method_car_vor,  method_car_left , method_car_right, method_car_back, method_car_check};
	public static String methods_tastatur[] = {method_tastatur_next};

	public static String methods_car_help[] = {method_car_constructor_help, method_car_vor_help,  method_car_left_help , method_car_right_help, method_car_back_help, method_car_check_help};
	public static String methods_tastatur_help[] = {method_tastatur_next_help};

	public static List<String[]> methods = Arrays.asList(methods_car, methods_tastatur, null, null);
	public static List<String[]> methods_help = Arrays.asList(methods_car_help, methods_tastatur_help, null, null);


}
