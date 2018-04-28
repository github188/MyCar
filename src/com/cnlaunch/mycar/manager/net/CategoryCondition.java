package com.cnlaunch.mycar.manager.net;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import com.cnlaunch.mycar.common.utils.Env;

public class CategoryCondition implements KvmSerializable {
	
   private static final long serialVersionUID = 6838305383172007559L;
    private String currentLanguage;
    
    public CategoryCondition(){
    	this.currentLanguage = Env.GetCurrentLanguage().trim();
    }
    public String getCurrentLanguage()
    {
        return currentLanguage;
    }

    public void setCurrentLanguage(String currentLanguage)
    {
        this.currentLanguage = currentLanguage;
    }
	@Override
	public Object getProperty(int arg0) {
		switch (arg0) {
		case 0:
			return currentLanguage;
		}
		return null;
	}

	@Override
	public int getPropertyCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public void getPropertyInfo(int index,Hashtable arg1, PropertyInfo info) {
		// TODO Auto-generated method stub
		switch (index) {
		case 0:
			info.type = PropertyInfo.STRING_CLASS;
			info.name = "currentLanguage";
			break;
		default:
			break;
		}

	}

	@Override
	public void setProperty(int index, Object arg1) {
		switch (index) {
			case 0:
				currentLanguage = arg1.toString();
				break;
			default:
				break;
		}
	}
	@Override
	public String toString() {
		return currentLanguage;
	}

}
