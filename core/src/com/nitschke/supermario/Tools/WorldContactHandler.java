//package com.nitschke.supermario.Tools;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.physics.box2d.Contact;
//import com.badlogic.gdx.physics.box2d.Fixture;
//import com.nitschke.supermario.MarioGame;
//import com.nitschke.supermario.Sprites.Enemies.Enemy;
//import com.nitschke.supermario.Sprites.Items.Item;
//import com.nitschke.supermario.Sprites.Mario;
//import com.nitschke.supermario.Sprites.Other.FireBall;
//import InteractiveTileObject;
//
//
//import java.util.LinkedList;
//import java.util.Stack;
//
//// Try: Package private: Only the WorldContactListener needs to know about this class.
////      Now: Public because Playscreen has a private instance of this.
//public class WorldContactHandler {
////    private Stack<Contact> contacts;
//    private LinkedList<Contact> contacts;
//
//    // TODO: Stack vs Que? Does not the stack flip the order? Does it not matter?
//    public WorldContactHandler(){
////        contacts  = new Stack<Contact>();
//        contacts  = new LinkedList<Contact>();
//    }
//
//
//    void addContact(Contact contact){
////        contacts.push(contact);
//        contacts.add(contact);
//    }
//
//    // TODO: This is associated with implementation of bit fields, see MarioGame.
//    //
//    // 		Effective Java, p.167: Good use of enums: Use enums any time you need a set of constants whose members are known at compile time.
//
//    public void handleContacts(){
////        if(!contacts.isEmpty())
////            Gdx.app.debug("ContactHandler", "Handling... #Contacts = " + contacts.size());
//
//        while(!contacts.isEmpty()){
////            Gdx.app.debug("ContactHandler", "Handling contact");
//
////            Contact contact = contacts.pop();
//            Contact contact = contacts.poll();
//
//            // TODO: Investigate if contacts are duplicated, (It looks like I get two contacts when jumping into a brick, this may work as intended).
////            Gdx.app.debug("ContactHandler", "contact.getFixtureA().getUserData(): " + contact.getFixtureA().getUserData());
////            Gdx.app.debug("ContactHandler", "contact.getFixtureB().getUserData(): " + contact.getFixtureB().getUserData() + "\n");
//
//
//            Fixture fixA = contact.getFixtureA();
//            Fixture fixB = contact.getFixtureB();
//
//            int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;
////            Gdx.app.debug("ContactHandler", "contact " + contact);
////            Gdx.app.debug("ContactHandler", "fixA: " + fixA);
////            Gdx.app.debug("ContactHandler", "fixB: " + fixB);
////
////            Gdx.app.debug("ContactHandler", "fixA.getFilterData().categoryBits: " + fixA.getFilterData().categoryBits);
////            Gdx.app.debug("ContactHandler", "fixB.getFilterData().categoryBits: " + fixB.getFilterData().categoryBits + "\n");
//
//
//
//            switch (cDef){
//                case MarioGame.MARIO_HEAD_BIT | MarioGame.BRICK_BIT:
////                    Gdx.app.debug("ContactHandler", "MarioGame.MARIO_HEAD_BIT | MarioGame.BRICK_BIT");
//
//                case MarioGame.MARIO_HEAD_BIT | MarioGame.COIN_BIT:
////                    Gdx.app.debug("ContactHandler", "MarioGame.MARIO_HEAD_BIT | MarioGame.COIN_BIT");
//
//                    if (fixA.getFilterData().categoryBits == MarioGame.MARIO_HEAD_BIT)
//                        ((InteractiveTileObject) fixB.getUserData()).onHeadHit((Mario) fixA.getUserData());
//                    else
//                        ((InteractiveTileObject) fixA.getUserData()).onHeadHit((Mario) fixB.getUserData());
//                    break;
//                case MarioGame.ENEMY_HEAD_BIT | MarioGame.MARIO_BIT:
////                    Gdx.app.debug("ContactHandler", "MarioGame.ENEMY_HEAD_BIT | MarioGame.MARIO_BIT");
//
//                    if (fixA.getFilterData().categoryBits == MarioGame.ENEMY_HEAD_BIT){
////                        Gdx.app.debug("ContactHandler", "Trying to call enemy.hitOnHead method");
//                        ((Enemy)fixA.getUserData()).hitOnHead((Mario) fixB.getUserData());
//                    }
//                    else {
////                        Gdx.app.debug("ContactHandler", "Trying to call enemy.hitOnHead method");
//                        ((Enemy)fixB.getUserData()).hitOnHead((Mario) fixA.getUserData());
//                    }
//                    break;
//                case MarioGame.ENEMY_BIT | MarioGame.OBJECT_BIT:
////                    Gdx.app.debug("ContactHandler", "MarioGame.ENEMY_BIT | MarioGame.OBJECT_BIT:");
//
//                    if (fixA.getFilterData().categoryBits == MarioGame.ENEMY_BIT){
////                        Gdx.app.debug("ContactHandler", "Trying to reverseVelocity");
//                        ((Enemy)fixA.getUserData()).reverseVelocity(true, false);
//                    }
//                    else {
////                        Gdx.app.debug("ContactHandler", "Trying to reverseVelocity");
//                        ((Enemy)fixB.getUserData()).reverseVelocity(true, false);
//                    }
//                    break;
//                case MarioGame.MARIO_BIT | MarioGame.ENEMY_BIT:
////                    Gdx.app.debug("ContactHandler", "MarioGame.MARIO_BIT | MarioGame.ENEMY_BIT:");
//
//                    if (fixA.getFilterData().categoryBits == MarioGame.MARIO_BIT)
//                        ((Mario) fixA.getUserData()).hit((Enemy)fixB.getUserData());
//                    else
//                        ((Mario) fixB.getUserData()).hit((Enemy)fixA.getUserData());
//                    break;
//                case MarioGame.ENEMY_BIT | MarioGame.ENEMY_BIT:
////                    Gdx.app.debug("ContactHandler", "MarioGame.ENEMY_BIT | MarioGame.ENEMY_BIT");
//
//                    ((Enemy)fixA.getUserData()).hitByEnemy((Enemy)fixB.getUserData());
//                    ((Enemy)fixB.getUserData()).hitByEnemy((Enemy)fixA.getUserData());
//                    break;
//                case MarioGame.ITEM_BIT | MarioGame.OBJECT_BIT:
////                    Gdx.app.debug("ContactHandler", "MarioGame.ITEM_BIT | MarioGame.OBJECT_BIT");
//
//                    if (fixA.getFilterData().categoryBits == MarioGame.ITEM_BIT)
//                        ((Item)fixA.getUserData()).reverseVelocity(true, false);
//                    else
//                        ((Item)fixB.getUserData()).reverseVelocity(true, false);
//                    break;
//                case MarioGame.ITEM_BIT | MarioGame.MARIO_BIT:
////                    Gdx.app.debug("ContactHandler", "MarioGame.ITEM_BIT | MarioGame.MARIO_BIT");
//
//                    if (fixA.getFilterData().categoryBits == MarioGame.ITEM_BIT)
//                        ((Item)fixA.getUserData()).use((Mario) fixB.getUserData());
//                    else
//                        ((Item)fixB.getUserData()).use((Mario) fixA.getUserData());
//                    break;
//                case MarioGame.FIREBALL_BIT | MarioGame.OBJECT_BIT:
////                    Gdx.app.debug("ContactHandler", "MarioGame.FIREBALL_BIT | MarioGame.OBJECT_BIT");
//
//                    if (fixA.getFilterData().categoryBits == MarioGame.FIREBALL_BIT)
//                        ((FireBall)fixA.getUserData()).setToDestroy();
//                    else
//                        ((FireBall)fixB.getUserData()).setToDestroy();
//                    break;
//                    default:
////                        Gdx.app.debug("ContactHandler", "Reached end of switch statement...");
//            }
//        }
//    }
//}
