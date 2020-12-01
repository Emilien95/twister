package twister;

import lejos.hardware.motor.Motor;
import lejos.robotics.subsumption.Behavior;

public class Reculer implements Behavior {

	public Reculer() {}

	@Override
	public boolean takeControl() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void action() {
		Motor.B.setSpeed(100);
		Motor.C.setSpeed(100);
		Motor.B.backward();
		Motor.C.backward();
	}

	@Override
	public void suppress() {
		Motor.B.stop(true);
		Motor.C.stop(true);
	}
}
