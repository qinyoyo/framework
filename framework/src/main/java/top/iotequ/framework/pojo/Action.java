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

//  Pojo entity : Action (功能列表)
@CgTableAnnotation(name="sys_action",
                   title="sysAction",
                   baseUrl="/framework/sysAction",
                   hasLicence=false,
                   pkType="Integer",
                   pkKeyType="1",
                   generatorName="sysAction",
                   module="framework")
@Getter
@Setter
public class Action implements CgEntity {
    @SerializedName(value = "note", alternate = {"NOTE"})
    @CgFieldAnnotation(name="note",jdbcType="VARCHAR",nullable=true,format="@")
    private String note;		//描述 db field:note

    @SerializedName(value = "id", alternate = {"ID"})
    @CgFieldAnnotation(name="id",jdbcType="INTEGER",nullable=false,format="")
    private Integer id;		//ID db field:id

    @SerializedName(value = "value", alternate = {"VALUE"})
    @CgFieldAnnotation(name="value",jdbcType="VARCHAR",nullable=false,format="@")
    private String value;		//值 db field:value

    @SerializedName(value = "params", alternate = {"PARAMS"})
    @CgFieldAnnotation(name="params",jdbcType="VARCHAR",nullable=true,format="@")
    private String params;		//参数 db field:params

    @SerializedName(value = "method", alternate = {"METHOD"})
    @CgFieldAnnotation(name="method",jdbcType="VARCHAR",nullable=true,format="@")
    private String method;		//方法 db field:method

    private String htmlNote;		//注意 非数据库字段

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
//^_^自定义代码结束,请不要删除和修改本行
}
