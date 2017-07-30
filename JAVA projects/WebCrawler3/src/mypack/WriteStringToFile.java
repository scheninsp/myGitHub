package mypack;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

public class WriteStringToFile {
	
	// write one string line into strFile.
	public static void writeStringLine(final String strBuffer, final FileWriter fileWriter){
		try{			
			fileWriter.write(strBuffer+"\r\n");
			//fileWriter.write();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	//write a string List to a new file
	public static void writeNewStringList(final List strList, final String strFilename){
		try{
			File fileText = new File(strFilename);
			FileWriter fileWriter = new FileWriter(fileText);
			
			Iterator it1 = strList.iterator();
			while (it1.hasNext()){
			writeStringLine(it1.next().toString(), fileWriter);}
			
			fileWriter.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	//append a string List to a file
	public static void appendStringList(final List strList, final String strFilename){
		try{
			File fileText = new File(strFilename);
			FileWriter fileWriter = new FileWriter(fileText,fileText.exists());
			
			Iterator it1 = strList.iterator();
			while (it1.hasNext()){
			writeStringLine(it1.next().toString(), fileWriter);}
			
			fileWriter.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	
	//append string list to a file with current time
	public static void appendLog(final List strList, final String strFilename){
		try{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String curDatetime = formatter.format(new java.util.Date());
			
			File fileText = new File(strFilename);
			FileWriter fileWriter = new FileWriter(fileText,fileText.exists());
			
			fileWriter.write("---------------"+ curDatetime +"----------\r\n");
			
			Iterator it1 = strList.iterator();
			while (it1.hasNext()){
			writeStringLine(it1.next().toString(), fileWriter);}
			
			fileWriter.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
}
