package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.sprites.Starship;
import com.mygdx.game.utilities.ResourceManager;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PlayerManager {

    private Socket socket;
    private Starship player;
    private Texture playerShip;
    private Texture friendlyShip;
    private HashMap<String, Starship> players;
    private float timer = 0f;

    public PlayerManager(){
        this.initTextures();
        this.connectSocket();
        this.configSocketEvents();
        players = new HashMap<>();
    }

    private void initTextures(){
        playerShip = new Texture("playerShip2.png");
        friendlyShip = new Texture("playerShip.png");
    }

    private void connectSocket(){
        try {
            socket = IO.socket("http://localhost:8080");
            socket.connect();
        } catch (Exception e){
            System.out.println(e);
        }
    }

    public void configSocketEvents(){
        socket.on(Socket.EVENT_CONNECT, args -> player = new Starship(playerShip)).on("newPlayer", args -> {
            JSONObject data = (JSONObject) args[0];
            try {
                String newPlayerID = data.getString("id");
                players.put(newPlayerID, new Starship(friendlyShip));
            } catch (JSONException e) {
                Gdx.app.log("SocketIO", "Error adding new Player");
            }
        }).on("playerDisconnected", args -> {
            JSONObject data = (JSONObject) args[0];
            try {
                String anotherPlayerID = data.getString("id");
                players.remove(anotherPlayerID);
            } catch (JSONException e) {
                Gdx.app.log("SocketIO", "Error remove old Player");
            }
        }).on("getPlayers", args ->  {
            JSONArray objects = (JSONArray) args[0];
            try {
                for(int i = 0; i < objects.length(); i++){
                    Starship coopPlayer = new Starship(friendlyShip);
                    float x = ((Double) objects.getJSONObject(i).getDouble("x")).floatValue();
                    float y = ((Double) objects.getJSONObject(i).getDouble("y")).floatValue();
                    coopPlayer.setPosition(x, y);
                    players.put(objects.getJSONObject(i).getString("id"), coopPlayer);
                }
            }catch (JSONException e){
                Gdx.app.log("SocketIO", "Error get all Players");
            }
        }).on("playerMoved", args -> {
            JSONObject data = (JSONObject) args[0];
            try {
                String playerId = data.getString("id");
                Double x = data.getDouble("x");
                Double y = data.getDouble("y");
                if(players.get(playerId) != null){
                    players.get(playerId).setPosition(x.floatValue(),y.floatValue());
                }
            } catch (JSONException e) {
                Gdx.app.log("SocketIO", "Error move Player");
            }
        });
    }

    public void render(SpriteBatch batch, OrthographicCamera camera) {
        if (this.isPlayer()) {
            camera.position.x = player.getX();
            camera.position.y = player.getY();
            camera.update();
            player.draw(batch);
        }
        for (HashMap.Entry<String, Starship> entry : players.entrySet()) {
            entry.getValue().draw(batch);
        }
    }

    public void updateServer(float deltaTime){
        timer += deltaTime;
        if(timer >= ResourceManager.updateTime() && player != null && player.hasMoved()){
            JSONObject data = new JSONObject();
            try {
                data.put("x", player.getX());
                data.put("y", player.getY());
                socket.emit("playerMoved", data);
            }catch (JSONException e){
                Gdx.app.log("SOCKET.IO", "Error");
            }
        }
    }

    public void dispose(){
        playerShip.dispose();
        friendlyShip.dispose();
    }

    public boolean isPlayer(){
        return player != null;
    }

    public void handleInput(float deltaTime){
        if(this.isPlayer()) this.player.handleInput(deltaTime);
    }

}
