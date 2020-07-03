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

//  Pojo entity : OauthClientDetails (OAuth2客户端配置)
@CgTableAnnotation(name="oauth_client_details",
                   title="sysOauthClientDetails",
                   pk="client_id",
                   entityPk="clientId",
                   baseUrl="/framework/sysOauthClientDetails",
                   hasLicence=false,
                   pkType="String",
                   pkKeyType="3",
                   generatorName="sysOauthClientDetails",
                   module="framework")
@Getter
@Setter
public class OauthClientDetails implements CgEntity {
    @SerializedName(value = "clientId", alternate = {"client_id","CLIENT_ID"})
    @CgFieldAnnotation(name="client_id",jdbcType="VARCHAR",nullable=false,format="@")
    private String clientId;		//client_id db field:client_id

    @SerializedName(value = "clientSecret", alternate = {"client_secret","CLIENT_SECRET"})
    @CgFieldAnnotation(name="client_secret",jdbcType="VARCHAR",nullable=false,format="@")
    private String clientSecret;		//client_secret db field:client_secret

    @SerializedName(value = "scope", alternate = {"SCOPE"})
    @CgFieldAnnotation(name="scope",jdbcType="VARCHAR",nullable=false,format="@")
    private String scope;		//scope db field:scope

    @SerializedName(value = "authorizedGrantTypes", alternate = {"authorized_grant_types","AUTHORIZED_GRANT_TYPES"})
    @CgFieldAnnotation(name="authorized_grant_types",jdbcType="VARCHAR",nullable=false,format="@")
    private String authorizedGrantTypes;		//认证类型 db field:authorized_grant_types

    @SerializedName(value = "webServerRedirectUri", alternate = {"web_server_redirect_uri","WEB_SERVER_REDIRECT_URI"})
    @CgFieldAnnotation(name="web_server_redirect_uri",jdbcType="VARCHAR",nullable=true,format="@")
    private String webServerRedirectUri;		//redirect_url db field:web_server_redirect_uri

    @SerializedName(value = "authorities", alternate = {"AUTHORITIES"})
    @CgFieldAnnotation(name="authorities",jdbcType="VARCHAR",nullable=false,format="@")
    private String authorities;		//权限集 db field:authorities

    @SerializedName(value = "accessTokenValidity", alternate = {"access_token_validity","ACCESS_TOKEN_VALIDITY"})
    @CgFieldAnnotation(name="access_token_validity",jdbcType="INTEGER",nullable=false,format="")
    private Integer accessTokenValidity;		//token有效时间 db field:access_token_validity

    @SerializedName(value = "refreshTokenValidity", alternate = {"refresh_token_validity","REFRESH_TOKEN_VALIDITY"})
    @CgFieldAnnotation(name="refresh_token_validity",jdbcType="INTEGER",nullable=false,format="")
    private Integer refreshTokenValidity;		//刷新时间秒 db field:refresh_token_validity

    @SerializedName(value = "autoapprove", alternate = {"AUTOAPPROVE"})
    @CgFieldAnnotation(name="autoapprove",jdbcType="VARCHAR",nullable=false,format="@")
    private String autoapprove;		//自动授权 db field:autoapprove

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @SerializedName(value = "expiredDate", alternate = {"expired_date","EXPIRED_DATE"})
    @CgFieldAnnotation(name="expired_date",jdbcType="TIMESTAMP",nullable=true,format="yyyy-mm-dd hh:mm")
    private Date expiredDate;		//过期时间 db field:expired_date

    @SerializedName(value = "locked", alternate = {"LOCKED"})
    @CgFieldAnnotation(name="locked",jdbcType="TINYINT",nullable=false,format="")
    private Boolean locked;		//锁定 db field:locked

    @SerializedName(value = "enabled", alternate = {"ENABLED"})
    @CgFieldAnnotation(name="enabled",jdbcType="TINYINT",nullable=false,format="")
    private Boolean enabled;		//激活 db field:enabled

    @SerializedName(value = "decription", alternate = {"DECRIPTION"})
    @CgFieldAnnotation(name="decription",jdbcType="VARCHAR",nullable=true,format="@")
    private String decription;		//描述 db field:decription

    @SerializedName(value = "additionalInformation", alternate = {"additional_information","ADDITIONAL_INFORMATION"})
    @CgFieldAnnotation(name="additional_information",jdbcType="VARCHAR",nullable=true,format="@")
    private String additionalInformation;		//附加属性(json) db field:additional_information

    @SerializedName(value = "resourceIds", alternate = {"resource_ids","RESOURCE_IDS"})
    @CgFieldAnnotation(name="resource_ids",jdbcType="VARCHAR",nullable=true,format="@")
    private String resourceIds;

    private String secret;		//secret 非数据库字段

    private String authUrl;		//OAuth请求url 非数据库字段

    @Override public Object getPkValue(){ return getClientId(); }
    @Override
    public void setPkValue(Object value) {
        if (value==null) setClientId(null);
        else setClientId(String.valueOf(value.toString()));
    }
    @Override
    public String toString() {
    	return StringUtil.toJsonString(this);
    }
//^_^自定义代码: java代码 ,请不要删除和修改本行
//^_^自定义代码结束,请不要删除和修改本行
}
