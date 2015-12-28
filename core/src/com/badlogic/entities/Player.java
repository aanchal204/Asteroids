package com.badlogic.entities;

import com.badlogic.asteroids.Asteroids;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;

/**
 * Created by aanchaldalmia on 26/12/15.
 */
public class Player extends SpaceObject {

    //additional attributes of player

    //movement : depending on what key is pressed
    private boolean right;
    private boolean left;
    private boolean up;

    private float maxSpeed;
    private float acceleration;
    private float deceleration;

    //flames at time of acceleration
    private float[] flamex;
    private float[] flamey;

    private float acceleratingTimer;

    //bullet list
    private ArrayList<Bullet> bullets;

    //how many maximum no of bullets a player can have ?
    private final int MAX_BULLETS = 4;

    public Player(ArrayList<Bullet> bullets) {

        this.bullets = bullets;

        //set the player's position to the center of the screen
        x = Asteroids.WIDTH/2;
        y = Asteroids.HEIGHT/2;

        //pixels/second
        maxSpeed = 300;
        acceleration = 200;
        deceleration = 10;

        shapex = new float[4];
        shapey = new float[4];

        radians = MathUtils.PI/2;       //facing upwards initially
        rotationSpeed = 3;

        flamex = new float[3];
        flamey = new float[3];

    }

    public void setShape(){
        shapex[0] = x + MathUtils.cos(radians) * 8;     //Polar coordinates x = x + rcos(angle)
        shapey[0] = y + MathUtils.sin(radians) * 8;

        shapex[1] = x + MathUtils.cos(radians - 4 * MathUtils.PI / 5) * 8;
        shapey[1] = y + MathUtils.sin(radians - 4 * MathUtils.PI / 5) * 8;

        shapex[2] = x + MathUtils.cos(radians - MathUtils.PI) * 5;
        shapey[2] = y + MathUtils.sin(radians - MathUtils.PI) * 5;

        shapex[3] = x + MathUtils.cos(radians + 4 * MathUtils.PI / 5) * 8;
        shapey[3] = y + MathUtils.sin(radians + 4 * MathUtils.PI / 5) * 8;
    }

    public void setFlame(){
        flamex[0] = x + MathUtils.cos(radians + 5*MathUtils.PI/6) * 5;
        flamey[0] = y + MathUtils.sin(radians + 5 * MathUtils.PI / 6) * 5;

        flamex[1] = x + MathUtils.cos(radians + MathUtils.PI) * (6 + acceleratingTimer*50);
        flamey[1] = y + MathUtils.sin(radians + MathUtils.PI) * (6 + acceleratingTimer*50);

        flamex[2] = x + MathUtils.cos(radians - 5*MathUtils.PI/6) * 5;
        flamey[2] = y + MathUtils.sin(radians - 5 * MathUtils.PI / 6) * 5;

    }

    public void setLeft(boolean b){
        left = b;
    }
    public void setRight(boolean b){
        right = b;
    }
    public void setUp(boolean b){
        up = b;
    }

    public void shoot(){
        if(bullets.size()>=MAX_BULLETS)
            return ;
        bullets.add(new Bullet(x,y,radians));
    }

    public void hit(){

    }

    public void update(float dt){       //delta time

        //turning
        if(left)
            radians += rotationSpeed*dt;
        if(right)
            radians -= rotationSpeed*dt;

        //moving
        // accelerating
        if(up){
            dx += MathUtils.cos(radians)*acceleration*dt;
            dy += MathUtils.sin(radians) *acceleration*dt;
            acceleratingTimer+=dt;
            if(acceleratingTimer > 0.1f)
                acceleratingTimer=0;
        }else
            acceleratingTimer = 0;

        //deceleration
        float vec = (float)Math.sqrt(dx*dx + dy*dy);
        if(vec>0){
            dx -= (dx/vec) * deceleration*dt;
            dy -= (dy/vec) *deceleration*dt;
        }
        if(vec>maxSpeed){
            dx = (dx/vec)*maxSpeed;
            dy = (dy/vec)*maxSpeed;
        }
        x += dx*dt;
        y += dy*dt;


        setShape();
        if(up)
            setFlame();
        //screen wrap
        wrap();

    }

    public void draw(ShapeRenderer sr){

        sr.setColor(1, 1, 1, 1);

        //draw the polygon as a set of connected lines
        sr.begin(ShapeRenderer.ShapeType.Line);

        int i;
        for(i=0;i<shapex.length-1;i++){
            sr.line(shapex[i],shapey[i],shapex[i+1],shapey[i+1]);
        }
        sr.line(shapex[i],shapey[i],shapex[0],shapey[0]);

        //draw the flames
        if(up){
            for(i=0;i<flamex.length-1;i++){
                sr.line(flamex[i],flamey[i],flamex[i+1],flamey[i+1]);
            }
            sr.line(flamex[i],flamey[i],flamex[0],flamey[0]);
        }

        sr.end();
    }
}
