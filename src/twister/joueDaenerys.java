package twister;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.robotics.subsumption.Behavior;

public class joueDaenerys implements Behavior {

	@Override
	public boolean takeControl() {
		return true;
	}

	@Override
	public void action() {
		//Variables globales
		Plateau board = new Plateau();
		int monTour = 1;
		boolean stop = false;
						
		//Reception des couleurs plateau
		LCD.clear();
		LCD.drawString("Appuer sur", 0, 1);
		LCD.drawString("bouton droit", 0, 2);
		LCD.drawString("pour connection", 0, 3);
		LCD.drawString("avec Jon", 0, 4);
		LCD.refresh();
		Button.RIGHT.waitForPressAndRelease();
				
		for(int i=0;i<4;i++) {
			for (int j=0;j<6;j++) {
					
				String couleur = board.receptionString();
				board.setMap(i, j, couleur);
						
				LCD.clear();
				LCD.drawString("Appuer sur", 0, 1);
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
				LCD.drawString("Appuer sur", 0, 1);
				LCD.drawString("bouton droit", 0, 2);
				LCD.drawString("pour envoie", 0, 3);
				LCD.drawString("couleur à Jon", 0, 4);
				LCD.refresh();
				Button.RIGHT.waitForPressAndRelease();
						
				board.envoieString(couleur, board.getJon().getAdresse());
				
				//Receptions des coordonnées et de la direction de Jon
				//Abscisse
				LCD.clear();
				LCD.drawString("Appuyer sur", 0, 1);
				LCD.drawString("bouton droit", 0, 2);
				LCD.drawString("pour reception abscisse", 0, 3);
				LCD.drawString("de Jon", 0, 4);
				LCD.refresh();
				Button.RIGHT.waitForPressAndRelease();
				int abs = board.receptionInt();
				board.getJon().setAbs(abs);
				
				//Ordonnée
				LCD.clear();
				LCD.drawString("Appuyer sur", 0, 1);
				LCD.drawString("bouton droit", 0, 2);
				LCD.drawString("pour reception ordonnée", 0, 3);
				LCD.drawString("de Jon", 0, 4);
				LCD.refresh();
				Button.RIGHT.waitForPressAndRelease();
				int ord = board.receptionInt();
				board.getJon().setOrd(ord);
				
				//Direction 
				LCD.clear();
				LCD.drawString("Appuyer sur", 0, 1);
				LCD.drawString("bouton droit", 0, 2);
				LCD.drawString("pour reception direction", 0, 3);
				LCD.drawString("de Jon", 0, 4);
				LCD.refresh();
				Button.RIGHT.waitForPressAndRelease();
				int direction = board.receptionInt();
				board.getJon().setDirection(direction);
				
			}  else {
				
				//Reception de la couleur à atteindre
				LCD.clear();
				LCD.drawString("Appuyer sur", 0, 1);
				LCD.drawString("bouton droit", 0, 2);
				LCD.drawString("pour reception", 0, 3);
				LCD.drawString("couleur", 0, 4);
				LCD.refresh();
				Button.RIGHT.waitForPressAndRelease();
						
				String couleur = board.receptionString();
				
				//Déplacement
				board.deplacement(false, couleur);
				
				//Envoie des coordonnées et de la direction
				//Abscisse
				int abs = board.getDaenerys().getAbs();
				LCD.clear();
				LCD.drawString("Appuyer sur", 0, 1);
				LCD.drawString("bouton droit", 0, 2);
				LCD.drawString("pour envoie abscisse", 0, 3);
				LCD.drawString("à Jon", 0, 4);
				LCD.refresh();
				Button.RIGHT.waitForPressAndRelease();
				board.envoieInt(abs, board.getJon().getAdresse());
				
				//Ordonnée
				int ord = board.getDaenerys().getOrd();
				LCD.clear();
				LCD.drawString("Appuyer sur", 0, 1);
				LCD.drawString("bouton droit", 0, 2);
				LCD.drawString("pour envoie ordonnée", 0, 3);
				LCD.drawString("à Jon", 0, 4);
				LCD.refresh();
				Button.RIGHT.waitForPressAndRelease();
				board.envoieInt(ord, board.getJon().getAdresse());
				
				//Direction 
				int direction = board.getDaenerys().getOrd();
				LCD.clear();
				LCD.drawString("Appuer sur", 0, 1);
				LCD.drawString("bouton droit", 0, 2);
				LCD.drawString("pour envoie direction", 0, 3);
				LCD.drawString("à Jon", 0, 4);
				LCD.refresh();
				Button.RIGHT.waitForPressAndRelease();
				board.envoieInt(direction, board.getJon().getAdresse());
			}
					
			//Ré-initialisation des variables
			monTour += 1;
		}
	}

	@Override
	public void suppress() {}
}
