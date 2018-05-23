package com.nitschke.supermario.Tools;


import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class B2dAssetManager {


    public final AssetManager manager = new AssetManager();

        // Music
        public void queueAddMusic(){
            manager.load("audio/music/mario_music.ogg", Music.class);
        }


        // Sounds
        public void queueAddSounds() {
            manager.load("audio/sounds/coin.wav", Sound.class);
            manager.load("audio/sounds/bump.wav", Sound.class);
            manager.load("audio/sounds/breakblock.wav", Sound.class);
            manager.load("audio/sounds/powerup_spawn.wav", Sound.class);
            manager.load("audio/sounds/powerup.wav", Sound.class);
            manager.load("audio/sounds/powerdown.wav", Sound.class);
            manager.load("audio/sounds/stomp.wav", Sound.class);
            manager.load("audio/sounds/mariodie.wav", Sound.class);
        }



    public void playMarioMusic(){
        Music music = manager.get("audio/music/mario_music.ogg", Music.class);
//        manager.get("audio/music/mario_music.ogg", Music.class).play();
        music.setLooping(true);
        music.setVolume(0.3f);
        music.play();
    }
    public void stopMarioMusic(){
        manager.get("audio/music/mario_music.ogg", Music.class).stop();
    }


    public void playMarioDieSound(){
        manager.get("audio/sounds/mariodie.wav", Sound.class).play();
    }
    public void playPowerUpSound(){
        manager.get("audio/sounds/powerup.wav", Sound.class).play();
    }
    public void playPowerDownSound(){
        manager.get("audio/sounds/powerdown.wav", Sound.class).play();
    }
    public void playStompSound(){
        manager.get("audio/sounds/stomp.wav", Sound.class).play();
    }
    public void playBreakBlockSound(){
        manager.get("audio/sounds/breakblock.wav", Sound.class).play();
    }
    public void playBumpSound(){
        manager.get("audio/sounds/bump.wav", Sound.class).play();
    }
    public void playPowerUpSpawnSound(){
        manager.get("audio/sounds/powerup_spawn.wav", Sound.class).play();
    }
    public void playCoinSound(){
        manager.get("audio/sounds/coin.wav", Sound.class).play();
    }


    public void dispose(){
        manager.dispose();
    }


}
