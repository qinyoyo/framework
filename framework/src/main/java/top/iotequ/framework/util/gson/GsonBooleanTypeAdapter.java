package top.iotequ.framework.util.gson;

import java.io.IOException;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import top.iotequ.framework.exception.IotequIOException;
import top.iotequ.framework.exception.IotequThrowable;
import top.iotequ.framework.util.Util;

public class GsonBooleanTypeAdapter extends TypeAdapter<Boolean> {
	@Override
	public void write(JsonWriter out, Boolean value) throws IOException {
		if (value == null) {
            out.nullValue();
        } else {
            out.value(value);
        }		
	}
	@Override
	public Boolean read(JsonReader in) throws IOException {
        JsonToken peek = in.peek();
        switch (peek) {
            case BOOLEAN:
                return in.nextBoolean();
            case NULL:
                in.nextNull();
                return null;
            case NUMBER:
                return in.nextInt() != 0;
            case STRING:
                return Util.boolValue(in.nextString());
            default:
                throw new IotequIOException(IotequThrowable.IO_FORMATTER_ERROR,"Expected BOOLEAN or NUMBER but was " + peek);
        }
	}
}
