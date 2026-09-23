package com.wiessen_10kgame.ui.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.wiessen_10kgame.core.Recursos;
import com.wiessen_10kgame.core.controlador.ControladorJuego;
import com.wiessen_10kgame.core.controlador.ControladorJuegoListener;
import com.wiessen_10kgame.core.modelo.EstadoDado;
import com.wiessen_10kgame.core.modelo.Jugador;
import com.wiessen_10kgame.red.HiloCliente;
import com.wiessen_10kgame.ui.componentes.Boton;
import com.wiessen_10kgame.ui.componentes.Entrada;
import com.wiessen_10kgame.ui.componentes.RenderizadorDados;
import com.wiessen_10kgame.ui.componentes.Texto;
import com.wiessen_10kgame.utilidades.ScreenManager;
import com.wiessen_10kgame.utilidades.Sonidos;

import java.util.ArrayList;
import java.util.List;

/**
 * Pantalla principal de juego donde se desarrolla la partida.
 * Visualiza los dados tirados, los puntajes de ronda y totales, y gestiona las acciones de tirar o plantarse.
 * Totalmente desacoplada del modelo: no contiene variables estáticas de juego.
 */
public class SalaJuego implements Screen, ControladorJuegoListener {

    private final ControladorJuego controlador;
    private final HiloCliente hiloCliente;

    private SpriteBatch batch;
    private RenderizadorDados renderizadorDados;
    private Boton tirarBtn, plantarBtn;
    private Texto ptsTxt, ptsTotalesTxt, ptsTotalesNro, ptsNro, turnoTxt, turnoNombre;
    private final List<Texto> listaJugadoresTxt = new ArrayList<>();
    private Entrada entrada;
    private boolean clicTirar = false, clicPlantar = false;
    private boolean irAPantallaGanador = false;

    public SalaJuego(ControladorJuego controlador, HiloCliente hiloCliente) {
        this.controlador = controlador;
        this.hiloCliente = hiloCliente;
    }

    public SalaJuego(ControladorJuego controlador) {
        this(controlador, null);
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        entrada = ScreenManager.getInstance().getEntrada();
        if (entrada != null) {
            Gdx.input.setInputProcessor(entrada);
        }

        renderizadorDados = new RenderizadorDados();

        tirarBtn = new Boton("Tirar", Color.WHITE, Color.BLACK, 30, false);
        plantarBtn = new Boton("Me planto", Color.WHITE, Color.BLACK, 22, false);
        tirarBtn.setPosition(Gdx.graphics.getWidth() - tirarBtn.getWidth() - 20, 20);
        plantarBtn.setPosition(tirarBtn.getPosicion().x, tirarBtn.getPosicion().y + 20 + plantarBtn.getHeight());

        ptsTotalesTxt = new Texto(Recursos.FUENTE_MENU, 30, Recursos.COLOR_LETRA, "Puntos totales:", Color.WHITE);
        ptsTotalesTxt.setPosicion((float) Gdx.graphics.getWidth() / 2 - ptsTotalesTxt.getWidth() / 2, Gdx.graphics.getHeight() - 50);

        turnoTxt = new Texto(Recursos.FUENTE_MENU, 26, Recursos.COLOR_LETRA, "Turno de:", Color.WHITE);
        turnoTxt.setPosicion(20, 20 + turnoTxt.getHeight());

        ptsTxt = new Texto(Recursos.FUENTE_MENU, 26, Color.valueOf("0eac12"), "Puntos:", Color.WHITE);
        ptsTxt.setPosicion(20, turnoTxt.getY() + turnoTxt.getHeight() + 20);

        ptsTotalesNro = new Texto(Recursos.FUENTE_MENU, 30, Recursos.COLOR_LETRA, "0", Color.WHITE);
        ptsTotalesNro.setPosicion(ptsTotalesTxt.getPosicion().x + ptsTotalesTxt.getWidth() + 10, ptsTotalesTxt.getPosicion().y);

        turnoNombre = new Texto(Recursos.FUENTE_MENU, 26, Recursos.COLOR_LETRA, "-", Color.WHITE);
        turnoNombre.setPosicion(turnoTxt.getPosicion().x + turnoTxt.getWidth() + 10, turnoTxt.getPosicion().y);

        ptsNro = new Texto(Recursos.FUENTE_MENU, 26, Color.valueOf("0eac12"), "0", Color.WHITE);
        ptsNro.setPosicion(ptsTxt.getPosicion().x + ptsTxt.getWidth() + 10, ptsTxt.getPosicion().y);

        if (controlador != null) {
            controlador.agregarListener(this);
            actualizarTextosEstado();
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1f);

        if (irAPantallaGanador) {
            irAPantallaGanador = false;
            ScreenManager.getInstance().setScreen(new PantallaGanador(controlador, hiloCliente));
            return;
        }

        verificarAccionesJugador();

        batch.begin();
        mostrarDados();
        dibujarTextos();
        dibujarBotones();
        batch.end();
    }

