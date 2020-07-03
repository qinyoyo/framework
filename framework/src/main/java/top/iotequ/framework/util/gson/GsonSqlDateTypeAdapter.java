package top.iotequ.framework.util.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import top.iotequ.framework.exception.IotequIOException;
import top.iotequ.framework.exception.IotequThrowable;
import top.iotequ.framework.util.DateUtil;

import java.io.IOException;
import java.sql.Date;


public class GsonSqlDateTypeAdapter extends TypeAdapter<Date> {
    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(DateUtil.date2String(value,"yyyy-MM-dd")+" 00:00:00");
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
                return new Date(DateUtil.string2Date(in.nextString()).getTime());
            default:
                throw new IotequIOException(IotequThrowable.IO_FORMATTER_ERROR,"Expected Date or String but was " + peek);
        }
    }
}