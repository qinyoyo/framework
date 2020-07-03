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
import com.google.gson.annotations.SerializedName;
import top.iotequ.framework.util.CgFieldAnnotation;
import top.iotequ.framework.util.CgTableAnnotation;
import java.util.*;

//  Pojo entity : SysLog (系统日志)
@CgTableAnnotation(name="sys_log",
                   title="sysLog",
                   baseUrl="/framework/sysLog",
                   hasLicence=false,
                   pkType="String",
                   pkKeyType="2",
                   generatorName="sysLog",
                   module="framework")
@Getter
@Setter
public class SysLog implements CgEntity {
    @SerializedName(value = "id", alternate = {"ID"})
    @CgFieldAnnotation(name="id",jdbcType="VARCHAR",nullable=false,format="@")
    private String id;		//uuid主键 db field:id

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @SerializedName(value = "time", alternate = {"TIME"})
    @CgFieldAnnotation(name="time",jdbcType="TIMESTAMP",nullable=false,format="yyyy-mm-dd hh:mm")
    private Date time;		//时间 db field:time

    @SerializedName(value = "keyword", alternate = {"KEYWORD"})
    @CgFieldAnnotation(name="keyword",jdbcType="VARCHAR",nullable=false,format="@")
    private String keyword;		//关键词 db field:keyword

    @SerializedName(value = "userType", alternate = {"user_type","USER_TYPE"})
    @CgFieldAnnotation(name="user_type",jdbcType="VARCHAR",nullable=true,format="@")
    private String userType;		//用户类别 db field:user_type

    @SerializedName(value = "userInfo", alternate = {"user_info","USER_INFO"})
    @CgFieldAnnotation(name="user_info",jdbcType="VARCHAR",nullable=true,format="@")
    private String userInfo;		//用户信息 db field:user_info

    @SerializedName(value = "note", alternate = {"NOTE"})
    @CgFieldAnnotation(name="note",jdbcType="VARCHAR",nullable=true,format="@")
    private String note;		//详情 db field:note

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
//^_^自定义代码结束,请不要删除和修改本行
}
