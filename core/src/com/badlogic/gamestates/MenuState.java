package com.badlogic.gamestates;

import com.badlogic.asteroids.Asteroids;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.managers.GameStateManager;


/**
 * Created by aanchaldalmia on 31/12/15.
 */
public class MenuState extends GameState {

    //for drawing the fonts
    private SpriteBatch batch;
    private BitmapFont titleFont;
    private BitmapFont font;

    //current item in the menu list
    private int currentItem;
    private String[] menuItems;
    private final String title = "Asteroids";

    //used to find the width of the font
    //font.getBounds() has become depricated
    private static GlyphLayout glyphLayout ;

    public MenuState(GameStateManager gsm){
        super(gsm);
    }

    @Override
    public void init() {

        batch = new SpriteBatch();
        //setting up the font
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Hyperspace Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        //setting up the font size

        //title font
        parameter.size = 56;
        titleFont = gen.generateFont(parameter);
        titleFont.setColor(Color.WHITE);

        //menu font
        parameter.size = 20;
        font = gen.generateFont(parameter);

        //menu list
        currentItem = 0;
        menuItems = new String[] {
                "Play",
                "Highscores",
                "Exit"
        };

        glyphLayout = new GlyphLayout();

    }

    @Override
    public void update(float dt) {

        handleInput();

    }

    @Override
    public void draw() {

        batch.setProjectionMatrix(Asteroids.camera.combined);
        batch.begin();
        //title
        glyphLayout.setText(titleFont,title);
        float width = glyphLayout.width;
        titleFont.draw(batch,title,(Asteroids.WIDTH - width)/2,300);

        //menu
        for(int i=0;i<menuItems.length;i++){
            glyphLayout.setText(font,menuItems[i]);
            width = glyphLayout.width;
            if(currentItem==i){
                font.setColor(Color.RED);       //selected item is shown in red
            }else{
                font.setColor(Color.WHITE);
            }
            font.draw(batch, menuItems[i], (Asteroids.WIDTH - width) / 2, 180 - 35 * i);
        }
        batch.end();

    }

    @Override
    public void handleInput() {

        if(Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            if (currentItem > 0)
                currentItem--;
        }
        else if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)){
            if(currentItem < menuItems.length-1)
                currentItem++;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
            select();
        }
    }

    public void select(){
        if(currentItem==0)
            gsm.setState(GameStateManager.PLAY);
        else if(currentItem==1) {
            //gsm.setState(GameStateManager.HIGHSCORES);
        }
        else if(currentItem==2)
            Gdx.app.exit();
    }

    @Override
    public void dispose() {

    }
}