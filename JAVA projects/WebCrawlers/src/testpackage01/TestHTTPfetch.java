package testpackage01;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class TestHTTPfetch {

	public static void main(String[] args) {
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet("http://www.baidu.com");
		System.out.println("request: "+httpget.getURI());
		
		try{
			HttpResponse response = httpClient.execute(httpget);
			System.out.println(response.getStatusLine());
			Header[] heads = response.getAllHeaders();
			for (Header h:heads){
				System.out.println(h.getName()+" : "+h.getValue());
			}
			
			HttpEntity entity = response.getEntity();
			System.out.println("-----------------------------------");
			
			if (entity != null){
				System.out.println(EntityUtils.toString(entity));
				System.out.println("---------------------------------------");
				System.out.println("LENGTH : " + entity.getContentLength());
			}
		}
		catch (ClientProtocolException e){
			e.printStackTrace();
		}
		catch (IOException e){
			e.printStackTrace();
		}finally{
			httpClient.getConnectionManager().shutdown();
		}
		
	}

}
