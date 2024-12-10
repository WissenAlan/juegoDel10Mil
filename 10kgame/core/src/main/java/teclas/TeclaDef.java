package teclas;

public class TeclaDef {
	
	private int keyCode,charCode;
	private TipoTecla tipoTecla;
	private boolean caracter;
	
	public TeclaDef(int keyCode, int charCode, boolean caracter) {
		this.keyCode = keyCode;
		this.charCode = charCode;
		this.caracter = caracter;
		tipoTecla=(charCode==0)?TipoTecla.ESPECIAL:(caracter)?TipoTecla.NORMAL:TipoTecla.FUNCION;
		//si charCode == 0, tipoTecla es ESPECIAL
		//si charCode no es == 0, se chequea el boolean caracter
		//si (caracter) entonces tipoTecla es NORMAL
		//si (!caracter) entonces tipoTecla es FUNCION
	}
	
	public TeclaDef(int keyCode, int charCode) {
		this.keyCode = keyCode;
		this.charCode = charCode;
	}
	
	public int getKeyCode() {
		return keyCode;
	}
	
	public int getCharCode() {
		return charCode;
	}
	
	public boolean getCaracter() {
		return caracter;
	}
	
	public TipoTecla getTipoTecla() {
		return tipoTecla;
	}
	
	public boolean esIgual(Tecla tecla) {
		return (keyCode==tecla.getTecla().getKeyCode()&&charCode==tecla.getTecla().getCharCode())?true:false;
	}

}
