package com.badlogic.gamestates;

import com.badlogic.asteroids.Asteroids;
import com.badlogic.entities.Asteroid;
import com.badlogic.entities.Bullet;
import com.badlogic.entities.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.managers.GameStateManager;

import java.util.ArrayList;

/**
 * Created by aanchaldalmia on 26/12/15.
 */
public class PlayState extends GameState{

    private ShapeRenderer sr;
    private Player player;
    private ArrayList<Bullet> bullets;
    private ArrayList<Asteroid> asteroids;

    private int level;
    private int totalAsteroids;
    private int numAsteroidsLeft;

    public PlayState(GameStateManager gsm){
        super(gsm);
    }

    @Override
    public void init() {
        sr = new ShapeRenderer();
        bullets = new ArrayList<Bullet>();
        player = new Player(bullets);
        asteroids = new ArrayList<Asteroid>();

        level = 1;
        spawnAsteroid();
    }

    private void spawnAsteroid(){
        asteroids.clear();
        int numToSpawn = 3 + level;
        totalAsteroids = 7*numToSpawn;
        numAsteroidsLeft = totalAsteroids;

        for(int i=0;i<numToSpawn;i++){
            float x = MathUtils.random(Asteroids.WIDTH);
            float y = MathUtils.random(Asteroids.HEIGHT);

            float dx = x - player.getx();
            float dy = y - player.gety();

            float dist = (float)Math.sqrt(dx*dx + dy*dy);
            while(dist<100){
                x = MathUtils.random(Asteroids.WIDTH);
                y = MathUtils.random(Asteroids.HEIGHT);

                dx = x - player.getx();
                dy = y - player.gety();
                dist = (float)Math.sqrt(dx*dx + dy*dy);
            }
            asteroids.add(new Asteroid(x,y,Asteroid.LARGE));
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
        //update the player
        player.update(dt);

        //update the bullets
        for(int i=0;i<bullets.size();i++){
            bullets.get(i).update(dt);
            if(bullets.get(i).shouldRemove()){
                bullets.remove(i);
                i--;
            }
        }

        //update asteroids
        for(int i=0;i<asteroids.size();i++){
            asteroids.get(i).update(dt);
            if(asteroids.get(i).shouldRemove()){
                asteroids.remove(i);
                i--;
            }
        }

    }

    @Override
    public void draw() {
        //draw the player
        player.draw(sr);

        //draw the bullets
        for(int i=0;i<bullets.size();i++)
            bullets.get(i).draw(sr);

        //draw the aseroids
        for(int i=0;i<asteroids.size();i++)
            asteroids.get(i).draw(sr);
    }

    @Override
    public void handleInput() {
        player.setLeft(Gdx.input.isKeyPressed(Input.Keys.LEFT));
        player.setRight(Gdx.input.isKeyPressed(Input.Keys.RIGHT));
        player.setUp(Gdx.input.isKeyPressed(Input.Keys.UP));
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            player.shoot();
    }

    @Override
    public void dispose() {

    }
}
