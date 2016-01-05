package com.badlogic.gamestates;

import com.badlogic.asteroids.Asteroids;
import com.badlogic.entities.Asteroid;
import com.badlogic.entities.Bullet;
import com.badlogic.entities.FlyingSaucer;
import com.badlogic.entities.Particle;
import com.badlogic.entities.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.managers.GameStateManager;
import com.badlogic.managers.Jukebox;
import com.badlogic.managers.SaveGame;

import java.util.ArrayList;

/**
 * Created by aanchaldalmia on 26/12/15.
 */
public class PlayState extends GameState{

    //play state has the space objects : player, its bullets, asteroids, particles, flying saucers and its bullets
    private ShapeRenderer sr;
    private Player player;
    private ArrayList<Bullet> bullets;

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    private ArrayList<Asteroid> asteroids;
    private ArrayList<Particle> particles;
    private ArrayList<Bullet> enemyBullets;
    private FlyingSaucer flyingSaucer;
    private float fsTimer;
    private float fsTime;

    private int level;
    private int totalAsteroids;
    private int numAsteroidsLeft;

    //used to draw fonts, images, textures
    private SpriteBatch batch;
    private BitmapFont font;

    //extra Lives Player : to draw the extra lives
    private Player extraLivesPlayer;

    //playing the background music
    private float maxDelay;
    private float minDelay;
    private float currentDelay;
    private float bgTimer;
    private boolean playLowPulse;

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

        batch = new SpriteBatch();

        //set font
        FreeTypeFontGenerator gen = new
                FreeTypeFontGenerator(Gdx.files.internal("fonts/Hyperspace Bold.ttf")); //file stored in assets folder
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        font = gen.generateFont(parameter);    //size=20

        extraLivesPlayer = new Player(null);

        //saucer timer
        fsTimer = 0;
        fsTime = 15;    //saucer will come every 15 seconds
        enemyBullets = new ArrayList<Bullet>();

