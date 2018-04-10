package com.nitschke.supermario.Sprites.TileObjects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.nitschke.supermario.MarioGame;
import com.nitschke.supermario.Scenes.Hud;
import com.nitschke.supermario.Screens.PlayScreen;
import com.nitschke.supermario.Sprites.Items.ItemDef;
import com.nitschke.supermario.Sprites.Items.Mushroom;
import com.nitschke.supermario.Sprites.Mario;

/**
 * Created by brentaureli on 8/28/15.
 */
public class Coin extends InteractiveTileObject {
    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28;

    public Coin(PlayScreen screen, MapObject object){
        super(screen, object);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(MarioGame.COIN_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        if(getCell().getTile().getId() == BLANK_COIN)
            MarioGame.manager.get("audio/sounds/bump.wav", Sound.class).play();
        else {
            if(object.getProperties().containsKey("mushroom")) {
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / MarioGame.PPM),
                        Mushroom.class));
                MarioGame.manager.get("audio/sounds/powerup_spawn.wav", Sound.class).play();
            }
            else
                MarioGame.manager.get("audio/sounds/coin.wav", Sound.class).play();
            getCell().setTile(tileSet.getTile(BLANK_COIN));
            Hud.addScore(100);
        }
    }
}
