package com.cnlaunch.mycar.manager.bll;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;

import android.content.Context;

import com.cnlaunch.mycar.common.database.UserDbHelper;
import com.cnlaunch.mycar.common.utils.Env;
import com.cnlaunch.mycar.manager.database.Account;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

/**
 * @author xuzhuowei ÕËÄ¿Êý¾ÝBLL²ã
 */
public class AccountBll {
	private Dao<Account, Integer> dao;

	public static final int LAST_OPERATE_ADD = 0;
	public static final int LAST_OPERATE_EDIT = 1;
	public static final int LAST_OPERATE_DEL = 2;
	public static final int LAST_OPERATE_DOWNLOAD = 3;
	public static final int SYNC_FLAG_NO = 0;
	public static final int SYNC_FLAG_YES = 1;

	public AccountBll(Context context, UserDbHelper dbHelper) {
		if (dbHelper == null) {
			throw (new IllegalArgumentException("DbHelper Should not be null!"));
		}
		this.dao = dbHelper.getDao(Account.class);
	}

	public void save(Account account) {
		try {
			if (find(account.getId()) == null) {
				dao.create(account);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Integer getMonthTotal() {
		StringBuilder sb = new StringBuilder();
		sb.append("select sum(amount) as totalAmount from account where year='");
		sb.append(com.cnlaunch.mycar.common.utils.Format.DateStr.getYear());
		sb.append("' and month='");
		sb.append(com.cnlaunch.mycar.common.utils.Format.DateStr.getMonth());
		sb.append("' and ");
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
				if (result != null) {
					String total = result[0];
					if (total == null) {
						return 0;
					}
					try {
						return Math.round(Float.parseFloat(result[0]));
					} catch (NumberFormatException e) {
						return 0;
					}
				} else {
					return 0;
				}
			} catch (SQLException e) {
				return 0;
			}
		}
		return 0;
	}

	public void saveForDownload(Account account) {
		try {
			account.setLastOperate(LAST_OPERATE_DOWNLOAD);
			account.setSyncFlag(SYNC_FLAG_YES);
			dao.create(account);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void update(Account account) {
		try {
			account.setLastOperate(LAST_OPERATE_EDIT);
			account.setSyncFlag(SYNC_FLAG_NO);
			dao.update(account);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public Account find(int daoId) {
		try {
			return dao.queryForId(daoId);
		} catch (SQLException e) {
			return null;
		}
	}

	public Account find(String id) {
		try {
			List<Account> list = dao.queryForEq("id", id);
			if (list != null && list.size() != 0) {
				return list.get(0);
			} else {
				return null;
			}
		} catch (SQLException e) {
			return null;
		}
	}

	public void delete(int daoId) {
		try {
			dao.deleteById(daoId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void delete(String id) {
		try {
			DeleteBuilder<Account, Integer> db = dao.deleteBuilder();
			db.where().eq("id", id);
			dao.delete(db.prepare());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setDelFlag(int daoId) {
		Account account = find(daoId);
		account.setLastOperate(LAST_OPERATE_DEL);
		try {
			dao.update(account);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setSyncFlag(Boolean isSync, int daoId) {
		Account account = find(daoId);
		if (isSync) {
			account.setSyncFlag(SYNC_FLAG_YES);
		} else {
			account.setSyncFlag(SYNC_FLAG_NO);
		}
		try {
			dao.update(account);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setSyncFlag(Boolean isSync, String id) {
		Account account = find(id);
		if (isSync) {
			account.setSyncFlag(SYNC_FLAG_YES);
		} else {
			account.setSyncFlag(SYNC_FLAG_NO);
		}
		try {
			dao.update(account);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<Account> getDataForDay(String year, String month, String day) {
		Date date = com.cnlaunch.mycar.common.utils.Format.DateStr.strToDate(
				year, month, day);
		if (date == null) {
			return null;
		}

		try {
			QueryBuilder<Account, Integer> qb = dao.queryBuilder();
			qb.where().ge("expenseTime", date).and().le("expenseTime", DateUtils.addDays(date, 1)).and().ne("lastOperate", LAST_OPERATE_DEL).and().eq("currentLanguage", Env.GetCurrentLanguage().trim());;
			qb.orderBy("expenseTime", false);
			return dao.query(qb.prepare());
		} catch (SQLException e) {
			return null;
		}
	}

	public List<Account> getAllAccountForSync() {
		try {
			QueryBuilder<Account, Integer> qb = dao.queryBuilder();
			qb.where().eq("syncFlag", SYNC_FLAG_NO).and().eq("currentLanguage", Env.GetCurrentLanguage().trim());;
			return dao.query(qb.prepare());
		} catch (SQLException e) {
			return null;
		}

	}

	public List<Account> getDataForExport() {

		try {
			QueryBuilder<Account, Integer> qb = dao.queryBuilder();
			qb.where().ne("lastOperate", LAST_OPERATE_DEL).and().eq("currentLanguage", Env.GetCurrentLanguage().trim());;
			return dao.query(qb.prepare());
		} catch (SQLException e) {
			return null;
		}
	}

}
