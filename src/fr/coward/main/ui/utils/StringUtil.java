package fr.coward.main.ui.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StringUtil {
	//"EEE MMM dd HH:mm:ss z yyyy"
	public static String DB_DATE_PATTERN = "yyyy-MM-dd";

	public static boolean isNotNullNotEmpty(String strValue){
		return (null != strValue) && !strValue.isEmpty();
	}
	
	public static Date toDate(String dateStr) throws ParseException{
		DateFormat formatter = new SimpleDateFormat(DB_DATE_PATTERN, Locale.ENGLISH);
		return formatter.parse(dateStr);
	}
	
	public static String getFormattedDate(Date date, String pattern){
		DateFormat formatter = new SimpleDateFormat(pattern);
		
		return formatter.format(date);
	}

	public static boolean isNullOrEmpty(String label) {
		return !isNotNullNotEmpty(label);
	}
}
