package twister;

import lejos.hardware.motor.Motor;
import lejos.robotics.subsumption.Behavior;

public class Avancer implements Behavior {

	@Override
	public boolean takeControl() {
		return false;
	}

	@Override
	public void action() {
		Motor.B.setSpeed(100);
		Motor.C.setSpeed(100);
		Motor.B.forward();
		Motor.C.forward();
	}

	@Override
	public void suppress() {
		Motor.B.stop(true);
		Motor.C.stop(true);
	}

}
