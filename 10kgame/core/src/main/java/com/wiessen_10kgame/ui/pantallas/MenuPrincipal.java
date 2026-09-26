package com.wiessen_10kgame.ui.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.wiessen_10kgame.MainCliente;
import com.wiessen_10kgame.core.Recursos;
import com.wiessen_10kgame.core.controlador.ControladorJuego;
import com.wiessen_10kgame.red.HiloCliente;
import com.wiessen_10kgame.ui.componentes.Entrada;
import com.wiessen_10kgame.ui.componentes.Texto;
import com.wiessen_10kgame.utilidades.ScreenManager;
import com.wiessen_10kgame.utilidades.Sonidos;

/**
 * Pantalla del menú principal del juego.
 */
public class MenuPrincipal implements Screen {

    public enum EstadoMenu {
        PRINCIPAL,
        VENTANA_MULTIJUGADOR,
        INGRESAR_NOMBRE,
        INGRESAR_IP
    }

    public enum ModoPartida {
        LOCAL,
        CREAR_SALA,
        UNIRSE_SALA
    }

    private SpriteBatch b;
    private Texto titulo, jugar, multijugador, reglas, salir;
    private Texto tituloVentana, crearSala, unirseSala, volverVentana;
    private Texto nombretxt, sublabelTxt, nombre;

    private Texture cartelNegroTex, nombreTex, barraTex;
    private Sprite cartelNegroSpr, nombreSpr, barra;

    private Entrada e = ScreenManager.getInstance().getEntrada();
    private boolean clic = false;
    private boolean flag = false;
    private boolean inicializadoInput = false;
    private boolean cambiarPantalla = false;

    private EstadoMenu estado = EstadoMenu.PRINCIPAL;
    private ModoPartida modoSeleccionado = ModoPartida.LOCAL;
    private String nombreJugadorPendiente = "";

    private ControladorJuego controlador;
    private HiloCliente hc = null;

