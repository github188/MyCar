package com.cnlaunch.mycar.common.webservice;

/**
 * Ϊ�˴����¼ʧЧ���⣬ÿ��WebService���صĽ������װ��WSBaseResult
 * ����ģ��ȡ����Ӧͷ��Ĵ���responseCode�����Ϊ-1��˵��δ��¼���ߵ�½ʧЧ�����Ϊ0��˵����Ӧ�ɹ�
 * ����WebService�Ĳ���˳�����£�
 * ��ѯMyCarActivity�ﾲ̬����isLogin��
 *      ���isLogin == true
 *          ʵ����WebServiceManager.java����
 *          ����execute()��������WebService����
 *          ���շ���ֵWSBaseResult��ȡ�������responseCode���ԣ�
 *          ���responseCode == 0��˵�������ͳɹ���
 *              ȡ��WSBaseResult�����object���������Ϊ����˷��صĶ���
 *          ���������responseCode == -1��˵����¼ʧЧ����ο�MyCarActivity�е�autoLogin()������¼
 *          �ٵ���WebServiceManager�����execute()��������WebService����
 *      ��������δ��¼��
 *          ��ο�MyCarActivity�е�autoLogin()������¼
 *          �ٵ���WebServiceManager�����execute()��������WebService����
 *       �����쳣�����responseCode == 2 ��ʾIO�����쳣
 *       responseCode == 3 ��ʾXml��������
 *       "501", "�û��������������"
 * 		"401", "��������Ϊ��"
 * 		"402", "��������Ϊ�ջ��߲������ʹ���"
 * 		"411", "ԭ�������"
 * 		"502", "�û�״̬����"
 * 		"503", "�û�δ�����������"
 * 		"511", "�û�������"
 * 		"512", "�û��ֻ��ѱ������û�ʹ��"
 * 		"521", "�û�δ��¼������ʧЧ"
 * 		"513", "�û������ѱ������û�ʹ��"
 * @author xiangyuanmao
 *
 */
public class WSBaseResult {

	public int responseCode; // ���ؽ������ -1��¼��ʱ��0�ɹ�
	public Object object; // ���ؽ������
	
	@Override
	public String toString()
	{
		return "WSBaseResult [responseCode=" + responseCode + ", object="
				+ object + "]";
	}
	
}
