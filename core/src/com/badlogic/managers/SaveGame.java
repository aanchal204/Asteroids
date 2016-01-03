package com.badlogic.managers;

import com.badlogic.gdx.Gdx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by aanchaldalmia on 03/01/16.
 */
//saves the instance of the game: an object of the GameData class
    //i.e. the highscores are saved in a file
public class SaveGame {

    //the object to be saved
    public static GameData gameData;

    
    public static void save(){
        try{
            ObjectOutputStream out = new ObjectOutputStream(
                    new FileOutputStream("highscore.sav")
            );
            out.writeObject(gameData);
            out.close();
        }catch(Exception e){
            e.printStackTrace();
            Gdx.app.exit();
        }
    }

    public static void load(){
        try{
            if(!saveFileExists()){
                init();
                return;
            }
            ObjectInputStream in = new ObjectInputStream(
                    new FileInputStream("highscore.sav")
            );
            gameData = (GameData)in.readObject();
            in.close();
        }catch(Exception e){
            e.printStackTrace();
            Gdx.app.exit();
        }
    }

    public static boolean saveFileExists(){
        File file = new File("highscore.sav");
        return file.exists();
    }

    public static void init(){
        gameData = new GameData();
        gameData.init();
        save();
    }
}