    @Override
    public void show() {
        b = new SpriteBatch();

        titulo = new Texto(Recursos.FUENTE_MENU, 32, Color.WHITE, "EL JUEGO DEL 10.000", Recursos.COLOR_LETRA);
        titulo.setPosicion((float) Gdx.graphics.getWidth() / 2 - titulo.getWidth() / 2, Gdx.graphics.getHeight() - 100);

        jugar = new Texto(Recursos.FUENTE_MENU, 26, Color.WHITE, "LOCAL", Recursos.COLOR_LETRA);
        jugar.setPosicion((float) Gdx.graphics.getWidth() / 2 - jugar.getWidth() / 2, titulo.getY() - 140);

        multijugador = new Texto(Recursos.FUENTE_MENU, 26, Color.WHITE, "MULTIJUGADOR", Recursos.COLOR_LETRA);
        multijugador.setPosicion((float) Gdx.graphics.getWidth() / 2 - multijugador.getWidth() / 2, jugar.getY() - 65);

        reglas = new Texto(Recursos.FUENTE_MENU, 26, Color.WHITE, "REGLAS", Recursos.COLOR_LETRA);
        reglas.setPosicion((float) Gdx.graphics.getWidth() / 2 - reglas.getWidth() / 2, multijugador.getY() - 65);

        salir = new Texto(Recursos.FUENTE_MENU, 26, Color.WHITE, "SALIR", Recursos.COLOR_LETRA);
        salir.setPosicion((float) Gdx.graphics.getWidth() / 2 - salir.getWidth() / 2, reglas.getY() - 65);

        // Ventana emergente Multijugador
        cartelNegroTex = new Texture("sprites/cartelNegro.jpg");
        cartelNegroSpr = new Sprite(cartelNegroTex);
        cartelNegroSpr.setSize(520, 320);
        cartelNegroSpr.setPosition((float) Gdx.graphics.getWidth() / 2 - cartelNegroSpr.getWidth() / 2,
                (float) Gdx.graphics.getHeight() / 2 - cartelNegroSpr.getHeight() / 2);

        tituloVentana = new Texto(Recursos.FUENTE_MENU, 26, Color.WHITE, "MULTIJUGADOR", Recursos.COLOR_LETRA);
        tituloVentana.setPosicion(cartelNegroSpr.getX() + (cartelNegroSpr.getWidth() - tituloVentana.getWidth()) / 2,
                cartelNegroSpr.getY() + cartelNegroSpr.getHeight() - 40);

        crearSala = new Texto(Recursos.FUENTE_MENU, 24, Color.WHITE, "CREAR SALA", Recursos.COLOR_LETRA);
        crearSala.setPosicion(cartelNegroSpr.getX() + (cartelNegroSpr.getWidth() - crearSala.getWidth()) / 2,
                cartelNegroSpr.getY() + 190);

        unirseSala = new Texto(Recursos.FUENTE_MENU, 24, Color.WHITE, "UNIRSE A LA SALA", Recursos.COLOR_LETRA);
        unirseSala.setPosicion(cartelNegroSpr.getX() + (cartelNegroSpr.getWidth() - unirseSala.getWidth()) / 2,
                cartelNegroSpr.getY() + 120);

        volverVentana = new Texto(Recursos.FUENTE_MENU, 20, Color.WHITE, "VOLVER", Recursos.COLOR_LETRA);
        volverVentana.setPosicion(cartelNegroSpr.getX() + (cartelNegroSpr.getWidth() - volverVentana.getWidth()) / 2,
                cartelNegroSpr.getY() + 45);

        nombre = new Texto(Recursos.FUENTE_MENU, 26, Color.WHITE);

        if (e != null) {
            Gdx.input.setInputProcessor(e);
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1f);

        if (cambiarPantalla) {
            cambiarPantalla = false;
            ScreenManager.getInstance().setScreen(new SalaEspera(controlador, hc));
            return;
        }

        b.begin();

        titulo.dibujar(b);
        jugar.dibujar(b);
        multijugador.dibujar(b);
        reglas.dibujar(b);
        salir.dibujar(b);

        if (estado == EstadoMenu.VENTANA_MULTIJUGADOR) {
            dibujarVentanaMultijugador();
        } else if (estado == EstadoMenu.INGRESAR_NOMBRE) {
            ingresarNombre();
        } else if (estado == EstadoMenu.INGRESAR_IP) {
            ingresarIp();
        }

        b.end();

        if (estado == EstadoMenu.PRINCIPAL) {
            controlarMenuPrincipal();
        }

        if (clic && e != null && !e.isTouch()) {
            clic = false;
        }
    }

    private void controlarMenuPrincipal() {
        manejarHoverYClick(jugar, () -> {
            modoSeleccionado = ModoPartida.LOCAL;
            abrirIngresoNombre();
        });

        manejarHoverYClick(multijugador, () -> {
            estado = EstadoMenu.VENTANA_MULTIJUGADOR;
        });

        manejarHoverYClick(reglas, this::mostrarReglas);

        manejarHoverYClick(salir, () -> Gdx.app.exit());
    }

