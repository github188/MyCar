#include "com_cnlaunch_mycar_jni_JniX431File.h"
#include "lsx.h"
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <malloc.h>

#ifdef WIN32
#else
#ifdef __linux
#else
#include <android/log.h>
#endif
#endif

#pragma warning (disable : 4311 4312 4313 4996)

#ifdef __cplusplus   
extern "C"  
{   
#endif  

    //带GBK编码的字符串拷贝，防止截取半个中文字符的情况发生   
    //形参表的含义同strncpy函数   
    void strncpy2(char *pDst,char *pSrc,int nDstLen)   
    {   
        int nCount=0;   

        int i = 0;

        //pDst缓存足够容纳pSrc中的数据   
        if((int)strlen(pSrc)<nDstLen)   
        {   
            strncpy(pDst,pSrc,nDstLen);   
        }   

        //pDst Buffer 's size must be greater than or equal 2!   
        if(nDstLen<2)   
            return;   

        memset(pDst,0,nDstLen);   


        for( ; i<nDstLen; i++)   
        {   
            if(pSrc[i]&0x80)   
                nCount++;   
        }   

        if(nCount%2==0&&pSrc[nDstLen-1]&0x80)   
            strncpy(pDst,pSrc,nDstLen-2);   
        else  
            strncpy(pDst,pSrc,nDstLen-1);   
    }   



    jstring CharTojstring(JNIEnv* env, char* str) 
    { 
        jstring rtn = 0; 
        jsize len;
        jclass clsstring;
        jstring strencode;
        jmethodID mid;
        jbyteArray barr;

        if ( 0 == str )
        {
            // jstring对象为空是返回0，不能返回""
            // 不要想当然的把jstring等同为C++的string
            return (jstring)0;
        }

        len = (int)strlen(str); 

        if ( 0 == len )
        {
            // jstring对象为空是返回0，不能返回""
            // 不要想当然的把jstring等同为C++的string
            return (jstring)0;
        }



        clsstring = (*env)->FindClass(env, "java/lang/String"); 

        //new   encode   string   default   "GBK " 
        strencode = (*env)->NewStringUTF(env, "GBK"); 
        mid = (*env)->GetMethodID(env, clsstring, "<init>", "([BLjava/lang/String;)V"); 
        barr = (*env)-> NewByteArray(env, len); 

        (*env)->SetByteArrayRegion(env, barr, 0, len, (jbyte*)str); 

        //call   new   String(byte[]   b,String   encode) 
        rtn = (jstring)(*env)->NewObject(env, clsstring, mid, barr, strencode); 

        return rtn; 
    } 

    char* jstringToChar(JNIEnv* env, jstring jstr)
    {
        char* rtn = NULL; 
        jclass clsstring = (*env)->FindClass(env, "java/lang/String"); 

        //new   encode   string   default   "GBK " 
        jstring strencode = (*env)->NewStringUTF(env, "GBK"); 
        jmethodID mid = (*env)->GetMethodID(env, clsstring, "getBytes", "(Ljava/lang/String;)[B"); 

        //call   String.getBytes   method   to   avoid   incompatible   migrating   into   solaris 
        jbyteArray barr= (jbyteArray)(*env)->CallObjectMethod(env, jstr, mid, strencode); 

        jsize alen = (*env)->GetArrayLength(env, barr); 
        jbyte* ba = (*env)-> GetByteArrayElements(env, barr, JNI_FALSE); 

        if ( alen > 0 ) 
        { 
            rtn = (char*)malloc(alen+1);         //new   char[alen+1]; 
            memcpy(rtn, ba, alen); 
            rtn[alen] = 0; 
        } 
        (*env)->ReleaseByteArrayElements(env, barr, ba, 0); 

        return   rtn; 
    } 

    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_init
    * Signature: ()I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1init
        (JNIEnv *env, jobject obj)
    {
#ifdef WIN32
        printf("JniX431FileTest, lsx_init:\n");
#else
#ifdef __linux
        printf("JniX431FileTest, lsx_init:\n");
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_init:");
#endif
#endif


        return (jint)lsx_init();
    }


    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_deinit
    * Signature: (I)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1deinit
        (JNIEnv *env, jobject obj, jint hlsx)
    {   
#ifdef WIN32
        printf("JniX431FileTest, lsx_deinit:\n");
#else
#ifdef __linux
        printf("JniX431FileTest, lsx_deinit:\n");
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_deinit:");
#endif
#endif
        return (jint)lsx_deinit((HLSX)hlsx);
    }


    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_open
    * Signature: (ILcom/cnlaunch/mycar/jni/X431String;ILcom/cnlaunch/mycar/jni/X431Integer;)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1open
        (JNIEnv *env, jobject obj, jint hlsx, jobject filename, jint mode, jobject error)
    {
        jclass clsstr;
        jfieldID  fid_strValue;
        jstring jstrfilename;
        char* pfilename;
        int len;
        jclass cls;
        jfieldID  fid_mValue;
        int iError;
        LSX_FILE lsxfile;

        if ( 0 == hlsx )
        {

#ifdef WIN32
            printf("lsx_open: parameter[hlsx] invalid\n");
#else
#ifdef __linux
            printf("lsx_open: parameter[hlsx] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_open: parameter[hlsx] invalid");
#endif
#endif
            return (jint)-1;
        }

        // 参数转换
        clsstr = (*env)->FindClass(env, "com/cnlaunch/mycar/jni/X431String");      
        if(clsstr == 0)
        {    
#ifdef WIN32
            printf("lsx_open: [FindClass->X431String] error\n");
#else
#ifdef __linux
            printf("lsx_open: [FindClass->X431String] error\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_open: [FindClass->X431String] error");
#endif
#endif
            return (jint)-2; 
        }    

        fid_strValue = (*env)->GetFieldID(env, clsstr, "mValue","Ljava/lang/String;"); 

        jstrfilename = (*env)->GetObjectField(env, filename, fid_strValue);    

        pfilename = jstringToChar(env, jstrfilename);
        len = (int)strlen(pfilename);

        // x431文件路径长度，至少7个字符(/+$path+.x431)，至多不超过256个字符
        if ( pfilename == 0 || len < 7 || len > 256 )
        {
#ifdef WIN32
            printf("lsx_open: parameter[filename] invalid\n");
#else
#ifdef __linux
            printf("lsx_open: parameter[filename] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_open: parameter[filename] invalid");
#endif
#endif

            return (jint)-1;
        }

        // 从error对象获取值
        cls = (*env)->FindClass(env, "com/cnlaunch/mycar/jni/X431Integer");      
        if(cls == 0)
        {    
#ifdef WIN32
            printf("lsx_open: [FindClass->X431Integer] error\n");
#else
#ifdef __linux
            printf("lsx_open: [FindClass->X431Integer] error\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_open: [FindClass->X431Integer] error");
#endif
#endif
            return (jint)-2; 
        }    

        // 获取error对象mValue字段值
        fid_mValue = (*env)->GetFieldID(env, cls, "mValue","I"); 
        iError = (int)(*env)->GetObjectField(env, error, fid_mValue);     

#ifdef WIN32
        printf("lsx_open: hlsx=[%d], arr=[%s], len=[%d], mode=[%d], iError=[%d]\n",
            (int)hlsx, pfilename, (int)len, (int)mode, iError);
#else
#ifdef __linux
        printf("lsx_open: hlsx=[%d], arr=[%s], len=[%d], mode=[%d], iError=[%d]\n",
            (int)hlsx, pfilename, (int)len, (int)mode, iError);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", 
            "lsx_open: hlsx=[%d], arr=[%s], len=[%d], mode=[%d], iError=[%d]",
            (int)hlsx, pfilename, (int)len, (int)mode, iError);    
#endif
#endif


        lsxfile = lsx_open((HLSX)hlsx, pfilename, (int)mode, &iError);

        // 设置error对象mValue字段值的值
        (*env)->SetIntField(env, error, fid_mValue, iError);

#ifdef WIN32
        printf("lsx_open: lsxfile=[%d]\n", (int)lsxfile);
#else
#ifdef __linux
        printf("lsx_open: lsxfile=[%d]\n", (int)lsxfile);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_open: lsxfile=[%d]", lsxfile);        
#endif
#endif

        free(pfilename);

        return (jint)lsxfile;
    }    


    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_close
    * Signature: (I)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1close
        (JNIEnv *env, jobject obj, jint lsx_file)
    {
        int iRet = lsx_close((LSX_FILE)lsx_file);

#ifdef WIN32
        printf("lsx_close: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_close: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_close: iRet=[%d]", iRet);
#endif
#endif

        return iRet;
    }


    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_checkfile
    * Signature: (Lcom/cnlaunch/mycar/jni/X431String;)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1checkfile
        (JNIEnv *env, jobject obj, jobject filepath)
    {
        jfieldID  fid_mValue;
        jstring jstrfilename;
        char* pfilename;
        int len;
        unsigned int uiRet;

        // 参数转换
        jclass clsstr = (*env)->FindClass(env, "com/cnlaunch/mycar/jni/X431String");      
        if(clsstr == 0)
        {    
#ifdef WIN32
            printf("lsx_checkfile: [FindClass->X431String] error\n");
#else
#ifdef __linux
            printf("lsx_checkfile: [FindClass->X431String] error\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_checkfile: [FindClass->X431String] error");
#endif
#endif
            return (jint)-2; 
        }    

        fid_mValue = (*env)->GetFieldID(env, clsstr, "mValue","Ljava/lang/String;"); 

        jstrfilename = (*env)->GetObjectField(env, filepath, fid_mValue);    

        pfilename = jstringToChar(env, jstrfilename);
        len = (int)strlen(pfilename);

        // x431文件路径长度，至少7个字符(/+$path+.x431)，至多不超过256个字符
        if ( pfilename == 0 || len < 7 || len > 256 )
        {
#ifdef WIN32
            printf("lsx_checkfile: parameter[filename] invalid\n");
#else
#ifdef __linux
            printf("lsx_checkfile: parameter[filename] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_checkfile: parameter[filename] invalid");
#endif
#endif

            return (jint)-1;
        }        

        uiRet = lsx_checkfile(pfilename);

#ifdef WIN32
        printf("lsx_checkfile: uiRet=[%d]\n", uiRet);
#else
#ifdef __linux
        printf("lsx_checkfile: uiRet=[%d]\n", uiRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_checkfile: uiRet=[%d]", uiRet);
#endif
#endif

        free(pfilename);

        return (jint)uiRet;
    }


    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_read_baseinfo
    * Signature: (ILcom/cnlaunch/mycar/jni/LSX_BASEINFO;)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1read_1baseinfo
        (JNIEnv *env, jobject obj, jint lsx_file, jobject baseinfo)
    {
        LSX_BASEINFO  bi;
        jclass    cls;
        jfieldID  fid_serialno;
        jfieldID  fid_productid;
        jfieldID  fid_codepage;
        jfieldID  fid_langname;
        jfieldID  fid_langcode;
        jfieldID  fid_langcode_en;
        jfieldID  fid_diagversion;
        jfieldID  fid_creationtime;

        jstring jstr_serialno;
        jshort jshort_productid;
        jint jint_codepage;
        jstring jstr_langname;
        jstring jstr_langcode;
        jstring jstr_langcode_en;
        jstring jstr_diagversion;
        jstring jstr_creationtime;
        int iRet;

        if ( 0 == lsx_file )
        {
#ifdef WIN32
            printf("lsx_read_baseinfo: parameter[lsx_file] invalid\n");
#else
#ifdef __linux
            printf("lsx_read_baseinfo: parameter[lsx_file] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_read_baseinfo: parameter[lsx_file] invalid");
#endif
#endif

            return (jint)-1;
        }


        iRet = lsx_read_baseinfo((LSX_FILE)lsx_file, &bi);

#ifdef WIN32
        printf("lsx_read_baseinfo: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_read_baseinfo: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_read_baseinfo: iRet=[%d]", iRet);
#endif
#endif

        // 设置java的LSX_BASEINFO对象
        cls   = (*env)->FindClass(env, "com/cnlaunch/mycar/jni/LSX_BASEINFO");           
        //         jmethodID m_mid   = (*env)->GetMethodID(env, cls,"<init>","()V");
        //         jobject   m_obj   = (*env)->NewObject(env, cls,m_mid);   

        fid_serialno = (*env)->GetFieldID(env, cls, "serialno", "Ljava/lang/String;");           
        fid_productid = (*env)->GetFieldID(env, cls, "productid", "S");   
        fid_codepage = (*env)->GetFieldID(env, cls, "codepage", "I");
        fid_langname = (*env)->GetFieldID(env, cls, "langname", "Ljava/lang/String;");
        fid_langcode = (*env)->GetFieldID(env, cls, "langcode", "Ljava/lang/String;");
        fid_langcode_en = (*env)->GetFieldID(env, cls, "langcode_en", "Ljava/lang/String;");
        fid_diagversion = (*env)->GetFieldID(env, cls, "diagversion", "Ljava/lang/String;");
        fid_creationtime = (*env)->GetFieldID(env, cls, "creationtime", "Ljava/lang/String;");


        jstr_serialno = CharTojstring(env, bi.serialno);
        jshort_productid = (jshort)bi.productid;
        jint_codepage = (jint)bi.codepage;
        jstr_langname = CharTojstring(env, bi.langname);
        jstr_langcode = CharTojstring(env, bi.langcode);
        jstr_langcode_en = CharTojstring(env, bi.langcode_en);
        jstr_diagversion = CharTojstring(env, bi.diagversion);
        jstr_creationtime = CharTojstring(env, bi.creationtime);


#ifdef WIN32
        printf("lsx_read_baseinfo: bi.serialno=[%s], bi.productid=[%d], bi.codepage=[%d], bi.langname=[%s], " 
            "bi.langcode=[%s], bi.langcode_en=[%s], bi.diagversion=[%s], bi.creationtime=[%s]\n", 
            bi.serialno, bi.productid, bi.codepage, bi.langname, bi.langcode, bi.langcode_en, bi.diagversion, bi.creationtime);
#else
#ifdef __linux
        printf("lsx_read_baseinfo: bi.serialno=[%s], bi.productid=[%d], bi.codepage=[%d], bi.langname=[%s], " 
            "bi.langcode=[%s], bi.langcode_en=[%s], bi.diagversion=[%s], bi.creationtime=[%s]\n", 
            bi.serialno, bi.productid, bi.codepage, bi.langname, bi.langcode, bi.langcode_en, bi.diagversion, bi.creationtime);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest",
            "lsx_read_baseinfo: bi.serialno=[%s], bi.productid=[%d], bi.codepage=[%d], bi.langname=[%s], " 
            "bi.langcode=[%s], bi.langcode_en=[%s], bi.diagversion=[%s], bi.creationtime=[%s]", 
            bi.serialno, bi.productid, bi.codepage, bi.langname, bi.langcode, bi.langcode_en, bi.diagversion, bi.creationtime);
#endif
#endif


        (*env)->SetObjectField(env, baseinfo, fid_serialno, jstr_serialno);   
        (*env)->SetShortField(env, baseinfo, fid_productid, jshort_productid);   
        (*env)->SetIntField(env, baseinfo, fid_codepage, jint_codepage);   
        (*env)->SetObjectField(env, baseinfo, fid_langname, jstr_langname);   
        (*env)->SetObjectField(env, baseinfo, fid_langcode, jstr_langcode);   
        (*env)->SetObjectField(env, baseinfo, fid_langcode_en, jstr_langcode_en);   
        (*env)->SetObjectField(env, baseinfo, fid_diagversion, jstr_diagversion);   
        (*env)->SetObjectField(env, baseinfo, fid_creationtime, jstr_creationtime);   

        return iRet;
    }


    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_write_baseinfo
    * Signature: (ILcom/cnlaunch/mycar/jni/LSX_BASEINFO;)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1write_1baseinfo
        (JNIEnv *env, jobject obj, jint lsx_file, jobject baseinfo)
    {
        jclass    cls; 

        jfieldID  fid_serialno;           
        jfieldID  fid_productid;
        jfieldID  fid_codepage ;
        jfieldID  fid_langname;
        jfieldID  fid_langcode;
        jfieldID  fid_langcode_en;
        jfieldID  fid_diagversion;
        jfieldID  fid_creationtime;

        jstring jstr_serialno;
        jshort  jshort_productid;
        jint  jint_codepage;
        jstring jstr_langname;
        jstring jstr_langcode;
        jstring jstr_langcode_en;
        jstring jstr_diagversion;
        jstring jstr_creationtime;

        char* serialno;
        char* langname;
        char* langcode;
        char* langcode_en;
        char* diagversion;
        char* creationtime;

        LSX_BASEINFO bi;

        int iRet;


        if ( 0 == lsx_file )
        {
#ifdef WIN32
            printf("lsx_write_baseinfo: parameter[lsx_file] invalid\n");
#else
#ifdef __linux
            printf("lsx_write_baseinfo: parameter[lsx_file] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_write_baseinfo: parameter[lsx_file] invalid");
#endif
#endif
            return (jint)-1;
        }

        // 从baseinfo对象获取参数值
        cls   = (*env)->FindClass(env, "com/cnlaunch/mycar/jni/LSX_BASEINFO");           

        fid_serialno = (*env)->GetFieldID(env, cls, "serialno", "Ljava/lang/String;");           
        fid_productid = (*env)->GetFieldID(env, cls, "productid", "S");   
        fid_codepage = (*env)->GetFieldID(env, cls, "codepage", "I");
        fid_langname = (*env)->GetFieldID(env, cls, "langname", "Ljava/lang/String;");
        fid_langcode = (*env)->GetFieldID(env, cls, "langcode", "Ljava/lang/String;");
        fid_langcode_en = (*env)->GetFieldID(env, cls, "langcode_en", "Ljava/lang/String;");
        fid_diagversion = (*env)->GetFieldID(env, cls, "diagversion", "Ljava/lang/String;");
        fid_creationtime = (*env)->GetFieldID(env, cls, "creationtime", "Ljava/lang/String;");

        jstr_serialno = (*env)->GetObjectField(env, baseinfo, fid_serialno);
        jshort_productid = (*env)->GetShortField(env, baseinfo, fid_productid);
        jint_codepage = (*env)->GetIntField(env, baseinfo, fid_codepage);
        jstr_langname = (*env)->GetObjectField(env, baseinfo, fid_langname);
        jstr_langcode = (*env)->GetObjectField(env, baseinfo, fid_langcode);
        jstr_langcode_en = (*env)->GetObjectField(env, baseinfo, fid_langcode_en);
        jstr_diagversion = (*env)->GetObjectField(env, baseinfo, fid_diagversion);
        jstr_creationtime = (*env)->GetObjectField(env, baseinfo, fid_creationtime);

        serialno = jstringToChar(env, jstr_serialno);
        langname = jstringToChar(env, jstr_langname);
        langcode = jstringToChar(env, jstr_langcode);
        langcode_en = jstringToChar(env, jstr_langcode_en);
        diagversion = jstringToChar(env, jstr_diagversion);
        creationtime = jstringToChar(env, jstr_creationtime);        

#ifdef WIN32
        printf("lsx_write_baseinfo: "
            "productid=[%d], codepage=[%d], langname=[%s]," 
            "langcode=[%s], langcode_en=[%s], diagversion=[%s],"
            "serialno=[%s]\n", 
            jshort_productid, jint_codepage, langname,
            langcode, langcode_en, diagversion,
            serialno);
#else
#ifdef __linux
        printf("lsx_write_baseinfo: "
            "productid=[%d], codepage=[%d], langname=[%s]," 
            "langcode=[%s], langcode_en=[%s], diagversion=[%s],"
            "serialno=[%s]\n", 
            jshort_productid, jint_codepage, langname,
            langcode, langcode_en, diagversion,
            serialno);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_write_baseinfo: "
            "productid=[%d], codepage=[%d], langname=[%s]," 
            "langcode=[%s], langcode_en=[%s], diagversion=[%s],"
            "serialno=[%s]", 
            jshort_productid, jint_codepage, langname,
            langcode, langcode_en, diagversion,
            serialno);
