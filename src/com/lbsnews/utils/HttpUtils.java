package com.lbsnews.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class HttpUtils {
	private static final String TAG = "**********HttpUtils**********";

	public static void httpPostMethod(String url, JSONObject json, Handler handler)	
			throws UnsupportedEncodingException, IOException, ClientProtocolException {
		httpPostMethod(url, json.toString(), handler);
	}

	public static void httpPostMethod(String url, JSONArray json, Handler handler)	
			throws UnsupportedEncodingException, IOException, ClientProtocolException {
		httpPostMethod(url, json.toString(), handler);
	}

	public static void httpPostMethod(String url, String json, Handler handler)	
			throws UnsupportedEncodingException, IOException, ClientProtocolException {
		HttpParams params = new BasicHttpParams();
		//�������ӳ�ʱʱ��
		HttpConnectionParams.setConnectionTimeout(params, 50000);
		HttpClient client = new DefaultHttpClient(params);

		//�ύjson���ݵ�������
		HttpPost request = new HttpPost(url);
		StringEntity se = new StringEntity(json, HTTP.UTF_8);
	//	se.setContentEncoding("UTF-8");
		se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
                    "application/json"));
		se.setContentType("text/json");
	//	request.setHeader("Content-Type","application/json;charset=UTF-8");  
		request.setEntity(se);;

		//	request.setHeader("json", json);
		HttpResponse response = client.execute(request);
	//	LogUtils.i(TAG, "sending json: " + json);

		//��ȡ���������ص�����
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){

			String res = EntityUtils.toString(response.getEntity(), "UTF-8");
			Log.d("httpResponse", res);
			Message msg = new Message();
			msg.what = 0;
			Bundle bundle = new Bundle();
			bundle.putString("res", res);
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}


}
