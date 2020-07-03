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

//  Pojo entity : SysRoute (路由表)
@CgTableAnnotation(name="sys_route",
                   title="sysRoute",
                   baseUrl="/framework/sysRoute",
                   hasLicence=false,
                   pkType="Integer",
                   pkKeyType="1",
                   generatorName="sysRoute",
                   module="framework")
@Getter
@Setter
public class SysRoute implements CgEntity {
    @SerializedName(value = "id", alternate = {"ID"})
    @CgFieldAnnotation(name="id",jdbcType="INTEGER",nullable=false,format="")
    private Integer id;		//ID db field:id

    @SerializedName(value = "path", alternate = {"PATH"})
    @CgFieldAnnotation(name="path",jdbcType="VARCHAR",nullable=false,format="@")
    private String path;		//路径 db field:path

    @SerializedName(value = "name", alternate = {"NAME"})
    @CgFieldAnnotation(name="name",jdbcType="VARCHAR",nullable=false,format="@")
    private String name;		//名称 db field:name

    @SerializedName(value = "component", alternate = {"COMPONENT"})
    @CgFieldAnnotation(name="component",jdbcType="VARCHAR",nullable=false,format="@")
    private String component;		//组件 db field:component

    @SerializedName(value = "title", alternate = {"TITLE"})
    @CgFieldAnnotation(name="title",jdbcType="VARCHAR",nullable=false,format="@")
    private String title;		//标题 db field:title

    @SerializedName(value = "breadcrumbShow", alternate = {"breadcrumb_show","BREADCRUMB_SHOW"})
    @CgFieldAnnotation(name="breadcrumb_show",jdbcType="TINYINT",nullable=false,format="")
    private Boolean breadcrumbShow;		//面包屑显示 db field:breadcrumb_show

    @SerializedName(value = "needCache", alternate = {"need_cache","NEED_CACHE"})
    @CgFieldAnnotation(name="need_cache",jdbcType="TINYINT",nullable=false,format="")
    private Boolean needCache;		//需要缓存 db field:need_cache

    @SerializedName(value = "tagView", alternate = {"tag_view","TAG_VIEW"})
    @CgFieldAnnotation(name="tag_view",jdbcType="TINYINT",nullable=false,format="")
    private Boolean tagView;		//标签显示 db field:tag_view

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
