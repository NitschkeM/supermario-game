package com.nitschke.supermario.Sprites.TileObjects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.nitschke.supermario.MarioGame;
import com.nitschke.supermario.Scenes.Hud;
import com.nitschke.supermario.Screens.PlayScreen;
import com.nitschke.supermario.Sprites.Mario;

/**
 * Created by brentaureli on 8/28/15.
 */
public class Brick extends InteractiveTileObject {
    public Brick(PlayScreen screen, MapObject object){
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MarioGame.BRICK_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        if(mario.isBig()) {
            setCategoryFilter(MarioGame.DESTROYED_BIT);
            getCell().setTile(null);
            Hud.addScore(200);
            MarioGame.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
        }
        MarioGame.manager.get("audio/sounds/bump.wav", Sound.class).play();
    }

}
