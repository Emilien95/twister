package twister;

import twister.Robot;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.remote.nxt.BTConnection;
import lejos.remote.nxt.BTConnector;
import lejos.remote.nxt.NXTConnection;
import lejos.robotics.subsumption.Behavior;

public class Plateau {

	private long tempsCase; //temps pour traverser une case
	private long tempsDemieCase; //temps pour traverser une case et demie
	private Robot jon;
	private Robot daenerys;
	private String[][] map = new String[5][7]; //Plateau de twister
	private int[] seuil = new int[6]; /*seuil[0] : bleu ; seuil[1] : rouge ; seuil[2] : orange ; seuil[3] : vert ; seuil[4] : blanc ; seuil[5] : noir*/
	
	//Constructeur
	public Plateau() {
		this.jon= new Robot(0, 6, 1);
		this.daenerys = new Robot(4, 1, 1);
	}
	
	//Guetter et Setter
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
	
	public String getMap(int i, int j) {
		return map[i][j];
	}
	
	public void setMap(int i, int j, String str) {
		this.map[i][j] = str;
	} 
	
	
	/**
	  * Permet d'enregistrer une mesure de chaque couleur sur le plateau de jeu
	  * Sera utilisé pour comparer les couleurs que le robot captera
	  */
	public void seuil(){
		
		EV3ColorSensor capteurCouleur;
		capteurCouleur= new EV3ColorSensor(SensorPort.S3);
		
		//Enregistrement de chaque couleur du plateau
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
			
			//Demande à poser sur la couleur et d'appuyer sur le bouton droit
			LCD.drawString(nomCouleur,0,3);
			LCD.drawString("+ bouton droit", 0, 4);
			LCD.refresh();
			Button.RIGHT.waitForPressAndRelease();
			LCD.clear();
			
			//Enregistrement de la couleur
			float[] tab = new float[3]; 
			capteurCouleur.getRGBMode().fetchSample(tab, 0);
			float color= (tab[0]*100)+(tab[1]*1000)+(tab[2]*10000); 
			seuil[i] = (int)color;
		}
		
