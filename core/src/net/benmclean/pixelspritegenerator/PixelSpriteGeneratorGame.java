package net.benmclean.pixelspritegenerator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PixelSpriteGeneratorGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	Texture pixmaptex;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		Pixmap pixmap = new Pixmap( 64, 64, Pixmap.Format.RGBA8888 );
		pixmap.setColor( 0, 1, 0, 0.75f );
		pixmap.fillCircle( 32, 32, 32 );
		pixmaptex = new Texture( pixmap );
		pixmap.dispose();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
        batch.draw(pixmaptex, 300, 300);
		batch.end();
	}
}
