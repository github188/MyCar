package com.cnlaunch.mycar.updatecenter.dao;
/**
 * 重复的对象异常，数据库中不能出现内容相同的对象
 * */
public class DuplicateObjectException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DuplicateObjectException(String message) {
        super(message);
    }
}
