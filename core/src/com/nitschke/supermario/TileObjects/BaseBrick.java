package com.nitschke.supermario.TileObjects;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.nitschke.supermario.MarioGame;

abstract class BaseBrick extends InteractiveTileObject {

    private TiledMap tiledMap;

//    Body edgeShapeBody;
    Fixture edgeShapeFixture;

    BaseBrick(MapObject object, TiledMap tiledMap, World b2world) {
        super(object);
        this.tiledMap = tiledMap;

        addObjectToWorld(b2world);
    }


    private void addObjectToWorld(World b2world){
        Rectangle mapObjectBounds = ((RectangleMapObject) mapObject).getRectangle();

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((mapObjectBounds.getX() + (mapObjectBounds.getWidth() / 2)) / MarioGame.PPM, (mapObjectBounds.getY() + (mapObjectBounds.getHeight() / 2)) / MarioGame.PPM);

        b2body = b2world.createBody(bdef);

        shape.setAsBox((mapObjectBounds.getWidth() / 2) / MarioGame.PPM, (mapObjectBounds.getHeight() / 2) / MarioGame.PPM);
        fdef.shape = shape;
        this.fixture = b2body.createFixture(fdef);

        // TODO: Consisteny in fDef creation.
        FixtureDef edgeShapeFixtureDef = createEdgeShapeFixtureDef(mapObjectBounds);
        this.edgeShapeFixture = b2body.createFixture(edgeShapeFixtureDef);
    }

    private FixtureDef createEdgeShapeFixtureDef(Rectangle brickBounds){

        float leftEdgeXposWorld = b2body.getPosition().x * MarioGame.PPM - (brickBounds.getWidth() / 2);
        float rightEdgeXposWorld = b2body.getPosition().x  * MarioGame.PPM+ (brickBounds.getWidth() / 2);
        float yPosWorld = b2body.getPosition().y * MarioGame.PPM + (brickBounds.getHeight() / 2);

        float leftEdgeXpos = -(brickBounds.getWidth() / 2)   / MarioGame.PPM;
        float rightEdgeXpos = (brickBounds.getWidth() / 2)  / MarioGame.PPM;
        float yPos = (brickBounds.getHeight() / 2)          / MarioGame.PPM;
        float heightOffset = 2 / MarioGame.PPM;
        float ghostVerticesXoffset = 1/MarioGame.PPM;

        Vector2 vertex1 = new Vector2((leftEdgeXpos), yPos + heightOffset);
        Vector2 vertex2 = new Vector2((rightEdgeXpos), yPos + heightOffset);

        Gdx.app.debug("Creating EdgeShape", "Vertex_1:" + new Vector2(leftEdgeXposWorld, (yPosWorld+1)) + "\tVertex_2: " + new Vector2(rightEdgeXposWorld, (yPosWorld+1) ));

        EdgeShape edgeShape = new EdgeShape();
        edgeShape.set(vertex1, vertex2);
//                    Gdx.app.debug("Created edge:",
//                            new Vector2((leftEdgeOfFirstBrickInChain) / MarioGame.PPM, (currentChainHeight+1) / MarioGame.PPM) + "   " +
//                                    new Vector2((rightEdgeOfPreviousBrickInChain) / MarioGame.PPM, (currentChainHeight+1) / MarioGame.PPM));

        edgeShape.setVertex0(vertex1.add(-ghostVerticesXoffset, 0));
        edgeShape.setVertex3(vertex2.add(ghostVerticesXoffset, 0));

        FixtureDef fdef = new FixtureDef();

        fdef.shape = edgeShape;
        return fdef;
    }

    TiledMapTileLayer.Cell getCell(){
        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get(1);
        return layer.getCell((int)(b2body.getPosition().x * MarioGame.PPM / 16),
                (int)(b2body.getPosition().y * MarioGame.PPM / 16));
    }

    public Vector2 getPosition(){
        return b2body.getPosition();
    }
}
