package twister;

import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.subsumption.Behavior;

public class TrouverTempsCase implements Behavior {

	@Override
	public boolean takeControl() {
		// TODO Auto-generated method stub
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
