package com.example.cookbook;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class GetCookbookData {
	
	/*申请到的 聚合数据api 的key*/
	private final String appKey = "dbab58a7a5fb096079bf765370257eef";
	private final String url = "http://apis.juhe.cn/cook/query";
	
	/*菜谱信息结构体*/
	public class Cookbook {
		public int id;	//id
		public String title;	//标题
		public String tags;	//标签
		public String imtro;	//介绍
		public String ingredients;	//材料
		public String burden;	//原料
		public String albums;	//图片
		public ArrayList<String> step = new ArrayList<String>();	//步骤
	}
	
	/**
	 * 获得api返回的json字符串
	 * @param menu
	 * @return
	 */
	private String getJsonFromServer(String menu) throws Exception {
		
		BufferedReader in = null;
		String result = null;
		
		try { 
			HttpClient client = new DefaultHttpClient();
			HttpPost request = new HttpPost(url);
			
	        // 创建名/值组列表  
	        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
	        parameters.add(new BasicNameValuePair("key", appKey));
	        parameters.add(new BasicNameValuePair("menu", menu));
	        parameters.add(new BasicNameValuePair("albums", "1"));
	        
	        // 创建UrlEncodedFormEntity对象  
	        UrlEncodedFormEntity formEntiry = new UrlEncodedFormEntity(parameters, HTTP.UTF_8);//设置编码，防止中文乱码 
	        request.setEntity(formEntiry);
	        
	        // 执行请求  
	        HttpResponse response = client.execute(request);
	        
	        // 接收并处理返回数据
	        in = new BufferedReader(new InputStreamReader(response.getEntity().getContent())); 
	        StringBuffer sb = new StringBuffer("");
	        String line = "";
	        String NL = System.getProperty("line.separator");
	        while ((line = in.readLine()) != null) {  
	            sb.append(line + NL);
	        } 
	        in.close(); 
	        result = sb.toString();
	        
	        //测试用，logcat打印返回的json字符串
	        Log.i("myTag", "服务器返回的结果：" + result);
        
		} catch(Exception e) {
			Log.e("myTag", "getJsonFromServer出错 ");
			e.printStackTrace();
		} finally {
			if (in != null) {
                try {
                    in.close();  
                } catch (Exception e) {  
                	Log.e("myTag", e.getMessage());
                }  
            }
		}
		
		return result;
	}
	
	/**
	 * 将json字符串转换成cookbook类型的ArrayList容器
	 * @param json
	 * @throws JSONException 
	 */
	private ArrayList<Cookbook> jsonToCookbook(String json) throws JSONException {
		
		//初始化ArrayList<Cookbook>
		ArrayList<Cookbook> listCookbook =  new ArrayList<Cookbook>();
		
		//获得返回的状态码
		JSONObject jsonObject = new JSONObject(json);
		int resultCode = jsonObject.getInt("resultcode");
		
		//状态码为200再解析
		if(resultCode == 200) {
			JSONObject jsonObjectResult = new JSONObject(jsonObject.getString("result"));
			JSONArray jsonArrayData = new JSONArray(jsonObjectResult.getString("data"));		
			for(int i = 0; i < jsonArrayData.length(); i++) {
				Cookbook cookbook = new Cookbook();
				JSONObject jsonObjectCookbook = (JSONObject) jsonArrayData.get(i);
				JSONArray jsonArraySteps = new JSONArray(jsonObjectCookbook.getString("steps"));
				
				cookbook.id = jsonObjectCookbook.getInt("id");
				cookbook.title = jsonObjectCookbook.getString("title");
				cookbook.tags = jsonObjectCookbook.getString("tags");
				cookbook.imtro = jsonObjectCookbook.getString("imtro");
				
				//去掉地址中的转义符号\
				cookbook.imtro = cookbook.imtro.replace("\\" , "");	
				
				cookbook.ingredients = jsonObjectCookbook.getString("ingredients");
				cookbook.burden = jsonObjectCookbook.getString("burden");
				cookbook.albums = jsonObjectCookbook.getString("albums");
				
				for(int j = 0; j < jsonArraySteps.length(); j++) {
					String img;
					JSONObject JSONObjectStep = (JSONObject) jsonArraySteps.get(j);
					//去掉地址中的转义符号\
//					img = JSONObjectStep.getString("img").replace("\\" , "");
//					cookbook.step.add(img);		
					cookbook.step.add(JSONObjectStep.getString("step"));
				}
				listCookbook.add(cookbook);
			}
			return listCookbook;
		} else {
			String reason = jsonObject.getString("reason");
			int errorCode = jsonObject.getInt("error_code");
			Log.e("mytag", "resultcode: " + resultCode + "\nreason: " + reason + "\nerror_code: " + errorCode);
			return null;
		}

	}
	
	public ArrayList<Cookbook> run(String menu) {
		String result = null;
		ArrayList<Cookbook> listCookbook =  new ArrayList<Cookbook>();
		
		try {
			result = getJsonFromServer(menu);
			if(result != null) {
				listCookbook = jsonToCookbook(result);
			}
		} catch (Exception e) {
			Log.d("myTag", "无法从服务端获得数据: ");
			e.printStackTrace();
		}
		
		return listCookbook; 	
	}
}
