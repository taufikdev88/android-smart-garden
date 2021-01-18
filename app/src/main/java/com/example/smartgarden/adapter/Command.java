package com.example.smartgarden.adapter;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class Command {
    private String command;
    private String id;
    private String key;

    public Command(){
        this.command = "";
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
            jsonObject.put("command", this.command);
            jsonObject.put("id", this.id);
            jsonObject.put("key", this.key);
            return jsonObject.toString();
        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
