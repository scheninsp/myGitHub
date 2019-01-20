package decompress;

import java.io.File;
import java.util.ArrayList;

import decompress.DeCompressUtil;

public class MainActivity {

	private static ArrayList<String> filelist = new ArrayList<String>();

	static void decompressFiles(String filePath){
		File root = new File(filePath);
		File[] files = root.listFiles();
		for(File file:files){
			if(file.isDirectory()){
				System.out.println("Enter: "+ file.getAbsolutePath());
				decompressFiles(file.getAbsolutePath());
				filelist.add(file.getAbsolutePath());
			}
			else{
				String file_fullpath = file.getAbsolutePath();
				System.out.println("Processing: "+ file_fullpath);
				
				String type = file_fullpath.substring(file_fullpath.lastIndexOf(".")+1); 
				//process file according to fileType
			    if(type.equals("zip") || type.equals("rar")){ 
			    	try{
			    		DeCompressUtil.deCompress(file.getAbsolutePath(),filePath);
			    		file.delete();
			    	}
			    	catch(Exception e){
			    		e.printStackTrace();
			    	}
			    }
			    
			    else if(type.equals("torrent")){
			    	try{
			    		file.delete();
			    	}
			    	catch(Exception e){
			    		e.printStackTrace();
			    	}
			    }
			    
			    else{
			    	//not '.zip', '.rar', '.torrent'
			    	//pass;
			    }
			}
		}
	}

	public static void main(String[] args) throws Exception{
	    String filePath = "C:\\Anime\\temp\\cg_temp";
	    decompressFiles(filePath);
	    System.out.println("Process Finished");
	}
	


}
