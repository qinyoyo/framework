package top.iotequ.framework.util.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import top.iotequ.framework.exception.IotequIOException;
import top.iotequ.framework.exception.IotequThrowable;
import top.iotequ.framework.util.DateUtil;

import java.io.IOException;
import java.sql.Time;

public class GsonTimeTypeAdapter extends TypeAdapter<Time> {
    @Override
    public void write(JsonWriter out, Time value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(DateUtil.date2String(value,null));
        }
    }
    @Override
    public Time read(JsonReader in) throws IOException {
        JsonToken peek = in.peek();
        switch (peek) {
            case NULL:
                in.nextNull();
                return null;
            case NUMBER:
                return new Time(in.nextLong());
            case STRING:
                return new Time(DateUtil.string2Date(in.nextString()).getTime());
            default:
                throw new IotequIOException(IotequThrowable.IO_FORMATTER_ERROR,"Expected Date or String but was " + peek);
        }
    }
}