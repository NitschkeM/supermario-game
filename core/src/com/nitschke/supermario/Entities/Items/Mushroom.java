package com.nitschke.supermario.Entities.Items;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.nitschke.supermario.MarioGame;
import com.nitschke.supermario.Screens.PlayScreen;


public class Mushroom extends Item {
    public Mushroom(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        setRegion(screen.getAtlas().findRegion("mushroom"), 0, 0, 16, 16);
        velocity = new Vector2(0.7f, 0);
    }

    @Override
    public void defineItem() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioGame.PPM);
        fdef.filter.categoryBits = MarioGame.ITEM_BIT;
        fdef.filter.maskBits = MarioGame.MARIO_BIT |
                MarioGame.GROUND_BIT |
                MarioGame.COIN_BIT |
                MarioGame.BRICK_BIT |
                MarioGame.PIPE_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
    }

//    @Override
////    public void use(Mario mario) {
//    public void use() {
//        destroy();
////        mario.grow();   // TODO: refactor out: Don't want this to know about mario. Necessary in order to remove screen from mario.
//    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if(!isDestroyed()){
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            velocity.y = b2body.getLinearVelocity().y;
            b2body.setLinearVelocity(velocity);
        }
    }
}
