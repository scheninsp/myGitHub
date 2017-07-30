package mypack;

import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
 
public class MyProcessor2 implements PageProcessor {
    // 抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
    private static int count =0;
     
    @Override
    public Site getSite() {
        return site;
    }
 
    @Override
    public void process(Page page) {
    	
    	String logFile = "./log.txt";
    	
        //判断链接是否符合http://www.cnblogs.com/任意个数字字母-/p/7个数字.html格式
        if(!page.getUrl().regex("http://webmagic.io/docs/zh/posts/[a-z 0-9 -]+/[a-z 0-9 -]+.html").match()){
    	//加入满足条件的链接
        	System.out.println("-----------------------------------------");
        	List urlList;
        	urlList = page.getHtml().xpath("//li[@class='chapter']/a/@href").all();
        	
        	WriteStringToFile.appendStringList(urlList,logFile);
            //page.addTargetRequests(
          //          page.getHtml().xpath("//li[@class='chapter']/a/@href").all());
        }else{                             
            //获取页面需要的内容
          //  System.out.println("抓取的内容："+
          //          page.getHtml().xpath("//head/title/text()").get()
          //          );
            count ++;
        }
    }
 
    public static void main(String[] args) {
        long startTime, endTime;
        System.out.println("开始爬取...");
        startTime = System.currentTimeMillis();
        Spider.create(new MyProcessor2()).addUrl("http://webmagic.io/docs/zh/").thread(5).run();
        endTime = System.currentTimeMillis();
        System.out.println("爬取结束，耗时约" + ((endTime - startTime) / 1000) + "秒，抓取了"+count+"条记录");
    }
 
}