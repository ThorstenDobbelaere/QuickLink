package demo.config.types;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import framework.configurables.Stringifier;
import framework.request.response.ContentType;

public class JsonStringifier implements Stringifier {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public String stringify(Object o) {
        return gson.toJson(o);
    }

    @Override
    public ContentType getContentType() {
        return ContentType.JSON;
    }
}