#endif
#endif



        //        memcpy(bi.serialno, serialno, strlen(serialno));  // GBK字符串直接用strcpy或memcpy到char数组都会崩溃，不能这么用。
        bi.productid = (unsigned short)jshort_productid;
        bi.codepage = (unsigned short)jint_codepage;
        bi.diagversion = diagversion;

        strncpy2(bi.serialno, serialno, 20);
        strncpy2(bi.langname, langname, 30);
        strncpy2(bi.langcode, langcode, 4);
        strncpy2(bi.langcode_en, langcode_en, 4);        

#ifdef WIN32
        printf("lsx_write_baseinfo: "
            "bi.productid=[%d], bi.codepage=[%d], bi.langname=[%s]," 
            "bi.langcode=[%s], bi.langcode_en=[%s], bi.diagversion=[%s],"
            "bi.serialno=[%s]\n",
            bi.productid, bi.codepage, bi.langname,
            bi.langcode, bi.langcode_en, bi.diagversion,
            bi.serialno);
#else
#ifdef __linux
        printf("lsx_write_baseinfo: "
            "bi.productid=[%d], bi.codepage=[%d], bi.langname=[%s]," 
            "bi.langcode=[%s], bi.langcode_en=[%s], bi.diagversion=[%s],"
            "bi.serialno=[%s]\n",
            bi.productid, bi.codepage, bi.langname,
            bi.langcode, bi.langcode_en, bi.diagversion,
            bi.serialno);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_write_baseinfo: "
            "bi.productid=[%d], bi.codepage=[%d], bi.langname=[%s]," 
            "bi.langcode=[%s], bi.langcode_en=[%s], bi.diagversion=[%s],"
            "bi.serialno=[%s]",
            bi.productid, bi.codepage, bi.langname,
            bi.langcode, bi.langcode_en, bi.diagversion,
            bi.serialno);
#endif
#endif


        iRet = lsx_write_baseinfo((LSX_FILE)lsx_file, &bi);

#ifdef WIN32
        printf("lsx_write_baseinfo: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_write_baseinfo: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_write_baseinfo: iRet=[%d]", iRet);
#endif
#endif

        free(serialno);
        free(langname);
        free(langcode);
        free(langcode_en);
        free(diagversion);
        free(creationtime);

        return iRet;
    }


    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_read_autoinfo
    * Signature: (ILcom/cnlaunch/mycar/jni/LSX_AUTOINFO;)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1read_1autoinfo
        (JNIEnv *env, jobject obj, jint lsx_file, jobject autoinfo)
    {
        jclass    cls;     

        jfieldID  fid_vin; 
        jfieldID  fid_make;
        jfieldID  fid_model;
        jfieldID  fid_year;
        jfieldID  fid_madein;
        jfieldID  fid_chassis;
        jfieldID  fid_enginemodel;
        jfieldID  fid_displacement;

        jstring jstr_vin;
        jstring jstr_make;
        jstring jstr_model;
        jstring jstr_year;
        jstring jstr_madein;
        jstring jstr_chassis;
        jstring jstr_enginemodel;
        jstring jstr_displacement;

        LSX_AUTOINFO ai;

        int iRet;


        if ( 0 == lsx_file )
        {
#ifdef WIN32
            printf("lsx_read_autoinfo: parameter[lsx_file] invalid\n");
#else
#ifdef __linux
            printf("lsx_read_autoinfo: parameter[lsx_file] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_read_autoinfo: parameter[lsx_file] invalid");
#endif
#endif
            return (jint)-1;
        }

        iRet = lsx_read_autoinfo((LSX_FILE)lsx_file, &ai);

#ifdef WIN32
        printf("lsx_read_autoinfo, iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_read_autoinfo, iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_read_autoinfo, iRet=[%d]", iRet);
#endif
#endif

        // 设置java的LSX_AUTOINFO对象
        cls   = (*env)->FindClass(env, "com/cnlaunch/mycar/jni/LSX_AUTOINFO");           

        fid_vin = (*env)->GetFieldID(env, cls, "vin", "Ljava/lang/String;");           
        fid_make = (*env)->GetFieldID(env, cls, "make", "Ljava/lang/String;"); 
        fid_model = (*env)->GetFieldID(env, cls, "model", "Ljava/lang/String;"); 
        fid_year = (*env)->GetFieldID(env, cls, "year", "Ljava/lang/String;"); 
        fid_madein = (*env)->GetFieldID(env, cls, "madein", "Ljava/lang/String;"); 
        fid_chassis = (*env)->GetFieldID(env, cls, "chassis", "Ljava/lang/String;"); 
        fid_enginemodel = (*env)->GetFieldID(env, cls, "enginemodel", "Ljava/lang/String;"); 
        fid_displacement = (*env)->GetFieldID(env, cls, "displacement", "Ljava/lang/String;"); 

        jstr_vin = CharTojstring(env, ai.vin);
        jstr_make = CharTojstring(env, ai.make);
        jstr_model = CharTojstring(env, ai.model);
        jstr_year = CharTojstring(env, ai.year);
        jstr_madein = CharTojstring(env, ai.madein);
        jstr_chassis = CharTojstring(env, ai.chassis);
        jstr_enginemodel = CharTojstring(env, ai.enginemodel);
        jstr_displacement = CharTojstring(env, ai.displacement);

#ifdef WIN32
        printf("lsx_read_autoinfo: ai.vin=[%s], ai.make=[%s], ai.model=[%s], ai.year=[%s], "
            "ai.madein=[%s] ai.chassis=[%s], ai.enginemodel=[%s], ai.displacement=[%s]\n",
            ai.vin, ai.make, ai.model, ai.year,
            ai.madein, ai.chassis, ai.enginemodel, ai.displacement);
#else
#ifdef __linux
        printf("lsx_read_autoinfo: ai.vin=[%s], ai.make=[%s], ai.model=[%s], ai.year=[%s], "
            "ai.madein=[%s] ai.chassis=[%s], ai.enginemodel=[%s], ai.displacement=[%s]\n",
            ai.vin, ai.make, ai.model, ai.year,
            ai.madein, ai.chassis, ai.enginemodel, ai.displacement);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest",
            "lsx_read_autoinfo: ai.vin=[%s], ai.make=[%s], ai.model=[%s], ai.year=[%s], "
            "ai.madein=[%s] ai.chassis=[%s], ai.enginemodel=[%s], ai.displacement=[%s]",
            ai.vin, ai.make, ai.model, ai.year,
            ai.madein, ai.chassis, ai.enginemodel, ai.displacement);
