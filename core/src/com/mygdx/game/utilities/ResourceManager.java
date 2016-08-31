package com.mygdx.game.utilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class ResourceManager {

    private static TiledMap tiledMap = new TmxMapLoader().load("map.tmx");

    public static float width = Gdx.graphics.getWidth();
    public static float height = Gdx.graphics.getHeight();

    public static float updateTime(){
        return 1/60f;
    }

    public static TiledMap getMap() {return tiledMap; }

}
