package com.cnlaunch.mycar.updatecenter.onekeydiag;

public class CarBaseInfo
{
	String diagEntranceId;		//������id
	String carBrandName;			// ��ϵID
	String carBrandId;			// ��ϵID
	String values	;			//����ֵ�У����ֵ�� ��,�� �ŷֿ�
	String carBrandVin;			//VIN��
	String carModel	;			//����
	String carProducingYear;	//���
	String carEngineType;		//����������
	String carDisplacement;		//����
	String carGearboxType;		//��������
	int    carProducingAreaId;		//����id
	boolean  isEmpty = false;   // �յ�����
	
	public CarBaseInfo(){}
	
	public String getCarBrandName()
	{
		return carBrandName;
	}

	public void setCarBrandName(String carBrandName)
	{
		this.carBrandName = carBrandName;
	}

	public String getDiagEntranceId()
	{
		return diagEntranceId;
	}
	
	public void setDiagEntranceId(String diagEntranceId)
	{
		this.diagEntranceId = diagEntranceId;
	}
	
	public String getValue()
	{
		return values;
	}
	
	public void setValue(String value)
	{
		this.values = value;
	}
	
	public String getCarBrandVin()
	{
		return carBrandVin;
	}
	
	public void setCarBrandVin(String carBrandVin)
	{
		this.carBrandVin = carBrandVin;
	}
	
	public String getCarModel()
	{
		return carModel;
	}
	
	public void setCarModel(String carModel)
	{
		this.carModel = carModel;
	}
	
	public String getCarBrandId()
	{
		return carBrandId;
	}
	
	public void setCarBrandId(String carBrandId)
	{
		this.carBrandId = carBrandId;
	}
	
	public String getCarProducingYear()
	{
		return carProducingYear;
	}
	
	public void setCarProducingYear(String carProducingYear)
	{
		this.carProducingYear = carProducingYear;
	}
	
	public String getCarEngineType()
	{
		return carEngineType;
	}
	
	public void setCarEngineType(String carEngineType)
	{
		this.carEngineType = carEngineType;
	}
	
	public String getCarDisplacement()
	{
		return carDisplacement;
	}
	
	public void setCarDisplacement(String carDisplacement)
	{
		this.carDisplacement = carDisplacement;
	}
	
	public String getCarGearboxType()
	{
		return carGearboxType;
	}
	
	public void setCarGearboxType(String carGearboxType)
	{
		this.carGearboxType = carGearboxType;
	}
	
	public int getCarProducingAreaId()
	{
		return carProducingAreaId;
	}
	
	public void setCarProducingAreaId(int carProducingAreaId)
	{
		this.carProducingAreaId = carProducingAreaId;
	}
	
	public void setEmpyt(boolean e)
	{
		this.isEmpty = e;
	}
	
	public boolean isEmpty()
	{
		return isEmpty;
	}
	
	/**
	 * ��ȡ���е�������Ϣ
	 * @return �ַ���
	 */
	public String getAllInfo()
	{
		StringBuilder sb = new StringBuilder();
		if (this.carBrandName!=null)
		{
			sb.append(this.carBrandName+"\n");
		}
		if (this.carDisplacement!=null)
		{
			sb.append(this.carDisplacement+"\n");
		}
		if (this.carEngineType!=null)
		{
			sb.append(this.carEngineType+"\n");
		}
		if (this.carGearboxType!=null)
		{
			sb.append(this.carGearboxType+"\n");
		}
		if (this.carProducingYear!=null)
		{
			sb.append(this.carProducingYear+"\n");
		}
		return sb.toString();
	}
}