    private void verificarAccionesJugador() {
        if (controlador == null || entrada == null) return;

        boolean esMiTurno = controlador.isMiTurno();

        // Estado del botón Tirar
        if (esMiTurno && !tirarBtn.isHabilitado()) {
            tirarBtn.setHabilitado(true);
        } else if (!esMiTurno && tirarBtn.isHabilitado()) {
            tirarBtn.setHabilitado(false);
        }

        // Estado del botón Plantarse
        boolean puedePlantarse = controlador.isPuedePlantarse() && esMiTurno;
        if (plantarBtn.isHabilitado() != puedePlantarse) {
            plantarBtn.setHabilitado(puedePlantarse);
        }

        // Detección de clics
        if (esMiTurno && tirarBtn.estaDentro(entrada)) {
            if (entrada.isTouch() && !clicTirar) {
                clicTirar = true;
                tirarBtn.setHabilitado(false);
                Sonidos.playAudio(Sonidos.PREVIEWCOMPLETE.getAudio());
                if (hiloCliente != null) {
                    hiloCliente.enviarMensaje("ClicTirarBtn");
                }
            }
        } else if (puedePlantarse && plantarBtn.estaDentro(entrada)) {
            if (entrada.isTouch() && !clicPlantar) {
                clicPlantar = true;
                tirarBtn.setHabilitado(false);
                plantarBtn.setHabilitado(false);
                Sonidos.playAudio(Sonidos.PREVIEWCOMPLETE.getAudio());
                if (hiloCliente != null) {
                    hiloCliente.enviarMensaje("SumarPuntosTotales");
                }
            }
        }

        if (clicTirar && !entrada.isTouch()) {
            clicTirar = false;
        }
        if (clicPlantar && !entrada.isTouch()) {
            clicPlantar = false;
        }
    }

    private void mostrarDados() {
        if (controlador == null || renderizadorDados == null) return;

        int[] dados = controlador.getDados();
        int ultimoDado = controlador.getUltimoDado();
        int indiceTriple = controlador.getIndiceTriple();

        if (dados[0] != -1 && ultimoDado != -1) {
            int contar = controlador.getIndiceContar();

            // 1. Dados fijos de tiradas anteriores
            for (int i = 0; i < ultimoDado && i < dados.length; i++) {
                if (dados[i] >= 0 && dados[i] < 6) {
                    renderizadorDados.dibujarDado(batch, dados[i], true, i);
                }
            }

            // 2. Dados del triple actual
            if (indiceTriple != -1) {
                for (int i = ultimoDado; i < contar && i < dados.length; i++) {
                    if (dados[i] >= 0 && dados[i] < 6) {
                        renderizadorDados.dibujarDado(batch, dados[i], true, i);
                    }
                }
            }

            // 3. Dados libres restantes de la tirada actual
            for (int i = contar; i < dados.length; i++) {
                if (dados[i] >= 0 && dados[i] < 6) {
                    boolean contable = EstadoDado.fromIndice(dados[i]).isContable();
                    renderizadorDados.dibujarDado(batch, dados[i], contable, i);
                }
            }
        }
    }

