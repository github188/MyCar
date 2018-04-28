package com.cnlaunch.mycar.common.webservice;

/**
 * Webservice服务端响应对象的超类
 * 说明: 
 * 1> code 返回码
 * 2> message 消息
 * 3> 0请求成功
 * 4> 400表示客户端输入参数有问题
 * 5> 500 表示服务器异常(如数据库异常)
 * 6> 其它返回码由各自模块扩展(定义好后，务必通知相关人员)
 * 7> 所有有返回值的接口，都继承自WSResult
 * 8> 所有子WsResult类型都定义在http://www.x431.com namespace空间中
 * 9> 所有的complexType都定义在 http://www.x431.com namespace空间中
 * 10> 默认日期格式显示字符串如:2011-11-21T21:10:06+08:00 可以提供时间格式: 
 * @author xiangyuanmao
 *
 */
public class WSResult {
 public int code; // 错误码
 public String message; // 出错信息
}
