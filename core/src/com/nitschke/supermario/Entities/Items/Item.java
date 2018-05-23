package com.nitschke.supermario.Entities.Items;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.nitschke.supermario.MarioGame;
import com.nitschke.supermario.Screens.PlayScreen;


public abstract class Item extends Sprite {
    protected PlayScreen screen;
    protected World world;
    Vector2 velocity;
    private boolean setToDestroy;
    private boolean destroyed;
    Body b2body;

    Item(PlayScreen screen, float x, float y){
        this.screen = screen;
        this.world = screen.getWorld();
        setToDestroy = false;
        destroyed = false;

        setPosition(x, y);
        setBounds(getX(), getY(), 16 / MarioGame.PPM, 16 / MarioGame.PPM);
        defineItem();
    }

    public abstract void defineItem();
//    public abstract void use(Mario mario);
//    public abstract void use();

    public void update(float dt){
        if(setToDestroy && !destroyed){
            world.destroyBody(b2body);
            destroyed = true;
            b2body = null;      // TODO: b2body = null for debug. (null-ptr exception)
        }
    }

    public void draw(Batch batch){
        if(!destroyed)
            super.draw(batch);
    }

    public boolean isDestroyed(){
        return destroyed;
    }

    public void setToDestroy(){
        setToDestroy = true;
    }

    public void reverseVelocity(boolean x, boolean y){
        if(x)
            velocity.x = -velocity.x;
        if(y)
            velocity.y = -velocity.y;
    }
}
