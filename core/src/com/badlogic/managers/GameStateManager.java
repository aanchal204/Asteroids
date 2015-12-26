package com.badlogic.managers;

import com.badlogic.gamestates.GameState;
import com.badlogic.gamestates.PlayState;

/**
 * Created by aanchaldalmia on 26/12/15.
 */
public class GameStateManager {

    private GameState gameState;
    public static final int MENU = 0;
    public static final int PLAY = 1;

    public GameStateManager(){
        setState(PLAY);
    }

    public void setState(int state){
        if(gameState!=null) gameState.dispose();
        if(state == MENU){
            //switch to menu state
            //gameState = new MenuState(this);
        }
        if(state == PLAY){
            //switch to play state
            gameState = new PlayState(this);
        }
    }

    public void update (float dt){
        gameState.update(dt);
    }

    public void draw(){
        gameState.draw();
    }
}
