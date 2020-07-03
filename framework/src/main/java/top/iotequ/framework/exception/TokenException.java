package top.iotequ.framework.exception;

public class TokenException extends IllegalArgumentException implements IotequThrowable {
	private static final long serialVersionUID = 1L;
	private String error;
	private String addtionalMessage;
	@Override
	public String getError() {
		return error;
	}
	@Override
	public String getAddtionalMessage() { return addtionalMessage; }
    public TokenException(String error, String msg) {
		super(IotequThrowable.mergeMessage(error,msg));
		this.error=error;
		this.addtionalMessage=msg;
	}
	public IotequException iotequException() {
		return new IotequException(error,addtionalMessage);
	}
}
