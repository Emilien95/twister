package twister;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.robotics.subsumption.Behavior;

public class Cartographie implements Behavior {
	Plateau board;
	
	public Cartographie(Plateau b) {
		board = b;
	}

	@Override
	public boolean takeControl() {
		return false;
	}

	@Override
	public void action() {
		//Apprentissage des couleurs 
		board.seuil();
		//Cartographie
		LCD.drawString("Case rouge", 0, 1);
		LCD.refresh();
		Button.RIGHT.waitForPressAndRelease();
		board.parcourtCase();
		board.cartographie();
	}

	@Override
	public void suppress() {
		Motor.B.stop(true);
		Motor.C.stop(true);
	}
}
