package top.iotequ.framework.event;

import org.springframework.context.ApplicationEvent;

import top.iotequ.framework.pojo.Org;

public class OrgChangedEvent extends ApplicationEvent {
	private static final long serialVersionUID = 1L;
	static public final int update = 1;
	static public final int insert = 2;
	static public final int delete = 3;
	private Org org;
	private String idList;
	private int    mode;
	public OrgChangedEvent(Object source,Org org,boolean newOrg) {
		super(source);
		setOrg(org);
		setMode(newOrg?insert:update);
	}
	public OrgChangedEvent(Object source,String ids) {
		super(source);
		setIdList(ids);
		setMode(delete);
	}
	public Org getOrg() {
		return org;
	}

	public void setOrg(Org org) {
		this.org = org;
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
