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

    //getter methods
    public float getx(){
        return x;
    }

    public float gety(){
        return y;
    }

    public float[] getShapex(){
        return shapex;
    }

    public float[] getShapey(){
        return shapey;
    }

    //polygon-polygon intersection
    public boolean intersects(SpaceObject other){
        float[] otherx = other.getShapex();
        float[] othery = other.getShapey();
        //to detect intersections between polygons, check if any point in one polygon lies inside the other polygon
        for(int i=0;i<otherx.length;i++){
            if(this.contains(otherx[i],othery[i])){
                return true;
            }
        }
        return false;
    }

    //check if a polygon contains a point
    //from the point, extend a horizontal line towards the right and find the number of times the lise intersects the polygon
    // even : point lies outside
    //odd : point lies inside
    public boolean contains(float x , float y){
        boolean b = false;
        for(int i=0,j=shapex.length-1;i<shapex.length;j=i++){
            if(((y<shapey[i]) != (y<shapey[j]) ) &&     //if the y coordinate of the point lies between the y coordinates of the line
                    //if the point lies below the y coordinates of the end points of the line, or above both of them,
                    //then it is not possible for the horizontal line to intersect the line of the polygon
                    (x < ((y-shapey[i]) * (shapex[j] - shapex[i]) / (shapey[j] - shapey[i])  + shapex[i]) ) ){
                //hint: make use of slopes
                //let x1 y1 and x2 y2 be the end points of the line
                //slope m1 = (y2-y1)/(x2-x1)
                //slope of line between x y and x1 y1: m2= (y1-y)/(x1-x)
                //since we draw a horizontal line towards the right, it will intersect only if m2 > m1
                //(y2-y1)/(x2-x1) > (y1-y)/(x1-x)
                //solve for x to get the above inequality
                b = !b;
            }
        }
        return b;
    }

    //in case any object goes outside the screen, it should wrap around
    public void wrap(){
        if(x> Asteroids.WIDTH) x = 0;
        if(x<0) x = Asteroids.WIDTH;
        if(y>Asteroids.HEIGHT) y=0;
        if(y<0) y = Asteroids.HEIGHT;
    }
}
