package top.iotequ.framework.util.gson;

import java.io.IOException;
import java.util.Date;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import top.iotequ.framework.exception.IotequIOException;
import top.iotequ.framework.exception.IotequThrowable;
import top.iotequ.framework.util.DateUtil;

public class GsonDateTypeAdapter extends TypeAdapter<Date> {
	@Override
	public void write(JsonWriter out, Date value) throws IOException {
		if (value == null) {
            out.nullValue();
        } else {
            out.value(DateUtil.date2String(value,null));
        }		
	}
	@Override
	public Date read(JsonReader in) throws IOException {
        JsonToken peek = in.peek();
        switch (peek) {
            case NULL:
                in.nextNull();
                return null;
            case NUMBER:
                return new Date(in.nextLong());
            case STRING:
                return DateUtil.string2Date(in.nextString());
            default:
            	throw new IotequIOException(IotequThrowable.IO_FORMATTER_ERROR,"Expected Date or String but was " + peek);
        }
	}
}