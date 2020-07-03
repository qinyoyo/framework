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

//  Pojo entity : FlowProcess (流程处理)
@CgTableAnnotation(name="sys_flow_process",
                   title="sysFlowProcess",
                   join="LEFT JOIN sys_user ON sys_flow_process.operator = sys_user.id LEFT JOIN sys_user sys_user1 ON sys_flow_process.next_operator = sys_user1.id",
                   baseUrl="/framework/sysFlowProcess",
                   hasLicence=false,
                   pkType="String",
                   pkKeyType="2",
                   generatorName="sysFlowProcess",
                   module="framework")
@Getter
@Setter
public class FlowProcess implements CgEntity {
    @SerializedName(value = "id", alternate = {"ID"})
    @CgFieldAnnotation(name="sys_flow_process.id",jdbcType="VARCHAR",nullable=false,format="@")
    private String id;

    @SerializedName(value = "flowId", alternate = {"flow_id","FLOW_ID"})
    @CgFieldAnnotation(name="sys_flow_process.flow_id",jdbcType="VARCHAR",nullable=false,format="@")
    private String flowId;		//流程单 db field:flow_id

    @SerializedName(value = "operation", alternate = {"OPERATION"})
    @CgFieldAnnotation(name="sys_flow_process.operation",jdbcType="VARCHAR",nullable=true,format="@")
    private String operation;		//操作 db field:operation

    @SerializedName(value = "selection", alternate = {"SELECTION"})
    @CgFieldAnnotation(name="sys_flow_process.selection",jdbcType="VARCHAR",nullable=true,format="@")
    private String selection;		//选择 db field:selection

    @SerializedName(value = "stateName0", alternate = {"state_name0","STATE_NAME0"})
    @CgFieldAnnotation(name="sys_flow_process.state_name0",jdbcType="VARCHAR",nullable=true,format="@")
    private String stateName0;		//初始状态 db field:state_name0

    @SerializedName(value = "stateName1", alternate = {"state_name1","STATE_NAME1"})
    @CgFieldAnnotation(name="sys_flow_process.state_name1",jdbcType="VARCHAR",nullable=true,format="@")
    private String stateName1;		//处理后状态 db field:state_name1

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @SerializedName(value = "time", alternate = {"TIME"})
    @CgFieldAnnotation(name="sys_flow_process.time",jdbcType="TIMESTAMP",nullable=false,format="yyyy-mm-dd hh:mm")
    private Date time;		//处理时间 db field:time

    @SerializedName(value = "note", alternate = {"NOTE"})
    @CgFieldAnnotation(name="sys_flow_process.note",jdbcType="VARCHAR",nullable=true,format="@")
    private String note;		//处理意见 db field:note

    @SerializedName(value = "state0", alternate = {"STATE0"})
    @CgFieldAnnotation(name="sys_flow_process.state0",jdbcType="INTEGER",nullable=true,format="")
    private Integer state0;		//初始状态 db field:state0

    @SerializedName(value = "state1", alternate = {"STATE1"})
    @CgFieldAnnotation(name="sys_flow_process.state1",jdbcType="INTEGER",nullable=false,format="")
    private Integer state1;		//处理后状态 db field:state1

    @SerializedName(value = "operator", alternate = {"OPERATOR"})
    @CgFieldAnnotation(name="sys_flow_process.operator",jdbcType="VARCHAR",nullable=false,format="@")
    private String operator;		//处理人 db field:operator
    @SerializedName(value = "operatorSysUserRealName", alternate = {"operator_sys_user_real_name","OPERATOR_SYS_USER_REAL_NAME"})
    @CgFieldAnnotation(name="sys_user.real_name",jdbcType="VARCHAR")
    private String operatorSysUserRealName;

    @SerializedName(value = "nextOperator", alternate = {"next_operator","NEXT_OPERATOR"})
    @CgFieldAnnotation(name="sys_flow_process.next_operator",jdbcType="VARCHAR",nullable=true,format="@")
    private String nextOperator;		//后续处理人 db field:next_operator
    @SerializedName(value = "nextOperatorSysUserRealName", alternate = {"next_operator_sys_user_real_name","NEXT_OPERATOR_SYS_USER_REAL_NAME"})
    @CgFieldAnnotation(name="sys_user1.real_name",jdbcType="VARCHAR")
    private String nextOperatorSysUserRealName;

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
