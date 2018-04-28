package com.cnlaunch.mycar.jni;

import java.io.File;

import android.util.Log;
import junit.framework.TestCase;

/**********************************************************
 * JniX431File�Ĳ����࣬Ҳ����ΪJniX431File���ʹ�òο�
 * 
 * @author ��ID:2860��
 * 
 * @version
 * 
 * @since
 **********************************************************/
public class JniX431FileTest extends TestCase {

	// ��־��¼��TAG
	private static final String TAG = "JniX431FileTest";

	// JniX431File�����ڲ��������乲��
	private static JniX431File mJxf = new JniX431File();

	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}

	

	/* ����������ʾ�� */
	private static final int DS_TYPE_1 = 0x0100;
	private static final int DS_TYPE_2 = 0x0101;
	private static final int DS_TYPE_3 = 0x0200;
	private static final int DS_TYPE_4 = 0x0203;
	private static final int DS_TYPE_5 = 0x0205;
	private static final int DS_TYPE_6 = 0x0300;
	private static final int DS_TYPE_7 = 0x0301;
	private static final int DS_TYPE_8 = 0x0313;
	private static final int DS_TYPE_9 = 0x0303;

	/* ��������������lsx_write_userinfo */
	boolean Write_Userinfo(int file) {
		LSX_USERINFO userinfo = new LSX_USERINFO();
		userinfo.name = "customer A";
		userinfo.phone = "77777777";
		userinfo.license = "123456";
		return mJxf.lsx_write_userinfo(file, userinfo) == 0;
	}

	/* ��������������lsx_write_baseinfo */
	boolean Write_Baseinfo(int file) {
		// createtimeû�и�ֵ��
		LSX_BASEINFO baseinfo = new LSX_BASEINFO();
		baseinfo.productid = JniX431File.PRODUCT_CRECORDER;
		baseinfo.codepage = 936;
		baseinfo.langname = "Chinese Simplified";
		baseinfo.langcode = "cn";
		baseinfo.langcode_en = "en";
		baseinfo.diagversion = "Audi V13.00";
		baseinfo.serialno = "980241111111";
		// baseinfo.creationtime = "";

		return mJxf.lsx_write_baseinfo(file, baseinfo) == 0;
	}

	/* ��������������lsx_write_spinfo */
	boolean Write_Spinfo(int file) {
		LSX_SPINFO spinfo = new LSX_SPINFO();
		spinfo.name = "������A";
		spinfo.phone = "88888888";
		return mJxf.lsx_write_spinfo(file, spinfo) == 0;
	}

	/* ��������������lsx_write_autoinfo */
	boolean Write_Autoinfo(int file) {
		LSX_AUTOINFO autoinfo = new LSX_AUTOINFO();
		autoinfo.model = "�µ�";
		autoinfo.make = "Volkswagen";
		autoinfo.year = "2006";
		autoinfo.madein = "China";
		autoinfo.chassis = "chassis";
		autoinfo.enginemodel = "engine model";
		autoinfo.displacement = "2.0L";
		autoinfo.vin = "12345678901234567";
		return mJxf.lsx_write_autoinfo(file, autoinfo) == 0;
	}

	/* ��������������lsx_write_baseinfo */
	boolean Write_Baseinfo_langexttest(int file, String langcode,
			String langname) {
		LSX_BASEINFO baseinfo = new LSX_BASEINFO();
		baseinfo.productid = JniX431File.PRODUCT_CRECORDER;
		baseinfo.codepage = (short) 51932;
		baseinfo.langname = langname;
		baseinfo.langcode = langcode;
		baseinfo.langcode_en = "en";
		baseinfo.diagversion = "Audi V13.00";
		baseinfo.serialno = "980241111111";

		return mJxf.lsx_write_baseinfo(file, baseinfo) == 0;
	}

	/* ��������������lsx_write_baseinfo */
	boolean Write_Baseinfo_itemtest(int file) {
		LSX_BASEINFO baseinfo = new LSX_BASEINFO();
		baseinfo.productid = JniX431File.PRODUCT_CRECORDER;
		baseinfo.codepage = 936;
		baseinfo.langname = "Chinese Simplified";
		baseinfo.langcode = "cn";
		baseinfo.langcode_en = "en";
		baseinfo.diagversion = "Audi V13.00";
		baseinfo.serialno = "980241111111";

		return mJxf.lsx_write_baseinfo(file, baseinfo) == 0;
	}

	/* ��������������lsx_rec_writereadiness */
	boolean Write_Rdn(int grp) {
		LSX_STRING namestrs[] = new LSX_STRING[10];
		LSX_STRING textstrs[] = new LSX_STRING[10];
		for (int i = 0; i < 10; i++) {
			namestrs[i] = new LSX_STRING();
			textstrs[i] = new LSX_STRING();
		}

		int n = 7;
		namestrs[0].str = "RDN ����1";
		namestrs[0].str_en = "rdn name1";
		namestrs[1].str = "RDN ����2";
		namestrs[1].str_en = "rdn name2";
		namestrs[2].str = "RDN ����3";
		namestrs[2].str_en = "rdn name3";
		namestrs[3].str = "RDN ����4";
		namestrs[3].str_en = "rdn name4";
		namestrs[4].str = "RDN ����5";
		namestrs[4].str_en = "rdn name5";
		namestrs[5].str = "RDN ����6";
		namestrs[5].str_en = "rdn name6";
		namestrs[6].str = "RDN ����7";
		namestrs[6].str_en = "rdn name7";

		textstrs[0].str = "RDN ��ֵ1";
		textstrs[0].str_en = "rdn item1";
		textstrs[1].str = "RDN ��ֵ2";
		textstrs[1].str_en = "rdn item2";
		textstrs[2].str = "RDN ��ֵ3";
		textstrs[2].str_en = "rdn item3";
		textstrs[3].str = "RDN ��ֵ4";
		textstrs[3].str_en = "rdn item4";
		textstrs[4].str = "RDN ��ֵ5";
		textstrs[4].str_en = "rdn item5";
		textstrs[5].str = "RDN ��ֵ6";
		textstrs[5].str_en = "rdn item6";
		textstrs[6].str = "RDN ��ֵ7";
		textstrs[6].str_en = "rdn item7";

		return (mJxf.lsx_rec_writereadiness(grp, namestrs, textstrs, n) == 0);
	}

	/* ��������������lsx_rec_writedtc */
	boolean Write_Dtcs(int grp, String dtc) {
		LSX_STRING state = new LSX_STRING();
		LSX_STRING desc = new LSX_STRING();
		String dtctime = new String();

		state.str = dtc + " ״̬";
		state.str_en = dtc + " state";
		desc.str = dtc + " ����";
		desc.str_en = dtc + " desc";
		dtctime = dtc + " time";

		return (mJxf.lsx_rec_writedtc(grp, dtc, state, desc, dtctime) == 0);
	}

	/* ��������������lsx_rec_writevi */
	boolean Write_Vi(int grp) {
		LSX_STRING vi = new LSX_STRING();

		String vi_str = "�汾��Ϣ1\n�汾��Ϣ2";
		String vi_str_en = "vi info1\nvi info2";
		vi.str = vi_str;
		vi.str_en = vi_str_en;

		return (mJxf.lsx_rec_writevi(grp, vi) == 0);
	}

	/* ��������������lsx_rec_writedsbasics */
	boolean Write_DsBasics(int grp) {
		LSX_STRING namestrs[] = new LSX_STRING[10];
		LSX_STRING unitstrs[] = new LSX_STRING[10];
		for (int i = 0; i < 10; i++) {
			namestrs[i] = new LSX_STRING();
			unitstrs[i] = new LSX_STRING();
		}

		int type[] = new int[10];

		namestrs[0].str = "������ ����1";
		namestrs[0].str_en = "ds name1";
		namestrs[1].str = "������ ����2";
		namestrs[1].str_en = "ds name2";
		namestrs[2].str = "������ ����3";
		namestrs[2].str_en = "ds name3";
		namestrs[3].str = "������ ����4";
		namestrs[3].str_en = "ds name4";
		namestrs[4].str = "������ ����5";
		namestrs[4].str_en = "ds name5";
		namestrs[5].str = "������ ����6";
		namestrs[5].str_en = "ds name6";
		namestrs[6].str = "������ ����7";
		namestrs[6].str_en = "ds name7";
		namestrs[7].str = "������ ����8";
		namestrs[7].str_en = "ds name8";
		namestrs[8].str = "������ ����9";
		namestrs[8].str_en = "ds name9";

		unitstrs[0].str = "������ ��λ1";
		unitstrs[0].str_en = "ds unit1";
		unitstrs[1].str = "������ ��λ2";
		unitstrs[1].str_en = "ds unit2";
		unitstrs[2].str = "������ ��λ3";
		unitstrs[2].str_en = "ds unit3";
		unitstrs[3].str = "������ ��λ4";
		unitstrs[3].str_en = "ds unit4";
		unitstrs[4].str = "������ ��λ5";
		unitstrs[4].str_en = "ds unit5";
		unitstrs[5].str = "������ ��λ6";
		unitstrs[5].str_en = "ds unit6";
		unitstrs[6].str = "������ ��λ7";
		unitstrs[6].str_en = "ds unit7";
		unitstrs[7].str = "������ ��λ8";
		unitstrs[7].str_en = "ds unit8";
		unitstrs[8].str = "������ ��λ9";
		unitstrs[8].str_en = "ds unit9";

		type[0] = DS_TYPE_1;
		type[2] = DS_TYPE_3;
		type[4] = DS_TYPE_5;
		type[5] = DS_TYPE_6;
		type[7] = DS_TYPE_8;

		int n = 9;
		return (mJxf.lsx_rec_writedsbasics(grp, namestrs, unitstrs, type, n) == 0);
	}

	/* ��������������lsx_rec_writeds */
	boolean Write_Ds(int grp, int startlineno, int linecount) {
		LSX_STRING itemstrs[] = new LSX_STRING[10];
		int i;

		for (i = 0; i < 10; ++i) {
			itemstrs[i] = new LSX_STRING();
		}

		int cols = 9;
		int count = 0;
		while (++count <= linecount) {
			for (i = 0; i < cols * 2; i += 2) {
				itemstrs[i / 2].str = ("������ ��ֵ" + startlineno) + ","
						+ (i / 2 + 1);
				itemstrs[i / 2].str_en = ("ds item" + startlineno) + ","
						+ (i / 2 + 1);
			}

			++startlineno;
			if (mJxf.lsx_rec_writeds(grp, itemstrs, cols) != 0) {
				break;
			}
		}

		return (count == linecount + 1);
	}

	/* ��������������lsx_rec_writefreezeframe */
	boolean Write_FF(int grp, String dtc) {
		LSX_STRING namestrs[] = new LSX_STRING[5];
		LSX_STRING unitstrs[] = new LSX_STRING[5];
		LSX_STRING textstrs[] = new LSX_STRING[5];
		int type[] = new int[5];
		int i;

		for (i = 0; i < 5; ++i) {
			namestrs[i] = new LSX_STRING();
			unitstrs[i] = new LSX_STRING();
			textstrs[i] = new LSX_STRING();
		}

		namestrs[0].str = "FF ����1";
		namestrs[0].str_en = "ff name1";
		namestrs[1].str = "FF ����2";
		namestrs[1].str_en = "ff name2";
		namestrs[2].str = "FF ����3";
		namestrs[2].str_en = "ff name3";
		namestrs[3].str = "FF ����4";
		namestrs[3].str_en = "ff name4";
		namestrs[4].str = "FF ����5";
		namestrs[4].str_en = "ff name5";

		unitstrs[0].str = "FF ��λ1";
		unitstrs[0].str_en = "ff unit1";
		unitstrs[1].str = "FF ��λ2";
		unitstrs[1].str_en = "ff unit2";
		unitstrs[2].str = "FF ��λ3";
		unitstrs[2].str_en = "ff unit3";
		unitstrs[3].str = "FF ��λ4";
		unitstrs[3].str_en = "ff unit4";
		unitstrs[4].str = "FF ��λ5";
		unitstrs[4].str_en = "ff unit5";

		type[0] = DS_TYPE_1;
		type[2] = DS_TYPE_3;
		type[4] = DS_TYPE_5;

		textstrs[0].str = "FF ��ֵ1";
		textstrs[0].str_en = "ff item1";
		textstrs[1].str = "FF ��ֵ2";
		textstrs[1].str_en = "ff item2";
		textstrs[2].str = "FF ��ֵ3";
		textstrs[2].str_en = "ff item3";
		textstrs[3].str = "FF ��ֵ4";
		textstrs[3].str_en = "ff item4";
		textstrs[4].str = "FF ��ֵ5";
		textstrs[4].str_en = "ff item5";

		return (mJxf.lsx_rec_writefreezeframe(grp, dtc, namestrs, unitstrs,
				type, textstrs, 5) == 0);
	}

	/*
	 * ������������lsx_init��lsx_open��lsx_close��lsx_deinit�ĸ�������
	 * 
	 * �ο�C/C++���Թ�����������TEST (V2FileWriteTest)
	 */
	public void testlsx_open_lsx_close() throws Throwable {

		// ��ʼ��
		int hlsx = mJxf.lsx_init();
		assertNotNull(hlsx);

		String strFileName = FileUtils.sdCardGetDirectoryPath()
				+ File.separator + "v2.x431";

		// ȷ���ļ�����
		boolean bExist = FileUtils.isFileExist(strFileName);
		assertTrue(bExist);

		X431String filename = new X431String(strFileName);
		X431Integer error = new X431Integer(10);

		// ���ļ�
		int lsx_file = mJxf.lsx_open(hlsx, filename, JniX431File.MODE_WRITE,
				error);
		assertTrue(lsx_file == 0);
		assertTrue(error.mValue == JniX431File.LSX_ERR_LOW_FILEVERSION);

		// String str1 = "lsx_open returned: ";
		// str1 += iRet;
		// Log.i(TAG, str1);

		// �ر��ļ�
		mJxf.lsx_close(lsx_file);

		// �ͷ���Դ
		mJxf.lsx_deinit(hlsx);
	}

	/*
	 * ������������lsx_checkfile������
	 * 
	 * �ο�C/C++���Թ�����������TEST (V2FileCheckTest)
	 */
	public void testlsx_checkfile() throws Throwable {

		String strFileName = FileUtils.sdCardGetDirectoryPath()
				+ File.separator + "v2.x431";

		// ȷ���ļ�����
		boolean bExist = FileUtils.isFileExist(strFileName);
		assertTrue(bExist);

		X431String filepath = new X431String(strFileName);

		int iRet = mJxf.lsx_checkfile(filepath);

		// ������ȷ��
		int itmp1 = (iRet) & (JniX431File.LSX_FILE_READABLE);
		int itmp2 = (iRet) & (JniX431File.LSX_FILE_V2);
		int itmp3 = (iRet) & (JniX431File.LSX_FILE_WRITABLE);

		assertTrue(itmp1 != 0);
		assertTrue(itmp2 != 0);
		assertTrue(itmp3 == 0);

	}

	/*
	 * ������������lsx_init��lsx_open��lsx_read_baseinfo��lsx_close��lsx_deinit���������
	 * 
	 * �ο�C/C++���Թ�����������TEST (V2FileReadBasicDataTest)
	 */
	public void testlsx_read_baseinfo() throws Throwable {

		// ��ʼ��
		int hlsx = mJxf.lsx_init();
		assertNotNull(hlsx);

		String strFileName = FileUtils.sdCardGetDirectoryPath()
				+ File.separator + "v2.x431";

		// ȷ���ļ�����
		boolean bExist = FileUtils.isFileExist(strFileName);
		assertTrue(bExist);

		X431String filename = new X431String(strFileName);

		X431Integer error = new X431Integer(10);

		// ���ļ�
		int lsx_file = mJxf.lsx_open(hlsx, filename, JniX431File.MODE_READ,
				error);
		assertTrue(lsx_file != 0);

		LSX_BASEINFO baseinfo = new LSX_BASEINFO();

		// lsx_read_baseinfo��������
		int iRet = mJxf.lsx_read_baseinfo(lsx_file, baseinfo);

		// ������ȷ��
		assertEquals(0, iRet);
		assertEquals("980241111111", baseinfo.serialno);
		assertEquals(JniX431File.PRODUCT_X431, baseinfo.productid);
		assertEquals(1252, baseinfo.codepage);
		assertNull(baseinfo.langname);
		assertEquals("en", baseinfo.langcode);
		assertNull(baseinfo.langcode_en);
		assertEquals("Audi V13.00", baseinfo.diagversion);
		assertTrue(baseinfo.creationtime.length() >= 24);

		mJxf.lsx_close(lsx_file);
		mJxf.lsx_deinit(hlsx);
	}

	/*
	 * ������������lsx_init��lsx_open��lsx_read_autoinfo��lsx_close��lsx_deinit���������
	 * 
	 * �ο�C/C++���Թ�����������TEST (V2FileReadBasicDataTest)
	 */
	public void testlsx_read_autoinfo() throws Throwable {

		// ��ʼ��
		int hlsx = mJxf.lsx_init();
		assertNotNull(hlsx);

		String strFileName = FileUtils.sdCardGetDirectoryPath()
				+ File.separator + "v2.x431";

		// ȷ���ļ�����
		boolean bExist = FileUtils.isFileExist(strFileName);
		assertTrue(bExist);

		X431String filename = new X431String(strFileName);
		X431Integer error = new X431Integer(10);

		// ���ļ�
		int lsx_file = mJxf.lsx_open(hlsx, filename, JniX431File.MODE_READ,
				error);
		assertTrue(lsx_file != 0);

		LSX_AUTOINFO autoinfo = new LSX_AUTOINFO();

		// lsx_read_autoinfo��������
		int iRet = mJxf.lsx_read_autoinfo(lsx_file, autoinfo);

		// ������ȷ��
		assertEquals(0, iRet);
		assertEquals("12345678901234567", autoinfo.vin);
		assertEquals("Volkswagen", autoinfo.make);
		assertEquals("Audi", autoinfo.model);
		assertEquals("2006", autoinfo.year);
		assertEquals("China", autoinfo.madein);
		assertEquals("chassis", autoinfo.chassis);
		assertEquals("engine model", autoinfo.enginemodel);
		assertEquals("2.0L", autoinfo.displacement);

		mJxf.lsx_close(lsx_file);
		mJxf.lsx_deinit(hlsx);
	}

	/*
	 * ������������lsx_init��lsx_open��lsx_read_spinfo��lsx_close��lsx_deinit���������
	 * 
	 * �ο�C/C++���Թ�����������TEST (V2FileReadBasicDataTest)
	 */
	public void testlsx_read_spinfo() throws Throwable {

		// ��ʼ��
		int hlsx = mJxf.lsx_init();
		assertNotNull(hlsx);

		String strFileName = FileUtils.sdCardGetDirectoryPath()
				+ File.separator + "v2.x431";

		// ȷ���ļ�����
		boolean bExist = FileUtils.isFileExist(strFileName);
		assertTrue(bExist);

		X431String filename = new X431String(strFileName);

		X431Integer error = new X431Integer(10);

		// ���ļ�
		int lsx_file = mJxf.lsx_open(hlsx, filename, JniX431File.MODE_READ,
				error);
		assertTrue(lsx_file != 0);

		LSX_SPINFO spinfo = new LSX_SPINFO();

		// lsx_read_spinfo��������
		int iRet = mJxf.lsx_read_spinfo(lsx_file, spinfo);

		// ������ȷ��
		assertEquals(0, iRet);
		assertEquals("������A", spinfo.name);
		assertEquals("88888888", spinfo.phone);

		mJxf.lsx_close(lsx_file);
		mJxf.lsx_deinit(hlsx);
	}

	/*
	 * ������������lsx_init��lsx_open��lsx_read_userinfo��lsx_close��lsx_deinit���������
	 * 
	 * �ο�C/C++���Թ�����������TEST (V2FileReadBasicDataTest)
	 */
	public void testlsx_read_userinfo() throws Throwable {

		// ��ʼ��
		int hlsx = mJxf.lsx_init();
		assertNotNull(hlsx);

		String strFileName = FileUtils.sdCardGetDirectoryPath()
				+ File.separator + "v2.x431";

		// ȷ���ļ�����
		boolean bExist = FileUtils.isFileExist(strFileName);
		assertTrue(bExist);

		X431String filename = new X431String(strFileName);

		X431Integer error = new X431Integer(10);

		// ���ļ�
		int lsx_file = mJxf.lsx_open(hlsx, filename, JniX431File.MODE_READ,
				error);
		assertTrue(lsx_file != 0);

		LSX_USERINFO userinfo = new LSX_USERINFO();

		// lsx_read_userinfo��������
		int iRet = mJxf.lsx_read_userinfo(lsx_file, userinfo);

		// ������ȷ��
		assertEquals(0, iRet);
		assertEquals("customer B", userinfo.name);
		assertEquals("999999999", userinfo.phone);
		assertEquals("654321", userinfo.license);

		mJxf.lsx_close(lsx_file);
		mJxf.lsx_deinit(hlsx);
	}

	/*
	 * ������������lsx_init��lsx_open��lsx_rec_readgroupcount��lsx_close��lsx_deinit���������
	 * 
	 * �ο�C/C++���Թ�����������TEST (V2FileReadBasicDataTest)
	 */
	public void testlsx_rec_readgroupcount() throws Throwable {

		// ��ʼ��
		int hlsx = mJxf.lsx_init();
		assertNotNull(hlsx);

		String strFileName = FileUtils.sdCardGetDirectoryPath()
				+ File.separator + "v2.x431";

		// ȷ���ļ�����
		boolean bExist = FileUtils.isFileExist(strFileName);
		assertTrue(bExist);

		X431String filename = new X431String(strFileName);

		X431Integer error = new X431Integer(10);

		// ���ļ�
		int lsx_file = mJxf.lsx_open(hlsx, filename, JniX431File.MODE_READ,
				error);
		assertTrue(lsx_file != 0);

		// lsx_rec_readgroupcount��������
		int iRet = mJxf.lsx_rec_readgroupcount(lsx_file);

		// ������ȷ��
		assertEquals(2, iRet);

		mJxf.lsx_close(lsx_file);
		mJxf.lsx_deinit(hlsx);
	}

	/*
	 * ������������lsx_init��lsx_open��lsx_read_fileversion��lsx_close��lsx_deinit���������
	 * 
	 * �ο�C/C++���Թ�����������TEST (V2FileReadBasicDataTest)
	 */
	public void testlsx_read_fileversion() throws Throwable {

		// ��ʼ��
		int hlsx = mJxf.lsx_init();
		assertNotNull(hlsx);

		String strFileName = FileUtils.sdCardGetDirectoryPath()
				+ File.separator + "v2.x431";

		// ȷ���ļ�����
		boolean bExist = FileUtils.isFileExist(strFileName);
		assertTrue(bExist);

		X431String filename = new X431String(strFileName);

		X431Integer error = new X431Integer(10);

		// ���ļ�
		int lsx_file = mJxf.lsx_open(hlsx, filename, JniX431File.MODE_READ,
				error);
		assertTrue(lsx_file != 0);

		// lsx_read_fileversion��������
		short iRet = mJxf.lsx_read_fileversion(lsx_file);

		// ������ȷ��
		int iTmp = iRet & 0x0200;
		assertTrue(iTmp != 0);

		mJxf.lsx_close(lsx_file);
		mJxf.lsx_deinit(hlsx);
	}

	/*
	 * ������������lsx_init��lsx_open��lsx_read_langcode��lsx_close��lsx_deinit���������
	 * 
	 * �ο�C/C++���Թ�����������TEST (V2FileReadBasicDataTest)
	 */
	public void testlsx_read_langcode() throws Throwable {

		// ��ʼ��
		int hlsx = mJxf.lsx_init();
		assertNotNull(hlsx);

		String strFileName = FileUtils.sdCardGetDirectoryPath()
				+ File.separator + "v2.x431";

		// ȷ���ļ�����
		boolean bExist = FileUtils.isFileExist(strFileName);
		assertTrue(bExist);

		X431String filename = new X431String(strFileName);

		X431Integer error = new X431Integer(10);

		// ���ļ�
		int lsx_file = mJxf.lsx_open(hlsx, filename, JniX431File.MODE_READ,
				error);
		assertTrue(lsx_file != 0);

		X431String code = new X431String();
		X431String code_en = new X431String();

		// lsx_read_langcode��������
		int iRet = mJxf.lsx_read_langcode(lsx_file, code, code_en);

		// ������ȷ��
		assertEquals(1, iRet);
		assertEquals("en", code.mValue);
		assertNull(code_en.mValue);

		mJxf.lsx_close(lsx_file);
		mJxf.lsx_deinit(hlsx);
	}

	/*
	 * ������������lsx_init��lsx_open��lsx_rec_readgroupcount��
	 * 
	 * lsx_rec_readgroupid, lsx_rec_readdtccount�� lsx_rec_readfirstdtcitem��
	 * 
	 * lsx_rec_readdtc�� lsx_rec_readnextdtcitem�� lsx_rec_readdtcinfo��
	 * 
	 * lsx_close��lsx_deinit�ȷ�����
	 * 
	 * Ϊ�˷����д������ҲΪ�˷���ʹ�õ���Ա�ο�
	 * 
	 * �����߼��������ָ�C/C++��������һ�£�
	 * 
	 * �ο�C/C++���Թ�����������TEST (V2FileReadDtcDataTest)
	 */
	public void testV2FileReadDtcDataTest() throws Throwable {

		// lsx_init����
		int hlsx = mJxf.lsx_init();
		assertNotNull(hlsx);

		String strFileName = FileUtils.sdCardGetDirectoryPath()
				+ File.separator + "v2.x431";

		// ȷ���ļ�����
		boolean bExist = FileUtils.isFileExist(strFileName);
		assertTrue(bExist);

		X431String filename = new X431String(strFileName);

		X431Integer error = new X431Integer(10);

		// lsx_open����
		int lsx_file = mJxf.lsx_open(hlsx, filename, JniX431File.MODE_READ,
				error);
		assertTrue(lsx_file != 0);

		// lsx_rec_readgroupcount����
		int grpcount = mJxf.lsx_rec_readgroupcount(lsx_file);
		assertEquals(2, grpcount);

		int grp = 0;
		int item = 0;
		X431String dtc = new X431String();
		X431String state = new X431String();
		X431String desc = new X431String();
		X431String time = new X431String();

		for (int i = 1; i < grpcount; ++i) {

			grp = mJxf.lsx_rec_readgroupid(lsx_file, i);
			assertTrue(grp != 0);

			int dtccount = mJxf.lsx_rec_readdtccount(grp);
			assertEquals(5, dtccount);

			item = mJxf.lsx_rec_readfirstdtcitem(grp);
			assertTrue(item != 0);

			int count = 0;
			while (item != 0) {

				int iRet = mJxf.lsx_rec_readdtc(item, dtc, state, desc, time);
				assertEquals(0, iRet);

				++count;
				if (count == 1) {
					assertEquals("P1200", dtc.mValue);
					assertEquals("current", state.mValue);
					assertEquals("fault 1", desc.mValue);
					assertEquals("16:05:00", time.mValue);
				} else if (count == 4) {
					assertEquals("P1300", dtc.mValue);
					assertEquals("current", state.mValue);
					assertEquals("fault 3", desc.mValue);
					assertEquals("16:05:00", time.mValue);
				} else if (count == 5) {
					assertEquals("P1301", dtc.mValue);
					assertEquals("current", state.mValue);
					assertEquals("fault 4", desc.mValue);
					assertEquals("16:05:00", time.mValue);
				}

				item = mJxf.lsx_rec_readnextdtcitem(item);
			}

			assertEquals(5, count);

			assertEquals(0,
					mJxf.lsx_rec_readdtcinfo(grp, "P1201", state, desc, time));
			assertEquals("current", state.mValue);
			assertEquals("fault 2", desc.mValue);
			assertEquals("16:05:00", time.mValue);

			assertEquals(0,
					mJxf.lsx_rec_readdtcinfo(grp, "P1200", state, desc, time));
			assertEquals("current", state.mValue);
			assertEquals("fault 1", desc.mValue);
			assertEquals("16:05:00", time.mValue);

			assertEquals(0,
					mJxf.lsx_rec_readdtcinfo(grp, "P1301", state, desc, time));
			assertEquals("current", state.mValue);
			assertEquals("fault 4", desc.mValue);
			assertEquals("16:05:00", time.mValue);
		}

		mJxf.lsx_close(lsx_file);
		mJxf.lsx_deinit(hlsx);
	}

	/*
	 * ������������lsx_init��lsx_open��lsx_rec_readgroupcount��
	 * 
	 * lsx_rec_readgroupid, lsx_rec_readdsitemcount�� lsx_rec_readdscolcount��
	 * 
	 * lsx_rec_readdsname�� lsx_rec_readdsunit�� lsx_rec_readdstype��
	 * 
	 * lsx_rec_readfirstdsitem, lsx_rec_readds, lsx_rec_readrelndsitem,
	 * 
	 * lsx_close��lsx_deinit�ȷ�����
	 * 
	 * Ϊ�˷����д������ҲΪ�˷���ʹ�õ���Ա�ο�
	 * 
	 * �����߼��������ָ�C/C++��������һ�£�
	 * 
	 * �ο�C/C++���Թ�����������TEST (V2FileReadDSDataTest)
	 */
	public void testV2FileReadDSDataTest() throws Throwable {

		// ��ʼ��
		int hlsx = mJxf.lsx_init();
		assertNotNull(hlsx);

		String strFileName = FileUtils.sdCardGetDirectoryPath()
				+ File.separator + "v2.x431";

		// ȷ���ļ�����
		boolean bExist = FileUtils.isFileExist(strFileName);
		assertTrue(bExist);

		X431String filename = new X431String(strFileName);

		X431Integer error = new X431Integer(10);

		// ���ļ�
		int lsx_file = mJxf.lsx_open(hlsx, filename, JniX431File.MODE_READ,
				error);
		assertTrue(lsx_file != 0);

		int grpcount = mJxf.lsx_rec_readgroupcount(lsx_file);
		assertEquals(2, grpcount);

		int grp = 0;
		int item = 0;
		int cols;
		String buf = new String();
		String namestrs[] = new String[10];
		String unitstrs[] = new String[10];
		String textstrs[] = new String[10];
		short type[] = new short[10];
		int count, itemcount;
		for (int i = 1; i <= grpcount; ++i) {

			grp = mJxf.lsx_rec_readgroupid(lsx_file, i);
			assertTrue(grp != 0);

			itemcount = mJxf.lsx_rec_readdsitemcount(grp);

			if (i == 1) {
				assertEquals(6, itemcount);
			} else {
				assertEquals(9, itemcount);
			}

			cols = mJxf.lsx_rec_readdscolcount(grp);
			assertEquals(7, cols);

			assertEquals(0, mJxf.lsx_rec_readdsname(grp, namestrs, cols));
			assertEquals(0, mJxf.lsx_rec_readdsunit(grp, unitstrs, cols));
			assertTrue(mJxf.lsx_rec_readdstype(grp, type, cols) < 0);

			for (int k = 0; k < cols; ++k) {
				buf = "name " + (k + 1);
				assertEquals(buf, namestrs[k]);

				buf = "unit " + (k + 1);
				assertEquals(buf, unitstrs[k]);
			}

			item = mJxf.lsx_rec_readfirstdsitem(grp);
			assertTrue(item != 0);

			count = 0;
			while (item != 0) {
				assertEquals(0, mJxf.lsx_rec_readds(item, textstrs, cols));

				++count;
				for (int k = 0; k < cols; ++k) {
					buf = "item " + count;
					buf += k + 1;
					assertEquals(buf, textstrs[k]);
				}

				item = mJxf.lsx_rec_readrelndsitem(item, 1);
			}

			assertEquals(count, itemcount);
		}

		mJxf.lsx_close(lsx_file);
		mJxf.lsx_deinit(hlsx);
	}

	/*
	 * Ϊ�˷����д������ҲΪ�˷���ʹ�õ���Ա�ο�
	 * 
	 * �����߼��������ָ�C/C++��������һ�£�
	 * 
	 * �ο�C/C++���Թ�����simpletest.cpp������
	 * 
	 * ��Ϊsimpletest.cpp�������������Ⱥ�˳��
	 * 
	 * ���԰����Ⱥ�˳��ȫ����д�ڱ�������
	 */
	public void testsimpletest() throws Throwable {

		TEST_NewFileNoDataWriteTest();
		TEST_NewFileNoDataReadTest();
		TEST_NewFileBasicDataWriteTest();
		TEST_NewFileBasicDataReadTest();
	}

	private void TEST_NewFileNoDataWriteTest() {
		String dstfile = FileUtils.sdCardGetDirectoryPath() + File.separator
				+ "newfilenodata.x431";
		FileUtils.DeleFile(dstfile);

		assertFalse(FileUtils.isFileExist(dstfile));

		int hlsx = mJxf.lsx_init();
		assertNotNull(hlsx);

		X431String filename = new X431String(dstfile);
		X431Integer error = new X431Integer(10);

		// ���ļ�
		int lsx_file = mJxf.lsx_open(hlsx, filename, JniX431File.MODE_WRITE,
				error);
		assertTrue(lsx_file != 0);

		mJxf.lsx_close(lsx_file);
		mJxf.lsx_deinit(hlsx);

		int iRet0 = mJxf.lsx_checkfile(filename)
				& JniX431File.LSX_FILE_READABLE;
		int iRet1 = mJxf.lsx_checkfile(filename)
				& JniX431File.LSX_FILE_WRITABLE;
		int iRet2 = mJxf.lsx_checkfile(filename) & JniX431File.LSX_FILE_V3;

		assertTrue(iRet0 != 0);
		assertTrue(iRet1 != 0);
		assertTrue(iRet2 != 0);
		assertEquals(312, FileUtils.GetFileSize(dstfile));
	}

	private void TEST_NewFileNoDataReadTest() {
		String dstfile = FileUtils.sdCardGetDirectoryPath() + File.separator
				+ "newfilenodata.x431";
		assertTrue(FileUtils.isFileExist(dstfile));

		int hlsx = mJxf.lsx_init();
		assertNotNull(hlsx);

		X431String filename = new X431String(dstfile);
		X431Integer error = new X431Integer(10);

		// ���ļ�
		int file = mJxf.lsx_open(hlsx, filename, JniX431File.MODE_READ, error);
		assertTrue(file != 0);

		LSX_BASEINFO bi = new LSX_BASEINFO();
		assertTrue(mJxf.lsx_read_baseinfo(file, bi) == 0);
		assertNull(bi.serialno);

		// ������ȷ��
		assertNull(bi.serialno);
		assertEquals(JniX431File.PRODUCT_UNKNOWN, bi.productid);
		assertEquals(1252, bi.codepage);
		assertEquals("English", bi.langname);
		assertEquals("en", bi.langcode);
		assertNull(bi.langcode_en);
		assertEquals(null, bi.diagversion);
		assertTrue(bi.creationtime.length() >= 24);

		LSX_AUTOINFO ai = new LSX_AUTOINFO();
		assertTrue(mJxf.lsx_read_autoinfo(file, ai) == 0);

		// ������ȷ��
		assertNull(ai.vin);
		assertNull(ai.make);
		assertNull(ai.model);
		assertNull(ai.year);
		assertNull(ai.madein);
		assertNull(ai.chassis);
		assertNull(ai.enginemodel);
		assertNull(ai.displacement);

		LSX_SPINFO spi = new LSX_SPINFO();
		assertTrue(mJxf.lsx_read_spinfo(file, spi) == 0);

		// ������ȷ��
		assertNull(spi.name);
		assertNull(spi.phone);

		LSX_USERINFO ui = new LSX_USERINFO();
		assertTrue(mJxf.lsx_read_userinfo(file, ui) == 0);
		assertNull(ui.name);
		assertNull(ui.phone);
		assertNull(ui.license);

		assertTrue(mJxf.lsx_rec_readgroupcount(file) == 0);

		short version = mJxf.lsx_read_fileversion(file);
		int iTmp = (int) (version & 0x0300);
		assertTrue(iTmp != 0);

		X431String code = new X431String();
		X431String code_en = new X431String();

		// lsx_read_langcode��������
		assertEquals(1, mJxf.lsx_read_langcode(file, code, code_en));
		assertEquals("en", code.mValue);
		assertNull(code_en.mValue);

		mJxf.lsx_close(file);
		mJxf.lsx_deinit(hlsx);
	}

	private void TEST_NewFileBasicDataWriteTest() {
		String dstfile = FileUtils.sdCardGetDirectoryPath() + File.separator
				+ "newfilebasicdata.x431";

		FileUtils.DeleFile(dstfile);
		assertFalse(FileUtils.isFileExist(dstfile));

		int hlsx = mJxf.lsx_init();
		assertNotNull(hlsx);

		X431String filename = new X431String(dstfile);
		X431Integer error = new X431Integer(10);

		// ���ļ�
		int file = mJxf.lsx_open(hlsx, filename, JniX431File.MODE_WRITE, error);
		assertTrue(file != 0);

		assertTrue(Write_Userinfo(file));
		assertTrue(Write_Baseinfo(file));
		assertTrue(Write_Spinfo(file));
		assertTrue(Write_Autoinfo(file));

		mJxf.lsx_close(file);
		mJxf.lsx_deinit(hlsx);

		int iRet1 = mJxf.lsx_checkfile(filename)
				& JniX431File.LSX_FILE_READABLE;
		int iRet2 = mJxf.lsx_checkfile(filename)
				& JniX431File.LSX_FILE_WRITABLE;
		int iRet3 = mJxf.lsx_checkfile(filename) & JniX431File.LSX_FILE_V3;
		long lRet = FileUtils.GetFileSize(dstfile);

		assertTrue(iRet1 != 0);
		assertTrue(iRet2 != 0);
		assertTrue(iRet3 != 0);

		assertEquals(471, lRet);
	}

	private void TEST_NewFileBasicDataReadTest() {
		String dstfile = FileUtils.sdCardGetDirectoryPath() + File.separator
				+ "newfilebasicdata.x431";

		assertTrue(FileUtils.isFileExist(dstfile));

		int hlsx = mJxf.lsx_init();
		assertNotNull(hlsx);

		X431String filename = new X431String(dstfile);
		X431Integer error = new X431Integer(10);

		// ���ļ�
		int file = mJxf.lsx_open(hlsx, filename, JniX431File.MODE_READ, error);
		assertTrue(file != 0);

		LSX_BASEINFO bi = new LSX_BASEINFO();
		assertEquals(0, mJxf.lsx_read_baseinfo(file, bi));
		assertEquals("980241111111", bi.serialno);
		assertEquals(JniX431File.PRODUCT_CRECORDER, bi.productid);
		assertEquals(936, bi.codepage);
		assertEquals("cn", bi.langcode);
		assertEquals("en", bi.langcode_en);
		assertEquals("Chinese Simplified", bi.langname);
		assertEquals("Audi V13.00", bi.diagversion);
		assertTrue(bi.creationtime.length() >= 24);

		LSX_AUTOINFO ai = new LSX_AUTOINFO();
		assertEquals(0, mJxf.lsx_read_autoinfo(file, ai));
		assertEquals("12345678901234567", ai.vin);
		assertEquals("Volkswagen", ai.make);
		assertEquals("�µ�", ai.model);
		assertEquals("2006", ai.year);
		assertEquals("China", ai.madein);
		assertEquals("chassis", ai.chassis);
		assertEquals("engine model", ai.enginemodel);
		assertEquals("2.0L", ai.displacement);

		LSX_SPINFO spi = new LSX_SPINFO();
		assertEquals(0, mJxf.lsx_read_spinfo(file, spi));
		assertEquals("������A", spi.name);
		assertEquals("88888888", spi.phone);

		LSX_USERINFO ui = new LSX_USERINFO();
		assertEquals(0, mJxf.lsx_read_userinfo(file, ui));
		assertEquals("customer A", ui.name);
		assertEquals("77777777", ui.phone);
		assertEquals("123456", ui.license);

		assertEquals(0, mJxf.lsx_rec_readgroupcount(file));

		short version = mJxf.lsx_read_fileversion(file);
		assertTrue((version & 0x0300) != 0);

		X431String code = new X431String();
		X431String code_en = new X431String();
		assertEquals(2, mJxf.lsx_read_langcode(file, code, code_en));
		assertEquals("cn", code.mValue);
		assertEquals("en", code_en.mValue);

		mJxf.lsx_close(file);
		mJxf.lsx_deinit(hlsx);

	}

	/*
	 * Ϊ�˷����д������ҲΪ�˷���ʹ�õ���Ա�ο�
	 * 
	 * �����߼��������ָ�C/C++��������һ�£�
	 * 
	 * �ο�C/C++���Թ�����langexttest.cpp������
	 * 
	 * ��Ϊlangexttest.cpp�������������Ⱥ�˳��
	 * 
	 * ���԰����Ⱥ�˳��ȫ����д�ڱ�������
	 */
	public void testlangexttest() throws Throwable {

		TEST_NewFileJaExtWriteTest();
		TEST_NewFileJaExtReadTest();
		TEST_NewFileChsExtWriteTest();
		TEST_NewFileChsExtReadTest();
	}

	private void TEST_NewFileJaExtWriteTest() {
		String dstfile = FileUtils.sdCardGetDirectoryPath() + File.separator
				+ "newfilejaext.x431";
		FileUtils.DeleFile(dstfile);
		assertFalse(FileUtils.isFileExist(dstfile));

		int hlsx = mJxf.lsx_init();
		assertNotNull(hlsx);

		X431String filename = new X431String(dstfile);
		X431Integer error = new X431Integer(10);

		// ���ļ�
		int file = mJxf.lsx_open(hlsx, filename, JniX431File.MODE_WRITE, error);
		assertTrue(file != 0);

		assertTrue(Write_Baseinfo_langexttest(file, "ja", "Japanese"));

		mJxf.lsx_close(file);
		mJxf.lsx_deinit(hlsx);

		assertTrue((mJxf.lsx_checkfile(filename) & JniX431File.LSX_FILE_READABLE) != 0);
		assertTrue((mJxf.lsx_checkfile(filename) & JniX431File.LSX_FILE_WRITABLE) != 0);
		assertTrue((mJxf.lsx_checkfile(filename) & JniX431File.LSX_FILE_V3) != 0);
		assertEquals(340, FileUtils.GetFileSize(dstfile));
	}

	private void TEST_NewFileJaExtReadTest() {
		String dstfile = FileUtils.sdCardGetDirectoryPath() + File.separator
				+ "newfilejaext.x431";
		assertTrue(FileUtils.isFileExist(dstfile));

		int hlsx = mJxf.lsx_init();
		assertNotNull(hlsx);

		X431String filename = new X431String(dstfile);
		X431Integer error = new X431Integer(10);

		// ���ļ�
		int file = mJxf.lsx_open(hlsx, filename, JniX431File.MODE_READ, error);
		assertTrue(file != 0);

		LSX_BASEINFO bi = new LSX_BASEINFO();
		assertEquals(0, mJxf.lsx_read_baseinfo(file, bi));
		assertEquals("980241111111", bi.serialno);
		assertEquals(JniX431File.PRODUCT_CRECORDER, bi.productid);
		assertEquals(51932, bi.codepage);
		assertEquals("jp", bi.langcode);
		assertEquals("en", bi.langcode_en);
		assertEquals("Japanese", bi.langname);
		assertEquals("Audi V13.00", bi.diagversion);
		assertTrue(bi.creationtime.length() >= 24);

		assertEquals(0, mJxf.lsx_rec_readgroupcount(file));

		short version = mJxf.lsx_read_fileversion(file);
		assertTrue((version & 0x0300) != 0);

		X431String code = new X431String();
		X431String code_en = new X431String();
		assertEquals(2, mJxf.lsx_read_langcode(file, code, code_en));
		assertEquals("jp", code.mValue);
		assertEquals("en", code_en.mValue);

		mJxf.lsx_close(file);
		mJxf.lsx_deinit(hlsx);
	}

	private void TEST_NewFileChsExtWriteTest() {
		String dstfile = FileUtils.sdCardGetDirectoryPath() + File.separator
				+ "newfilechsext.x431";
		FileUtils.DeleFile(dstfile);
		assertFalse(FileUtils.isFileExist(dstfile));

		int hlsx = mJxf.lsx_init();
		assertNotNull(hlsx);

		X431String filename = new X431String(dstfile);
		X431Integer error = new X431Integer(10);

		// ���ļ�
		int file = mJxf.lsx_open(hlsx, filename, JniX431File.MODE_WRITE, error);
		assertTrue(file != 0);

		assertTrue(Write_Baseinfo_langexttest(file, "chs", "Chinese Simplified"));

		mJxf.lsx_close(file);
		mJxf.lsx_deinit(hlsx);

		assertTrue((mJxf.lsx_checkfile(filename) & JniX431File.LSX_FILE_READABLE) != 0);
		assertTrue((mJxf.lsx_checkfile(filename) & JniX431File.LSX_FILE_WRITABLE) != 0);
		assertTrue((mJxf.lsx_checkfile(filename) & JniX431File.LSX_FILE_V3) != 0);
		assertEquals(351, FileUtils.GetFileSize(dstfile));
	}

	private void TEST_NewFileChsExtReadTest() {
		String dstfile = FileUtils.sdCardGetDirectoryPath() + File.separator
				+ "newfilechsext.x431";
		assertTrue(FileUtils.isFileExist(dstfile));

		int hlsx = mJxf.lsx_init();
		assertNotNull(hlsx);

		X431String filename = new X431String(dstfile);
		X431Integer error = new X431Integer(10);

		// ���ļ�
		int file = mJxf.lsx_open(hlsx, filename, JniX431File.MODE_READ, error);
		assertTrue(file != 0);

		LSX_BASEINFO bi = new LSX_BASEINFO();
		assertEquals(0, mJxf.lsx_read_baseinfo(file, bi));
		assertEquals("980241111111", bi.serialno);
		assertEquals(JniX431File.PRODUCT_CRECORDER, bi.productid);
		assertEquals(51932, bi.codepage);
		assertEquals("cn", bi.langcode);
		assertEquals("en", bi.langcode_en);
		assertEquals("Chinese Simplified", bi.langname);
		assertEquals("Audi V13.00", bi.diagversion);
		assertTrue(bi.creationtime.length() >= 24);

		assertEquals(0, mJxf.lsx_rec_readgroupcount(file));

		short version = mJxf.lsx_read_fileversion(file);
		assertTrue((version & 0x0300) != 0);

		X431String code = new X431String();
		X431String code_en = new X431String();
		assertEquals(2, mJxf.lsx_read_langcode(file, code, code_en));
		assertEquals("cn", code.mValue);
		assertEquals("en", code_en.mValue);

		mJxf.lsx_close(file);
		mJxf.lsx_deinit(hlsx);
	}

	/*
	 * Ϊ�˷����д������ҲΪ�˷���ʹ�õ���Ա�ο�
	 * 
	 * �����߼��������ָ�C/C++��������һ�£�
	 * 
	 * �ο�C/C++���Թ�����itemtest.cpp������
	 * 
	 * ��Ϊitemtest.cpp�������������Ⱥ�˳��
	 * 
	 * ���԰����Ⱥ�˳��ȫ����д�ڱ�������
	 * 
	 * ע�����Ⱥ�˳�����������
	 */

	public void testitemtest() throws Throwable {

		TEST_GroupItemWriteTest();
		TEST_GroupItemReadInOrderTest();
		TEST_GroupItemReadReverseOrderTest();
		TEST_GroupItemReadDtcInOrderTest();
		TEST_GroupItemReadDtcReverseOrderTest();
		TEST_GroupItemReadFFInOrderTest();
		TEST_GroupItemReadFFReverseOrderTest();
		TEST_GroupItemReadDSInOrderTest();
		TEST_GroupItemReadDSReverseOrderTest();
	}

	private void TEST_GroupItemWriteTest() {
		String dstfile = FileUtils.sdCardGetDirectoryPath() + File.separator
				+ "itemtest.x431";
		FileUtils.DeleFile(dstfile);
		assertFalse(FileUtils.isFileExist(dstfile));

		int hlsx = mJxf.lsx_init();
		assertNotNull(hlsx);

		X431String filename = new X431String(dstfile);
		X431Integer error = new X431Integer(10);

		// ���ļ�
		int file = mJxf.lsx_open(hlsx, filename, JniX431File.MODE_WRITE, error);
		assertTrue(file != 0);

		assertTrue(Write_Baseinfo_itemtest(file));

		int grp = mJxf.lsx_rec_writenewgroup(file, "Audi", "Canbus",
				"12345678976543210", "2008/09/26 21:30:31", 2);
		assertTrue(grp != 0);

		assertTrue(Write_Rdn(grp));
		assertTrue(Write_Dtcs(grp, "P1200")); // 2 - 4
		assertTrue(Write_Dtcs(grp, "P1201")); // 3
		assertTrue(Write_Dtcs(grp, "P1202")); // 4
		assertTrue(Write_Vi(grp)); // 5

		int lineno = 1;
		assertTrue(Write_DsBasics(grp)); // 6
		assertTrue(Write_Ds(grp, lineno, 13)); // 7 - 19

		lineno += 13;
		assertTrue(Write_FF(grp, "P1300")); // 20 - 21
		assertTrue(Write_FF(grp, "P1301")); // 21
		assertTrue(Write_Ds(grp, lineno, 7)); // 22 - 28

		lineno += 7;
		assertTrue(Write_Dtcs(grp, "P1500")); // 29 - 30
		assertTrue(Write_Dtcs(grp, "P1501")); // 30

		assertTrue(Write_Rdn(grp)); // 31
		assertTrue(Write_Ds(grp, lineno, 10)); // 32 - 41

		assertTrue(Write_FF(grp, "P1600")); // 42 - 43
		assertTrue(Write_FF(grp, "P1601")); // 43

		assertTrue(Write_Dtcs(grp, "P1700")); // 44 - 50
		assertTrue(Write_Dtcs(grp, "P1701")); // 45
		assertTrue(Write_Dtcs(grp, "P1702")); // 46
		assertTrue(Write_Dtcs(grp, "P1703")); // 47
		assertTrue(Write_Dtcs(grp, "P1704")); // 48
		assertTrue(Write_Dtcs(grp, "P1705")); // 49
		assertTrue(Write_Dtcs(grp, "P1700")); // 50

		assertTrue(mJxf.lsx_rec_finishnewgroup(grp, "2008/09/26/23:01:03") == 0);

		mJxf.lsx_close(file);
		mJxf.lsx_deinit(hlsx);

		assertEquals(13082, FileUtils.GetFileSize(dstfile));
	}

	private void TEST_GroupItemReadInOrderTest() {
		String dstfile = FileUtils.sdCardGetDirectoryPath() + File.separator
				+ "itemtest.x431";
		assertTrue(FileUtils.isFileExist(dstfile));

		int hlsx = mJxf.lsx_init();
		assertNotNull(hlsx);

		X431String filename = new X431String(dstfile);
		X431Integer error = new X431Integer(10);

		// ���ļ�
		int file = mJxf.lsx_open(hlsx, filename, JniX431File.MODE_READ, error);
		assertTrue(file != 0);

		assertTrue(mJxf.lsx_rec_readgroupcount(file) == 1);

		int grp = mJxf.lsx_rec_readgroupid(file, 1);
		assertTrue(grp != 0);

		// ����һ��group��Ϣ
		X431String name = new  X431String();
		X431String protocol = new  X431String();
		X431String vin = new  X431String();
		X431String starttime = new  X431String();
		X431String endtime = new  X431String();
		X431Integer dsinterval = new  X431Integer();
		
		assertTrue(mJxf.lsx_rec_readgroupinfo(grp, name, protocol, vin, starttime, endtime, dsinterval) == 0);
		assertEquals("Audi" ,name.mValue);
		assertEquals("Canbus" ,protocol.mValue);
		assertEquals("12345678976543210" ,vin.mValue);
		assertTrue(starttime.mValue.length() >= 19);
		assertTrue(endtime.mValue.length() >= 19);
		assertEquals(2 ,dsinterval.mValue);
		
		
		int itemtype = mJxf.lsx_rec_readalltype(grp);
		assertTrue((itemtype & JniX431File.RECORD_DTC) != 0);
		assertTrue((itemtype & JniX431File.RECORD_DATASTREAM) != 0);
		assertTrue((itemtype & JniX431File.RECORD_FREEZEFRAME) != 0);
		assertTrue((itemtype & JniX431File.RECORD_READINESS) != 0);
		assertTrue((itemtype & JniX431File.RECORD_VERSIONINFO) != 0);

		int item = mJxf.lsx_rec_readfirstitem(grp);

		int count = 0;
		while (item != 0) {
			++count;
			if (count == 1) {
				assertTrue((mJxf.lsx_rec_readitemtype(item) & JniX431File.RECORD_READINESS) != 0);
			} else if (count >= 2 && count <= 4) {
				assertTrue((mJxf.lsx_rec_readitemtype(item) & JniX431File.RECORD_DTC) != 0);
			} else if (count == 5) {
				assertTrue((mJxf.lsx_rec_readitemtype(item) & JniX431File.RECORD_VERSIONINFO) != 0);
			} else if (count == 6) {
				assertTrue((mJxf.lsx_rec_readitemtype(item) & JniX431File.RECORD_DSBASICS) != 0);
			} else if (count >= 7 && count <= 19) {
				assertTrue((mJxf.lsx_rec_readitemtype(item) & JniX431File.RECORD_DATASTREAM) != 0);
			} else if (count >= 20 && count <= 21) {
				assertTrue((mJxf.lsx_rec_readitemtype(item) & JniX431File.RECORD_FREEZEFRAME) != 0);
			} else if (count >= 22 && count <= 28) {
				assertTrue((mJxf.lsx_rec_readitemtype(item) & JniX431File.RECORD_DATASTREAM) != 0);
			} else if (count >= 29 && count <= 30) {
				assertTrue((mJxf.lsx_rec_readitemtype(item) & JniX431File.RECORD_DTC) != 0);
			} else if (count == 31) {
				assertTrue((mJxf.lsx_rec_readitemtype(item) & JniX431File.RECORD_READINESS) != 0);
			} else if (count >= 32 && count <= 41) {
				assertTrue((mJxf.lsx_rec_readitemtype(item) & JniX431File.RECORD_DATASTREAM) != 0);
			} else if (count >= 42 && count <= 43) {
				assertTrue((mJxf.lsx_rec_readitemtype(item) & JniX431File.RECORD_FREEZEFRAME) != 0);
			} else if (count >= 44 && count <= 50) {
				assertTrue((mJxf.lsx_rec_readitemtype(item) & JniX431File.RECORD_DTC) != 0);
			}

			item = mJxf.lsx_rec_readnextitem(item);
		}

		mJxf.lsx_close(file);
		mJxf.lsx_deinit(hlsx);
	}

	private void TEST_GroupItemReadReverseOrderTest() {
		String dstfile = FileUtils.sdCardGetDirectoryPath() + File.separator
				+ "itemtest.x431";
		assertTrue(FileUtils.isFileExist(dstfile));

		int hlsx = mJxf.lsx_init();
		assertNotNull(hlsx);

		X431String filename = new X431String(dstfile);
		X431Integer error = new X431Integer(10);

		// ���ļ�
		int file = mJxf.lsx_open(hlsx, filename, JniX431File.MODE_READ, error);
		assertTrue(file != 0);

		assertTrue(mJxf.lsx_rec_readgroupcount(file) == 1);

		int grp = mJxf.lsx_rec_readgroupid(file, 1);
		assertTrue(grp != 0);

		int itemtype = mJxf.lsx_rec_readalltype(grp);
		assertTrue((itemtype & JniX431File.RECORD_DTC) != 0);
		assertTrue((itemtype & JniX431File.RECORD_DATASTREAM) != 0);
		assertTrue((itemtype & JniX431File.RECORD_FREEZEFRAME) != 0);
		assertTrue((itemtype & JniX431File.RECORD_READINESS) != 0);
		assertTrue((itemtype & JniX431File.RECORD_VERSIONINFO) != 0);

		int item = mJxf.lsx_rec_readlastitem(grp);

		int count = 50;
		while (item != 0) {
			if (count == 1) {
				assertTrue((mJxf.lsx_rec_readitemtype(item) & JniX431File.RECORD_READINESS) != 0);
			} else if (count >= 2 && count <= 4) {
				assertTrue((mJxf.lsx_rec_readitemtype(item) & JniX431File.RECORD_DTC) != 0);
			} else if (count == 5) {
				assertTrue((mJxf.lsx_rec_readitemtype(item) & JniX431File.RECORD_VERSIONINFO) != 0);
			} else if (count == 6) {
				assertTrue((mJxf.lsx_rec_readitemtype(item) & JniX431File.RECORD_DSBASICS) != 0);
			} else if (count >= 7 && count <= 19) {
				assertTrue((mJxf.lsx_rec_readitemtype(item) & JniX431File.RECORD_DATASTREAM) != 0);
			} else if (count >= 20 && count <= 21) {
				assertTrue((mJxf.lsx_rec_readitemtype(item) & JniX431File.RECORD_FREEZEFRAME) != 0);
			} else if (count >= 22 && count <= 28) {
				assertTrue((mJxf.lsx_rec_readitemtype(item) & JniX431File.RECORD_DATASTREAM) != 0);
			} else if (count >= 29 && count <= 30) {
				assertTrue((mJxf.lsx_rec_readitemtype(item) & JniX431File.RECORD_DTC) != 0);
			} else if (count == 31) {
				assertTrue((mJxf.lsx_rec_readitemtype(item) & JniX431File.RECORD_READINESS) != 0);
			} else if (count >= 32 && count <= 41) {
				assertTrue((mJxf.lsx_rec_readitemtype(item) & JniX431File.RECORD_DATASTREAM) != 0);
			} else if (count >= 42 && count <= 43) {
				assertTrue((mJxf.lsx_rec_readitemtype(item) & JniX431File.RECORD_FREEZEFRAME) != 0);
			} else if (count >= 44 && count <= 50) {
				assertTrue((mJxf.lsx_rec_readitemtype(item) & JniX431File.RECORD_DTC) != 0);
			}

			--count;
			item = mJxf.lsx_rec_readprevitem(item);
		}

		mJxf.lsx_close(file);
		mJxf.lsx_deinit(hlsx);
	}

	private void TEST_GroupItemReadDtcInOrderTest() {
		String dstfile = FileUtils.sdCardGetDirectoryPath() + File.separator
				+ "itemtest.x431";
		assertTrue(FileUtils.isFileExist(dstfile));

		int hlsx = mJxf.lsx_init();
		assertNotNull(hlsx);

		X431String filename = new X431String(dstfile);
		X431Integer error = new X431Integer(10);

		// ���ļ�
		int file = mJxf.lsx_open(hlsx, filename, JniX431File.MODE_READ, error);
		assertTrue(file != 0);

		assertTrue(mJxf.lsx_rec_readgroupcount(file) == 1);

		int grp = mJxf.lsx_rec_readgroupid(file, 1);
		assertTrue(grp != 0);

		assertTrue(mJxf.lsx_rec_readdtccount(grp) == 12);

		int item = mJxf.lsx_rec_readfirstdtcitem(grp);

		X431String dtc = new X431String();
		X431String state = new X431String();
		X431String desc = new X431String();
		X431String dtctime = new X431String();
		int count = 0;
		while (item != 0) {
			assertTrue(mJxf.lsx_rec_readdtc(item, dtc, state, desc, dtctime) == 0);
			switch (++count) {
			case 1:
				assertEquals("P1200", dtc.mValue);
				break;
			case 2:
				assertEquals("P1201", dtc.mValue);
				break;
			case 3:
				assertEquals("P1202", dtc.mValue);
				break;
			case 4:
				assertEquals("P1500", dtc.mValue);
				break;
			case 5:
				assertEquals("P1501", dtc.mValue);
				break;
			case 6:
				assertEquals("P1700", dtc.mValue);
				break;
			case 7:
				assertEquals("P1701", dtc.mValue);
				break;
			case 8:
				assertEquals("P1702", dtc.mValue);
				break;
			case 9:
				assertEquals("P1703", dtc.mValue);
				break;
			case 10:
				assertEquals("P1704", dtc.mValue);
				break;
			case 11:
				assertEquals("P1705", dtc.mValue);
				break;
			case 12:
				assertEquals("P1700", dtc.mValue);
				break;
			}

			item = mJxf.lsx_rec_readnextdtcitem(item);
		}

		mJxf.lsx_close(file);
		mJxf.lsx_deinit(hlsx);
	}

	private void TEST_GroupItemReadDtcReverseOrderTest() {
		String dstfile = FileUtils.sdCardGetDirectoryPath() + File.separator
				+ "itemtest.x431";
		assertTrue(FileUtils.isFileExist(dstfile));

		int hlsx = mJxf.lsx_init();
		assertNotNull(hlsx);

		X431String filename = new X431String(dstfile);
		X431Integer error = new X431Integer(10);

		// ���ļ�
		int file = mJxf.lsx_open(hlsx, filename, JniX431File.MODE_READ, error);
		assertTrue(file != 0);

		assertTrue(mJxf.lsx_rec_readgroupcount(file) == 1);

		int grp = mJxf.lsx_rec_readgroupid(file, 1);
		assertTrue(grp != 0);

		assertTrue(mJxf.lsx_rec_readdtccount(grp) == 12);

		int item = mJxf.lsx_rec_readlastdtcitem(grp);

		X431String dtc = new X431String();
		X431String state = new X431String();
		X431String desc = new X431String();
		X431String dtctime = new X431String();
		int count = 12;
		while (item != 0) {
			assertTrue(mJxf.lsx_rec_readdtc(item, dtc, state, desc, dtctime) == 0);
			switch (count) {
			case 1:
				assertEquals("P1200", dtc.mValue);
				break;
			case 2:
				assertEquals("P1201", dtc.mValue);
				break;
			case 3:
				assertEquals("P1202", dtc.mValue);
				break;
			case 4:
				assertEquals("P1500", dtc.mValue);
				break;
			case 5:
				assertEquals("P1501", dtc.mValue);
				break;
			case 6:
				assertEquals("P1700", dtc.mValue);
				break;
			case 7:
				assertEquals("P1701", dtc.mValue);
				break;
			case 8:
				assertEquals("P1702", dtc.mValue);
				break;
			case 9:
				assertEquals("P1703", dtc.mValue);
				break;
			case 10:
				assertEquals("P1704", dtc.mValue);
				break;
			case 11:
				assertEquals("P1705", dtc.mValue);
				break;
			case 12:
				assertEquals("P1700", dtc.mValue);
				break;
			}

			--count;
			item = mJxf.lsx_rec_readprevdtcitem(item);
		}

		mJxf.lsx_close(file);
		mJxf.lsx_deinit(hlsx);

	}

	private void TEST_GroupItemReadFFInOrderTest() {
		String dstfile = FileUtils.sdCardGetDirectoryPath() + File.separator
				+ "itemtest.x431";
		assertTrue(FileUtils.isFileExist(dstfile));

		int hlsx = mJxf.lsx_init();
		assertNotNull(hlsx);

		X431String filename = new X431String(dstfile);
		X431Integer error = new X431Integer(10);

		// ���ļ�
		int file = mJxf.lsx_open(hlsx, filename, JniX431File.MODE_READ, error);
		assertTrue(file != 0);

		assertTrue(mJxf.lsx_rec_readgroupcount(file) == 1);

		int grp = mJxf.lsx_rec_readgroupid(file, 1);
		assertTrue(grp != 0);

		assertTrue(mJxf.lsx_rec_readffitemcount(grp) == 4);

		int item = mJxf.lsx_rec_readfirstffitem(grp);

		X431String dtc = new X431String();
		String textstrs[] = new String[10];
		int count = 0;
		while (item != 0) {
			int cols = mJxf.lsx_rec_readffcolcount(item);
			assertTrue(cols == 5);

			assertTrue(mJxf.lsx_rec_readfreezeframe(item, dtc, textstrs, 5) == 0);
			switch (++count) {
			case 1:
				assertEquals("P1300", dtc.mValue);
				break;
			case 2:
				assertEquals("P1301", dtc.mValue);
				break;
			case 3:
				assertEquals("P1600", dtc.mValue);
				break;
			case 4:
				assertEquals("P1601", dtc.mValue);
				break;
			}

			item = mJxf.lsx_rec_readnextffitem(item);
		}

		mJxf.lsx_close(file);
		mJxf.lsx_deinit(hlsx);
	}

	private void TEST_GroupItemReadFFReverseOrderTest() {
		String dstfile = FileUtils.sdCardGetDirectoryPath() + File.separator
				+ "itemtest.x431";
		assertTrue(FileUtils.isFileExist(dstfile));

		int hlsx = mJxf.lsx_init();
		assertNotNull(hlsx);

		X431String filename = new X431String(dstfile);
		X431Integer error = new X431Integer(10);

		// ���ļ�
		int file = mJxf.lsx_open(hlsx, filename, JniX431File.MODE_READ, error);
		assertTrue(file != 0);

		assertTrue(mJxf.lsx_rec_readgroupcount(file) == 1);

		int grp = mJxf.lsx_rec_readgroupid(file, 1);
		assertTrue(grp != 0);

		assertTrue(mJxf.lsx_rec_readffitemcount(grp) == 4);

		int item = mJxf.lsx_rec_readlastffitem(grp);

		X431String dtc = new X431String();
		String textstrs[] = new String[10];
		int count = 4;
		while (item != 0) {
			int cols = mJxf.lsx_rec_readffcolcount(item);
			assertTrue(cols == 5);

			assertTrue(mJxf.lsx_rec_readfreezeframe(item, dtc, textstrs, 5) == 0);
			switch (count) {
			case 1:
				assertEquals("P1300", dtc.mValue);
				break;
			case 2:
				assertEquals("P1301", dtc.mValue);
				break;
			case 3:
				assertEquals("P1600", dtc.mValue);
				break;
			case 4:
				assertEquals("P1601", dtc.mValue);
				break;
			}

			--count;
			item = mJxf.lsx_rec_readprevffitem(item);
		}

		mJxf.lsx_close(file);
		mJxf.lsx_deinit(hlsx);
	}

	private void TEST_GroupItemReadDSInOrderTest() {
		String dstfile = FileUtils.sdCardGetDirectoryPath() + File.separator
				+ "itemtest.x431";
		assertTrue(FileUtils.isFileExist(dstfile));

		int hlsx = mJxf.lsx_init();
		assertNotNull(hlsx);

		X431String filename = new X431String(dstfile);
		X431Integer error = new X431Integer(10);

		// ���ļ�
		int file = mJxf.lsx_open(hlsx, filename, JniX431File.MODE_READ, error);
		assertTrue(file != 0);

		X431String code = new X431String();
		X431String code_en = new X431String();
		assertTrue(mJxf.lsx_read_langcode(file, code, code_en) == 2);
		assertEquals("cn", code.mValue);
		assertEquals("en", code_en.mValue);

		assertTrue(mJxf.lsx_rec_readgroupcount(file) == 1);

		int grp = mJxf.lsx_rec_readgroupid(file, 1);
		assertTrue(grp != 0);

		int dscols = mJxf.lsx_rec_readdscolcount(grp);
		assertTrue(dscols == 9);

		int item = mJxf.lsx_rec_readfirstdsitem(grp);

		String textstrs[] = new String[10];
		int count = 0;
		while (item != 0) {
			++count;
			assertTrue(mJxf.lsx_selectreadtextlang(file, code.mValue) == 0);

			assertTrue(mJxf.lsx_rec_readds(item, textstrs, 9) == 0);
			switch (count) {
			case 1:
				assertEquals("������ ��ֵ1,1", textstrs[0]);
				assertEquals("������ ��ֵ1,3", textstrs[2]);
				assertEquals("������ ��ֵ1,9", textstrs[8]);
				break;
			case 2:
				assertEquals("������ ��ֵ2,1", textstrs[0]);
				assertEquals("������ ��ֵ2,4", textstrs[3]);
				assertEquals("������ ��ֵ2,9", textstrs[8]);
				break;
			case 15:
				assertEquals("������ ��ֵ15,1", textstrs[0]);
				assertEquals("������ ��ֵ15,7", textstrs[6]);
				assertEquals("������ ��ֵ15,9", textstrs[8]);
				break;
			case 30:
				assertEquals("������ ��ֵ30,1", textstrs[0]);
				assertEquals("������ ��ֵ30,5", textstrs[4]);
				assertEquals("������ ��ֵ30,9", textstrs[8]);
				break;
			}

			assertTrue(mJxf.lsx_selectreadtextlang(file, code_en.mValue) == 0);

			assertTrue(mJxf.lsx_rec_readds(item, textstrs, 9) == 0);
			switch (count) {
			case 1:
				assertEquals("ds item1,1", textstrs[0]);
				assertEquals("ds item1,3", textstrs[2]);
				assertEquals("ds item1,9", textstrs[8]);
				break;
			case 2:
				assertEquals("ds item2,1", textstrs[0]);
				assertEquals("ds item2,4", textstrs[3]);
				assertEquals("ds item2,9", textstrs[8]);
				break;
			case 15:
				assertEquals("ds item15,1", textstrs[0]);
				assertEquals("ds item15,7", textstrs[6]);
				assertEquals("ds item15,9", textstrs[8]);
				break;
			case 30:
				assertEquals("ds item30,1", textstrs[0]);
				assertEquals("ds item30,5", textstrs[4]);
				assertEquals("ds item30,9", textstrs[8]);
				break;
			}

			item = mJxf.lsx_rec_readrelndsitem(item, 1);
		}

		mJxf.lsx_close(file);
		mJxf.lsx_deinit(hlsx);
	}

	private void TEST_GroupItemReadDSReverseOrderTest() {
		String dstfile = FileUtils.sdCardGetDirectoryPath() + File.separator
				+ "itemtest.x431";
		assertTrue(FileUtils.isFileExist(dstfile));

		int hlsx = mJxf.lsx_init();
		assertNotNull(hlsx);

		X431String filename = new X431String(dstfile);
		X431Integer error = new X431Integer(10);

		// ���ļ�
		int file = mJxf.lsx_open(hlsx, filename, JniX431File.MODE_READ, error);
		assertTrue(file != 0);

		X431String code = new X431String();
		X431String code_en = new X431String();
		assertTrue(mJxf.lsx_read_langcode(file, code, code_en) == 2);
		assertEquals("cn", code.mValue);
		assertEquals("en", code_en.mValue);

		assertTrue(mJxf.lsx_rec_readgroupcount(file) == 1);

		int grp = mJxf.lsx_rec_readgroupid(file, 1);
		assertTrue(grp != 0);

		int dscols = mJxf.lsx_rec_readdscolcount(grp);
		assertTrue(dscols == 9);

		int item = mJxf.lsx_rec_readlastdsitem(grp);

		String textstrs[] = new String[10];
		int count = 30;
		while (item != 0) {
			++count;
			assertTrue(mJxf.lsx_selectreadtextlang(file, code.mValue) == 0);

			assertTrue(mJxf.lsx_rec_readds(item, textstrs, 9) == 0);
			switch (count) {
			case 1:
				assertEquals("������ ��ֵ1,1", textstrs[0]);
				assertEquals("������ ��ֵ1,3", textstrs[2]);
				assertEquals("������ ��ֵ1,9", textstrs[8]);
				break;
			case 2:
				assertEquals("������ ��ֵ2,1", textstrs[0]);
				assertEquals("������ ��ֵ2,4", textstrs[3]);
				assertEquals("������ ��ֵ2,9", textstrs[8]);
				break;
			case 15:
				assertEquals("������ ��ֵ15,1", textstrs[0]);
				assertEquals("������ ��ֵ15,7", textstrs[6]);
				assertEquals("������ ��ֵ15,9", textstrs[8]);
				break;
			case 30:
				assertEquals("������ ��ֵ30,1", textstrs[0]);
				assertEquals("������ ��ֵ30,5", textstrs[4]);
				assertEquals("������ ��ֵ30,9", textstrs[8]);
				break;
			}

			assertTrue(mJxf.lsx_selectreadtextlang(file, code_en.mValue) == 0);

			assertTrue(mJxf.lsx_rec_readds(item, textstrs, 9) == 0);
			switch (count) {
			case 1:
				assertEquals("ds item1,1", textstrs[0]);
				assertEquals("ds item1,3", textstrs[2]);
				assertEquals("ds item1,9", textstrs[8]);
				break;
			case 2:
				assertEquals("ds item2,1", textstrs[0]);
				assertEquals("ds item2,4", textstrs[3]);
				assertEquals("ds item2,9", textstrs[8]);
				break;
			case 15:
				assertEquals("ds item15,1", textstrs[0]);
				assertEquals("ds item15,7", textstrs[6]);
				assertEquals("ds item15,9", textstrs[8]);
				break;
			case 30:
				assertEquals("ds item30,1", textstrs[0]);
				assertEquals("ds item30,5", textstrs[4]);
				assertEquals("ds item30,9", textstrs[8]);
				break;
			}

			--count;
			item = mJxf.lsx_rec_readrelndsitem(item, 1);
		}

		mJxf.lsx_close(file);
		mJxf.lsx_deinit(hlsx);

	}

}
