package com.nitschke.supermario.Entities.Enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.nitschke.supermario.MarioGame;


//public abstract class Enemy extends Sprite {
public abstract class Enemy  {
    World b2world;
    boolean destroyed;

    Body b2body;

    // TODO: Use to adjust difficulty.
    final Vector2 initialVelocity;
    Vector2 currentVelocity;


    Enemy(final World b2world, final Vector2 b2Position, final Vector2 enemyVelocity){
        this.b2world = b2world;

        // Set sprite position
        // TODO: SPRITE STUFF: FACTORED OUT. PLAN: Let sprite update() adjust position according to b2Body.
//        setPosition(b2Position.x, b2Position.y);
        // Create enemy
        defineEnemy(b2Position);

        // Hardcoded velocity. Functioning as default velocity for all enemies.
//        velocity = new Vector2(-1, -2);
        initialVelocity = new Vector2(enemyVelocity);
//        initialVelocity = enemyVelocity;
        currentVelocity = new Vector2(enemyVelocity);
        // Wait for playScreen to activate.
        b2body.setActive(false);
    }

    public abstract void update(float dt);
//    public void update(float dt){
//
//    }

    public abstract void hitByEnemy(Enemy enemy);

    public abstract void activate();

    public void reverseVelocity(final boolean x, final boolean y){
        if(x)
            currentVelocity.x = -currentVelocity.x;
        if(y)
            currentVelocity.y = -currentVelocity.y;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public Vector2 getB2Position(){
        if (destroyed){
            Gdx.app.error("Enemy.getB2Position()", "Called getPosition on a destroyed body, not allowed.");
            return null;
        }
        return b2body.getPosition();
    }


    private void defineEnemy(final Vector2 b2Position){
        BodyDef bodyDef = createBodyDef(b2Position);
        b2body = b2world.createBody(bodyDef);

        FixtureDef fixtureDef = createFixtureDef();
        b2body.createFixture(fixtureDef).setUserData(this);

        FixtureDef fixtureDefHead = createFixtureDefHead();     // Can reuse other fDef, but.. Clarity?
        b2body.createFixture(fixtureDefHead).setUserData(this);
    }


    private BodyDef createBodyDef(final Vector2 b2Position){
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(b2Position);
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        return bodyDef;
    }

    private FixtureDef createFixtureDef(){
        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioGame.PPM);

        fixtureDef.filter.categoryBits = MarioGame.ENEMY_BIT;
        fixtureDef.filter.maskBits = MarioGame.GROUND_BIT |
                MarioGame.COIN_BIT |
                MarioGame.BRICK_BIT |
                MarioGame.ENEMY_BIT |
                MarioGame.MARIO_BIT |
                MarioGame.PIPE_BIT;

        fixtureDef.shape = shape;

        return fixtureDef;
    }

    private FixtureDef createFixtureDefHead(){
        FixtureDef fixtureDef = new FixtureDef();

        //Create the head shape;
        PolygonShape head = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-5, 8).scl(1 / MarioGame.PPM);
        vertice[1] = new Vector2(5, 8).scl(1 / MarioGame.PPM);
//        vertice[2] = new Vector2(-5.3f, 3.3f).scl(1 / MarioGame.PPM);
//        vertice[3] = new Vector2(5.3f, 3.3f).scl(1 / MarioGame.PPM);
        vertice[2] = new Vector2(-2.3f, 3).scl(1 / MarioGame.PPM);
        vertice[3] = new Vector2(2.3f, 3).scl(1 / MarioGame.PPM);
        head.set(vertice);

        fixtureDef.shape = head;
        if (this instanceof Turtle)
            fixtureDef.restitution = 1.8f;  // TODO: Only difference between Goomba and turtle, set to 1.8f in turtle.
        else
            fixtureDef.restitution = 0.5f;

        fixtureDef.filter.categoryBits = MarioGame.ENEMY_HEAD_BIT;
        fixtureDef.filter.maskBits =
                MarioGame.MARIO_BIT;

        return fixtureDef;
    }







}

// Effective Java, p.44: "The Liskov substitution principle says that any important property of a type should also
//  hold for all of its subtypes so that any method written for the type should work equally well on its subtypes."