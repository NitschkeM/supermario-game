package com.nitschke.supermario;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.nitschke.supermario.Tools.B2dAssetManager;
import com.nitschke.supermario.Level.Level;
import com.nitschke.supermario.Level.LevelData;
import com.nitschke.supermario.Tools.ScreenManager;

import java.util.ArrayList;


public class MarioGame extends Game {
	//Virtual Screen size
	public static final int V_WIDTH = 400;
	public static final int V_HEIGHT = 208;

	// Box2D Scale(Pixels Per Meter),
	// Methods passing around positions should name scaled position with a b2 prefix, e.g.: setB2Position(Vector2 b2Position) 	// TODO.
	public static final float PPM = 100f;
	public static final Vector2 BRICK_SIZE = new Vector2(16f/PPM, 16f/PPM);


	// TODO: 	Consider: Effective Java Item 36: "Use EnumSet instead of bit fields"
	// TODO:	Effective Java, p.167: Good use of enums: Use enums any time you need a set of constants whose members are known at compile time.
	//Box2D Collision Bits
	public static final short NOTHING_BIT = 0;
	public static final short GROUND_BIT = 1;
	public static final short MARIO_BIT = 2;
	public static final short BRICK_BIT = 4;
	public static final short COIN_BIT = 8;
	public static final short DESTROYED_BIT = 16;
	public static final short WIN_CONDITION_BIT = 32;
	public static final short ENEMY_BIT = 64;
	public static final short ENEMY_HEAD_BIT = 128;
	public static final short ITEM_BIT = 256;
	public static final short MARIO_HEAD_BIT = 512;
	public static final short FIREBALL_BIT = 1024;
	public static final short PIPE_BIT = 2048;
//	public static final short OBJECT_BIT = 4096;
	// PIPE_HEAD_BIT


	private TextureAtlas textureAtlas;
	private B2dAssetManager assetManager;
	private ScreenManager screenManager;

	private SpriteBatch spriteBatch;

	private ArrayList<Level> levels;



	@Override
	public void create () {
		// Un-comment activate Gdx.app.debug() methods.
//		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		spriteBatch = new SpriteBatch();
		screenManager = new ScreenManager(this);
		textureAtlas = new TextureAtlas("Mario_and_Enemies.pack");
		assetManager = new B2dAssetManager();

		// Load music and sounds, then block until finished loading.
		assetManager.queueAddMusic();
		assetManager.queueAddSounds();
		assetManager.manager.finishLoading();

		initializeLevels();

		screenManager.changeToNonPlayScreen(ScreenManager.ScreenType.MenuScreen);
	}

	public ScreenManager getScreenManager(){
		return screenManager;
	}
	public B2dAssetManager getAssetManager(){
		return assetManager;
	}
	public TextureAtlas getTextureAtlas(){return textureAtlas;}
	public SpriteBatch getSpriteBatch(){return spriteBatch;}

	private void initializeLevels() {
		levels = new ArrayList<Level>();
		// Generate levels.
		for (LevelData levelData : LevelData.values()) {
			levels.add(new Level(levelData));
		}
		// Generate pipes.
		for (Level level : levels) {
			Gdx.app.debug("Generating pipes", "Level: " + level.getLevelData().name());
			level.generatePipes();
		}
		// Connect pipes.
		int i=0;
		for (Level level : levels) {
			Gdx.app.debug("\tConnecting pipes", "Level: " + level.getLevelData().name());
			level.connectPipes(levels);
		}

//		// Debug loop
//		for (Level level : levels){
//			Gdx.app.debug("\nLevel", level.getLevelData().name());
//			for (Pipe pipe : level.getPipes()){
//		Gdx.app.debug("",pipe.toString());
//			}
//		}

	}


	// Used by MenuScreen to display levels.
	public ArrayList<Level> getMainLevels(){
		return levels;
	}

	@Override
	public void dispose() {
		super.dispose();
		assetManager.dispose();
		spriteBatch.dispose();
 		textureAtlas.dispose();
	}

	@Override
	public void render () {
		super.render();
	}
}



// TODO: Destroy turtle b2bodies and remove from playScreen array.

// TODO: Implement Object layer in tiledMaps. Give the mapObjects categoryBit: OBJECT_BIT.
// 		OR: Replace Pipe layer w/ object layer. And add Pipe_Head layer: Small rectangles on top of pipes.
//		- Treat identically to PIPE_BIT in collision handling.
//		- Need Pipe layer for pipe implementation.
// 		- Need Object layer for collision handling between Entities and Objects.
//		- Need Pipe_Head bit for collision detection.

// TODO: Factor out:
//		- collision handling from playScreens.
//		- Hud out of playScreens.

// TODO: Animations
//		- Win Animation + sound.
// 		- Coin Animation.
//		- Pipe tunneling animation + sound.

// TODO: Make fireballs kill.
// 		- Add as property/key of QuestionMarkBricks (in Tiled)
// 		- Fireball state / flag for mario.
// 				- Used by inputHandler to see if createFireball is OK.
//				- Used by MarioSprite to select TextureRegions.

// TODO: Make playscreens share/pass a single instance of Mario around.
// 		- MarioSprite is ready to be shared.

// TODO: Debug enemies: If b2Postion does not change within some time, reversePosition internally.

// TODO: Remake with Ashley.




