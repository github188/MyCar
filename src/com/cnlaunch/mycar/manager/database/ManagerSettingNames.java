package com.cnlaunch.mycar.manager.database;

/**
 * @author xuzhuowei �û�������
 */
public enum ManagerSettingNames {
	budget,//Ԥ���� 
	oilType,// ��Ʒ����
	categoryGroup, // Ĭ��֧��������
	lastBackupDateTime, // �ϴα���ʱ��
	lastSyncDateTime, // �ϴ�ͬ��ʱ��
	lastExportDateTime, // �ϴε���ʱ��
	syncDownAccountTotalPage, // ��ͬ�����ص���Ŀ�ܷ�ҳ��
	syncDownAccountPageAchieved, // ͬ��������Ŀ���ѱ���ķ�ҳ��
	syncDownAccountPageFinished, //  ��Ŀͬ�����أ���һ����������ȡ�ܷ�ҳ�����Ĳ����Ƿ������
	syncDownAccountFinished, // ͬ��������Ŀ���ݣ��Ƿ������
	syncDownOilTotalPage, // ��ͬ�����صļ��������ܷ�ҳ��
	syncDownOilPageAchieved, // ͬ�����ؼ������ݵ��ѱ���ķ�ҳ��
	syncDownOilPageFinished, // ����ͬ�����أ���һ����������ȡ�ܷ�ҳ�����Ĳ����Ƿ������
	syncDownOilFinished, // ͬ�����ؼ������ݣ��Ƿ������
	syncDownManagerSettingFinished, // ͬ������������Ϣͬ�������
	syncDownCustomCategoryFinished, // ͬ�������Զ���֧������Ƿ������
	syncDownUserCarFinished, // ͬ�������û��������Ƿ������
	syncDownFinished, // ͬ�������������ݣ��Ƿ������
	oilPrice, //�ϴμ���ʱ����д���ͼ�
	lastOilType, //�ϴμ���ʱ����д����Ʒ
}
