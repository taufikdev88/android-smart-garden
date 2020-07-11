package com.example.fishfeeder.adapter;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class WsCommand {
    private String command;

    public WsCommand(String _command){
        this.command = _command;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @NonNull
    public String toString(){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "command");
            jsonObject.put("command", this.command);
            return jsonObject.toString();
        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }
}
