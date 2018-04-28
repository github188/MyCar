package com.cnlaunch.mycar.im.common;

public interface ILetterReceiver {
    int getId();

    IPostBox getPostBox();
    
    void setPostBox(IPostBox postBox);

    void dealLetter(ILetter letter);
}
