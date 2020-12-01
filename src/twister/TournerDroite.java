package twister;

import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.subsumption.Behavior;

public class TournerDroite implements Behavior {
	Plateau board;
	
	public TournerDroite(Plateau b) {
		board = b;
	}

	@Override
	public boolean takeControl() {
		return false;
	}

	@Override
	public void action() {
		EV3ColorSensor capteurCouleur;
		capteurCouleur= new EV3ColorSensor(SensorPort.S3);
		
		//Avance d'une demie case
		long tempsTourne = board.getTempsCase()/2;
		Behavior avancer = new Avancer();
		avancer.action();
		try{Thread.sleep(tempsTourne);} catch(InterruptedException e) {}
		avancer.suppress();
		
		//Tourne à droite
		Motor.B.setSpeed(100);
		Motor.B.rotate(386);
		
		//Avance un peu pour être sûr de ne pas être sur une ligne noire
		//A voir si necessaire 
		avancer.action();
		try{Thread.sleep(board.getTempsCase()/4);} catch(InterruptedException e) {}
		avancer.suppress();
		
		//Avance jusqu'à la prochaine ligne noire
		avancer.action();
		boolean trouveNoir = false;
		while(!trouveNoir) { 
			float[] tab = new float[3]; 
			capteurCouleur.getRGBMode().fetchSample(tab, 0);
			if(board.compareCouleur(tab) == "noir") {
				trouveNoir = true;
			}
		}
		avancer.suppress();
		
		//Recule au milieu de la case
		Behavior reculer = new Reculer();
		reculer.action();
		try{Thread.sleep(tempsTourne);} catch(InterruptedException e) {}
		reculer.suppress();
		
		capteurCouleur.close();
	}

	@Override
	public void suppress() {}

}
