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

import java.time.Clock;

/**
 * Pantalla del menú principal del juego.
 */
public class MenuPrincipal implements Screen {

    private SpriteBatch b;
    private Texto titulo, jugar, salir, reglas, nombre, nombretxt;
    private boolean primeraVez = true, primeraVez2 = true, play = false, flag = false, cambiarPantalla = false;
    private int aux = -1;
    private Sprite nombreSpr, barra;
    private Texture nombreTex, barraTex;
    private Entrada e = ScreenManager.getInstance().getEntrada();
    private int var = 0;
    private HiloCliente hc = null;
    private ControladorJuego controlador;

    @Override
    public void show() {
        b = new SpriteBatch();
        titulo = new Texto(Recursos.FUENTE_MENU, 32, Color.WHITE, "EL JUEGO DEL 10.000", Recursos.COLOR_LETRA);
        titulo.setPosicion((float) Gdx.graphics.getWidth() / 2 - titulo.getWidth() / 2, Gdx.graphics.getHeight() - 100);

        jugar = new Texto(Recursos.FUENTE_MENU, 26, Color.WHITE, "JUGAR", Recursos.COLOR_LETRA);
        jugar.setPosicion((float) Gdx.graphics.getWidth() / 2 - jugar.getWidth() / 2, titulo.getY() - 160);

        reglas = new Texto(Recursos.FUENTE_MENU, 26, Color.WHITE, "REGLAS", Recursos.COLOR_LETRA);
        reglas.setPosicion((float) Gdx.graphics.getWidth() / 2 - reglas.getWidth() / 2, jugar.getY() - 90);

        salir = new Texto(Recursos.FUENTE_MENU, 26, Color.WHITE, "SALIR", Recursos.COLOR_LETRA);
        salir.setPosicion((float) Gdx.graphics.getWidth() / 2 - salir.getWidth() / 2, reglas.getY() - 90);

        nombre = new Texto(Recursos.FUENTE_MENU, 26, Color.WHITE);
    }

    @Override
    public void render(float delta) {
        // 1. Limpieza de pantalla
        ScreenUtils.clear(0, 0, 0, 1f);

        // 2. Actualización de lógica (fuera o dentro según estado)
        if (!play) {
            controlarMousePos();
        }

        // 3. Renderizado de componentes gráficos
        b.begin();

        titulo.dibujar(b);
        jugar.dibujar(b);
        reglas.dibujar(b);
        salir.dibujar(b);

        if (play) {
            ingresarNombre();
        }
        b.end();
        if(cambiarPantalla)
            ScreenManager.getInstance().setScreen(new SalaEspera(controlador, hc));
    }

    private void controlarMousePos() {
        Texto variable;
        if(var == 3)
            var = 0;
            variable = igualarVariable(var);
            if (variable == null) return;

            if (variable.mouseDentroDeTexto(e)) {
                if (primeraVez) {
                    Sonidos.playAudio(Sonidos.SOFTCLICK.getAudio());
                    primeraVez = false;
                    aux = var;
                }
                variable.setColorTexto(Recursos.COLOR_LETRA, Color.WHITE);
                if (e.isTouch()) {
                    Sonidos.playAudio(Sonidos.PREVIEWCOMPLETE.getAudio());
                    ejecutarAccion(variable);
                }
            } else if (!primeraVez && aux == var) {
                primeraVez = true;
                primeraVez2 = true;
                variable.setColorTexto(Color.WHITE, Recursos.COLOR_LETRA);
            }
        var++;

    }

    private void ejecutarAccion(Texto variable) {
        if (variable == salir && !play) {
            Gdx.app.exit();
        } else if (variable == reglas && !play) {
            mostrarReglas();
        } else if (variable == jugar) {
            play = true;
        }
    }

    private void ingresarNombre() {
        // Inicialización diferida segura en el primer render del formulario de nombre
        if (primeraVez2) {
            primeraVez2 = false;

            nombreTex = new Texture("sprites/ingresesunombre2.jpg");
            nombreSpr = new Sprite(nombreTex);
            nombreSpr.setPosition((float) Gdx.graphics.getWidth() / 2 - nombreSpr.getWidth() / 2,
                (float) Gdx.graphics.getHeight() / 2 - nombreSpr.getHeight() / 2);

            nombretxt = new Texto(Recursos.FUENTE_MENU, 32, Color.WHITE, "INGRESE SU NOMBRE", Recursos.COLOR_LETRA);
            nombretxt.setPosicion(nombreSpr.getX() + nombreSpr.getWidth() / 2 - nombretxt.getWidth() / 2,
                nombreSpr.getY() + nombreSpr.getHeight() - 40);

            barraTex = new Texture(Recursos.BARRA_ESCRIBIR);
            barra = new Sprite(barraTex);
            barra.setSize(barra.getWidth(), 21);

            nombre.setPosicion(nombreSpr.getX() + 103, nombreSpr.getY() + 143);
        }

        // Dibujar el cuadro y texto informativo
        nombreSpr.draw(b);
        nombretxt.dibujar(b);

        // Procesar entrada por teclado
        if (nombre.getTexto().length() < 16) {
            e.setEscribiendo(true);
            nombre.setTexto(e.getTexto());
            barra.setPosition(nombreSpr.getX() + 104 + nombre.getWidth(), nombreSpr.getY() + 122);
            barra.draw(b);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DEL)) {
            if (!e.getTexto().isEmpty()) {
                e.setTexto(e.getTexto().substring(0, e.getTexto().length() - 1));
                nombre.setTexto(e.getTexto());
            }
        } else {
            e.setEscribiendo(false);
        }

        // Confirmar con ENTER
        if (!nombre.getTexto().isEmpty()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && !flag) {
                flag = true;
                String nom = nombre.getTexto().trim();
                controlador = new ControladorJuego(nom);

                if (Gdx.app != null && Gdx.app.getApplicationListener() instanceof MainCliente) {
                    MainCliente main = (MainCliente) Gdx.app.getApplicationListener();
                    hc = main.conectarServidor(nom, controlador);
                }
                cambiarPantalla=true;
            }
        }
        nombre.dibujar(b);
    }

    private Texto igualarVariable(int i) {
        switch (i) {
            case 0: return jugar;
            case 1: return reglas;
            case 2: return salir;
            default: return null;
        }
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
        if (reglas != null) reglas.dispose();
        if (salir != null) salir.dispose();
        if (nombre != null) nombre.dispose();
        if (nombretxt != null) nombretxt.dispose();
        if (nombreTex != null) nombreTex.dispose();
        if (barraTex != null) barraTex.dispose();
    }
}
