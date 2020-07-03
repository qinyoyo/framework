package top.iotequ.framework.util;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import top.iotequ.framework.exception.IotequDatabaseException;
import top.iotequ.framework.exception.IotequException;
import top.iotequ.framework.exception.IotequThrowable;

public class HttpUtils {
	public static <T> T getRequestBody (Type typeOfT, HttpServletRequest request) throws IotequException {
		InputStream is = null;
		try
		{
			is = request.getInputStream();
			StringBuilder sb = new StringBuilder();
			byte[] b = new byte[4096];
			for (int n; (n = is.read(b)) != -1;)
			{
				sb.append(new String(b, 0, n));
			}
			return Util.getGson().fromJson(sb.toString(), typeOfT);
		}
		catch (Exception e)
		{
			throw new IotequException(IotequThrowable.IO_EXCEPTION,e.getMessage());
		}
		finally
		{
			if (null != is)
			{
				try
				{
					is.close();
				}
				catch (Exception e1)
				{
				}
			}
		}
	}

	public static Map<String,Object> getRequestBody(HttpServletRequest request) throws IotequException {
		InputStream is = null;
		try
		{
			is = request.getInputStream();
			StringBuilder sb = new StringBuilder();
			byte[] b = new byte[4096];
			for (int n; (n = is.read(b)) != -1;)
			{
				sb.append(new String(b, 0, n));
			}
			Map<String,Object> map = Util.mapFromJson(sb.toString());
			if (map!=null) return map;
			else return null;
		}
		catch (Exception e)
		{
			throw new IotequException(IotequThrowable.IO_EXCEPTION,e.getMessage());
		}
		finally
		{
			if (null != is)
			{
				try
				{
					is.close();
				}
				catch (Exception e1)
				{
				}
			}
		}
	}

	public static String getRequestBodyParameter (String key, HttpServletRequest request) throws IotequException {
		InputStream is = null;
		try
		{
			is = request.getInputStream();
			StringBuilder sb = new StringBuilder();
			byte[] b = new byte[4096];
			for (int n; (n = is.read(b)) != -1;)
			{
				sb.append(new String(b, 0, n));
			}
			Map<String,Object> map = Util.getGson().fromJson(sb.toString(), Map.class);
			if (map!=null) return StringUtil.toString(map.get(key));
			else return null;
		}
		catch (Exception e)
		{
			throw new IotequException(IotequThrowable.IO_EXCEPTION,e.getMessage());
		}
		finally
		{
			if (null != is)
			{
				try
				{
					is.close();
				}
				catch (Exception e1)
				{
				}
			}
		}
	}

	/**
	 * 将http返回转换为JSONObject
	 * @param response  http响应
	 * @return 转换结果
	 * @throws IotequException 异常
	 */
	public static JSONObject getHttpEntity(HttpResponse response) throws IotequException {
		try {
			if (response == null) return null;
			HttpEntity entity = response.getEntity();
			if (entity.getContentLength() == 0 || !entity.isStreaming()) {
				return new JSONObject("{\"code\":" + response.getStatusLine().getStatusCode() + "}");
			}
			String line = null;
			StringBuilder entityStringBuilder = new StringBuilder();
			BufferedReader b = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"), 8 * 1024);
			while ((line = b.readLine()) != null) {
				entityStringBuilder.append(line + "\n");
			}
			JSONObject resultJsonObject = new JSONObject(entityStringBuilder.toString());
			return resultJsonObject;
		} catch (Exception e) {
			throw IotequException.newInstance(e);
		}
	}
	/**
	 * 将http返回转换为String
	 * @param response  http响应
	 * @return 转换结果
	 * @throws IotequException 异常
	 */
	public static String getHttpString(HttpResponse response) throws IotequException {
		if (response==null) return null;
		try {
			HttpEntity entity = response.getEntity();
			if (entity.getContentLength() == 0 || !entity.isStreaming()) {
				return "{\"code\":" + response.getStatusLine().getStatusCode() + "}";
			}
			String line = null;
			StringBuilder entityStringBuilder = new StringBuilder();
			BufferedReader b = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"), 8 * 1024);
			while ((line = b.readLine()) != null) {
				entityStringBuilder.append(line + "\n");
			}
			return entityStringBuilder.toString();
		}catch (Exception e) {
			throw IotequException.newInstance(e);
		}
	}	

