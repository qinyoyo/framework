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

//  Pojo entity : Message (消息)
@CgTableAnnotation(name="sys_message",
                   title="sysMessage",
                   baseUrl="/framework/sysMessage",
                   hasLicence=false,
                   pkType="Integer",
                   pkKeyType="1",
                   generatorName="sysMessage",
                   module="framework")
@Getter
@Setter
public class Message implements CgEntity {
    @SerializedName(value = "id", alternate = {"ID"})
    @CgFieldAnnotation(name="id",jdbcType="INTEGER",nullable=false,format="")
    private Integer id;		//主键 db field:id

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @SerializedName(value = "readTime", alternate = {"read_time","READ_TIME"})
    @CgFieldAnnotation(name="read_time",jdbcType="TIMESTAMP",nullable=true,format="")
    private Date readTime;		//已阅 db field:read_time

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @SerializedName(value = "createTime", alternate = {"create_time","CREATE_TIME"})
    @CgFieldAnnotation(name="create_time",jdbcType="TIMESTAMP",nullable=false,format="yyyy-mm-dd hh:mm")
    private Date createTime;		//创建时间 db field:create_time

    @SerializedName(value = "receiverName", alternate = {"receiver_name","RECEIVER_NAME"})
    @CgFieldAnnotation(name="receiver_name",jdbcType="VARCHAR",nullable=true,format="@")
    private String receiverName;		//消息接收人姓名 db field:receiver_name

    @SerializedName(value = "senderName", alternate = {"sender_name","SENDER_NAME"})
    @CgFieldAnnotation(name="sender_name",jdbcType="VARCHAR",nullable=true,format="@")
    private String senderName;		//消息发送人姓名 db field:sender_name

    @SerializedName(value = "title", alternate = {"TITLE"})
    @CgFieldAnnotation(name="title",jdbcType="VARCHAR",nullable=false,format="@")
    private String title;		//消息标题 db field:title

    @SerializedName(value = "content", alternate = {"CONTENT"})
    @CgFieldAnnotation(name="content",jdbcType="VARCHAR",nullable=false,format="@")
    private String content;		//消息内容 db field:content

    @SerializedName(value = "url", alternate = {"URL"})
    @CgFieldAnnotation(name="url",jdbcType="VARCHAR",nullable=true,format="@")
    private String url;		//消息点击链接 db field:url

    @SerializedName(value = "receiver", alternate = {"RECEIVER"})
    @CgFieldAnnotation(name="receiver",jdbcType="VARCHAR",nullable=true,format="@")
    private String receiver;		//消息接收人 db field:receiver

    @SerializedName(value = "sender", alternate = {"SENDER"})
    @CgFieldAnnotation(name="sender",jdbcType="VARCHAR",nullable=true,format="@")
    private String sender;		//消息发送人 db field:sender

    @SerializedName(value = "eventId", alternate = {"event_id","EVENT_ID"})
    @CgFieldAnnotation(name="event_id",jdbcType="VARCHAR",nullable=true,format="@")
    private String eventId;		//关联id db field:event_id

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
