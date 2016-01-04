package com.badlogic.gamestates;

import com.badlogic.asteroids.Asteroids;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.managers.GameStateManager;
import com.badlogic.managers.SaveGame;


/**
 * Created by aanchaldalmia on 03/01/16.
 */
public class HighScoreState extends GameState {

    SpriteBatch batch;
    BitmapFont font;
    private long[] highScore;
    private String[] name;
    GlyphLayout glyphLayout;


    public HighScoreState(GameStateManager gsm){
        super(gsm);
    }

    @Override
    public void init() {

        batch = new SpriteBatch();
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Hyperspace Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        font = gen.generateFont(parameter);
        glyphLayout = new GlyphLayout();

        //loading the saved highscores from the file
        SaveGame.load();
        highScore = SaveGame.gameData.getScores();
        name = SaveGame.gameData.getNames();

    }

    @Override
    public void update(float dt) {

        handleInput();

    }

    @Override
    public void draw() {

        batch.setProjectionMatrix(Asteroids.camera.combined);
        batch.begin();

        glyphLayout.setText(font, "Highscores");
        float width = glyphLayout.width;
        font.draw(batch, "Highscores", (Asteroids.WIDTH - width) / 2, 400);
        for(int i=0;i<highScore.length;i++){
            String s = String.format("%2d. %7s %s",i+1,highScore[i],name[i]);
            glyphLayout.setText(font,s);
            width = glyphLayout.width;
            font.draw(batch , s , (Asteroids.WIDTH - width)/2 , 370 - 20*i);
        }

        batch.end();
    }

    @Override
    public void handleInput() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
            gsm.setState(GameStateManager.MENU);
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }


}
