package mypack;

public class TestCookieManager {

	public static void main(String args[]){
				
	    CookieManager cookieManager = CookieManager.getInstance();  
	    
	    String str1 = cookieManager.getTopLevelDomain("book.douban.com");
	    
	    String str = cookieManager.getCookies("book.douban.com");
	    
		System.out.println("getting Cookies...");
		if (str == null)
			{System.out.println("str is null");}
		else
			{System.out.println(str);}
	}
	
}
