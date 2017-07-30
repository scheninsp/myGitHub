package mypack;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestMyClass {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
    	String logFile = "./log.txt";
    	String str1 = "aaa21935876sss";
    	String str2 = "bbbsudh328747iuf";
    	
    	List<String> list1 = new ArrayList<>();
    	List<String> list2 = new ArrayList<>();
    	list1.add(str1);
    	list2.add(str2);
    	
    	WriteStringToFile.appendLog(list1,logFile);
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	WriteStringToFile.appendLog(list2,logFile);
    	
    	Pattern pattern_topick = Pattern.compile("[0-9]+");
    	Matcher matcher = pattern_topick.matcher(str1);
    	while(matcher.find()){
    	System.out.println(matcher.group());}

	}

}
