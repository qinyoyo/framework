package top.iotequ.framework.event;

import org.springframework.context.ApplicationEvent;

import top.iotequ.framework.pojo.User;

public class UserChangedEvent  extends ApplicationEvent {
	private static final long serialVersionUID = 1L;
	static public final int update = 1;
	static public final int insert = 2;
	static public final int delete = 3;
	private User user;
	private String idList;
	private int    mode;
	public UserChangedEvent(Object source,User user,boolean newUser) {
		super(source);
		setUser(user);
		setMode(newUser?insert:update);
	}
	public UserChangedEvent(Object source,String ids) {
		super(source);
		setIdList(ids);
		setMode(delete);
	}
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getIdList() {
		return idList;
	}

	public void setIdList(String idList) {
		this.idList = idList;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}
	
}
