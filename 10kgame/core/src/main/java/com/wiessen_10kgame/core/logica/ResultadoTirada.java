package com.wiessen_10kgame.core.logica;

import java.util.Arrays;

/**
 * Encapsula el resultado de calcular los puntos obtenidos en una tirada de dados.
 */
public class ResultadoTirada {

    private final int puntosTotales;
    private final boolean[] dadosContables;
    private final boolean tieneTriple;
    private final int valorCaraTriple;
    private final int puntosTriple;
    private final int puntosIndividuales;

    public ResultadoTirada(int puntosTotales, boolean[] dadosContables, boolean tieneTriple,
                           int valorCaraTriple, int puntosTriple, int puntosIndividuales) {
        this.puntosTotales = puntosTotales;
        this.dadosContables = dadosContables != null ? dadosContables.clone() : new boolean[0];
        this.tieneTriple = tieneTriple;
        this.valorCaraTriple = valorCaraTriple;
        this.puntosTriple = puntosTriple;
        this.puntosIndividuales = puntosIndividuales;
    }

    public int getPuntosTotales() {
        return puntosTotales;
    }

    public boolean[] getDadosContables() {
        return dadosContables.clone();
    }

    public boolean isDadoContable(int indice) {
        if (indice >= 0 && indice < dadosContables.length) {
            return dadosContables[indice];
        }
        return false;
    }

    public boolean tieneTriple() {
        return tieneTriple;
    }

    public int getValorCaraTriple() {
        return valorCaraTriple;
    }

    public int getPuntosTriple() {
        return puntosTriple;
    }

    public int getPuntosIndividuales() {
        return puntosIndividuales;
    }

    /**
     * Una tirada es falla cuando no otorga ningún punto (no hay 1s, ni 5s, ni triples).
     */
    public boolean esFalla() {
        return puntosTotales == 0;
    }

    /**
     * Devuelve true si todos los dados evaluados en la tirada anotaron puntos.
     */
    public boolean todosAnotaron() {
        if (dadosContables.length == 0) return false;
        for (boolean contable : dadosContables) {
            if (!contable) return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ResultadoTirada{" +
                "puntosTotales=" + puntosTotales +
                ", dadosContables=" + Arrays.toString(dadosContables) +
                ", tieneTriple=" + tieneTriple +
                ", valorCaraTriple=" + valorCaraTriple +
                ", puntosTriple=" + puntosTriple +
                ", puntosIndividuales=" + puntosIndividuales +
                '}';
    }
}
