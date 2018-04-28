package com.cnlaunch.mycar.updatecenter.version;
/**
 * �汾�� ���ڰ汾��С�Ƚ��Լ��汾���͵��ж�
 * @author luxingsong
 */
public class VersionNumber {
    private String vstr; // �汾�ַ���
    private VersionPattern vp; // �汾��ʽ
    
    public VersionNumber(String s)
    {
        this.vstr = s;
        vp = new VersionPattern(vstr);
    }
    
    public boolean assureSamePatternWith(VersionNumber other)
    {
        return this.vp.isSameWith(other.vp);
    }
    
    // ����
    public boolean isGreaterThan(VersionNumber other)
    {
        if(!assureSamePatternWith(other))
            return false;
        
        int t = toInt(vstr);
        int o = other.toInt(other.vstr);
        if(t > o)
            return true;
        return false;
    }
    
    // С��
    public boolean isLessThan(VersionNumber other)
    {
        if(!assureSamePatternWith(other))
            return false;
        
        int t = toInt(vstr);
        int o = other.toInt(other.vstr);
        if(t < o)
            return true;
        return false;
    }
    
    // ����
    public boolean isEqualTo(VersionNumber other)
    {
        if(!assureSamePatternWith(other))
            return false;
        
        int t = toInt(vstr);
        int o = other.toInt(other.vstr);
        if(t == o)
            return true;
        return false;
    }
    
    // С�ڵ���
    public boolean isLessEqualTo(VersionNumber other)
    {
        if(!assureSamePatternWith(other))
            return false;
        
        int t = toInt(vstr);
        int o = other.toInt(other.vstr);
        if(t <= o)
            return true;
        return false;
    }
    
    public int getInt()
    {
        return toInt(vstr);
    }
    public static int toInt(String str)
    {
        if (str == null)
        {
            return -1;
        }
        str = str.replace("V", "");
        str = str.replace("v", "");
        str = str.replace(".", "");
        return new Integer(str);
    }
//  // �汾�ַ�ת��Ϊ����
//  private int toInt(String s)
//  {
//      String[] sa = s.substring(1, s.length()).split("\\.");
//      if (sa!=null)
//      {
//          int ret = 0;
//          for(int i = 0;i < sa.length;i++)
//          {
//              System.out.println("[ " + sa[i] + " ]");
//              ret += Integer.valueOf(sa[i])* powerOfTen(sa.length - i);
//          }
//          return ret;
//      }
//      return -1;
//  }
    
    // 10��n�η�
    private int powerOfTen(int index)
    {
        int[] vals = new int[]{
            1,
            10,
            100,
            1000,
            10000,
            100000,
            1000000,
            10000000,
        };
        return vals[index];
    }
}