#endif
#endif


        (*env)->SetObjectField(env, autoinfo, fid_vin, jstr_vin);   
        (*env)->SetObjectField(env, autoinfo, fid_make, jstr_make); 
        (*env)->SetObjectField(env, autoinfo, fid_model, jstr_model); 
        (*env)->SetObjectField(env, autoinfo, fid_year, jstr_year); 
        (*env)->SetObjectField(env, autoinfo, fid_madein, jstr_madein); 
        (*env)->SetObjectField(env, autoinfo, fid_chassis, jstr_chassis); 
        (*env)->SetObjectField(env, autoinfo, fid_enginemodel, jstr_enginemodel); 
        (*env)->SetObjectField(env, autoinfo, fid_displacement, jstr_displacement); 


        return iRet;
    }

    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_write_autoinfo
    * Signature: (ILcom/cnlaunch/mycar/jni/LSX_AUTOINFO;)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1write_1autoinfo
        (JNIEnv *env, jobject obj, jint lsx_file, jobject autoinfo)
    {
        jclass    cls ;

        jfieldID  fid_vin ;
        jfieldID  fid_make ;
        jfieldID  fid_model ;
        jfieldID  fid_year ;
        jfieldID  fid_madein;
        jfieldID  fid_chassis;
        jfieldID  fid_enginemodel ;
        jfieldID  fid_displacement ;

        jstring jstr_vin ;
        jstring jstr_make ;
        jstring jstr_model ;
        jstring jstr_year ;
        jstring jstr_madein;
        jstring jstr_chassis;
        jstring jstr_enginemodel ;
        jstring jstr_displacement ;

        char* vin ;
        char* make ;
        char* model ;
        char* year ;
        char* madein;
        char* chassis;
        char* enginemodel ;
        char* displacement ;

        LSX_AUTOINFO ai;

        int iRet ;


        if ( 0 == lsx_file )
        {
#ifdef WIN32
            printf("lsx_write_autoinfo: parameter[lsx_file] invalid\n");
#else
#ifdef __linux
            printf("lsx_write_autoinfo: parameter[lsx_file] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_write_autoinfo: parameter[lsx_file] invalid");
#endif
#endif
            return (jint)-1;
        }

        // 从autoinfo对象获取字段值
        cls   = (*env)->FindClass(env, "com/cnlaunch/mycar/jni/LSX_AUTOINFO");           

        fid_vin = (*env)->GetFieldID(env, cls, "vin", "Ljava/lang/String;");           
        fid_make = (*env)->GetFieldID(env, cls, "make", "Ljava/lang/String;"); 
        fid_model = (*env)->GetFieldID(env, cls, "model", "Ljava/lang/String;"); 
        fid_year = (*env)->GetFieldID(env, cls, "year", "Ljava/lang/String;"); 
        fid_madein = (*env)->GetFieldID(env, cls, "madein", "Ljava/lang/String;"); 
        fid_chassis = (*env)->GetFieldID(env, cls, "chassis", "Ljava/lang/String;"); 
        fid_enginemodel = (*env)->GetFieldID(env, cls, "enginemodel", "Ljava/lang/String;"); 
        fid_displacement = (*env)->GetFieldID(env, cls, "displacement", "Ljava/lang/String;"); 

        jstr_vin = (*env)->GetObjectField(env, autoinfo, fid_vin);
        jstr_make = (*env)->GetObjectField(env, autoinfo, fid_make);
        jstr_model = (*env)->GetObjectField(env, autoinfo, fid_model);
        jstr_year = (*env)->GetObjectField(env, autoinfo, fid_year);
        jstr_madein = (*env)->GetObjectField(env, autoinfo, fid_madein);
        jstr_chassis = (*env)->GetObjectField(env, autoinfo, fid_chassis);
        jstr_enginemodel = (*env)->GetObjectField(env, autoinfo, fid_enginemodel);
        jstr_displacement = (*env)->GetObjectField(env, autoinfo, fid_displacement);

        vin = jstringToChar(env, jstr_vin);
        make = jstringToChar(env, jstr_make);
        model = jstringToChar(env, jstr_model);
        year = jstringToChar(env, jstr_year);
        madein = jstringToChar(env, jstr_madein);
        chassis = jstringToChar(env, jstr_chassis);
        enginemodel = jstringToChar(env, jstr_enginemodel);
        displacement = jstringToChar(env, jstr_displacement);


        strcpy(ai.vin, vin);
        ai.make = make;
        ai.model = model;
        ai.year =  year;
        ai.madein = madein;
        ai.chassis = chassis;
        ai.enginemodel = enginemodel;
        ai.displacement = displacement;

        iRet = lsx_write_autoinfo((LSX_FILE)lsx_file, &ai);

#ifdef WIN32
        printf("lsx_write_autoinfo, iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_write_autoinfo, iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_write_autoinfo, iRet=[%d]", iRet);
#endif
#endif

        free(vin);
        free(make);
        free(model);
        free(year);
        free(madein);
        free(chassis);
        free(enginemodel);
        free(displacement);

        return iRet;
    }

    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_read_spinfo
    * Signature: (ILcom/cnlaunch/mycar/jni/LSX_SPINFO;)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1read_1spinfo
        (JNIEnv *env, jobject obj, jint lsx_file, jobject spinfo)
    {
        LSX_SPINFO spi;
        int iRet;

        jclass    cls ;

        jfieldID  fid_name ;
        jfieldID  fid_phone;   

        jstring jstr_name ;
        jstring jstr_phone ;

        if ( 0 == lsx_file )
        {
#ifdef WIN32
            printf("lsx_read_spinfo: parameter[lsx_file] invalid\n");
#else
#ifdef __linux
             printf("lsx_read_spinfo: parameter[lsx_file] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_read_spinfo: parameter[lsx_file] invalid");
#endif
#endif
            return (jint)-1;
        }

        iRet = lsx_read_spinfo((LSX_FILE)lsx_file, &spi);

#ifdef WIN32
        printf("lsx_read_spinfo, iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_read_spinfo, iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_read_spinfo, iRet=[%d]", iRet);
#endif
#endif

        // 设置java的LSX_SPINFO对象
        cls   = (*env)->FindClass(env, "com/cnlaunch/mycar/jni/LSX_SPINFO");           

        fid_name = (*env)->GetFieldID(env, cls, "name", "Ljava/lang/String;");           
        fid_phone = (*env)->GetFieldID(env, cls, "phone", "Ljava/lang/String;");   

        jstr_name = CharTojstring(env, spi.name);
        jstr_phone = CharTojstring(env, spi.phone);

#ifdef WIN32
        printf("lsx_read_spinfo: spi.name=[%s], spi.phone=[%s]\n", jstr_name, jstr_phone);
#else
#ifdef __linux
       printf("lsx_read_spinfo: spi.name=[%s], spi.phone=[%s]\n", jstr_name, jstr_phone);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest",
            "lsx_read_spinfo: spi.name=[%s], spi.phone=[%s]", jstr_name, jstr_phone);
#endif
#endif


        (*env)->SetObjectField(env, spinfo, fid_name, jstr_name);   
        (*env)->SetObjectField(env, spinfo, fid_phone, jstr_phone);   

        return iRet;
    }

    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_write_spinfo
    * Signature: (ILcom/cnlaunch/mycar/jni/LSX_SPINFO;)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1write_1spinfo
        (JNIEnv *env, jobject obj, jint lsx_file, jobject spinfo)
    {
        jclass    cls ;         

        jfieldID  fid_name ;
        jfieldID  fid_phone ;

        jstring jstr_name ;
        jstring jstr_phone ;

        char* name ;
        char* phone ;

        int iRet;

        LSX_SPINFO spi;

        if ( 0 == lsx_file )
        {
#ifdef WIN32
            printf("lsx_write_spinfo: parameter[lsx_file] invalid\n");
#else
#ifdef __linux
            printf("lsx_write_spinfo: parameter[lsx_file] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_write_spinfo: parameter[lsx_file] invalid");
#endif
#endif
            return (jint)-1;
        }

        // 从spinfo对象获取字段值
        cls   = (*env)->FindClass(env, "com/cnlaunch/mycar/jni/LSX_SPINFO");           

        fid_name = (*env)->GetFieldID(env, cls, "name", "Ljava/lang/String;");           
        fid_phone = (*env)->GetFieldID(env, cls, "phone", "Ljava/lang/String;");   

        jstr_name = (*env)->GetObjectField(env, spinfo, fid_name);
        jstr_phone = (*env)->GetObjectField(env, spinfo, fid_phone);

        name = jstringToChar(env, jstr_name);
        phone = jstringToChar(env, jstr_phone);

        spi.name = name;
        spi.phone = phone;

        iRet = lsx_write_spinfo((LSX_FILE)lsx_file, &spi);

#ifdef WIN32
        printf("lsx_write_spinfo, iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_write_spinfo, iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_write_spinfo, iRet=[%d]", iRet);
#endif
#endif

        free(name);
        free(phone);

        return iRet;
    }

    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_read_userinfo
    * Signature: (ILcom/cnlaunch/mycar/jni/LSX_USERINFO;)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1read_1userinfo
        (JNIEnv *env, jobject obj, jint lsx_file, jobject userinfo)
    {
        LSX_USERINFO user;
        int iRet;

        jclass    cls ;

        jfieldID  fid_name ;
        jfieldID  fid_phone ;
        jfieldID  fid_license;

        jstring jstr_name ;
        jstring jstr_phone ;
        jstring jstr_license;

        if ( 0 == lsx_file )
        {
#ifdef WIN32
            printf("lsx_read_userinfo: parameter[lsx_file] invalid\n");
#else
#ifdef __linux
            printf("lsx_read_userinfo: parameter[lsx_file] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_read_userinfo: parameter[lsx_file] invalid");
#endif
#endif
            return (jint)-1;
        }

        iRet = lsx_read_userinfo((LSX_FILE)lsx_file, &user);

#ifdef WIN32
        printf("lsx_read_userinfo: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_read_userinfo: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_read_userinfo: iRet=[%d]", iRet);
#endif
#endif

        // 设置java的LSX_USERINFO对象
        cls   = (*env)->FindClass(env, "com/cnlaunch/mycar/jni/LSX_USERINFO");           

        fid_name = (*env)->GetFieldID(env, cls, "name", "Ljava/lang/String;");           
        fid_phone = (*env)->GetFieldID(env, cls, "phone", "Ljava/lang/String;");   
        fid_license = (*env)->GetFieldID(env, cls, "license", "Ljava/lang/String;");   


        jstr_name = CharTojstring(env, user.name);
        jstr_phone = CharTojstring(env, user.phone);
        jstr_license = CharTojstring(env, user.license);


#ifdef WIN32
        printf("lsx_read_userinfo: user.name=[%s], user.phone=[%s], user.license=[%s]\n", jstr_name, jstr_phone, user.license);
#else
#ifdef __linux
       printf("lsx_read_userinfo: user.name=[%s], user.phone=[%s], user.license=[%s]\n", jstr_name, jstr_phone, user.license);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest",
            "lsx_read_userinfo: user.name=[%s], user.phone=[%s], user.license=[%s]", jstr_name, jstr_phone, user.license);
