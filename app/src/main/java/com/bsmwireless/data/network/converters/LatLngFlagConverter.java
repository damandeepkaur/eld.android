package com.bsmwireless.data.network.converters;

import com.bsmwireless.models.ELDEvent;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class LatLngFlagConverter implements JsonDeserializer<ELDEvent.LatLngFlag>, JsonSerializer<ELDEvent.LatLngFlag> {
    @Override
    public ELDEvent.LatLngFlag deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return json.isJsonNull() ?
                ELDEvent.LatLngFlag.FLAG_NONE :
                ELDEvent.LatLngFlag.createbyCode(json.getAsString());
    }

    @Override
    public JsonElement serialize(ELDEvent.LatLngFlag src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getCode());
    }
}
