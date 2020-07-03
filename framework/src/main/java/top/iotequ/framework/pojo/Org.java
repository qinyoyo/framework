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

//  Pojo entity : Org (组织机构)
@CgTableAnnotation(name="sys_org",
                   title="sysOrg",
                   pk="org_code",
                   entityPk="orgCode",
                   baseUrl="/framework/sysOrg",
                   hasLicence=false,
                   parentEntityField="parent",
                   pkType="Integer",
                   pkKeyType="1",
                   generatorName="sysOrg",
                   module="framework")
@Getter
@Setter
public class Org implements CgEntity {
    @SerializedName(value = "orgCode", alternate = {"org_code","ORG_CODE"})
    @CgFieldAnnotation(name="org_code",jdbcType="INTEGER",nullable=false,format="")
    private Integer orgCode;		//代码 db field:org_code

    @SerializedName(value = "name", alternate = {"NAME"})
    @CgFieldAnnotation(name="name",jdbcType="VARCHAR",nullable=false,format="@")
    private String name;		//名称 db field:name

    @SerializedName(value = "parent", alternate = {"PARENT"})
    @CgFieldAnnotation(name="parent",jdbcType="INTEGER",nullable=true,format="")
    private Integer parent;		//上级机构 db field:parent

    @SerializedName(value = "phone", alternate = {"PHONE"})
    @CgFieldAnnotation(name="phone",jdbcType="VARCHAR",nullable=true,format="@")
    private String phone;		//电话 db field:phone

    @SerializedName(value = "fax", alternate = {"FAX"})
    @CgFieldAnnotation(name="fax",jdbcType="VARCHAR",nullable=true,format="@")
    private String fax;		//传真 db field:fax

    @SerializedName(value = "roleList", alternate = {"role_list","ROLE_LIST"})
    @CgFieldAnnotation(name="role_list",jdbcType="VARCHAR",nullable=true,format="@")
    private String roleList;		//角色序列 db field:role_list

    @SerializedName(value = "address", alternate = {"ADDRESS"})
    @CgFieldAnnotation(name="address",jdbcType="VARCHAR",nullable=true,format="@")
    private String address;		//地址 db field:address

    private List<Org> children;
    @Override public Object getPkValue(){ return getOrgCode(); }
    @Override
    public void setPkValue(Object value) {
        if (value==null) setOrgCode(null);
        else setOrgCode(Integer.valueOf(value.toString()));
    }
    @Override
    public String toString() {
    	return StringUtil.toJsonString(this);
    }
//^_^自定义代码: java代码 ,请不要删除和修改本行
//^_^自定义代码结束,请不要删除和修改本行
}
