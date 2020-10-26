package twister;

import twister.Plateau;
import lejos.hardware.Button;

public class Main {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Button.RIGHT.waitForPressAndRelease();
		Plateau p = new Plateau();
		p.seuil();
	}
}