		capteurCouleur.close();
	}
	
	/**
	  * Trouve la couleur qui correspond à celle capté par le robot.
	  * Compare les valeur RGB captées avec celle enregistrées précedemment dans la méthode seuil().
	  * @param valeurs RGB captées par le robot
	  * @return le nom de la couleur qui correspond à celle captée par le robot
	  */
	public String compareCouleur(float[] tab) {
		
		String result;
		float color= (tab[0]*100)+(tab[1]*1000)+(tab[2]*10000); 
		int valeurAcomparer = (int)color;
		int differenceMin = valeurAcomparer - seuil[0]; //initialise la différence entre valeurs captées et seuil
		int indice = 0;
		
		//Calcul de la valeur absolue
		if(differenceMin<0) {
			differenceMin = -differenceMin;
		}
		
		//Cherche le seuil le plus proche de la couleur captée
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
		
		//Retourne le nom de couleur associée
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
	
	/**
	  * Résumé du rôle de la méthode.
	  * Commentaires détaillés sur le role de la methode
	  */
	public void parcourtCase() {

		EV3ColorSensor capteurCouleur;
		capteurCouleur= new EV3ColorSensor(SensorPort.S3);
		boolean trouveAutreCouleur = false;
		boolean trouveNoir = false;
		float[] rgbValeur = new float[3];
		Behavior avancer = new Avancer();
		Behavior reculer = new Reculer();
		Chrono timer = new Chrono();
		
		//Avance jusqu'à la ligne noire
		avancer.action();
		while(!trouveNoir) { 
			capteurCouleur.getRGBMode().fetchSample(rgbValeur, 0);
					
			if(this.compareCouleur(rgbValeur) == "noir") {
				trouveNoir = true;
			}
		}
		avancer.suppress();
		
		//Réinitialisation des variables
		trouveNoir = false;
		trouveAutreCouleur = false;
								
		//Parcourt de la case et calcul du temps
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
		tempsCase = timer.getDureeMs();
		tempsDemieCase = tempsCase/2;
		
		//Retour au point de départ
		long temps = tempsCase + tempsDemieCase;
		reculer.action();
		try{Thread.sleep(temps);} catch(InterruptedException e) {}
		reculer.suppress();
		
		capteurCouleur.close();
		
	}
	
	/**
	  * Fait avancer le robot 
	  * Déclenche le comportement Avancer et l'arrête après un certain temps
	  * @param temps à avancer
	  */
	public void avance(long temps) {
		Behavior avancer = new Avancer();
		avancer.action();
		try{Thread.sleep(temps);} catch(InterruptedException e) {}
		avancer.suppress();
	}
	
	/**
	  * Fait reculer le robot 
	  * Déclenche le comportement Reculer et l'arrête après un certain temps
	  * @param temps à reculer
	  */
	public void recule(long temps) {
		Behavior reculer = new Reculer();
		reculer.action();
		try{Thread.sleep(temps);} catch(InterruptedException e) {}
		reculer.suppress();
	}
	
	/**
	  * Fait tourner le robot
	  * Le robot avance d'une demie case et tourne à droite ou recule d'une demie case et tourne à gauche, puis se positionne au milieu de la case
	  * @param direction : false si gauche ou true si droite
	  */
	public void tourne(boolean direction) {

		EV3ColorSensor capteurCouleur;
		capteurCouleur= new EV3ColorSensor(SensorPort.S3);
		float[] tab = new float[3]; 
		boolean trouveNoir;
		long temps = tempsCase/4;
		Behavior avancer = new Avancer();
		
		if(direction) { //Tourne à droite
			
			//Avance d'une demie case
			avance(tempsDemieCase);
			//Tourne
			Motor.B.setSpeed(100);
			Motor.B.rotate(340);

			Motor.B.stop(true);
			Motor.C.stop(true);
			
		} else { //Tourne à gauche : n'a pas été testé
			
			//Recule d'une demie case
			recule(tempsDemieCase);
			//Tourne
			Motor.C.setSpeed(100);
			Motor.C.rotate(340);

			Motor.C.stop(true);
			Motor.B.stop(true);
		}
		
		//Attend et vérifie que le robot se soit bien arrêté
		try{Thread.sleep(temps);} catch(InterruptedException e) {}
		Motor.B.stop(true);
		Motor.C.stop(true);
		
		//Positionnement du robotau milieu de la case:
		//Avance un peu pour être sûr de ne pas être sur une ligne noire
		avance(temps);
		
		//Avance jusqu'à la prochaine ligne noire
		avancer.action();
		trouveNoir = false;
		while(!trouveNoir) { 
			capteurCouleur.getRGBMode().fetchSample(tab, 0);
			if(this.compareCouleur(tab) == "noir") {
				trouveNoir = true;
			}
		}
		avancer.suppress();
		
		//Recule au milieu de la case
		recule(tempsDemieCase);
		
		capteurCouleur.close();
	}
	
	/**
	  * Fait parcourir un certains nombres de cases alignées (de bas en haut ou de haut en bas) au robot, en captant la couleur de chaque case parcourues.
	  * @param i : indice de la ligne,  
	  * @param j : indice de la colonne dans laquelle se trouve le robot au départ 
	  * @param n : indice à ne pas dépasser pendant le parcourt des cases
	  * @param d : si 1 alors déplacement de haut en bas, si -1 alors déplacement de bas en haut
	  */
	public void parcourtColonne(int i, int j, int n, int d) {

		EV3ColorSensor capteurCouleur;
		capteurCouleur= new EV3ColorSensor(SensorPort.S3);
		float[] rgbValeur = new float[3]; 
		
		//Enregistrement de la couleur de la case sur laquelle est le robot
		capteurCouleur.getRGBMode().fetchSample(rgbValeur, 0);
		map[i][j] = this.compareCouleur(rgbValeur);
		
		//Affichage de la couleur
		LCD.clear();
		LCD.drawString(this.compareCouleur(rgbValeur), 0, 1);
		LCD.refresh();
		
		if(d==1) { //Vers le bas
			for(int y=j+1;y<n;y++) {
				
				//Avance d'une case
				avance(tempsCase);
				
				//Enregistrement de la couleur de la case sur laquelle est le robot
				capteurCouleur.getRGBMode().fetchSample(rgbValeur, 0);
				map[i][y] = this.compareCouleur(rgbValeur);
				
				//Affichage de la couleur
				LCD.clear();
				LCD.drawString(this.compareCouleur(rgbValeur), 0, 1);
				LCD.refresh();
			}
		} else if(d==-1) { //Vers le haut
			
			for(int y=j-1;y>n;y--) { 
				
				//Avance d'une case
				avance(tempsCase);
				
				//Enregistrement de la couleur de la case sur laquelle est le robot
				capteurCouleur.getRGBMode().fetchSample(rgbValeur, 0);
				map[i][y] = this.compareCouleur(rgbValeur);
				
				//Affichage de la couleur
				LCD.clear();
				LCD.drawString(this.compareCouleur(rgbValeur), 0, 1);
				LCD.refresh();
			}
		}
		
		capteurCouleur.close();
	}
	
	/**
	  * Fait parcourir un certains nombres de cases alignées (de droite à gauche) au robot, en captant la couleur de chaque case parcourues.
	  * @param i : indice de la ligne dans laquelle se trouve la robot au départ
	  * @param j : indice de la colonne
	  * @param n : indice à ne pas dépasser pendant le parcourt des cases
	  * @param d : si 1 alors déplacement de gauche à droite, si -1 alors déplacement de droite à gauche
	  */
	public void parcourtLigne(int i, int j, int n, int d) {

		EV3ColorSensor capteurCouleur;
		capteurCouleur= new EV3ColorSensor(SensorPort.S3);
		float[] rgbValeur = new float[3]; 
		
		//Enregistrement de la couleur de la case sur laquelle est le robot
		capteurCouleur.getRGBMode().fetchSample(rgbValeur, 0);
		map[i][j] = this.compareCouleur(rgbValeur);
		LCD.clear();
		LCD.drawString(this.compareCouleur(rgbValeur), 0, 1);
		LCD.refresh();
		
		if(d==1) { //Vers la droite
			for(int x=i+1;x<n;x++) {
				
				//Avance d'une case
				avance(tempsCase);;
				
				//Enregistrement de la couleur de la case sur laquelle est le robot
				capteurCouleur.getRGBMode().fetchSample(rgbValeur, 0);
				map[x][j] = this.compareCouleur(rgbValeur);
				
				//Affichage de la couleur
				LCD.clear();
				LCD.drawString(this.compareCouleur(rgbValeur), 0, 1);
				LCD.refresh();
			}
		} else if(d==-1) { //Vers la gauche
			
			for(int x=i-1;x>n;x--) { 
				
				//Avance
				avance(tempsCase);
				
				//Enregistrement de la couleur de la case sur laquelle est le robot
				capteurCouleur.getRGBMode().fetchSample(rgbValeur, 0);
				map[x][j] = this.compareCouleur(rgbValeur);
				
				//Affichage de la couleur
				LCD.clear();
				LCD.drawString(this.compareCouleur(rgbValeur), 0, 1);
				LCD.refresh();
			}
		}
		
		capteurCouleur.close();
	}
	
	/**
	  * Lance les différentes méthodes avec les bons paramètres pour réaliser la cartographie
	  */
	public void cartographie() {

		//Calcul du temps pour parcourir une case
		parcourtCase();
		
		//Cartographie : parcourt "en escargot"
		//Première colonne
		parcourtColonne(0, 6, -1, -1);
		tourne(true);
		//Première ligne 
		parcourtLigne(1, 0, 5, 1);
		tourne(true);
		//Cinquième colonne
		parcourtColonne(4, 1, 7, 1);
		tourne(true);
		//Septième ligne 
		parcourtLigne(3, 6, 0, -1);
		tourne(true);
		//Deuxième colonne 
		parcourtColonne(1, 5, 0, -1);
		tourne(true);
		//Deuxième ligne  
		parcourtLigne(2, 1, 4, 1);
		tourne(true);
		//Quatrième colonne 
		parcourtColonne(3, 2, 6, 1);
		tourne(true);
		//Sixième ligne 
		parcourtLigne(2, 5, 1, -1);
		tourne(true);
		//Troisième colonne
		parcourtColonne(2, 4, 1, -1);
	}
	
	/**
	  * Retourne une couleur aléatoire parmis les couleurs des cases du plateau
	  */
	public String choixCouleur() {
		String[] couleur = new String[5];
		couleur[0] = "bleu";
		couleur[1] = "rouge";
		couleur[2] = "orange";
		couleur[3] = "vert";
		couleur[4] = "blanc";
		Random rand = new Random(); 
		int indice = rand.nextInt(5); //choix aléatoire d'un indice du tableau qui contient les différentes couleurs
		return couleur[indice];
	}
	
	/**
	  * Déplace le robot sur une même ligne
	  * @param objectif : abscisse à atteindre
	  * @param asc : abscisse actuelle du robot
	  * @param direction : direction actuelle du robot (1:haut, -1:bas, 2:droite, -2:gauche)
	  * @return la direction du robot après déplacement
	  */
	public int deplaceLigne(int objectif, int abs, int direction) {
		long temps;
		int nouveauSens = direction;
		
		//Si le robot doit aller à gauche
		if(objectif < abs) {
			//Suivant le sens du robot
			switch (direction) {
				case 1 :
					//Tourne à gauche
					tourne(false);
					//Avance jusqu'à la case
					temps = (abs-objectif-1)*tempsCase;
					avance(temps);
					nouveauSens = -2;
					break;
				case -1 :
					//Tourne à droite
					tourne(true);
					//Avance jusqu'à la case
					temps = (abs-objectif-1)*tempsCase;
					avance(temps);
					nouveauSens = -2;
					break;
				case 2 :
					//Recule jusqu'à la case
					temps = (abs-objectif)*tempsCase;
					recule(temps);
					break;
				case -2:
					//Avance jusqu'à la case
					temps = (abs-objectif)*tempsCase;
					avance(temps);
					break;
				
			}
		}
		//Si le robot doit aller à droite
		else {
			switch (direction) {
			case 1 :
				//Tourne à droite
				tourne(true);
				//Avance jusqu'à la case
				temps = (abs-objectif-1)*tempsCase;
				avance(temps);
				nouveauSens = 2;
				break;
			case -1 :
				//Tourne à gauche
				tourne(false);
				//Avance jusqu'à la case
				temps = (abs-objectif-1)*tempsCase;
				avance(temps);
				nouveauSens = 2;
				break;
			case 2 :
				//Recule jusqu'à la case
				temps = (abs-objectif)*tempsCase;
				recule(temps);
				break;
			case -2:
				//Avance jusqu'à la case
				temps = (abs-objectif)*tempsCase;
				avance(temps);
				break;
			}
		}
		return nouveauSens;
	}
	
	/**
	  * Déplace le robot sur une même colonne
	  * @param objectif : ordonnée à atteindre
	  * @param ord : ordonnée actuelle du robot
	  * @param direction : direction actuelle du robot (1:haut, -1:bas, 2:droite, -2:gauche)
	  * @return la direction du robot après déplacement
	  */
	public int deplaceColonne(int objectif, int ord, int direction) {
		long temps;
		int nouveauSens = direction;
		
		//Si le robot doit monter
		if(objectif < ord) {
			//Suivant le sens du robot
			switch (direction) {
				case 1 :
					//Avance jusqu'à la case
					temps = (ord-objectif)*tempsCase;
					avance(temps);
					break;
				case -1 :
					//Recule jusqu'à la case
					temps = (ord-objectif)*tempsCase;
					recule(temps);
					break;
				case 2 :
					//Tourne à gauche
					tourne(false);
					//Avance jusqu'à la case
					temps = (ord-objectif-1)*tempsCase;
					avance(temps);
					nouveauSens = 1;
					break;
				case -2:
					//Tourne à droite
					tourne(true);
					//Avance jusqu'à la case
					temps = (ord-objectif-1)*tempsCase;
					avance(temps);
					nouveauSens = 1;
					break;
			}
		}
		//Si le robot doit descendre
		else {
			//Suivant le sens du robot
			switch (direction) {
				case 1 :
					//Recule jusqu'à la case
					temps = (objectif-ord)*tempsCase;
					recule(temps);
					break;
				case -1 :
					//Avance jusqu'à la case
					temps = (objectif-ord)*tempsCase;
					avance(temps);
					break;
				case 2 :
					//Tourne à droite
					tourne(true);
					//Avance jusqu'à la case
					temps = (objectif-ord-1)*tempsCase;
					avance(temps);
					nouveauSens = -1;
					break;
				case -2:
					//Tourne à gauche
					tourne(false);
					//Avance jusqu'à la case
					temps = (objectif-ord-1)*tempsCase;
					avance(temps);
					nouveauSens = -1;
					break;
			}
		}
		return nouveauSens;
	}
	
	/**
	  * Trouve la case la plus proche
	  * Parcourt tout le plateau, trouve les cases de la bonne couleur et enregistre les coordonnées de la plus proche
	  * @param robot : robot à déplacer
	  * @param couleur : couleur de la case à trouver
	  * @return coordonnées de la case la plus proche
	  */
	public int[] trouveCaseProche(String couleur, Robot robot) {
		int[] valeur = new int[2];
		valeur[0] = robot.getAbs(); 
		valeur[1] = robot.getOrd();
		int nombreCaseMini = 50;
		
		//Vérifie la case où se trouve le robot
		for (int i=0;i<5;i++) {
			for (int j=0; j<7;j++) {
				if(map[i][j] == couleur) { //si bonne couleur
					//Calcul de la distance
					int nombreCase = 0;
					if(i>robot.getAbs()) {
						nombreCase +=(i-robot.getAbs());
					} else {
						nombreCase +=(robot.getAbs()-i);
					}
					if(j>robot.getOrd()) {
						nombreCase +=(j-robot.getOrd());
					} else {
						nombreCase +=(robot.getOrd()-j);
					}
					if(nombreCase<nombreCaseMini) { //si c'est la case la plus proche
						nombreCaseMini = nombreCase;
						valeur[0] = i;
						valeur[1] = j;
					}
				}
			}
		}
		return valeur;
	}
	
	/**
	  * Lance le déplacement du robot pour atteindre une case de couleur donnée
	  * Trouve la case la plus proche, puis en fonction de si le robot doit changer de ligne et/ou de colonne, lance les différents déplacement
	  * @param isJon : true si le robot quie doit se déplacer et Jon, false si c'est Daenerys
	  * @param couleur : couleur de la case à atteindre
	  */
	public void deplacement (boolean isJon, String couleur) {
		
		int objectif[] = new int[2];
		int coordonnee[] = new int [2];
		int sens;
		
		if (isJon) {
			int valeur[] = trouveCaseProche(couleur, jon);
			objectif[0] = valeur[0];
			objectif[1] = valeur[1];
			coordonnee[0] = jon.getAbs();
			coordonnee[1] = jon.getOrd();
			sens = jon.getDirection();
			
		} else {
			int valeur[] = trouveCaseProche(couleur, daenerys);
			objectif[0] = valeur[0];
			objectif[1] = valeur[1];
			coordonnee[0] = daenerys.getAbs();
			coordonnee[1] = daenerys.getOrd();
			sens = daenerys.getDirection();
		}
		
		//Atteindre la case
		//Si le robot doit bouger de ligne et de colonne
		if ((objectif[1] != coordonnee[1])&&(objectif[0] != coordonnee[0])) {
			//Deplace jusqu'à atteindre la bonne colonne
			sens = deplaceColonne(objectif[1], coordonnee[1], sens);
			//Deplace jusqu'à atteindre le bonne ligne et donc la bonne case
			sens = deplaceLigne(objectif[0], coordonnee[0], sens);
		}
		//Si le robot doit seulement changer de colonne
		else if (objectif[0] == coordonnee[0]) {
			sens = deplaceColonne(objectif[1], coordonnee[1], sens);
		}
		//Si le robot doit seulement changer de ligne
		else if (objectif[1] == coordonnee[1]) {
			sens = deplaceLigne(objectif[0], coordonnee[0], sens);
		}
		
		//Mettre à jours coordonnées et sens du robot
		if (isJon) {
			jon.setAbs(objectif[0]);
			jon.setOrd(objectif[1]);
			jon.setDirection(sens);
		} else {
			daenerys.setAbs(objectif[0]);
			daenerys.setOrd(objectif[1]);
			daenerys.setDirection(sens);
		}
	}
	
	/**
	  * Envoie une phrase à un autre robot
	  * @param robot : adresse du robot à qui envoyer
	  * @param str : phrase à envoyer
	  */
	public void envoieString(String str, String robot) {
		
		try {
			
			BTConnector bt = new BTConnector();
			BTConnection btc = bt.connect(robot, NXTConnection.PACKET); 

			LCD.clear();
			LCD.drawString("Connecté", 0, 1);
			LCD.refresh();
			
			OutputStream os = btc.openOutputStream();
			DataOutputStream dos = new DataOutputStream(os);
			dos.writeUTF(str);
			dos.flush(); 
			
			LCD.clear();
			LCD.drawString("Envoyé", 0, 1);
			LCD.refresh();
			
			dos.close();
			btc.close();
			
		} catch (Exception e) {}
	}
	
	/**
	  * Receptionne une phrase
	  * @return la phrase receptionnée
	  */
	public String receptionString() {
		String valeur = "erreur";
		try {
			
			BTConnector bt = new BTConnector();
			NXTConnection btc = bt.waitForConnection(100000, NXTConnection.PACKET);

			if (btc !=null) {
				LCD.clear();
				LCD.drawString("connecté", 0, 1);
				LCD.refresh();
	
				InputStream is = btc.openInputStream();
				DataInputStream dis = new DataInputStream(is);
	
				valeur = dis.readUTF();
				
				dis.close();
				btc.close();
				
				LCD.clear();
				LCD.drawString(valeur, 0, 1);
				LCD.refresh();
				
			} else {
				LCD.clear();
				LCD.drawString("pas de connexion", 0, 1);
				LCD.refresh();
			}
		} catch (Exception e) {}
		
		return valeur;
	}
	
	/**
	  * Envoie un entier à un autre robot
	  * @param robot : adresse du robot à qui envoyer
	  * @param n : entier à envoyer
	  */
	public void envoieInt(int n, String robot) {
		
		try {
			
			BTConnector bt = new BTConnector();
			BTConnection btc = bt.connect(robot, NXTConnection.PACKET); 

			LCD.clear();
			LCD.drawString("Connecté", 0, 1);
			LCD.refresh();
			
			OutputStream os = btc.openOutputStream();
			DataOutputStream dos = new DataOutputStream(os);
			dos.write(n);
			dos.flush(); 
			
			LCD.clear();
			LCD.drawString("Envoyé", 0, 1);
			LCD.refresh();
			
			dos.close();
			btc.close();
			
		} catch (Exception e) {}
	}
	
	/**
	  * Receptionne un entier
	  * @return l'entier receptionné
	  */
	public int receptionInt() {
		int valeur = -1;
		try {
			
			BTConnector bt = new BTConnector();
			NXTConnection btc = bt.waitForConnection(100000, NXTConnection.PACKET);

			if (btc !=null) {
				LCD.clear();
				LCD.drawString("connecté", 0, 1);
				LCD.refresh();
	
				InputStream is = btc.openInputStream();
				DataInputStream dis = new DataInputStream(is);
	
				valeur = dis.read();
				
				dis.close();
				btc.close();
				
			} else {
				
				LCD.clear();
				LCD.drawString("pas de connexion", 0, 1);
				LCD.refresh();
			}
		} catch (Exception e) {}
		
		return valeur;
	}
}
