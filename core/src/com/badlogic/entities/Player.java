package com.badlogic.entities;

import com.badlogic.asteroids.Asteroids;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
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

    //states of player
    //player goes in to dead state after being hit
    private boolean hit;
    private boolean dead;

    //represents the lines once the player explodes
    private Line2D.Float[] hitLines;
    //direction in which the lines should move
    private Point2D.Float[] hitLinesVector;

    private float hitTimer;
    private float hitTime;

    private long score;
    private int extraLives;
    private long requiredScore; //score required to get an extra life

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

        hit = false;
        hitTime = 2;        //player will remain on the screen for 2 secs before respawning
        hitTimer = 0;

        score = 0;
        extraLives = 3;
        requiredScore = 10000;
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

    public void setLeft(boolean b) { left = b; }
    public void setRight(boolean b) { right = b; }
    public void setUp(boolean b){ up = b; }
    public boolean isHit(){
        return hit;
    }
    public boolean isDead(){ return dead; }

    public long getScore(){ return score;}
    public int getLives(){ return extraLives; }
    public void loseLives() { extraLives --; }
    public void incrementScore(long l){
        score += l;
    }


    //called when a player is hit
    //player should respawn in the center of the screen
    public void reset(){
        x = Asteroids.WIDTH/2;
        y = Asteroids.HEIGHT/2;
        setShape();
        dead = hit = false;
    }
    public void shoot(){
        if(bullets.size()>=MAX_BULLETS)
            return ;
        bullets.add(new Bullet(x,y,radians));
    }

    //called when the player is hit with an asteroid
    public void hit(){
        if(hit)
            return;
        hit = true;
        //stop the player from moving
        dx = dy = 0;
        //disable the keys
        left = right = up = false;
        hitLines = new Line2D.Float[4];
        for(int i=0,j=hitLines.length-1;
            i<hitLines.length;
            j=i++){
            hitLines[i] = new Line2D.Float(shapex[i],shapey[i],shapex[j],shapey[j]);
        }
        hitLinesVector = new Point2D.Float[4];
        hitLinesVector[0] = new Point2D.Float(MathUtils.cos(radians + 1.5f) , MathUtils.sin(radians + 1.5f));
        hitLinesVector[1] = new Point2D.Float(MathUtils.cos(radians - 1.5f) , MathUtils.sin(radians - 1.5f));
        hitLinesVector[2] = new Point2D.Float(MathUtils.cos(radians + 2.8f) , MathUtils.sin(radians + 2.8f));
        hitLinesVector[3] = new Point2D.Float(MathUtils.cos(radians - 2.8f) , MathUtils.sin(radians - 2.8f));
    }

    public void update(float dt){       //delta time

        if(hit){
            hitTimer+=dt;
            if(hitTimer > hitTime){
                dead = true;
                hitTimer = 0;
            }
            for(int i=0;i<hitLines.length;i++){
                hitLines[i].setLine(
                        hitLines[i].x1 + hitLinesVector[i].x * 10 *dt,
                        hitLines[i].y1 + hitLinesVector[i].y * 10 *dt,
                        hitLines[i].x2 + hitLinesVector[i].x * 10 *dt,
                        hitLines[i].y2 + hitLinesVector[i].y * 10 *dt
                         );
            }
            return ;
        }

        //check extra lives
        if(score >= requiredScore){
            extraLives++;
            requiredScore += 10000;
        }

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

        if(hit){
            for(int i=0;i<hitLines.length;i++){
                sr.line(
                        hitLines[i].x1,
                        hitLines[i].y1,
                        hitLines[i].x2,
                        hitLines[i].y2
                );
            }
            sr.end();
            return;
        }

        int i;
        for(i=0;i<shapex.length-1;i++){
            sr.line(shapex[i],shapey[i],shapex[i+1],shapey[i+1]);
        }
        sr.line(shapex[i], shapey[i], shapex[0], shapey[0]);

        //draw the flames
        if(up){
            for(i=0;i<flamex.length-1;i++){
                sr.line(flamex[i],flamey[i],flamex[i+1],flamey[i+1]);
            }
            sr.line(flamex[i], flamey[i], flamex[0], flamey[0]);
        }

        sr.end();
    }
}
