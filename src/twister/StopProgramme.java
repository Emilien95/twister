package twister;

import lejos.robotics.subsumption.Behavior;
import lejos.hardware.Button;

public class StopProgramme implements Behavior {

	@Override
	public boolean takeControl() {
		return Button.LEFT.isDown(); //Prend le contrôle si l'utilisateur appuie sur le bouton gauche du robot
	}

	@Override
	public void action() {
		System.exit(1); //Quitte le programme
	}

	@Override
	public void suppress() {}

}
