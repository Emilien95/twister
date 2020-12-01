package twister;

import twister.Robot;
import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.subsumption.Behavior;

public class Plateau {

	private long tempsCase;
	private Robot jon;
	private Robot daenerys;
	private String[][] map = new String[5][7];
	private int[] seuil = new int[6]; 
	/*seuil[0] : bleu ; seuil[1] : rouge ; seuil[2] : orange ; seuil[3] : vert ; seuil[4] : blanc ; seuil[5] : noir*/
	
	/*Constructeur*/
	public Plateau() {
		this.jon= new Robot(0, 6, 1);
		this.daenerys = new Robot(4, 1, 1);
	}
	
	/*Setter et guetter*/
	public long getTempsCase() {
		return tempsCase;
	}
	
	public void setTempsCase(int tempsCase) {
		this.tempsCase = tempsCase;
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
	
	public String[][] getMap() {
		return map;
	}
	
	public void setMap(String[][] map) {
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
			float color= tab[0]+(tab[1]*10)+(tab[2]*100); 
			seuil[i] = (int)color;
		}
		
		capteurCouleur.close();
	}
	
	public String compareCouleur(float[] tab) {
		String result;
		
		float color= tab[0]+(tab[1]*10)+(tab[2]*100); 
		int valeurAcomparer = (int)color;
		
		int differenceMin = valeurAcomparer - seuil[0];
		if(differenceMin<0) {
			differenceMin = -differenceMin;
		}
		int indice = 0;
		
		for(int i=1;i<6;i++) {
			int difference = valeurAcomparer - seuil[i];
			if(difference<0) {
				difference = -difference;
			}
			if(difference<differenceMin) {
				differenceMin = difference;
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

		EV3ColorSensor capteurCouleur;
		capteurCouleur= new EV3ColorSensor(SensorPort.S3);
		boolean trouveAutreCouleur = false;
		boolean trouveNoir = false;
		float[] rgbValeur = new float[3];
		Behavior avancer = new Avancer();
		Behavior reculer = new Reculer();
		Chrono timer = new Chrono();
		
		//Il avance jusqu'à la ligne noir
		avancer.action();
		while(!trouveNoir) { 
			capteurCouleur.getRGBMode().fetchSample(rgbValeur, 0);
					
			if(this.compareCouleur(rgbValeur) == "noir") {
				trouveNoir = true;
			}
		}
		avancer.suppress();
		trouveNoir = false;
								
		//Parcourt la case
		avancer.action();
		timer.start();
		while(!trouveNoir) { 
			capteurCouleur.getRGBMode().fetchSample(rgbValeur, 0);
			
			if(this.compareCouleur(rgbValeur) != "noir") {
				trouveAutreCouleur = true;
			}
			
			if(this.compareCouleur(rgbValeur) == "noir" && trouveAutreCouleur) {
				trouveNoir = true;
			}
		}
		avancer.suppress();
		timer.stop();
		LCD.clear();
		LCD.drawString(""+timer.getDureeMs()+"",0,1);
		LCD.refresh();
		tempsCase = timer.getDureeMs();
		
		//Il recule jusqu'à être au milieu de la case de départ
		long temps = tempsCase + (tempsCase/2);
		reculer.action();
		try{Thread.sleep(temps);} catch(InterruptedException e) {}
		reculer.suppress();
		
		capteurCouleur.close();
		
	}
	
	public void cartographie() {
		//Cartographie : parcourt "en escargot"
		//Première colonne
		parcourtColonne(0, 6, -1, -1);
		tourneDroite();
		//Première ligne 
		parcourtLigne(1, 0, 5, 1);
		tourneDroite();		
		//Cinquième colonne
		parcourtColonne(4, 1, 7, 1);
		tourneDroite();
		//Septième ligne 
		parcourtLigne(3, 6, 0, -1);
		tourneDroite();
		//Deuxième colonne 
		parcourtColonne(1, 5, 0, -1);
		tourneDroite();
		//Deuxième ligne  
		parcourtLigne(2, 1, 4, 1);
		tourneDroite();
		//Quatrième colonne 
		parcourtColonne(3, 2, 6, 1);
		tourneDroite();
		//Sixième ligne 
		parcourtLigne(2, 5, 1, -1);
		tourneDroite();
		//Troisième colonne
		parcourtColonne(2, 4, 1, -1);
	}
	
	public void tourneDroite() {

		EV3ColorSensor capteurCouleur;
		capteurCouleur= new EV3ColorSensor(SensorPort.S3);
		
		//Avance d'une demie case
		long tempsTourne = tempsCase/2;
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
		try{Thread.sleep(tempsCase/4);} catch(InterruptedException e) {}
		avancer.suppress();
		
		//Avance jusqu'à la prochaine ligne noire
		avancer.action();
		boolean trouveNoir = false;
		while(!trouveNoir) { 
			float[] tab = new float[3]; 
			capteurCouleur.getRGBMode().fetchSample(tab, 0);
			if(this.compareCouleur(tab) == "noir") {
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
	
	
	//i: indice ligne, j:indice colonne, n:indice à ne pas dépasser dans la boucle, d:direction
	public void parcourtColonne(int i, int j, int n, int d) {

		EV3ColorSensor capteurCouleur;
		capteurCouleur= new EV3ColorSensor(SensorPort.S3);
		Behavior avancer = new Avancer();
		float[] rgbValeur = new float[3]; 
		
		capteurCouleur.getRGBMode().fetchSample(rgbValeur, 0);
		map[i][j] = this.compareCouleur(rgbValeur);
		LCD.clear();
		LCD.drawString(this.compareCouleur(rgbValeur), 0, 1);
		LCD.refresh();
		
		if(d==1) { //Vers le bas
			for(int y=j+1;y<n;y++) {
				avancer.action();
				try{Thread.sleep(tempsCase);} catch(InterruptedException e) {}
				avancer.suppress();
				
				capteurCouleur.getRGBMode().fetchSample(rgbValeur, 0);
				map[i][y] = this.compareCouleur(rgbValeur);
				LCD.clear();
				LCD.drawString(this.compareCouleur(rgbValeur), 0, 1);
				LCD.refresh();
			}
		} else if(d==-1) { //Vers le haut
			
			for(int y=j-1;y>n;y--) { 
				avancer.action();
				try{Thread.sleep(tempsCase);} catch(InterruptedException e) {}
				avancer.suppress();
				
				capteurCouleur.getRGBMode().fetchSample(rgbValeur, 0);
				map[i][y] = this.compareCouleur(rgbValeur);
				LCD.clear();
				LCD.drawString(this.compareCouleur(rgbValeur), 0, 1);
				LCD.refresh();
			}
		}
		
		capteurCouleur.close();
	}
	
	
	public void parcourtLigne(int i, int j, int n, int d) {

		EV3ColorSensor capteurCouleur;
		capteurCouleur= new EV3ColorSensor(SensorPort.S3);
		Behavior avancer = new Avancer();
		float[] rgbValeur = new float[3]; 
		

		capteurCouleur.getRGBMode().fetchSample(rgbValeur, 0);
		map[i][j] = this.compareCouleur(rgbValeur);
		LCD.clear();
		LCD.drawString(this.compareCouleur(rgbValeur), 0, 1);
		LCD.refresh();
		
		if(d==1) { //Vers le bas
			for(int x=i+1;x<n;x++) {
				avancer.action();
				try{Thread.sleep(tempsCase);} catch(InterruptedException e) {}
				avancer.suppress();
				
				capteurCouleur.getRGBMode().fetchSample(rgbValeur, 0);
				map[x][j] = this.compareCouleur(rgbValeur);
				LCD.clear();
				LCD.drawString(this.compareCouleur(rgbValeur), 0, 1);
				LCD.refresh();
			}
		} else if(d==-1) { //Vers le haut
			
			for(int x=i-1;x>n;x--) { 
				avancer.action();
				try{Thread.sleep(tempsCase);} catch(InterruptedException e) {}
				avancer.suppress();
				
				capteurCouleur.getRGBMode().fetchSample(rgbValeur, 0);
				map[x][j] = this.compareCouleur(rgbValeur);
				LCD.clear();
				LCD.drawString(this.compareCouleur(rgbValeur), 0, 1);
				LCD.refresh();
			}
		}
		
		capteurCouleur.close();
	}
}
