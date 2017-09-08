package com.bsmwireless.data.network.converters;

import com.bsmwireless.models.Malfunction;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class MalfunctionConverter implements JsonDeserializer<Malfunction>, JsonSerializer<Malfunction> {
    @Override
    public Malfunction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Malfunction.createByCode(json.getAsString());
    }

    @Override
    public JsonElement serialize(Malfunction src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getCode());
    }
}
