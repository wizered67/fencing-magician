package com.wizered67.game;

import com.badlogic.gdx.Game;
import com.wizered67.game.Screens.GameScreen;

public class MainGame extends Game {
	GameScreen gameScreen;
	@Override
	public void create() {
		GameManager.init(this);
		gameScreen = new GameScreen();
        setScreen(gameScreen);       
	}
	
}
