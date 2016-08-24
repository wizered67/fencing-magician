package com.wizered67.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

public class GUIManager {
	private static Table table;
	private static Skin skin = new Skin();
	private static Label textBox;
	private static Stage stage;
	private final static Vector2 TEXTBOX_SIZE = new Vector2(200, 80);
	private static String remainingText = "";
	private static int textTimer = 4;
	private static int numLines = 0;

	public GUIManager(Stage st){
		stage = st;
 		// Generate a 1x1 white texture and store it in the skin named "white".
 		Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
 		pixmap.setColor(Color.WHITE);
 		pixmap.fill();
 		skin.add("white", new Texture(pixmap));

 		// Store the default libgdx font under the name "default".
 		skin.add("default", new BitmapFont());
    	 table = new Table();
    	 table.setFillParent(true);
    	 stage.addActor(table);
	     table.setDebug(true); // This is optional, but enables debug lines for tables.
    	    // Add widgets to the table here.
	     
	     TextButtonStyle textButtonStyle = new TextButtonStyle();
			textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
			textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
			textButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
			textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
			textButtonStyle.font = skin.getFont("default");
			skin.add("default", textButtonStyle);
			final TextButton button = new TextButton("Click me!", skin);
			button.setPosition(40, 40);
			button.setSize(60, 60);
			//table.add(button).pad(20).expand().bottom().left();
			stage.addActor(button);
			// Add a listener to the button. ChangeListener is fired when the button's checked state changes, eg when clicked,
			// Button#setChecked() is called, via a key press, etc. If the event.cancel() is called, the checked state will be reverted.
			// ClickListener could have been used, but would only fire when clicked. Also, canceling a ClickListener event won't
			// revert the checked state.
			button.addListener(new ChangeListener() {
				public void changed (ChangeEvent event, Actor actor) {
					System.out.println("Clicked! Is checked: " + ((Button)actor).isChecked());
					((TextButton)actor).setText("Good job!");
				}
			});

		Label.LabelStyle labelStyle = new Label.LabelStyle();
		labelStyle.font = skin.getFont("default");
		labelStyle.background = skin.newDrawable("white", Color.DARK_GRAY);
		skin.add("default", labelStyle);
		textBox = new Label("", skin);
		textBox.setAlignment(Align.topLeft);
		//textBox
		//
		textBox.setStyle(labelStyle);
		remainingText = "THIS IS A MESSAGE I AM TESTING TO SEE IF IT PROPERLY SCROLLS AND STUFF AND I KNOW THERE'S A LOT TO FIX AND STUFF BUT IT SEEMS TO WORK!";
		//textBox.setText("TESTING A MESSAGE BRO");
		//textBox = new Label("this is a really long test message and I want to see if word wrap is doing anything? Test Message!", labelStyle);
		textBox.setWrap(true);
		textBox.setPosition(200, 400);
		textBox.setSize(500, 200);
		textBox.setVisible(false);
		stage.addActor(textBox);
	}

	public static Stage getStage(){
		return stage;
	}

	public static void setTextBoxShowing(boolean show){
		textBox.setVisible(show);
	}

	public static void update(float deltaTime){
		textTimer = Math.max(textTimer - 1, 0);
		if (textTimer <= 0 && !remainingText.isEmpty()) {
			String newText = textBox.getText().append(remainingText.charAt(0)).toString();
			String testText = textBox.getText().append(remainingText.split(" ")[0]).toString();
			System.out.println(remainingText.split(" ")[0]);
			//textBox.setText(testText);
			textBox.getGlyphLayout().setText(textBox.getStyle().font, testText, Color.WHITE, textBox.getWidth(), Align.left, true);
			int currentNumLines = textBox.getGlyphLayout().runs.size;
			if (currentNumLines != 1 && currentNumLines > numLines){
				newText = "\n" + newText;
			}
			numLines = currentNumLines;
			textBox.setText(newText);
			remainingText = remainingText.substring(1);
			textTimer = 2;
			textBox.invalidate();
		}
		else if (remainingText.isEmpty()){
			textBox.setVisible(false);
		}
	}

	public static void resize(int width, int height){
		textBox.setSize(TEXTBOX_SIZE.x * width / Constants.VIRTUAL_WIDTH,
				TEXTBOX_SIZE.y * height / Constants.VIRTUAL_HEIGHT);
		textBox.setPosition((width - textBox.getWidth()) / 2,
				height - height / 8 - textBox.getHeight());
	}

	public static void setRemainingText(String text){
		remainingText = text;
	}
}
