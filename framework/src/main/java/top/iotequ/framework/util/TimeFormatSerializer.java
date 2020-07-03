package top.iotequ.framework.util;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeFormatSerializer implements JsonSerializer<Date>,JsonDeserializer<Date> {
   @Override
   public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
       return context.serialize(new SimpleDateFormat("HH:mm:ss").format(src));
   }
	@Override
	public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		if (json==null || json.isJsonNull()) return null;
		String res = json.getAsString();
		return DateUtil.string2Date(res);
	}
}
