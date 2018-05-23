package com.nitschke.supermario.Entities.Other;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.nitschke.supermario.MarioGame;
import com.nitschke.supermario.Screens.PlayScreen;


public class FireBall extends Sprite {

    private World world;
    private Animation<TextureRegion> fireAnimation;
    private float stateTime;
    private boolean destroyed;
    private boolean setToDestroy;
    private boolean fireRight;

    private Body b2body;

    public FireBall(PlayScreen screen, float x, float y, boolean fireRight){
        this.fireRight = fireRight;
        this.world = screen.getWorld();
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for(int i = 0; i < 4; i++){
            frames.add(new TextureRegion(screen.getAtlas().findRegion("fireball"), i * 8, 0, 8, 8));
        }
        fireAnimation = new Animation(0.2f, frames);
        setRegion(fireAnimation.getKeyFrame(0));
        setBounds(x, y, 6 / MarioGame.PPM, 6 / MarioGame.PPM);
        defineFireBall();
    }

    private void defineFireBall(){

        BodyDef bdef = new BodyDef();
        bdef.position.set(fireRight ? getX() + 12 /MarioGame.PPM : getX() - 12 /MarioGame.PPM, getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        //TODO: Remove debug comments
        //TODO: Hypothesis: !world.isLocked() check is not necessary because this method will never be fired within a timestemp (no threads). & playscreen cant do two things at once.
                        // Current suspect #1: This needs to run... So what happens when it does not? ()
                        // Cleared for now as this problem should have been reproducable simply by spamming fireball.
        if(!world.isLocked()) {
            // The world isLocked() if it is in the middle of a timestep.
            Gdx.app.debug("Fireball", "Creating..."); // DEBUG
            b2body = world.createBody(bdef);            // THIS IS the method that always is mentioned in logs!!! NOT CLEARED... But would that not also imply that this method is called!?

        }

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(3 / MarioGame.PPM);
        fdef.filter.categoryBits = MarioGame.FIREBALL_BIT;
        fdef.filter.maskBits = MarioGame.GROUND_BIT |
                MarioGame.COIN_BIT |
                MarioGame.BRICK_BIT |
                MarioGame.ENEMY_BIT |
                MarioGame.PIPE_BIT;

        fdef.shape = shape;
        fdef.restitution = 1;
        fdef.friction = 0;
        b2body.createFixture(fdef).setUserData(this);
        b2body.setLinearVelocity(new Vector2(fireRight ? 2 : -2, 2.5f));
    }

    // TODO: Factor out into smaller methods with logical names.
    public void update(float dt){
        if((stateTime > 3 || setToDestroy) && !destroyed) {
            Gdx.app.debug("Fireball", "Destroying " + b2body); // DEBUG
            world.destroyBody(b2body);
            destroyed = true;
            // TODO: Body to null debug
            b2body = null;
        }
        if (!destroyed){
            stateTime += dt;
            setRegion(fireAnimation.getKeyFrame(stateTime, true));
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2); // Set sprite position - graphics follows physics.

            if(b2body.getLinearVelocity().y > 2f)   // TODO: What is this? Reduce velocity? Perhaps because jump + fire according to physics would give y-velocity >2.
                b2body.setLinearVelocity(b2body.getLinearVelocity().x, 2f);
            if((fireRight && b2body.getLinearVelocity().x < 0) || (!fireRight && b2body.getLinearVelocity().x > 0)) // Destroy when bumping into "ground" i.e. walls etc. //TODO: Clarify.
                setToDestroy();
        }
    }

    // TODO: Why set to destroy? Is it for "outside" to be able to destroy - while still performing the action in a specified place - the update loop?
    public void setToDestroy(){
        setToDestroy = true;
    }

    public boolean isDestroyed(){
        return destroyed;
    }


}