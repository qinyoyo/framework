/**************************************************
    Create by iotequ generator 3.0.0
    Author : Qinyoyo
**************************************************/

package top.iotequ.framework.pojo;
import top.iotequ.framework.util.StringUtil;
import top.iotequ.framework.pojo.CgEntity;
import lombok.Getter;
import lombok.Setter;
import com.google.gson.annotations.SerializedName;
import top.iotequ.framework.util.CgFieldAnnotation;
import top.iotequ.framework.util.CgTableAnnotation;
import java.util.*;

//  Pojo entity : Permission (功能分配表)
@CgTableAnnotation(name="sys_permission",
                   title="sysPermission",
                   baseUrl="/framework/sysPermission",
                   hasLicence=false,
                   pkType="Integer",
                   pkKeyType="1",
                   generatorName="sysPermission",
                   module="framework")
@Getter
@Setter
public class Permission implements CgEntity {
    @SerializedName(value = "id", alternate = {"ID"})
    @CgFieldAnnotation(name="id",jdbcType="INTEGER",nullable=false,format="")
    private Integer id;

    @SerializedName(value = "role", alternate = {"ROLE"})
    @CgFieldAnnotation(name="role",jdbcType="INTEGER",nullable=false,format="")
    private Integer role;		//角色 db field:role

    @SerializedName(value = "action", alternate = {"ACTION"})
    @CgFieldAnnotation(name="action",jdbcType="INTEGER",nullable=false,format="")
    private Integer action;		//功能 db field:action

    @Override public Object getPkValue(){ return getId(); }
    @Override
    public void setPkValue(Object value) {
        if (value==null) setId(null);
        else setId(Integer.valueOf(value.toString()));
    }
    @Override
    public String toString() {
    	return StringUtil.toJsonString(this);
    }
//^_^自定义代码: java代码 ,请不要删除和修改本行
	private Role roleObject;
	private Action actionObject; // 为了提高检索速度
	public Role getRoleObject() {
		return roleObject;
	}
	public void setRoleObject(Role roleObject) {
		this.roleObject = roleObject;
	}
	public Action getActionObject() {
		return actionObject;
	}
	public void setActionObject(Action actionObject) {
		this.actionObject = actionObject;
	}
//^_^自定义代码结束,请不要删除和修改本行
}
