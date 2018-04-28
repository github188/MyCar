package com.cnlaunch.mycar.im.common;

public interface IPostBox {
   
	String getId();
	
    void send(IEnvelope envelope);

    void dispatchEnvelope();

    void regiestReceiver(ILetterReceiver letterReceiver);

    void unRegiestReceiver(ILetterReceiver letterReceiver);

    void open();

    void close();
}
