package twister;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.robotics.subsumption.Behavior;

public class joueJon implements Behavior {

	@Override
	public boolean takeControl() {
		return true;
	}

	@Override
	public void action() {
		//Variables globales
		Plateau board = new Plateau();
		int monTour = 2;
		boolean stop = false;
				
		LCD.drawString("Appuyer sur", 0, 1);
		LCD.drawString("bouton droit", 0, 2);
		LCD.refresh();
		Button.RIGHT.waitForPressAndRelease();
				
		//Apprentissage des couleurs 
		board.seuil();
				
		//Cartographie
		LCD.clear();
		LCD.drawString("Case rouge", 0, 1);
		LCD.refresh();
		Button.RIGHT.waitForPressAndRelease();
		board.cartographie();
				
		//Envoie plateau à Daenerys
		LCD.clear();
		LCD.drawString("Appuer sur", 0, 1);
		LCD.drawString("bouton droit", 0, 2);
		LCD.drawString("pour connection", 0, 3);
		LCD.drawString("avec Daenerys", 0, 4);
		LCD.refresh();
		Button.RIGHT.waitForPressAndRelease();
				
		for(int i=0;i<4;i++) {
			for (int j=0;j<6;j++) {
				String envoi = board.getMap(i, j);
				board.envoieString(envoi, board.getDaenerys().getAdresse());
						
				LCD.clear();
				LCD.drawString("Appuyer sur", 0, 1);
				LCD.drawString("bouton droit", 0, 2);
				LCD.refresh();
				Button.RIGHT.waitForPressAndRelease();
			}
		}
				
		//Début du jeu
		while(!stop) { //boucle infinit car le robot s'arrête lorsque le comportement StopProgramme prend le dessus
			
			if(monTour % 2 != 0) {
				
				//Choix couleur et envoie
				String couleur = board.choixCouleur();
				LCD.clear();
				LCD.drawString("Appuyer sur", 0, 1);
				LCD.drawString("bouton droit", 0, 2);
				LCD.drawString("pour envoie couleur", 0, 3);
				LCD.drawString("à Daenerys", 0, 4);
				LCD.refresh();
				Button.RIGHT.waitForPressAndRelease();
						
				board.envoieString(couleur, board.getDaenerys().getAdresse());

				//Reception et mise à jours coordonnées de Daenerys
				//Abscisse
				LCD.clear();
				LCD.drawString("Appuyer sur", 0, 1);
				LCD.drawString("bouton droit", 0, 2);
				LCD.drawString("pour reception abscisse", 0, 3);
				LCD.drawString("de Daenerys", 0, 4);
				LCD.refresh();
				Button.RIGHT.waitForPressAndRelease();
				int abs = board.receptionInt();
				board.getDaenerys().setAbs(abs);
				
				//Ordonnée
				LCD.clear();
				LCD.drawString("Appuyer sur", 0, 1);
				LCD.drawString("bouton droit", 0, 2);
				LCD.drawString("pour reception ordonnée", 0, 3);
				LCD.drawString("de Daenerys", 0, 4);
				LCD.refresh();
				Button.RIGHT.waitForPressAndRelease();
				int ord = board.receptionInt();
				board.getDaenerys().setOrd(ord);
				
				//Direction 
				LCD.clear();
				LCD.drawString("Appuyer sur", 0, 1);
				LCD.drawString("bouton droit", 0, 2);
				LCD.drawString("pour reception direction", 0, 3);
				LCD.drawString("de Daenerys", 0, 4);
				LCD.refresh();
				Button.RIGHT.waitForPressAndRelease();
				int direction = board.receptionInt();
				board.getDaenerys().setDirection(direction);
				
			}  else {
				//Reception de la couleur à atteindre
				LCD.clear();
				LCD.drawString("Appuer sur", 0, 1);
				LCD.drawString("bouton droit", 0, 2);
				LCD.drawString("pour reception", 0, 3);
				LCD.drawString("couleur", 0, 4);
				LCD.refresh();
				Button.RIGHT.waitForPressAndRelease();
						
				String couleur = board.receptionString();
				
				//Déplacement
				board.deplacement(true, couleur);
				
				//Envoie des coordonnées et de la direction
				//Abscisse
				int abs = board.getJon().getAbs();
				LCD.clear();
				LCD.drawString("Appuer sur", 0, 1);
				LCD.drawString("bouton droit", 0, 2);
				LCD.drawString("pour envoie abscisse", 0, 3);
				LCD.drawString("à Daenerys", 0, 4);
				LCD.refresh();
				Button.RIGHT.waitForPressAndRelease();
				board.envoieInt(abs, board.getDaenerys().getAdresse());
				
				//Ordonnée
				int ord = board.getJon().getOrd();
				LCD.clear();
				LCD.drawString("Appuer sur", 0, 1);
				LCD.drawString("bouton droit", 0, 2);
				LCD.drawString("pour envoie ordonnée", 0, 3);
				LCD.drawString("à Daenerys", 0, 4);
				LCD.refresh();
				Button.RIGHT.waitForPressAndRelease();
				board.envoieInt(ord, board.getDaenerys().getAdresse());
				
				//Direction 
				int direction = board.getJon().getOrd();
				LCD.clear();
				LCD.drawString("Appuer sur", 0, 1);
				LCD.drawString("bouton droit", 0, 2);
				LCD.drawString("pour envoie direction", 0, 3);
				LCD.drawString("à Daenerys", 0, 4);
				LCD.refresh();
				Button.RIGHT.waitForPressAndRelease();
				board.envoieInt(direction, board.getDaenerys().getAdresse());
			}
			
			//Ré-initialisation des variables
			monTour =+ 1;
		}
	}

	@Override
	public void suppress() {}

}
