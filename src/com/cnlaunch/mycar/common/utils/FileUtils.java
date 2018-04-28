package com.cnlaunch.mycar.common.utils;

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
import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

/**
 * 文件操作工具类
 * 
 * @author xiangyuanmao
 * 
 */
public class FileUtils {

	private String SDPATH;
	/**
	 * 构造方法：初始化SD卡的根目录
	 */
	public FileUtils() {
		SDPATH = Environment.getExternalStorageDirectory().toString();
		Log.e("FileUtil","sd card 路径:"+SDPATH);
	}

	/**
	 * 通过文件路径的到文件
	 * @param path
	 * @return
	 */
	public File getFile(String path) {
		if (this.isFileExist(path)) {
			return new File(SDPATH + path);
		}
		return null;
	}
	
	/**
	 * 在SD卡上创建一个空文件
	 * 
	 * @param newFileName
	 * @return
	 */
	public File createFileOnSD(String newFileName) throws IOException {

		File dir = new File(SDPATH);
		if(!dir.exists())
			   dir.mkdirs();
		File file = new File(SDPATH + File.separator +newFileName);
		file.createNewFile();
		return file;

	}
	
	public static void deleteFileRecursively(File dir)
	{
		if(dir.isDirectory())
		{
			File[] sub = dir.listFiles();
			if(sub.length > 0)
			{
				int len = sub.length;
				for (int i = 0; i < sub.length; i++) {
					if(sub[i].isDirectory()){
						deleteFileRecursively(sub[i]);
					}else{
						sub[i].delete();
					}
				}
			}
			dir.delete();
		}
	}
	 
	/**
	 * 在SD卡上创建目录
	 * 
	 * @param dirName
	 * @return
	 * @throws IOException
	 */
	public File createDirOnSD(String dirName) throws IOException {
		File dir = new File(SDPATH + dirName);
		System.out.println("创建目录: "+SDPATH + dirName);
		boolean  ret = dir.mkdirs();
		if(ret){
			System.out.println("创建目录: "+SDPATH + dirName+"成功!");
		}else{
			System.out.println("创建目录: "+SDPATH + dirName+"失败!");
		}
		return dir;
	}

