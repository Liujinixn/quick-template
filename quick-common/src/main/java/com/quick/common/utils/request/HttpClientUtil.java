package com.quick.common.utils.request;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public class HttpClientUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtil.class);

	private static RequestConfig requestConfig = null;
	private static CloseableHttpClient httpClient = null;
	private static PoolingHttpClientConnectionManager connectionManager = null;

	static {
		connectionManager = new PoolingHttpClientConnectionManager(1, TimeUnit.MINUTES);
		connectionManager.setMaxTotal(1000);
		connectionManager.setDefaultMaxPerRoute(100);
		httpClient = HttpClients.custom().setConnectionManager(connectionManager).disableAutomaticRetries().build();
		requestConfig = RequestConfig.custom().setSocketTimeout(200000).setConnectTimeout(200000)
				.setConnectionRequestTimeout(200000).build();
	}

	/**
	 * 发送 post请求
	 *
	 * @param httpUrl 地址
	 */
	public static String sendHttpPost(String httpUrl) {
		HttpPost httpPost = new HttpPost(httpUrl);
		return sendHttpPost(httpPost);
	}

	/**
	 * 发送 post请求
	 *
	 * @param httpUrl 地址
	 * @param params  参数(格式:key1=value1&key2=value2)
	 */
	public static String sendHttpPost(String httpUrl, String params) {
		HttpPost httpPost = new HttpPost(httpUrl);// 创建httpPost
		StringEntity stringEntity = new StringEntity(params, "UTF-8");
		stringEntity.setContentType("application/x-www-form-urlencoded");
		httpPost.setEntity(stringEntity);
		return sendHttpPost(httpPost);
	}

	/**
	 * 发送 post请求
	 *
	 * @param httpUrl 地址
	 * @param params  参数(格式:key1=value1&key2=value2)
	 */
	public static String sendHttpPostHeaders(String httpUrl, String params, Map<String, String> headers) {
		HttpPost httpPost = new HttpPost(httpUrl);// 创建httpPost
		StringEntity stringEntity = new StringEntity(params, "UTF-8");
		stringEntity.setContentType("application/x-www-form-urlencoded");
		for (Entry<String, String> entry : headers.entrySet()) {
			httpPost.setHeader(entry.getKey(), entry.getValue());
		}
		httpPost.setEntity(stringEntity);
		return sendHttpPost(httpPost);
	}

	/**
	 * 发送 post请求
	 *
	 * @param httpUrl 地址
	 * @param params  参数(格式:json)
	 */
	public static String sendHttpPostByJson(String httpUrl, String params, Map<String, String> headers) {
		HttpPost httpPost = new HttpPost(httpUrl);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json");
		for (Entry<String, String> entry : headers.entrySet()) {
			httpPost.setHeader(entry.getKey(), entry.getValue());
		}
		if (params == null) {
			params = "";
		}
		StringEntity stringEntity = new StringEntity(params, "UTF-8");
		httpPost.setEntity(stringEntity);
		return sendHttpPost(httpPost);
	}

	/**
	 * 发送Post请求
	 *
	 * @param httpPost
	 * @return
	 */
	private static String sendHttpPost(HttpPost httpPost) {
		httpPost.setConfig(requestConfig);
		HttpEntity entity = null;
		String responseContent = null;
		try (CloseableHttpResponse response = httpClient.execute(httpPost);) {
			entity = response.getEntity();
			responseContent = EntityUtils.toString(entity, "UTF-8");
		} catch (IOException e) {
			LOGGER.error("sendHttpPost " + e.getMessage());
			throw new RuntimeException(e);
		}
		return responseContent;
	}

	/**
	 * 发送 get请求
	 *
	 * @param httpUrl
	 */
	public static String sendHttpGet(String httpUrl) {
		HttpGet httpGet = new HttpGet(httpUrl);// 创建get请求
		return sendHttpGet(httpGet);
	}

	/**
	 *
	 */
	public static String appendParamToUrl(String url, Map<String, Object> paramMap) {
		if (paramMap == null || 0 == paramMap.size()) {
			return url;
		}
		String result = "";
		for (Entry<String, Object> entry : paramMap.entrySet()) {
			result += entry.getKey() + "=" + entry.getValue() + "&";
		}
		return url + "?" + result.substring(0, result.length() - 1);
	}

	/**
	 * 发送 get请求
	 *
	 */
	public static String sendHttpGet(String httpUrl, Map<String, String> headers) {
		HttpGet httpGet = new HttpGet(httpUrl);
		if (headers != null && !headers.isEmpty()) {
			for (Entry<String, String> header : headers.entrySet()) {
				httpGet.setHeader(header.getKey(), header.getValue());
			}
		}
		return sendHttpGet(httpGet);
	}

	/**
	 * 发送Get请求
	 *
	 * @param httpGet
	 * @return
	 */
	private static String sendHttpGet(HttpGet httpGet) {
		HttpEntity entity = null;
		String responseContent = null;
		httpGet.setConfig(requestConfig);
		try (CloseableHttpResponse response = httpClient.execute(httpGet);) {
			entity = response.getEntity();
			responseContent = EntityUtils.toString(entity, "UTF-8");
		} catch (IOException e) {
			LOGGER.error("sendHttpGet " + e.getMessage());
			throw new RuntimeException(e);
		}
		return responseContent;
	}


	/**
	 * 发送 put请求
	 *
	 */
	public static String sendHttpPutJson(String httpUrl, String body, Map<String, String> headers) {
		HttpPut httpPut = new HttpPut(httpUrl);
		if (headers != null && !headers.isEmpty()) {
			for (Entry<String, String> header : headers.entrySet()) {
				httpPut.setHeader(header.getKey(), header.getValue());
			}
		}
		httpPut.setHeader("Content-Type", "application/json");
		HttpEntity entity = new StringEntity(body, Consts.UTF_8);
		httpPut.setEntity(entity);
		return sendHttpPut(httpPut);
	}

	/**
	 * 发送Put请求
	 *
	 * @param httpPut
	 * @return
	 */
	private static String sendHttpPut(HttpPut httpPut) {
		HttpEntity entity = null;
		String responseContent = null;
		httpPut.setConfig(requestConfig);
		try (CloseableHttpResponse response = httpClient.execute(httpPut);) {
			entity = response.getEntity();
			responseContent = EntityUtils.toString(entity, "UTF-8");
		} catch (IOException e) {
			LOGGER.error("sendHttpPut " + e.getMessage());
			throw new RuntimeException(e);
		}
		return responseContent;
	}


	/**
	 * 下载文件
	 */
	public static void httpDownload(String url, Path localFile) {
		HttpGet httpGet = new HttpGet(url);
		HttpEntity entity = null;
		httpGet.setConfig(requestConfig);
		try (OutputStream outputStream = new FileOutputStream(localFile.toFile());
				CloseableHttpResponse response = httpClient.execute(httpGet);) {
			entity = response.getEntity();
			IOUtils.copyLarge(entity.getContent(), outputStream);
		} catch (Exception e) {
			LOGGER.error("httpDownload", e);
			throw new RuntimeException(e);
		}
	}



	/**
	 * 发送http请求
	 *
	 * @param httpUrl 地址
	 * @param params  参数(格式:json)
	 */
	public static String sendHttpByJsonAndHeader(HttpRequestBase httpRequestBase, String httpUrl, String params,
			Header[] headers) {
		try {
			httpRequestBase.setURI(new URI(httpUrl));
		} catch (URISyntaxException e) {
			LOGGER.error("setURI", e);
		}
		if (headers != null && headers.length > 0) {
			for (Header header : headers) {
				httpRequestBase.setHeader(header);
			}
		}
		httpRequestBase.setHeader("Accept", "application/json");
		httpRequestBase.setHeader("Content-type", "application/json");
		if(StringUtils.isNotBlank(params)) {
			StringEntity stringEntity = new StringEntity(params, "UTF-8");
			if (httpRequestBase instanceof HttpPut) {
				((HttpPut) httpRequestBase).setEntity(stringEntity);
			} else if (httpRequestBase instanceof HttpPost) {
				((HttpPost) httpRequestBase).setEntity(stringEntity);
			}
		}
		return sendHttp(httpRequestBase);
	}

	/**
	 * 发送Http请求
	 *
	 * @param httpRequestBase
	 * @return
	 */
	private static String sendHttp(HttpRequestBase httpRequestBase) {
		HttpEntity entity = null;
		String responseContent = null;
		httpRequestBase.setConfig(requestConfig);
		try (CloseableHttpResponse response = httpClient.execute(httpRequestBase);) {
			entity = response.getEntity();
			responseContent = EntityUtils.toString(entity, "UTF-8");
		} catch (IOException e) {
			LOGGER.error("sendHttp " + e.getMessage());
			throw new RuntimeException(e);
		}
		return responseContent;
	}


	public static RestTemplate createRestTemplate() {
		final RestTemplate restTemplate = new RestTemplate();
		// sslIgnore
		SimpleClientHttpRequestFactory requestFactory;
		requestFactory = getUnsafeClientHttpRequestFactory();
		requestFactory.setConnectTimeout(120 * 1000);
		requestFactory.setReadTimeout(120 * 1000);
		restTemplate.setRequestFactory(requestFactory);
		return restTemplate;
	}

	private static SimpleClientHttpRequestFactory getUnsafeClientHttpRequestFactory() {
		TrustManager[] byPassTrustManagers = new TrustManager[] { new X509TrustManager() {
			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}

			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) {
			}
		} };
		final SSLContext sslContext;
		try {
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, byPassTrustManagers, new SecureRandom());
			sslContext.getSocketFactory();
		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			throw new RuntimeException(e);
		}

		return new SimpleClientHttpRequestFactory() {
			@Override
			protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
				super.prepareConnection(connection, httpMethod);
				if (connection instanceof HttpsURLConnection) {
					((HttpsURLConnection) connection).setSSLSocketFactory(sslContext.getSocketFactory());
				}
			}
		};
	}

}
