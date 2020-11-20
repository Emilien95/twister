package twister;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Button.RIGHT.waitForPressAndRelease();
		Plateau p = new Plateau();
		p.seuil();
		LCD.drawString("ligne noire",0,1);
		LCD.drawString("+ bouton droit", 0, 2);
		LCD.refresh();
		Button.RIGHT.waitForPressAndRelease();
		LCD.clear();
		p.parcourtCase();
	}

}
