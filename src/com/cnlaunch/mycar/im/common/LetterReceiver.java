package com.cnlaunch.mycar.im.common;

public abstract class LetterReceiver implements ILetterReceiver {
	protected IPostBox mPostBox;

	@Override
	public abstract int getId();

	@Override
	public IPostBox getPostBox() {
		return mPostBox;
	}

	@Override
	public void setPostBox(IPostBox postBox) {
		mPostBox = postBox;
	}

	@Override
	public abstract void dealLetter(ILetter letter);
}
