package com.dongwoo.SQM.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DateUtils {

	//현재날짜 구하기
	public static String getToday(){
		return getToday("");
	}

	//현재날짜 구하기
	public static String getToday(String sep){
		Calendar cal = Calendar.getInstance();
		String yyyy = Integer.toString(cal.get(Calendar.YEAR));
		String mm = Integer.toString(cal.get(Calendar.MONTH)+1);
		String dd = Integer.toString(cal.get(Calendar.DATE));

		if (mm.length() == 1) {
			mm = "0" + mm;
		}

		if (dd.length() == 1) {
			dd = "0" + dd;
		}

		String date = yyyy + sep + mm + sep + dd;
		return date;
	}

	//현재날짜 구하기
	public static String getTodayAll(){
		Calendar cal = Calendar.getInstance();
		String yyyy = Integer.toString(cal.get(Calendar.YEAR));
		String mm = Integer.toString(cal.get(Calendar.MONTH)+1);
		String dd = Integer.toString(cal.get(Calendar.DATE));
		String hh = Integer.toString(cal.get(Calendar.HOUR));
		String MM = Integer.toString(cal.get(Calendar.MINUTE));
		String ss = Integer.toString(cal.get(Calendar.SECOND));
		String mi = Integer.toString(cal.get(Calendar.MILLISECOND));


		if (mm.length() == 1) {
			mm = "0" + mm;
		}

		if (dd.length() == 1) {
			dd = "0" + dd;
		}

		if (hh.length() == 1) {
			hh = "0" + hh;
		}

		if (MM.length() == 1) {
			MM = "0" + MM;
		}

		if (ss.length() == 1) {
			ss = "0" + ss;
		}

		String date = yyyy + mm + dd + hh + MM + ss + mi;
		return date;
	}

	//현재월 구하기
	public static String getMonth(){
		Calendar cal = Calendar.getInstance();
		String yyyy = Integer.toString(cal.get(Calendar.YEAR));
		String mm = Integer.toString(cal.get(Calendar.MONTH)+1);

		if (mm.length() == 1) {
			mm = "0" + mm;
		}

		String date = yyyy + mm;
		return date;
	}

	//4자리 년도 반환
	public static String getFullYear(){
		Calendar cal = Calendar.getInstance();
		return Integer.toString(cal.get(Calendar.YEAR));
	}

	//날짜에서 월을 더하거나 뺌
	public static String addMonth(String yearMonth, int val) {
		int year = Integer.parseInt(yearMonth.substring(0, 4));
		int month = Integer.parseInt(yearMonth.substring(4, 6));

		GregorianCalendar gc = new GregorianCalendar(year, month, 1);
		gc.add(Calendar.MONTH, val - 1);

		return setDateFormat(gc, "yyyyMM");
	}

	public static String setDateFormat(GregorianCalendar gc, String fm) {
		if (fm == null) {
			return null;
		}

		SimpleDateFormat sdf = new SimpleDateFormat(fm);
		return sdf.format(gc.getTime());
	}

	//오늘날짜에서 더하거나 뺀 날짜
	public static String addDate(String ymd, int val){
		String upperYmd = ymd.toUpperCase();

		GregorianCalendar ggaCal = new GregorianCalendar();
		if("Y".equals(upperYmd)) {
			ggaCal.add( Calendar.YEAR, val);
		} else if("M".equals(upperYmd)) {
			ggaCal.add( Calendar.MONTH, val);
		} else if("D".equals(upperYmd)) {
			ggaCal.add( Calendar.DATE, val);
		}

		int y = ggaCal.get(Calendar.YEAR);
		int m = ggaCal.get(Calendar.MONTH) + 1;
		int d= ggaCal.get(Calendar.DATE);

		String strM = m + "";
		String strD = d + "";

		if(m < 10) {
			strM = "0" + m;
		}
		if(d < 10) {
			strD = "0" + d;
		}

		return y + strM + strD;
	}

	public static String getTime() {
		String time = getTime("-");
		return time;
	}

	public static String getTime(String shape) {
		Calendar cal = Calendar.getInstance();
		String hour = Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
		String min = Integer.toString(cal.get(Calendar.MINUTE));
		String sec = Integer.toString(cal.get(Calendar.SECOND));

		if (min.length() == 1) {
			min = "0" + min;
		}
		if (sec.length() == 1) {
			sec = "0" + sec;
		}

		String time = hour + shape + min + shape + sec;
		return time;

	}

	//문자열날짜에 구분자 삽입
	public static String addDateSeperator(String date, String sep){
		if(date == null || "".equals(date) || date.length() != 8) {
			return date;
		}

		String y = date.substring(0, 4);
		String m = date.substring(4, 6);
		String d = date.substring(6, 8);

		return y + sep + m + sep + d;
	}

	public static int getDestDate(String timeUnit, Date targetDate, int period) {
		@SuppressWarnings("deprecation")
		GregorianCalendar ggaCal = new GregorianCalendar(targetDate.getYear(), targetDate.getMonth(), targetDate.getDay(), targetDate.getHours(), targetDate.getMinutes(), targetDate.getSeconds());
		int result = 0;

		if(timeUnit.equals("SEC")) {
			ggaCal.add(Calendar.SECOND, period);
		} else if(timeUnit.equals("MIN")) {
			ggaCal.add(Calendar.MINUTE, period);
		} else if(timeUnit.equals("HOUR")) {
			ggaCal.add(Calendar.HOUR, period);
		} else if(timeUnit.equals("DAY")) {
			ggaCal.add(Calendar.DATE, period);
		} else if(timeUnit.equals("WEEK")){
			ggaCal.add(Calendar.DATE, period * 7);
		} else if(timeUnit.equals("MONTH")){
			ggaCal.add(Calendar.MONTH, period);
		} else if(timeUnit.equals("YEAR")){
			ggaCal.add(Calendar.YEAR, period);
		}

		Date target = new Date(ggaCal.getTimeInMillis());
		Date now = new Date(0);
		long diff = target.getTime() - now.getTime();

		if(diff > 0) {
			result = 1;
		}

		return result;
	}

	public static String changeDateFormat(Date fromDate, String changeFormat) {
		String resultDate = "";

		SimpleDateFormat transFormat = new SimpleDateFormat(changeFormat);
		try {
			resultDate = transFormat.format(fromDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultDate;
	}


	public static Date changeStr2DateFormat(String fromDate, String changeFormat) {
		Date resultDate = null;
		SimpleDateFormat transFormat = new SimpleDateFormat(changeFormat ,Locale.KOREAN);//Locale.CHINESE
		try {
			resultDate = transFormat.parse(fromDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return resultDate;
	}

	public static boolean isDate(String checkStr) {
		if (null == checkStr) return false;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//Locale.CHINESE
		try {
			String checkDate = sdf.format(sdf.parse(checkStr));
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	public static String parseDate(String tgStr) {
		return parseDate(tgStr, "yyyy-MM-dd");
	}

	public static String parseDate(String tgStr, String format) {
		if (null == tgStr) return "";

		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			return sdf.format(sdf.parse(tgStr));
		} catch (ParseException e) {
			return "";
		}
	}

}