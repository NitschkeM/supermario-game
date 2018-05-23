package com.nitschke.supermario.Tools;


//public class CollisionHandler {
//
////    Mario mario;
////    PlayScreen playScreen
//
////    public CollisionHandler(){
////    }
//
//
//
//
//    public void handleMarioPipeCollision(PlayScreen playScreen, Pipe pipe){
//        playScreen.setContactPipe(pipe);
//
//
////        this.contactPipe = pipe;
////        marioIsInContactWithPipe = true;
//    }
//    public void handleMarioPipeCollisionEnd(PlayScreen playScreen, Pipe pipe){
//        playScreen.endPipeContact(pipe);
//
////        this.contactPipe = null;
////        marioIsInContactWithPipe = false;
//    }
//
//    public void handleMarioEnemyCollison(Mario mario, Enemy enemy){
//        if(enemy instanceof Turtle && ((Turtle) enemy).currentState == Turtle.State.STANDING_SHELL)
//            ((Turtle) enemy).kick(enemy.b2body.getPosition().x > mario.getPosition().x ? Turtle.KICK_RIGHT : Turtle.KICK_LEFT);    // TODO: Uses public b2body of turtle.
//        else {
//            if (mario.isBig()) {
//                mario.makeMarioSmall();
//                playPowerDownSound();
//            } else {
//                playScreen.killMario();
//            }
//        }
//    }
//
//    public void handleMarioEnemyHeadCollison(Enemy enemy){
//        if (enemy instanceof Goomba){
//            playStompSound();
//            ((Goomba) enemy).destroy();
//        }
//        else if (enemy instanceof Turtle){                      // TODO: else > else-if, with current implementation.
//            ((Turtle) enemy).hitOnHead(mario.getPosition());
//        }
//    }
//
//    public void handleMarioHeadQuestionMarkBrickCollision(QuestionmarkBrick questionmarkBrick){
//        if (questionmarkBrick.isEmpty())
//            playBumpSound();
//        else {
//            if (questionmarkBrick.isMushroom()){
//                spawnItem(new ItemDef(new Vector2(questionmarkBrick.getPosition().x, questionmarkBrick.getPosition().y + 16 / MarioGame.PPM), Mushroom.class));     //TODO: Why is not X position adjusted to PPM?
//                playPowerUpSpawnSound();
//            }
//            else {
//                playCoinSound();
//            }
//            questionmarkBrick.setToBlankCoin();
//            hud.addScore(100);      // TODO: Hardcoded score system.
////            Hud.addScore(100);
//        }
//    }
//
//    public void handleMarioHeadBrickCollision(StandardBrick standardBrick){
//        if (mario.isBig()){
//            standardBrick.breakBrick();
//            bodiesToDestroy.add(standardBrick.getBody());
//            hud.addScore(200);  // TODO: Hardcoded score system.
////            Hud.addScore(200);
//            playBreakBlockSound();
//        }
//        else {
//            playBumpSound();
//        }
//    }
//
//    public void handleMarioItemCollision(Item item){
//        if(item instanceof Mushroom){
//            item.setToDestroy();
//            mario.makeMarioBig();
//            playPowerUpSound();
//        }
//    }
//
//    public void playerTouchedWinObject(){
//        playerHasWon = true;
//    }
//
//}
