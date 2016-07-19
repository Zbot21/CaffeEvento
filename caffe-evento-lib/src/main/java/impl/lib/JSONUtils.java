package impl.lib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by chris on 7/18/16.
 */
public final class JSONUtils {
    private JSONUtils() {}
    private static final Gson gson = new GsonBuilder().create();

    public static <T> String convertToJson(T object) {
        return gson.toJson(object);
    }

    public static <T> T convertFromJson(String json, Class<? extends T> objectType) {
        return gson.fromJson(json, objectType);
    }
}
