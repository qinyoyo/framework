/**************************************************
    Create by iotequ generator 3.0.0
    Author : Qinyoyo
**************************************************/

package top.iotequ.framework.pojo;
import top.iotequ.framework.util.StringUtil;
import top.iotequ.framework.pojo.CgEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Collection;
import java.util.List;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.google.gson.annotations.SerializedName;
import top.iotequ.framework.util.CgFieldAnnotation;
import top.iotequ.framework.util.CgTableAnnotation;
import java.util.*;

//  Pojo entity : User (用户)
@CgTableAnnotation(name="sys_user",
                   title="sysUser",
                   baseUrl="/framework/sysUser",
                   hasLicence=false,
                   pkType="String",
                   pkKeyType="3",
                   generatorName="sysUser",
                   module="framework")
@Getter
@Setter
public class User implements CgEntity, UserDetails {
    private static final long serialVersionUID = 2645995L;
    @SerializedName(value = "id", alternate = {"ID"})
    @CgFieldAnnotation(name="id",jdbcType="VARCHAR",nullable=false,format="@")
    private String id;		//uuid主键 db field:id

    @SerializedName(value = "icon", alternate = {"ICON"})
    @CgFieldAnnotation(name="icon",jdbcType="VARCHAR",nullable=true,multiple=false,format="@")
    private String icon;		//头像 db field:icon

    @SerializedName(value = "name", alternate = {"NAME"})
    @CgFieldAnnotation(name="name",jdbcType="VARCHAR",nullable=false,format="@")
    private String name;		//用户名 db field:name

    @SerializedName(value = "realName", alternate = {"real_name","REAL_NAME"})
    @CgFieldAnnotation(name="real_name",jdbcType="VARCHAR",nullable=false,format="@")
    private String realName;		//真实名 db field:real_name

    @SerializedName(value = "sex", alternate = {"SEX"})
    @CgFieldAnnotation(name="sex",jdbcType="VARCHAR",nullable=true,format="@")
    private String sex;		//性别 db field:sex

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @SerializedName(value = "birthDate", alternate = {"birth_date","BIRTH_DATE"})
    @CgFieldAnnotation(name="birth_date",jdbcType="DATE",nullable=true,format="yyyy-mm-dd")
    private Date birthDate;		//生日 db field:birth_date

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @SerializedName(value = "regTime", alternate = {"reg_time","REG_TIME"})
    @CgFieldAnnotation(name="reg_time",jdbcType="TIMESTAMP",nullable=true,format="yyyy-mm-dd hh:mm")
    private Date regTime;		//注册时间 db field:reg_time

    @SerializedName(value = "mobilePhone", alternate = {"mobile_phone","MOBILE_PHONE"})
    @CgFieldAnnotation(name="mobile_phone",jdbcType="VARCHAR",nullable=true,format="@")
    private String mobilePhone;		//手机号码 db field:mobile_phone

    @SerializedName(value = "email", alternate = {"EMAIL"})
    @CgFieldAnnotation(name="email",jdbcType="VARCHAR",nullable=true,format="@")
    private String email;		//邮箱 db field:email

    @SerializedName(value = "wechatOpenid", alternate = {"wechat_openid","WECHAT_OPENID"})
    @CgFieldAnnotation(name="wechat_openid",jdbcType="VARCHAR",nullable=true,format="@")
    private String wechatOpenid;		//微信openId db field:wechat_openid

    @SerializedName(value = "orgCode", alternate = {"org_code","ORG_CODE"})
    @CgFieldAnnotation(name="org_code",jdbcType="INTEGER",nullable=false,format="")
    private Integer orgCode;		//部门 db field:org_code

    @SerializedName(value = "employeeNo", alternate = {"employee_no","EMPLOYEE_NO"})
    @CgFieldAnnotation(name="employee_no",jdbcType="VARCHAR",nullable=true,format="@")
    private String employeeNo;		//工号 db field:employee_no

