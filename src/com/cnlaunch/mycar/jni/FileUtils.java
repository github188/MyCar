package com.cnlaunch.mycar.jni;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.StringReader;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

/**
 * �ļ�����������
 * 
 * @author xiangyuanmao
 * 
 */
public class FileUtils {

	private String SDPATH;

	/**
	 * ���췽������ʼ��SD���ĸ�Ŀ¼
	 */
	public FileUtils() {
		SDPATH = Environment.getExternalStorageDirectory() + File.separator;

	}

	/**
	 * ��SD���ϴ����ļ�
	 * 
	 * @param newFileName
	 * @return
	 */
	public File createFileOnSD(String newFileName) throws IOException {

		File file = new File(SDPATH + newFileName);
		file.createNewFile();
		return file;

	}

	/**
	 * ��SD���ϴ���Ŀ¼
	 * 
	 * @param dirName
	 * @return
	 * @throws IOException
	 */
	public File createDirOnSD(String dirName) throws IOException {
		File dir = new File(SDPATH + dirName);
		Log.d("System.out", SDPATH + dirName);
		dir.mkdir();
		return dir;
	}

	/**
	 * �ж��ļ��Ƿ����
	 * 
	 * @param filaPath
	 * @return
	 */
	static public boolean isFileExist(String filaPath) {
		File file = new File(filaPath);
		return file.exists();
	}

