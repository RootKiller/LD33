package es.bimgam.ld33.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Color;

public class Font {
	public static final String POLISH_CHARACTERS = "AĄBCĆDEĘFGHIJKLŁMNŃOÓPQRSŚTUVWXYZŻŹ"
		+ "aąbcćdeęfghijklłmnńoópqrsśtuvwxyzżź"
		+ "1234567890.,:;_¡!?\"'+-*/()[]={}";

	private BitmapFont font;

	private Graphics graphics;

	private GlyphLayout glyphLayout;

	public Font(String fontName, Integer size) {
		this.graphics = Gdx.graphics;

		FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		fontParameter.size = size;
		fontParameter.characters = POLISH_CHARACTERS;

		FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal(fontName));
		this.font = fontGenerator.generateFont(fontParameter);

		fontGenerator.dispose();
		glyphLayout = new GlyphLayout();
	}

	public void draw(Batch batch, CharSequence str, float x, float y) {
		this.glyphLayout.setText(font, str);
		this.font.draw(batch, glyphLayout, x, graphics.getHeight() - y);
	}

	public void draw(Batch batch, CharSequence str, float x, float y, Color color, float targetWidth, int halign, boolean wrap) {
		this.glyphLayout.setText(font, str, color, targetWidth, halign, wrap);
		this.font.draw(batch, glyphLayout, x, graphics.getHeight() - y);
	}

	public float getRenderWidth(CharSequence str) {
		this.glyphLayout.setText(font, str);
		return this.glyphLayout.width;
	}

	public float getRenderWidth(CharSequence str, float targetWidth, int halign, boolean wrap) {
		this.glyphLayout.setText(font, str, Color.WHITE, targetWidth, halign, wrap);
		return this.glyphLayout.width;
	}

	public float getRenderHeight(CharSequence str) {
		this.glyphLayout.setText(font, str);
		return this.glyphLayout.height;
	}

	public float getRenderHeight(CharSequence str, float targetWidth, int halign, boolean wrap) {
		this.glyphLayout.setText(font, str, Color.WHITE, targetWidth, halign, wrap);
		return this.glyphLayout.height;
	}
    
	public void dispose() {
		this.font.dispose();
    }
}
