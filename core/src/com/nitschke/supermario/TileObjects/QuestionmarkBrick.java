package com.nitschke.supermario.TileObjects;


import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.physics.box2d.World;
import com.nitschke.supermario.MarioGame;


public class QuestionmarkBrick extends BaseBrick {
    private static TiledMapTileSet tileSet;
    private final int EMPTY_QUESTIONMARK_BRICK = 28;          //TODO: Seems a bit out of place?

    public QuestionmarkBrick(MapObject object, TiledMap tiledMap, World b2world){
        super(object, tiledMap, b2world);

        tileSet = tiledMap.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(MarioGame.COIN_BIT);
    }

    public boolean isEmpty(){
        return getCell().getTile().getId() == EMPTY_QUESTIONMARK_BRICK;
    }

    public void setToBlankCoin(){
        getCell().setTile(tileSet.getTile(EMPTY_QUESTIONMARK_BRICK));
    }

    public boolean isMushroom(){
        return mapObject.getProperties().containsKey("mushroom");
    }
}
