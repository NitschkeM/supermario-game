package com.nitschke.supermario.Entities.Enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;


public class Goomba extends Enemy
{
    public enum State{ALIVE, DESTROYED, REMOVABLE}

    private State currentState;
    private State previousState;
    private float stateTimer;

    private boolean goombaIsDead;

    private GoombaSprite goombaSprite;


    public Goomba(final World b2world, final Vector2 b2Position, final TextureAtlas textureAtlas, final Vector2 initialVelocity) {
        super(b2world, b2Position, initialVelocity);
        goombaSprite = new GoombaSprite(textureAtlas);

        currentState = State.ALIVE;
        previousState = State.ALIVE;

        goombaIsDead = false;
        destroyed = false;
    }

    public State getState() {
        return currentState;
    }
    public void kill() {
        goombaIsDead = true;
    }

    public void update(float dt){
//        stateTimer += dt;
        updateState(dt);

        // If alive: Move and update sprite.
        if (currentState == State.ALIVE){
            b2body.setLinearVelocity(currentVelocity);
            goombaSprite.update(b2body.getPosition(), stateTimer);
        }
        // Else if just died, tell sprite to switch textureRegion.
        else if (currentState == State.DESTROYED && stateTimer == 0){
            goombaSprite.useDeadTextureRegion();
        }
    }

    public void draw(SpriteBatch spriteBatch){
        // A removable entity need not be drawn.
        if (currentState != State.REMOVABLE)
            goombaSprite.draw(spriteBatch);
    }

    @Override
    public void hitByEnemy(Enemy enemy) {
        if(enemy instanceof Turtle && ((Turtle) enemy).currentState == Turtle.State.MOVING_SHELL){
            kill();
        }
        else
            reverseVelocity(true, false);
    }

    @Override
    public void activate() {
        if (currentState != State.DESTROYED && !b2body.isActive())
            b2body.setActive(true);                 // NOTE:    FALSE: The isActive() check also touches b2body and triggers null-pointer exception if destroyed.
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // *******************************  PRIVATE METHODS  **********************************************

    private void updateState(float dt) {

        // If removable, the entity has nothing more to do.
        if (currentState == State.REMOVABLE){
        }
        // If goomba has been destroyed for 1 sec. The death animation is done.
        else if (currentState == State.DESTROYED){
            if (stateTimer > 1)
                currentState = State.REMOVABLE;
        }
        // If goomba has just been killed, destroy and update state.        // TODO: Consider: Kill() --> currentState = Dead. Then: else if (current = DEAD && prev == Alive) destroy. + isStateSwitchAllowed(targetState)
        else if (currentState == State.ALIVE) {
            if (goombaIsDead){
                destroy();
                currentState = State.DESTROYED;
            }
        }
        // Else
        //  Goomba is alive (and !goombaIsDead).
        //  Or destroyed, displaying death animation.

        // If no change, increment stateTimer.
        if (currentState == previousState) {
            stateTimer += dt;
        } // Else reset stateTimer and update previousState.
        else {
            Gdx.app.debug("Goomba State", "State changed from: " + previousState + " \t to: " + currentState + "\tstateTimer: " + stateTimer);
            previousState = currentState;
            stateTimer = 0;
        }
    }

    private void destroy(){
        b2world.destroyBody(b2body);
        b2body = null; // TODO: Body to null debug
        destroyed = true;
    }

}
