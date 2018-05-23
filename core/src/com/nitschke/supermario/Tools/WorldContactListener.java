package com.nitschke.supermario.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.nitschke.supermario.MarioGame;
import com.nitschke.supermario.Screens.PlayScreen;
import com.nitschke.supermario.Entities.Enemies.Enemy;
import com.nitschke.supermario.Entities.Items.Item;
import com.nitschke.supermario.Entities.Other.FireBall;
import com.nitschke.supermario.TileObjects.StandardBrick;
import com.nitschke.supermario.TileObjects.QuestionmarkBrick;
import com.nitschke.supermario.TileObjects.Pipe;


public class WorldContactListener implements ContactListener {

    private PlayScreen playScreen;

    public WorldContactListener(PlayScreen playScreen){
        this.playScreen = playScreen;
    }

    @Override
    public void beginContact(Contact contact) {
        // This is called when two fixtures begin to overlap. This is called for sensors and non-sensors.
        // This event can only occur inside the time-step.

        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        // Combine category bits with bitwise OR.
        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        // Look for match.
        switch (cDef){
            case MarioGame.MARIO_HEAD_BIT | MarioGame.BRICK_BIT:
//                Gdx.app.debug("ContactListener", "Collision: MARIO_HEAD_BIT | BRICK_BIT");
                if(fixA.getFilterData().categoryBits == MarioGame.MARIO_HEAD_BIT)
                    playScreen.handleMarioHeadBrickCollision((StandardBrick) fixB.getUserData());
                else
                    playScreen.handleMarioHeadBrickCollision((StandardBrick) fixA.getUserData());
                break;
            case MarioGame.MARIO_HEAD_BIT | MarioGame.COIN_BIT:
//                Gdx.app.debug("ContactListener", "Collision: MARIO_HEAD_BIT | COIN_BIT");
                if(fixA.getFilterData().categoryBits == MarioGame.MARIO_HEAD_BIT)
                    playScreen.handleMarioHeadQuestionMarkBrickCollision((QuestionmarkBrick) fixB.getUserData());
                else
                    playScreen.handleMarioHeadQuestionMarkBrickCollision((QuestionmarkBrick) fixA.getUserData());
                break;
            case MarioGame.ENEMY_HEAD_BIT | MarioGame.MARIO_BIT:
                Gdx.app.debug("ContactListener", "Collision: ENEMY_HEAD_BIT | MARIO_BIT");
                if(fixA.getFilterData().categoryBits == MarioGame.ENEMY_HEAD_BIT)
                    playScreen.handleMarioEnemyHeadCollison((Enemy) fixA.getUserData());
                else
                    playScreen.handleMarioEnemyHeadCollison((Enemy) fixB.getUserData());
                break;
            case MarioGame.ENEMY_BIT | MarioGame.PIPE_BIT:
//                Gdx.app.debug("ContactListener", "Collision: ENEMY_BIT | PIPE_BIT");
                if(fixA.getFilterData().categoryBits == MarioGame.ENEMY_BIT)
                    playScreen.handleEnemyObjectCollision((Enemy) fixA.getUserData());
                else
                    playScreen.handleEnemyObjectCollision((Enemy) fixB.getUserData());
                break;
            case MarioGame.MARIO_BIT | MarioGame.ENEMY_BIT:
                Gdx.app.debug("ContactListener", "Collision: MARIO_BIT | ENEMY_BIT");
                if(fixA.getFilterData().categoryBits == MarioGame.MARIO_BIT)
                    playScreen.handleMarioEnemyCollison((Enemy) fixB.getUserData());
                else
                    playScreen.handleMarioEnemyCollison((Enemy) fixA.getUserData());
                break;
            case MarioGame.ENEMY_BIT | MarioGame.ENEMY_BIT:
//                Gdx.app.debug("ContactListener", "Collision: ENEMY_BIT | ENEMY_BIT");
                playScreen.handleEnemyEnemyCollision((Enemy) fixA.getUserData(), (Enemy) fixB.getUserData());
                break;
            case MarioGame.ITEM_BIT | MarioGame.PIPE_BIT:
//                Gdx.app.debug("ContactListener", "Collision: ITEM_BIT | PIPE_BIT");
                if(fixA.getFilterData().categoryBits == MarioGame.ITEM_BIT)
                    ((Item)fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Item)fixB.getUserData()).reverseVelocity(true, false);
                break;
            case MarioGame.ITEM_BIT | MarioGame.MARIO_BIT:
//                Gdx.app.debug("ContactListener", "Collision: ITEM_BIT | MARIO_BIT");
                if(fixA.getFilterData().categoryBits == MarioGame.ITEM_BIT)
                    playScreen.handleMarioItemCollision((Item) fixA.getUserData());
                else
                    playScreen.handleMarioItemCollision((Item) fixB.getUserData());
                break;
            case MarioGame.FIREBALL_BIT | MarioGame.PIPE_BIT:
//                Gdx.app.debug("ContactListener", "Collision: FIREBALL_BIT | PIPE_BIT");
                if(fixA.getFilterData().categoryBits == MarioGame.FIREBALL_BIT)
                    ((FireBall)fixA.getUserData()).setToDestroy();
                else
                    ((FireBall)fixB.getUserData()).setToDestroy();
                break;
            // TODO: TESTING
            case MarioGame.MARIO_BIT | MarioGame.PIPE_BIT:
//              Gdx.app.debug("ContactListener", "Collision: MARIO_BIT | PIPE_BIT");
                if(fixA.getFilterData().categoryBits == MarioGame.MARIO_BIT)
                    playScreen.handleMarioPipeCollision((Pipe) fixB.getUserData());
                else
                    playScreen.handleMarioPipeCollision((Pipe) fixA.getUserData());
                break;
            case MarioGame.MARIO_BIT | MarioGame.WIN_CONDITION_BIT:
//              Gdx.app.debug("ContactListener", "Collision: MARIO_BIT | WIN_CONDITION_BIT");
                playScreen.playerTouchedWinObject();
                break;


        }
    }

    @Override
    public void endContact(Contact contact) {
        // This is called when two fixtures cease to overlap. This is called for sensors and non-sensors.
        // This may be called when a body is destroyed This event can only occur inside the time-step.
        // TODO: TESTING
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef){
            case MarioGame.MARIO_BIT | MarioGame.PIPE_BIT:
//                Gdx.app.debug("ContactListener", "CollisionEND: MARIO_BIT | PIPE_BIT");
                if(fixA.getFilterData().categoryBits == MarioGame.MARIO_BIT)
                    playScreen.handleMarioPipeCollisionEnd((Pipe)fixB.getUserData());
                else
                    playScreen.handleMarioPipeCollisionEnd((Pipe)fixA.getUserData());
                break;
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}


// Box2D Manual:
// - There are different kinds of contacts for managing different kinds of fixtures. (E.g.: Polygon-Polygon collision, Circle-Circle Collision).
//      Terminology:
//          Contact point:      Where two shapes touch.
//          Contact normal:     A unit vector pointing from one shape to another. By convention, the normal points from Fixture A to Fixture B.
//          Contact manifold:   Contact between two convex polygons may generate up to two contact points. Both of these use the same normal,
//                                  so they are grouped into a contact manifold, which is an approximation of a continuous region of contact.
//          Normal impulse:     The normal force is the force applied at a contact point to prevent the shapes from penetrating.
//                                  For convinience, Box2D works with impulses. The normal impulse is just the normal force multiplied by the the time step.
//          Tangent impulse:    See manual.
//          Contact ids:        See manual. (Box2D uses contact ids to match contact points across time steps)
//
//
// Contacts are objects created by Box2D to manage collision between two fixtures. (Box2D manual).
// Relevant, or C++ only?:
//      CAUTION:    Do not keep a reference to the pointers sent to b2ContactListener. Instead make a
//                  deep copy of the contact point data into your own buffer. The example below shows one way of doing this (p.52).