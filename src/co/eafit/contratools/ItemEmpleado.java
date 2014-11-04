package co.eafit.contratools;

public class ItemEmpleado {

	private int image;
	private String nombre, cargo, documento, vinculacion, comentarios, id;

	public ItemEmpleado() {
		super();
	}

	public ItemEmpleado(int image, String nombre, String cargo,
			String documento, String vinculacion, String comentarios, String id) {
		super();
		this.image = image;
		this.nombre = nombre;
		this.cargo = cargo;
		this.documento = documento;
		this.vinculacion = vinculacion;
		this.comentarios = comentarios;
		this.id = id;
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

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public String getVinculacion() {
		return vinculacion;
	}

	public void setVinculacion(String vinculacion) {
		this.vinculacion = vinculacion;
	}

	public String getComentarios() {
		return comentarios;
	}

	public void setComentarios(String comentarios) {
		this.comentarios = comentarios;
	}

}
