// MarioSprite.Java:    Used as member of Mario class.
//

package com.nitschke.supermario.Entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.nitschke.supermario.MarioGame;

class MarioSprite {

    private Sprite sprite;

    private static Vector2 SMALL_MARIO_SPRITE_SIZE = new Vector2(MarioGame.BRICK_SIZE);
    private static Vector2 BIG_MARIO_SPRITE_SIZE = new Vector2(1f*SMALL_MARIO_SPRITE_SIZE.x, 2f*SMALL_MARIO_SPRITE_SIZE.y);

    private TextureRegion marioStandTR;
    private TextureRegion marioJumpTR;
    private TextureRegion marioDeadTR;
    private TextureRegion bigMarioStandTR;
    private TextureRegion bigMarioJumpTR;
    private Animation<TextureRegion> littleMarioRunAnimation;
    private Animation<TextureRegion> bigMarioRunAnimation;
    private Animation<TextureRegion> growMarioAnimation;


    MarioSprite(TextureAtlas textureAtlas){
        sprite = new Sprite();
        setupTextureRegionsAndAnimations(textureAtlas);
    }

    boolean isGrowAnimationFinished(float stateTimer){
        return growMarioAnimation.isAnimationFinished(stateTimer);
    }

    void update(final Vector2 b2Position, final com.nitschke.supermario.Entities.Mario.State currentState, final float stateTimer, final boolean isFacingRight, final boolean isBig){
        updatePosition(b2Position);
        updateBounds(isBig);  // Uses position.
        updateTextureRegion(currentState, stateTimer, isFacingRight, isBig);
    }

    void draw(Batch batch){
        sprite.draw(batch);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // *******************************  PRIVATE METHODS  **********************************************

    private void updateBounds(final boolean marioIsBig){
        if (marioIsBig)
            sprite.setBounds(sprite.getX(), sprite.getY(), BIG_MARIO_SPRITE_SIZE.x, BIG_MARIO_SPRITE_SIZE.y);
        else
            sprite.setBounds(sprite.getX(), sprite.getY(), SMALL_MARIO_SPRITE_SIZE.x, SMALL_MARIO_SPRITE_SIZE.y);
    }

    private void updatePosition(final Vector2 b2bodyPosition){
        // Update sprite position according to b2body position.
        // X: Center according to b2body.
        // Y: Subtract a tiny value because b2body hovers slightly above ground (due to physics engine implementation).
        sprite.setPosition(b2bodyPosition.x - (sprite.getWidth() / 2), b2bodyPosition.y - (1.5f/ MarioGame.PPM));
    }

    private void updateTextureRegion(final com.nitschke.supermario.Entities.Mario.State currentState, final float stateTimer, final boolean isFacingRight, final boolean marioIsBig){
        // Create appropriate region according to currentState
        TextureRegion textureRegion = getAppropriateTextureRegion(currentState, stateTimer, marioIsBig);

        // If mario is facing left and the texture is not facing left... flip it.
        if(!isFacingRight && !textureRegion.isFlipX()){
            textureRegion.flip(true, false);
        }
        // Else if mario is facing right and the texture is not facing right... flip it.
        else if(isFacingRight && textureRegion.isFlipX()){
            textureRegion.flip(true, false);
        }

        sprite.setRegion(textureRegion);
    }

    // Uses arguments to return an appropriate TextureRegion.
    private TextureRegion getAppropriateTextureRegion(final com.nitschke.supermario.Entities.Mario.State currentState, final float stateTimer, final boolean marioIsBig){
        TextureRegion textureRegion;

        switch(currentState){
            case DEAD:
                textureRegion = marioDeadTR;
                break;
            case GROWING:
                textureRegion = growMarioAnimation.getKeyFrame(stateTimer);
                break;
            case JUMPING:
                textureRegion = marioIsBig ? bigMarioJumpTR : marioJumpTR;
                break;
            case RUNNING:
                textureRegion = marioIsBig ? bigMarioRunAnimation.getKeyFrame(stateTimer, true) : littleMarioRunAnimation.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                textureRegion = marioIsBig ? bigMarioStandTR : marioStandTR;
                break;
        }
        return textureRegion;
    }

    private void setupTextureRegionsAndAnimations(TextureAtlas textureAtlas) {
        Array<TextureRegion> frames = new Array<TextureRegion>();

        //get little mario run animation frames and add them to marioRun Animation
        for (int i = 1; i < 4; i++)
            frames.add(new TextureRegion(textureAtlas.findRegion("little_mario"), i * 16, 0, 16, 16));
        littleMarioRunAnimation = new Animation(0.1f, frames);

        frames.clear();

        //get big mario run animation frames and add them to bigMarioRun Animation
        for (int i = 1; i < 4; i++)
            frames.add(new TextureRegion(textureAtlas.findRegion("big_mario"), i * 16, 0, 16, 32));
        bigMarioRunAnimation = new Animation(0.1f, frames);

        frames.clear();

        //get set animation frames from growing mario
        frames.add(new TextureRegion(textureAtlas.findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(textureAtlas.findRegion("big_mario"), 0, 0, 16, 32));
        frames.add(new TextureRegion(textureAtlas.findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(textureAtlas.findRegion("big_mario"), 0, 0, 16, 32));
        growMarioAnimation = new Animation(0.2f, frames);

        //get jump animation frames and add them to marioJump Animation
        marioJumpTR = new TextureRegion(textureAtlas.findRegion("little_mario"), 80, 0, 16, 16);
        bigMarioJumpTR = new TextureRegion(textureAtlas.findRegion("big_mario"), 80, 0, 16, 32);

        //create texture region for mario standing
        marioStandTR = new TextureRegion(textureAtlas.findRegion("little_mario"), 0, 0, 16, 16);
        bigMarioStandTR = new TextureRegion(textureAtlas.findRegion("big_mario"), 0, 0, 16, 32);

        //create dead mario texture region
        marioDeadTR = new TextureRegion(textureAtlas.findRegion("little_mario"), 96, 0, 16, 16);
    }


}