package com.cnlaunch.mycar.updatecenter.dao;
/**
 * �ظ��Ķ����쳣�����ݿ��в��ܳ���������ͬ�Ķ���
 * */
public class DuplicateObjectException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DuplicateObjectException(String message) {
        super(message);
    }
}
