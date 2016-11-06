package gitadapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class RequestUrl {
	
	
	public static void main(String[] args) throws MalformedURLException, IOException {
		request();
	}
	
	private static String request() throws MalformedURLException, IOException {
		// TODO Auto-generated method stub
		String charset = "UTF-8";
		String responseBody ;
		StringBuilder sb = new StringBuilder();
		String url = "https://api.github.com/users/BuildSlackers/repos";
		URLConnection connection = new URL(url).openConnection();
		connection.setRequestProperty("Accept-Charset", charset);
		InputStream response = connection.getInputStream();
		//InputStream response = new URL(url).openStream();
		try (Scanner scanner = new Scanner(response)) {
		    responseBody = scanner.useDelimiter("\\A").next();
		    System.out.println(responseBody);
		}
		 JSONArray obj = new JSONArray(responseBody);
		// JSONArray arr = obj.getJSONArray("name");
		 for (int i =0;i<obj.length();i++)
		 {
			 JSONObject obj1 = obj.getJSONObject(i);
			 sb.append(obj1.getString("name")+"\n");
			 System.out.println(obj1.getString("name"));
			 
		 }
		 return sb.toString();
		
	}
}
