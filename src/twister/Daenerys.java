package twister;

import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;

public class Daenerys {

	public static void main(String[] args) {
		Behavior daenerys = new joueDaenerys();
		Behavior stop = new StopProgramme();
		Behavior[] bArray= {daenerys, stop};
		Arbitrator arby= new Arbitrator(bArray);
		arby.go();
	}
}
