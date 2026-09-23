package com.wiessen_10kgame.core.modelo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EstadoDadoTest {

    @Test
    @DisplayName("Puntaje individual de cada dado sin depender de LibGDX")
    void testPuntosIndividuales() {
        assertEquals(100, EstadoDado.UNO.getPunto());
        assertEquals(0, EstadoDado.DOS.getPunto());
        assertEquals(0, EstadoDado.TRES.getPunto());
        assertEquals(0, EstadoDado.CUATRO.getPunto());
        assertEquals(50, EstadoDado.CINCO.getPunto());
        assertEquals(0, EstadoDado.SEIS.getPunto());
    }

    @Test
    @DisplayName("Puntaje de combinaciones triples")
    void testPuntosTriple() {
        assertEquals(1000, EstadoDado.UNO.getTriple());
        assertEquals(200, EstadoDado.DOS.getTriple());
        assertEquals(300, EstadoDado.TRES.getTriple());
        assertEquals(400, EstadoDado.CUATRO.getTriple());
        assertEquals(500, EstadoDado.CINCO.getTriple());
        assertEquals(600, EstadoDado.SEIS.getTriple());
    }

    @Test
    @DisplayName("Identificación de dados contables individualmente")
    void testIsContable() {
        assertTrue(EstadoDado.UNO.isContable());
        assertFalse(EstadoDado.DOS.isContable());
        assertFalse(EstadoDado.TRES.isContable());
        assertFalse(EstadoDado.CUATRO.isContable());
        assertTrue(EstadoDado.CINCO.isContable());
        assertFalse(EstadoDado.SEIS.isContable());
    }

    @Test
    @DisplayName("Conversión a partir de valor de cara (1..6)")
    void testFromNumero() {
        assertEquals(EstadoDado.UNO, EstadoDado.fromNumero(1));
        assertEquals(EstadoDado.DOS, EstadoDado.fromNumero(2));
        assertEquals(EstadoDado.TRES, EstadoDado.fromNumero(3));
        assertEquals(EstadoDado.CUATRO, EstadoDado.fromNumero(4));
        assertEquals(EstadoDado.CINCO, EstadoDado.fromNumero(5));
        assertEquals(EstadoDado.SEIS, EstadoDado.fromNumero(6));

        assertThrows(IllegalArgumentException.class, () -> EstadoDado.fromNumero(0));
        assertThrows(IllegalArgumentException.class, () -> EstadoDado.fromNumero(7));
    }

    @Test
    @DisplayName("Conversión a partir de índice ordinal (0..5)")
    void testFromIndice() {
        assertEquals(EstadoDado.UNO, EstadoDado.fromIndice(0));
        assertEquals(EstadoDado.SEIS, EstadoDado.fromIndice(5));
        assertThrows(IndexOutOfBoundsException.class, () -> EstadoDado.fromIndice(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> EstadoDado.fromIndice(6));
    }

    @Test
    @DisplayName("Compatibilidad con métodos de firma antigua")
    void testCompatibilidadAntigua() {
        assertEquals(100, EstadoDado.UNO.getPunto(0));
        assertEquals(50, EstadoDado.CINCO.getPunto(4));
        assertEquals(0, EstadoDado.DOS.getPunto(1));

        assertTrue(EstadoDado.UNO.isContable(0));
        assertTrue(EstadoDado.CINCO.isContable(4));
        assertFalse(EstadoDado.TRES.isContable(2));
    }
}
