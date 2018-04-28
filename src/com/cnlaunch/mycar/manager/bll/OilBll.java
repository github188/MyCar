package com.cnlaunch.mycar.manager.bll;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;

import android.content.Context;

import com.cnlaunch.mycar.common.database.UserDbHelper;
import com.cnlaunch.mycar.common.utils.Env;
import com.cnlaunch.mycar.manager.database.Oil;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;

/**
 * @author xuzhuowei ¼ÓÓÍ¼ÇÂ¼BLL²ã
 */
public class OilBll {
	private Dao<Oil, Integer> dao;

	public static final int LAST_OPERATE_ADD = 0;
	public static final int LAST_OPERATE_EDIT = 1;
	public static final int LAST_OPERATE_DEL = 2;
	public static final int LAST_OPERATE_DOWNLOAD = 3;
	public static final int SYNC_FLAG_NO = 0;
	public static final int SYNC_FLAG_YES = 1;

	public OilBll(Context context, UserDbHelper dbHelper) {
		if (dbHelper == null) {
			throw (new IllegalArgumentException("DbHelper Should not be null!"));
		}
		this.dao = dbHelper.getDao(Oil.class);
	}

	public void save(Oil oil) {
		try {
			if (find(oil.getId()) == null) {
				dao.create(oil);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void saveForDownload(Oil oil) {
		try {
			oil.setLastOperate(LAST_OPERATE_DOWNLOAD);
			oil.setSyncFlag(SYNC_FLAG_YES);
			dao.create(oil);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void update(Oil oil) {
		try {
			oil.setLastOperate(LAST_OPERATE_EDIT);
			oil.setSyncFlag(SYNC_FLAG_NO);
			dao.update(oil);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public Oil find(int daoId) {
		try {
			return dao.queryForId(daoId);
		} catch (SQLException e) {
			return null;
		}
	}

	public Oil find(String id) {
		try {
			List<Oil> list = dao.queryForEq("id", id);
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
			DeleteBuilder<Oil, Integer> db = dao.deleteBuilder();
			db.where().eq("id", id);
			dao.delete(db.prepare());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setDelFlag(int daoId) {
		Oil oil = find(daoId);
		oil.setLastOperate(LAST_OPERATE_DEL);
		try {
			dao.update(oil);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setSyncFlag(Boolean isSync, int daoId) {
		Oil oil = find(daoId);
		if (isSync) {
			oil.setSyncFlag(SYNC_FLAG_YES);
		} else {
			oil.setSyncFlag(SYNC_FLAG_NO);
		}
		try {
			dao.update(oil);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setSyncFlag(boolean isSync, String id) {
		Oil oil = find(id);
		if (isSync) {
			oil.setSyncFlag(SYNC_FLAG_YES);
		} else {
			oil.setSyncFlag(SYNC_FLAG_NO);
		}
		try {
			dao.update(oil);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<Oil> getList(Date startDate, Date endDate) {

		if (startDate == null || endDate == null) {
			return null;
		}

		try {
			QueryBuilder<Oil, Integer> qb = dao.queryBuilder();
			qb.where().ge("expenseTime", DateUtils.addDays(startDate,-1)).and().le("expenseTime", endDate).and().ne("lastOperate", LAST_OPERATE_DEL).and().eq("currentLanguage", Env.GetCurrentLanguage().trim());
			qb.orderBy("expenseTime", false);
			return dao.query(qb.prepare());
		} catch (SQLException e) {
			return null;
		}
	}

	public List<Oil> getList(Date startDate, Date endDate, String userCarId) {

		if (startDate == null || endDate == null) {
			return null;
		}

		try {
			QueryBuilder<Oil, Integer> qb = dao.queryBuilder();
			qb.where().eq("userCarId", userCarId).and().ge("expenseTime", DateUtils.addDays(startDate,-1))
			.and().le("expenseTime", endDate).and().ne("lastOperate", LAST_OPERATE_DEL).and().eq("currentLanguage", Env.GetCurrentLanguage().trim());
			qb.orderBy("expenseTime", false);
			return dao.query(qb.prepare());
		} catch (SQLException e) {
			return null;
		}
	}

	public List<Oil> getList(String userCarId) {

		if (userCarId == null) {
			return null;
		}

		try {
			QueryBuilder<Oil, Integer> qb = dao.queryBuilder();
			qb.where().eq("userCarId", userCarId).and().ne("lastOperate", LAST_OPERATE_DEL).and().eq("currentLanguage", Env.GetCurrentLanguage().trim());
			return dao.query(qb.prepare());
		} catch (SQLException e) {
			return null;
		}
	}

	public List<Oil> getAllOilForSync() {
		try {
			QueryBuilder<Oil, Integer> qb = dao.queryBuilder();
			qb.where().eq("syncFlag", SYNC_FLAG_NO).and().eq("currentLanguage", Env.GetCurrentLanguage().trim());
			return dao.query(qb.prepare());
		} catch (SQLException e) {
			return null;
		}

	}

	public List<Oil> getDataForExport() {

		try {
			QueryBuilder<Oil, Integer> qb = dao.queryBuilder();
			qb.where().ne("lastOperate", LAST_OPERATE_DEL).and().eq("currentLanguage", Env.GetCurrentLanguage().trim());
			return dao.query(qb.prepare());
		} catch (SQLException e) {
			return null;
		}
	}

}
