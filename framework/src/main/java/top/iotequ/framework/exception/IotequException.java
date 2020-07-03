package top.iotequ.framework.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import top.iotequ.framework.util.SqlUtil;
import top.iotequ.framework.util.Util;

import javax.servlet.ServletException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IotequException extends ServletException implements IotequThrowable {
	private static final long serialVersionUID = 1L;
	private String error;
	private String addtionalMessage;
	@Override
	public String getError() {
		return error;
	}
	@Override
	public String getAddtionalMessage() { return addtionalMessage; }
	public IotequException(String error,String msg) {
		super(IotequThrowable.mergeMessage(error,msg));
		this.error=error;
		this.addtionalMessage=msg;
	}
	/*
	public IotequException(Exception e) {
		super(e.getMessage(), e);
		this.addtionalMessage = e.getMessage();
		this.error = IotequThrowable.EXCEPTION;
	}
	*/
	public static IotequException newInstance(Exception e) {
		if (e instanceof IotequException) return (IotequException)e;
		else {
			if (e instanceof DataAccessException) return SqlUtil.changeException((DataAccessException)e);
			else {
				String msg=null;
				String clazz=e.getClass().getSimpleName();
				if (e.getCause()!=null ) msg=e.getCause().getMessage();
				if (Util.isEmpty(msg)) msg=e.getLocalizedMessage();
				return new IotequException("exception."+clazz,msg);
			}
		}
	}

	@Override
	public String getMessage() {
		return IotequThrowable.mergeMessage(error,addtionalMessage);
	}

	@Override
	public String getLocalizedMessage() {
		return getMessage();
	}
}