	/**
	 * ��SD������д�ļ�
	 * 
	 * @param path
	 * @param fileName
	 * @param input
	 * @return
	 */
	public File writeToSDFromInput(String path, String fileName,
			InputStream input) {
		// ����һ���ļ������һ�����������
		File file = null;
		// file.list();
		OutputStream output = null;

		try {
			// ����Ŀ¼���ļ�
			this.createDirOnSD(path);
			File dir = new File(SDPATH + path);
			Log.d("System.out", "file dir exist is:" + dir.exists());
			file = this.createFileOnSD(path + fileName);

			// ʵ����һ�����������
			output = new FileOutputStream(file);
			// ����һ��4kb���ֽ����飬���ڴ�����������ÿ�ζ�ȡ4kb����
			byte[] buffer = new byte[4 * 1024];
			int len = 0;
			// ��ʼ���ֽ���
			while ((len = input.read(buffer)) != -1) {
				output.write(buffer, 0, len);
			}
			output.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return file;
	}

	/** ����д���ֵ��͵�I/O������ʽ */
	/**
	 * һ�����������ļ�
	 */
	public String read(String fileName) throws IOException {
		// reading input by lines
		BufferedReader bufferedReader = new BufferedReader(new FileReader(
				fileName));
		String line;
		StringBuffer sb = new StringBuffer();
		while ((line = bufferedReader.readLine()) != null) {
			sb.append(line + "\n");
		}

		bufferedReader.close();
		return sb.toString();
	}

	/**
	 * ���ڴ�����: ����ʹ��BufferedInputFile���ļ������ڴ棬Ȼ��ʹ�ö������ַ�������һ��
	 * StringReader��Ȼ�����readһ��һ���ض�ȡ�ַ����������̨
	 * 
	 * @param args
	 * @throws IOException
	 */
	public void memoryInput(String fileName) throws IOException {
		StringReader stringReader = new StringReader(this.read(fileName));
		int c;
		while ((c = stringReader.read()) != -1) {
			System.out.println((char) c);
		}
	}

	public static void main(String[] args) {
		// BufferInputFile bif = new BufferInputFile();
		// try {
		// FileUtils fu = new FileUtils();
		// fu.memoryInput("D:/AndroidWorkSpace/Socket/src/com/cnlaunch/server/BufferInputFile.java");
		//
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	public File getFile(String path) {
		if (this.isFileExist(path)) {
			return new File(SDPATH + path);
		}
		return null;

	}

	/**
	 * ���ֽ�Ϊ��λ��ȡ�ļ��������ڶ��������ļ�����ͼƬ��������Ӱ����ļ���
	 * 
	 * @param fileName
	 *            �ļ�����
	 */
	public static void readFileByBytes(String fileName) {
		File file = new File(fileName);
		InputStream in = null;
		try {
			System.out.println("���ֽ�Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�һ���ֽڣ�");
			// һ�ζ�һ���ֽ�
			in = new FileInputStream(file);
			int tempbyte;
			while ((tempbyte = in.read()) != -1) {
				System.out.write(tempbyte);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		try {
			System.out.println("���ֽ�Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�����ֽڣ�");

			// һ�ζ�����ֽ�
			byte[] tempbytes = new byte[100];
			int byteread = 0;
			in = new FileInputStream(fileName);
			// ReadFromFile.showAvailableBytes(in);
			// �������ֽڵ��ֽ������У�bytereadΪһ�ζ�����ֽ���
			while ((byteread = in.read(tempbytes)) != -1) {
				System.out.write(tempbytes, 0, byteread);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	/**
	 * ���ַ�Ϊ��λ��ȡ�ļ��������ڶ��ı������ֵ����͵��ļ�
	 * 
	 * @param fileName
	 *            �ļ���
	 */
	public static void readFileByChars(String fileName) {
		File file = new File(fileName);
		Reader reader = null;
		try {
			System.out.println("���ַ�Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�һ���ֽڣ�");
			// һ�ζ�һ���ַ�
			reader = new InputStreamReader(new FileInputStream(file));
			int tempchar;
			while ((tempchar = reader.read()) != -1) {
				// ����windows�£�rn�������ַ���һ��ʱ����ʾһ�����С�
				// ������������ַ��ֿ���ʾʱ���ỻ�����С�
				// ��ˣ����ε�r����������n�����򣬽������ܶ���С�
				if (((char) tempchar) != 'r') {
					System.out.print((char) tempchar);
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			System.out.println("���ַ�Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�����ֽڣ�");
			// һ�ζ�����ַ�
			char[] tempchars = new char[30];
			int charread = 0;
			reader = new InputStreamReader(new FileInputStream(fileName));
			// �������ַ����ַ������У�charreadΪһ�ζ�ȡ�ַ���
			while ((charread = reader.read(tempchars)) != -1) {
				// ͬ�����ε�r����ʾ
				if ((charread == tempchars.length)
						&& (tempchars[tempchars.length - 1] != 'r')) {
					System.out.print(tempchars);
				} else {
					for (int i = 0; i < charread; i++) {
						if (tempchars[i] == 'r') {
							continue;
						} else {
							System.out.print(tempchars[i]);
						}
					}
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	/**
	 * ����Ϊ��λ��ȡ�ļ��������ڶ������еĸ�ʽ���ļ�
	 * 
	 * @param fileName
	 *            �ļ���
	 */
	public static void readFileByLines(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			System.out.println("����Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�һ���У�");
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int line = 1;
			// һ�ζ���һ�У�ֱ������nullΪ�ļ�����
			while ((tempString = reader.readLine()) != null) {
				// ��ʾ�к�
				System.out.println("line " + line + ": " + tempString);
				line++;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	/**
	 * �����ȡ�ļ�����
	 * 
	 * @param fileName
	 *            �ļ���
	 */
	public static void readFileByRandomAccess(String fileName) {
		RandomAccessFile randomFile = null;
		try {
			System.out.println("�����ȡһ���ļ����ݣ�");
			// ��һ����������ļ�������ֻ����ʽ
			randomFile = new RandomAccessFile(fileName, "r");
			// �ļ����ȣ��ֽ���
			long fileLength = randomFile.length();
			// ���ļ�����ʼλ��
			int beginIndex = (fileLength > 4) ? 4 : 0;
			// �����ļ��Ŀ�ʼλ���Ƶ�beginIndexλ�á�
			randomFile.seek(beginIndex);
			byte[] bytes = new byte[10];
			int byteread = 0;
			// һ�ζ�10���ֽڣ�����ļ����ݲ���10���ֽڣ����ʣ�µ��ֽڡ�
			// ��һ�ζ�ȡ���ֽ�������byteread
			while ((byteread = randomFile.read(bytes)) != -1) {
				System.out.write(bytes, 0, byteread);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (randomFile != null) {
				try {
					randomFile.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	// /**
	// * ��ʾ�������л�ʣ���ֽ���
	// *
	// * @param in
	// */
	// private static void showAvailableBytes(InputStream in) {
	// try {
	// System.out.println("��ǰ�ֽ��������е��ֽ���Ϊ:" + in.available());
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

	// public static void main(String[] args) {
	// String fileName = "C:/temp/newTemp.txt";
	// ReadFromFile.readFileByBytes(fileName);
	// ReadFromFile.readFileByChars(fileName);
	// ReadFromFile.readFileByLines(fileName);
	// ReadFromFile.readFileByRandomAccess(fileName);
	// }

	// ����������׷�ӵ��ļ�β��

	/**
	 * ������׷�ӵ��ļ�β��
	 */
	/**
	 * A����׷���ļ���ʹ��RandomAccessFile
	 * 
	 * @param fileName
	 *            �ļ���
	 * @param content
	 *            ׷�ӵ�����
	 */
	public static void appendMethodA(String fileName,

	String content) {
		try {
			// ��һ����������ļ���������д��ʽ
			RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");
			// �ļ����ȣ��ֽ���
			long fileLength = randomFile.length();
			// ��д�ļ�ָ���Ƶ��ļ�β��
			randomFile.seek(fileLength);
			randomFile.writeBytes(content);
			randomFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * B����׷���ļ���ʹ��FileWriter
	 * 
	 * @param fileName
	 * @param content
	 */
	public static void appendMethodB(String fileName, String content) {
		try {
			// ��һ��д�ļ��������캯���еĵڶ�������true��ʾ��׷����ʽд�ļ�
			FileWriter writer = new FileWriter(fileName, true);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// /**
	// * short��ת��Ϊ�ֽ�����
	// *
	// * @param s
	// * @return
	// */
	// private static byte[] shortToByteArray(short s) {
	// byte[] shortBuf = new byte[2];
	// for (int i = 0; i < 2; i++) {
	// int offset = (shortBuf.length - 1 - i) * 8;
	// shortBuf[i] = (byte) ((s >>> offset) & 0xff);
	// }
	// return shortBuf;
	// }

	/**
	 * �ֽ�����ת��Ϊshort��
	 * 
	 * @param b
	 * @return
	 */
	public static final int byteArrayToShort(byte[] b) {
		return (b[0] << 8) + (b[1] & 0xFF);
	}

	/**
	 * int ת��Ϊ�ֽ�����
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] intToByteArray(int value) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			int offset = (b.length - 1 - i) * 8;
			b[i] = (byte) ((value >>> offset) & 0xFF);
		}
		return b;
	}

	/**
	 * �ֽ�����ת��Ϊint��
	 * 
	 * @param b
	 * @return
	 */
	public static final int byteArrayToInt(byte[] b) {
		return (b[0] << 24) + ((b[1] & 0xFF) << 16) + ((b[2] & 0xFF) << 8)
				+ (b[3] & 0xFF);
	}

	/**
	 * ��ȡSD���Ƿ����
	 * 
	 * @param
	 * @return File
	 */
	public static boolean sdCardIsMount() {

		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	/**
	 * ��ȡSD�����ļ�����
	 * 
	 * @param
	 * @return File
	 */
	public static File sdCardGetDirectoryFile() {

		if (FileUtils.sdCardIsMount()) {
			return Environment.getExternalStorageDirectory();
		}

		return null;

	}

	/**
	 * ��ȡSD��Ŀ¼��·��
	 * 
	 * @param
	 * @return String
	 */
	public static String sdCardGetDirectoryPath() {

		if (FileUtils.sdCardIsMount()) {
			return Environment.getExternalStorageDirectory().getPath();
		}

		return "";
	}

	/**
	 * ��ȡSD��block��size The size, in bytes, of a block on the file system
	 * 
	 * @param
	 * @return int
	 */
	public static int sdCardGetBlockSize() {

		if (FileUtils.sdCardIsMount()) {

			StatFs stat = new StatFs(FileUtils.sdCardGetDirectoryPath()); // ����StatFs����

			return stat.getBlockSize(); // ���block��size
		}

		return -1;
	}

	/**
	 * ��ȡSD��blocks��
	 * 
	 * @param
	 * @return int
	 */
	public static int sdCardGetBlockCount() {

		if (FileUtils.sdCardIsMount()) {

			StatFs stat = new StatFs(FileUtils.sdCardGetDirectoryPath()); // ����StatFs����

			return stat.getBlockCount(); // ���������
		}
		return -1;
	}

	/**
	 * ��ȡSD��Ӧ�ÿ���blocks����������ϵͳ�����Ŀռ䣩
	 * 
	 * @param
	 * @return int
	 */
	public static int sdCardGetAvailableBlocks() {

		if (FileUtils.sdCardIsMount()) {
			StatFs stat = new StatFs(FileUtils.sdCardGetDirectoryPath()); // ����StatFs����

			return stat.getAvailableBlocks(); // ���Ӧ�ÿ���blocks����������ϵͳ�����Ŀռ䣩
		}

		return -1;
	}

	/**
	 * ��ȡSD��ʣ��blocks��������ϵͳ�����Ŀռ䣩
	 * 
	 * @param
	 * @return int
	 */
	public static int sdCardGetFreeBlocks() {

		if (FileUtils.sdCardIsMount()) {
			StatFs stat = new StatFs(FileUtils.sdCardGetDirectoryPath()); // ����StatFs����

			return stat.getFreeBlocks(); // ���ʣ��ʣ��blocks��������ϵͳ�����Ŀռ䣩
		}

		return -1;
	}

	/**
	 * ��ȡSD����������MBΪ��λ��
	 * 
	 * @param
	 * @return int
	 */
	public static int sdCardGetSizeAsMB() {

		if (FileUtils.sdCardIsMount()) {
			StatFs stat = new StatFs(FileUtils.sdCardGetDirectoryPath()); // ����StatFs����
			int blockSize = stat.getBlockSize(); // ���block��size
			int totalBlocks = stat.getBlockCount(); // ���������

			return (blockSize * totalBlocks) / 1024 / 1024; //
		}

		return -1;

	}

	/**
	 * ��ȡSD�����ÿռ�ٷֱ�
	 * 
	 * @param
	 * @return int
	 */
	public static int sdCardAvailableSizePercent() {

		if (FileUtils.sdCardIsMount()) {
			StatFs stat = new StatFs(FileUtils.sdCardGetDirectoryPath()); // ����StatFs����

			return (stat.getAvailableBlocks() * 100) / stat.getBlockCount();
		}

		return -1;
	}

	/**
	 * ɾ���ļ�
	 * 
	 * @param
	 * @return
	 * 
	 */
	public static boolean DeleFile(String strFilePath) {

		File file = new File(strFilePath);

		return file.delete();

	}

	public static long GetFileSize(String strFilePath) {

		File file = new File(strFilePath);

		return file.length();
	}

}
