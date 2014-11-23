package co.eafit.contratools;

public class ItemProyecto {

	private int image;
	private String nombre, inicio, provista, real, activo, lugar, cliente,
			comentarios, id;

	public ItemProyecto(int imagen, String nombre, String inicio,
			String provista, String real, String activo, String lugar,
			String cliente, String comentarios, String id) {
		super();
		this.image = imagen;
		this.nombre = nombre;
		this.inicio = inicio;
		this.provista = provista;
		this.real = real;
		this.activo = activo;
		this.lugar = lugar;
		this.cliente = cliente;
		this.comentarios = comentarios;
		this.id = id;
	}

	public int getImage() {
		return image;
	}

	public void setImage(int image) {
		this.image = image;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getInicio() {
		return inicio;
	}

	public void setInicio(String inicio) {
		this.inicio = inicio;
	}

	public String getProvista() {
		return provista;
	}

	public void setProvista(String provista) {
		this.provista = provista;
	}

	public String getReal() {
		return real;
	}

	public void setReal(String real) {
		this.real = real;
	}

	public String getActivo() {
		return activo;
	}

	public void setActivo(String activo) {
		this.activo = activo;
	}

	public String getLugar() {
		return lugar;
	}

	public void setLugar(String lugar) {
		this.lugar = lugar;
	}

	public String getCliente() {
		return cliente;
	}

	public void setCliente(String cliente) {
		this.cliente = cliente;
	}

	public String getComentarios() {
		return comentarios;
	}

	public void setComentarios(String comentarios) {
		this.comentarios = comentarios;
	}

}
