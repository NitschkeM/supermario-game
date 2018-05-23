package com.nitschke.supermario.Entities.Enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;


public class Turtle extends Enemy {
    // Velocity of a moving shell.
    public static final int MOVING_SHELL_VELOCITY = 2;
//    public static final int KICK_RIGHT = 2;


    public enum State {WALKING, MOVING_SHELL, SLEEPING_SHELL}
    public State currentState;
    private State previousState;
    private float stateTimer;
    private boolean isFacingRight;

    // TODO: Use to adjust difficulty.
    private float shellSleepTime;


//    private Animation<TextureRegion> walkAnimation;
//    private TextureRegion shellTR;


    TurtleSprite turtleSprite;

    // TODO: Destroy b2body.
    public Turtle(final World b2world, final Vector2 b2Position, final TextureAtlas textureAtlas, final Vector2 initialVelocity, final float shellSleepTime) {
        super(b2world, b2Position, initialVelocity);
        turtleSprite = new TurtleSprite(textureAtlas);

        // Initialize facing direction and state
        isFacingRight = (initialVelocity.x > 0);
        currentState = State.WALKING;
        previousState = State.WALKING;

        // Set time a shell sleeps before moving again.
        this.shellSleepTime = shellSleepTime;

//        turtleSprite = new Sprite();
        // SPRITE STUFF
//        Array<TextureRegion> frames = new Array<TextureRegion>();
//        frames.add(new TextureRegion(textureAtlas.findRegion("turtle"), 0, 0, 16, 24));
//        frames.add(new TextureRegion(textureAtlas.findRegion("turtle"), 16, 0, 16, 24));
//        walkAnimation = new Animation(0.2f, frames);
//        shellTR = new TextureRegion(textureAtlas.findRegion("turtle"), 64, 0, 16, 24);
//        setBounds(getX(), getY(), 16 / MarioGame.PPM, 24 / MarioGame.PPM);
//        turtleSprite.setBounds(b2Position.x, b2Position.y, 16 / MarioGame.PPM, 24 / MarioGame.PPM);
    }

//    private void getFrame(float dt){
//        TextureRegion region;
//
//        // TODO: TurtleSprite.selectTextureRegion
//        switch (currentState){
//            case MOVING_SHELL:
//            case SLEEPING_SHELL:
//                region = shellTR;
//                break;
//            case WALKING:
//            default:
//                region = walkAnimation.getKeyFrame(stateTimer, true);
//                break;
//        }
////        if(velocity.x > 0 && !region.isFlipX()){
////            region.flip(true, false);
////        }
////        if(velocity.x < 0 && region.isFlipX()){
////            region.flip(true, false);
////        }
//
//
//
//        stateTimer = currentState == previousState ? stateTimer + dt : 0;
//        //update previous state
//        previousState = currentState;
//    }

//    int counter = 0;
    @Override
    public void update(float dt) {
//        // TODO: Debug, periodically prints b2body position.
//        if (counter > 180){
////            Gdx.app.debug("Mario", "Position*100: " + b2body.getPosition().x*MarioGame.PPM  +", " + b2body.getPosition().y*MarioGame.PPM);
//            Gdx.app.debug("Turtle", "currentVelocity:" + currentVelocity.hashCode());
//            counter = 0;
//        }
//        counter++;
        updateState(dt);
        updateFacingDirection();


        turtleSprite.update(getB2Position(), currentState, stateTimer, isFacingRight);
        b2body.setLinearVelocity(currentVelocity);

        // TODO: Factored
//        setRegion(getFrame(dt));
//        if(currentState == State.SLEEPING_SHELL && stateTimer > 5){
//            currentState = State.WALKING;
//            velocity.x = 1;
//        }
//
//        // Update sprite according to b2body position.
//        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - 8 /MarioGame.PPM);
//        // TODO: Consider situation: The velocity variable functions as a temp variable used to manipulate b2Body velocity at appropriate time, i.e. here, update().
//        b2body.setLinearVelocity(velocity);
    }

    private void updateState(float dt){
        // Shell just went to sleep. Pass through to reset timer.
        if (currentState == State.SLEEPING_SHELL && previousState != State.SLEEPING_SHELL){
        }
        // Wakes up in opposite direction to initial velocity.
        else if (currentState == State.SLEEPING_SHELL && stateTimer > shellSleepTime){
            currentState = State.WALKING;
            currentVelocity.x = -initialVelocity.x;
        }

//        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        if (currentState == previousState){
            stateTimer += dt;
        }
        else {
            Gdx.app.debug("Turtle updateState END", "State changed from: " + previousState + " \t to: " + currentState + "\tstateTimer: " + stateTimer);
            previousState = currentState;
            stateTimer = 0;
        }
    }

    private void updateFacingDirection(){
        if(currentVelocity.x > 0){
            isFacingRight = true;
        }

        else if(currentVelocity.x < 0){
            isFacingRight = false;
        }
    }

    public void hitOnHead(Vector2 marioPosition) {
        if(currentState == State.SLEEPING_SHELL) {
            if(marioPosition.x > b2body.getPosition().x)
                currentVelocity.x = -2;
            else
                currentVelocity.x = 2;
            currentState = State.MOVING_SHELL;
        }
        else {
            currentState = State.SLEEPING_SHELL;
            currentVelocity.x = 0;
        }
        Gdx.app.debug("Turtle HitOnHead has run", "previousState " + previousState + " \t currentState: " + currentState + "stateTimer: " + stateTimer);
    }

    @Override
    public void hitByEnemy(Enemy enemy) {
        reverseVelocity(true, false);
    }

    @Override
    public void activate() {
        if(!destroyed && !b2body.isActive())        // TODO: !destoryed should be unnecessary. Hypothesis: An enemy cannot be destoryed before it has been activated.
            b2body.setActive(true);
    }

//    // TODO: direction is not intuitively represented as an int. (Velocity)
//    public void kick(int velocity){
//        this.currentVelocity.x = velocity;
//        currentState = State.MOVING_SHELL;
//    }

    public void kickRight(){
        this.currentVelocity.x = MOVING_SHELL_VELOCITY;
        currentState = State.MOVING_SHELL;
    }
    public void kickLeft(){
        this.currentVelocity.x = -MOVING_SHELL_VELOCITY;
        currentState = State.MOVING_SHELL;
    }

    public void draw(SpriteBatch spriteBatch){
        turtleSprite.draw(spriteBatch);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // *******************************  PRIVATE METHODS  **********************************************



}