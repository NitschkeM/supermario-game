package com.nitschke.supermario.Level;


// Effective Java, p.167: Use enums any time you need a set of constants whose members are known at compile time.

import com.badlogic.gdx.math.Vector2;
import com.nitschke.supermario.MarioGame;

public enum LevelData {

    // Different levels can use the same TiledMap.
    LEVEL_1(TiledMap.TILED_MAP_1.filePath, new Vector2(32 / MarioGame.PPM, 32 / MarioGame.PPM)),
    LEVEL_2(TiledMap.TILED_MAP_2.filePath, new Vector2(32 / MarioGame.PPM, 32 / MarioGame.PPM)),
    LEVEL_3(TiledMap.TILED_MAP_3.filePath, new Vector2(32 / MarioGame.PPM, 32 / MarioGame.PPM));

    LevelData(String filePath, Vector2 spawnPosition) {
        this.tiledMapFilePath = filePath;
        this.defaultPlayerSpawnPosition = spawnPosition;
    }

    // TODO: Add a default spawnpoint for each level, then: If null --> This is a sublevel --> Express with isMainLevel isSubLevel in Level class?
    // Used as the players spawn position when a level is entered from the MenuScreen(I.e. not from a pipe).
    // Also used by Levels to determine whether they are viable MainLevels (Can be entered from MenuScreen) or strictly function as sub-levels (Only entered through pipes).
    private final Vector2 defaultPlayerSpawnPosition;
    private final String tiledMapFilePath;
    private LD_PI[] targetPipes;

    Vector2 getDefaultPlayerSpawnPosition(){return defaultPlayerSpawnPosition;}
    String getTiledMapFilePath() { return tiledMapFilePath; }
    LD_PI[] getTargetPipes() { return targetPipes; }

    // Define to which Level each pipe for a given Level leads.
    // Note result: The same pipe on a given TiledMap, can lead to a different sub-level when assigned to a different Level.    [L_1: P_1--> L_X] and [L_2: P_1--> L_Y]
    static {
        // LEVEL_1 has 8 pipes and 5 sub-levels.
        LEVEL_1.targetPipes = new LD_PI[] {
                new LD_PI(LEVEL_1, 1),
                new LD_PI(LEVEL_2, 1),
                null,
                new LD_PI(LEVEL_2, 0),
                new LD_PI(LEVEL_1, 5),
                new LD_PI(LEVEL_2, 1),
                null,
                new LD_PI(LEVEL_1, 0),
        };
        // LEVEL_2 has 2 pipes and 2 sub-levels.
        LEVEL_2.targetPipes = new LD_PI[] {
                new LD_PI(LEVEL_1, 3),
                new LD_PI(LEVEL_2, 0)
        };
        // LEVEL_3 has 14 pipes and 12 sub-levels.
        LEVEL_3.targetPipes = new LD_PI[] {
                new LD_PI(LEVEL_3, 1),      // Next pipe in row.
                new LD_PI(LEVEL_3, 2),      // Next pipe in row.
                new LD_PI(LEVEL_3, 3),      // Next pipe in row.
                new LD_PI(LEVEL_3, 0),      // First pipe in row.
                new LD_PI(LEVEL_3, 11),     // To Coins and enemies     (Fifth pipe, index 4)
                new LD_PI(LEVEL_3, 13),     // to Win flag              (Sixth pipe, index 4)
                null,
                new LD_PI(LEVEL_1, 0),      // To Level 1
                new LD_PI(LEVEL_2, 0),      // To Level 2          9'th pipe (index 8), last in row at start.
                new LD_PI(LEVEL_3, 0),      // Rest of pipes goes to 1'st pipe.
                new LD_PI(LEVEL_3, 0),
                new LD_PI(LEVEL_3, 0),
                new LD_PI(LEVEL_3, 0),
                new LD_PI(LEVEL_3, 0),
        };
    }

    // Define file path to tiled maps only once.
    // TODO: Change name to something not identical to badlogic...
    private enum TiledMap {
        TILED_MAP_1("level1.tmx"),
        TILED_MAP_2("level2.tmx"),
        TILED_MAP_3("tile-map-3.tmx");

        private final String filePath;

        TiledMap(String filePath) {this.filePath = filePath; }
    }
}

// Links LevelData and PipeIndexes.
class LD_PI{
    private LevelData levelDataOfTarget;    // TODO: final? Can't be changed anyway?..
    private int indexOfTargetPipe;

    LD_PI(LevelData levelDataOfTarget, int indexOfTargetPipe){
        this.levelDataOfTarget = levelDataOfTarget;
        this.indexOfTargetPipe = indexOfTargetPipe;
    }

    public int getIndexOfTargetPipe() {
        return indexOfTargetPipe;
    }

    public LevelData getLevelDataOfTarget() {
        return levelDataOfTarget;
    }
}