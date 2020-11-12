package twister;

import twister.Plateau;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;

public class Main {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Button.RIGHT.waitForPressAndRelease();
		Plateau p = new Plateau();
		
		LCD.drawString("ligne noire",0,1);
		LCD.drawString("+ bouton droit", 0, 2);
		LCD.refresh();
		Button.RIGHT.waitForPressAndRelease();
		LCD.clear();
		p.parcourtCase();
		
	}
}
