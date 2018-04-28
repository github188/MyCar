package com.cnlaunch.mycar.manager.bll;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.cnlaunch.mycar.common.database.UserDbHelper;
import com.cnlaunch.mycar.common.utils.Env;
import com.cnlaunch.mycar.manager.database.Account;
import com.cnlaunch.mycar.manager.database.StatisticsByCategory;
import com.cnlaunch.mycar.manager.database.StatisticsByDate;
import com.cnlaunch.mycar.manager.database.StatisticsType;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;

/**
 * @author xuzhuowei
 *ÕËÄ¿Í³¼ÆBLL²ã
 */
public class StatisticsBll {
	private Dao<Account, Integer> dao;

	public StatisticsBll(Context context, UserDbHelper dbHelper) {
		if (dbHelper == null) {
			throw (new IllegalArgumentException("DbHelper Should not be null!"));
		}
		this.dao = dbHelper.getDao(Account.class);
	}

	public List<StatisticsByDate> getStatisticForDay(String year, String month) {
		if (year == null || month == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("select sum(amount) as totalAmount,year,month,day from account where year='");
		sb.append(year);
		sb.append("' and month='");
		sb.append(month);
		sb.append("' and ");
		sb.append(" lastOperate!=" + AccountBll.LAST_OPERATE_DEL);
		sb.append(" and ");
		sb.append(" currentLanguage='" + Env.GetCurrentLanguage().trim()+"'");
		sb.append(" group by year,month,day  order by expenseTime desc");

		GenericRawResults<String[]> rawResults;
		try {
			rawResults = dao.queryRaw(sb.toString());
		} catch (SQLException e) {
			return null;
		}

		if (rawResults != null) {
			List<StatisticsByDate> beans = new ArrayList<StatisticsByDate>();
			for (String[] result : rawResults) {
				StatisticsByDate bean = new StatisticsByDate();
				bean.setType(StatisticsType.forDay);
				bean.setTotalAmount(Double.parseDouble(result[0]));
				bean.setYear(result[1]);
				bean.setMonth(result[2]);
				bean.setDay(result[3]);
				beans.add(bean);
			}
			return beans;
		}
		return null;
	}

	public List<StatisticsByDate> getStatisticForMonth(String year) {
		if (year == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("select sum(amount) as totalAmount,year,month from account where year='");
		sb.append(year);
		sb.append("' and ");
		sb.append(" lastOperate!=" + AccountBll.LAST_OPERATE_DEL);
		sb.append(" and ");
		sb.append(" currentLanguage='" + Env.GetCurrentLanguage().trim()+"'");
		sb.append(" group by year,month  order by expenseTime desc");
		
		GenericRawResults<String[]> rawResults;
		try {
			rawResults = dao.queryRaw(sb.toString());
		} catch (SQLException e) {
			return null;
		}

		if (rawResults != null) {
			List<StatisticsByDate> beans = new ArrayList<StatisticsByDate>();
			for (String[] result : rawResults) {
				StatisticsByDate bean = new StatisticsByDate();
				bean.setType(StatisticsType.forMonth);
				bean.setTotalAmount(Double.parseDouble(result[0]));
				bean.setYear(result[1]);
				bean.setMonth(result[2]);
				beans.add(bean);
			}
			return beans;
		}
		return null;
	}

	public List<StatisticsByDate> getStatisticForYear() {
		StringBuilder sb = new StringBuilder();
		sb.append("select sum(amount) as totalAmount,year from account where");
		sb.append(" lastOperate!=" + AccountBll.LAST_OPERATE_DEL);
		sb.append(" and currentLanguage='" + Env.GetCurrentLanguage().trim()+"'");
		sb.append(" group by year  order by expenseTime desc");

		GenericRawResults<String[]> rawResults;
		try {
			rawResults = dao.queryRaw(sb.toString());
		} catch (SQLException e) {
			return null;
		}

		if (rawResults != null) {
			List<StatisticsByDate> beans = new ArrayList<StatisticsByDate>();
			for (String[] result : rawResults) {
				StatisticsByDate bean = new StatisticsByDate();
				bean.setType(StatisticsType.forYear);
				bean.setTotalAmount(Double.parseDouble(result[0]));
				bean.setYear(result[1]);
				beans.add(bean);
			}
			return beans;
		}
		return null;
	}

	public List<Account> getAccountForCategorySomeYear(String year,
			String category) {
		return getAccountForCategory(year,null,category);
	}

	public List<Account> getAccountForCategorySomeMonth(String year,String month,
			String category) {
		return getAccountForCategory(year,month,category);
	}

	public List<Account> getAccountForCategory(String year, String month,
			String categoryId) {
		if (year == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("select amount,year,month,day,time,id,category,remark,categoryId from account where year='");
		sb.append(year);
		if (month != null) {
			sb.append("' and month='");
			sb.append(month);
		}
		sb.append("' and ");
		sb.append(" lastOperate!=" + AccountBll.LAST_OPERATE_DEL);
		sb.append(" and ");
		sb.append(" categoryId='"+categoryId+"'");
		sb.append(" and ");
		sb.append(" currentLanguage='" + Env.GetCurrentLanguage().trim()+"'");
		sb.append(" order by expenseTime desc");

		GenericRawResults<String[]> rawResults;
		try {
			rawResults = dao.queryRaw(sb.toString());
		} catch (SQLException e) {
			return null;
		}

		if (rawResults != null) {
			List<Account> beans = new ArrayList<Account>();
			for (String[] result : rawResults) {
				Account bean = new Account();
				bean.setAmount(result[0]);
				bean.setYear(result[1]);
				bean.setMonth(result[2]);
				bean.setDay(result[3]);
				bean.setTime(result[4]);
				bean.setId(result[5]);
				bean.setCategory(result[6]);
				bean.setRemark(result[7]);
				bean.setCategoryId(result[8]);
				beans.add(bean);
			}
			return beans;
		}
		return null;
	}

	public List<StatisticsByCategory> getStatisticForCategorySomeYear(
			String year) {
		if (year == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("select sum(amount) as totalAmount,count(*) as frequency,year,month,category,categoryId from account where year='");
		sb.append(year);
		sb.append("' and ");
		sb.append(" lastOperate!=" + AccountBll.LAST_OPERATE_DEL);
		sb.append(" and ");
		sb.append(" currentLanguage='" + Env.GetCurrentLanguage().trim()+"'");
		sb.append(" group by year,categoryId  order by expenseTime desc");

		GenericRawResults<String[]> rawResults;
		try {
			rawResults = dao.queryRaw(sb.toString());
		} catch (SQLException e) {
			return null;
		}

		if (rawResults != null) {
			List<StatisticsByCategory> beans = new ArrayList<StatisticsByCategory>();
			for (String[] result : rawResults) {
				StatisticsByCategory bean = new StatisticsByCategory();
				bean.setType(StatisticsType.forYear);
				bean.setTotalAmount(Double.parseDouble(result[0]));
				bean.setFrequency(Integer.parseInt(result[1]));
				bean.setYear(result[2]);
				bean.setMonth(result[3]);
				bean.setCategory(result[4]);
				bean.setCategoryId(result[5]);
				beans.add(bean);
			}
			return beans;
		}
		return null;
	}

	public List<StatisticsByCategory> getStatisticForCategorySomeMonth(
			String year, String month) {
		if (year == null || month == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		sb.append("select sum(amount) as totalAmount,count(*) as frequency,year,month,category,categoryId from account where year='");
		sb.append(year);
		sb.append("' and month='");
		sb.append(month);
		sb.append("' and ");
		sb.append(" lastOperate!=" + AccountBll.LAST_OPERATE_DEL);
		sb.append(" and ");
		sb.append(" currentLanguage='" + Env.GetCurrentLanguage().trim()+"'");
		sb.append(" group by year,month,categoryId  order by expenseTime desc");

		GenericRawResults<String[]> rawResults;
		try {
			rawResults = dao.queryRaw(sb.toString());
		} catch (SQLException e) {
			return null;
		}

		if (rawResults != null) {
			List<StatisticsByCategory> beans = new ArrayList<StatisticsByCategory>();
			for (String[] result : rawResults) {
				StatisticsByCategory bean = new StatisticsByCategory();
				bean.setType(StatisticsType.forMonth);
				bean.setTotalAmount(Double.parseDouble(result[0]));
				bean.setFrequency(Integer.parseInt(result[1]));
				bean.setYear(result[2]);
				bean.setMonth(result[3]);
				bean.setCategory(result[4]);
				bean.setCategoryId(result[5]);
				beans.add(bean);
			}
			return beans;
		}
		return null;

	}

	public long getCount() {
		StringBuilder sb = new StringBuilder();
		sb.append("select count(*) from account where ");
		sb.append(" lastOperate!=" + AccountBll.LAST_OPERATE_DEL);
		sb.append(" and ");
		sb.append(" currentLanguage='" + Env.GetCurrentLanguage().trim()+"'");
		GenericRawResults<String[]> rawResults;
		try {
			rawResults = dao.queryRaw(sb.toString());
		} catch (SQLException e) {
			return 0;
		}

		if (rawResults != null) {
			String[] result;
			try {
				result = rawResults.getResults().get(0);
				return Long.parseLong(result[0]);
			} catch (SQLException e) {
				return 0;
			}
		}
		return 0;
	}
}
