package com.wizered67.game;

import com.badlogic.gdx.Game;
import com.wizered67.game.Screens.GameScreen;

public class GameManager {
	public static Game game;
	public static void init(Game g){
		game = g;
	}
	public static GameScreen getGameScreen(){
		if (game.getScreen() instanceof GameScreen){
			return (GameScreen) game.getScreen();
		}
		else
			return null;
	}
}
