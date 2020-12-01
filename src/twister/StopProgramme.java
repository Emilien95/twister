package twister;

import lejos.robotics.subsumption.Behavior;
import lejos.hardware.Button;

public class StopProgramme implements Behavior {

	@Override
	public boolean takeControl() {
		return Button.LEFT.isDown();
	}

	@Override
	public void action() {
		System.exit(1);
	}

	@Override
	public void suppress() {}

}
