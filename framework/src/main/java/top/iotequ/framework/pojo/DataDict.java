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

//  Pojo entity : DataDict (数据字典)
@CgTableAnnotation(name="sys_data_dict",
                   title="sysDataDict",
                   baseUrl="/framework/sysDataDict",
                   hasLicence=false,
                   pkType="Integer",
                   pkKeyType="1",
                   generatorName="sysDataDict",
                   module="framework")
@Getter
@Setter
public class DataDict implements CgEntity {
    @SerializedName(value = "id", alternate = {"ID"})
    @CgFieldAnnotation(name="id",jdbcType="INTEGER",nullable=false,format="")
    private Integer id;

    @SerializedName(value = "dict", alternate = {"DICT"})
    @CgFieldAnnotation(name="dict",jdbcType="VARCHAR",nullable=false,format="@")
    private String dict;		//分类 db field:dict

    @SerializedName(value = "code", alternate = {"CODE"})
    @CgFieldAnnotation(name="code",jdbcType="VARCHAR",nullable=false,format="@")
    private String code;		//代码 db field:code

    @SerializedName(value = "text", alternate = {"TEXT"})
    @CgFieldAnnotation(name="text",jdbcType="VARCHAR",nullable=false,format="@")
    private String text;		//显示值 db field:text

    @SerializedName(value = "orderNum", alternate = {"order_num","ORDER_NUM"})
    @CgFieldAnnotation(name="order_num",jdbcType="INTEGER",nullable=true,format="")
    private Integer orderNum;		//排列顺序 db field:order_num

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
