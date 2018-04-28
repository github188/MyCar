package com.cnlaunch.mycar.diagnose.constant;

public class DiagnoseCounter {
	private static int counter = 0;

	public static int getCounter() {
		return counter;
	}

	public static void setCounter(int counter) {
		DiagnoseCounter.counter = counter;
	}
	public static void increase(){
		++counter;
	}
}
