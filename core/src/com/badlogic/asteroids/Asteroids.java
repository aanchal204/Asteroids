package com.badlogic.asteroids;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.managers.GameStateManager;
import com.badlogic.managers.Jukebox;

public class Asteroids extends ApplicationAdapter {

	public static int WIDTH;
	public static int HEIGHT;
	public static OrthographicCamera camera;

	private GameStateManager gsm;
	
	@Override
	public void create () {

		WIDTH = Gdx.graphics.getWidth();
		HEIGHT = Gdx.graphics.getHeight();
		camera = new OrthographicCamera();
		// camera originally has the lower left corner at -1,-1
		camera.setToOrtho(false, WIDTH, HEIGHT);		//boolean yDown, width, height
		//Sets this camera to an orthographic projection, centered at (Width/2, Height/2), with the y-axis pointing up or down.
		camera.update();

		//loading the sounds needed during the game
		Jukebox.load("explode", "sounds/explode.ogg");
		Jukebox.load("extralife", "sounds/extralife.ogg");
		Jukebox.load("pulsehigh","sounds/pulsehigh.ogg");
		Jukebox.load("pulselow","sounds/pulselow.ogg");
		Jukebox.load("shoot","sounds/shoot.ogg");
		Jukebox.load("thruster","sounds/thruster.ogg");


		gsm = new GameStateManager();

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		//we can keep all the update calls here, but it tends to get very messy
		//thus organise well by incorporating game states : play, menu, highscore etc

		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.draw();
	}
}
