package com.nitschke.supermario.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.nitschke.supermario.MarioGame;
import com.nitschke.supermario.Screens.PlayScreen;
import com.nitschke.supermario.Entities.Enemies.Goomba;
import com.nitschke.supermario.Entities.Enemies.Turtle;
import com.nitschke.supermario.TileObjects.StandardBrick;
import com.nitschke.supermario.TileObjects.QuestionmarkBrick;


public class B2WorldCreator {


    // TODO: Factor out Enemy initial velocities. And turtle sleep time.

    public B2WorldCreator(PlayScreen screen) {
        World b2world = screen.getWorld();
        TiledMap tiledMap = screen.getMap();

        float turtleShellSleepTime = 5f;
        Vector2 enemyInitialVelocity = new Vector2(-1, -2);



        {
            // Create body and Fixture variables
            BodyDef bdef = new BodyDef();
            PolygonShape shape = new PolygonShape();
            FixtureDef fdef = new FixtureDef();
            Body body;
            // TODO: Why isn't category filter of ground fixture set to ground bit?
            // Create ground bodies/fixtures
            for (MapObject mapObject : tiledMap.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
                Rectangle rect = ((RectangleMapObject) mapObject).getRectangle();

                bdef.type = BodyDef.BodyType.StaticBody;
                bdef.position.set((rect.getX() + rect.getWidth() / 2) / MarioGame.PPM, (rect.getY() + rect.getHeight() / 2) / MarioGame.PPM);

                body = b2world.createBody(bdef);

                shape.setAsBox((rect.getWidth() / 2) / MarioGame.PPM, (rect.getHeight() / 2) / MarioGame.PPM);
                fdef.shape = shape;
                body.createFixture(fdef);
            }

            // Create win condition body. First make sure map has a WinCondition Layer and a WinCondition object.
            if (tiledMap.getLayers().get(8) != null && tiledMap.getLayers().get(8).getObjects().get(0) != null){
                for (MapObject mapObject : tiledMap.getLayers().get(8).getObjects().getByType(RectangleMapObject.class)) {

                    Rectangle rect = ((RectangleMapObject) mapObject).getRectangle();

                    bdef.type = BodyDef.BodyType.StaticBody;
                    bdef.position.set((rect.getX() + rect.getWidth() / 2) / MarioGame.PPM, (rect.getY() + rect.getHeight() / 2) / MarioGame.PPM);

                    body = b2world.createBody(bdef);

                    shape.setAsBox((rect.getWidth() / 2) / MarioGame.PPM, (rect.getHeight() / 2) / MarioGame.PPM);
                    fdef.shape = shape;

                    Filter filter = new Filter();
                    filter.categoryBits = MarioGame.WIN_CONDITION_BIT;
//                    fixture.setFilterData(filter);

                    body.createFixture(fdef).setFilterData(filter);
                }
            }
        }

        //create brick bodies/fixtures. Cover consecutive bricks with an EdgeShape to avoid ghost collisions.
        Array<Array<Rectangle>> arraysOfMapObjectsAtSameHeight = new Array<Array<Rectangle>>();

        for (MapObject mapObject : tiledMap.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle mapObjectBounds = ((RectangleMapObject) mapObject).getRectangle();

            Vector2 topLeftCorner = new Vector2(mapObjectBounds.getX(), mapObjectBounds.getY() + mapObjectBounds.getHeight());
            Vector2 topRightCorner = new Vector2(mapObjectBounds.getX() + mapObjectBounds.getWidth(), topLeftCorner.y);
            new StandardBrick(mapObject, tiledMap, screen.getWorld());
        }

        //create coin bodies/fixtures
        for(MapObject mapObject : tiledMap.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){
            Rectangle mapObjectBounds = ((RectangleMapObject) mapObject).getRectangle();
            new QuestionmarkBrick(mapObject, tiledMap, screen.getWorld());
        }

        // Create all goombas
        for(MapObject mapObject : tiledMap.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) mapObject).getRectangle();
            Vector2 b2Position = new Vector2(rect.getX(), rect.getY()).scl(1/MarioGame.PPM);
            screen.addGoomba(new Goomba(b2world, b2Position, screen.getAtlas(), enemyInitialVelocity));
        }
        // Create all turtles
        for(MapObject mapObject : tiledMap.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) mapObject).getRectangle();
            Vector2 b2Position = new Vector2(rect.getX(), rect.getY()).scl(1/MarioGame.PPM);
            screen.addTurtle(new Turtle(b2world, b2Position, screen.getAtlas(), enemyInitialVelocity, turtleShellSleepTime));
        }
    }

}
