package net.benmclean.pixelspritegenerator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import net.benmclean.pixelspritegenerator.pixelspritegenerator.GenSprite;

public class PixelSpriteGeneratorGame extends ApplicationAdapter {
    private long SEED;
    private SpriteBatch batch;
    private Texture pixmaptex;
    private Stage stage;
    private Stage uiStage;
    private VisTable table;
    private VisTextField seedTextField;
    private VisTextButton seedButton;
    private VisTextButton timerButton;

    @Override
    public void create() {
        VisUI.load();
        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport(), batch);
        uiStage = new Stage(new FitViewport(640, 320));
        Gdx.input.setInputProcessor(uiStage);

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
                } catch (NumberFormatException exception) {
                }
                ;
                return true;
            }
        });
        table.add(seedButton);

        timerButton = new VisTextButton("Seed from timer");
        timerButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                newSprite();
                return true;
            }
        });
        table.add(timerButton);

        table.center().top();
        uiStage.addActor(table);

        newSprite();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        uiStage.getViewport().update(width, height, true);
        VisUI.getSizes().scaleFactor = 999999;
        newSprite(0);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        final float scale = stage.getViewport().getScreenHeight() * 0.75f;
        batch.draw(pixmaptex, scale / 12, scale / 12, scale, scale);
        batch.end();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        uiStage.act(Gdx.graphics.getDeltaTime());
        uiStage.draw();
    }

    public void dispose() {
        stage.dispose();
        VisUI.dispose();
    }

    public void newSprite() {
        newSprite(System.currentTimeMillis());
    }

    public void newSprite(long SEED) {
        this.SEED = SEED;
        seedTextField.setText(Long.toString(SEED));
        Pixmap pixmap = GenSprite.generatePixmap(new GenSprite.Mask(new int[]{
                0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 1, 1,
                0, 0, 0, 0, 1, -1,
                0, 0, 0, 1, 1, -1,
                0, 0, 0, 1, 1, -1,
                0, 0, 1, 1, 1, -1,
                0, 1, 1, 1, 2, 2,
                0, 1, 1, 1, 2, 2,
                0, 1, 1, 1, 2, 2,
                0, 1, 1, 1, 1, -1,
                0, 0, 0, 1, 1, 1,
                0, 0, 0, 0, 0, 0
        }, 6, 12, true, false), true, 0.3f, 0.2f, 0.3f, 0.5f, SEED);

        pixmaptex = new Texture(pixmap);
        pixmaptex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        pixmap.dispose();
    }
}
