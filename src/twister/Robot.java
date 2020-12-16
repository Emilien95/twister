package twister;

public class Robot {

	private int abs;
	private int ord;
	private int direction;  /*1:haut, -1:bas, 2:droite, -2:gauche*/
	private String adresse;
	
	//Constructeur
	public Robot (int abscisse, int ordonnee, int direction) {
		this.abs = abscisse;
		this.ord = ordonnee;
		this.direction = direction;
		this.adresse = "";
	}
	
	//Guetter et Setter
	public String getAdresse() {
		return adresse;
	}

	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getAbs() {
		return abs;
	}

	public void setAbs(int abs) {
		this.abs = abs;
	}

	public int getOrd() {
		return ord;
	}

	public void setOrd(int ord) {
		this.ord = ord;
	}
}
