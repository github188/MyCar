package com.cnlaunch.mycar.common.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.cnlaunch.mycar.R;

import android.content.Context;
import android.content.res.Resources;

public class Format {

	public static class DateStr {

		public static Date strToDate(String pattern, String dateStr)
				throws ParseException {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			return simpleDateFormat.parse(dateStr);
		}

		public static Date strToDate(String strDate) throws ParseException {
			SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
			return f.parse(strDate);
		}

		public static Date strToDate(String year, String month, String day) {
			StringBuilder sb = new StringBuilder();
			if (year.length() != 4) {
				return null;
			} else {
				sb.append(year);
			}

			if (month.length() == 1) {
				sb.append("0");
				sb.append(month);
			} else if (month.length() == 2) {
				sb.append(month);
			} else {
				return null;
			}

			if (day.length() == 1) {
				sb.append("0");
				sb.append(day);
			} else if (day.length() == 2) {
				sb.append(day);
			} else {
				return null;
			}

			try {
				return strToDate(sb.toString() + "000000");
			} catch (ParseException e) {
				return null;
			}
		}

		public static String getDay(Date date) {
			SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
			String dateStr = f.format(date);
			return dateStr.substring(6, 8);

		}

		public static String getDateTime(Date date) {
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return f.format(date);
		}

		public static String getDate(Date date) {
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
			return f.format(date);
		}

		public static String getMonthDayHourMinute(Date date) {
			SimpleDateFormat f = new SimpleDateFormat("MM-dd HH:mm");
			return f.format(date);

		}

		public static String getHourMinute(Date date) {
			SimpleDateFormat f = new SimpleDateFormat("HH:mm");
			return f.format(date);
		}

		public static String getMonth(Date date) {
			SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
			String dateStr = f.format(date);
			return dateStr.substring(4, 6);

		}

		public static String getYear(Date date) {
			SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
			String dateStr = f.format(date);
			return dateStr.substring(0, 4);

		}

		public static String getTime(Date date) {
			SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
			String dateStr = f.format(date);
			return dateStr.substring(8, 14);
		}

		public static String getDateTime() {
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return f.format(new Date());
		}

		public static String getYearMonthDayWeek(Context context, Date date) {
			Resources r = context.getResources();
			String[] weekDays = { r.getString(R.string.day0),
					r.getString(R.string.day1), r.getString(R.string.day2),
					r.getString(R.string.day3), r.getString(R.string.day4),
					r.getString(R.string.day5), r.getString(R.string.day6), };
			SimpleDateFormat f = new SimpleDateFormat(r.getString(R.string.datetime_mask));
			return f.format(date) + "  " + weekDays[date.getDay()];
		}

		public static String getMonthDayWeek(Context context) {
			return getYearMonthDayWeek(context, new Date());
		}

		public static String getDay() {
			return getDay(new Date());
		}

		public static String getMonth() {
			return getMonth(new Date());
		}

		public static String getYear() {
			return getYear(new Date());

		}

		public static String getTime() {
			return getTime(new Date());
		}

		public static String getDate() {
			return getDate(new Date());
		}

	}

	public static String doubleToCommercialString(double doubleNumber) {
		NumberFormat f = NumberFormat.getInstance();
		f.setMaximumFractionDigits(2);
		f.setMinimumFractionDigits(2);
		return f.format(doubleNumber);
	}

	public static String doubleToCommercialNoSeparatorString(double doubleNumber) {
		DecimalFormat f = new java.text.DecimalFormat("#0.00");
		return f.format(doubleNumber);
	}

	public static String doubleToCommercialNoSeparatorNoEndZeroString(
			double doubleNumber) {
		String str = doubleToCommercialNoSeparatorString(doubleNumber);
		while (str.charAt(str.length() - 1) == '0') {
			str = str.substring(0, str.length() - 1);
		}
		if (str.charAt(str.length() - 1) == '.') {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	/**
	 * @param size
	 *            文件大小，以B为单位
	 * @return
	 */
	public static String getFileSize(long size) {
		if (size == 0) {
			return "0K";
		}
		Float ret = new Float(size);
		String unit = "";

		if (size < 1024) {
			unit = "B";
		} else if (size < 1024 * 1024) {
			ret = (float) (size / 1024);// 以K为单位时，不保留小数
			unit = "K";
		} else {
			ret /= 1024 * 1024;
			unit = "M";
		}

		return floatToCommercialNoSeparatorNoEndZeroString(ret) + unit;
	}

	/**
	 * 将float转换成保留两位小数的字符串，每三位中间，无分隔符。并且当末尾是0时，去掉0。当小数部分全是0时，去掉小数点
	 * 
	 * @param floatNumber
	 * @return
	 */
	public static String floatToCommercialNoSeparatorNoEndZeroString(
			float floatNumber) {
		String str = floatToCommercialNoSeparatorString(floatNumber);
		while (str.charAt(str.length() - 1) == '0') {
			str = str.substring(0, str.length() - 1);
		}
		if (str.charAt(str.length() - 1) == '.') {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	/**
	 * 将float转换成保留两位小数的字符串，每三位中间，无分隔符
	 * 
	 * @param floatNumber
	 * @return
	 */
	public static String floatToCommercialNoSeparatorString(float floatNumber) {
		DecimalFormat f = new java.text.DecimalFormat("#.00");
		return f.format(floatNumber);
	}

}
