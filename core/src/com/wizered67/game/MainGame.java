package com.wizered67.game;

import com.badlogic.gdx.Game;

public class MainGame extends Game {
	GameScreen gameScreen;
	@Override
	public void create() {
		GameManager.init(this);
		gameScreen = new GameScreen(this);
        setScreen(gameScreen);       
	}
	
}
