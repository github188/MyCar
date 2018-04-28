package com.cnlaunch.mycar.manager.database;

import com.cnlaunch.mycar.common.utils.Format;


/**
 * @author xuzhuowei
 *按类别统计Model层
 */
public class StatisticsByCategory {

	private Double totalAmount;
	private String category;
	private String year;
	private String month;
	private StatisticsType type;
	private Integer frequency;
	private String categoryId;
	
	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public StatisticsByCategory(){}

	public StatisticsByCategory(Double totalAmount,String year, String month, String category, StatisticsType type, Integer frequency)
	{
		this.totalAmount = totalAmount;
		this.year = year;
		this.month = month;
		this.category = category;
		this.type = type;
		this.frequency = frequency;
	}

	public Integer getFrequency() {
		return frequency;
	}

	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}
	
	public String getTotalAmountStr() {
	
		return Format.doubleToCommercialString(getTotalAmount());
	}
	
	public void setTotalAmount(Double totalAmount) {
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

	public String getYearMonth() {
		return year+"-"+month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getCategory() {
		return category;
	}
	

	public void setCategory(String day) {
		this.category = day;
	}

	public StatisticsType getType() {
		return type;
	}

	public void setType(StatisticsType type) {
		this.type = type;
	}
	

	@Override
	public String toString()
	{
		return "StatisticsModel [type=" + type + ", totalAmount = " + totalAmount + ", year=" + year+ ", month=" + month+ ", category=" + category +  ", frequency=" + frequency + "]";
	}
}
