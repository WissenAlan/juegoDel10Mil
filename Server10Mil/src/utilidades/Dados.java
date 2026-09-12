package utilidades;

/**
 * Representa los dados en el Servidor de forma pura e independiente.
 */
public enum Dados {

    UNO(1, 100),
    DOS(2, 0),
    TRES(3, 0),
    CUATRO(4, 0),
    CINCO(5, 50),
    SEIS(6, 0);

    private final int numero;
    private final int puntoIndividual;

    Dados(int numero, int puntoIndividual) {
        this.numero = numero;
        this.puntoIndividual = puntoIndividual;
    }

    // Constructor previo para compatibilidad
    Dados(int numero) {
        this(numero, (numero == 1 ? 100 : (numero == 5 ? 50 : 0)));
    }

    public int getNumero() {
        return numero;
    }

    public int getPunto() {
        return puntoIndividual;
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

    public boolean isContable() {
        return puntoIndividual > 0;
    }

    public boolean isContable(int dados) {
        return (dados + 1 == 1 || dados + 1 == 5);
    }

    public static Dados fromNumero(int numero) {
        for (Dados d : values()) {
            if (d.numero == numero) return d;
        }
        throw new IllegalArgumentException("Valor de dado inválido: " + numero);
    }
}