    private void dibujarVentanaMultijugador() {
        cartelNegroSpr.draw(b);
        tituloVentana.dibujar(b);
        crearSala.dibujar(b);
        unirseSala.dibujar(b);
        volverVentana.dibujar(b);

        manejarHoverYClick(crearSala, () -> {
            modoSeleccionado = ModoPartida.CREAR_SALA;
            abrirIngresoNombre();
        });

        manejarHoverYClick(unirseSala, () -> {
            modoSeleccionado = ModoPartida.UNIRSE_SALA;
            abrirIngresoNombre();
        });

        manejarHoverYClick(volverVentana, () -> {
            estado = EstadoMenu.PRINCIPAL;
        });

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            estado = EstadoMenu.PRINCIPAL;
        }
    }

    private boolean manejarHoverYClick(Texto t, Runnable onClick) {
        if (t == null || e == null) return false;
        if (t.mouseDentroDeTexto(e)) {
            t.setColorTexto(Recursos.COLOR_LETRA, Color.WHITE);
            if (e.isTouch() && !clic) {
                clic = true;
                Sonidos.playAudio(Sonidos.PREVIEWCOMPLETE.getAudio());
                onClick.run();
                return true;
            }
        } else {
            t.setColorTexto(Color.WHITE, Recursos.COLOR_LETRA);
        }
        return false;
    }

    private void abrirIngresoNombre() {
        estado = EstadoMenu.INGRESAR_NOMBRE;
        flag = false;
        if (e != null) {
            e.setTexto("");
            e.setEscribiendo(true);
            e.setEscribeMas(true);
        }
        if (nombre != null) {
            nombre.setTexto("");
        }
        garantizarComponentesInput();
        if (nombretxt != null) {
            nombretxt.setTexto("INGRESE SU NOMBRE");
            nombretxt.setPosicion(nombreSpr.getX() + (nombreSpr.getWidth() - nombretxt.getWidth()) / 2,
                    nombreSpr.getY() + nombreSpr.getHeight() - 40);
        }
        if (sublabelTxt != null) {
            sublabelTxt.setTexto("(MAXIMO 15 CARACTERES)");
            sublabelTxt.setPosicion(nombreSpr.getX() + (nombreSpr.getWidth() - sublabelTxt.getWidth()) / 2,
                    nombreSpr.getY() + nombreSpr.getHeight() - 75);
        }
    }

    private void abrirIngresoIp() {
        estado = EstadoMenu.INGRESAR_IP;
        flag = false;
        String ipDefecto = "127.0.0.1";
        if (e != null) {
            e.setTexto(ipDefecto);
            e.setEscribiendo(true);
            e.setEscribeMas(true);
        }
        if (nombre != null) {
            nombre.setTexto(ipDefecto);
        }
        garantizarComponentesInput();
        if (nombretxt != null) {
            nombretxt.setTexto("IP DEL HOST");
            nombretxt.setPosicion(nombreSpr.getX() + (nombreSpr.getWidth() - nombretxt.getWidth()) / 2,
                    nombreSpr.getY() + nombreSpr.getHeight() - 40);
        }
        if (sublabelTxt != null) {
            sublabelTxt.setTexto("(ENTER: 127.0.0.1 O ESCRIBA IP)");
            sublabelTxt.setPosicion(nombreSpr.getX() + (nombreSpr.getWidth() - sublabelTxt.getWidth()) / 2,
                    nombreSpr.getY() + nombreSpr.getHeight() - 75);
        }
    }

    private void garantizarComponentesInput() {
        if (!inicializadoInput) {
            inicializadoInput = true;

            nombreTex = new Texture("sprites/ingresesunombre2.jpg");
            nombreSpr = new Sprite(nombreTex);
            nombreSpr.setPosition((float) Gdx.graphics.getWidth() / 2 - nombreSpr.getWidth() / 2,
                    (float) Gdx.graphics.getHeight() / 2 - nombreSpr.getHeight() / 2);

            nombretxt = new Texto(Recursos.FUENTE_MENU, 30, Color.WHITE, "INGRESE SU NOMBRE", Recursos.COLOR_LETRA);
            nombretxt.setPosicion(nombreSpr.getX() + (nombreSpr.getWidth() - nombretxt.getWidth()) / 2,
                    nombreSpr.getY() + nombreSpr.getHeight() - 40);

            sublabelTxt = new Texto(Recursos.FUENTE_MENU, 14, Color.LIGHT_GRAY, "(MAXIMO 15 CARACTERES)", Recursos.COLOR_LETRA);
            sublabelTxt.setPosicion(nombreSpr.getX() + (nombreSpr.getWidth() - sublabelTxt.getWidth()) / 2,
                    nombreSpr.getY() + nombreSpr.getHeight() - 75);

            barraTex = new Texture(Recursos.BARRA_ESCRIBIR);
            barra = new Sprite(barraTex);
            barra.setSize(barra.getWidth(), 21);

            nombre.setPosicion(nombreSpr.getX() + 103, nombreSpr.getY() + 130);
        }
    }

    private void ingresarNombre() {
        garantizarComponentesInput();

        nombreSpr.draw(b);
        nombretxt.dibujar(b);
        if (sublabelTxt != null) {
            sublabelTxt.dibujar(b);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (e != null) e.setEscribiendo(false);
            if (modoSeleccionado == ModoPartida.LOCAL) {
                estado = EstadoMenu.PRINCIPAL;
            } else {
                estado = EstadoMenu.VENTANA_MULTIJUGADOR;
            }
            return;
        }

        // Manejo de entrada
        if (e != null) {
            e.setEscribiendo(true);
            e.setEscribeMas(e.getTexto().length() < 15);
            nombre.setTexto(e.getTexto());
        }
        barra.setPosition(nombreSpr.getX() + 104 + nombre.getWidth(), nombreSpr.getY() + 110);
        barra.draw(b);

        if (!nombre.getTexto().trim().isEmpty()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && !flag) {
                flag = true;
                if (e != null) e.setEscribiendo(false);
                String nom = nombre.getTexto().trim();

                if (modoSeleccionado == ModoPartida.LOCAL) {
                    controlador = new ControladorJuego(nom);
                    controlador.setAdmin(true);
                    cambiarPantalla = true;
                } else if (modoSeleccionado == ModoPartida.CREAR_SALA) {
                    controlador = new ControladorJuego(nom);
                    if (Gdx.app != null && Gdx.app.getApplicationListener() instanceof MainCliente) {
                        MainCliente main = (MainCliente) Gdx.app.getApplicationListener();
                        hc = main.crearSalaYConectar(nom, controlador);
                    }
                    cambiarPantalla = true;
                } else if (modoSeleccionado == ModoPartida.UNIRSE_SALA) {
                    nombreJugadorPendiente = nom;
                    abrirIngresoIp();
                }
            }
        }
        nombre.dibujar(b);
    }

    private void ingresarIp() {
        garantizarComponentesInput();

        nombreSpr.draw(b);
        nombretxt.dibujar(b);
        if (sublabelTxt != null) {
            sublabelTxt.dibujar(b);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            abrirIngresoNombre();
            return;
        }

        // Manejo de entrada para IP / Host
        if (e != null) {
            e.setEscribiendo(true);
            e.setEscribeMas(e.getTexto().length() < 30);
            nombre.setTexto(e.getTexto());
        }
        barra.setPosition(nombreSpr.getX() + 104 + nombre.getWidth(), nombreSpr.getY() + 110);
        barra.draw(b);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && !flag) {
            flag = true;
            if (e != null) e.setEscribiendo(false);
            String ip = (nombre.getTexto() != null && !nombre.getTexto().trim().isEmpty())
                    ? nombre.getTexto().trim() : "127.0.0.1";

            controlador = new ControladorJuego(nombreJugadorPendiente);

            if (Gdx.app != null && Gdx.app.getApplicationListener() instanceof MainCliente) {
                MainCliente main = (MainCliente) Gdx.app.getApplicationListener();
                hc = main.unirseASala(ip, nombreJugadorPendiente, controlador);
            }
            cambiarPantalla = true;
        }
        nombre.dibujar(b);
    }

    private void mostrarReglas() {}

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
        if (b != null) {
            b.dispose();
            b = null;
        }
        if (titulo != null) titulo.dispose();
        if (jugar != null) jugar.dispose();
        if (multijugador != null) multijugador.dispose();
        if (reglas != null) reglas.dispose();
        if (salir != null) salir.dispose();
        if (tituloVentana != null) tituloVentana.dispose();
        if (crearSala != null) crearSala.dispose();
        if (unirseSala != null) unirseSala.dispose();
        if (volverVentana != null) volverVentana.dispose();
        if (cartelNegroTex != null) cartelNegroTex.dispose();
        if (nombreTex != null) nombreTex.dispose();
        if (barraTex != null) barraTex.dispose();
        if (nombre != null) nombre.dispose();
        if (nombretxt != null) nombretxt.dispose();
        if (sublabelTxt != null) sublabelTxt.dispose();
    }
}
