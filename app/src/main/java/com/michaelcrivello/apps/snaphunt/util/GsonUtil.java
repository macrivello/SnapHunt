package com.michaelcrivello.apps.snaphunt.util;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.bson.types.ObjectId;

import retrofit.converter.GsonConverter;

public class GsonUtil {

    public static Gson getMongoDocGson(){
        GsonBuilder gb = new GsonBuilder();
        // Deserializer
        gb.registerTypeAdapter(ObjectId.class, new GsonUtil.ObjectIdDeserializer());
        gb.registerTypeAdapter(Date.class, new GsonUtil.DateDeserializer());

        // Serializer
        gb.registerTypeAdapter(ObjectId.class, new GsonUtil.ObjectIdSerializer());
        gb.registerTypeAdapter(Date.class, new GsonUtil.DateSerializer());

        return gb.create();
    }

    /**
     * ObjectIdDeserializer.deserialize
     * @return Bson.Types.ObjectId
     */
    public static class ObjectIdDeserializer implements JsonDeserializer<ObjectId>
    {
        @Override
        public ObjectId deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            try
            {
                return new ObjectId(json.getAsJsonObject().get("$oid").getAsString());
            }
            catch (Exception e)
            {
                return null;
            }
        }
    }

    /**
     * ObjectIdSerializer.serialize
     * @return $oid JsonObject from BSON ObjectId
     */
    public static class ObjectIdSerializer implements JsonSerializer<ObjectId>
    {
        @Override
        public JsonElement serialize(ObjectId id, Type typeOfT, JsonSerializationContext context)
        {
            JsonObject jo = new JsonObject();
            jo.addProperty("$oid", id.toHexString());
            return jo;
        }
    }

    /**
     * DateDeserializer.deserialize
     * @return Java.util.Date
     */
    public static class DateDeserializer implements JsonDeserializer<Date>
    {
        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException
        {
            Date d = null;
            SimpleDateFormat f2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            try
            {
                d = f2.parse(json.getAsJsonObject().get("$date").getAsString());
            }
            catch (ParseException e)
            {
                d = null;
            }
            return d;
        }
    }

    /**
     * DateSerializer.serialize
     * @return date JsonElement
     */
    public static class DateSerializer implements JsonSerializer<Date>
    {
        @Override
        public JsonElement serialize(Date date, Type typeOfT, JsonSerializationContext context)
        {
            Date d = (Date)date;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            JsonObject jo = new JsonObject();
            jo.addProperty("$date", format.format(d));
            return jo;
        }
    }
}