// GoombaSprite.java -- Used by instances of the Goomba class.
//                      Holds a Sprite instance representing the graphics of a Goomba instance.

package com.nitschke.supermario.Entities.Enemies;


import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.nitschke.supermario.MarioGame;

class GoombaSprite {

    private Sprite sprite;
    private Animation<TextureRegion> walkAnimation;
    private TextureRegion deadTR;

    // TODO: Much hardcoded values here.
    GoombaSprite(TextureAtlas textureAtlas){
        sprite = new Sprite();
        createWalkAnimation(textureAtlas);
        // Create textureRegion for dead goomba.
        deadTR = new TextureRegion(textureAtlas.findRegion("goomba"), 32, 0, 16, 16);
        // Set size of the sprite.
        sprite.setSize(16 / MarioGame.PPM, 16 / MarioGame.PPM);
//        setRegion(walkAnimation.getKeyFrame(0, true));
//        setBounds(32, 32, 16 / MarioGame.PPM, 16 / MarioGame.PPM);
    }

    void useDeadTextureRegion(){
        sprite.setRegion(deadTR);
    }

    void update(final Vector2 b2Position, final float stateTimer){
        sprite.setPosition(b2Position.x - sprite.getWidth()/2, b2Position.y - sprite.getHeight()/2);
        sprite.setRegion(walkAnimation.getKeyFrame(stateTimer, true));
    }

    void draw(Batch batch){
        sprite.draw(batch);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // *******************************  PRIVATE METHODS  **********************************************

    private void createWalkAnimation(final TextureAtlas textureAtlas){
        // Create walk animation.
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for(int i = 0; i < 2; i++)
            frames.add(new TextureRegion(textureAtlas.findRegion("goomba"), i * 16, 0, 16, 16));
        walkAnimation = new Animation(0.4f, frames);
    }

}
