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

//  Pojo entity : Task (调度任务)
@CgTableAnnotation(name="sys_task",
                   title="sysTask",
                   baseUrl="/framework/sysTask",
                   hasLicence=false,
                   pkType="Integer",
                   pkKeyType="1",
                   generatorName="sysTask",
                   module="framework")
@Getter
@Setter
public class Task implements CgEntity {
    @SerializedName(value = "id", alternate = {"ID"})
    @CgFieldAnnotation(name="id",jdbcType="INTEGER",nullable=false,format="")
    private Integer id;

    @SerializedName(value = "name", alternate = {"NAME"})
    @CgFieldAnnotation(name="name",jdbcType="VARCHAR",nullable=false,format="@")
    private String name;		//任务名 db field:name

    @SerializedName(value = "description", alternate = {"DESCRIPTION"})
    @CgFieldAnnotation(name="description",jdbcType="VARCHAR",nullable=true,format="@")
    private String description;		//详细说明 db field:description

    @SerializedName(value = "sceduleYears", alternate = {"scedule_years","SCEDULE_YEARS"})
    @CgFieldAnnotation(name="scedule_years",jdbcType="VARCHAR",nullable=false,format="@")
    private String sceduleYears;		//调度年 db field:scedule_years

    @SerializedName(value = "scheduleMonths", alternate = {"schedule_months","SCHEDULE_MONTHS"})
    @CgFieldAnnotation(name="schedule_months",jdbcType="VARCHAR",nullable=false,format="@")
    private String scheduleMonths;		//调度月 db field:schedule_months

    @SerializedName(value = "scheduleDays", alternate = {"schedule_days","SCHEDULE_DAYS"})
    @CgFieldAnnotation(name="schedule_days",jdbcType="VARCHAR",nullable=false,format="@")
    private String scheduleDays;		//调度日 db field:schedule_days

    @SerializedName(value = "scheduleWeeks", alternate = {"schedule_weeks","SCHEDULE_WEEKS"})
    @CgFieldAnnotation(name="schedule_weeks",jdbcType="VARCHAR",nullable=false,format="@")
    private String scheduleWeeks;		//调度星期 db field:schedule_weeks

    @SerializedName(value = "scheduleHours", alternate = {"schedule_hours","SCHEDULE_HOURS"})
    @CgFieldAnnotation(name="schedule_hours",jdbcType="VARCHAR",nullable=false,format="@")
    private String scheduleHours;		//调度时 db field:schedule_hours

    @SerializedName(value = "scheduleMinutes", alternate = {"schedule_minutes","SCHEDULE_MINUTES"})
    @CgFieldAnnotation(name="schedule_minutes",jdbcType="VARCHAR",nullable=false,format="@")
    private String scheduleMinutes;		//调度分 db field:schedule_minutes

    @SerializedName(value = "className", alternate = {"class_name","CLASS_NAME"})
    @CgFieldAnnotation(name="class_name",jdbcType="VARCHAR",nullable=false,format="@")
    private String className;		//类名 db field:class_name

    @SerializedName(value = "mothodName", alternate = {"mothod_name","MOTHOD_NAME"})
    @CgFieldAnnotation(name="mothod_name",jdbcType="VARCHAR",nullable=false,format="@")
    private String mothodName;		//方法 db field:mothod_name

    @SerializedName(value = "isStatic", alternate = {"is_static","IS_STATIC"})
    @CgFieldAnnotation(name="is_static",jdbcType="TINYINT",nullable=false,format="")
    private Boolean isStatic;		//静态方法 db field:is_static

    @SerializedName(value = "parames", alternate = {"PARAMES"})
    @CgFieldAnnotation(name="parames",jdbcType="VARCHAR",nullable=true,format="@")
    private String parames;		//参数 db field:parames

    @SerializedName(value = "isRunning", alternate = {"is_running","IS_RUNNING"})
    @CgFieldAnnotation(name="is_running",jdbcType="TINYINT",nullable=false,format="")
    private Boolean isRunning;		//运行中 db field:is_running

    private String run;		//Run 非数据库字段

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
