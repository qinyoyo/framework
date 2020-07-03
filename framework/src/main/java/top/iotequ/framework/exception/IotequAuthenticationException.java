package top.iotequ.framework.exception;

import org.springframework.security.core.AuthenticationException;

public class IotequAuthenticationException extends AuthenticationException implements IotequThrowable {
	private static final long serialVersionUID = 1L;
	private String error;
	private String addtionalMessage;
	@Override
	public String getError() {
		return error;
	}
	@Override
	public String getAddtionalMessage() {
		return addtionalMessage;
	}
	public IotequAuthenticationException(String error,String msg) {
		super(IotequThrowable.mergeMessage(error,msg));
		this.error=error;
		this.addtionalMessage=msg;
	}
	public IotequException iotequException() {
		return new IotequException(error,addtionalMessage);
	}
}
