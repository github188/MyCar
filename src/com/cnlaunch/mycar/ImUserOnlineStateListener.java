package com.cnlaunch.mycar;

import com.cnlaunch.mycar.im.model.ImSession;

public class ImUserOnlineStateListener implements IUserOnlineState {

	@Override
	public void loginSuccess() {
		// ÔÝÎÞ¶¯×÷
		
	}

	@Override
	public void logout() {
		ImSession.cleanSession();
	}

}
