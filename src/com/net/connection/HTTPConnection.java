package com.net.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import com.logger.Logger;

public class HTTPConnection {
	protected Properties headers = null;
	protected Map<String, Cookie> cookies = null;
	private String encoding = "UTF-8";// 默认编码方式
	private String host = null;
	private short port = -1;
	private String method = "GET";
	private int timeout;
	private Map<String, List<String>> response = null;

	public HTTPConnection(String host) {
		this(host, 5000);
	}

	public HTTPConnection(String host, short port) {
		this(host, port, 5000);
	}

	public HTTPConnection(String host, int timeout) {
		this(host, (short) 80, timeout);
	}

	public HTTPConnection(String host, short port, int timeout) {
		this(host, port, timeout, "encoding");
	}

	public HTTPConnection(String host, short port, int timeout, String encoding) {
		this.host = host;
		this.port = port;
		this.timeout = timeout;
		this.encoding = encoding;
		try {
			headers = new Properties();
			headers.load(HTTPConnection.class
					.getResourceAsStream("headers.properties"));
			if (encoding != null && encoding.length() != 0)
				this.encoding = encoding;
		} catch (IOException e) {
			Logger.error(e);
		}
		this.cookies = new HashMap<String, Cookie>();
	}

	public byte[] request(String file) throws Exception {
		return request("GET", file);
	}

	public byte[] request(String method, String file) throws Exception {
		return request(method, file, null);
	}
	
	public byte[] request(String file, Map<String, String> headers)
			throws Exception {
		return request("HTTP","GET", file, null, headers);
	}

	public byte[] request(String method, String file, Map<String, String> headers)
			throws Exception {
		return request("HTTP",method, file, null, headers);
	}

	public byte[] request(String protocol,String method, String file, Map<String, String> params,
			Map<String, String> headers) throws Exception {
		if (!"GET".equalsIgnoreCase(method) && !"POST".equalsIgnoreCase(method)) {
			Exception e = new Exception(
					"Request Method Must be \"GET\" or \"POST\".");
			Logger.error(e);
			throw e;
		}
		URL url;
		String paramString = getHTTPRequestParameter(params);
		if ("GET".equalsIgnoreCase(method)) {
			if (paramString.length() > 0)
				paramString = "?" + paramString;
			url = new URL(protocol, host, port, file + paramString);
		} else {
			url = new URL(protocol, host, port, file);
		}
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setInstanceFollowRedirects(true);
		connection.setRequestMethod(method);
		connection.setDoOutput(true);
		connection.setReadTimeout(timeout);
		setHTTPRequestHeaders(connection, headers);
		if ("POST".equalsIgnoreCase(method)) {
			setPostBody(connection, paramString);
		}
		int respCode = connection.getResponseCode();
		System.out.println(respCode);
		String respStatus = connection.getResponseMessage();
		Logger.info("HTTPConnection", host+"\t"+respCode+"\t"+respStatus+"\n");
		if(respCode==200){
			InputStream is = connection.getInputStream();
			BufferByte buffer = new BufferByte();
			byte[] bs = new byte[1024];
			int len = -1;
			while((len=is.read(bs))!=-1){
				buffer.append(bs, 0, len);
			}
			is.close();
			//从响应头中获取Cookie
			response = connection.getHeaderFields();
			Set<Map.Entry<String, List<String>>> responseHeaders = connection.getHeaderFields().entrySet();
			Logger.info("Response-Headers", connection.getHeaderFields());
			for (Map.Entry<String, List<String>> entry : responseHeaders) {
				if("Set-Cookie".equals(entry.getKey())){
					for (int i = 0; i < entry.getValue().size(); i++) {
						Cookie cookie = Cookie.parse(entry.getValue().get(i));
						cookies.put(cookie.getName(),cookie);
					} 
				}
			}
			connection.disconnect();
			return buffer.getBuffer();
		}else if(respCode==404){
			Logger.info("HTTPConnection","File on Host:"+host+" Not Found!\n");
			System.err.println("File on Host:"+host+" Not Found!");
		}else if(respCode==500){
			Logger.info("HTTPConnection","Server on Host:"+host+" Denied Access!\n");
			System.err.println("Server on Host:"+host+" Denied Access!");
		}else if(respCode==302){
			List<String> newurls = connection.getHeaderFields().get("Location");
			Logger.info("RELocation", newurls);
			URL newurl = new URL(newurls.get(0));
			this.host = newurl.getHost();;
			String newfile = newurl.getFile();
			System.out.println("Location was ReLocated to Host: "+host+" @ File Addr: "+newfile);
			return request("HTTP",method, newfile, params, headers);
		}
		return null;
	}

	private void setHTTPRequestHeaders(HttpURLConnection connection,
			Map<String, String> headers) {
		if (headers != null) {
			for (Entry<String, String> entry : headers.entrySet()) {
				this.headers.put(entry.getKey(), entry.getValue());
			}
		}
		for (Entry<Object, Object> entry : this.headers.entrySet()) {
			connection.setRequestProperty((String)entry.getKey(), (String)entry.getValue());
		}
		StringBuffer cookieBuffer = new StringBuffer();
		Set<Map.Entry<String, Cookie>> entrys = cookies.entrySet();
		for (Map.Entry<String, Cookie> entry : entrys) {
			Cookie cookie = entry.getValue();
			cookieBuffer.append(cookie.getName() + "=" + cookie.getValue()+ ";");
		}
		connection.setRequestProperty("Cookie", cookieBuffer.toString());
		connection.setRequestProperty("Host", this.host);

	}

	private String getHTTPRequestParameter(Map<String, String> params) {
		if (params == null)
			return "";
		StringBuffer paramString = new StringBuffer();
		for (Entry<String, String> entry : params.entrySet()) {
			if (paramString.length()>0)
				paramString.append("&");
			paramString.append(entry.getKey() + "=" + entry.getValue());
		}
		return paramString.toString();
	}

	private void setPostBody(HttpURLConnection connection, String paramString)
			throws IOException {
		connection.setDoInput(true);
		OutputStream os = connection.getOutputStream();
		os.write(paramString.getBytes());
		os.flush();
		os.close();
	}

	public static void main(String[] args) {
		HTTPConnection conn = new HTTPConnection("w.seu.edu.cn");
		try {
			byte[] res = conn.request("/");
			System.out.println(new String(res));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
