package top.iotequ.framework.exception;

import java.io.IOException;

public class IotequIOException extends IOException  implements IotequThrowable {
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

	public IotequIOException(String error,String msg) {
		super(IotequThrowable.mergeMessage(error,msg));
		this.error=error;
		this.addtionalMessage=msg;
	}
	public IotequException iotequException() {
		return new IotequException(error,addtionalMessage);
	}
}
