package utilidades;

public enum Dados {

	UNO(1), DOS(2), TRES(3), CUATRO(4), CINCO(5), SEIS(6);

	private int numero;

	private Dados(int numero) {
		this.numero = numero;
	}

	public int getNumero() {
		return numero;
	}

	public int getPunto(int dados) {
		if (dados + 1 == 1) {
			return 100;
		} else if (dados + 1 == 5) {
			return 50;
		} else {
			return 0;
		}
	}

	public int getTriple() {
		if (numero == 1) {
			return 1000;
		} else {
			return numero * 100;
		}
	}

	public boolean isContable(int dados) {
		if (dados + 1 == 1 || dados + 1 == 5) {
			return true;
		}
		return false;
	}
}