	/*
	 * get
	 * 
	 * @param host
	 * @param headers
	 * @param querys
	 * @return  HttpResponse
	 * @throws IotequException 出错抛出异常
	 */
	public static HttpResponse doGet(String host, Map<String, Object> headers, Map<String, Object> querys)  throws IotequException {    	
    	try {
			HttpClient httpClient = wrapClient(host);
			HttpGet request = new HttpGet(buildUrl(host, querys));
			if (headers != null)
				for (Map.Entry<String, Object> e : headers.entrySet()) {
					request.addHeader(e.getKey(), StringUtil.toString(e.getValue()));
				}
			return httpClient.execute(request);
		}catch (Exception e) {
			throw IotequException.newInstance(e);
		}
    }
	/**
	 * 以json与http进行get操作
	 * @param host  url地址
	 * @param querys  可选的参数
	 * @return  json结果
	 * @throws IotequException 异常
	 */
	@SuppressWarnings("serial")
	public static JSONObject doJsonGet(String host, Map<String, Object> querys)  throws IotequException {
		Map<String, Object> headers=new HashMap<String, Object>() {{
			put("content-type", "application/json; charset=UTF-8");
			put("Accept-Charset", "UTF-8");
            put("Connection", "Keep-Alive");
		}};
		return getHttpEntity(doGet(host,headers,querys));
	}
	/*
	 * post form
	 * 
	 * @param host
	 * @param headers
	 * @param querys
	 * @param bodys
	 * @return
	 * @throws IotequException 出错抛出异常
	 */
	public static HttpResponse doPost(String host, Map<String, Object> headers,	Map<String, Object> querys,	Map<String, Object> bodys)     throws IotequException {    	
    	try {
			HttpClient httpClient = wrapClient(host);

			HttpPost request = new HttpPost(buildUrl(host, querys));
			if (headers != null)
				for (Map.Entry<String, Object> e : headers.entrySet()) {
					request.addHeader(e.getKey(), StringUtil.toString(e.getValue()));
				}

			if (bodys != null) {
				List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

				for (String key : bodys.keySet()) {
					nameValuePairList.add(new BasicNameValuePair(key, StringUtil.toString(bodys.get(key))));
				}
				UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairList, "utf-8");
				formEntity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
				request.setEntity(formEntity);
			}

			return httpClient.execute(request);
		}catch (Exception e) {
			throw IotequException.newInstance(e);
		}
    }	
	/**
	 * 以json与http进行post操作
	 * @param host  url地址
	 * @param headers  请求头参数
	 * @param querys  可选的参数
	 * @param bodys  post的参数
	 * @return  json结果
	 * @throws IotequException 异常
	 */
	@SuppressWarnings("serial")
	public static JSONObject doJsonPost(String host, Map<String, Object> headers,Map<String, Object> querys,Map<String, Object> bodys)  throws IotequException {
		try {
			if (headers == null) headers = new HashMap<String, Object>();
			headers.put("content-type", "application/json; charset=UTF-8");
			headers.put("Accept-Charset", "UTF-8");
			headers.put("Connection", "Keep-Alive");
			return getHttpEntity(doPost(host, headers, querys, bodys));
		}catch (Exception e) {
			throw IotequException.newInstance(e);
		}
	}
	/*
	 * Post String
	 * 
	 * @param host
	 * @param headers
	 * @param querys
	 * @param body
	 * @return
	 * @throws IotequException 出错抛出异常
	 */
	public static HttpResponse doPost(String host, Map<String, Object> headers,Map<String, Object> querys,String body)    throws IotequException {    	
		try {
			HttpClient httpClient = wrapClient(host);

			HttpPost request = new HttpPost(buildUrl(host, querys));
			if (headers != null)
				for (Map.Entry<String, Object> e : headers.entrySet()) {
					request.addHeader(e.getKey(), StringUtil.toString(e.getValue()));
				}

			if (!Util.isEmpty(body)) {
				request.setEntity(new StringEntity(body, "utf-8"));
			}

			return httpClient.execute(request);
		}catch (Exception e) {
			throw IotequException.newInstance(e);
		}
    }
	/**
	 * 以json与http进行post操作
	 * @param host  url地址
	 * @param querys  可选的参数
	 * @param body  post的参数
	 * @return  json结果
	 * @throws IotequException 异常
	 */
	@SuppressWarnings("serial")
	public static JSONObject doJsonPost(String host, Map<String, Object> querys,String body)  throws IotequException {
		Map<String, Object> headers=new HashMap<String, Object>() {{
			put("content-type", "application/json; charset=UTF-8");
			put("Accept-Charset", "UTF-8");
            put("Connection", "Keep-Alive");
		}};
		return getHttpEntity(doPost(host,headers,querys,body));
	}
	/*
	 * Post stream
	 * 
	 * @param host
	 * @param headers
	 * @param querys
	 * @param body
	 * @return
	 * @throws IotequException 出错抛出异常
	 */
	public static HttpResponse doPost(String host, Map<String, Object> headers, 	Map<String, Object> querys,	byte[] body)        throws IotequException {    	
    	try {
			HttpClient httpClient = wrapClient(host);

			HttpPost request = new HttpPost(buildUrl(host, querys));
			if (headers != null)
				for (Map.Entry<String, Object> e : headers.entrySet()) {
					request.addHeader(e.getKey(), StringUtil.toString(e.getValue()));
				}

			if (body != null) {
				request.setEntity(new ByteArrayEntity(body));
			}

			return httpClient.execute(request);
		} catch (Exception e) {
			throw IotequException.newInstance(e);
		}
    }
	public static HttpResponse doPost(String host) throws IotequException {
		Map<String, Object> headers = null;
		Map<String, Object> querys = null;
		Map<String, Object> bodys=null;
		return doPost(host,headers,querys,bodys);
	}
	/*
	 * Put String
	 * @param host
	 * @param headers
	 * @param querys
	 * @param body
	 * @return
	 * @throws IotequException 出错抛出异常
	 */
	public static HttpResponse doPut(String host, Map<String, Object> headers,Map<String, Object> querys,	String body)         throws IotequException {    	
    	try {
			HttpClient httpClient = wrapClient(host);

			HttpPut request = new HttpPut(buildUrl(host, querys));
			if (headers != null)
				for (Map.Entry<String, Object> e : headers.entrySet()) {
					request.addHeader(e.getKey(), StringUtil.toString(e.getValue()));
				}

			if (!Util.isEmpty(body)) {
				request.setEntity(new StringEntity(body, "utf-8"));
			}

			return httpClient.execute(request);
		}catch (Exception e) {
			throw IotequException.newInstance(e);
		}
    }
	
	/*
	 * Put stream
	 * @param host
	 * @param headers
	 * @param querys
	 * @param body
	 * @return
	 * @throws IotequException 出错抛出异常
	 */
	public static HttpResponse doPut(String host, String path, String method, Map<String, Object> headers,	Map<String, Object> querys,	byte[] body)    throws IotequException {    	
    	try {
			HttpClient httpClient = wrapClient(host);

			HttpPut request = new HttpPut(buildUrl(host, querys));
			if (headers != null)
				for (Map.Entry<String, Object> e : headers.entrySet()) {
					request.addHeader(e.getKey(), StringUtil.toString(e.getValue()));
				}

			if (body != null) {
				request.setEntity(new ByteArrayEntity(body));
			}

			return httpClient.execute(request);
		}catch (Exception e) {
			throw IotequException.newInstance(e);
		}
    }
	
	/*
	 * Delete
	 *  
	 * @param host
	 * @param headers
	 * @param querys
	 * @return
	 * @throws IotequException 出错抛出异常
	 */
	public static HttpResponse doDelete(String host, Map<String, Object> headers,	Map<String, Object> querys)
            throws IotequException {
		try {
			HttpClient httpClient = wrapClient(host);

			HttpDelete request = new HttpDelete(buildUrl(host, querys));
			if (headers != null)
				for (Map.Entry<String, Object> e : headers.entrySet()) {
					request.addHeader(e.getKey(), StringUtil.toString(e.getValue()));
				}

			return httpClient.execute(request);
		}catch (Exception e) {
			throw IotequException.newInstance(e);
		}
    }
	
	private static String buildUrl(String host, Map<String, Object> querys) throws UnsupportedEncodingException {
    	StringBuilder sbUrl = new StringBuilder();
    	sbUrl.append(host);
    	if (null != querys) {
    		StringBuilder sbQuery = new StringBuilder();
        	for (Map.Entry<String, Object> query : querys.entrySet()) {
        		if (0 < sbQuery.length()) {
        			sbQuery.append("&");
        		}
        		if (Util.isEmpty(query.getKey()) && !Util.isEmpty(query.getValue())) {
        			sbQuery.append(query.getValue());
                }
        		if (!Util.isEmpty(query.getKey())) {
        			sbQuery.append(query.getKey());
        			if (!Util.isEmpty(query.getValue())) {
        				sbQuery.append("=");
        				sbQuery.append(URLEncoder.encode(StringUtil.toString(query.getValue()), "utf-8"));
        			}        			
                }
        	}
        	if (0 < sbQuery.length()) {
        		sbUrl.append(host.indexOf("?")>=0 ? "&":"?").append(sbQuery);
        	}
        }
    	
    	return sbUrl.toString();
    }
	
	private static HttpClient wrapClient(String host) {
		if (host.toLowerCase().startsWith("https://")) {
			return sslClient();
		} else return HttpClients.createDefault(); 
	}
	
	private static HttpClient sslClient() {        
        try {
            // 在调用SSL之前需要重写验证方法，取消检测SSL
            X509TrustManager trustManager = new X509TrustManager() {
                @Override public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                @Override public void checkClientTrusted(X509Certificate[] xcs, String str) {}
                @Override public void checkServerTrusted(X509Certificate[] xcs, String str) {}
            };
            SSLContext ctx = SSLContext.getInstance(SSLConnectionSocketFactory.TLS);
            ctx.init(null, new TrustManager[] { trustManager }, null);
            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(ctx, NoopHostnameVerifier.INSTANCE);
            // 创建Registry
            RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT)
                    .setExpectContinueEnabled(Boolean.TRUE).setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM,AuthSchemes.DIGEST))
                    .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC)).build();
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https",socketFactory).build();
            // 创建ConnectionManager，添加Connection配置信息
            PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            CloseableHttpClient closeableHttpClient = HttpClients.custom().setConnectionManager(connectionManager).setSSLSocketFactory(socketFactory)
                    .setDefaultRequestConfig(requestConfig).build();
            return closeableHttpClient;
        } catch (KeyManagementException ex) {
            throw new RuntimeException(ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
     }
}