package net.benmclean.pixelspritegenerator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import net.benmclean.pixelspritegenerator.pixelspritegenerator.Mask;
import net.benmclean.pixelspritegenerator.pixelspritegenerator.Sprite;

public class PixelSpriteGeneratorGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture pixmaptex;

    public void newSprite (long SEED) {
        Sprite sprite = new Sprite(12, 12, new Mask(new int[]{
                0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 1, 1,
                0, 0, 0, 0, 1,-1,
                0, 0, 0, 1, 1,-1,
                0, 0, 0, 1, 1,-1,
                0, 0, 1, 1, 1,-1,
                0, 1, 1, 1, 2, 2,
                0, 1, 1, 1, 2, 2,
                0, 1, 1, 1, 2, 2,
                0, 1, 1, 1, 1,-1,
                0, 0, 0, 1, 1, 1,
                0, 0, 0, 0, 0, 0
        }, 6, 12, true, false), true, 0.3, 0.2, 0.3, 0.5, SEED);

        Pixmap pixmap = new Pixmap(sprite.getHeight(), sprite.getWidth(), Pixmap.Format.RGBA8888 );
        int[] spritePixels = sprite.renderPixelData();
        int height = sprite.getHeight();
        int width = sprite.getWidth();

        for (int x=0; x<height; x++)
            for (int y=0; y<width; y++) {
                int i = (width * y + x)*4;
                int red = spritePixels[i];
                int green = spritePixels[i + 1];
                int blue = spritePixels[i + 2];
                int alpha = spritePixels[i + 3];
                pixmap.drawPixel(x, y, Color.rgba8888(red/255f,green/255f,blue/255f,alpha/255f));
            }

        pixmaptex = new Texture( pixmap );
        pixmaptex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pixmap.dispose();
    }
	
	@Override
	public void create () {
        batch = new SpriteBatch();
        long SEED = System.currentTimeMillis();
        newSprite(SEED);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
        batch.draw(pixmaptex, 16, 16, pixmaptex.getHeight() * 16, pixmaptex.getWidth() * 16);
		batch.end();
	}
}
