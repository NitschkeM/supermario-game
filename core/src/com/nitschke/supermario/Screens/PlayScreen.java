package com.nitschke.supermario.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nitschke.supermario.MarioGame;
import com.nitschke.supermario.Scenes.Hud;
import com.nitschke.supermario.Entities.Enemies.Enemy;
import com.nitschke.supermario.Entities.Enemies.Goomba;
import com.nitschke.supermario.Entities.Enemies.Turtle;
import com.nitschke.supermario.Entities.Items.Item;
import com.nitschke.supermario.Entities.Items.ItemDef;
import com.nitschke.supermario.Entities.Items.Mushroom;
import com.nitschke.supermario.Entities.Mario;
import com.nitschke.supermario.Entities.MarioSyncInfo;
import com.nitschke.supermario.Entities.Other.FireBall;
import com.nitschke.supermario.TileObjects.StandardBrick;
import com.nitschke.supermario.TileObjects.QuestionmarkBrick;
import com.nitschke.supermario.TileObjects.Pipe;
import com.nitschke.supermario.Tools.B2WorldCreator;
import com.nitschke.supermario.Level.Level;
import com.nitschke.supermario.Tools.ScreenManager;
import com.nitschke.supermario.Tools.WorldContactListener;

import java.util.concurrent.LinkedBlockingQueue;


public class PlayScreen extends BaseScreen {
    static final float ENEMY_ACTIVATION_RANGE = 224f / MarioGame.PPM;


    private TextureAtlas textureAtlas;

    //basic playscreen variables
    private OrthographicCamera gamecam;
    private Viewport gameViewport;
    private Hud hud;

    //Tiled map variables
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;

    //Box2d variables
    private World b2World;
    private Box2DDebugRenderer b2DebugRenderer;
    private B2WorldCreator b2WorldCreator;          // TODO: b2WorldCreator is assigned but never accessed.

    //Player
    private Mario mario;
    private boolean playerHasWon;


    private Array<Goomba> goombas;
    private Array<Turtle> turtles;
    private Array<FireBall> fireballs;
    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;
    // Used to destroy b2bodies without an associated update method. (I.e. StandardBricks)
    private LinkedBlockingQueue<Body> bodiesToDestroy;

    // Used to travel through pipes to other levels.
    private Pipe contactPipe = null;
    private boolean marioIsInContactWithPipe = false;
    private Vector2 marioSpawnPosition;
    private Level level;


    // Used when entering a Level from MenuScreen (Not through a pipe).
    public PlayScreen(MarioGame game, Level level) {
        super(game);
        this.level = level;
        this.marioSpawnPosition = level.getDefaultSpawnPosition();
        initialize();
    }

    // Used when entering a Level through a pipe (From another PlayScreen).
    public PlayScreen(MarioGame game, Pipe targetPipe) {
        super(game);
        this.level = targetPipe.getLevel();
        this.marioSpawnPosition = new Vector2(targetPipe.getB2Position().x, targetPipe.getB2Position().y + 4); // TODO: Spawning 5 units above pipe position.
        initialize();
    }

    private void initialize() {
        playerHasWon = false;


        goombas = new Array<Goomba>();
        turtles = new Array<Turtle>();
        fireballs = new Array<FireBall>();
        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();
        bodiesToDestroy = new LinkedBlockingQueue<Body>();

        textureAtlas = game.getTextureAtlas();
//        textureAtlas = new TextureAtlas("Mario_and_Enemies.pack");

        //create cam used to follow mario through cam world
        gamecam = new OrthographicCamera();

        //create a FitViewport to maintain virtual aspect ratio despite screen size
        gameViewport = new FitViewport(MarioGame.V_WIDTH / MarioGame.PPM, MarioGame.V_HEIGHT / MarioGame.PPM, gamecam);

        //create our game HUD for scores/timers/level info
        hud = game.getScreenManager().getHud();
//        hud = new Hud(game.getSpriteBatch());

        //Load our map and setup our map renderer
        TmxMapLoader tmxMapLoader = new TmxMapLoader();
        tiledMap = tmxMapLoader.load(level.getTiledMapFilePath());
        orthogonalTiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1 / MarioGame.PPM);  // TODO: How does this work? https://youtu.be/knF6o8nYAMM?list=PLZm85UZQLd2SXQzsF-a0-pPF6IWDDdrXt&t=322

