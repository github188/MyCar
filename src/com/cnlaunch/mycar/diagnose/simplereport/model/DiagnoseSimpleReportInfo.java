package com.cnlaunch.mycar.diagnose.simplereport.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
/**
 * @author liaochuanhai
 *简易报告 实体类
 */
public class DiagnoseSimpleReportInfo implements Serializable {

	private String  dataStreamId;//数据流ID
	private String  dataStreamName;//数据流名称
	private String  dataStreamValue;//数据流值
	private String  maxvalue;//最大值
	private String  minvalue;//最小值
	private String  causeResult;//引发的后果
	private String  helpAdvice;//帮助建议
	private List<HashMap<String, String>> simpleReportList; //数据流列表
	public String getCauseResult() {
		return causeResult;
	}
	public void setCauseResult(String causeResult) {
		this.causeResult = causeResult;
	}
	public String getHelpAdvice() {
		return helpAdvice;
	}
	public void setHelpAdvice(String helpAdvice) {
		this.helpAdvice = helpAdvice;
	}
	public String getDataStreamName() {
		return dataStreamName;
	}
	public void setDataStreamName(String dataStreamName) {
		this.dataStreamName = dataStreamName;
	}
	public String getDataStreamValue() {
		return dataStreamValue;
	}
	public void setDataStreamValue(String dataStreamValue) {
		this.dataStreamValue = dataStreamValue;
	}
	public String getMaxvalue() {
		return maxvalue;
	}
	public void setMaxvalue(String maxvalue) {
		this.maxvalue = maxvalue;
	}
	public String getMinvalue() {
		return minvalue;
	}
	public void setMinvalue(String minvalue) {
		this.minvalue = minvalue;
	}
	public String getDataStreamId() {
		return dataStreamId;
	}
	public void setDataStreamId(String dataStreamId) {
		this.dataStreamId = dataStreamId;
	}
	
}
