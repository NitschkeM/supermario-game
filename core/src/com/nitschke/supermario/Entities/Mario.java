package com.nitschke.supermario.Entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.nitschke.supermario.MarioGame;


public class Mario {

    static private float LOWEST_SURVIAVALBE_HEIGHT = -50f/MarioGame.PPM;
    static private float B2BODY_HEAD_WIDTH = 3f/MarioGame.PPM;

    private static float SMALL_MARIO_SHAPE_HEIGHT = 12.5f/MarioGame.PPM;
    private static float BIG_MARIO_SHAPE_HEIGHT = 28f/MarioGame.PPM;

    // TODO: Factor?
    private enum MarioShape {
        SMALL_MARIO_SHAPE(new float[]{
                -2f/MarioGame.PPM,0f,
                2f/MarioGame.PPM,0f,
                -4f/MarioGame.PPM,   (1/3f)*SMALL_MARIO_SHAPE_HEIGHT,
                4f/MarioGame.PPM,    (1/3f)*SMALL_MARIO_SHAPE_HEIGHT,
                -4f/MarioGame.PPM,   (2/3f)*SMALL_MARIO_SHAPE_HEIGHT,
                4f/MarioGame.PPM,    (2/3f)*SMALL_MARIO_SHAPE_HEIGHT,
                -2f/MarioGame.PPM,   SMALL_MARIO_SHAPE_HEIGHT,
                2f/MarioGame.PPM,    SMALL_MARIO_SHAPE_HEIGHT}),
        BIG_MARIO_SHAPE(new float[]{
                -2/MarioGame.PPM,0,
                2/MarioGame.PPM,0,
                -4/MarioGame.PPM,   (1/3f)*BIG_MARIO_SHAPE_HEIGHT,
                4/MarioGame.PPM,    (1/3f)*BIG_MARIO_SHAPE_HEIGHT,
                -4/MarioGame.PPM,   (2/3f)*BIG_MARIO_SHAPE_HEIGHT,
                4/MarioGame.PPM,    (2/3f)*BIG_MARIO_SHAPE_HEIGHT,
                -2/MarioGame.PPM,   BIG_MARIO_SHAPE_HEIGHT,
                2/MarioGame.PPM,    BIG_MARIO_SHAPE_HEIGHT
        });

        private float[] vertices;

        MarioShape(float[] vertices){
            this.vertices = vertices;
        }
        PolygonShape getShape(){
            PolygonShape polygonShape = new PolygonShape();
            polygonShape.set(vertices);
            return polygonShape;
        }
    }

    public enum State { FALLING, JUMPING, STANDING, RUNNING, GROWING, DEAD }


    private State currentState;
    private State previousState;
    private float stateTimer;

    private World b2world;      // Used when creating and destoying the b2body  // TODO: Create b2body handling system.
    private Body b2body;


    // Used to avoid entering STANDING state when reaching 0 velocity in y direction in collision moment. (TODO: But why is not 0 velocity reached in a regular jump? )
    private boolean hasJustHitBrickWithHead;
    private boolean isFacingRight;
    private boolean marioIsBig;
    private boolean runGrowAnimation;
//    private boolean timeToDefineBigMario;
    private boolean timeToRedefineMario;
    private boolean marioIsDead;                // TODO: Can the job of this variable be performed by State?

    private MarioSprite marioSprite;

    // ******************** PUBLIC *************************************

    public Mario(World b2world, TextureAtlas textureAtlas, Vector2 spawnPosition){
        //initialize default values
        this.b2world = b2world;
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        isFacingRight = true;
        hasJustHitBrickWithHead = false;

        marioSprite = new MarioSprite(textureAtlas);

        //define mario in Box2d
        defineMario(spawnPosition);
    }

    public float getStateTimer(){
        return stateTimer;
    }
    public State getState(){ return currentState; }
    public Vector2 getB2Position(){ return b2body.getPosition(); }        // TODO: Should be redundant - Already inherits getX() and getY() from Sprite class.

    public boolean isBig(){
        return marioIsBig;
    }
    public boolean isDead(){ return marioIsDead; }
    public boolean isFacingRight(){return isFacingRight; }
    public boolean hasFallenOffMap(){
        return b2body.getPosition().y < LOWEST_SURVIAVALBE_HEIGHT;
    }

