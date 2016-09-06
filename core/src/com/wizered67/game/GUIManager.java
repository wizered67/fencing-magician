package com.wizered67.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

public class GUIManager {
	private static Table table;
	private static Skin skin = new Skin();
	private static Label textBox;
    private static Label speakerLabel;
	private static Stage stage;
	private final static Vector2 TEXTBOX_SIZE = new Vector2(250, 80);
	private static String remainingText = "";
    private static String remainingTextNoTags = "";
	private static int textTimer = 4;
	private static int numLines = 0;
    private final static int LEFT_PADDING = 10;
    private static boolean dummyTagAdded = false;

    public GUIManager(Stage st){
		stage = st;
 		// Generate a 1x1 white texture and store it in the skin named "white".
 		Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
 		pixmap.setColor(Color.WHITE);
 		pixmap.fill();
 		skin.add("white", new Texture(pixmap));
 		// Store the default libgdx font under the name "default".
		BitmapFont defaultFont;
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("arial.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = 24;
		defaultFont = generator.generateFont(parameter); // font size 12 pixels
        defaultFont.getData().markupEnabled = true;
		generator.dispose(); // don't forget to dispose to avoid memory leaks!
 		skin.add("default", defaultFont);
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
		Drawable newDrawable = skin.newDrawable("white", Color.DARK_GRAY);
		newDrawable.setLeftWidth(LEFT_PADDING);
        newDrawable.setLeftWidth(LEFT_PADDING);
		//newDrawable.setRightWidth(20);
		labelStyle.background = newDrawable;
		skin.add("default", labelStyle);
		textBox = new Label("", skin);
		textBox.setAlignment(Align.topLeft);
		textBox.setStyle(labelStyle);
        textBox.setWrap(true);
        stage.addActor(textBox);

        Label.LabelStyle speakerLabelStyle = new Label.LabelStyle();
        speakerLabelStyle.font = skin.getFont("default");
        Drawable speakerDrawable = skin.newDrawable("white", Color.GRAY);
        speakerDrawable.setLeftWidth(5);
        //speakerDrawable.setRightWidth(5);
        //newDrawable.setRightWidth(20);
        speakerLabelStyle.background = speakerDrawable;
        skin.add("speaker", speakerLabelStyle);
        speakerLabel = new Label("Adam", skin, "speaker");
        //speakerLabel.setStyle(speakerLabelStyle);
        speakerLabel.setAlignment(Align.center);
        stage.addActor(speakerLabel);

		remainingText = "Yo bruh, this is a message that we're currently 'testing' here at the lab. Our top text scientists are attempting to determine if it properly scrolls. We are taking notes and keeping our observations in a top secret log!           ";
		remainingTextNoTags = removeTags(remainingText);
        //System.out.println(remainingTextNoTags);
        //remainingText = "this is a new message just so you know.";
		//textBox.setText("TESTING A MESSAGE BRO");
		//textBox = new Label("this is a really long test message and I want to see if word wrap is doing anything? Test Message!", labelStyle);

		//textBox.setPosition(200, 400);
		//textBox.setSize(350, 60);
		//textBox.setVisible(false);
		/*
        TextTooltip.TextTooltipStyle textTooltipStyle = new TextTooltip.TextTooltipStyle();
		textTooltipStyle.background = skin.newDrawable("white", Color.DARK_GRAY);
		textTooltipStyle.label = labelStyle;
		textTooltipStyle.wrapWidth = 300;
		skin.add("default", textTooltipStyle);
		TextTooltip textTooltip = new TextTooltip("This [GREEN]is [WHITE]a tooltip! This is a tooltip! This is a tooltip! This is a tooltip! This is a tooltip! This is a tooltip!", skin);
		//textTooltip.setAlways(true);
		textTooltip.setInstant(true);
		button.addListener(textTooltip);
		*/
		//Table tooltipTable = new Table(skin);
		//tooltipTable.pad(10).background("default-round");
		//tooltipTable.add(new TextButton("Fancy tooltip!", skin));

	}

    public static String removeTags(String s){
        boolean inTag = false;
        String output = "";
        for (int i = 0; i < s.length(); i++){
            char newChar = s.charAt(i);
            if (!inTag){ //not in tag so keep char unless starting tag
                if (newChar != '[') //todo, allow escape character [[?
                    output += newChar;
                else
                    inTag = true;
            }
            else{ //in tag so don't keep char. If closing bracket end tag
                if (newChar == ']')
                    inTag = false;
            }
        }
        return output;
    }

	public static Stage getStage(){
		return stage;
	}

	public static void setTextBoxShowing(boolean show){
		textBox.setVisible(show);
	}

	public static void update(float deltaTime){
		//textBox.getGlyphLayout().width = textBox.getWidth() - 400;

        textTimer = Math.max(textTimer - 1, 0);
		if (textTimer <= 0 && !remainingText.isEmpty()) {
            boolean textAdded = false;
            boolean tagAdded = false;
            String newText = null;
            String originalText = textBox.getText().toString();
            if (dummyTagAdded){
                //originalText = originalText.substring(0, originalText.length() - 2);
                dummyTagAdded = false;
            }
			while (!textAdded) {
                //String newText = textBox.getText().append(remainingText.charAt(0)).toString();
                String[] words = remainingTextNoTags.split(" ");
                String nextWord = "";
                if (words.length > 0)
                    nextWord = words[0];

                String testText = originalText + nextWord;//textBox.getText().append(nextWord).toString();
                //System.out.println(remainingText.split(" ")[0]);
                //textBox.setText(testText);
                textBox.getGlyphLayout().setText(textBox.getStyle().font, testText, Color.WHITE, textBox.getWidth() - LEFT_PADDING, Align.left, true);

                int currentNumLines = textBox.getGlyphLayout().runs.size;
                newText = originalText;
                String tag = getTag(remainingText);
                if (tag == null) {
                    if (currentNumLines != 1 && currentNumLines > numLines) {
                        newText = newText + "\n";
                    }
                    if (remainingText.length() != 0) {
                        newText += remainingText.charAt(0);
                        remainingText = remainingText.substring(1);
                        remainingTextNoTags = remainingTextNoTags.substring(1);
                    }
                    textAdded = true;

                } else {
                    tagAdded = true;
                    newText += tag;
                    remainingText = remainingText.substring(tag.length());
                    currentNumLines += 1;
                }
                numLines = currentNumLines;
                originalText = newText;
            }
            if (tagAdded){
                String closeTag = getTag(remainingText);
                if (closeTag != null){
                    //newText += closeTag;
                    //remainingText = remainingText.substring(closeTag.length());
                    dummyTagAdded = false;
                }
                else{
                    dummyTagAdded = true;
                    //newText += "[]";
                }
            }
            textTimer = 2;
            textBox.setText(newText);
            //System.out.println(textBox.getGlyphLayout());
            textBox.invalidate();
            //System.out.println(textBox.getText().toString());
		}
		else if (remainingText.isEmpty()){
			//textBox.setVisible(false);
            if (!speakerLabel.getText().toString().equals("Christine")) {
                setRemainingText("Yep, that's right!");
                setSpeaker("Christine");
            }
            //System.out.println(textBox.getGlyphLayout());
		}

	}

    public static String getTag(String s){
        if (s.length() == 0 || s.charAt(0) != '[')
            return null;
        else{
            String tag = "";
            for (int i = 0; i < s.length(); i++){
                char nextChar = s.charAt(i);
                tag += nextChar;
                if (nextChar == ']')
                    break;
            }
            return tag;
        }
    }

	public static void resize(int width, int height){
		textBox.setSize(TEXTBOX_SIZE.x * width / Constants.VIRTUAL_WIDTH,
				TEXTBOX_SIZE.y * height / Constants.VIRTUAL_HEIGHT);
		textBox.setPosition((width - textBox.getWidth()) / 2,
				height - height / 8 - textBox.getHeight());
        speakerLabel.setPosition(textBox.getX(), textBox.getY() + textBox.getHeight());
	}

	public static void setRemainingText(String text){
		remainingText = text;
        remainingTextNoTags = removeTags(text);
        textBox.setText("");
        textBox.invalidate();
	}

    public static void setSpeaker(String text){
        speakerLabel.setText(text);
        speakerLabel.setSize(speakerLabel.getPrefWidth(), speakerLabel.getPrefHeight());
        speakerLabel.invalidate();
    }
}
