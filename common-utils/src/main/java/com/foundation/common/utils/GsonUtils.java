package com.foundation.common.utils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.DateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

/**
 * @author : jacksonz
 * @date : 2022/7/9 13:47
 * @description :
 */
public class GsonUtils {

    private static final TypeAdapter<LocalDateTime> LOCAL_DATE_TIME_TYPE_ADAPTER = new TypeAdapter<LocalDateTime>() {
        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            if (Objects.nonNull(value)) {
                out.value(value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            }
            else {
                out.nullValue();
            }
        }

        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            final JsonToken token = in.peek();
            if (token == JsonToken.NULL) {
                return null;
            }
            else {
                return LocalDateTime.ofInstant(Instant.ofEpochMilli(in.nextLong()), ZoneId.systemDefault());
            }
        }
    };

    private static final TypeAdapter<Date> DATE_TYPE_ADAPTER = new TypeAdapter<Date>() {
        @Override
        public void write(JsonWriter out, Date value) throws IOException {
            if (Objects.nonNull(value)) {
                out.value(value.getTime());
            }
            else {
                out.nullValue();
            }
        }

        @Override
        public Date read(JsonReader in) throws IOException {
            final JsonToken token = in.peek();
            if (token == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            else {
                return new Date(in.nextLong());
            }
        }
    };

    public static final Gson GSON = new GsonBuilder().setDateFormat(DateFormat.LONG)
            .disableHtmlEscaping()
            .registerTypeAdapter(Date.class, DATE_TYPE_ADAPTER)
            .registerTypeAdapter(LocalDateTime.class, LOCAL_DATE_TIME_TYPE_ADAPTER)
            .create();

    public static final Gson UNDERSCORE_GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setDateFormat(DateFormat.LONG)
            .disableHtmlEscaping()
            .registerTypeAdapter(Date.class, DATE_TYPE_ADAPTER)
            .registerTypeAdapter(LocalDateTime.class, LOCAL_DATE_TIME_TYPE_ADAPTER)
            .create();
}
