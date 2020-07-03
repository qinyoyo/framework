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

//  Pojo entity : Menu (系统菜单)
@CgTableAnnotation(name="sys_menu",
                   title="sysMenu",
                   baseUrl="/framework/sysMenu",
                   hasLicence=false,
                   parentEntityField="parent",
                   pkType="Integer",
                   pkKeyType="1",
                   generatorName="sysMenu",
                   module="framework")
@Getter
@Setter
public class Menu implements CgEntity {
    @SerializedName(value = "id", alternate = {"ID"})
    @CgFieldAnnotation(name="id",jdbcType="INTEGER",nullable=false,format="")
    private Integer id;

    @SerializedName(value = "sortNum", alternate = {"sort_num","SORT_NUM"})
    @CgFieldAnnotation(name="sort_num",jdbcType="INTEGER",nullable=false,format="")
    private Integer sortNum;		//排列顺序 db field:sort_num

    @SerializedName(value = "name", alternate = {"NAME"})
    @CgFieldAnnotation(name="name",jdbcType="VARCHAR",nullable=false,format="@")
    private String name;		//名称 db field:name

    @SerializedName(value = "parent", alternate = {"PARENT"})
    @CgFieldAnnotation(name="parent",jdbcType="INTEGER",nullable=true,format="")
    private Integer parent;		//上级菜单 db field:parent

    @SerializedName(value = "isDivider", alternate = {"is_divider","IS_DIVIDER"})
    @CgFieldAnnotation(name="is_divider",jdbcType="TINYINT",nullable=false,format="")
    private Boolean isDivider;		//分割线 db field:is_divider

    @SerializedName(value = "icon", alternate = {"ICON"})
    @CgFieldAnnotation(name="icon",jdbcType="VARCHAR",nullable=true,format="@")
    private String icon;		//图标 db field:icon

    @SerializedName(value = "action", alternate = {"ACTION"})
    @CgFieldAnnotation(name="action",jdbcType="VARCHAR",nullable=true,format="@")
    private String action;		//功能地址 db field:action

    @SerializedName(value = "className", alternate = {"class_name","CLASS_NAME"})
    @CgFieldAnnotation(name="class_name",jdbcType="VARCHAR",nullable=true,format="@")
    private String className;		//附加属性 db field:class_name

    @SerializedName(value = "dataAction", alternate = {"data_action","DATA_ACTION"})
    @CgFieldAnnotation(name="data_action",jdbcType="VARCHAR",nullable=true,format="@")
    private String dataAction;		//附加参数 db field:data_action

    @SerializedName(value = "bigicon", alternate = {"bigIcon","BIGICON"})
    @CgFieldAnnotation(name="bigIcon",jdbcType="VARCHAR",nullable=true,format="@")
    private String bigicon;		//大图标 db field:bigIcon

    @SerializedName(value = "mobileHidden", alternate = {"mobile_hidden","MOBILE_HIDDEN"})
    @CgFieldAnnotation(name="mobile_hidden",jdbcType="TINYINT",nullable=false,format="")
    private Boolean mobileHidden;		//手机隐藏 db field:mobile_hidden

    @SerializedName(value = "jsCmd", alternate = {"js_cmd","JS_CMD"})
    @CgFieldAnnotation(name="js_cmd",jdbcType="VARCHAR",nullable=true,format="@")
    private String jsCmd;		//操作函数 db field:js_cmd

    private List<Menu> children;
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
