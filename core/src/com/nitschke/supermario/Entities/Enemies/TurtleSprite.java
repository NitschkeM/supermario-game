// TurtleSprite.java -- Used by instances of the Turtle class.
//                      Holds a Sprite instance representing the graphics of a Turtle instance.

package com.nitschke.supermario.Entities.Enemies;


import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.nitschke.supermario.MarioGame;



// TODO: Destroy b2body at appropriate time, and remove from list in PlayScreen.
class TurtleSprite {

    private static final Vector2 TURTLE_SPRITE_SIZE = new Vector2(16, 24).scl(1/MarioGame.PPM);
    private final Sprite sprite;

    private final Animation<TextureRegion> walkAnimation;
    private final TextureRegion shellTR;


    TurtleSprite(TextureAtlas textureAtlas){
        sprite = new Sprite();
        sprite.setSize(TURTLE_SPRITE_SIZE.x, TURTLE_SPRITE_SIZE.y);

        walkAnimation = createWalkAnimation(textureAtlas);
        shellTR = new TextureRegion(textureAtlas.findRegion("turtle"), 64, 0, 16, 24);
    }


    void update(final Vector2 b2Position, final Turtle.State currentState, final float stateTimer, final boolean isFacingRight){
        sprite.setPosition(b2Position.x - sprite.getWidth() / 2, b2Position.y - 8 / MarioGame.PPM);

        TextureRegion textureRegion = getAppropriateTextureRegion(currentState, stateTimer, isFacingRight);

        sprite.setRegion(textureRegion);
    }

    public void draw(SpriteBatch spriteBatch){
        sprite.draw(spriteBatch);
    }

    private TextureRegion getAppropriateTextureRegion(Turtle.State currentState, final float stateTimer, final boolean isFacingRight){
        TextureRegion textureRegion;
        // TODO: TurtleSprite.selectTextureRegion
        switch (currentState){
            case MOVING_SHELL:
            case SLEEPING_SHELL:
                textureRegion = shellTR;
                break;
            case WALKING:
            default:
                textureRegion = walkAnimation.getKeyFrame(stateTimer, true);
                break;
        }

        // Note: Turtle faces opposite direction to Mario in .png file.
        // If turtle is facing left and the texture is not facing left... flip it.
        if(!isFacingRight && textureRegion.isFlipX()){
            textureRegion.flip(true, false);
        }
        // Else if turtle is facing right and the texture is not facing right... flip it.
        else if(isFacingRight && !textureRegion.isFlipX()){
            textureRegion.flip(true, false);
        }

        return textureRegion;
    }



    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // *******************************  PRIVATE METHODS  **********************************************

    private Animation<TextureRegion> createWalkAnimation(TextureAtlas textureAtlas){
        Array<TextureRegion> frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(textureAtlas.findRegion("turtle"), 0, 0, 16, 24));
        frames.add(new TextureRegion(textureAtlas.findRegion("turtle"), 16, 0, 16, 24));
        return new Animation(0.2f, frames);
    }

}
