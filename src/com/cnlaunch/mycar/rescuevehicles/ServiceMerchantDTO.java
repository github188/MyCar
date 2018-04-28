package com.cnlaunch.mycar.rescuevehicles;
/**
 *@author zhangweiwei
 *@version 2011-12-14下午2:58:53
 *类说明
 */
public class ServiceMerchantDTO {
	String auditFlag = null;
	String companyName;
	String companyAddress ;
	String contacter ;
    String createTime ;
    String gprsX ;
    String gprsY ;
	String mobile ;
	String phone ;
	String primaryService ;
	String serviceId = null;
	public String getAuditFlag() {
		return auditFlag;
	}
	public void setAuditFlag(String auditFlag) {
		this.auditFlag = auditFlag;
	}
	public String getCompanyAddress() {
		return companyAddress;
	}
	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
	}
	public String getContacter() {
		return contacter;
	}
	public void setContacter(String contacter) {
		this.contacter = contacter;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getGprsX() {
		return gprsX;
	}
	public void setGprsX(String gprsX) {
		this.gprsX = gprsX;
	}
	public String getGprsY() {
		return gprsY;
	}
	public void setGprsY(String gprsY) {
		this.gprsY = gprsY;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPrimaryService() {
		return primaryService;
	}
	public void setPrimaryService(String primaryService) {
		this.primaryService = primaryService;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
}