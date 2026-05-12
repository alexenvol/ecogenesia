package main;

import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Sound {
	
	Clip clip;
	URL soundURL[] = new URL[30];
	FloatControl fc;
	int volumeScale = 3;
	float volume;
	
	public Sound() {
		
		soundURL[0] = getClass().getResource("/sound/menu_principal.wav");
		soundURL[1] = getClass().getResource("/sound/coin.wav");
		soundURL[2] = getClass().getResource("/sound/jeu.wav");
		soundURL[3] = getClass().getResource("/sound/menu.wav");
		soundURL[4] = getClass().getResource("/sound/fin.wav");
		soundURL[5] = getClass().getResource("/sound/cursor.wav");
		soundURL[6] = getClass().getResource("/sound/ocean.wav");
	}
	
	public void setFile(int i) {
	    try {
	        if (soundURL[i] != null) {
	            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
	            clip = AudioSystem.getClip();
	            clip.open(ais);

	            // Vérifie les contrôles disponibles
	            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
	                fc = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
	            } else if (clip.isControlSupported(FloatControl.Type.VOLUME)) {
	                fc = (FloatControl) clip.getControl(FloatControl.Type.VOLUME);
	            } else {
	                System.err.println("Aucun contrôle de volume disponible pour le clip à l'index : " + i);
	                fc = null;
	            }

	            // Appliquer immédiatement le volume
	            checkVolume();

	        } else {
	            System.err.println("Erreur : soundURL[" + i + "] est null.");
	        }
	    } catch (Exception e) {
	        System.err.println("Erreur lors du chargement du fichier audio à l'index " + i);
	        e.printStackTrace();
	    }
	}
	
	public int getCurrentPosition() {
	    if (clip != null) {
	        int position = clip.getFramePosition();
	        return position;
	    }
	    System.err.println("Clip is null in getCurrentPosition()");
	    return 0;
	}

	public void resume(int framePosition) {
	    if (clip != null) {
	        clip.setFramePosition(framePosition);
	        clip.start();
	    } else {
	        System.err.println("Clip is null in resume()");
	    }
	}

	public void play() {
		
		clip.start();
	}
	
	public void loop() {
		
		clip.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	public void stop() {
	    if (clip != null) {
	        clip.stop();
	    } else {
	        System.err.println("Error: Attempted to stop a null clip!");
	    }
	}

	public void checkVolume() {
	    if (fc == null) {
	        System.err.println("Erreur : FloatControl (fc) est null. Impossible d'ajuster le volume.");
	        return;
	    }

	    switch(volumeScale) {
	        case 0: volume = -80f; break;
	        case 1: volume = -20f; break;
	        case 2: volume = -12f; break;
	        case 3: volume = -5f; break;
	        case 4: volume = 1f; break;
	        case 5: volume = 6f; break;
	    }
	    fc.setValue(volume);
	}
}