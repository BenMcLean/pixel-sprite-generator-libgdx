package net.benmclean.pixelspritegenerator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import net.benmclean.pixelspritegenerator.pixelspritegenerator.Mask;
import net.benmclean.pixelspritegenerator.pixelspritegenerator.Sprite;

public class PixelSpriteGeneratorGame extends ApplicationAdapter {
    private long SEED;
    private SpriteBatch batch;
	private Texture pixmaptex;
    private Stage stage;
    private VisTable table;
    private VisTextField seedTextField;
    private VisTextButton seedButton;
    private VisTextButton timerButton;

    public void newSprite (long SEED) {
        this.SEED = SEED;
        seedTextField.setText(Long.toString(SEED));
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
        VisUI.load();
        batch = new SpriteBatch();
        stage = new Stage(new FitViewport(640, 320), batch);
        Gdx.input.setInputProcessor(stage);

        table = new VisTable();
        table.setFillParent(true);

        //table.setDebug(true);

        seedTextField = new VisTextField();
        table.add(seedTextField);

        seedButton = new VisTextButton("Seed from input");
        seedButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                try {
                    newSprite(Long.parseLong(seedTextField.getText()));
                }
                catch (NumberFormatException exception){};
                return true;
            }
        });
        table.add(seedButton);

        timerButton = new VisTextButton("Seed from timer");
        timerButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                newSprite(System.currentTimeMillis());
                return true;
            }
        });
        table.add(timerButton);

        table.center().top();
        stage.addActor(table);

        newSprite(System.currentTimeMillis());
	}

    public void resize (int width, int height) {
        stage.getViewport().update(width, height, true);
    }

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
        final float scale = 256;
        batch.draw(pixmaptex, scale/12, scale/12, scale, scale);
		batch.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
	}

    public void dispose () {
        stage.dispose();
        VisUI.dispose();
    }
}
