package top.iotequ.framework.util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;

import com.google.gson.Gson;

import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import top.iotequ.framework.exception.IotequAccessDeniedException;
import top.iotequ.framework.exception.IotequDatabaseException;
import top.iotequ.framework.exception.IotequException;
import top.iotequ.framework.exception.IotequThrowable;

public class RestJson {
	private Map<String, Object> attributes;
	private boolean downloadFileMode = false;
	public RestJson() {
		attributes=new HashMap<>();
		attributes.put("success", true);
		attributes.put("error", null);  // error-code
		attributes.put("message", "success");
		attributes.put("detailMessage", null);
	}

	public OutputStream getDownloadOutputStream(HttpServletResponse response, @NonNull String mediaType, @NonNull String fileName) {
		try {
			response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
		} catch (Exception e) {
			setMessage(e);
			return null;
		}
		response.setContentType(mediaType);
		downloadFileMode = true;
		try {
			return response.getOutputStream();
		} catch (IOException e) {
			downloadFileMode = false;
			setMessage(e);
			return null;
		}
	}
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	public RestJson setAttributes(Map<String, Object> attributes) {
		this.attributes.putAll(attributes);
		return this;
	}
	public Object get(String key) {
		return attributes.get(key);
	}
	public RestJson put(String key,Object value) {
		this.attributes.put(key, value);
		return this;
	}
	private void mapAttribute(String attributeKey,Map<String, Object> value) {
		this.attributes.put(attributeKey, value);
	}
	@SuppressWarnings({ "serial", "unchecked" })
	private void mapAttribute(String attributeKey,String key,Object value) {
		Object data=this.attributes.get(attributeKey);
		if (data==null && value!=null) {
			this.attributes.put(attributeKey, new HashMap<String,Object>(){{
				put(key,value);
			}});
		} else if (data!=null) {
			if (data instanceof Map) ((Map<String, Object>)data).put(key,value);
			else {
				try {
					EntityUtil.setPrivateField(data,key,value);
				} catch (Exception e) {
					Map<String,Object> map = EntityUtil.mapFromEntity(data);
					map.put(key,value);
					this.attributes.put(attributeKey,map);
				}
			}
		}
	}
	public RestJson data(Object value) {
		this.attributes.put("data",value);
		return this;
	}
	public RestJson data(String field,Object value) {
		this.mapAttribute("data",field,value);
		return this;
	}
	public RestJson dictionary(Map<String, Object> value) {
		mapAttribute("dictionary",value);
		return this;
	}
	public RestJson dictionary(@NonNull String key,Object value) {
		mapAttribute("dictionary",key,value);
		return this;
	}
	public RestJson parameter(Map<String, Object> value) {
		mapAttribute("parameter",value);
		return this;
	}
	public RestJson parameter(@NonNull String key,Object value) {
		mapAttribute("parameter",key,value);
		return this;
	}
	public String getMessage() {
		return StringUtil.toString(attributes.get("message"));
	}
	public RestJson setMessage(String message) {
		attributes.put("message",  message);
		return this;
	}

	public RestJson setMessage(Exception e) {
		if (e==null) return this;
		if (e instanceof AuthenticationException) {
			String url= Util.getRequest()==null ? null : Util.getRequest().getServletPath() ;
			e= new IotequAccessDeniedException(IotequThrowable.ACCESS_DENIED,url);
		}
		if (Util.runInIdeMode || !(e instanceof IotequThrowable)
		   || (e instanceof IotequThrowable && ((IotequThrowable) e).getError().equals(IotequThrowable.EXCEPTION)))
			e.printStackTrace();
		String detail = StringUtil.printStackTrace(e);
		if (e instanceof IotequDatabaseException) {
			String sql = ((IotequDatabaseException)e).getSql();
			if (!Util.isEmpty(sql)) detail = sql + "\n" +detail;
		}
		attributes.put("detailMessage",detail);
		setSuccess(false);
		if (e instanceof IotequThrowable) {
			attributes.put("error", ((IotequThrowable)e).getError());
			attributes.put("message", ((IotequThrowable)e).getAddtionalMessage());
		} else {
			IotequException ne=IotequException.newInstance(e);
			attributes.put("error", ne.getError());
			attributes.put("message", ne.getAddtionalMessage());
		}
		return this;
	}
	public RestJson setError(Exception e) {
		return setMessage(e);
	}
	public boolean isSuccess() {
		Object o=attributes.get("success");
		if (o!=null) return (Boolean)o;
		else return false;
	}

	public RestJson setSuccess(boolean success) {
		attributes.put("success", success);
		attributes.put("message", (success ? "success" : "failure"));
		return this;
	}
	public RestJson setError(String errorCode,String message,String detailMessage) {
		attributes.put("success", false);
		attributes.put("error", errorCode);  
		attributes.put("message", message);
		attributes.put("detailMessage", detailMessage);
		return this;
	}
	public RestJson setErrorCode(String errorCode,String message) {
		return setError(errorCode,message,null);
	}
	public RestJson setError(String errorCode,Exception e) {
		attributes.put("success", false);
		attributes.put("error", errorCode);
		attributes.put("message",e.getMessage());
		attributes.put("detailMessage",StringUtil.printStackTrace(e));
		if (Util.runInIdeMode) e.printStackTrace();
		return this;
	}
	public ResponseEntity<Map<String, Object>> toResponse(int status) {
		if (downloadFileMode) return null;
		HttpStatus httpStatus = HttpStatus.valueOf(status);
		return new ResponseEntity<>(attributes, httpStatus);
	}
	public ResponseEntity<Map<String, Object>> toResponse() {
		if (downloadFileMode) return null;
		return new ResponseEntity<>(attributes, HttpStatus.OK);
	}
	@Override
	public String toString(){
		Gson gson = Util.getGson();  
		String s=gson.toJson(this.attributes);
		return s;
	}
	public void sendTo(HttpServletResponse response) {
		response.setContentType("text/json; charset=UTF-8");
		try {
			String s=toString();
			response.getWriter().print(s);
			response.getWriter().flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				response.getWriter().close();
			} catch (IOException e) {
			}
		}
		
	}
}
