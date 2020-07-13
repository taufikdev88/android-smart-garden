package com.example.fishfeeder.adapter;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class WsRegister {
    private String uniqueid;
    private String uniquekey;

    public WsRegister(String _uniqueid, String _uniquekey){
        this.uniqueid = _uniqueid;
        this.uniquekey = _uniquekey;
    }

    public String getUniqueid() {
        return uniqueid;
    }

    public void setUniqueid(String uniqueid) {
        this.uniqueid = uniqueid;
    }

    public String getUniquekey() {
        return uniquekey;
    }

    public void setUniquekey(String uniquekey) {
        this.uniquekey = uniquekey;
    }

    @NonNull
    public String toString(){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "register");
            jsonObject.put("ishw", false);
            jsonObject.put("uniqueid", this.uniqueid);
            jsonObject.put("uniquekey", this.uniquekey);
            return jsonObject.toString(); // {"type": "register","ishw": false ....
        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }
}
