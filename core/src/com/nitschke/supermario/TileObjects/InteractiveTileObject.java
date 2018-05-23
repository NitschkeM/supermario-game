package com.nitschke.supermario.TileObjects;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;


public abstract class InteractiveTileObject {

    Body b2body;
    MapObject mapObject;

    Fixture fixture;

    InteractiveTileObject(MapObject object){
        this.mapObject = object;
    }

    void setCategoryFilter(short filterBit){
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }

}
