package utilidades;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class ScreenManager {

	private static ScreenManager instance;
	private Game game;
	private Screen actual;

	private ScreenManager() {
		super();
	}

	public static ScreenManager getInstance() {
		if (instance == null) {
			instance = new ScreenManager();
		}
		return instance;
	}

	public void initialize(Game game) {
		this.game = game;
	}

	/*
	 * Guarda en ACTUAL la screen en pantalla actualmente. Setea en pantalla la
	 * screen que se pasa por parametro.
	 */
	public void showScreenWindow(Screen screen) {
		Screen currentScreen = game.getScreen();
		actual = currentScreen;

		Screen newScreen = screen;

		game.setScreen(newScreen);
	}

	/*
	 * Elimina la screen "actual". Cuando se hace un showScreenWindow() se llama a
	 * este metodo despues para eliminar la que antes era la "actual"
	 */
	public void closeCurrentWindow() {
		if (actual != null) {
			actual.dispose();
		}
	}

	/*
	 * Guarda la screen en pantalla, setea para mostrar la guardada en ACTUAL (si es
	 * que no se hizo un closeCurrentWindow()). Luego elimina la screen
	 * anteriormente en pantalla.
	 */
	public void showPreviousWindow() {
		Screen currentScreen = game.getScreen();

		game.setScreen(actual);

		if (currentScreen != null) {
			currentScreen.dispose();
		}
	}

	public Screen getScreen() {
		return game.getScreen();
	}

}
