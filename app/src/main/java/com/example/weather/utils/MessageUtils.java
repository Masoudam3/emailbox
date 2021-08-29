package com.example.weather.utils;


import com.example.weather.model.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MessageUtils {

    public static List<Message> jsonArrayToMessageList(JSONArray jsonArray){
        List<Message> list = new ArrayList<>();
        try {
            for(int i = 0 ; i < jsonArray.length(); i++){
                JSONObject json = jsonArray.getJSONObject(i);
                Message message = new Message();
                list.add(message);

                message.setId(json.getLong("id"));
                message.setFrom(json.getString("from"));
                message.setImportant(json.getBoolean("isImportant"));
                message.setMessage(json.getString("message"));
                message.setPicture(json.getString("picture"));
                message.setRead(json.getBoolean("isRead"));
                message.setSubject(json.getString("subject"));
                message.setTimestamp(json.getString("timestamp"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }


}
