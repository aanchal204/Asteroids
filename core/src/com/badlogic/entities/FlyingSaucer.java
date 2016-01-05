package com.badlogic.entities;

import com.badlogic.asteroids.Asteroids;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.managers.Jukebox;

import java.util.ArrayList;

/**
 * Created by aanchaldalmia on 04/01/16.
 */
public class FlyingSaucer extends SpaceObject {

    //type of saucer : big or small
    public static final int SMALL = 0;
    public static final int LARGE = 1;
    private int type;

    //direction of saucer : right or left
    public static final int RIGHT = 0;
    public static final int LEFT = 1;
    private int direction;

    //bullet list of the saucer
    private ArrayList<Bullet> bullets;

    //timer to shoot the buller
    private float fireTimer;
    private float fireTime;

    //timer to decide the path of the saucer
    private float pathTimer;
    private float pathTime1;
    private float pathTime2;

    //refernce to the player: used while shooting towards the player
    private Player player;

    private int score;

    private boolean remove;

    public FlyingSaucer(int type, int direction, ArrayList<Bullet> bullets, Player player){
        this.type = type;
        this.direction = direction;
        this.player = player;
        this.bullets = bullets;

        fireTimer = 0;
        fireTime = 1;   //shoot a bullet every 1 second

        pathTimer = 0;
        pathTime1 = 2;
        pathTime2 = pathTime1 + 2;

        speed = 70;
        if(direction == LEFT){  //saucer is moving towards the left: will enter from the right edge of the screen
            x = Asteroids.WIDTH;
            dx = -speed;    //moving in the negative x direction
        }
        else if(direction == RIGHT){
            x = 0;
            dx = speed;
        }
        y = MathUtils.random(Asteroids.HEIGHT); //randomly select the height at which the saucer moves

        //flying saucer's shape
        shapex = new float[6];
        shapey = new float[6];
        setShape();

        if(type == LARGE){
            score = 200;
            Jukebox.loop("largesaucer");
        }
        else if(type == SMALL){
            score = 1000;
            Jukebox.loop("smallsaucer");
        }

    }

    public void setShape(){

        if(type == LARGE) {
            shapex[0] = x - 10;
            shapey[0] = y;

            shapex[1] = x - 3;
            shapey[1] = y - 5;

            shapex[2] = x + 3;
            shapey[2] = y - 5;

            shapex[3] = x + 10;
            shapey[3] = y;

            shapex[4] = x + 3;
            shapey[4] = y + 5;

            shapex[5] = x - 3;
            shapey[5] = y + 5;
        }
        else if(type == SMALL){
            shapex[0] = x - 6;
            shapey[0] = y;

            shapex[1] = x - 2;
            shapey[1] = y - 3;

            shapex[2] = x + 2;
            shapey[2] = y - 3;

            shapex[3] = x + 6;
            shapey[3] = y;

            shapex[4] = x + 2;
            shapey[4] = y + 3;

            shapex[5] = x - 2;
            shapey[5] = y + 3;
        }
    }

    public int getScore(){
        return score;
    }

    public void update(float dt){

        //shoot
        //flying saucer will shoot only when the player is not hit and the timer expires
        if(!player.isHit()){
            fireTimer += dt;
            if(fireTimer >= fireTime){
                fireTimer = 0;
                //large saucer will shoot in any direction
                if(type == LARGE)
                    radians = MathUtils.random(2 * MathUtils.PI);
                //small saucer will shoot only towards the player
                else if(type == SMALL)
                    radians = MathUtils.atan2(player.gety() - y, player.getx() - x);
                bullets.add(new Bullet(x,y,radians));
                Jukebox.play("saucershoot");
            }
        }

        //move along the path
        pathTimer += dt;
        if(pathTimer < pathTime1)
            dy = 0;
        else if(pathTimer>= pathTime1 && pathTimer< pathTime2)
            dy = -speed;
        else
            dy = 0;

        x += dx*dt;
        y += dy*dt;

        //screen wrap: only wrap in the y direction
        if(y<0)
            y = Asteroids.HEIGHT;

        setShape();

        //check if remove
        if((direction == RIGHT && x>Asteroids.WIDTH) || (direction == LEFT && x<0))
            remove = true;
    }

    public void draw(ShapeRenderer sr){
        sr.setProjectionMatrix(Asteroids.camera.combined);
        sr.setColor(1,1,1,1);
        sr.begin(ShapeRenderer.ShapeType.Line);
        for(int i = 0, j = shapex.length - 1;
                i<shapey.length;
                j = i++){
            sr.line(shapex[i] , shapey[i] , shapex[j] , shapey[j]);
        }

        sr.line(shapex[0], shapey[0], shapex[3], shapey[3]);
        sr.end();
    }
    public boolean shouldRemove(){
        return remove;
    }
    public int getType(){
        return type;
    }

    public void destroyFlyingSaucer(){
        if(this.getType() == LARGE)
            Jukebox.stop("largesaucer");
        else if(this.getType() == SMALL)
            Jukebox.stop("smallsaucer");
    }
}
