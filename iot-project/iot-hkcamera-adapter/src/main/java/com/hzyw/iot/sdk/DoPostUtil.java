package com.hzyw.iot.sdk;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * httpclient发送请求
 *
 */
public class DoPostUtil {

	public static String enterUrl = ResourcesConfig.postUrl+"/api/etc/exchange/updateEnter";
	public static String exitUrl = ResourcesConfig.postUrl+"/api/etc/exchange/updateExit";
    /*public static void main(String[] args) throws Exception {
    	String apiUrl = "http://192.168.10.200:181/api/etc/exchange/updateEnter";
    	Map<String, String> headers = new HashMap<>();
    	//headers.put("X-Auth-Token", tokenId);
    	String json = "{\"bizContent\": {\"berthCode\": \"0007\", \"plateNum\": \"淅G----10\",\"exitTime\": 1578928956},\"parkCode\": \"test-hk--001\", \"serviceName\": \"exit\"}";
    	doPost( headers, enterUrl, json); 
    }*/
    
    /**
     * 入场
     */
    public static void postEnter(String json){
    	Map<String, String> headers = new HashMap<>();
    	//headers.put("X-Auth-Token", tokenId);
    	doPost( headers, enterUrl, json); 
    }
    
    /**
     * 出场
     */
    public static void postExit(String json){
    	Map<String, String> headers = new HashMap<>();
    	//headers.put("X-Auth-Token", tokenId);
    	doPost( headers, exitUrl, json); 
    }
    
    private static PoolingHttpClientConnectionManager connMgr;
	private static RequestConfig requestConfig;
	private static final int MAX_TIMEOUT = 7000;

	static {
		// 设置连接池
		connMgr = new PoolingHttpClientConnectionManager();
		// 设置连接池大小
		connMgr.setMaxTotal(100);
		connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());
	
		RequestConfig.Builder configBuilder = RequestConfig.custom();
		// 设置连接超时
		configBuilder.setConnectTimeout(MAX_TIMEOUT);
		// 设置读取超时
		configBuilder.setSocketTimeout(MAX_TIMEOUT);
		// 设置从连接池获取连接实例的超时
		configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
		// 在提交请求之前 测试连接是否可用
		configBuilder.setStaleConnectionCheckEnabled(true);
		requestConfig = configBuilder.build();
	} 
	
    public static String doPost(Map<String, String> headers, String apiUrl, Map<String, Object> params) {
    	System.out.println(apiUrl);

    	CloseableHttpClient httpClient = HttpClients.createDefault();
    	String httpStr = null;
    	HttpPost httpPost = new HttpPost(apiUrl);
    	CloseableHttpResponse response = null;

    	try {
	    	httpPost.setConfig(requestConfig);
	    	List<NameValuePair> pairList = new ArrayList<>(params.size());
	    	for (Map.Entry<String, Object> entry : params.entrySet()) {
		    	NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
		    	pairList.add(pair);
	    	}
	    	httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));
	    	for (Map.Entry<String, String> entry : headers.entrySet()) {
	    		httpPost.setHeader(entry.getKey(), entry.getValue());
	    	}
	    	response = httpClient.execute(httpPost);
	    	System.out.println(response.getStatusLine().getStatusCode());
	    	System.out.println(response.toString());
	    	HttpEntity entity = response.getEntity();
	    	httpStr = EntityUtils.toString(entity, "UTF-8");
    	} catch (IOException e) {
    		e.printStackTrace();
    	} finally {
	    	if (response != null) {
		    	try {
		    		EntityUtils.consume(response.getEntity());
		    	} catch (IOException e) {
		    		e.printStackTrace();
		    	}
	    	}
	    	try {
				httpClient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	return httpStr;
    } 
    
    /**
    * 发送 POST 请求,JSON形式 ,并可携带多个请求头
    * @param headers
    * @param apiUrl
    * @param json
    * @return
    */
    public static String doPost(Map<String, String> headers, String apiUrl, String json) {
	    CloseableHttpClient httpClient = HttpClients.createDefault();
	    String httpStr = null;
	    HttpPost httpPost = new HttpPost(apiUrl);
	    CloseableHttpResponse response = null;
	
	    try {
		    httpPost.setConfig(requestConfig);
		    StringEntity stringEntity = new StringEntity(json.toString(), "UTF-8");// 解决中文乱码问题
		    stringEntity.setContentEncoding("UTF-8");
		    stringEntity.setContentType("application/json");
		    httpPost.setEntity(stringEntity);
		    for (Map.Entry<String, String> entry : headers.entrySet()) {
		    	httpPost.setHeader(entry.getKey(), entry.getValue());
		    }
		    response = httpClient.execute(httpPost);
		    HttpEntity entity = response.getEntity();
		    System.out.println(response.getStatusLine().getStatusCode());
		    httpStr = EntityUtils.toString(entity, "UTF-8");
	    } catch (IOException e) {
	    	e.printStackTrace();
	    } finally {
		    if (response != null) {
			    try {
			    	EntityUtils.consume(response.getEntity());
			    } catch (IOException e) {
			    	e.printStackTrace();
			    }
		    }
		    try {
				httpClient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    return httpStr;
    } 
    
    
    
} 