        //initially set our gamcam to be centered correctly at the start of of map
        gamecam.position.set(gameViewport.getWorldWidth() / 2, gameViewport.getWorldHeight() / 2, 0);

        //create our Box2D world, setting no gravity in X, -10 gravity in Y, and allow bodies to sleep
        b2World = new World(new Vector2(0, -10), true);
        //allows for debug lines of our box2d world.
        b2DebugRenderer = new Box2DDebugRenderer();

        // Adds objects to b2world. Fills up goombas and turtles arrays.
        b2WorldCreator = new B2WorldCreator(this);
        // Add pipes to world as well.
        for (Pipe pipe : level.getPipes()) {
            pipe.addPipeToWorld(this.b2World);
        }

        //create mario in our game world
        mario = new Mario(b2World, textureAtlas, marioSpawnPosition);

        b2World.setContactListener(new WorldContactListener(this));

        // TODO: Activate marioMusic
        game.getAssetManager().playMarioMusic();
    }

    @Override
    public void render(float dt) {
        //separate our update logic from render
        update(dt);                                 // NOTE: https://github.com/libgdx/libgdx/wiki/Box2d recommends calling world.step @ END of render method of Game class.
//                                                  // ALSO: recommends to render all graphics before the physics timestep is done (otherwise it'll be out of sync).
        clearScreen();

        //render our game map
        orthogonalTiledMapRenderer.render();

        //renderer our Box2DDebugLines
        b2DebugRenderer.render(b2World, gamecam.combined);

        //takes 1 step in the physics simulation(60 times per second) ******
        b2World.step(1 / 60f, 6, 2);
        // You should always process the contact points immediately after the time step (Box2D manual).

        game.getSpriteBatch().setProjectionMatrix(gamecam.combined);
        game.getSpriteBatch().begin();
        mario.draw(game.getSpriteBatch());

        for (Goomba goomba : goombas)
            goomba.draw(game.getSpriteBatch());
        for (Turtle turtle : turtles)
            turtle.draw(game.getSpriteBatch());
        for (Item item : items)
            item.draw(game.getSpriteBatch());
        for (FireBall ball : fireballs)
            ball.draw(game.getSpriteBatch());
        game.getSpriteBatch().end();

        //Set our batch to now draw what the Hud camera sees.
        game.getSpriteBatch().setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        // TODO: Feels like this is an update method rather than a render method.
        if (gameOver()) {
            game.getScreenManager().changeToNonPlayScreen(ScreenManager.ScreenType.GameOverScreen);
//            dispose();
        } else if (playerHasWon) {
            game.getAssetManager().stopMarioMusic();
            game.getScreenManager().changeToNonPlayScreen(ScreenManager.ScreenType.WinScreen);
        }
    }

    public void update(float dt) {
        //handle user input first
        handleInput();
        handleSpawningItems();

        updateMario(dt);

        updateEnemies(dt);
        updateItems(dt);
        updateFireballs(dt);

        updateHud(dt);

        updateGamecam();

        destroyBodies();

        //tell our renderer to draw only what our camera can see in our game world.
        orthogonalTiledMapRenderer.setView(gamecam);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // *******************************  UPDATE METHODS *********************************************
    // TODO: So spawn only a single item in each loop? Why? While(notEmpty?)
    private void handleSpawningItems() {
        if (!itemsToSpawn.isEmpty()) {
            ItemDef idef = itemsToSpawn.poll();
            if (idef.type == Mushroom.class) {
                items.add(new Mushroom(this, idef.position.x, idef.position.y));
            }
        }
    }

    private void destroyBodies() {
        while (bodiesToDestroy.peek() != null) {
            b2World.destroyBody(bodiesToDestroy.poll());
        }
    }

//    public void queueDestroyBody(Body body){
//        bodiesToDestroy.add(body);
//    }

    private void handleInput() {
        //control our mario using immediate impulses
        if (mario.getState() != Mario.State.DEAD) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP))
                mario.jump();
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
                mario.moveRight();
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
                mario.moveLeft();
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
                createFireball();


            // TODO: TESTING
            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                if (marioIsInContactWithPipe && contactPipe.hasTargetPipe()) {
                    game.getScreenManager().changeToPlayScreen(contactPipe.getTargetPipe());
                }
            }
        }
    }

    private void updateMario(float dt) {
        if (!mario.isDead() && marioMustDie()) {
            killMario();
        }
        mario.update(dt);
    }

    // TODO: Remove dead enemies from arrays.
    private void updateEnemies(float dt) {
        for (Goomba goomba : goombas) {
            goomba.update(dt);
            if (goomba.getState() == Goomba.State.REMOVABLE)
                goombas.removeValue(goomba, true);
            else if (!goomba.isDestroyed() && (goomba.getB2Position().x < mario.getB2Position().x + ENEMY_ACTIVATION_RANGE))
                goomba.activate();
        }
        for (Turtle turtle : turtles) {
            turtle.update(dt);

            if (turtle.getB2Position().x < mario.getB2Position().x + ENEMY_ACTIVATION_RANGE)
                turtle.activate();
        }
    }

    private void updateItems(float dt) {
        for (Item item : items) {
            if (item.isDestroyed())
                items.removeValue(item, true);
            else
                item.update(dt);
        }
    }


    private void updateFireballs(float dt) {
        for (FireBall fireBall : fireballs) {
            if (fireBall.isDestroyed())
                fireballs.removeValue(fireBall, true);  // TODO: true : ==, fasle : .equals()
            else
                fireBall.update(dt);
        }
    }

    private void updateHud(float dt) {
        hud.update(dt);
    }

    private void updateGamecam() {
        //attach our gamecam to our marios.x coordinate
        if (mario.getState() != Mario.State.DEAD) {
            gamecam.position.x = mario.getB2Position().x;
        }

        //update our gamecam with correct coordinates after changes
        gamecam.update();
    }

    private boolean marioMustDie() {
        if (hud.isTimeUp())
            return true;
        else if (mario.hasFallenOffMap())
            return true;

        return false;
    }

    private void killMario() {
        mario.kill();
        game.getAssetManager().stopMarioMusic();
        game.getAssetManager().playMarioDieSound();
    }

    private void createFireball() {
        fireballs.add(new FireBall(this, mario.getB2Position().x, mario.getB2Position().y, mario.isFacingRight()));
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // *******************************  RENDERING METHODS *********************************************
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    // TODO: Feels like this is an update method rather than a render method.
    private boolean gameOver() {
        return (mario.getState() == Mario.State.DEAD && mario.getStateTimer() > 3);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // *********************************  Accessor Methods ********************************************
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    public TiledMap getMap() {
        return tiledMap;
    }
    public World getWorld() {
        return b2World;
    }
    public TextureAtlas getAtlas() {
        return textureAtlas;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // ***********************  Methods inherited from BaseScreen / Screen interface ******************
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void resize(int width, int height) {
        //updated our game viewport
        gameViewport.update(width, height);

    }

    //TODO: Consider: Are there more resources to dispose of?
    @Override
    public void dispose() {
        //dispose of all our opened resources
        tiledMap.dispose();
        orthogonalTiledMapRenderer.dispose();
        b2World.dispose();
        b2DebugRenderer.dispose();
//        hud.dispose();  // TODO: Testing.
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // ******  Methods called by ScreenManger and B2WorldCreator and CollisionHandler methods *********


    // Called from ScreenManger in order to Sync mario state across screens/levels.
    public MarioSyncInfo createMarioSyncInfo(Vector2 position) {
        return new MarioSyncInfo(position, mario.isBig());
    }

    // Used by ScreenManager to check the level loaded by this screen.
    public Level getLevel() {
        return level;
    }

    // Called when switching to this playScreen from other playScreens.
    public void syncMarioState(MarioSyncInfo syncInfo) {
        mario.sync(syncInfo);
//        mario.syncPosition(marioPosition);
    }

    // Used by B2WorldCreator when creating the tiledMap.
    public void addGoomba(Goomba goomba) {
        goombas.add(goomba);
    }
    public void addTurtle(Turtle turtle) {
        turtles.add(turtle);
    }

    // Used by handleMarioHeadQuestionMarkBrickCollision method to spawn mushrooms.
    private void spawnItem(ItemDef idef) {
        itemsToSpawn.add(idef);
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // *******************************  COLLISION HANDLER METHODS **************************************
    // These methods are all called from WorldContactListener when it detects some collision


    public void handleMarioEnemyCollison(Enemy enemy) {
        if (enemy instanceof Turtle && ((Turtle) enemy).currentState == Turtle.State.SLEEPING_SHELL) {
            if (enemy.getB2Position().x > mario.getB2Position().x) {
                ((Turtle) enemy).kickRight();
            } else {
                ((Turtle) enemy).kickLeft();
            }
        } else {
            if (mario.isBig()) {
                mario.makeMarioSmall();
                game.getAssetManager().playPowerDownSound();
            } else {
                killMario();
            }
        }
    }

    public void handleMarioEnemyHeadCollison(Enemy enemy) {
        if (enemy instanceof Goomba) {
            game.getAssetManager().playStompSound();
            ((Goomba) enemy).kill();
        } else if (enemy instanceof Turtle) {
            ((Turtle) enemy).hitOnHead(mario.getB2Position());
        }
        mario.smallJump();
    }

    public void handleEnemyObjectCollision(Enemy enemy) {
        enemy.reverseVelocity(true, false);
    }

    public void handleEnemyEnemyCollision(Enemy enemyA, Enemy enemyB) {
        enemyA.hitByEnemy(enemyB);
        enemyB.hitByEnemy(enemyA);
    }

    public void handleMarioHeadQuestionMarkBrickCollision(QuestionmarkBrick questionmarkBrick) {
        if (questionmarkBrick.isEmpty())
            game.getAssetManager().playBumpSound();
        else {
            if (questionmarkBrick.isMushroom()) {
                spawnItem(new ItemDef(new Vector2(questionmarkBrick.getPosition().x, questionmarkBrick.getPosition().y + 16 / MarioGame.PPM), Mushroom.class));     //TODO: Why is not X position adjusted to PPM?
                game.getAssetManager().playPowerUpSpawnSound();
            } else {
                game.getAssetManager().playCoinSound();
            }
            questionmarkBrick.setToBlankCoin();
            hud.addScore(100);      // TODO: Hardcoded score system.
        }
        mario.setHasJustHitBrickWithHead();
    }

    public void handleMarioHeadBrickCollision(StandardBrick standardBrick) {
        if (mario.isBig()) {
            standardBrick.breakBrick();
            bodiesToDestroy.add(standardBrick.getBody());
            hud.addScore(200);  // TODO: Hardcoded score system.
            game.getAssetManager().playBreakBlockSound();
        } else {
            game.getAssetManager().playBumpSound();
        }
        mario.setHasJustHitBrickWithHead();
    }

    public void handleMarioItemCollision(Item item) {
        if (item instanceof Mushroom) {
            item.setToDestroy();
            mario.makeMarioBig();
            game.getAssetManager().playPowerUpSound();
        }
    }

    public void playerTouchedWinObject() {
        playerHasWon = true;
    }

    public void handleMarioPipeCollision(Pipe pipe) {
        this.contactPipe = pipe;
        marioIsInContactWithPipe = true;
    }

    public void handleMarioPipeCollisionEnd(Pipe pipe) {
        this.contactPipe = null;
        marioIsInContactWithPipe = false;
    }


}
