package fr.coward.main.model.utils;

import fr.coward.main.ui.utils.StringUtil;

public class FileUtils {

	public static String getExtension(String fileName){
		
		if(StringUtil.isNotNullNotEmpty(fileName)){
			String[] tokens = fileName.split("\\.(?=[^\\.]+$)");
			if(tokens.length == 2){
				return tokens[1];
			}
		}
		
		return "";
	}
	
	public static String getBase(String fileName){
		
		if(StringUtil.isNotNullNotEmpty(fileName)){
			String[] tokens = fileName.split("\\.(?=[^\\.]+$)");
			if(tokens.length > 0){
				return tokens[0];
			}
		}
		
		return "";
	}
	
}
