package com.nitschke.supermario.Screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.nitschke.supermario.MarioGame;


// Sets game as member.
// provides clearScreen method.

// TODO: BaseScreen seems unnecessary... Just "overcoding" / "overdesigning", not simplifying.
public abstract class BaseScreen implements Screen {

    MarioGame game;

    //TODO: TESTING
    String tiledMapPath = "";

    BaseScreen(MarioGame game){
        this.game = game;
//        initialize();
    }

//    // TODO: TESTING
//    BaseScreen(MarioGame game, String tiledMapPath){
//        this.game = game;
//        this.tiledMapPath = tiledMapPath;
////        initialize();
//    }


    void clearScreen(){
        //Clear the game screen with Black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

//    public abstract void initialize();
    public abstract void update(float dt);


    // TODO: Consider, does not all screens call update(dt) then clearScreen? --> call super.render(dt)
    @Override
    public void render(float dt) {
        update(dt);
        // clearScreen();
        // draw?         TODO: OR let screens draw themselves for now? I have no base std. drawing system to default to it seems.
    }

    @Override
    public void show() {}
    @Override
    public void resize(int width, int height) {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {}





}
