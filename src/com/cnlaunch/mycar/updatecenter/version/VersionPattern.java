package com.cnlaunch.mycar.updatecenter.version;


import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionPattern {
	public final static int PATTERN_NOT_SUPPORT = -1;
	private String str;
	private ArrayList<PatternStlye> patternList = new ArrayList<PatternStlye>();
		
	class PatternStlye
	{
		int pid;
		String regExp;
		String[] examples;

		public int getPid() {
			return pid;
		}

		public void setPid(int pid) {
			this.pid = pid;
		}

		
		public PatternStlye(int pid,String regExp, String[] example) {
			this.pid = pid;
			this.regExp = regExp;
			this.examples = example;
		}

		public String getRegExp() {
			return regExp;
		}

		public void setRegExp(String regExp) {
			this.regExp = regExp;
		}

		public String[] getExamples() {
			return examples;
		}

		public void setExample(String[] example) {
			this.examples = example;
		}
		
	}
	
	public VersionPattern(String s)
	{
		this.str = s;
		initPatternList();
	}
	
	private void initPatternList()
	{
		patternList.add(new PatternStlye(1,"(V|v)[0-9]{1,2}\\.[0-9]\\.[0-9]",new String[]{"V1.0.0","V11.0.0"}));
		patternList.add(new PatternStlye(2,"(V|v)[0-9]{1,2}\\.[0-9]{2}\\.[0-9]{3}",new String[]{"V1.00.000","V11.00.000"}));
//		patternList.add(new PatternStlye(3,"(V|v)[0-9]{1,2}.[0-9]{1,2}.[0-9]{3}",new String[]{"V11.00.000"}));
		patternList.add(new PatternStlye(4,"(V|v)[0-9]{1,2}\\.[0-9]{2}",new String[]{"V1.00","V11.23"}));
	}
	
	public boolean isSameWith(VersionPattern other)
	{
		VersionPattern vpt = new VersionPattern(str);
		int ido = other.checkPattern(other.str);
		int idt = vpt.checkPattern(str);
		
		System.out.println("pid other: "+  ido);
		System.out.println("pid this: "+  idt);
		
		if ((ido!=PATTERN_NOT_SUPPORT && idt!=PATTERN_NOT_SUPPORT) && ido == idt){
			return true;
		}
		return false;
	}
	
	private int checkPattern(String str)
	{
		for (int i=0;i< patternList.size();i++)
		{
			PatternStlye ps = patternList.get(i);
			Matcher m = Pattern.compile(ps.getRegExp()).matcher(str);
			if (m.matches()){
				int len = ps.getExamples().length;
				System.out.println("-> " + str +" found match pattern like:");
				for(int j=0;j < len;j++){
					System.out.println(" "+ps.getExamples()[j]);
				}
				System.out.println("pid : " + ps.getPid());
				return ps.getPid();
			}
		}
		return PATTERN_NOT_SUPPORT;
	}
}
