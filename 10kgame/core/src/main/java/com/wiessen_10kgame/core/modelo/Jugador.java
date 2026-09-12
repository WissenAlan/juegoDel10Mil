package com.wiessen_10kgame.core.modelo;

import java.util.Objects;

/**
 * Modelo POJO que representa a un participante del Juego del 10 Mil.
 * Es completamente independiente del framework gráfico (LibGDX) y de la capa de red.
 */
public class Jugador {

    private String nombre;
    private int puntosTotales;
    private int puntosRonda;
    private boolean admin;

    public Jugador(String nombre) {
        this.nombre = nombre != null ? nombre.trim() : "";
        this.puntosTotales = 0;
        this.puntosRonda = 0;
        this.admin = false;
    }

    public Jugador(String nombre, boolean admin) {
        this(nombre);
        this.admin = admin;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre != null ? nombre.trim() : "";
    }

    public int getPuntosTotales() {
        return puntosTotales;
    }

    public void setPuntosTotales(int puntosTotales) {
        this.puntosTotales = Math.max(0, puntosTotales);
    }

    public int getPuntosRonda() {
        return puntosRonda;
    }

    public void setPuntosRonda(int puntosRonda) {
        this.puntosRonda = Math.max(0, puntosRonda);
    }

    public void sumarPuntosRonda(int puntos) {
        if (puntos > 0) {
            this.puntosRonda += puntos;
        }
    }

    /**
     * Confirma los puntos acumulados en la ronda actual sumándolos al puntaje total
     * y reinicia los puntos de la ronda a cero.
     *
     * @return El nuevo puntaje total acumulado.
     */
    public int confirmarPuntosRonda() {
        this.puntosTotales += this.puntosRonda;
        this.puntosRonda = 0;
        return this.puntosTotales;
    }

    /**
     * Anula los puntos de la ronda cuando el jugador saca una tirada no válida (falla).
     */
    public void anularPuntosRonda() {
        this.puntosRonda = 0;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    /**
     * Determina si el jugador tiene suficientes puntos para plantarse (por defecto 750).
     */
    public boolean puedePlantarse(int puntajeMinimo) {
        return (puntosTotales >= puntajeMinimo || puntosRonda >= puntajeMinimo);
    }

    /**
     * Comprueba si el jugador alcanzó o superó la meta para ganar.
     */
    public boolean alcanzoMeta(int meta) {
        return puntosTotales >= meta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Jugador jugador = (Jugador) o;
        return Objects.equals(nombre, jugador.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }

    @Override
    public String toString() {
        return "Jugador{" +
                "nombre='" + nombre + '\'' +
                ", puntosTotales=" + puntosTotales +
                ", puntosRonda=" + puntosRonda +
                ", admin=" + admin +
                '}';
    }
}
