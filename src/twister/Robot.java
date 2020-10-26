package twister;

public class Robot {

	private int abs;
	private int ord;
	private int direction;  /*1 pour haut, 2 pour droite, 3 pour bas, 4 pour gauche */
	private String couleurCase;
	
	public Robot (int abscisse, int ordonnee, int direction) {
		this.abs = abscisse;
		this.ord = ordonnee;
		this.direction = direction;
		this.couleurCase = "rouge";
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
