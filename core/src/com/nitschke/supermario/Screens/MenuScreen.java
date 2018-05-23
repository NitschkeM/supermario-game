package com.nitschke.supermario.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nitschke.supermario.MarioGame;


public class MenuScreen extends BaseScreen {

    private Viewport viewport;
    private Stage stage;

    private BitmapFont font;
    private TextureAtlas buttonsAtlas;
    private Skin buttonSkin;

    private final float btnHeight = 30f;
    private final float btnWidth = 3.75f * btnHeight;


    public MenuScreen(MarioGame game){
        super(game);
        viewport = new FitViewport(MarioGame.V_WIDTH, MarioGame.V_HEIGHT, new OrthographicCamera());
        initialize();
    }

    private void initialize() {
        // Initialize resources and create the stage.
        initializeReserouces();
        stage = new Stage(viewport, game.getSpriteBatch());

        // Create header and button styles.
        TextButton.TextButtonStyle levelBtnStyle = createButtonStyle();
        TextButton.TextButtonStyle headerSyle = createHeaderSyle();

        // Create 1 rootTable, 1 header, 3 buttons.
        Table table = createRootTable();
        TextButton headerLabel = new TextButton("Welcome to Mario Game", headerSyle);
        TextButton levelBtn0 = createLevelButton(0, levelBtnStyle);
        TextButton levelBtn1 = createLevelButton(1, levelBtnStyle);
        TextButton levelBtn2 = createLevelButton(2, levelBtnStyle);

        // Specify defaults to apply when adding items to table..
        table.defaults().expandX().spaceTop(5);    // Let cells expand horizontally, createSpace between elements.
        table.defaults().width(btnWidth);
        table.defaults().height(btnHeight);

        // Place header and buttons in table.
        table.add(headerLabel).width(2*btnWidth).height(2*btnHeight).padBottom(btnHeight/2).space(0);   // Override tableDefaults, add padding.
        table.row();
        table.row();
        table.add(levelBtn0);
        table.row();
        table.add(levelBtn1);
        table.row();
        table.add(levelBtn2);

        // Add table to stage.
        stage.addActor(table);
    }

    private void initializeReserouces(){
        // Font for button text.
        font = new BitmapFont();
        // Get images for buttons, and add them to skin.
        buttonsAtlas = new TextureAtlas("button/button.pack");
        buttonSkin = new Skin();
        buttonSkin.addRegions(buttonsAtlas);
    }

    private TextButton.TextButtonStyle createButtonStyle(){
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        // Use skin and font to create a buttonStyle.
        buttonStyle.up = buttonSkin.getDrawable("buttonOff");
        buttonStyle.down = buttonSkin.getDrawable("buttonOn");
        buttonStyle.font = font;

        return buttonStyle;
    }

    private TextButton.TextButtonStyle createHeaderSyle(){
        TextButton.TextButtonStyle headerSyle = new TextButton.TextButtonStyle();
        // Use skin and font to create a buttonStyle (for header).
        headerSyle.up = buttonSkin.getDrawable("buttonOff");
        headerSyle.font = font;

        return headerSyle;
    }

    private Table createRootTable(){
        Table table = new Table();
        table.setFillParent(true);      // The root table covers everything.

//        table.setDebug(true);         // Enable/Disable debug lines.
        return table;
    }

    private TextButton createLevelButton(final int levelIndex, TextButton.TextButtonStyle buttonStyle){
        String levelName = "Level " + (levelIndex+1);
        TextButton levelBtn = new TextButton(levelName, buttonStyle);

        levelBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.getScreenManager().changeToPlayScreen(game.getMainLevels().get(levelIndex).getPipes()[0]);
                // TODO: Remove debug
//                Gdx.app.log("Viewport", "Height: " + viewport.getScreenHeight() + ", Width: " + viewport.getScreenWidth() );
//                Gdx.app.log("Gdx.graphcs", "Height: " + Gdx.graphics.getHeight() + ", Width: " + Gdx.graphics.getWidth() );
            }
        });
        return levelBtn;
    }


    @Override
    public void show(){
        Gdx.input.setInputProcessor(this.stage);
    }
    @Override
    public void hide(){
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void resize(int width, int height){
        viewport.update(width,height);
    }

    @Override
    public void render(float dt) {
        update(dt);
        clearScreen();
        stage.draw();
    }

    @Override
    public void update(float dt) {
        stage.act();
    }

    @Override
    public void dispose() {
        stage.dispose();

        buttonsAtlas.dispose();
        buttonSkin.dispose();
        font.dispose();
    }

}
