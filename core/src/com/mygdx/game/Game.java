package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.mygdx.game.utilities.ResourceManager;

public class Game extends ApplicationAdapter {

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private TiledMapRenderer tiledMapRenderer;
    private PlayerManager playerManager;

    @Override
    public void create(){
        batch = new SpriteBatch();
        this.initCamera();
        this.initMap();
        this.initPlayers();
    }

    private void initCamera(){
        camera = new OrthographicCamera(ResourceManager.width, ResourceManager.height);
        camera.setToOrtho(false,ResourceManager.width, ResourceManager.height);
        camera.update();
    }

    private void initMap(){
        tiledMapRenderer = new OrthogonalTiledMapRenderer(ResourceManager.getMap());
    }

    private void initPlayers(){
        playerManager = new PlayerManager();
    }

    @Override
    public void render () {
        playerManager.handleInput(Gdx.graphics.getDeltaTime());
        playerManager.updateServer(Gdx.graphics.getDeltaTime());

        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        batch.begin();
        playerManager.render(batch, camera);
        batch.end();
    }

    @Override
    public void dispose(){
        super.dispose();
        playerManager.dispose();
    }

}