    @SerializedName(value = "orgPrivilege", alternate = {"org_privilege","ORG_PRIVILEGE"})
    @CgFieldAnnotation(name="org_privilege",jdbcType="INTEGER",nullable=true,format="")
    private Integer orgPrivilege;		//部门权限 db field:org_privilege

    @SerializedName(value = "roleList", alternate = {"role_list","ROLE_LIST"})
    @CgFieldAnnotation(name="role_list",jdbcType="VARCHAR",nullable=true,format="@")
    private String roleList;		//角色序列 db field:role_list

    @SerializedName(value = "locked", alternate = {"LOCKED"})
    @CgFieldAnnotation(name="locked",jdbcType="TINYINT",nullable=false,format="")
    private Boolean locked;		//被锁定 db field:locked

    @SerializedName(value = "state", alternate = {"STATE"})
    @CgFieldAnnotation(name="state",jdbcType="TINYINT",nullable=false,format="")
    private Boolean state;		//激活 db field:state

    @SerializedName(value = "idType", alternate = {"id_type","ID_TYPE"})
    @CgFieldAnnotation(name="id_type",jdbcType="INTEGER",nullable=false,format="")
    private Integer idType;		//证件类型 db field:id_type

    @SerializedName(value = "idNumber", alternate = {"id_number","ID_NUMBER"})
    @CgFieldAnnotation(name="id_number",jdbcType="VARCHAR",nullable=false,format="@")
    private String idNumber;		//证件号码 db field:id_number

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @SerializedName(value = "expiredTime", alternate = {"expired_time","EXPIRED_TIME"})
    @CgFieldAnnotation(name="expired_time",jdbcType="DATE",nullable=true,format="yyyy-mm-dd")
    private Date expiredTime;		//账号过期时间 db field:expired_time

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @SerializedName(value = "passwordExpiredTime", alternate = {"password_expired_time","PASSWORD_EXPIRED_TIME"})
    @CgFieldAnnotation(name="password_expired_time",jdbcType="DATE",nullable=true,format="yyyy-mm-dd")
    private Date passwordExpiredTime;		//密码过期时间 db field:password_expired_time

    @SerializedName(value = "password", alternate = {"PASSWORD"})
    @CgFieldAnnotation(name="password",jdbcType="VARCHAR",nullable=false,format="@")
    private String password;		//密码 db field:password

    @SerializedName(value = "passwordErrorTimes", alternate = {"password_error_times","PASSWORD_ERROR_TIMES"})
    @CgFieldAnnotation(name="password_error_times",jdbcType="INTEGER",nullable=false,format="")
    private Integer passwordErrorTimes;

    private String htmlNote;		//提示 非数据库字段

    @Override public Object getPkValue(){ return getId(); }
    @Override
    public void setPkValue(Object value) {
        if (value==null) setId(null);
        else setId(String.valueOf(value.toString()));
    }
    @Override
    public String toString() {
    	return StringUtil.toJsonString(this);
    }
//^_^自定义代码: java代码 ,请不要删除和修改本行
	private List<Role> grantedRoles;
 	@Override
	public String getUsername() {
		return name;
	}
	@Override
	public boolean isAccountNonExpired() {
		if (expiredTime != null)
			return expiredTime.getTime() > new Date().getTime();
		else
			return true;
	}
	@Override
	public boolean isAccountNonLocked() {
		return locked==null || !locked;
	}
	@Override
	public boolean isCredentialsNonExpired() {
		if (passwordExpiredTime != null)
			return passwordExpiredTime.getTime() > new Date().getTime();
		else
			return true;
	}
	@Override
	public boolean isEnabled() {
		return state!=null && state;
	}
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedRoles;
    }
    public Collection<? extends ConfigAttribute> getConfigAttribute() {
	        return grantedRoles;
    }
    public void setAuthorities(List<Role> grantedRoles) {
        this.grantedRoles=grantedRoles;
    }
//^_^自定义代码结束,请不要删除和修改本行
}
