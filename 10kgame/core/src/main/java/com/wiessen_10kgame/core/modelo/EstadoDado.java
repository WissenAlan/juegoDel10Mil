package com.wiessen_10kgame.core.modelo;

/**
 * Representa los posibles valores y reglas de puntuación básica de un dado de 6 caras.
 * Esta clase es un POJO puro, sin dependencias de librerías gráficas, lo que permite
 * ser testeada de forma aislada sin inicializar OpenGL/LibGDX.
 */
public enum EstadoDado {

    UNO(1, 100),
    DOS(2, 0),
    TRES(3, 0),
    CUATRO(4, 0),
    CINCO(5, 50),
    SEIS(6, 0);

    private final int numero;
    private final int puntoIndividual;

    EstadoDado(int numero, int puntoIndividual) {
        this.numero = numero;
        this.puntoIndividual = puntoIndividual;
    }

    public int getNumero() {
        return numero;
    }

    public int getPunto() {
        return puntoIndividual;
    }

    /**
     * Sobrecarga para mantener compatibilidad con el código original donde
     * se pasaba el índice (0..5) o valor.
     */
    public int getPunto(int dados) {
        if (dados + 1 == 1) {
            return 100;
        } else if (dados + 1 == 5) {
            return 50;
        } else {
            return 0;
        }
    }

    /**
     * Puntos otorgados por una combinación triple (3 dados iguales).
     * Tres 1 otorgan 1000 puntos, tres dados de cualquier otro número otorgan su valor * 100.
     */
    public int getTriple() {
        if (numero == 1) {
            return 1000;
        } else {
            return numero * 100;
        }
    }

    /**
     * Indica si este dado puntúa de forma individual fuera de una combinación especial.
     * En el Juego del 10 Mil, solo los 1 (100 pts) y los 5 (50 pts) son contables individualmente.
     */
    public boolean isContable() {
        return puntoIndividual > 0;
    }

    /**
     * Sobrecarga para compatibilidad con el código original.
     */
    public boolean isContable(int dados) {
        return (dados + 1 == 1 || dados + 1 == 5);
    }

    /**
     * Obtiene el enum a partir del número de la cara (1 a 6).
     */
    public static EstadoDado fromNumero(int numero) {
        for (EstadoDado estado : values()) {
            if (estado.numero == numero) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Valor de dado inválido: " + numero);
    }

    /**
     * Obtiene el enum a partir de su índice ordinal (0 a 5).
     */
    public static EstadoDado fromIndice(int indice) {
        if (indice < 0 || indice >= values().length) {
            throw new IndexOutOfBoundsException("Índice de dado fuera de rango: " + indice);
        }
        return values()[indice];
    }
}
