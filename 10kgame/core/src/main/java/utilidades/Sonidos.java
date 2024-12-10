package utilidades;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public enum Sonidos {
	
	PREVIEWCOMPLETE("previewcomplete"),
	SOFTCLICK("softclick");
	
	private String nombre;
	
	Sonidos(String nombre){
		this.nombre = nombre;
	}
	
	public Sound getAudio() {
		String ruta =  "sonidos/gfx/" + nombre + ".wav";
		return Gdx.audio.newSound(Gdx.files.internal(ruta));
	}
	
	public static void playAudio(Sound audio) {
		audio.play();
	}

}
