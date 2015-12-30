package com.badlogic.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;

/**
 * Created by aanchaldalmia on 30/12/15.
 */
public class Jukebox {

    private static HashMap<String, Sound> sounds;

    static{
        sounds = new HashMap<String, Sound>();
    }
    public static void load(String name, String path){
        sounds.put(name, Gdx.audio.newSound(Gdx.files.internal(path)));
    }
    public static void play(String name){
        sounds.get(name).play();
    }
    public static void loop(String name){
        sounds.get(name).loop();
    }
    public static void stop(String name){
        sounds.get(name).stop();
    }
    public void stopAll(){
        for(Sound s : sounds.values()){
            s.stop();
        }
    }
}