	/**
	 * 判断文件是否存在
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		return file.exists();
	}
	
	public interface FileDownloadListener{
		public void onFinished();
		public void onProgress(int percent);
	}
	/**
	 * 向SD卡里面写文件
	 * 
	 * @param path
	 * @param fileName
	 * @param input
	 * @return
	 */
	public File writeToSDFromInput(String path, 
			String fileName,
			InputStream input,
			long fileLen,
			FileDownloadListener listener) {
		// 声明一个文件对象和一个输出流对象
		File file = null;
		// file.list();
		OutputStream output = null;

		try {
			// 创建目录和文件
			this.createDirOnSD(path);
			File dir = new File(SDPATH + path);
			Log.d("System.out", "file dir exist is:" + dir.exists());
			file = this.createFileOnSD(path + fileName);

			// 实例化一个输出流对象
			output = new FileOutputStream(file);
			// 定义一个4kb的字节数组，用于从输入流里面每次读取4kb内容
			byte[] buffer = new byte[4 * 1024];
			int len = 0;
			int percent = 0;
			int currentBytes = 0;
			// 开始读字节流
			while ((len = input.read(buffer)) != -1) {
				currentBytes += len;
				percent = (int)(100 * currentBytes / fileLen);
				output.write(buffer, 0, len);
				listener.onProgress(percent);
			}
			output.flush();
			listener.onFinished();
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

	/** 下面写几种典型的I/O操作方式 */
	/**
	 * 一、缓冲输入文件
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
	 * 从内存输入: 首先使用BufferedInputFile把文件读到内存，然后使用读到的字符串构造一个
	 * StringReader。然后调用read一个一个地读取字符输出到控制台
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
		BufferInputFile bif = new BufferInputFile();
		try {
			FileUtils fu = new FileUtils();
			fu.memoryInput("D:/AndroidWorkSpace/Socket/src/com/cnlaunch/server/BufferInputFile.java");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * 以字节为单位读取文件，常用于读二进制文件，如图片、声音、影像等文件。
	 * 
	 * @param fileName
	 *            文件的名
	 */
	public static void readFileByBytes(String fileName) {
		File file = new File(fileName);
		InputStream in = null;
		try {
			System.out.println("以字节为单位读取文件内容，一次读一个字节：");
			// 一次读一个字节
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
			System.out.println("以字节为单位读取文件内容，一次读多个字节：");

			// 一次读多个字节
			byte[] tempbytes = new byte[100];
			int byteread = 0;
			in = new FileInputStream(fileName);
			// ReadFromFile.showAvailableBytes(in);
			// 读入多个字节到字节数组中，byteread为一次读入的字节数
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
	 * 以字符为单位读取文件，常用于读文本，数字等类型的文件
	 * 
	 * @param fileName
	 *            文件名
	 */
	public static void readFileByChars(String fileName) {
		File file = new File(fileName);
		Reader reader = null;
		try {
			System.out.println("以字符为单位读取文件内容，一次读一个字节：");
			// 一次读一个字符
			reader = new InputStreamReader(new FileInputStream(file));
			int tempchar;
			while ((tempchar = reader.read()) != -1) {
				// 对于windows下，rn这两个字符在一起时，表示一个换行。
				// 但如果这两个字符分开显示时，会换两次行。
				// 因此，屏蔽掉r，或者屏蔽n。否则，将会多出很多空行。
				if (((char) tempchar) != 'r') {
					System.out.print((char) tempchar);
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			System.out.println("以字符为单位读取文件内容，一次读多个字节：");
			// 一次读多个字符
			char[] tempchars = new char[30];
			int charread = 0;
			reader = new InputStreamReader(new FileInputStream(fileName));
			// 读入多个字符到字符数组中，charread为一次读取字符数
			while ((charread = reader.read(tempchars)) != -1) {
				// 同样屏蔽掉r不显示
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
	 * 以行为单位读取文件，常用于读面向行的格式化文件
	 * 
	 * @param fileName
	 *            文件名
	 */
	public static void readFileByLines(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			System.out.println("以行为单位读取文件内容，一次读一整行：");
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int line = 1;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				// 显示行号
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
	 * 随机读取文件内容
	 * 
	 * @param fileName
	 *            文件名
	 */
	public static void readFileByRandomAccess(String fileName) {
		RandomAccessFile randomFile = null;
		try {
			System.out.println("随机读取一段文件内容：");
			// 打开一个随机访问文件流，按只读方式
			randomFile = new RandomAccessFile(fileName, "r");
			// 文件长度，字节数
			long fileLength = randomFile.length();
			// 读文件的起始位置
			int beginIndex = (fileLength > 4) ? 4 : 0;
			// 将读文件的开始位置移到beginIndex位置。
			randomFile.seek(beginIndex);
			byte[] bytes = new byte[10];
			int byteread = 0;
			// 一次读10个字节，如果文件内容不足10个字节，则读剩下的字节。
			// 将一次读取的字节数赋给byteread
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


	/**
	 * 将内容追加到文件尾部
	 */
	/**
	 * A方法追加文件：使用RandomAccessFile
	 * 
	 * @param fileName
	 *            文件名
	 * @param content
	 *            追加的内容
	 */
	public static void appendMethodA(String fileName,

	String content) {
		try {
			// 打开一个随机访问文件流，按读写方式
			RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");
			// 文件长度，字节数
			long fileLength = randomFile.length();
			// 将写文件指针移到文件尾。
			randomFile.seek(fileLength);
			randomFile.writeBytes(content);
			randomFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * B方法追加文件：使用FileWriter
	 * 
	 * @param fileName
	 * @param content
	 */
	public static void appendMethodB(String fileName, String content) {
		try {
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			FileWriter writer = new FileWriter(fileName, true);
			writer.write(content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	/**
	 * 字节数组转换为short型
	 * 
	 * @param b
	 * @return
	 */
	public static final int byteArrayToShort(byte[] b) {
		return (b[0] << 8) + (b[1] & 0xFF);
	}

	/**
	 * int 转换为字节数组
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
	 * 字节数组转换为int型
	 * 
	 * @param b
	 * @return
	 */
	public static final int byteArrayToInt(byte[] b) {
		return (b[0] << 24) + ((b[1] & 0xFF) << 16) + ((b[2] & 0xFF) << 8)
				+ (b[3] & 0xFF);
	}
	
	/**
	 * 获取SD卡是否挂载
	 * 
	 * @param
	 * @return File
	 */
	public static boolean sdCardIsMount() {

		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	/**
	 * 获取SD卡的文件对象
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
	 * 获取SD卡目录的路径
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
	 * 获取SD卡block的size
	 * The size, in bytes, of a block on the file system
	 * 
	 * @param
	 * @return int
	 */
	public static int sdCardGetBlockSize() {

		if (FileUtils.sdCardIsMount()) {

			StatFs stat = new StatFs(FileUtils.sdCardGetDirectoryPath()); // 创建StatFs对象

			return stat.getBlockSize(); // 获得block的size
		}

		return -1;
	}

	/**
	 * 获取SD卡blocks数
	 * 
	 * @param
	 * @return int
	 */
	public static int sdCardGetBlockCount() {

		if (FileUtils.sdCardIsMount()) {

			StatFs stat = new StatFs(FileUtils.sdCardGetDirectoryPath()); // 创建StatFs对象

			return stat.getBlockCount(); // 获得总容量
		}
		return -1;
	}

	/**
	 * 获取SD卡应用可用blocks数（不包含系统保留的空间）
	 * 
	 * @param
	 * @return int
	 */
	public static int sdCardGetAvailableBlocks() {

		if (FileUtils.sdCardIsMount()) {
			StatFs stat = new StatFs(FileUtils.sdCardGetDirectoryPath()); // 创建StatFs对象

			return stat.getAvailableBlocks(); // 获得应用可用blocks数（不包含系统保留的空间）
		}

		return -1;
	}

	/**
	 * 获取SD卡剩余blocks数（包含系统保留的空间）
	 * 
	 * @param
	 * @return int
	 */
	public static int sdCardGetFreeBlocks() {

		if (FileUtils.sdCardIsMount()) {
			StatFs stat = new StatFs(FileUtils.sdCardGetDirectoryPath()); // 创建StatFs对象

			return stat.getFreeBlocks(); // 获得剩余剩余blocks数（包含系统保留的空间）
		}

		return -1;
	}

	/**
	 * 获取SD卡容量（以MB为单位）
	 * 
	 * @param
	 * @return int
	 */
	public static int sdCardGetSizeAsMB() {

		if (FileUtils.sdCardIsMount()) {
			StatFs stat = new StatFs(FileUtils.sdCardGetDirectoryPath()); // 创建StatFs对象
			int blockSize = stat.getBlockSize(); // 获得block的size
			int totalBlocks = stat.getBlockCount(); // 获得总容量

			return (blockSize * totalBlocks) / 1024 / 1024; //
		}

		return -1;

	}

	/**
	 * 获取SD卡可用空间百分比
	 * 
	 * @param
	 * @return int
	 */
	public static int sdCardAvailableSizePercent() {

		if (FileUtils.sdCardIsMount()) {
			StatFs stat = new StatFs(FileUtils.sdCardGetDirectoryPath()); // 创建StatFs对象

			return (stat.getAvailableBlocks() * 100) / stat.getBlockCount();
		}

		return -1;

	}


	public void sort(File[] fileList, int type)
	{
	    Arrays.sort(fileList, new FileSorter(type));
	}
    public class FileSorter implements Comparator<File>{
        /**默认排序的方式， 按目录，文件排序TYPE_DIR*/
        public static final int TYPE_DEFAULT             = -1;
        /**按修改时间，降序*/
        public static final int TYPE_MODIFIED_DATE_DOWN = 1;
        /**按修改时间，升序*/
        public static final int TYPE_MODIFIED_DATE_UP    = 2;
        /**按文件大小，降序*/
        public static final int TYPE_SIZE_DOWN            = 3;
        /**按文件大小，升序*/
        public static final int TYPE_SIZE_UP            = 4;
    /*  public static final int TYPE_NAME_DOWN            = 5;
        public static final int TYPE_NAME_UP            = 6;*/
        /**按文件名*/
        public static final int TYPE_NAME                 = 5;
        /**按目录，文件排序*/
        public static final int TYPE_DIR                = 7;
        
        private int mType = -1;
        
        public FileSorter(int type) {
            if (type < 0 || type > 7) {
                type = TYPE_DIR;
            }
            mType = type;
        }
        @Override
        public int compare(File object1, File object2) {
            
            int result = 0;
            
            switch (mType) {
            
            case TYPE_MODIFIED_DATE_DOWN://last modified date down
                result = compareByModifiedDateDown(object1, object2);
                break;
                
            case TYPE_MODIFIED_DATE_UP://last modified date up
                result = compareByModifiedDateUp(object1, object2);
                break;
                
            case TYPE_SIZE_DOWN:    // file size down
                result = compareBySizeDown(object1, object2);
                break;
                
            case TYPE_SIZE_UP:        //file size up
                result = compareBySizeUp(object1, object2);
                break;
                
            case TYPE_NAME:            //name 
                result = compareByName(object1, object2);
                break;
                
            case TYPE_DIR:            //dir or file
                result = compareByDir(object1, object2);
                break;
            default:
                result = compareByDir(object1, object2);
                break;
            }
            return result;
        }
        private int compareByModifiedDateDown(File object1, File object2) {
            
            long d1 = object1.lastModified();
            long d2 = object2.lastModified();
            
            if (d1 == d2){
                return 0;
            } else {
                return d1 < d2 ? 1 : -1;
            }
        }
        private int compareByModifiedDateUp(File object1, File object2) {
            
            long d1 = object1.lastModified();
            long d2 = object2.lastModified();
            
            if (d1 == d2){
                return 0;
            } else {
                return d1 > d2 ? 1 : -1;
            }
        }
        private int compareBySizeDown(File object1, File object2) {
            
            if (object1.isDirectory() && object2.isDirectory()) {
                return 0;
            }
            if (object1.isDirectory() && object2.isFile()) {
                return -1;
            }
            if (object1.isFile() && object2.isDirectory()) {
                return 1;
            }
            long s1 = object1.length();
            long s2 = object2.length();
            
            if (s1 == s2){
                return 0;
            } else {
                return s1 < s2 ? 1 : -1;
            }
        }
        private int compareBySizeUp(File object1, File object2) {
            
            if (object1.isDirectory() && object2.isDirectory()) {
                return 0;
            }
            if (object1.isDirectory() && object2.isFile()) {
                return -1;
            }
            if (object1.isFile() && object2.isDirectory()) {
                return 1;
            }
            
            long s1 = object1.length();
            long s2 = object2.length();
            
            if (s1 == s2){
                return 0;
            } else {
                return s1 > s2 ? 1 : -1;
            }
        }
        private int compareByName(File object1, File object2) {
            
            Comparator<Object> cmp = Collator.getInstance(java.util.Locale.CHINA);
            
            return cmp.compare(object1.getName(), object2.getName());
        }
        private int compareByDir(File object1, File object2) {
            
            if (object1.isDirectory() && object2.isFile()) {
                return -1;
            } else if (object1.isDirectory() && object2.isDirectory()) {
                return compareByName(object1, object2);
            } else if (object1.isFile() && object2.isDirectory()) {
                return 1;
            } else {  //object1.isFile() && object2.isFile()) 
                return compareByName(object1, object2);
            }
        }
    }
}
