package com.wiessen_10kgame.ui.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.wiessen_10kgame.core.Recursos;
import com.wiessen_10kgame.core.controlador.ControladorJuego;
import com.wiessen_10kgame.core.modelo.Jugador;
import com.wiessen_10kgame.red.HiloCliente;
import com.wiessen_10kgame.ui.componentes.Boton;
import com.wiessen_10kgame.ui.componentes.Entrada;
import com.wiessen_10kgame.ui.componentes.Texto;
import com.wiessen_10kgame.utilidades.ScreenManager;
import com.wiessen_10kgame.utilidades.Sonidos;

import java.util.ArrayList;
import java.util.List;

/**
 * Pantalla que muestra el ganador de la partida y la tabla final de puntajes.
 * Desacoplada del modelo y sin variables estáticas.
 */
public class PantallaGanador implements Screen {

    private final ControladorJuego controlador;
    private final HiloCliente hiloCliente;

    private SpriteBatch batch;
    private Texto tituloGanadorTxt;
    private final List<Texto> puntajesFinalesTxt = new ArrayList<>();
    private Boton salirBtn;
    private Entrada entrada;
    private boolean clicSalir = false;

    public PantallaGanador(ControladorJuego controlador, HiloCliente hiloCliente) {
        this.controlador = controlador;
        this.hiloCliente = hiloCliente;
    }

    public PantallaGanador(ControladorJuego controlador) {
        this(controlador, null);
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        entrada = ScreenManager.getInstance().getEntrada();
        if (entrada != null) {
            Gdx.input.setInputProcessor(entrada);
        }

        String nombreGanador = "Desconocido";
        if (controlador != null) {
            Jugador g = controlador.getPartida().getGanador();
            if (g == null) {
                g = controlador.getPartida().getJugadorActual();
            }
            if (g != null) {
                nombreGanador = g.getNombre();
            }
        }

        tituloGanadorTxt = new Texto(Recursos.FUENTE_MENU, 34, Color.WHITE, "¡Ganador: " + nombreGanador + "!", Recursos.COLOR_LETRA);
        tituloGanadorTxt.setPosicion((float) Gdx.graphics.getWidth() / 2 - tituloGanadorTxt.getWidth() / 2, Gdx.graphics.getHeight() - 80);

        puntajesFinalesTxt.clear();
        if (controlador != null) {
            List<Jugador> jugadores = controlador.getPartida().getJugadores();
            for (int i = 0; i < jugadores.size(); i++) {
                Jugador j = jugadores.get(i);
                Texto txt = new Texto(Recursos.FUENTE_MENU, 26, Color.WHITE,
                        (i + 1) + ". " + j.getNombre() + ": " + j.getPuntosTotales() + " pts",
                        Recursos.COLOR_LETRA);
                txt.setPosicion(120, tituloGanadorTxt.getY() - 80 - (i * 45));
                puntajesFinalesTxt.add(txt);
            }
        }

        salirBtn = new Boton("Salir", Color.WHITE, Color.BLACK, 30, true);
        salirBtn.setPosition(Gdx.graphics.getWidth() - salirBtn.getWidth() - 30, 30);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1f);

        verificarSalirBtn();

        batch.begin();
        if (tituloGanadorTxt != null) {
            tituloGanadorTxt.dibujar(batch);
        }
        for (Texto txt : puntajesFinalesTxt) {
            if (txt != null) {
                txt.dibujar(batch);
            }
        }
        if (salirBtn != null) {
            salirBtn.draw(batch);
        }
        batch.end();
    }

    private void verificarSalirBtn() {
        if (salirBtn == null || entrada == null) return;

        if (salirBtn.estaDentro(entrada)) {
            if (entrada.isTouch() && !clicSalir) {
                clicSalir = true;
                salirBtn.setHabilitado(false);
                Sonidos.playAudio(Sonidos.PREVIEWCOMPLETE.getAudio());

                if (hiloCliente != null) {
                    hiloCliente.terminar();
                }
                ScreenManager.getInstance().setScreen(new MenuPrincipal());
            }
        }
        if (clicSalir && !entrada.isTouch()) {
            clicSalir = false;
        }
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
            batch = null;
        }
        if (tituloGanadorTxt != null) {
            tituloGanadorTxt.dispose();
            tituloGanadorTxt = null;
        }
        for (Texto txt : puntajesFinalesTxt) {
            if (txt != null) {
                txt.dispose();
            }
        }
        puntajesFinalesTxt.clear();
    }
}
