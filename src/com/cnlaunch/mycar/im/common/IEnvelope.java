package com.cnlaunch.mycar.im.common;

public interface IEnvelope {

    ILetter getLetter();
    IEnvelope getNext();
    int getCategory();
    int getSource();
    
    void setLetter(ILetter letter);
    void setNext(IEnvelope envelope);
    void setCategory(int category);
    void setSource(int source);
    
    byte[] toBinary();
}
