package com.badlogic.gamestates;

import com.badlogic.asteroids.Asteroids;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.managers.GameStateManager;
import com.badlogic.managers.SaveGame;


/**
 * Created by aanchaldalmia on 04/01/16.
 */
public class GameOverState extends GameState {

    private SpriteBatch batch;
    private ShapeRenderer sr;

    private BitmapFont gameOverFont;
    private BitmapFont font;

    private GlyphLayout glyphLayout;

    private boolean isHighScore;
    private long newScore;

    private char[] newName;
    private int currentChar;


    public GameOverState(GameStateManager gsm){
        super(gsm);
    }

    @Override
    public void init() {
        batch = new SpriteBatch();
        sr = new ShapeRenderer();
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Hyperspace Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32;
        gameOverFont = gen.generateFont(parameter);

        parameter.size = 20;
        font = gen.generateFont(parameter);

        glyphLayout = new GlyphLayout();

        newScore = SaveGame.gameData.getTempScore();
        isHighScore = SaveGame.gameData.isHighScore(newScore);

        newName = new char[]{'A' , 'A', 'A'};
        currentChar = 0;

    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void draw() {

        batch.setProjectionMatrix(Asteroids.camera.combined);
        sr.setProjectionMatrix(Asteroids.camera.combined);
        batch.begin();

        //title: Game Over
        String s = "Game Over";
        glyphLayout.setText(gameOverFont,s);
        float width = glyphLayout.width;
        gameOverFont.draw(batch, s, (Asteroids.WIDTH - width) / 2, 300);

        if(isHighScore){
            s = "New High Score : " + newScore;
            glyphLayout.setText(font , s);
            width = glyphLayout.width;
            font.draw(batch, s, (Asteroids.WIDTH - width) / 2, 250);

            for(int i=0;i<newName.length;i++){
                font.draw(batch , Character.toString(newName[i]) , 370 + 14*i, 200);
            }

        }
        batch.end();

        //drawing the line beneath the current letter
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.line(
                370 + currentChar*14,
                150,
                384 + currentChar*14,
                150
        );
        sr.end();
    }

    @Override
    public void handleInput() {

        if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
            if(isHighScore){        //save the highscore in the file
                SaveGame.gameData.addHighScore(newScore , new String(newName));
                SaveGame.save();
            }
            gsm.setState(GameStateManager.MENU);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.UP)){
            if(newName[currentChar] == 'A')
                newName[currentChar] = 'Z';
            else
                newName[currentChar]--;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)){
            if(newName[currentChar] == 'Z')
                newName[currentChar] = 'A';
            else
                newName[currentChar]++;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)){
            if(currentChar < newName.length - 1)
                currentChar++;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT))
            if(currentChar > 0)
                currentChar--;


    }

    @Override
    public void dispose() {
        batch.dispose();
        sr.dispose();
        gameOverFont.dispose();
        font.dispose();
    }
}