#endif
#endif

        (*env)->SetObjectField(env, userinfo, fid_name, jstr_name);   
        (*env)->SetObjectField(env, userinfo, fid_phone, jstr_phone);   
        (*env)->SetObjectField(env, userinfo, fid_license, jstr_license);   

        return iRet;
    }

    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_write_userinfo
    * Signature: (ILcom/cnlaunch/mycar/jni/LSX_USERINFO;)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1write_1userinfo
        (JNIEnv *env, jobject obj, jint lsx_file, jobject userinfo)
    {
        jclass    cls  ;

        jfieldID  fid_name ;
        jfieldID  fid_phone ;
        jfieldID  fid_license;

        jstring jstrname ;
        jstring jstrphone ;
        jstring jstrlicense;

        char* name ;
        char* phone ;
        char* license;

        LSX_USERINFO ui;

        int iRet;


        if ( 0 == lsx_file )
        {
#ifdef WIN32
            printf("lsx_write_userinfo: parameter[lsx_file] invalid\n");
#else
#ifdef __linux
            printf("lsx_write_userinfo: parameter[lsx_file] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_write_userinfo: parameter[lsx_file] invalid");
#endif
#endif
            return (jint)-1;
        }

        // 从userinfo对象取字段值
        cls   = (*env)->FindClass(env, "com/cnlaunch/mycar/jni/LSX_USERINFO");           

        fid_name = (*env)->GetFieldID(env, cls, "name", "Ljava/lang/String;");           
        fid_phone = (*env)->GetFieldID(env, cls, "phone", "Ljava/lang/String;");   
        fid_license = (*env)->GetFieldID(env, cls, "license", "Ljava/lang/String;");   

        jstrname = (*env)->GetObjectField(env, userinfo, fid_name);
        jstrphone = (*env)->GetObjectField(env, userinfo, fid_phone);
        jstrlicense = (*env)->GetObjectField(env, userinfo, fid_license);

        name = jstringToChar(env, jstrname);
        phone = jstringToChar(env, jstrphone);
        license = jstringToChar(env, jstrlicense);

        ui.license = license;
        ui.name = name;
        ui.phone = phone;

        iRet = lsx_write_userinfo((LSX_FILE)lsx_file, &ui);

#ifdef WIN32
        printf("lsx_write_userinfo: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_write_userinfo: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_write_userinfo: iRet=[%d]", iRet);
#endif
#endif

        free(name);
        free(phone);
        free(license);

        return iRet;
    }

    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_readgroupcount
    * Signature: (I)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1readgroupcount
        (JNIEnv *env, jobject obj, jint lsx_file)
    {
        int iRet;

        if ( 0 == lsx_file )
        {
#ifdef WIN32
            printf("lsx_rec_readgroupcount: parameter[lsx_file] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_readgroupcount: parameter[lsx_file] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readgroupcount: parameter[lsx_file] invalid");
#endif
#endif
            return (jint)-1;
        }

        iRet = lsx_rec_readgroupcount((LSX_FILE)lsx_file);

#ifdef WIN32
        printf("lsx_rec_readgroupcount: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_readgroupcount: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readgroupcount: iRet=[%d]", iRet);
#endif
#endif

        return iRet;

    }

    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_read_fileversion
    * Signature: (I)S
    */
    JNIEXPORT jshort JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1read_1fileversion
        (JNIEnv *env, jobject obj, jint lsx_file)
    {
        unsigned short usRet;

        if ( 0 == lsx_file )
        {
#ifdef WIN32
            printf("lsx_read_fileversion: parameter[lsx_file] invalid\n");
#else
#ifdef __linux
           printf("lsx_read_fileversion: parameter[lsx_file] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_read_fileversion: parameter[lsx_file] invalid");
#endif
#endif
            return (jint)-1;
        }

        usRet = lsx_read_fileversion((LSX_FILE)lsx_file);

#ifdef WIN32
        printf("lsx_read_fileversion: usRet=[%d]\n", usRet);
#else
#ifdef __linux
        printf("lsx_read_fileversion: usRet=[%d]\n", usRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_read_fileversion: usRet=[%d]", usRet);
#endif
#endif

        return (jshort)usRet;

    }


    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_read_langcode
    * Signature: (ILcom/cnlaunch/mycar/jni/X431String;Lcom/cnlaunch/mycar/jni/X431String;)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1read_1langcode
        (JNIEnv *env, jobject obj, jint lsx_file, jobject code, jobject code_en)
    {    
        char szcode[4] = {0};
        char szcode_en[4] = {0};

        int size ;
        int iRet;

        jstring strcode ;
        jstring strcode_en;

        jclass cls ;
        jfieldID  fid_mValue;

        if ( 0 == lsx_file )
        {
#ifdef WIN32
            printf("lsx_read_langcode: parameter[lsx_file] invalid\n");
#else
#ifdef __linux
           printf("lsx_read_langcode: parameter[lsx_file] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_read_langcode: parameter[lsx_file] invalid");
#endif
#endif
            return (jint)-1;
        }


        // 调用lsx_read_langcode
        size = 4;
        iRet = lsx_read_langcode((LSX_FILE)lsx_file, szcode, szcode_en, (int)size);

        strcode = CharTojstring(env, szcode);
        strcode_en = CharTojstring(env, szcode_en);            

#ifdef WIN32
        printf("lsx_read_langcode: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_read_langcode: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_read_langcode: iRet=[%d]", iRet);
#endif
#endif

        // 设置code和code_en的值        
        cls = (*env)->FindClass(env, "com/cnlaunch/mycar/jni/X431String");      
        if(cls == 0)
        {    
#ifdef WIN32
            printf("lsx_read_langcode: [FindClass->X431String] error\n");
#else
#ifdef __linux
            printf("lsx_read_langcode: [FindClass->X431String] error\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_read_langcode: [FindClass->X431String] error");
#endif
#endif
            return (jint)-2; 
        }    

        fid_mValue = (*env)->GetFieldID(env, cls, "mValue","Ljava/lang/String;"); 


        // 设置code,code_en对象mValue字段值的值
        (*env)->SetObjectField(env, code, fid_mValue, strcode);
        (*env)->SetObjectField(env, code_en, fid_mValue, strcode_en);        

        return iRet;        
    }

    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_readgroupid
    * Signature: (II)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1readgroupid
        (JNIEnv *env, jobject obj, jint lsx_file, jint i)
    {
        int iRet;

        if ( 0 == lsx_file )
        {
#ifdef WIN32
            printf("lsx_rec_readgroupid: parameter[lsx_file] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_readgroupid: parameter[lsx_file] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readgroupid: parameter[lsx_file] invalid");
#endif
#endif
            return (jint)-1;
        }

        iRet = (int)lsx_rec_readgroupid((LSX_FILE)lsx_file, (int)i);

#ifdef WIN32
        printf("lsx_rec_readgroupid: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_readgroupid: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readgroupid: iRet=[%d]", iRet);
#endif
#endif

        return iRet;
    }

    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_readdtccount
    * Signature: (I)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1readdtccount
        (JNIEnv *env, jobject obj, jint grp)
    {
        int iRet;

        if ( 0 == grp )
        {
#ifdef WIN32
            printf("lsx_rec_readdtccount: parameter[grp] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_readdtccount: parameter[grp] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readdtccount: parameter[grp] invalid");
#endif
#endif
            return (jint)-1;
        }

        iRet = lsx_rec_readdtccount((LSX_RECGROUP)grp);

#ifdef WIN32
        printf("lsx_rec_readdtccount: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_readdtccount: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readdtccount: iRet=[%d]", iRet);
#endif
#endif

        return iRet;
    }



    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_readfirstdtcitem
    * Signature: (I)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1readfirstdtcitem
        (JNIEnv *env, jobject obj, jint grp)
    {
        int iRet;

        if ( 0 == grp )
        {
#ifdef WIN32
            printf("lsx_rec_readfirstdtcitem: parameter[grp] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_readfirstdtcitem: parameter[grp] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readfirstdtcitem: parameter[grp] invalid");
#endif
#endif
            return (jint)-1;
        }

        iRet = (int)lsx_rec_readfirstdtcitem((LSX_RECGROUP)grp);

#ifdef WIN32
        printf("lsx_rec_readfirstdtcitem: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_readfirstdtcitem: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readfirstdtcitem: iRet=[%d]", iRet);
#endif
#endif

        return iRet;

    }


    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_readdtc
    * Signature: (ILcom/cnlaunch/mycar/jni/X431String;Lcom/cnlaunch/mycar/jni/X431String;Lcom/cnlaunch/mycar/jni/X431String;Lcom/cnlaunch/mycar/jni/X431String;)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1readdtc
        (JNIEnv *env, jobject obj, jint item, jobject dtc, jobject state, jobject desc, jobject time)
    {
        char *pdtc, *pstate, *pdesc, *ptime;
        int iRet;

        jstring jstr_dtc ;
        jstring jstr_state;
        jstring jstr_desc ;
        jstring jstr_time ;

        jfieldID  fid_mValue;

        jclass cls ;

        if ( 0 == item )
        {
#ifdef WIN32
            printf("lsx_rec_readdtc: parameter[item] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_readdtc: parameter[item] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readdtc: parameter[item] invalid");
#endif
#endif
            return (jint)-1;
        }

        iRet = lsx_rec_readdtc((LSX_RECITEM)item, &pdtc, &pstate, &pdesc, &ptime);

#ifdef WIN32
        printf("lsx_rec_readdtc: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_readdtc: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readdtc: iRet=[%d]", iRet);
#endif
#endif

        // 数据类型转换
        jstr_dtc = CharTojstring(env, pdtc);
        jstr_state = CharTojstring(env, pstate);
        jstr_desc = CharTojstring(env, pdesc);
        jstr_time = CharTojstring(env, ptime);

        // 设置jobject dtc, jobject state, jobject desc, jobject time
        cls = (*env)->FindClass(env, "com/cnlaunch/mycar/jni/X431String");      
        if(cls == 0)
        {    
#ifdef WIN32
            printf( "lsx_read_langcode: [FindClass->X431String] error\n");
#else
#ifdef __linux
            printf( "lsx_read_langcode: [FindClass->X431String] error\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_read_langcode: [FindClass->X431String] error");
#endif
#endif
            return (jint)-2; 
        }    

        fid_mValue = (*env)->GetFieldID(env, cls, "mValue","Ljava/lang/String;");         

        (*env)->SetObjectField(env, dtc, fid_mValue, jstr_dtc);
        (*env)->SetObjectField(env, state, fid_mValue, jstr_state);
        (*env)->SetObjectField(env, desc, fid_mValue, jstr_desc);
        (*env)->SetObjectField(env, time, fid_mValue, jstr_time);

        return iRet;
    }

    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_readnextdtcitem
    * Signature: (I)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1readnextdtcitem
        (JNIEnv *env, jobject obj, jint item)
    {
        int iRet = (int)lsx_rec_readnextdtcitem((LSX_RECITEM )item);

#ifdef WIN32
        printf("lsx_rec_readnextdtcitem: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_readnextdtcitem: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readnextdtcitem: iRet=[%d]", iRet);
#endif
#endif

        return iRet;
    }


    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_readdtcinfo
    * Signature: (ILjava/lang/String;Lcom/cnlaunch/mycar/jni/X431String;Lcom/cnlaunch/mycar/jni/X431String;Lcom/cnlaunch/mycar/jni/X431String;)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1readdtcinfo
        (JNIEnv *env, jobject obj, jint grp, jstring dtc, jobject state, jobject desc, jobject time)
    {
        char *pdtc ;
        char *pstate, *pdesc, *ptime;
        int iRet ;

        jclass cls;

        jfieldID  fid_mValue ;

        jstring jstr_state ;
        jstring jstr_desc ;
        jstring jstr_time ;


        if ( 0 == grp )
        {
#ifdef WIN32
            printf( "lsx_rec_readdtcinfo: parameter[grp] invalid\n");
#else
#ifdef __linux
            printf( "lsx_rec_readdtcinfo: parameter[grp] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readdtcinfo: parameter[grp] invalid");
#endif
#endif
            return (jint)-1;
        }

        pdtc = jstringToChar(env, dtc);
        pstate, *pdesc, *ptime;
        iRet = lsx_rec_readdtcinfo((LSX_RECGROUP)grp, pdtc, &pstate, &pdesc, &ptime);

#ifdef WIN32
        printf("lsx_rec_readdtcinfo: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_readdtcinfo: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readdtcinfo: iRet=[%d]", iRet);
#endif
#endif

        // 数据类型转换
        cls = (*env)->FindClass(env, "com/cnlaunch/mycar/jni/X431String");      
        if(cls == 0)
        {   
#ifdef WIN32
            printf("lsx_rec_readdtcinfo: [FindClass->X431String] error\n");
#else
#ifdef __linux
            printf("lsx_rec_readdtcinfo: [FindClass->X431String] error\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readdtcinfo: [FindClass->X431String] error");
#endif
#endif
            return (jint)-2; 
        }    

        fid_mValue = (*env)->GetFieldID(env, cls, "mValue","Ljava/lang/String;");         

        jstr_state = CharTojstring(env, pstate);
        jstr_desc = CharTojstring(env, pdesc);
        jstr_time = CharTojstring(env, ptime);

        // 设置jobject dtc, jobject state, jobject desc, jobject time        
        (*env)->SetObjectField(env, state, fid_mValue, jstr_state);
        (*env)->SetObjectField(env, desc, fid_mValue, jstr_desc);
        (*env)->SetObjectField(env, time, fid_mValue, jstr_time);

        free(pdtc);

        return iRet;
    }


    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_readdsitemcount
    * Signature: (I)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1readdsitemcount
        (JNIEnv *env, jobject obj, jint grp)
    {
        int iRet;

        if ( 0 == grp )
        {
#ifdef WIN32
            printf("lsx_rec_readdsitemcount: parameter[grp] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_readdsitemcount: parameter[grp] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readdsitemcount: parameter[grp] invalid");
#endif
#endif
            return (jint)-1;
        }

        iRet = lsx_rec_readdsitemcount((LSX_RECGROUP)grp);

#ifdef WIN32
        printf("lsx_rec_readdsitemcount: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_readdsitemcount: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readdsitemcount: iRet=[%d]", iRet);
#endif
#endif

        return iRet;
    }

    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_readdscolcount
    * Signature: (I)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1readdscolcount
        (JNIEnv *env, jobject obj, jint grp)
    {
        int iRet;

        if ( 0 == grp )
        {
#ifdef WIN32
            printf("lsx_rec_readdscolcount: parameter[grp] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_readdscolcount: parameter[grp] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readdscolcount: parameter[grp] invalid");
#endif
#endif
            return (jint)-1;
        }

        iRet = lsx_rec_readdscolcount((LSX_RECGROUP)grp);

#ifdef WIN32
        printf("lsx_rec_readdscolcount: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_readdscolcount: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readdscolcount: iRet=[%d]", iRet);
#endif
#endif

        return iRet;        
    }


    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_readdsname
    * Signature: (I[Ljava/lang/String;I)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1readdsname
        (JNIEnv *env, jobject obj, jint grp, jobjectArray textstrs, jint n)
    {
        int len;
        char **namestrs;
        int iRet;
        int iCount;
        int iTmp;
        jstring jstr;

        if ( 0 == grp )
        {
#ifdef WIN32
            printf("lsx_rec_readdsname: parameter[grp] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_readdsname: parameter[grp] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readdsname: parameter[grp] invalid");
#endif
#endif
            return (jint)-1;
        }

        /* 获取数组对象的元素个数 */        
        len = (*env)->GetArrayLength(env, textstrs);
        if ( len <= 0 )
        {
#ifdef WIN32
            printf("lsx_rec_readdsname: parameter[textstrs] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_readdsname: parameter[textstrs] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readdsname: parameter[textstrs] invalid");
#endif
#endif
            return (jint)-1;
        }

        if ( n <= 0 )
        {
#ifdef WIN32
            printf("lsx_rec_readdsname: parameter[n] invalid\n");
#else
#ifdef __linux
             printf("lsx_rec_readdsname: parameter[n] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readdsname: parameter[n] invalid");
#endif
#endif
            return (jint)-1;
        }

        namestrs = malloc(sizeof(char*) * n);
        iRet = lsx_rec_readdsname((LSX_RECGROUP)grp, namestrs, (int)n);

#ifdef WIN32
        printf( "lsx_rec_readdsname: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf( "lsx_rec_readdsname: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readdsname: iRet=[%d]", iRet);   
#endif
#endif

        iCount = (len > n) ? n : len;

        // 将结果传递给jobjectArray textstrs
        iTmp = 0;
        jstr;
        for ( ; iTmp<iCount; iTmp++)
        {
            jstr = CharTojstring(env, namestrs[iTmp]);
            (*env)->SetObjectArrayElement(env, textstrs, iTmp, jstr);
        }

        free(namestrs);

        return iRet;

    }

    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_readdsunit
    * Signature: (I[Ljava/lang/String;I)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1readdsunit
        (JNIEnv *env, jobject obj, jint grp, jobjectArray textstrs, jint n)
    {
        int len;
        char **unitstrs;
        int iRet;
        int iCount;


        int iTmp;
        jstring jstr;

        if ( 0 == grp )
        {
#ifdef WIN32
            printf( "lsx_rec_readdsunit: parameter[grp] invalid\n");
#else
#ifdef __linux
            printf( "lsx_rec_readdsunit: parameter[grp] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readdsunit: parameter[grp] invalid");
#endif
#endif
            return (jint)-1;
        }

        /* 获取数组对象的元素个数 */        
        len = (*env)->GetArrayLength(env, textstrs);
        if ( len <= 0 )
        {
#ifdef WIN32
            printf("lsx_rec_readdsunit: parameter[textstrs] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_readdsunit: parameter[textstrs] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readdsunit: parameter[textstrs] invalid");
#endif
#endif
            return (jint)-1;
        }

        if ( n <= 0 )
        {
#ifdef WIN32
            printf("lsx_rec_readdsunit: parameter[n] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_readdsunit: parameter[n] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readdsunit: parameter[n] invalid");
#endif
#endif
            return (jint)-1;
        }

        unitstrs = malloc(sizeof(char*) * n);
        iRet = lsx_rec_readdsunit((LSX_RECGROUP)grp, unitstrs, (int)n);

#ifdef WIN32
        printf("lsx_rec_readdsunit: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_readdsunit: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readdsunit: iRet=[%d]", iRet);
#endif
#endif


        iCount = (len > n) ? n : len;

        // 将结果传递给jobjectArray textstrs
        iTmp = 0;
        jstr;
        for ( ; iTmp<iCount; iTmp++)
        {
            jstr = CharTojstring(env, unitstrs[iTmp]);
            (*env)->SetObjectArrayElement(env, textstrs, iTmp, jstr);
        }

        free(unitstrs);

        return iRet;
    }

    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_readdstype
    * Signature: (I[SI)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1readdstype
        (JNIEnv *env, jobject obj, jint grp, jshortArray type, jint n)
    {
        int len;
        unsigned short *ptype;
        int iRet;

        if ( 0 == grp )
        {
#ifdef WIN32
            printf("lsx_rec_readdstype: parameter[grp] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_readdstype: parameter[grp] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readdstype: parameter[grp] invalid");
#endif
#endif
            return (jint)-1;
        }

        /* 获取数组对象的元素个数 */        
        len = (*env)->GetArrayLength(env, type);
        if ( len <= 0 )
        {
#ifdef WIN32
            printf("lsx_rec_readdstype: parameter[textstrs] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_readdstype: parameter[textstrs] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readdstype: parameter[textstrs] invalid");
#endif
#endif
            return (jint)-1;
        }

        if ( n <= 0 )
        {
#ifdef WIN32
            printf("lsx_rec_readdstype: parameter[n] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_readdstype: parameter[n] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readdstype: parameter[n] invalid");
#endif
#endif
            return (jint)-1;
        }

        ptype = malloc(sizeof(unsigned short) * n);
        iRet = lsx_rec_readdstype((LSX_RECGROUP)grp, ptype, n);

#ifdef WIN32
        printf("lsx_rec_readdstype: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_readdstype: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readdstype: iRet=[%d]", iRet);
#endif
#endif

        free(ptype);

        return iRet;
    }


    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_readfirstdsitem
    * Signature: (I)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1readfirstdsitem
        (JNIEnv *env, jobject obj, jint grp)
    {
        int iRet;

        if ( 0 == grp )
        {
#ifdef WIN32
            printf("lsx_rec_readfirstdsitem: parameter[grp] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_readfirstdsitem: parameter[grp] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readfirstdsitem: parameter[grp] invalid");
#endif
#endif
            return (jint)-1;
        }

        iRet = (int)lsx_rec_readfirstdsitem((LSX_RECGROUP)grp);

#ifdef WIN32
        printf("lsx_rec_readfirstdsitem: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_readfirstdsitem: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readfirstdsitem: iRet=[%d]", iRet);
#endif
#endif

        return iRet;
    }


    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_readds
    * Signature: (I[Ljava/lang/String;I)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1readds
        (JNIEnv *env, jobject obj, jint item, jobjectArray textstrs, jint n)
    {
        int len;
        char **namestrs;
        int iRet;
        int iCount;

        // 将结果传递给jobjectArray textstrs
        int iTmp;
        jstring jstr;

        if ( 0 == item )
        {
#ifdef WIN32
            printf("lsx_rec_readds: parameter[item] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_readds: parameter[item] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readds: parameter[item] invalid");
#endif
#endif
            return (jint)-1;
        }

        /* 获取数组对象的元素个数 */        
        len = (*env)->GetArrayLength(env, textstrs);
        if ( len <= 0 )
        {
#ifdef WIN32
            printf("lsx_rec_readds: parameter[textstrs] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_readds: parameter[textstrs] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readds: parameter[textstrs] invalid");
#endif
#endif
            return (jint)-1;
        }

        if ( n <= 0 )
        {
#ifdef WIN32
            printf("lsx_rec_readds: parameter[n] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_readds: parameter[n] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readds: parameter[n] invalid");
#endif
#endif
            return (jint)-1;
        }

        namestrs = malloc(sizeof(char*) * n);
        iRet = lsx_rec_readds((LSX_RECGROUP)item, namestrs, (int)n);

#ifdef WIN32
        printf("lsx_rec_readds: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_readds: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readds: iRet=[%d]", iRet);   
#endif
#endif

        iCount = (len > n) ? n : len;

        // 将结果传递给jobjectArray textstrs
        iTmp = 0;
        for ( ; iTmp<iCount; iTmp++)
        {
            jstr = CharTojstring(env, namestrs[iTmp]);
            (*env)->SetObjectArrayElement(env, textstrs, iTmp, jstr);
        }

        free(namestrs);

        return iRet;

    }


    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_readrelndsitem
    * Signature: (II)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1readrelndsitem
        (JNIEnv *env, jobject obj, jint item, jint n)
    {
        int iRet;

        if ( 0 == item )
        {
#ifdef WIN32
            printf("lsx_rec_readrelndsitem: parameter[item] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_readrelndsitem: parameter[item] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readrelndsitem: parameter[item] invalid");
#endif
#endif
            return (jint)-1;
        }

        iRet = (int)lsx_rec_readrelndsitem((LSX_RECITEM)item, n);

#ifdef WIN32
        printf("lsx_rec_readrelndsitem: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_readrelndsitem: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readrelndsitem: iRet=[%d]", iRet);   
#endif
#endif

        return iRet;
    }

    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_writenewgroup
    * Signature: (ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1writenewgroup
        (JNIEnv *env, jobject obj, jint lsx_file, jstring name, jstring protocol, jstring vin, jstring starttime, jint dsinterval)
    {
        char* pname ;
        char* pprotocol;
        char* pvin;
        char* pstarttime;
        int iRet;

        if ( 0 == lsx_file )
        {
#ifdef WIN32
            printf( "lsx_rec_writenewgroup: parameter[lsx_file] invalid\n");
#else
#ifdef __linux
            printf( "lsx_rec_writenewgroup: parameter[lsx_file] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_writenewgroup: parameter[lsx_file] invalid");
#endif
#endif
            return (jint)-1;
        } 

        pname = jstringToChar(env, name);
        pprotocol = jstringToChar(env, protocol);
        pvin = jstringToChar(env, vin);
        pstarttime = jstringToChar(env, starttime);        

#ifdef WIN32
        printf( "lsx_rec_writenewgroup: name=[%s], protocol=[%s], vin=[%s], starttime=[%s], dsinterval=[%d]\n", 
            pname, pprotocol, pvin, pstarttime, dsinterval);
#else
#ifdef __linux
        printf( "lsx_rec_writenewgroup: name=[%s], protocol=[%s], vin=[%s], starttime=[%s], dsinterval=[%d]\n", 
            pname, pprotocol, pvin, pstarttime, dsinterval);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", 
            "lsx_rec_writenewgroup: name=[%s], protocol=[%s], vin=[%s], starttime=[%s], dsinterval=[%d]", 
            pname, pprotocol, pvin, pstarttime, dsinterval);           
#endif
#endif

        iRet = (int)lsx_rec_writenewgroup((LSX_FILE)lsx_file, pname, pprotocol, pvin, pstarttime, (int)dsinterval);

#ifdef WIN32
        printf("lsx_rec_writenewgroup: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_writenewgroup: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_writenewgroup: iRet=[%d]", iRet);   
#endif
#endif

        free(pname);
        free(pprotocol);
        free(pvin);
        free(pstarttime);

        return iRet;

    }

    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_writereadiness
    * Signature: (I[Lcom/cnlaunch/mycar/jni/LSX_STRING;[Lcom/cnlaunch/mycar/jni/LSX_STRING;I)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1writereadiness
        (JNIEnv *env, jobject obj, jint grp, jobjectArray namestrs, jobjectArray textstrs, jint n)
    {
        int len_namestrs;
        int len_textstrs;

        jclass    cls ;

        jfieldID  fid_str;
        jfieldID  fid_str_en;

        int iCount;

        LSX_STRING **pNames;
        LSX_STRING **pTexts;

        int i;

        jobject obj_name ;

        jstring jstr_str_name ;
        char* p_str_name ;

        jstring jstr_str_en_name;
        char* p_str_en_name;

        jobject obj_text ;

        jstring jstr_str_text ;
        char* p_str_text ;

        jstring jstr_str_en;
        char* p_str_en_text;

        int iRet;

        if ( 0 == grp )
        {
#ifdef WIN32
            printf("lsx_rec_writereadiness: parameter[grp] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_writereadiness: parameter[grp] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_writereadiness: parameter[grp] invalid");
#endif
#endif
            return (jint)-1;
        } 

        /* 获取数组对象的元素 */        
        len_namestrs = (*env)->GetArrayLength(env, namestrs);
        if ( len_namestrs <= 0 )
        {
#ifdef WIN32
            printf("lsx_rec_writereadiness: parameter[namestrs] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_writereadiness: parameter[namestrs] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_writereadiness: parameter[namestrs] invalid");
#endif
#endif
            return (jint)-1;
        }

        len_textstrs = (*env)->GetArrayLength(env, textstrs);
        if ( len_textstrs <= 0 )
        {
#ifdef WIN32
            printf("lsx_rec_writereadiness: parameter[textstrs] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_writereadiness: parameter[textstrs] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_writereadiness: parameter[textstrs] invalid");
#endif
#endif
            return (jint)-1;
        }

        if ( n <= 0 )
        {
#ifdef WIN32
            printf("lsx_rec_writereadiness: parameter[n] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_writereadiness: parameter[n] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_writereadiness: parameter[n] invalid");
#endif
#endif
            return (jint)-1;
        }

        cls   = (*env)->FindClass(env, "com/cnlaunch/mycar/jni/LSX_STRING");   

        //         jmethodID m_mid   = (*env)->GetMethodID(env, m_cls,"<init>","()V");   

        fid_str = (*env)->GetFieldID(env, cls,"str","Ljava/lang/String;");   
        fid_str_en = (*env)->GetFieldID(env, cls,"str_en","Ljava/lang/String;");   

        iCount = (len_namestrs > n) ? n : len_namestrs;
        iCount = (iCount > len_textstrs) ? len_textstrs : iCount;

#ifdef WIN32
        printf( "lsx_rec_writereadiness: iCount=[%d]\n", iCount);
#else
#ifdef __linux
        printf( "lsx_rec_writereadiness: iCount=[%d]\n", iCount);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_writereadiness: iCount=[%d]", iCount);
#endif
#endif

        pNames = malloc(sizeof(LSX_STRING*) * iCount);
        pTexts = malloc(sizeof(LSX_STRING*) * iCount);

        i = 0;
        for ( ; i < iCount; i++)
        {
            pNames[i] = malloc(sizeof(LSX_STRING*));
            pTexts[i] = malloc(sizeof(LSX_STRING*));

            obj_name = (*env)->GetObjectArrayElement(env, namestrs, i);

            jstr_str_name = (*env)->GetObjectField(env, obj_name, fid_str);
            p_str_name = jstringToChar(env, jstr_str_name);
            pNames[i]->str = p_str_name;

            jstr_str_en_name = (*env)->GetObjectField(env, obj_name, fid_str_en);
            p_str_en_name = jstringToChar(env, jstr_str_en_name);
            pNames[i]->str_en = p_str_en_name;            

            obj_text = (*env)->GetObjectArrayElement(env, textstrs, i);

            jstr_str_text = (*env)->GetObjectField(env, obj_text, fid_str);
            p_str_text = jstringToChar(env, jstr_str_text);
            pTexts[i]->str = p_str_text;

            jstr_str_en = (*env)->GetObjectField(env, obj_text, fid_str_en);
            p_str_en_text = jstringToChar(env, jstr_str_en);
            pTexts[i]->str_en = p_str_en_text;

#ifdef WIN32
            printf("lsx_rec_writereadiness:"
                "pNames[%d]->str=[%s], pNames[%d]->str_en=[%s], pTexts[%d]->str=[%s], pTexts[%d]->str_en=[%s]\n",
                i, pNames[i]->str, i, pNames[i]->str_en, i, pTexts[i]->str, i, pTexts[i]->str_en);
#else
#ifdef __linux
            printf("lsx_rec_writereadiness:"
                "pNames[%d]->str=[%s], pNames[%d]->str_en=[%s], pTexts[%d]->str=[%s], pTexts[%d]->str_en=[%s]\n",
                i, pNames[i]->str, i, pNames[i]->str_en, i, pTexts[i]->str, i, pTexts[i]->str_en);
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_writereadiness:"
                "pNames[%d]->str=[%s], pNames[%d]->str_en=[%s], pTexts[%d]->str=[%s], pTexts[%d]->str_en=[%s]",
                i, pNames[i]->str, i, pNames[i]->str_en, i, pTexts[i]->str, i, pTexts[i]->str_en); 
#endif
#endif

        }


        iRet = lsx_rec_writereadiness((LSX_RECGROUP)grp, pNames, pTexts, n);

#ifdef WIN32
        printf("lsx_rec_writereadiness: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_writereadiness: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_writereadiness: iRet=[%d]", iRet); 
#endif
#endif

        i = 0;
        for ( ; i < iCount; i++)
        {
            free(pNames[i]->str);
            free(pNames[i]->str_en);
            free(pNames[i]);

            free(pTexts[i]->str);
            free(pTexts[i]->str_en);
            free(pTexts[i]);
        }

        free(pNames);
        free(pTexts);

        return iRet;

    };

    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_writedtc
    * Signature: (ILjava/lang/String;Lcom/cnlaunch/mycar/jni/LSX_STRING;Lcom/cnlaunch/mycar/jni/LSX_STRING;Ljava/lang/String;)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1writedtc
        (JNIEnv *env, jobject obj, jint grp, jstring dtc, jobject state, jobject desc, jstring time)
    {
        char* pdtc;
        char* ptime;

        jclass    cls ;
        jfieldID  fid_str ;
        jfieldID  fid_str_en;

        jstring jstr_str_state;
        char* p_str_state;
        jstring jstr_str_en_state;
        char* p_str_en_state;

        jstring jstr_str_desc;
        char* p_str_desc;
        jstring jstr_str_en_desc ;
        char* p_str_en_desc;

        LSX_STRING lsx_state, lsx_desc;

        int iRet;


        if ( 0 == grp )
        {
#ifdef WIN32
            printf("lsx_rec_writedtc: parameter[grp] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_writedtc: parameter[grp] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_writedtc: parameter[grp] invalid");
#endif
#endif
            return (jint)-1;
        } 

        pdtc = jstringToChar(env, dtc);
        ptime = jstringToChar(env, time);

        cls   = (*env)->FindClass(env, "com/cnlaunch/mycar/jni/LSX_STRING");
        fid_str = (*env)->GetFieldID(env, cls,"str","Ljava/lang/String;");   
        fid_str_en = (*env)->GetFieldID(env, cls,"str_en","Ljava/lang/String;");   

        jstr_str_state = (*env)->GetObjectField(env, state, fid_str);
        p_str_state = jstringToChar(env, jstr_str_state);
        jstr_str_en_state = (*env)->GetObjectField(env, state, fid_str_en);
        p_str_en_state = jstringToChar(env, jstr_str_en_state);

        jstr_str_desc = (*env)->GetObjectField(env, desc, fid_str);
        p_str_desc = jstringToChar(env, jstr_str_desc);
        jstr_str_en_desc = (*env)->GetObjectField(env, desc, fid_str_en);
        p_str_en_desc = jstringToChar(env, jstr_str_en_desc);

        lsx_state.str = p_str_state;
        lsx_state.str_en = p_str_en_state;
        lsx_desc.str = p_str_desc;
        lsx_desc.str_en = p_str_en_desc;

#ifdef WIN32
        printf("lsx_rec_writedtc: "
            "lsx_state.str=[%s], lsx_state.str_en=[%s], lsx_desc.str=[%s], lsx_desc.str_en=[%s]\n", 
            lsx_state.str, lsx_state.str_en, lsx_desc.str = p_str_desc, lsx_desc.str_en = p_str_en_desc);
#else
#ifdef __linux
        printf("lsx_rec_writedtc: "
            "lsx_state.str=[%s], lsx_state.str_en=[%s], lsx_desc.str=[%s], lsx_desc.str_en=[%s]\n", 
            lsx_state.str, lsx_state.str_en, lsx_desc.str = p_str_desc, lsx_desc.str_en = p_str_en_desc);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_writedtc: "
            "lsx_state.str=[%s], lsx_state.str_en=[%s], lsx_desc.str=[%s], lsx_desc.str_en=[%s]", 
            lsx_state.str, lsx_state.str_en, lsx_desc.str = p_str_desc, lsx_desc.str_en = p_str_en_desc); 
#endif
#endif

        iRet = lsx_rec_writedtc((LSX_RECGROUP)grp, pdtc, &lsx_state, &lsx_desc, ptime);

#ifdef WIN32
        printf("lsx_rec_writedtc: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_writedtc: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_writedtc: iRet=[%d]", iRet); 
#endif
#endif

        free(pdtc);
        free(ptime);
        free(p_str_state);
        free(p_str_en_state);
        free(p_str_desc);
        free(p_str_en_desc);           

        return iRet;

    }


    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_writevi
    * Signature: (ILcom/cnlaunch/mycar/jni/LSX_STRING;)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1writevi
        (JNIEnv *env, jobject obj, jint grp, jobject vi)
    {
        jclass    cls ;
        jfieldID  fid_str ;
        jfieldID  fid_str_en ;

        jstring jstr_str_vi;
        char* p_str_vi;
        jstring jstr_str_en_vi;
        char* p_str_en_vi;

        LSX_STRING lsx_vi;

        int iRet;

        if ( 0 == grp )
        {
#ifdef WIN32
            printf("lsx_rec_writevi: parameter[grp] invalid\n");
#else
#ifdef __linux
             printf("lsx_rec_writevi: parameter[grp] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_writevi: parameter[grp] invalid");
#endif
#endif
            return (jint)-1;
        } 

        cls   = (*env)->FindClass(env, "com/cnlaunch/mycar/jni/LSX_STRING");
        fid_str = (*env)->GetFieldID(env, cls,"str","Ljava/lang/String;");   
        fid_str_en = (*env)->GetFieldID(env, cls,"str_en","Ljava/lang/String;");   

        jstr_str_vi = (*env)->GetObjectField(env, vi, fid_str);
        p_str_vi = jstringToChar(env, jstr_str_vi);
        jstr_str_en_vi = (*env)->GetObjectField(env, vi, fid_str_en);
        p_str_en_vi = jstringToChar(env, jstr_str_en_vi);


        lsx_vi.str = p_str_vi;
        lsx_vi.str_en = p_str_en_vi;

        iRet = lsx_rec_writevi((LSX_RECGROUP)grp, &lsx_vi);

#ifdef WIN32
        printf("lsx_rec_writevi: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_writevi: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_writevi: iRet=[%d]", iRet); 
#endif
#endif

        free(p_str_vi);
        free(p_str_en_vi);

        return iRet;

    }


    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_writedsbasics
    * Signature: (I[Lcom/cnlaunch/mycar/jni/LSX_STRING;[Lcom/cnlaunch/mycar/jni/LSX_STRING;[II)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1writedsbasics
        (JNIEnv *env, jobject obj, jint grp, jobjectArray namestrs, jobjectArray unitstrs, jintArray type, jint n)
    {
        int len_namestrs ;
        int len_unitstrs;

        jboolean jb;
        int len_type;
        int *ptype;
        jint *jptype;

        int i;

        jclass    cls ;


        jfieldID  fid_str;
        jfieldID  fid_str_en ;

        LSX_STRING **pNames;
        LSX_STRING **pUnits;

        jobject obj_name;

        jstring jstr_str_name;
        char* p_str_name;

        jstring jstr_str_en_name;
        char* p_str_en_name;


        jobject obj_text;

        jstring jstr_str_text;
        char* p_str_text;

        jstring jstr_str_en ;
        char* p_str_en_text;

        int iRet;

        int iCount;

        if ( 0 == grp )
        {
#ifdef WIN32
            printf("lsx_rec_writedsbasics: parameter[grp] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_writedsbasics: parameter[grp] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_writedsbasics: parameter[grp] invalid");
#endif
#endif
            return (jint)-1;
        } 

        /* 获取数组对象的元素 */        
        len_namestrs = (*env)->GetArrayLength(env, namestrs);
        if ( len_namestrs <= 0 )
        {
#ifdef WIN32
            printf("lsx_rec_writedsbasics: parameter[namestrs] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_writedsbasics: parameter[namestrs] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_writedsbasics: parameter[namestrs] invalid");
#endif
#endif
            return (jint)-1;
        }

        len_unitstrs = (*env)->GetArrayLength(env, unitstrs);
        if ( len_unitstrs <= 0 )
        {
#ifdef WIN32
            printf( "lsx_rec_writedsbasics: parameter[unitstrs] invalid\n");
#else
#ifdef __linux
             printf( "lsx_rec_writedsbasics: parameter[unitstrs] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_writedsbasics: parameter[unitstrs] invalid");
#endif
#endif
            return (jint)-1;
        }

        if ( n <= 0 )
        {
#ifdef WIN32
            printf( "lsx_rec_writedsbasics: parameter[n] invalid\n");
#else
#ifdef __linux
            printf( "lsx_rec_writedsbasics: parameter[n] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_writedsbasics: parameter[n] invalid");
#endif
#endif
            return (jint)-1;
        }  

        jb = JNI_FALSE;
        len_type = (*env)->GetArrayLength(env, type);
        ptype = malloc(sizeof(int) * len_type);
        jptype = (*env)->GetIntArrayElements(env, type, &jb);  

        i = 0;
        for ( ; i< len_type; i++)
        {
            ptype[i] = jptype[i];
        }

        cls   = (*env)->FindClass(env, "com/cnlaunch/mycar/jni/LSX_STRING");   

        //         jmethodID m_mid   = (*env)->GetMethodID(env, m_cls,"<init>","()V");   

        fid_str = (*env)->GetFieldID(env, cls,"str","Ljava/lang/String;");   
        fid_str_en = (*env)->GetFieldID(env, cls,"str_en","Ljava/lang/String;");   

        iCount = (len_namestrs > n) ? n : len_namestrs;
        iCount = (iCount > len_unitstrs) ? len_unitstrs : iCount;

#ifdef WIN32
        printf( "lsx_rec_writedsbasics: iCount=[%d]\n", iCount);
#else
#ifdef __linux
        printf( "lsx_rec_writedsbasics: iCount=[%d]\n", iCount);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_writedsbasics: iCount=[%d]", iCount);
#endif
#endif

        pNames = malloc(sizeof(LSX_STRING*) * iCount);
        pUnits = malloc(sizeof(LSX_STRING*) * iCount);

        i = 0;
        for ( ; i < iCount; i++)
        {
            pNames[i] = malloc(sizeof(LSX_STRING*));
            pUnits[i] = malloc(sizeof(LSX_STRING*));

            obj_name = (*env)->GetObjectArrayElement(env, namestrs, i);

            jstr_str_name = (*env)->GetObjectField(env, obj_name, fid_str);
            p_str_name = jstringToChar(env, jstr_str_name);
            pNames[i]->str = p_str_name;

            jstr_str_en_name = (*env)->GetObjectField(env, obj_name, fid_str_en);
            p_str_en_name = jstringToChar(env, jstr_str_en_name);
            pNames[i]->str_en = p_str_en_name;


            obj_text = (*env)->GetObjectArrayElement(env, unitstrs, i);

            jstr_str_text = (*env)->GetObjectField(env, obj_text, fid_str);
            p_str_text = jstringToChar(env, jstr_str_text);
            pUnits[i]->str = p_str_text;

            jstr_str_en = (*env)->GetObjectField(env, obj_text, fid_str_en);
            p_str_en_text = jstringToChar(env, jstr_str_en);
            pUnits[i]->str_en = p_str_en_text;
        }

        iRet = lsx_rec_writedsbasics((LSX_RECGROUP)grp, pNames, pUnits, (const unsigned short*)ptype, n);        

#ifdef WIN32
        printf("lsx_rec_writedsbasics: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_writedsbasics: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_writedsbasics: iRet=[%d]", iRet); 
#endif
#endif

        i = 0;
        for ( ; i < iCount; i++)
        {
            free(pNames[i]->str);
            free(pNames[i]->str_en);
            free(pNames[i]);

            free(pUnits[i]->str);
            free(pUnits[i]->str_en);
            free(pUnits[i]);
        }

        free(pNames);
        free(pUnits);

        free(ptype);

        (*env)->ReleaseIntArrayElements(env, type, jptype, 0);

        return iRet;
    }

    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_writeds
    * Signature: (I[Lcom/cnlaunch/mycar/jni/LSX_STRING;I)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1writeds
        (JNIEnv *env, jobject obj, jint grp, jobjectArray itemstrs, jint n)
    {
        int len_itemstrs;

        jclass    cls; 

        jfieldID  fid_str;
        jfieldID  fid_str_en;

        int iCount;

        LSX_STRING **pItems;

        int i;

        int iRet;

        jobject obj_item ;

        jstring jstr_str_item ;
        char* p_str_item ;

        jstring jstr_str_en_item ;
        char* p_str_en_item;

        if ( 0 == grp )
        {
#ifdef WIN32
            printf("lsx_rec_writeds: parameter[grp] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_writeds: parameter[grp] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_writeds: parameter[grp] invalid");
#endif
#endif
            return (jint)-1;
        } 

        /* 获取数组对象的元素 */        
        len_itemstrs = (*env)->GetArrayLength(env, itemstrs);
        if ( len_itemstrs <= 0 )
        {
#ifdef WIN32
            printf("lsx_rec_writeds: parameter[itemstrs] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_writeds: parameter[itemstrs] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_writeds: parameter[itemstrs] invalid");
#endif
#endif
            return (jint)-1;
        }        

        if ( n <= 0 )
        {
#ifdef WIN32
            printf( "lsx_rec_writeds: parameter[n] invalid\n");
#else
#ifdef __linux
            printf( "lsx_rec_writeds: parameter[n] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_writeds: parameter[n] invalid");
#endif
#endif
            return (jint)-1;
        }        

        cls   = (*env)->FindClass(env, "com/cnlaunch/mycar/jni/LSX_STRING");           

        fid_str = (*env)->GetFieldID(env, cls,"str","Ljava/lang/String;");   
        fid_str_en = (*env)->GetFieldID(env, cls,"str_en","Ljava/lang/String;");   

        iCount = (len_itemstrs > n) ? n : len_itemstrs;

#ifdef WIN32
        printf( "lsx_rec_writeds: iCount=[%d]\n", iCount);
#else
#ifdef __linux
        printf( "lsx_rec_writeds: iCount=[%d]\n", iCount);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_writeds: iCount=[%d]", iCount);
#endif
#endif

        pItems = malloc(sizeof(LSX_STRING*) * iCount);

        i = 0;
        for ( ; i < iCount; i++)
        {
            pItems[i] = malloc(sizeof(LSX_STRING*));

            obj_item = (*env)->GetObjectArrayElement(env, itemstrs, i);

            jstr_str_item = (*env)->GetObjectField(env, obj_item, fid_str);
            p_str_item = jstringToChar(env, jstr_str_item);
            pItems[i]->str = p_str_item;

            jstr_str_en_item = (*env)->GetObjectField(env, obj_item, fid_str_en);
            p_str_en_item = jstringToChar(env, jstr_str_en_item);
            pItems[i]->str_en = p_str_en_item;                      

        }

        iRet = lsx_rec_writeds((LSX_RECGROUP)grp, pItems, (int)n);

#ifdef WIN32
        printf(  "lsx_rec_writeds: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf(  "lsx_rec_writeds: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_writeds: iRet=[%d]", iRet); 
#endif
#endif

        i = 0;
        for ( ; i < iCount; i++)
        {
            free(pItems[i]->str);
            free(pItems[i]->str_en);
            free(pItems[i]);
        }

        free(pItems);

        return iRet;       

    }


    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_writefreezeframe
    * Signature: (ILjava/lang/String;[Lcom/cnlaunch/mycar/jni/LSX_STRING;[Lcom/cnlaunch/mycar/jni/LSX_STRING;[I[Lcom/cnlaunch/mycar/jni/LSX_STRING;I)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1writefreezeframe
        (JNIEnv *env, jobject obj, jint grp, jstring dtc, jobjectArray namestrs, jobjectArray unitstrs, jintArray type, jobjectArray textstrs, jint n)
    {
        int len_namestrs;
        int len_unitstrs;
        int len_textstrs;
        char* pdtc ;

        jboolean jb;
        jint *jptype ;

        int len_type;
        int *ptype;
        int i;

        jclass    cls  ;

        jfieldID  fid_str ;
        jfieldID  fid_str_en;

        int iCount;

        LSX_STRING **pNames ;
        LSX_STRING **pUnits;
        LSX_STRING **pTexts;

        jobject obj_name;
        jstring jstr_str_name ;
        char* p_str_name ;
        jstring jstr_str_en_name ;
        char* p_str_en_name;


        jobject obj_unit ;
        jstring jstr_str_unit ;
        char* p_str_unit ;
        jstring jstr_str_en_unit ;
        char* p_str_en_unit;

        jobject obj_text;
        jstring jstr_str_text ;
        char* p_str_text;
        jstring jstr_str_en_text ;
        char* p_str_en_text;

        int iRet;

        if ( 0 == grp )
        {
#ifdef WIN32
            printf("lsx_rec_writefreezeframe: parameter[grp] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_writefreezeframe: parameter[grp] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_writefreezeframe: parameter[grp] invalid");
#endif
#endif
            return (jint)-1;
        } 

        /* 获取数组对象的元素 */        
        len_namestrs = (*env)->GetArrayLength(env, namestrs);
        if ( len_namestrs <= 0 )
        {
#ifdef WIN32
            printf("lsx_rec_writefreezeframe: parameter[namestrs] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_writefreezeframe: parameter[namestrs] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_writefreezeframe: parameter[namestrs] invalid");
#endif
#endif
            return (jint)-1;
        }

        len_unitstrs = (*env)->GetArrayLength(env, unitstrs);
        if ( len_unitstrs <= 0 )
        {
#ifdef WIN32
            printf("lsx_rec_writefreezeframe: parameter[unitstrs] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_writefreezeframe: parameter[unitstrs] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_writefreezeframe: parameter[unitstrs] invalid");
#endif
#endif
            return (jint)-1;
        }

        len_textstrs = (*env)->GetArrayLength(env, textstrs);
        if ( len_textstrs <= 0 )
        {
#ifdef WIN32
            printf("lsx_rec_writefreezeframe: parameter[textstrs] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_writefreezeframe: parameter[textstrs] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_writefreezeframe: parameter[textstrs] invalid");
#endif
#endif
            return (jint)-1;
        }

        if ( n <= 0 )
        {
#ifdef WIN32
            printf("lsx_rec_writefreezeframe: parameter[n] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_writefreezeframe: parameter[n] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_writefreezeframe: parameter[n] invalid");
#endif
#endif
            return (jint)-1;
        }

        pdtc = jstringToChar(env, dtc);

        jb = JNI_FALSE;
        jptype =  (*env)->GetIntArrayElements(env, type, &jb);       

        len_type = (*env)->GetArrayLength(env, type);
        ptype = malloc(sizeof(int) * len_type);
        i = 0;
        for ( ; i< len_type; i++)
        {
            ptype[i] = jptype[i];
        }


        cls   = (*env)->FindClass(env, "com/cnlaunch/mycar/jni/LSX_STRING");   

        fid_str = (*env)->GetFieldID(env, cls,"str","Ljava/lang/String;");   
        fid_str_en = (*env)->GetFieldID(env, cls,"str_en","Ljava/lang/String;");   

        iCount = (len_namestrs > n) ? n : len_namestrs;
        iCount = (iCount > len_unitstrs) ? len_unitstrs : iCount;
        iCount = (iCount > len_textstrs) ? len_textstrs : iCount;

#ifdef WIN32
        printf( "lsx_rec_writefreezeframe: iCount=[%d]\n", iCount);
#else
#ifdef __linux
        printf( "lsx_rec_writefreezeframe: iCount=[%d]\n", iCount);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_writefreezeframe: iCount=[%d]", iCount);
#endif
#endif

        pNames = malloc(sizeof(LSX_STRING*) * iCount);
        pUnits = malloc(sizeof(LSX_STRING*) * iCount);
        pTexts = malloc(sizeof(LSX_STRING*) * iCount);

        i = 0;
        for ( ; i < iCount; i++)
        {
            pNames[i] = malloc(sizeof(LSX_STRING*));
            pUnits[i] = malloc(sizeof(LSX_STRING*));
            pTexts[i] = malloc(sizeof(LSX_STRING*));

            obj_name = (*env)->GetObjectArrayElement(env, namestrs, i);            
            jstr_str_name = (*env)->GetObjectField(env, obj_name, fid_str);
            p_str_name = jstringToChar(env, jstr_str_name);
            pNames[i]->str = p_str_name;            
            jstr_str_en_name = (*env)->GetObjectField(env, obj_name, fid_str_en);
            p_str_en_name = jstringToChar(env, jstr_str_en_name);
            pNames[i]->str_en = p_str_en_name;


            obj_unit = (*env)->GetObjectArrayElement(env, unitstrs, i);            
            jstr_str_unit = (*env)->GetObjectField(env, obj_unit, fid_str);
            p_str_unit = jstringToChar(env, jstr_str_unit);
            pUnits[i]->str = p_str_unit;            
            jstr_str_en_unit = (*env)->GetObjectField(env, obj_unit, fid_str_en);
            p_str_en_unit = jstringToChar(env, jstr_str_en_unit);
            pUnits[i]->str_en = p_str_en_unit;

            obj_text = (*env)->GetObjectArrayElement(env, textstrs, i);            
            jstr_str_text = (*env)->GetObjectField(env, obj_text, fid_str);
            p_str_text = jstringToChar(env, jstr_str_text);
            pTexts[i]->str = p_str_text;            
            jstr_str_en_text = (*env)->GetObjectField(env, obj_text, fid_str_en);
            p_str_en_text = jstringToChar(env, jstr_str_en_text);
            pTexts[i]->str_en = p_str_en_text;
        }

        iRet = lsx_rec_writefreezeframe((LSX_RECGROUP)grp, pdtc, pNames, pUnits, (const unsigned short *)ptype, pTexts, n);

#ifdef WIN32
        printf("lsx_rec_writefreezeframe: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_writefreezeframe: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_writefreezeframe: iRet=[%d]", iRet); 
#endif
#endif

        i = 0;
        for ( ; i < iCount; i++)
        {
            free(pNames[i]->str);
            free(pNames[i]->str_en);
            free(pNames[i]);

            free(pUnits[i]->str);
            free(pUnits[i]->str_en);
            free(pUnits[i]);

            free(pTexts[i]->str);
            free(pTexts[i]->str_en);
            free(pTexts[i]);

        }

        free(pNames);
        free(pUnits);
        free(pTexts);

        free(pdtc);
        free(ptype);

        (*env)->ReleaseIntArrayElements(env, type, jptype, 0);

        return iRet;
    }


    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_finishnewgroup
    * Signature: (ILjava/lang/String;)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1finishnewgroup
        (JNIEnv *env, jobject obj, jint grp, jstring endtime)
    {
        char* pendtime ;
        int iRet ;

        if ( 0 == grp )
        {   
#ifdef WIN32
            printf("lsx_rec_finishnewgroup: parameter[grp] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_finishnewgroup: parameter[grp] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_finishnewgroup: parameter[grp] invalid");
#endif
#endif
            return (jint)-1;
        } 

        pendtime = jstringToChar(env, endtime);

        iRet = lsx_rec_finishnewgroup((LSX_RECGROUP)grp, pendtime);


#ifdef WIN32
        printf("lsx_rec_finishnewgroup: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_finishnewgroup: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_finishnewgroup: iRet=[%d]", iRet); 
#endif
#endif

        free(pendtime);

        return iRet;
    }

    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_readalltype
    * Signature: (I)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1readalltype
        (JNIEnv *env, jobject obj, jint grp)
    {
        int iRet;

        if ( 0 == grp )
        {
#ifdef WIN32
            printf("lsx_rec_readalltype: parameter[grp] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_readalltype: parameter[grp] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readalltype: parameter[grp] invalid");
#endif
#endif
            return (jint)-1;
        } 

        iRet = lsx_rec_readalltype((LSX_RECGROUP)grp);

#ifdef WIN32
        printf("lsx_rec_readalltype: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_readalltype: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readalltype: iRet=[%d]", iRet); 
#endif
#endif

        return iRet;

    }

    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_readfirstitem
    * Signature: (I)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1readfirstitem
        (JNIEnv *env, jobject obj, jint grp)
    {
        int iRet ;
        if ( 0 == grp )
        {
#ifdef WIN32
            printf("lsx_rec_readfirstitem: parameter[grp] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_readfirstitem: parameter[grp] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readfirstitem: parameter[grp] invalid");
#endif
#endif
            return (jint)-1;
        } 

        iRet = (int)lsx_rec_readfirstitem((LSX_RECGROUP)grp);

#ifdef WIN32
        printf("lsx_rec_readfirstitem: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_readfirstitem: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readfirstitem: iRet=[%d]", iRet); 
#endif
#endif

        return iRet;
    }

    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_readitemtype
    * Signature: (I)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1readitemtype
        (JNIEnv *env, jobject obj, jint item)
    {
        int iRet;

        if ( 0 == item )
        {
#ifdef WIN32
            printf("lsx_rec_readitemtype: parameter[item] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_readitemtype: parameter[item] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readitemtype: parameter[item] invalid");
#endif
#endif
            return (jint)-1;
        } 

        iRet = lsx_rec_readitemtype((LSX_RECITEM)item);

#ifdef WIN32
        printf("lsx_rec_readitemtype: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_readitemtype: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readitemtype: iRet=[%d]", iRet); 
#endif
#endif

        return iRet;
    }

    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_readnextitem
    * Signature: (I)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1readnextitem
        (JNIEnv *env, jobject obj, jint item)
    {
        int iRet;

        if ( 0 == item )
        {
#ifdef WIN32
            printf("lsx_rec_readnextitem: parameter[item] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_readnextitem: parameter[item] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readnextitem: parameter[item] invalid");
#endif
#endif
            return (jint)-1;
        } 

        iRet = (int)lsx_rec_readnextitem((LSX_RECITEM)item);

#ifdef WIN32
        printf("lsx_rec_readnextitem: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_readnextitem: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readnextitem: iRet=[%d]", iRet); 
#endif
#endif

        return iRet;
    }

    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_readlastitem
    * Signature: (I)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1readlastitem
        (JNIEnv *env, jobject obj, jint grp)
    {
        int iRet;

        if ( 0 == grp )
        {
#ifdef WIN32
            printf("lsx_rec_readlastitem: parameter[grp] invalid\n");
#else
#ifdef __linux
             printf("lsx_rec_readlastitem: parameter[grp] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readlastitem: parameter[grp] invalid");
#endif
#endif
            return (jint)-1;
        } 

        iRet = (int)lsx_rec_readlastitem((LSX_RECGROUP)grp);

#ifdef WIN32
        printf("lsx_rec_readlastitem: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_readlastitem: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readlastitem: iRet=[%d]", iRet); 
#endif
#endif

        return iRet;
    }

    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_readprevitem
    * Signature: (I)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1readprevitem
        (JNIEnv *env, jobject obj, jint item)
    {
        int iRet;

        if ( 0 == item )
        {
#ifdef WIN32
            printf("lsx_rec_readprevitem: parameter[item] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_readprevitem: parameter[item] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readprevitem: parameter[item] invalid");
#endif
#endif
            return (jint)-1;
        } 

        iRet = (int)lsx_rec_readprevitem((LSX_RECITEM)item);

#ifdef WIN32
        printf("lsx_rec_readprevitem: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_readprevitem: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readprevitem: iRet=[%d]", iRet); 
#endif
#endif

        return iRet;
    }

    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_readlastdtcitem
    * Signature: (I)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1readlastdtcitem
        (JNIEnv *env, jobject obj, jint grp)
    {
        int iRet;

        if ( 0 == grp )
        {
#ifdef WIN32
            printf("lsx_rec_readlastdtcitem: parameter[grp] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_readlastdtcitem: parameter[grp] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readlastdtcitem: parameter[grp] invalid");
#endif
#endif
            return (jint)-1;
        } 

        iRet = (int)lsx_rec_readlastdtcitem((LSX_RECGROUP)grp);

#ifdef WIN32
        printf("lsx_rec_readlastdtcitem: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_readlastdtcitem: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readlastdtcitem: iRet=[%d]", iRet); 
#endif
#endif

        return iRet;
    }

    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_readprevdtcitem
    * Signature: (I)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1readprevdtcitem
        (JNIEnv *env, jobject obj, jint item)
    {
        int iRet ;

        if ( 0 == item )
        {
#ifdef WIN32
            printf("lsx_rec_readprevdtcitem: parameter[item] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_readprevdtcitem: parameter[item] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readprevdtcitem: parameter[item] invalid");
#endif
#endif
            return (jint)-1;
        } 

        iRet = (int)lsx_rec_readprevdtcitem((LSX_RECITEM)item);

#ifdef WIN32
        printf("lsx_rec_readprevdtcitem: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_readprevdtcitem: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readprevdtcitem: iRet=[%d]", iRet); 
#endif
#endif

        return iRet;
    }


    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_readffitemcount
    * Signature: (I)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1readffitemcount
        (JNIEnv *env, jobject obj, jint grp)
    {
        int iRet;

        if ( 0 == grp )
        {
#ifdef WIN32
            printf("lsx_rec_readffitemcount: parameter[grp] invalid\n");
#else
#ifdef __linux
             printf("lsx_rec_readffitemcount: parameter[grp] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readffitemcount: parameter[grp] invalid");
#endif
#endif
            return (jint)-1;
        } 

        iRet = (int)lsx_rec_readffitemcount((LSX_RECGROUP)grp);

#ifdef WIN32
        printf("lsx_rec_readffitemcount: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_readffitemcount: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readffitemcount: iRet=[%d]", iRet); 
#endif
#endif

        return iRet;
    }


    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_readfirstffitem
    * Signature: (I)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1readfirstffitem
        (JNIEnv *env, jobject obj, jint grp)
    {
        int iRet;

        if ( 0 == grp )
        {
#ifdef WIN32
            printf("lsx_rec_readfirstffitem: parameter[grp] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_readfirstffitem: parameter[grp] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readfirstffitem: parameter[grp] invalid");
#endif
#endif
            return (jint)-1;
        } 

        iRet = (int)lsx_rec_readfirstffitem((LSX_RECGROUP)grp);

#ifdef WIN32
        printf("lsx_rec_readfirstffitem: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_readfirstffitem: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readfirstffitem: iRet=[%d]", iRet); 
#endif
#endif

        return iRet;
    }

    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_readfreezeframe
    * Signature: (ILcom/cnlaunch/mycar/jni/X431String;[Ljava/lang/String;I)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1readfreezeframe
        (JNIEnv *env, jobject obj, jint item, jobject dtc, jobjectArray textstrs, jint n)
    {
        int len;
        int iCount ;

        char *pdtc;
        char **pptextstrs;

        int iRet;

        int iTmp ;
        jstring jstr;
        jclass clsstr;
        jfieldID  fid_strValue;

        jstring jstr_dtc ;

        if ( 0 == item )
        {
#ifdef WIN32
            printf("lsx_rec_readfreezeframe: parameter[item] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_readfreezeframe: parameter[item] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readfreezeframe: parameter[item] invalid");
#endif
#endif
            return (jint)-1;
        } 


        len = (*env)->GetArrayLength(env, textstrs);
        iCount = (len > n) ? n : len;

        pdtc;
        pptextstrs = malloc(sizeof(char*) * (int)iCount);

        iRet = lsx_rec_readfreezeframe((LSX_RECITEM)item, &pdtc, pptextstrs, (int)n);

#ifdef WIN32
        printf("lsx_rec_readfreezeframe: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_readfreezeframe: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readfreezeframe: iRet=[%d]", iRet); 
#endif
#endif

        // 将结果传递给jobjectArray textstrs
        iTmp = 0;
        for ( ; iTmp<iCount; iTmp++)
        {
            jstr = CharTojstring(env, pptextstrs[iTmp]);
            (*env)->SetObjectArrayElement(env, textstrs, iTmp, jstr);
        }

        // 参数转换
        clsstr = (*env)->FindClass(env, "com/cnlaunch/mycar/jni/X431String");      
        if(clsstr == 0)
        {    
#ifdef WIN32
            printf("lsx_rec_readfreezeframe: [FindClass->X431String] error\n");
#else
#ifdef __linux
            printf("lsx_rec_readfreezeframe: [FindClass->X431String] error\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readfreezeframe: [FindClass->X431String] error");
#endif
#endif
            return (jint)-2; 
        }    

        fid_strValue = (*env)->GetFieldID(env, clsstr, "mValue","Ljava/lang/String;");         

        jstr_dtc = CharTojstring(env, pdtc);

        (*env)->SetObjectField(env, dtc, fid_strValue, jstr_dtc);        

        free(pptextstrs);

        return iRet;
    }


    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_readnextffitem
    * Signature: (I)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1readnextffitem
        (JNIEnv *env, jobject obj, jint item)
    {
        int iRet;

        if ( 0 == item )
        {
#ifdef WIN32
            printf("lsx_rec_readnextffitem: parameter[item] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_readnextffitem: parameter[item] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readnextffitem: parameter[item] invalid");
#endif
#endif
            return (jint)-1;
        } 

        iRet = (int)lsx_rec_readnextffitem((LSX_RECITEM)item);

#ifdef WIN32
        printf("lsx_rec_readnextffitem: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_readnextffitem: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readnextffitem: iRet=[%d]", iRet); 
#endif
#endif

        return iRet;
    }

    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_readffcolcount
    * Signature: (I)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1readffcolcount
        (JNIEnv *env, jobject obj, jint item)
    {
        int iRet;

        if ( 0 == item )
        {
#ifdef WIN32
            printf("lsx_rec_readffcolcount: parameter[item] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_readffcolcount: parameter[item] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readffcolcount: parameter[item] invalid");
#endif
#endif
            return (jint)-1;
        } 

        iRet = (int)lsx_rec_readffcolcount((LSX_RECITEM)item);

#ifdef WIN32
        printf("lsx_rec_readffcolcount: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_readffcolcount: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readffcolcount: iRet=[%d]", iRet); 
#endif
#endif

        return iRet;
    }

    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_readlastffitem
    * Signature: (I)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1readlastffitem
        (JNIEnv *env, jobject obj, jint grp)
    {
        int iRet;

        if ( 0 == grp )
        {
#ifdef WIN32
            printf("lsx_rec_readlastffitem: parameter[grp] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_readlastffitem: parameter[grp] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readlastffitem: parameter[grp] invalid");
#endif
#endif
            return (jint)-1;
        } 

        iRet = (int)lsx_rec_readlastffitem((LSX_RECGROUP)grp);

#ifdef WIN32
        printf("lsx_rec_readlastffitem: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_readlastffitem: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readlastffitem: iRet=[%d]", iRet); 
#endif
#endif

        return iRet;
    }

    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_readprevffitem
    * Signature: (I)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1readprevffitem
        (JNIEnv *env, jobject obj, jint item)
    {
        int iRet;

        if ( 0 == item )
        {
#ifdef WIN32
            printf("lsx_rec_readprevffitem: parameter[item] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_readprevffitem: parameter[item] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readprevffitem: parameter[item] invalid");
#endif
#endif
            return (jint)-1;
        } 

        iRet = (int)lsx_rec_readprevffitem((LSX_RECITEM)item);

#ifdef WIN32
        printf("lsx_rec_readprevffitem: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_readprevffitem: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readprevffitem: iRet=[%d]", iRet); 
#endif
#endif

        return iRet;
    }

    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_selectreadtextlang
    * Signature: (ILjava/lang/String;)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1selectreadtextlang
        (JNIEnv *env, jobject obj, jint lsx_file, jstring langcode)
    {
        char* plangcode;

        int iRet;


        if ( 0 == lsx_file )
        {
#ifdef WIN32
            printf("lsx_selectreadtextlang: parameter[lsx_file] invalid\n");
#else
#ifdef __linux
            printf("lsx_selectreadtextlang: parameter[lsx_file] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_selectreadtextlang: parameter[lsx_file] invalid");
#endif
#endif
            return (jint)-1;
        } 

        plangcode = jstringToChar(env, langcode);

        iRet = lsx_selectreadtextlang((LSX_FILE)lsx_file, plangcode);

#ifdef WIN32
        printf("lsx_selectreadtextlang: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_selectreadtextlang: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_selectreadtextlang: iRet=[%d]", iRet); 
#endif
#endif

        free(plangcode);

        return iRet;

    }



    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_readlastdsitem
    * Signature: (I)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1readlastdsitem
        (JNIEnv *env, jobject obj, jint grp)
    {
        int iRet;

        if ( 0 == grp )
        {
#ifdef WIN32
            printf("lsx_rec_readlastdsitem: parameter[grp] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_readlastdsitem: parameter[grp] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readlastdsitem: parameter[grp] invalid");
#endif
#endif
            return (jint)-1;
        } 

        iRet = (int)lsx_rec_readlastdsitem((LSX_RECGROUP)grp);

#ifdef WIN32
        printf("lsx_rec_readlastdsitem: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_readlastdsitem: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readlastdsitem: iRet=[%d]", iRet); 
#endif
#endif

        return iRet;
    }

    /*
    * Class:     com_cnlaunch_mycar_jni_JniX431File
    * Method:    lsx_rec_readgroupinfo
    * Signature: (ILcom/cnlaunch/mycar/jni/X431String;Lcom/cnlaunch/mycar/jni/X431String;Lcom/cnlaunch/mycar/jni/X431String;Lcom/cnlaunch/mycar/jni/X431String;Lcom/cnlaunch/mycar/jni/X431String;Lcom/cnlaunch/mycar/jni/X431Integer;)I
    */
    JNIEXPORT jint JNICALL Java_com_cnlaunch_mycar_jni_JniX431File_lsx_1rec_1readgroupinfo
        (JNIEnv *env, jobject obj, jint grp, jobject name, jobject protocol, jobject vin, jobject starttime, jobject endtime, jobject dsinterval)        
    {
        int iRet;
        jclass clsstr;
        jclass cls;
        jfieldID  fid_strValue;
        jfieldID fid_mValue;

        char*pname,*pprotocol,*pvin,*pstarttime,*pendtime;
        int idsinterval;

        jstring jstr_name;
        jstring jstr_protocol;
        jstring jstr_vin;
        jstring jstr_starttime;
        jstring jstr_endtime;
        jint jidsinterval;

        if ( 0 == grp )
        {
#ifdef WIN32
            printf("lsx_rec_readgroupinfo: parameter[grp] invalid\n");
#else
#ifdef __linux
            printf("lsx_rec_readgroupinfo: parameter[grp] invalid\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readgroupinfo: parameter[grp] invalid");
#endif
#endif
            return (jint)-1;
        } 

        // 参数转换
        clsstr = (*env)->FindClass(env, "com/cnlaunch/mycar/jni/X431String");      
        if(clsstr == 0)
        {    
#ifdef WIN32
            printf("lsx_open: [FindClass->X431String] error\n");
#else
#ifdef __linux
            printf("lsx_open: [FindClass->X431String] error\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readgroupinfo: [FindClass->X431String] error");
#endif
#endif
            return (jint)-2; 
        }    

        fid_strValue = (*env)->GetFieldID(env, clsstr, "mValue","Ljava/lang/String;"); 



        cls = (*env)->FindClass(env, "com/cnlaunch/mycar/jni/X431Integer");      
        if(cls == 0)
        {    
#ifdef WIN32
            printf("lsx_rec_readgroupinfo: [FindClass->X431Integer] error\n");
#else
#ifdef __linux
            printf("lsx_rec_readgroupinfo: [FindClass->X431Integer] error\n");
#else
            __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readgroupinfo: [FindClass->X431Integer] error");
#endif
#endif
            return (jint)-2; 
        }    

        // 获取error对象mValue字段值
        fid_mValue = (*env)->GetFieldID(env, cls, "mValue","I");  


        iRet = lsx_rec_readgroupinfo((LSX_RECGROUP)grp, &pname, &pprotocol, &pvin, &pstarttime, &pendtime, &idsinterval);

#ifdef WIN32
        printf("lsx_rec_readgroupinfo: iRet=[%d]\n", iRet);
#else
#ifdef __linux
        printf("lsx_rec_readgroupinfo: iRet=[%d]\n", iRet);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", "lsx_rec_readgroupinfo: iRet=[%d]", iRet); 
#endif
#endif

#ifdef WIN32
        printf("lsx_rec_readgroupinfo: pname=[%s], pprotocol=[%s], pvin=[%s], pstarttime=[%s], pendtime=[%s], idsinterval=[%d]\n", 
            pname, pprotocol, pvin, pstarttime, pendtime, idsinterval);
#else
#ifdef __linux
        printf("lsx_rec_readgroupinfo: pname=[%s], pprotocol=[%s], pvin=[%s], pstarttime=[%s], pendtime=[%s], idsinterval=[%d]\n", 
            pname, pprotocol, pvin, pstarttime, pendtime, idsinterval);
#else
        __android_log_print(ANDROID_LOG_INFO, "JniX431FileTest", 
            "lsx_rec_readgroupinfo: pname=[%s], pprotocol=[%s], pvin=[%s], pstarttime=[%s], pendtime=[%s], idsinterval=[%d]\n", 
            pname, pprotocol, pvin, pstarttime, pendtime, idsinterval); 
#endif
#endif

        jstr_name = CharTojstring(env, pname);
        jstr_protocol = CharTojstring(env, pprotocol);
        jstr_vin = CharTojstring(env, pvin);
        jstr_starttime = CharTojstring(env, pstarttime);
        jstr_endtime = CharTojstring(env, pendtime);
        jidsinterval = idsinterval;


        (*env)->SetObjectField(env, name, fid_strValue, jstr_name); 
        (*env)->SetObjectField(env, protocol, fid_strValue, jstr_protocol); 
        (*env)->SetObjectField(env, vin, fid_strValue, jstr_vin); 
        (*env)->SetObjectField(env, starttime, fid_strValue, jstr_starttime); 
        (*env)->SetObjectField(env, endtime, fid_strValue, jstr_endtime); 
        (*env)->SetIntField(env, dsinterval, fid_mValue, jidsinterval); 

        return iRet;

    }

#ifdef __cplusplus
}   
#endif 

