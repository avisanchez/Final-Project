package core.gameobject;

import java.io.Serializable;
import java.net.URL;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Sound implements Serializable {
    URL url;
    Clip clip;
    Runnable clipRunner;
    Thread clipPlayer;
    FloatControl fc;

    public Sound(String filePath, float soundReductionDB) {
        try {
            url = this.getClass().getClassLoader().getResource(filePath);
            clip = AudioSystem.getClip();

            clipRunner = new Runnable() {
                @Override
                public void run() {
                    try {
                        clip.open(AudioSystem.getAudioInputStream(url));
                        fc = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                        fc.setValue(soundReductionDB); // Reduce volume by x decibels.
                        clip.start();
                    } catch (Exception exc) {
                        exc.printStackTrace(System.out);
                    }
                }
            };
        } catch (Exception exc) {
            exc.printStackTrace(System.out);
        }
    }

    public void play() {
        clipPlayer = new Thread(clipRunner);
        clipPlayer.start();
    }

    public static Sound backgroundMusic = new Sound("core/resource/sound/gamemusic.wav", -10.0f);
}