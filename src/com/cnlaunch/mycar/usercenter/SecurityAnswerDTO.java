package com.cnlaunch.mycar.usercenter;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

/**
 * 
 * @author xiangyuanmao
 *
 */
public class SecurityAnswerDTO {
	private String answer;
	private Integer questionId;
	public SecurityAnswerDTO(Integer questionId,String answer){
		this.questionId = questionId;
		this.answer = answer;
	}
//	@Override
//	public Object getProperty(int arg0) {
//		// TODO Auto-generated method stub
//		Object res = null;
//		switch (arg0) {
//		case 0:
//			res = this.answer;
//			break;
//		case 1:
//			res = this.questionId;
//			break;
//		default:
//			break;
//		}
//		return res;
//	}
//	@Override
//	public int getPropertyCount() {
//		// TODO Auto-generated method stub
//		return 2;
//	}
//	@Override
//	public void getPropertyInfo(int arg0, Hashtable arg1, PropertyInfo arg2) {
//		// TODO Auto-generated method stub
//		switch (arg0) {
//		case 0:
//			arg2.type = PropertyInfo.STRING_CLASS;
//			arg2.name = "answer";
//			break;
//		case 1:
//			arg2.type = PropertyInfo.INTEGER_CLASS;
//			arg2.name = "questionId";
//			break;
//
//		default:
//			break;
//		}
//		
//	}
//	@Override
//	public void setProperty(int arg0, Object arg1) {
//		// TODO Auto-generated method stub
//		if(arg1==null)return;
//		switch (arg0) {
//		case 0:
//			this.answer = arg1.toString();
//			break;
//		case 1:
//			this.questionId = new Integer(arg1.toString());
//			break;
//
//		default:
//			break;
//		}
//		
//	}
	public String toString(){
		StringBuffer stringBuffer = new StringBuffer();
		if (answer != null)
		{
			stringBuffer.append(answer);	
		}
		if (questionId != null)
		{
			stringBuffer.append(questionId);			
		}
		return stringBuffer.toString();
	}
	public Integer getQuestionId() {
		return questionId;
	}
	public void setQuestionId(Integer questionId) {
		this.questionId = questionId;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}

}
