package mypack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
 
public class MyProcessor21 implements PageProcessor {
	// 抓取网站的相关配置，包括编码、抓取间隔、重试次数等
	private Site site = new Site().setRetryTimes(3).setSleepTime(1000)
			.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
			.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36")
			.addCookie("bid","e3V_nylMNas")
			.addCookie("gr_user_id", "04e43cbd-7cfb-4478-96f6-d7e4668c8d8b");
			
    private static int count =0;
	private static List list_bookname = new ArrayList<String>();
	private static List list_bookid = new ArrayList<String>();
	private static List list_rating = new ArrayList<String>();
    private static List list_tmp = new ArrayList<String>();  //save temporal strings
	
    private static Pattern pattern_bookid = Pattern.compile("[0-9]+");  //a serial number for book id
    
    
    @Override
    public Site getSite() {
        return site;
    }
 
    @Override
    public void process(Page page) {
    	
    	String logFile = "./log.txt";
    	
    	//scan next page field to determine whether exist next page
    	List list_nexturl;
    	list_nexturl = page.getHtml().xpath("//span[@class='next']/a/@href").all();
    	boolean exist_nextlist = !(list_nexturl.isEmpty());
    	
    	//add book names
        list_bookname.addAll(page.getHtml().xpath("//li[@class='subject-item']/div/h2/a/text()").all());
        
        //add book ids
        list_tmp = page.getHtml().xpath("//li[@class='subject-item']/div/a/@href").all();
        Iterator it_tmp = list_tmp.iterator();
        while(it_tmp.hasNext()){
        	String str_tmp = (String) it_tmp.next();
        	Matcher matcher = pattern_bookid.matcher(str_tmp);
        	if (matcher.find()){ list_bookid.add(matcher.group()); }
        	}
        
        //add book rating
        list_rating.addAll(page.getHtml().xpath("//div[@class='short-note']/div/span[contains(@class,'rating')]/@class").all());
        
        if(exist_nextlist){ //add next page to process
        	page.addTargetRequests(
        			page.getHtml().xpath("//span[@class='next']/a/@href").all());
        }
        
        //generate crawler log
    	List list_write = new ArrayList<>();
    	list_write.add(Boolean.toString(exist_nextlist));
    	WriteStringToFile.appendStringList(list_write, logFile);
    	        
    }
 
    public static void main(String[] args) {
        long startTime, endTime;
        System.out.println("开始爬取...");
        startTime = System.currentTimeMillis();
        Spider.create(new MyProcessor21()).addUrl("https://book.douban.com/people/infinite_space/collect").thread(1).run();
        endTime = System.currentTimeMillis();
        System.out.println("爬取结束，耗时约" + ((endTime - startTime) / 1000) + "秒，抓取了"+count+"条记录");
    
        WriteStringToFile.writeNewStringList(list_bookname, "./bookname.txt");
        WriteStringToFile.writeNewStringList(list_bookid, "./bookid.txt");
        WriteStringToFile.writeNewStringList(list_rating, "./bookrating.txt");

    }
 
}