package com.cnlaunch.mycar.manager.bll;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.database.UserDbHelper;
import com.cnlaunch.mycar.common.utils.Env;
import com.cnlaunch.mycar.manager.database.Category;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import android.content.Context;

/**
 * @author xuzhuowei 账目类别BLL层
 */
public class CategoryBll {
	private Dao<Category, Integer> dao;

	public CategoryBll(Context context, UserDbHelper dbHelper) {
		if (dbHelper == null) {
			throw (new IllegalArgumentException("DbHelper Should not be null!"));
		}
		this.dao = dbHelper.getDao(Category.class);
	}

	public void save(Category category) {
		try {
			if (find(category.getCategory()) == null) {
				dao.create(category);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void update(Category category) {
		try {
			dao.update(category);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public Category find(int daoId) {
		try {
			return dao.queryForId(daoId);
		} catch (SQLException e) {
			return null;
		}
	}

	public Category find(String strCategory) {
		try {

			List<Category> list = dao.queryForEq("category", strCategory);
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
	//获得支出类别与自定义支出类别	
	public Map<String,String> getAllLocalCategory(Context context) {
		
		Map<String,String> categroyMap = new  HashMap<String,String>();
		// 不读数据库，直接从资源文件读取,实现多语言自动切换
		String[] defaultCategorys = context.getResources().getStringArray(
				R.array.manager_oil_manager_default_category);
		int len = defaultCategorys.length;
		for (int i = 0; i < len; i++) {
			String[] arr = defaultCategorys[i].split("\\|");
				Category category = new Category();
				category.setCategory(arr[0]);
				category.setOrderId(Integer.parseInt(arr[1]));
				category.setType(arr[2]);
				category.setCategoryId(arr[3]);
				categroyMap.put(category.getCategory(), category.getCategoryId());
//			}
		}
		return categroyMap;

	}
	public List<Category> getSystemCategory(Context context) {
		// QueryBuilder<Category, Integer> qb = dao.queryBuilder();
		// qb.orderBy("orderId", false);
		// try {
		// qb.where().eq("type", "1");
		// return dao.query(qb.prepare());
		// } catch (SQLException e) {
		// return null;
		// }

		List<Category> categorys = new ArrayList<Category>();
		// 不读数据库，直接从资源文件读取,实现多语言自动切换
		String[] defaultCategorys = context.getResources().getStringArray(
				R.array.manager_oil_manager_default_category);
		int len = defaultCategorys.length;
		for (int i = 0; i < len; i++) {
			String[] arr = defaultCategorys[i].split("\\|");
			int type = Integer.parseInt(arr[2]);
			if (type == Category.TYPE_SYS) {
				Category category = new Category();
				category.setCategory(arr[0]);

				category.setOrderId(Integer.parseInt(arr[1]));
				category.setType(arr[2]);
				categorys.add(category);
			}
		}
		return categorys;

	}
	
	public boolean isSystemCategory(Context context,String categoryStr){
		// 不读数据库，直接从资源文件读取,实现多语言自动切换
		String[] defaultCategorys = context.getResources().getStringArray(
				R.array.manager_oil_manager_default_category);
		int len = defaultCategorys.length;
		for (int i = 0; i < len; i++) {
			String[] arr = defaultCategorys[i].split("\\|");
			int type = Integer.parseInt(arr[2]);
			if (type == Category.TYPE_SYS) {
				if(arr[0]!=null && arr[0].equals(categoryStr)){
					return true;
				}
			}
		}
		return false;
	}
	////获得支出类别与自定义支出类别名称	
	public Map<String,String> getAllLocalCategoryNameByID(Context context) {
		
		Map<String,String> categroyMap = new  HashMap<String,String>();
		// 不读数据库，直接从资源文件读取,实现多语言自动切换
		String[] defaultCategorys = context.getResources().getStringArray(
				R.array.manager_oil_manager_default_category);
		int len = defaultCategorys.length;
		for (int i = 0; i < len; i++) {
			String[] arr = defaultCategorys[i].split("\\|");
				Category category = new Category();
				category.setCategory(arr[0]);
				category.setOrderId(Integer.parseInt(arr[1]));
				category.setType(arr[2]);
				category.setCategoryId(arr[3]);
				categroyMap.put(category.getCategoryId(),category.getCategory());
//			}
		}
		return categroyMap;

	}
	public List<Category> getCustomCategory(Context context) {
		List<Category> categorys = new ArrayList<Category>();
		// 不读数据库，直接从资源文件读取，实现多语言自动切换
		String[] defaultCategorys = context.getResources().getStringArray(
				R.array.manager_oil_manager_default_category);
		int len = defaultCategorys.length;
		for (int i = 0; i < len; i++) {
			String[] arr = defaultCategorys[i].split("\\|");
			int type = Integer.parseInt(arr[2]);
			if (type == Category.TYPE_SYS_CUSTOM) {
				Category category = new Category();
				category.setCategory(arr[0]);

				category.setOrderId(Integer.parseInt(arr[1]));
				category.setType(arr[2]);
				categorys.add(category);
			}
		}
		ArrayList<Category> sserCustomCategory = (ArrayList<Category>) getUserCustomCategory();
		if (sserCustomCategory != null) {
			categorys.addAll(sserCustomCategory);
		}
		return categorys;

	}

	public List<Category> getUserCustomCategory() {
		QueryBuilder<Category, Integer> qb = dao.queryBuilder();
		qb.orderBy("orderId", false);
		try {
			qb.where().eq("type", String.valueOf(Category.TYPE_USER_CUSTOM)).and().eq("currentLanguage", Env.GetCurrentLanguage().trim());;
			return dao.query(qb.prepare());
		} catch (SQLException e) {
			return null;
		}
	}

}
