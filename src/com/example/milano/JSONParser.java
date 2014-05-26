package com.example.milano;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser {
	// Response from the HTTP response
	static InputStream httpResponseStream = null;
	// JSON Response String to create JSON object
	static String jsonString = "";
	
	// Issue HTTP request, parser JSON reuslt and return JSON object
	public JSONObject makeHTTPResult(String url, String method, List<NameValuePair> params) {
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			
			// determine what method
			if (method.equals("POST")) {
				HttpPost httpPost = new HttpPost(url);
				httpPost.setEntity(new UrlEncodedFormEntity(params));
				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				httpResponseStream = httpEntity.getContent();
			} else if (method.equals("GET")) {
				String paramString = URLEncodedUtils.format(params, "utf-8");
				url += "?" + paramString;
				HttpGet httpGet = new HttpGet(url);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity httpEntity = httpResponse.getEntity();
				httpResponseStream = httpEntity.getContent();
			}
		}  catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
        try {
            // Create buffered reader for the httpResponceStream
            BufferedReader httpResponseReader = new BufferedReader(
                    new InputStreamReader(httpResponseStream, "iso-8859-1"), 8);
            // String to hold current line from httpResponseReader
            String line = null;
            // Clear jsonString
            jsonString = "";
            // While there is still more response to read
            while ((line = httpResponseReader.readLine()) != null) {
                // Add line to jsonString
                jsonString += (line + "\n");
            }
            // Close Response Stream
            httpResponseStream.close();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        try {
            // Create jsonObject from the jsonString and return it
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
            // Return null if in error
            return null;
        }
		
	}
}
