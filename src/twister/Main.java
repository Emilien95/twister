package twister;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Button.RIGHT.waitForPressAndRelease();
		
		Behavior stop = new StopProgramme(); 
		Behavior vide = new Vide();
		/*Behavior[] bArray= {vide, stop}; // du moins prioritaire au plus prioritaire
		Arbitrator arby= new Arbitrator(bArray);
		arby.go();
		Button.RIGHT.waitForPressAndRelease();*/
		
		Plateau board = new Plateau();
		//Apprentissage des couleurs 
		board.seuil();
		//Cartographie
		LCD.drawString("Case rouge", 0, 1);
		LCD.refresh();
		Button.RIGHT.waitForPressAndRelease();
		board.parcourtCase();
		board.cartographie();
		
	}

}