    public void setHasJustHitBrickWithHead() {
        this.hasJustHitBrickWithHead = true;
    }

    // TODO: Naming
    public void sync(MarioSyncInfo syncInfo){
        marioIsBig = syncInfo.isBig();
        redefineMario(syncInfo.getPosition());
    }

    int counter = 0;
    public void update(float dt){
        // TODO: Debug, periodically prints b2body position.
        if (counter >420){
            Gdx.app.debug("Mario", "Position*100: " + b2body.getPosition().x*MarioGame.PPM  +", " + b2body.getPosition().y*MarioGame.PPM);
            counter = 0;
        }
        counter++;

        // Update current & previous state. Update isFacingRight.
        updateState(dt);
        updateFacingDirection();

        marioSprite.update(b2body.getPosition(), currentState, stateTimer, isFacingRight, isBig());

        // Redefine when mario transitions between small and big.
        if(timeToRedefineMario){
            redefineMario(b2body.getPosition());
        }
    }

    public void makeMarioBig(){
        if (!marioIsBig ) {
            marioIsBig = true;
            runGrowAnimation = true;
            timeToRedefineMario = true;
        }
    }

    // TODO: Will probably merge this method with the public killMario Method.
    public void kill() {

        if (!marioIsDead) {
            marioIsDead = true;

            Filter filter = new Filter();
            filter.maskBits = MarioGame.NOTHING_BIT;

            for (Fixture fixture : b2body.getFixtureList()) {
                fixture.setFilterData(filter);
            }
            b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
        }
    }

    public void jump(){
        if (currentState != State.JUMPING && currentState != State.GROWING) {
            b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
            currentState = State.JUMPING;
        }
    }

    // TODO: Experimental -
    // When hitting enemies head.
    public void smallJump(){
        Gdx.app.debug("Mario smallJump occured.", "");
//        b2body.applyLinearImpulse(new Vector2(0, 2f), b2body.getWorldCenter(), true);
        currentState = State.JUMPING;
        b2body.setLinearVelocity(b2body.getLinearVelocity().x, 1.5f);
//        if (currentState != State.JUMPING && currentState != State.GROWING) {
//            b2body.applyLinearImpulse(new Vector2(0, 1f), b2body.getWorldCenter(), true);
//            currentState = State.JUMPING;
//        }
    }

    public void moveRight(){
        if (b2body.getLinearVelocity().x <= 2)
            b2body.applyLinearImpulse(new Vector2(0.1f, 0), b2body.getWorldCenter(), true);
    }
    public void moveLeft(){
        if (b2body.getLinearVelocity().x >= -2)
            b2body.applyLinearImpulse(new Vector2(-0.1f, 0), b2body.getWorldCenter(), true);
    }

    public void makeMarioSmall(){
        marioIsBig = false;
        timeToRedefineMario = true;
    }

    // TODO: No problem, right?
//    public void draw(Batch batch){
//        marioSprite.draw(batch);
//    }
    public void draw(SpriteBatch batch){
        marioSprite.draw(batch);
    }



    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // *******************************  PRIVATE METHODS  **********************************************


    private void updateFacingDirection(){
        // Mario faces in the direction he is moving.
        if(b2body.getLinearVelocity().x < 0)
            isFacingRight = false;
        else if(b2body.getLinearVelocity().x > 0)
            isFacingRight = true;
        // Else keep facing direction of previous movement.
    }

    // CurrentState is manipulated at two places outside of updateState():
    //      1. Constructor initializes current and previous to STANDING.
    //      2. jump() method conditionally sets current state to JUMPING.
    private void updateState(float dt){
        //Test to Box2D for velocity on the X and Y-Axis
        //if mario is going positive in Y-Axis he is jumping... or if he just jumped and is falling remain in jump state
        if(marioIsDead)                     // TODO: Why not manipulate state, instead of using the bool marioIsDead?
            currentState = State.DEAD;
        else if(runGrowAnimation){
            currentState = State.GROWING;
            if(marioSprite.isGrowAnimationFinished(stateTimer))
                runGrowAnimation = false;
        }
        else if((b2body.getLinearVelocity().y > 0 && currentState == State.JUMPING)
                || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING)
                || hasJustHitBrickWithHead){
            currentState = State.JUMPING;
            hasJustHitBrickWithHead = false;
        }
        //if negative in Y-Axis mario is falling
        else if ((b2body.getLinearVelocity().y < 0))
            currentState = State.FALLING;
            //if mario is positive or negative in the X axis he is running
        else if(b2body.getLinearVelocity().x != 0)
            currentState = State.RUNNING;
            //if none of these return then he must be standing (Or hitting a brick with his head..)
        else
            currentState = State.STANDING;


