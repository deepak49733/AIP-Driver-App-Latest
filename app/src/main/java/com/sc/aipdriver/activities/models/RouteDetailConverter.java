package com.sc.aipdriver.activities.models;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sc.aipdriver.activities.models.LiRoutePlannerDetail;

import java.lang.reflect.Type;
import java.util.List;

public class RouteDetailConverter {

    @TypeConverter
    public static String fromList(List<LiRoutePlannerDetail> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<LiRoutePlannerDetail> toList(String value) {
        Type listType = new TypeToken<List<LiRoutePlannerDetail>>(){}.getType();
        return new Gson().fromJson(value, listType);
    }
}