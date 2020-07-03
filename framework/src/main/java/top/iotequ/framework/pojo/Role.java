/**************************************************
    Create by iotequ generator 3.0.0
    Author : Qinyoyo
**************************************************/

package top.iotequ.framework.pojo;
import top.iotequ.framework.util.StringUtil;
import top.iotequ.framework.pojo.CgEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.GrantedAuthority;
import com.google.gson.annotations.SerializedName;
import top.iotequ.framework.util.CgFieldAnnotation;
import top.iotequ.framework.util.CgTableAnnotation;
import java.util.*;

//  Pojo entity : Role (角色表)
@CgTableAnnotation(name="sys_role",
                   title="sysRole",
                   baseUrl="/framework/sysRole",
                   hasLicence=false,
                   pkType="Integer",
                   pkKeyType="1",
                   generatorName="sysRole",
                   module="framework")
@Getter
@Setter
public class Role implements CgEntity, ConfigAttribute,GrantedAuthority {
    private static final long serialVersionUID = 2552982L;
    @SerializedName(value = "id", alternate = {"ID"})
    @CgFieldAnnotation(name="id",jdbcType="INTEGER",nullable=false,format="")
    private Integer id;

    @SerializedName(value = "code", alternate = {"CODE"})
    @CgFieldAnnotation(name="code",jdbcType="VARCHAR",nullable=false,format="@")
    private String code;		//代码 db field:code

    @SerializedName(value = "name", alternate = {"NAME"})
    @CgFieldAnnotation(name="name",jdbcType="VARCHAR",nullable=false,format="@")
    private String name;		//名称 db field:name

    @SerializedName(value = "note", alternate = {"NOTE"})
    @CgFieldAnnotation(name="note",jdbcType="VARCHAR",nullable=true,format="@")
    private String note;		//说明 db field:note

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
    @Override
    public String getAttribute() {  
        return this.getAuthority();
    }

    @Override
    public String getAuthority() {
        return "ROLE_"+code;
    }    
//^_^自定义代码结束,请不要删除和修改本行
}
