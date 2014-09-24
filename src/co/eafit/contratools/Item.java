package co.eafit.contratools;

public class Item {

	private int image;
	private String nombre;
	private String serial;

	public Item() {
		super();
	}

	public Item(int image, String title, String url) {
		super();
		this.image = image;
		this.nombre = title;
		this.serial = url;
	}

	public int getImage() {
		return image;
	}

	public void setImage(int image) {
		this.image = image;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String interes) {
		this.serial = interes;
	}

}
