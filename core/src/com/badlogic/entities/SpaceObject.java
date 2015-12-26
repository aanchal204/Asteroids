package com.badlogic.entities;

import com.badlogic.asteroids.Asteroids;

/**
 * Created by aanchaldalmia on 26/12/15.
 */
public class SpaceObject {

    //a super class that has all the properties of our game's objects: Player, Asteroid, Bullet and Flying Saucer

    //position
    protected float x;
    protected float y;

    //vector: direction in which the object is moving
    protected float dx;
    protected float dy;

    //direction in which the object in facing
    protected float radians;

    //speed of the object
    protected float speed;

    //rotation speed
    protected float rotationSpeed;

    //size of the object
    protected float width;
    protected float height;

    //shape : polygon represented as an array of vertices
    protected float[] shapex;
    protected float[] shapey;

    public float getx(){
        return x;
    }

    public float gety(){
        return y;
    }

    //in case any object goes outside the screen, it should wrap around
    public void wrap(){
        if(x> Asteroids.WIDTH) x = 0;
        if(x<0) x = Asteroids.WIDTH;
        if(y>Asteroids.HEIGHT) y=0;
        if(y<0) y = Asteroids.HEIGHT;
    }
}
