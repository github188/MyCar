package com.cnlaunch.mycar.diagnose.simplereport.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
/**
 * @author liaochuanhai
 *���ױ��� ʵ����
 */
public class DiagnoseSimpleReportInfo implements Serializable {

	private String  dataStreamId;//������ID
	private String  dataStreamName;//����������
	private String  dataStreamValue;//������ֵ
	private String  maxvalue;//���ֵ
	private String  minvalue;//��Сֵ
	private String  causeResult;//�����ĺ��
	private String  helpAdvice;//��������
	private List<HashMap<String, String>> simpleReportList; //�������б�
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
