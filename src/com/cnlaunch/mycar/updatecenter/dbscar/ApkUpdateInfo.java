package com.cnlaunch.mycar.updatecenter.dbscar;
/**
 * 客户端升级描述信息
 * **/
public class ApkUpdateInfo {
	int detailId;
	int flag;
	String versionNumber;
	String updateDescription;

	public int getDetailId() {
		return detailId;
	}

	public void setDetailId(int aDetailId) {
		detailId = aDetailId;
	}

	public int getFlag() {
		return flag;
	}
	public void setFlag(int aFlag) {
		flag = aFlag;
	}

	public String getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(String aVersionNumber) {
		versionNumber = aVersionNumber;
	}
	public String getUpdateDescription() {
		return updateDescription;
	}
	public void setUpdateDescription(String aUpdateDescription) {
		updateDescription = aUpdateDescription;
	}

	public boolean forceUpdate() {
		return flag == 2;
	}

	public boolean optionalUpdate() {
		return flag == 1;
	}

	public boolean isLatestVersion() {
		return flag == 0;
	}

	@Override
	public String toString() {
		return "DBSCarUpdateInfo [detailId=" + detailId + ", flag=" + flag
				+ ", versionNumber=" + versionNumber + ", updateDescription="
				+ updateDescription + "]";
	}
}
