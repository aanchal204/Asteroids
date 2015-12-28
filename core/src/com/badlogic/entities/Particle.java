package com.badlogic.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

/**
 * Created by aanchaldalmia on 28/12/15.
 */

//creates particles when a asteroid explodes
public class Particle extends SpaceObject {

    //manage the life time of a particle
    private float timer;
    private float time;
    private boolean remove;

    public Particle(float x, float y){
        this.x = x;
        this.y = y;

        width = height = 2;
        speed = 50;
        radians = MathUtils.random(2 * MathUtils.PI);

        dx = MathUtils.cos(radians) * speed;
        dy = MathUtils.sin(radians) * speed;

        time = 1;
        timer = 0;
        remove = false;
    }

    public boolean shouldRemove(){
        return remove;
    }

    public void update(float dt){
        x += dx*dt;
        y += dy*dt;
        timer += dt;
        if(timer>time){
            remove = true;
        }
    }

    public void draw(ShapeRenderer sr){
        sr.setColor(1,1,1,1);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.circle(x-width/2,y-width/2,width/2);
        sr.end();
    }
}