        // If state has not changed, increment stateTimer.
        if (currentState == previousState)
            stateTimer += dt;
            // Else reset stateTimer and update previousState
        else {
            Gdx.app.debug("UpdateState", "State changed from: " + previousState + "\tto: " + currentState + "\tstateTimer: " + stateTimer);
            previousState = currentState;
            stateTimer = 0;
        }
    }



    // TODO: https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/physics/box2d/Shape.html
    // TODO: NOTE: YOU NEED TO DISPOSE SHAPES YOU CREATED YOURSELF AFTER YOU NO LONGER USE THEM! E.g. after calling body.createFixture();
    private void defineMario(Vector2 position){
//        Gdx.app.debug("defineMario(Vector2 position)", "Position*100: " + position.x*MarioGame.PPM  +", " +position.y*MarioGame.PPM);
        createB2body(position);
        FixtureDef fixtureDef = createFixtureDef();
        b2body.createFixture(fixtureDef).setUserData(this);
        FixtureDef fixtureDefHead = createFixtureDefHead();
        b2body.createFixture(fixtureDefHead).setUserData(this);
        // TODO Test:
        fixtureDef.shape.dispose();
    }

    private void redefineMario(Vector2 position){
        b2world.destroyBody(b2body);
        defineMario(position);

        timeToRedefineMario = false;
    }

    private void createB2body(Vector2 position){
        BodyDef bdef = new BodyDef();
        bdef.position.set(position.x, position.y);
        bdef.type = BodyDef.BodyType.DynamicBody;

        b2body = b2world.createBody(bdef);
    }

    private FixtureDef createFixtureDef(){
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape;

        if (marioIsBig)
            shape = MarioShape.BIG_MARIO_SHAPE.getShape();
        else
            shape = MarioShape.SMALL_MARIO_SHAPE.getShape();

        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = MarioGame.MARIO_BIT;

        // TODO: Testing (Also gather all collision masks at single place)
        fixtureDef.filter.maskBits =
                        MarioGame.GROUND_BIT |
                        MarioGame.COIN_BIT |
                        MarioGame.BRICK_BIT |
                        MarioGame.ENEMY_BIT |
                        MarioGame.ENEMY_HEAD_BIT |
                        MarioGame.ITEM_BIT |
                        MarioGame.PIPE_BIT |
                        MarioGame.WIN_CONDITION_BIT;

        return fixtureDef;
    }

    // Create head shape: A line slightly (2/PPM) above body shape. Adjust height according to marioIsBig.
    private FixtureDef createFixtureDefHead(){
        FixtureDef fixtureDef = new FixtureDef();
        EdgeShape headShape = new EdgeShape();

        Vector2 vertex1;
        Vector2 vertex2;
        float middlePosX = B2BODY_HEAD_WIDTH/2;

        if (marioIsBig){
            vertex1 = new Vector2(-middlePosX/2, BIG_MARIO_SHAPE_HEIGHT + 2/MarioGame.PPM);
            vertex2 = new Vector2(middlePosX/2, BIG_MARIO_SHAPE_HEIGHT + 2/MarioGame.PPM);
        }
        else{
            vertex1 = new Vector2(-middlePosX/2, SMALL_MARIO_SHAPE_HEIGHT + 2/MarioGame.PPM);
            vertex2 = new Vector2(middlePosX/2, SMALL_MARIO_SHAPE_HEIGHT + 2/MarioGame.PPM);
        }

        headShape.set(vertex1, vertex2);

        fixtureDef.shape = headShape;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = MarioGame.MARIO_HEAD_BIT;

        fixtureDef.filter.maskBits =
                        MarioGame.COIN_BIT |
                        MarioGame.BRICK_BIT;

        return fixtureDef;
    }


}

