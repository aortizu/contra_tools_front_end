package co.eafit.contratools;

public class ItemHerramienta {

	private int image;
	private String nombre, serial, descripcion, comentario, id;

	public ItemHerramienta() {
		super();
	}

	public ItemHerramienta(int image, String nombre, String serial,
			String descripcion, String comentario, String id ) {
		super();
		this.image = image;
		this.nombre = nombre;
		this.serial = serial;
		this.descripcion = descripcion;
		this.comentario = comentario;
		this.id=id;
	}

	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

}
