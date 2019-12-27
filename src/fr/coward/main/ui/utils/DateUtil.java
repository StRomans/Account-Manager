package fr.coward.main.ui.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DateUtil {

	public static LocalDate date2LocalDate(Date date) {
		return (null == date) ? null : date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}
	
	public static Date localDate2Date(LocalDate date) {
		return (null == date) ? null : Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}
}
