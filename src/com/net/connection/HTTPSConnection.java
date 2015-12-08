package com.net.connection;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HTTPSConnection extends HTTPConnection{
	
	private void setSSLContext() throws NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException{
		TrustManager[] tms = { new X509TrustManager() {
			public void checkClientTrusted(X509Certificate certificates[], String authType) throws CertificateException {}
			public void checkServerTrusted(X509Certificate[] ax509certificate, String s) throws CertificateException {}
			public X509Certificate[] getAcceptedIssuers() { return null;}
		}};
		SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
		sslContext.init(null, tms, new SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
	}
	public HTTPSConnection(String host) throws KeyManagementException, NoSuchAlgorithmException, NoSuchProviderException {
		this(host,(short)443);
	}

	public HTTPSConnection(String host, short port) throws KeyManagementException, NoSuchAlgorithmException, NoSuchProviderException {
		this(host, port, 5000);
	}

	public HTTPSConnection(String host, int timeout) throws KeyManagementException, NoSuchAlgorithmException, NoSuchProviderException {
		this(host, (short) 443, timeout);
	}

	public HTTPSConnection(String host, short port, int timeout) throws KeyManagementException, NoSuchAlgorithmException, NoSuchProviderException {
		this(host, port, timeout, "UTF-8");
	}

	public HTTPSConnection(String host, short port, int timeout, String encoding) throws KeyManagementException, NoSuchAlgorithmException, NoSuchProviderException {
		super(host, port, timeout, encoding);
		setSSLContext();
	}
	public byte[] request(String file) throws Exception {
		return request("GET", file);
	}

	public byte[] request(String method, String file) throws Exception {
		return request(method, file, null);
	}
	
	public byte[] request(String method,Map<String, String> params, String file)
			throws Exception {
		return request("HTTPS",method, file, params, null);
	}
	
	public byte[] request(String method, String file,Map<String, String> params, Map<String, String> headers)
			throws Exception {
		return request("HTTPS",method, file, params, headers);
	}

	public byte[] request(String method, String file, Map<String, String> headers)
			throws Exception {
		return request("HTTPS",method, file, null, headers);
	}

	public byte[] request(String protocol,String method, String file, Map<String, String> params,
			Map<String, String> headers) throws Exception {
		return super.request(protocol, method, file, params, headers);
	}
	public static void main(String[] args) {
		try {
			HTTPSConnection conn = new HTTPSConnection("kyfw.12306.cn");
			Map<String, String> params = new HashMap<String, String>();
			params.put("leftTicketDTO.train_date", "2015-12-17");
			params.put("leftTicketDTO.from_station", "SHH");
			params.put("leftTicketDTO.to_station", "NFF");
			params.put("purpose_codes", "ADULT");
			byte[] res = conn.request("GET", params,"/otn/leftTicket/log");
			System.out.println(new String(res));
			res = conn.request("GET", params,"/otn/leftTicket/queryT");
			System.out.println(new String(res));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
