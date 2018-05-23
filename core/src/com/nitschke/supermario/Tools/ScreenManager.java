package com.nitschke.supermario.Tools;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.nitschke.supermario.MarioGame;
import com.nitschke.supermario.Scenes.Hud;
import com.nitschke.supermario.Screens.GameOverScreen;
import com.nitschke.supermario.Screens.MenuScreen;
import com.nitschke.supermario.Screens.PlayScreen;
import com.nitschke.supermario.Screens.WinScreen;
import com.nitschke.supermario.TileObjects.Pipe;

public class ScreenManager {

    // TODO: Is a public enum inside a class good practice?
    // TODO: See Effective Java item 24, p.112. Perhaps make it static?
    static public enum ScreenType {
        MenuScreen,
        GameOverScreen,
        PlayScreen,
        WinScreen;
    }

    private MarioGame game;
    private Hud hud;

    private ScreenType currentScreenType;
    private Array<PlayScreen> activePlayScreens;

    public ScreenManager(MarioGame game){
        this.game = game;
        currentScreenType = null;
        activePlayScreens = new Array<PlayScreen>();
        hud = new Hud(game.getSpriteBatch());
    }

    public Hud getHud(){return hud;}

    public void changeToNonPlayScreen(ScreenType targetScreenType){
        if (suggestedScreenChangeIsAllowed(targetScreenType)){
//          Gdx.app.debug("MarioGame.changeToNonPlayScreen", "currentScreenType: " + currentScreenType);
            if(targetScreenType == ScreenType.MenuScreen){
                // currentScreenType is GameOverScreen, WinScreen or null(startup).
                if (currentScreenType != null){
                    // dispose current screen
                    game.getScreen().dispose();
                }
                game.setScreen(new MenuScreen(game));
                currentScreenType = targetScreenType;
            }
            else {
                if (targetScreenType == ScreenType.GameOverScreen){
                    game.setScreen(new GameOverScreen(game));
                    currentScreenType = targetScreenType;
                }
                else if (targetScreenType == ScreenType.WinScreen){
                    game.setScreen(new WinScreen(game, hud.getScore()));
                    currentScreenType = targetScreenType;
                }
                // Reset Hud.
                hud.dispose();
                hud = new Hud(game.getSpriteBatch());
                // Dispose of playScreens
                for (PlayScreen playScreen : activePlayScreens){
                    playScreen.dispose();
                }
                activePlayScreens.clear();
            }
        }
    }

    // TODO: Consider making PlayScreen constructors Pipe-independent, e.g. pass spawnPos, Level ?
    public void changeToPlayScreen(Pipe targetPipe) {
        if (suggestedScreenChangeIsAllowed(ScreenType.PlayScreen)){

            if (currentScreenType == ScreenType.MenuScreen){
                game.getScreen().dispose();
                // Menu can pass any pipe from a given level. A second PlayScreen constructor is used, utilizing a different spawn point (?)
                if(!(activePlayScreens.size == 0)){	// Note: updated version libGDX has isEmpty() method, implemented as return size==0.
                    Gdx.app.error("changeToPlayScreen(Pipe targetPipe)",
                            "Error: activePlayScreens array was not empty when currentScreenType was MenuScreen");
                }
                PlayScreen targetPlayScreen = new PlayScreen(game, targetPipe.getLevel());
                activePlayScreens.add(targetPlayScreen);

                game.setScreen(targetPlayScreen);
                currentScreenType = ScreenType.PlayScreen;

            }
            else { // currentScreenType is a PlayScreen.
                PlayScreen targetPlayScreen = getActivePlayScreenForGivenLevel(targetPipe.getLevel());
                // If no playScreen for the given level is active.
                if (targetPlayScreen == null){
                    targetPlayScreen = new PlayScreen(game, targetPipe);
                    activePlayScreens.add(targetPlayScreen);
                }
                // Current screen creates a MarioSyncInfo object.
                // Use it to sync the targetScreen.
                targetPlayScreen.syncMarioState(((PlayScreen)game.getScreen()).createMarioSyncInfo(targetPipe.getB2Position()));
                // Switch.
                game.setScreen(targetPlayScreen);
                currentScreenType = ScreenType.PlayScreen;

                Gdx.app.debug("MarioGame.changeToPlayScreen", "Entering playScreen from another PlayScreen");
            }

        }
    }

    private PlayScreen getActivePlayScreenForGivenLevel(com.nitschke.supermario.Level.Level level){
        for (PlayScreen playScreen : activePlayScreens)
            if (playScreen.getLevel() == level)
                return playScreen;
        return null;
    }

    private boolean suggestedScreenChangeIsAllowed(ScreenType targetScreenType){
        boolean allowed = false;

        if (targetScreenType == ScreenType.PlayScreen                   // A PlayScreen can be accessed from the MenuScreen or another PlayScreen.
                && (currentScreenType == ScreenType.MenuScreen
                || currentScreenType == ScreenType.PlayScreen))
            allowed = true;

        else if (targetScreenType == ScreenType.MenuScreen
                && (currentScreenType == null 							// The MenuScreen can be accessed on startup
                || currentScreenType == ScreenType.GameOverScreen)      // or from the GameOverScreen
                || currentScreenType == ScreenType.WinScreen)           // or from the WinScreen.
            allowed = true;

        else if (targetScreenType == ScreenType.GameOverScreen
                && (currentScreenType == ScreenType.PlayScreen))		// The GameOverScreen can only be accessed from a PlayScreen.
            allowed = true;
        else if (targetScreenType == ScreenType.WinScreen
                && (currentScreenType == ScreenType.PlayScreen))		// The WinScreen can only be accessed from a PlayScreen.
            allowed = true;

        if (!allowed){
            Gdx.app.error("suggestedScreenChangeIsAllowed(ScreenType targetScreenType)",
                    "Error: The suggested screen change is not allowed: currentScreenType: " + currentScreenType + ", targetScreenType: " + targetScreenType);
        }
        return allowed;
    }

//    private boolean activePlayScreenForGivenLevelExists(Level level){
//        for (PlayScreen playScreen : activePlayScreens)
//            if (playScreen.getLevel() == level)
//                return true;
//        return false;
//    }

}
