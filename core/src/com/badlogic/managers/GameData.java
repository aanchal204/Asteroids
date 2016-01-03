package com.badlogic.managers;

import java.io.Serializable;

/**
 * Created by aanchaldalmia on 03/01/16.
 */
public class GameData implements Serializable {

    private static final long serialVersionUID = 1;

    //how many highscores to be displayed
    private final int MAX_SCORES = 10;

    private long[] scores;
    private String[] names;
    private long tempScore;

    public GameData(){
        scores = new long[MAX_SCORES];
        names = new String[MAX_SCORES];
    }

    public void init(){
        for(int i = 0;i<MAX_SCORES;i++){
            scores[i] = 0;
            names[i] = "---";
        }
    }

    public long[] getScores(){
        return scores;
    }

    public String[] getNames(){
        return names;
    }

    public void setTempScore(long i){
        tempScore = i;
    }
    public long getTempScore(){
        return tempScore;
    }

    public boolean isHighScore(long score){
        return score > scores[MAX_SCORES - 1];
    }

    public void addHighScore(long newScore, String name){
        if(isHighScore(newScore)){
            scores[MAX_SCORES - 1] = newScore;
            names[MAX_SCORES - 1] = name;
            sortHighScore();
        }
    }

    private void sortHighScore(){
        for(int i=0;i<MAX_SCORES;i++){
            long score = scores[i];
            String name = names[i];
            int j;
            for(j=i-1;j>=0 && scores[j]<score;j++){
                scores[j+1] = scores[j];
                names[j+1] = names[j];
            }
            scores[j] = score;
            names[j] = name;
        }
    }
}
