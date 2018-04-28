package com.cnlaunch.mycar.manager.database;

import android.content.Context;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.utils.Format;

/**
 * @author xuzhuowei 按日期统计Model层
 */
public class StatisticsByDate {

	private double totalAmount;
	private String year;
	private String month;
	private String day;
	private StatisticsType type;
	private Context context;

	public StatisticsByDate() {
	}

	public StatisticsByDate(Float totalAmount, String year, String month,
			String day, StatisticsType type) {
		this.totalAmount = totalAmount;
		this.year = year;
		this.month = month;
		this.day = day;
		this.type = type;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public String getTotalAmountStr(Context context) {

		return com.cnlaunch.mycar.common.utils.StringUtil.getCurrency(context)
				+ Format.doubleToCommercialString(getTotalAmount());
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public String getMonthStr(Context context) {
		String ret = null;
		if (month == null) {
			return null;
		}

		switch (Integer.parseInt(month)) {
		case 1:
			ret = context.getString(R.string.month01);
			break;
		case 2:
			ret = context.getString(R.string.month02);
			break;
		case 3:
			ret = context.getString(R.string.month03);
			break;
		case 4:
			ret = context.getString(R.string.month04);
			break;
		case 5:
			ret = context.getString(R.string.month05);
			break;
		case 6:
			ret = context.getString(R.string.month06);
			break;
		case 7:
			ret = context.getString(R.string.month07);
			break;
		case 8:
			ret = context.getString(R.string.month08);
			break;
		case 9:
			ret = context.getString(R.string.month09);
			break;
		case 10:
			ret = context.getString(R.string.month10);
			break;
		case 11:
			ret = context.getString(R.string.month11);
			break;
		case 12:
			ret = context.getString(R.string.month12);
			break;

		default:
			break;
		}
		return ret;
	}

	public String getYearMonth() {
		return year + "-" + month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getDay() {
		return day;
	}

	public String getDayStr(Context context) {
		String ret = null;
		if (day == null) {
			return null;
		}
		switch (Integer.parseInt(day)) {
		case 1:
			ret = context.getString(R.string.day01);
			break;
		case 2:
			ret = context.getString(R.string.day02);
			break;
		case 3:
			ret = context.getString(R.string.day03);
			break;
		case 4:
			ret = context.getString(R.string.day04);
			break;
		case 5:
			ret = context.getString(R.string.day05);
			break;
		case 6:
			ret = context.getString(R.string.day06);
			break;
		case 7:
			ret = context.getString(R.string.day07);
			break;
		case 8:
			ret = context.getString(R.string.day08);
			break;
		case 9:
			ret = context.getString(R.string.day09);
			break;
		case 10:
			ret = context.getString(R.string.day10);
			break;
		case 11:
			ret = context.getString(R.string.day11);
			break;
		case 12:
			ret = context.getString(R.string.day12);
			break;
		case 13:
			ret = context.getString(R.string.day13);
			break;
		case 14:
			ret = context.getString(R.string.day14);
			break;
		case 15:
			ret = context.getString(R.string.day15);
			break;
		case 16:
			ret = context.getString(R.string.day16);
			break;
		case 17:
			ret = context.getString(R.string.day17);
			break;
		case 18:
			ret = context.getString(R.string.day18);
			break;
		case 19:
			ret = context.getString(R.string.day19);
			break;
		case 20:
			ret = context.getString(R.string.day20);
			break;
		case 21:
			ret = context.getString(R.string.day21);
			break;
		case 22:
			ret = context.getString(R.string.day22);
			break;
		case 23:
			ret = context.getString(R.string.day23);
			break;
		case 24:
			ret = context.getString(R.string.day24);
			break;
		case 25:
			ret = context.getString(R.string.day25);
			break;
		case 26:
			ret = context.getString(R.string.day26);
			break;
		case 27:
			ret = context.getString(R.string.day27);
			break;
		case 28:
			ret = context.getString(R.string.day28);
			break;
		case 29:
			ret = context.getString(R.string.day29);
			break;
		case 30:
			ret = context.getString(R.string.day30);
			break;
		case 31:
			ret = context.getString(R.string.day31);
			break;

		default:
			break;
		}
		return ret;
	}

	public String getYearMonthDay() {
		return year + "-" + month + "-" + day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public StatisticsType getType() {
		return type;
	}

	public void setType(StatisticsType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "StatisticsModel [type=" + type + ", totalAmount = "
				+ totalAmount + ", year=" + year + ", month=" + month
				+ ", day=" + day + "]";
	}
}
