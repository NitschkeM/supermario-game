// Pipe class: Pipes are created up-front by the  Level class.
// Unlike other InteractiveTileObjects they must manually be added to the world with the addPipeToWorld method.
// Pipes can be accessed from a playScreen through the Level of the given screen.

package com.nitschke.supermario.TileObjects;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.nitschke.supermario.MarioGame;
import com.nitschke.supermario.Level.Level;

public class Pipe extends InteractiveTileObject {

    private Pipe targetPipe;
    private Level level;
    private int indexAssignedByLevel;

    public Pipe(MapObject object, Level level, int index) {
        super(object);

        this.indexAssignedByLevel = index;
        targetPipe = null;
        this.level = level;
    }

    public void setTargetPipe(Pipe targetPipe){
        this.targetPipe = targetPipe;
    }
    public Pipe getTargetPipe(){
        if(targetPipe == null){
            Gdx.app.error("Pipe getTargetPipe()", "Cannot call getTargetPipe when targetPipe is null");
        }
        return targetPipe;
    }
    public int getIndexAssignedByLevel(){return indexAssignedByLevel;}

    public Level getLevel() {
        return level;
    }

    public boolean hasTargetPipe(){
        return targetPipe != null;
    }

    public void addPipeToWorld(World b2world){
        Rectangle bounds = ((RectangleMapObject) mapObject).getRectangle();

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((bounds.getX() + (bounds.getWidth() / 2)) / MarioGame.PPM, (bounds.getY() + (bounds.getHeight() / 2)) / MarioGame.PPM);

        b2body = b2world.createBody(bdef);

        shape.setAsBox(bounds.getWidth() / 2 / MarioGame.PPM, bounds.getHeight() / 2 / MarioGame.PPM);
        fdef.shape = shape;
        fixture = b2body.createFixture(fdef);


        setCategoryFilter(MarioGame.PIPE_BIT);
        fixture.setUserData(this);
    }

    public Vector2 getB2Position(){
        Rectangle bounds = ((RectangleMapObject) mapObject).getRectangle();
        return new Vector2((bounds.getX() + (bounds.getWidth() / 2)) / MarioGame.PPM, (bounds.getY() + (bounds.getHeight() / 2)) / MarioGame.PPM);
    }
    public Vector2 getMapPosition(){
        Rectangle bounds = ((RectangleMapObject) mapObject).getRectangle();
        return new Vector2(bounds.getX(), bounds.getY());
    }


    // Debug method.
//    @Override
//    public String toString(){
//        String level = this.getLevel().getLevelData().name();
//        String indexAssignedByLevel =  "" + this.getIndexAssignedByLevel();
//        String mapLocation = "(" + this.getMapPosition().x + ", " + this.getMapPosition().y + ")";
//
//        if (this.hasTargetPipe()){
//            String targetLevel = this.getTargetPipe().getLevel().getLevelData().name();
//            String targetPipeIndex = ""+this.getTargetPipe().getIndexAssignedByLevel();
//            String targetPipeLocation = "("+this.getTargetPipe().getMapPosition().x + ", " + this.getTargetPipe().getMapPosition().y +")";
//
//            return "\tLevel: " + level + "\tIndex: " + indexAssignedByLevel + "\tMapLocation: " + mapLocation +
//                    "\ttargetLevel: " + targetLevel + "\ttargetPipeIndex: " + targetPipeIndex + "\ttargetPipeMapLocation: " + targetPipeLocation;
//        }
//        else{
//            return "\tLevel: " + level + "\tIndex: " + indexAssignedByLevel + "\tMapLocation: " + mapLocation +
//                    "\tThis pipe leads to nowhere.";
//        }
//    }

}
