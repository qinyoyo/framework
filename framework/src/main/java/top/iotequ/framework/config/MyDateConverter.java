package top.iotequ.framework.config;

import java.util.Date;

import org.springframework.core.convert.converter.Converter;

import top.iotequ.framework.util.DateUtil;

public class MyDateConverter implements Converter<String,Date> {
	    @Override
	    public Date convert(String s) {
	        return DateUtil.string2Date(s);
	    }
}
