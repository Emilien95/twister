package twister;

import twister.Robot;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Behavior;

public class Plateau {

	private int tempsCase;
	private int distTourne;
	private Robot jon;
	private Robot daenerys;
	private int[][] map = new int[5][7];
	private int[][] seuil = new int[6][3]; 
	/*seuil[0] : bleu ; seuil[1] : rouge ; seuil[2] : orange ; seuil[3] : vert ; seuil[4] : blanc ; seuil[5] : noir*/
	
	/*Constructeur*/
	public Plateau() {
		this.jon= new Robot(0, 6, 1);
		this.daenerys = new Robot(4, 1, 1);
	}
	
	/*Setter et guetter*/
	public int getTempsCase() {
		return tempsCase;
	}
	
	public void setTempsCase(int tempsCase) {
		this.tempsCase = tempsCase;
	}
	
	public int getDistTourne() {
		return distTourne;
	}
	
	public void setDistTourne(int distTourne) {
		this.distTourne = distTourne;
	}
	
	public Robot getJon() {
		return jon;
	}
	
	public void setJon(Robot jon) {
		this.jon = jon;
	}
	
	public Robot getDaenerys() {
		return daenerys;
	}
	
	public void setDaenerys(Robot daenerys) {
		this.daenerys = daenerys;
	}
	
	public int[][] getMap() {
		return map;
	}
	
	public void setMap(int[][] map) {
		this.map = map;
	} 
	
	/*Méthode pour récuperer les seuils des différentes couleurs du plateau*/
	public void seuil(){
		
		EV3ColorSensor capteurCouleur;
		capteurCouleur= new EV3ColorSensor(SensorPort.S3);
		
		for(int i=0;i<6;i++){
			String nomCouleur = new String();
			LCD.drawString("Mettre sur case :",0,0);
			switch (i) {
			case 0:
				nomCouleur = "bleue";
				break;
			case 1:
				nomCouleur = "rouge";
				break;
			case 2:
				nomCouleur = "orange";
				break;
			case 3:
				nomCouleur = "verte";
				break;
			case 4:
				nomCouleur = "blanche";
				break;
			case 5:
				nomCouleur = "noire";
				break;
			}
			
			LCD.drawString(nomCouleur,0,1);
			LCD.drawString("+ bouton droit", 0, 2);
			LCD.refresh();
			Button.RIGHT.waitForPressAndRelease();
			LCD.clear();
			float[] tab = new float[3]; 
			capteurCouleur.getRGBMode().fetchSample(tab, 0); 
			for (int j=0;j<3;j++) { 
				float color= tab[j]*1000; 
				seuil[i][j] = (int)color;
			} 
			LCD.drawString("Bouton droit",0,0);
			Button.RIGHT.waitForPressAndRelease();
			LCD.clear();
		}
		
		capteurCouleur.close();
	}
	
	public String compareColor(int[] tab) {
		int difMin = 100;
		int indice = -1;
		String result;
		for(int i=0;i<6;i++) {
			int dif = (seuil[i][0]+seuil[i][1]+seuil[i][2])-(tab[0]+tab[1]+tab[2]);
			if(dif<0) {
				dif = -dif;
			}
			if(dif<difMin) {
				difMin = dif;
				indice = i;
			}
		}
		switch (indice) {
		case 0 :
			result = "bleu";
			break;
		case 1 :
			result =  "rouge";
			break;
		case 2 :
			result =  "orange";
			break;
		case 3 :
			result =  "vert";
			break;
		case 4 :
			result =  "blanc";
			break;
		case 5 :
			result =  "noir";
			break;
		default :
			result =  "erreur de couleur";
			break;
		}
		return result;
	}

	public void parcourtCase() {
		Wheel wheel1 = WheeledChassis.modelWheel(Motor.B, 56.).offset(-60);
		Wheel wheel2 = WheeledChassis.modelWheel(Motor.C, 56.).offset(60);
		Chassis chassis= new WheeledChassis(new Wheel[]{wheel1, wheel2}, 2); 
		MovePilot pilot = new MovePilot(chassis);
		pilot.setLinearSpeed(30.);

		EV3ColorSensor capteurCouleur;
		capteurCouleur= new EV3ColorSensor(SensorPort.S3);
		
		boolean trouve = false;
		
		Behavior b1 = new TrouverTempsCase();
		while(!trouve) { 
			b1.action();
			LCD.clear();
			LCD.drawString(""+capteurCouleur.getColorID()+"",0,1);
			LCD.refresh();
			
			if(capteurCouleur.getColorID() == lejos.robotics.Color.BLACK) {
				trouve = true;
				LCD.clear();
				LCD.drawString("Distance="+pilot.getMovement().getDistanceTraveled(),0,0);
				LCD.refresh();
			}
		}
		b1.suppress();
		Button.RIGHT.waitForPressAndRelease();
		capteurCouleur.close();
	}
	
	public void cartographie() {
		
	}
}
