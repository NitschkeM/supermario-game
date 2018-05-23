package com.nitschke.supermario.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nitschke.supermario.MarioGame;
import com.nitschke.supermario.Tools.ScreenManager;



public class WinScreen extends BaseScreen {


    private Stage stage;
    private int score;

    public WinScreen(MarioGame game, int score){
        super(game);
        this.score = score;

        initialize();
        System.out.println("Creating win screen, recieved score: " + score);
    }

    private void initialize() {
        Viewport viewport = new FitViewport(MarioGame.V_WIDTH, MarioGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, game.getSpriteBatch());

        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        Label winMessageLabel = new Label("Congratulations, you win.", font);
        Label scoreLabel = new Label("Score: " + score, font);
        Label playAgainLabel = new Label("Go To Menu: Click Something", font);

        table.add(winMessageLabel).expandX();
        table.row();
        table.add(scoreLabel).expandX().padTop(10f);
        table.row();
        table.add(playAgainLabel).expandX().padTop(10f);

        stage.addActor(table);
    }

    @Override
    public void update(float dt) {
        if(Gdx.input.justTouched()) {
            game.getScreenManager().changeToNonPlayScreen(ScreenManager.ScreenType.MenuScreen);
        }
    }

    @Override
    public void render(float dt) {
        update(dt);
        clearScreen();
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}
