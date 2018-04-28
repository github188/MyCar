package com.cnlaunch.mycar.obd2.service;


interface IDiagnoseServiceRemote {
	String getValue(int dataFlowName);
	void prepareValue(in int[] dataFlowNames);
	//void registerObserver(in DataFlowObserver observer);
	//void unRegisterObserver(in DataFlowObserver observer);

}