    private void dibujarTextos() {
        ptsTotalesTxt.dibujar(batch);
        ptsTotalesNro.dibujar(batch);
        turnoTxt.dibujar(batch);
        turnoNombre.dibujar(batch);
        ptsTxt.dibujar(batch);
        ptsNro.dibujar(batch);

        for (Texto txt : listaJugadoresTxt) {
            if (txt != null) {
                txt.dibujar(batch);
            }
        }
    }

    private void dibujarBotones() {
        tirarBtn.draw(batch);
        plantarBtn.draw(batch);
    }

    private void actualizarTextosEstado() {
        if (controlador == null) return;

        Jugador actual = controlador.getPartida().getJugadorActual();
        if (actual != null) {
            turnoNombre.setTexto(actual.getNombre());
            ptsTotalesNro.setTexto(Integer.toString(actual.getPuntosTotales()));
        }
        ptsNro.setTexto(Integer.toString(controlador.getPuntosRonda()));
    }

    // --- ControladorJuegoListener ---

    @Override
    public void onListaJugadoresActualizada(List<Jugador> jugadores) {
        listaJugadoresTxt.clear();
        for (int i = 0; i < jugadores.size(); i++) {
            Jugador j = jugadores.get(i);
            Texto t = new Texto(Recursos.FUENTE_MENU, 20, Color.WHITE, j.getNombre() + ": " + j.getPuntosTotales(), Recursos.COLOR_LETRA);
            t.setPosicion(20, Gdx.graphics.getHeight() - 100 - (i * 35));
            listaJugadoresTxt.add(t);
        }
    }

    @Override
    public void onPartidaIniciada() {}

    @Override
    public void onTurnoCambiado(int indiceTurno, Jugador jugadorActual, boolean esMiTurno) {
        actualizarTextosEstado();
    }

    @Override
    public void onDadosActualizados(int[] dados, int ultimoDado, int indiceTriple) {}

    @Override
    public void onPuntosRondaActualizados(int puntosRonda) {
        if (ptsNro != null) {
            ptsNro.setTexto(Integer.toString(puntosRonda));
        }
    }

    @Override
    public void onPuntosTotalesActualizados(int indiceJugador, int nuevosPuntosTotales) {
        actualizarTextosEstado();
    }

    @Override
    public void onPuedePlantarseCambiado(boolean puedePlantarse) {
        if (plantarBtn != null) {
            plantarBtn.setHabilitado(puedePlantarse && controlador.isMiTurno());
        }
    }

    @Override
    public void onGanadorDeclarado(Jugador ganador, int[] puntajesFinales) {
        irAPantallaGanador = true;
    }

    @Override
    public void onMensajeEstado(String mensaje) {}

    // --- Ciclo de vida ---

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        if (controlador != null) {
            controlador.removerListener(this);
        }
    }

    @Override
    public void dispose() {
        if (controlador != null) {
            controlador.removerListener(this);
        }
        if (renderizadorDados != null) {
            renderizadorDados.dispose();
            renderizadorDados = null;
        }
        if (batch != null) {
            batch.dispose();
            batch = null;
        }
        if (ptsTotalesTxt != null) ptsTotalesTxt.dispose();
        if (ptsTotalesNro != null) ptsTotalesNro.dispose();
        if (turnoTxt != null) turnoTxt.dispose();
        if (turnoNombre != null) turnoNombre.dispose();
        if (ptsTxt != null) ptsTxt.dispose();
        if (ptsNro != null) ptsNro.dispose();
        for (Texto t : listaJugadoresTxt) {
            if (t != null) t.dispose();
        }
        listaJugadoresTxt.clear();
    }
}
