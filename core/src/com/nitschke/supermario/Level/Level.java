package com.nitschke.supermario.Level;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.nitschke.supermario.TileObjects.Pipe;

import java.util.ArrayList;
import java.util.Comparator;


public class Level {
    private LevelData levelData;

    private String tiledMapFilePath;

//    private Array<Pipe> pipes;
    private Pipe[] pipes;
    private Vector2 defaultSpawnPosition = null;


    public Level(LevelData levelData){
        this.levelData = levelData;
        this.tiledMapFilePath = levelData.getTiledMapFilePath();
//        this.pipes = new Array<Pipe>();
        this.pipes = new Pipe[levelData.getTargetPipes().length];
        this.defaultSpawnPosition = levelData.getDefaultPlayerSpawnPosition();  // Is null if not MainLevel.
    }

    public LevelData getLevelData(){return levelData;}

    public boolean isMainLevel(){ return defaultSpawnPosition != null; }

    public Vector2 getDefaultSpawnPosition(){ return defaultSpawnPosition;}

//    public Array<Pipe> getPipes() {
//        return pipes;
//    }
    public Pipe[] getPipes(){return pipes;}

    public String getTiledMapFilePath(){
        return tiledMapFilePath;
    }

    // TODO: Called once only. --> Implement check / condition.
    public void generatePipes(){
        TmxMapLoader tmxMapLoader = new TmxMapLoader();
        TiledMap tiledMap = tmxMapLoader.load(tiledMapFilePath);

        // Sort pipes according to position before adding to level because index matters.
        Array<MapObject> pipeObjects = new Array<MapObject>();
        for (MapObject object : tiledMap.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            pipeObjects.add(object);
        }

        Comparator<MapObject> comparator = new Comparator<MapObject>() {
            @Override
            public int compare(MapObject pipeObject1, MapObject pipeObject2) {
                float xPos1 = ((RectangleMapObject)pipeObject1).getRectangle().getX();
                float xPos2 = ((RectangleMapObject)pipeObject2).getRectangle().getX();
                return Float.compare(xPos1,xPos2);
            }
        };
        pipeObjects.sort(comparator);


        // Generate pipes.
        int currentPipeIndex = 0;
//        for (MapObject object : tiledMap.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
        for (MapObject object : pipeObjects){
            Pipe pipe = new Pipe(object, this, currentPipeIndex);
//            pipes.add(pipe);
            pipes[currentPipeIndex] = pipe;
            currentPipeIndex++;

//            Gdx.app.debug("Generated pipe: ", "Xpos: " + ((RectangleMapObject)object).getRectangle().getX());

        }
        tiledMap.dispose();
    }

    // TODO: Called once only. --> Implement check / condition.
//    public void connectPipes(Level[] levels){
//    public void connectPipes(Array<Level> levels){
    public void connectPipes(ArrayList<Level> levels){
        int currentPipeIndex = 0;
        for (Pipe pipe : pipes){
            // If pipe leads somewhere.
            if (levelData.getTargetPipes()[currentPipeIndex] != null){

                LevelData levelDataOfTarget = levelData.getTargetPipes()[currentPipeIndex].getLevelDataOfTarget();
                int indexOfTargetPipe       = levelData.getTargetPipes()[currentPipeIndex].getIndexOfTargetPipe();

                for (Level level : levels){
//                    if (level.levelData == levelDataOfTarget){
                    if (level.getLevelData() == levelDataOfTarget){
                        pipe.setTargetPipe(level.getPipes()[indexOfTargetPipe]);
//                        pipe.setTargetPipe(level.getPipes().get(indexOfTargetPipe));
                    }
                }
            }
            currentPipeIndex++;
        }
    }

}