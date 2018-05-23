package com.nitschke.supermario.Entities;


import com.badlogic.gdx.math.Vector2;

public class MarioSyncInfo {
    private Vector2 position;
    private boolean isBig;

    public MarioSyncInfo(Vector2 position, boolean isBig){
        this.position = position;
        this.isBig = isBig;
    }

    public Vector2 getPosition() {
        return position;
    }
//    public Vector2 setPosition(Vector2 positon) {
//        this.position = position;
//    }
    public boolean isBig(){
        return isBig;
    }
}
