package twister;

import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;

public class Jon {

	public static void main(String[] args) {
		Behavior jon = new joueJon();
		Behavior stop = new StopProgramme();
		Behavior[] bArray= {jon, stop};
		Arbitrator arby= new Arbitrator(bArray);
		arby.go();
	}
}
