package com.badlogic.gamestates;

import com.badlogic.asteroids.Asteroids;
import com.badlogic.entities.Asteroid;
import com.badlogic.entities.Bullet;
import com.badlogic.entities.Particle;
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

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    private ArrayList<Asteroid> asteroids;
    private ArrayList<Particle> particles;

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
        particles = new ArrayList<Particle>();

        level = 1;
        spawnAsteroid();
    }

    //creates a new set of asteroids
    //when ever the player enters a new level
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

    //creates particles when ever a bullet hits a asteroid
    private void createParticles(float x, float y, int type){
        int num=0;
        if(type == Asteroid.LARGE)
            num = 10;
        else if(type == Asteroid.MEDIUM)
            num = 8;
        else if(type == Asteroid.SMALL)
            num = 6;
        for(int i=0;i<num;i++){
            particles.add(new Particle(x,y));
        }
    }

    //break a big asteroid into 2 smaller asteroids when a bullet hits it
    private void splitAsteroid(Asteroid a){
        createParticles(a.getx(), a.gety(), a.getType());
        numAsteroidsLeft--;
        if(a.getType()==Asteroid.LARGE){
            asteroids.add(new Asteroid(a.getx(),a.gety(),Asteroid.MEDIUM));
            asteroids.add(new Asteroid(a.getx(),a.gety(),Asteroid.MEDIUM));
        }if(a.getType()==Asteroid.MEDIUM){
            asteroids.add(new Asteroid(a.getx(),a.gety(),Asteroid.SMALL));
            asteroids.add(new Asteroid(a.getx(),a.gety(),Asteroid.SMALL));
        }
    }

    @Override
    public void update(float dt) {
        handleInput();

        //next level: when all asteroids are over
        if(asteroids.size()==0){
            level++;
            spawnAsteroid();
        }

        //update the player
        player.update(dt);
        if(player.isDead()) {
            player.reset();
            return;
        }

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

        //update particles
        for(int i=0;i<particles.size();i++) {
            particles.get(i).update(dt);
            if(particles.get(i).shouldRemove()){
                particles.remove(i);
                i--;
            }
        }

        //check for collisions
        checkCollisions();

    }

    private void checkCollisions(){

        //asteroid-player collision
        //check for polygon-polygon intersection
        if(!player.isHit()){
            for(int i=0;i<asteroids.size();i++){
                Asteroid a = asteroids.get(i);
                if(a.intersects(player)){
                    player.hit();
                    asteroids.remove(i);
                    i--;
                    splitAsteroid(a);
                    break;
                }
            }
        }


        //asteroid-bullet collision
        for(int i=0;i<bullets.size();i++){
            Bullet b = bullets.get(i);
            for(int j=0;j<asteroids.size();j++){
                Asteroid a = asteroids.get(j);
                //check if the asteroid contains the bullet
                //i.e. check if a polygon contains a point
                //even odd winding rule
                if(a.contains(b.getx(),b.gety())){      //if a collision has occurred, remove the bullet, asteroid and split the asteroid into 2
                    bullets.remove(i);
                    i--;
                    asteroids.remove(j);
                    j--;
                    splitAsteroid(a);
                    break;
                }
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

        //draw the asteroids
        for(int i=0;i<asteroids.size();i++)
            asteroids.get(i).draw(sr);

        //draw the particles
        for(int i=0;i<particles.size();i++)
            particles.get(i).draw(sr);
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