        maxDelay = 1;
        minDelay = 0.25f;
        currentDelay = maxDelay;
        bgTimer = 0;    //bg music will play when the bgtimer becomes >= current delay
        //and the current delay decreases as the asteroids decrease in number
        //hence as the level proceeds, and more asteroids are destroyed,
        //the music is played with higher frequency
        playLowPulse = false;
    }

    //creates a new set of asteroids
    //when ever the player enters a new level
    private void spawnAsteroid(){
        asteroids.clear();
        int numToSpawn = 3 + level;
        totalAsteroids = 7*numToSpawn;
        numAsteroidsLeft = totalAsteroids;
        //every time a player enters a new level, reset the current Delay
        currentDelay = maxDelay;

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
        //as asteroids are destroyed, the currentDelay should be reduces
        currentDelay = ((maxDelay - minDelay) * numAsteroidsLeft/totalAsteroids) + minDelay;
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
            if(player.getLives() == 0) {
                if(SaveGame.gameData == null)
                    SaveGame.load();
                Jukebox.stopAll();
                SaveGame.gameData.setTempScore(player.getScore());
                gsm.setState(GameStateManager.GAMEOVER);
                return;
            }
            player.reset();
            player.loseLives();
            if(flyingSaucer!=null)
                flyingSaucer.destroyFlyingSaucer();
            flyingSaucer = null;
            return;
        }

        //update the flying saucer
        //flying saucer appears only after the timer expires
        if(flyingSaucer == null){
            fsTimer += dt;
            if(fsTimer >= fsTime){
                fsTimer = 0;
                int type = MathUtils.random() > 0.5 ? FlyingSaucer.LARGE : FlyingSaucer.SMALL;
                int direction = MathUtils.random() > 0.5 ? FlyingSaucer.LEFT : FlyingSaucer.RIGHT;
                flyingSaucer = new FlyingSaucer(type,direction,enemyBullets,player);
            }
        }
        //if the flying saucer is already present
        else{
            flyingSaucer.update(dt);
            if(flyingSaucer.shouldRemove()){
                if(flyingSaucer.getType() == FlyingSaucer.LARGE)
                    Jukebox.stop("largesaucer");
                else if(flyingSaucer.getType() == FlyingSaucer.SMALL)
                    Jukebox.stop("smallsaucer");
                flyingSaucer = null;

            }
        }

        //update the enemy bullets
        for(int i=0;i<enemyBullets.size();i++){
            enemyBullets.get(i).update(dt);
            if(enemyBullets.get(i).shouldRemove()){
                enemyBullets.remove(i);
                i--;
            }
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

        //play the background timer
        bgTimer += dt;
        if(!player.isHit() && bgTimer>=currentDelay){
            if(playLowPulse)
                Jukebox.play("pulselow");
            else
                Jukebox.play("pulsehigh");
            playLowPulse = !playLowPulse;
            bgTimer = 0;
        }

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
                    //should the asteroid break when it hits the player?
                    splitAsteroid(a);
                    Jukebox.play("explode");
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
                if(a.contains(b.getx(),b.gety())){      //if a collision has occurred, remove the bullet, asteroid and split the asteroid into 2, and increment the score
                    bullets.remove(i);
                    i--;
                    asteroids.remove(j);
                    j--;
                    splitAsteroid(a);
                    player.incrementScore(a.getScore());
                    Jukebox.play("explode");
                    break;
                }
            }
        }

        //player-enemy bullet collision
        if(!player.isHit()){
            for(int i=0; i<enemyBullets.size();i++){
                Bullet b = enemyBullets.get(i);
                if(player.contains(b.getx(),b.gety())){
                    player.hit();
                    enemyBullets.remove(i);
                    i--;
                    Jukebox.play("explode");
                    break;
                }
            }
        }

        //flying saucer-bullet collision
        if(flyingSaucer!=null){
            for(int i=0;i<bullets.size();i++){
                Bullet b = bullets.get(i);
                if(flyingSaucer.contains(b.getx(),b.gety())){
                    player.incrementScore(flyingSaucer.getScore());
                    bullets.remove(i);
                    i--;
                    createParticles(flyingSaucer.getx(),flyingSaucer.gety(),flyingSaucer.getType());
                    flyingSaucer.destroyFlyingSaucer();
                    flyingSaucer = null;
                    Jukebox.play("explode");
                    break;
                }
            }
        }

        //asteroid-enemy bullet collision
        for(int i=0;i<enemyBullets.size();i++){
            Bullet b = enemyBullets.get(i);
            for(int j=0;j<asteroids.size();j++){
                Asteroid a = asteroids.get(j);
                if(a.contains(b.getx(),b.gety())){
                    enemyBullets.remove(i);
                    i--;
                    asteroids.remove(j);
                    j--;
                    splitAsteroid(a);
                    Jukebox.play("explode");
                    break;
                }
            }
        }

        //flying saucer-player collision
        if(flyingSaucer!=null){
            if(flyingSaucer.intersects(player)){
                player.hit();
                createParticles(flyingSaucer.getx(), flyingSaucer.gety(), flyingSaucer.getType());
                flyingSaucer.destroyFlyingSaucer();
                flyingSaucer = null;
                Jukebox.play("explode");
            }
        }

        //flying saucer-asteroid collision
        if(flyingSaucer!=null){
            for(int i=0;i<asteroids.size();i++){
                Asteroid a = asteroids.get(i);
                if(flyingSaucer.intersects(a)){
                    asteroids.remove(i);
                    i--;
                    splitAsteroid(a);
                    createParticles(flyingSaucer.getx(), flyingSaucer.gety(), flyingSaucer.getType());
                    flyingSaucer.destroyFlyingSaucer();
                    flyingSaucer = null;
                    Jukebox.play("explode");
                    break;
                }
            }
        }
    }

    @Override
    public void draw() {

        //depending on the window size, the size of the objects is scaled
        batch.setProjectionMatrix(Asteroids.camera.combined);
        sr.setProjectionMatrix(Asteroids.camera.combined);
        //draw the player
        player.draw(sr);

        //draw the bullets
        for(int i=0;i<bullets.size();i++)
            bullets.get(i).draw(sr);

        //draw the flying saucer
        if(flyingSaucer != null)
            flyingSaucer.draw(sr);

        //draw the saucer's bullets
        for(int i=0;i<enemyBullets.size();i++)
            enemyBullets.get(i).draw(sr);

        //draw the asteroids
        for(int i=0;i<asteroids.size();i++)
            asteroids.get(i).draw(sr);

        //draw the particles
        for(int i=0;i<particles.size();i++)
            particles.get(i).draw(sr);

        //draw the score
        batch.setColor(1,1,1,1);
        batch.begin();
        font.draw(batch,Long.toString(player.getScore()),40,390);       //position to draw
        batch.end();


        //draw the extraLives
        for(int i=0;i<player.getLives();i++){
            extraLivesPlayer.setPosition(40 + 10*i , 360);
            extraLivesPlayer.draw(sr);
        }
    }

    @Override
    public void handleInput() {
        if(!player.isHit()) {
            player.setLeft(Gdx.input.isKeyPressed(Input.Keys.LEFT));
            player.setRight(Gdx.input.isKeyPressed(Input.Keys.RIGHT));
            player.setUp(Gdx.input.isKeyPressed(Input.Keys.UP));
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
                player.shoot();
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        sr.dispose();
    }
}