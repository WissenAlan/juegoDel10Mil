package com.wiessen_10kgame.core.logica;

import com.wiessen_10kgame.core.modelo.EstadoDado;
import com.wiessen_10kgame.core.modelo.Jugador;

/**
 * Servicio de lógica pura para el cálculo de puntos y reglas del Juego del 10 Mil.
 * No contiene dependencias de gráficos ni de red, permitiendo pruebas unitarias exhaustivas.
 */
public class CalculadorPuntos10Mil {

    public static final int PUNTAJE_META_DEFAULT = 10000;
    public static final int PUNTAJE_MINIMO_PLANTARSE = 50;
    public static final int PUNTAJE_MINIMO_SALIDA = 750;

    /**
     * Calcula el resultado y puntos de una tirada de dados a partir de sus valores (1 a 6).
     *
     * @param valores Array con los valores numéricos de las caras de los dados (1 a 6)
     * @return {@link ResultadoTirada} con el desglose de puntos y dados contables
     */
    public ResultadoTirada calcularPuntos(int[] valores) {
        if (valores == null || valores.length == 0) {
            return new ResultadoTirada(0, new boolean[0], false, -1, 0, 0);
        }

        int n = valores.length;
        boolean[] contables = new boolean[n];

        // Conteo de frecuencias de cada cara (1..6)
        int[] frecuencias = new int[7];
        for (int v : valores) {
            if (v >= 1 && v <= 6) {
                frecuencias[v]++;
            }
        }

        // 1. Detectar triple (3 dados iguales)
        boolean tieneTriple = false;
        int valorTriple = -1;
        int puntosTriple = 0;

        for (int v = 1; v <= 6; v++) {
            if (frecuencias[v] >= 3) {
                tieneTriple = true;
                valorTriple = v;
                puntosTriple = EstadoDado.fromNumero(v).getTriple();
                break;
            }
        }

        // Marcar los 3 dados que forman el triple
        if (tieneTriple) {
            int marcados = 0;
            for (int i = 0; i < n; i++) {
                if (valores[i] == valorTriple && marcados < 3) {
                    contables[i] = true;
                    marcados++;
                }
            }
        }

        // 2. Sumar puntos individuales para los dados restantes no usados en el triple
        int puntosIndividuales = 0;
        for (int i = 0; i < n; i++) {
            if (!contables[i]) {
                int v = valores[i];
                if (v == 1) {
                    puntosIndividuales += 100;
                    contables[i] = true;
                } else if (v == 5) {
                    puntosIndividuales += 50;
                    contables[i] = true;
                }
            }
        }

        int puntosTotales = puntosTriple + puntosIndividuales;
        return new ResultadoTirada(puntosTotales, contables, tieneTriple, valorTriple, puntosTriple, puntosIndividuales);
    }

    /**
     * Calcula los puntos recibiendo un array de {@link EstadoDado}.
     */
    public ResultadoTirada calcularPuntos(EstadoDado[] dados) {
        if (dados == null) return new ResultadoTirada(0, new boolean[0], false, -1, 0, 0);
        int[] valores = new int[dados.length];
        for (int i = 0; i < dados.length; i++) {
            valores[i] = dados[i] != null ? dados[i].getNumero() : -1;
        }
        return calcularPuntos(valores);
    }

    /**
     * Calcula los puntos a partir de un array con índices ordinales (0 a 5),
     * formato utilizado por la implementación de red y servidor legado.
     */
    public ResultadoTirada calcularPuntosDesdeIndices(int[] indices) {
        if (indices == null) return new ResultadoTirada(0, new boolean[0], false, -1, 0, 0);
        int[] valores = new int[indices.length];
        for (int i = 0; i < indices.length; i++) {
            if (indices[i] >= 0 && indices[i] < 6) {
                valores[i] = indices[i] + 1; // 0 -> 1 (UNO), 5 -> 6 (SEIS)
            } else {
                valores[i] = -1;
            }
        }
        return calcularPuntos(valores);
    }

    /**
     * Determina si un jugador tiene derecho a plantarse y asegurar los puntos de la ronda.
     * Regla:
     * - El jugador debe acumular al menos 750 puntos para salir (abrir su puntaje).
     * - Una vez que tiene 750 puntos o más acumulados, puede plantarse con al menos 50 puntos en la ronda.
     * - La suma no debe exceder la meta (10.000).
     */
    public boolean puedePlantarse(int puntosRonda, int puntosTotales, int puntajeGanar, int puntajeMinimo) {
        if (puntosRonda <= 0) return false;
        int nuevoTotal = puntosTotales + puntosRonda;
        if (nuevoTotal > puntajeGanar) return false;
        if (nuevoTotal == puntajeGanar) return true;

        if (puntosTotales >= PUNTAJE_MINIMO_SALIDA) {
            return puntosRonda >= puntajeMinimo;
        }
        return puntosRonda >= PUNTAJE_MINIMO_SALIDA;
    }

    public boolean puedePlantarse(Jugador jugador, int puntajeGanar, int puntajeMinimo) {
        if (jugador == null) return false;
        return puedePlantarse(jugador.getPuntosRonda(), jugador.getPuntosTotales(), puntajeGanar, puntajeMinimo);
    }

    public boolean puedePlantarse(Jugador jugador) {
        return puedePlantarse(jugador, PUNTAJE_META_DEFAULT, PUNTAJE_MINIMO_PLANTARSE);
    }

    /**
     * Comprueba si una tirada es una falla (no sumó ningún punto).
     */
    public boolean esFalla(int[] valores) {
        return calcularPuntos(valores).esFalla();
    }
}
