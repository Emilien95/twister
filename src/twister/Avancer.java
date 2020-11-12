package twister;

import lejos.hardware.motor.Motor;
import lejos.robotics.subsumption.Behavior;

public class Avancer implements Behavior {

	@Override
	public boolean takeControl() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void action() {
		Motor.B.forward();
		Motor.C.forward();
	}

	@Override
	public void suppress() {
		Motor.B.stop(true);
		Motor.C.stop(true);
	}

}
