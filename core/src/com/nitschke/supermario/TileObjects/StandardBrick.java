package com.nitschke.supermario.TileObjects;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.nitschke.supermario.MarioGame;


public class StandardBrick extends BaseBrick {


    public StandardBrick(MapObject object, TiledMap tiledMap, World b2world){
        super(object, tiledMap, b2world);
        fixture.setUserData(this);
        setCategoryFilter(MarioGame.BRICK_BIT);
    }

    public void breakBrick(){
        setCategoryFilter(MarioGame.DESTROYED_BIT);
        getCell().setTile(null);
    }

    public Body getBody(){
        return this.b2body;
    }

}
