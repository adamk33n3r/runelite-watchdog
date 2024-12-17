package com.adamk33n3r.runelite.watchdog;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;

@Slf4j
public class MixedCaseEnumAdapter implements JsonDeserializer<Enum> {
    @Override
    public Enum deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        try {
            if (type instanceof Class && ((Class<?>) type).isEnum()) {
                return Enum.valueOf((Class<Enum>) type, jsonElement.getAsString().toUpperCase());
            }
            return null;
        } catch (Exception e) {
            log.error("Failed to deserialize enum", e);
            return null;
        }
    }
}
