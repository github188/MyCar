package com.cnlaunch.mycar.common.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.cnlaunch.mycar.MyCarActivity;
import com.cnlaunch.mycar.R;

/**
 * <功能简述> 自定义的下拉菜单，用于动态显示已登录的用户 
 * <功能详细描述> 本质上是一个Button，点击这个button是使用popwindow弹出一个
 * 下拉列表，里面放一个listview.关键点是弹出这个下拉列表的位置、动画以及下拉 列表的显示风格等等问题。
 * @author xiangyuanmao
 * @version 1.0 2012-5-7
 * @since DBS V100
 */
public class SpinnerView extends Button implements OnClickListener
{

    /**
     * 上下文，引用该控件的界面（Activity）
     */
    private Context mContext = null;
    /**
     * 下拉列表里面显示的内容，即已经成功登陆过系统的账号列表
     */
    private String[] accountArray = null;

    /**
     * 事件监听接口，用户监听弹出窗口的动作
     */
    private SpinnserPopuViewListener popuView = null;

    /**
     * 布局
     */
    private LinearLayout layout = null;

    /**
     * 动画效果
     */
    private ViewFlipper mViewFlipper = null;

    /**
     * 数据
     */
    private int dataId; // 被选中的元素的ID
    private int firstID; // 下拉列表中处于第一个位置的元素的ID
    private int centerID; // 下拉列表中位于第一个和最后一个元素之间的元素ID
    private int endID; // 下拉列表中最后一个元素的的ID

    /**
     * 标记点击按钮是否消失
     */
    private boolean flag = false;

    /**
     * 构造器 
     * @param context 当前上下文
     * @param attrs 控件属性集合
     */
    public SpinnerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mContext = context; // 指定当前控件上下文
        getAttrValues(context, attrs); // 从layout配置文件上读取相关属性
        this.setOnClickListener(this); // 给控件添加单击事件监听
        ShowPopupWindowMethod();       // 弹出控件的方式
    }

    /**
     * 构造器
     * @param context 当前上下文
     */
    public SpinnerView(Context context)
    {
        super(context);
        mContext = context;// 指定当前控件上下文
        this.setOnClickListener(this);// 给控件添加单击事件监听
        ShowPopupWindowMethod();// 弹出控件的方式
    }

    /**
     * 构造器
     * @param context 当前上下文
     * @param attrs 控件属性集合
     * @param defStyle 显示风格
     */
    public SpinnerView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        getAttrValues(context, attrs); // 从layout配置文件上读取相关属性
        this.setOnClickListener(this);// 给控件添加单击事件监听
        ShowPopupWindowMethod();// 弹出控件的方式
    }

    /**
     * 取到配置文件中的数据
     * @param context 上下文
     * @param attrs 控件属性
     */
    private void getAttrValues(Context context, AttributeSet attrs)
    {

        // 获取属性数据对象
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SpinnerView);
        dataId = a.getResourceId(R.styleable.SpinnerView_textInfo, 0);
        
        // 读取已经成功登陆的账号数组作为下拉列表内容的数据源
        accountArray = MyCarActivity.accountsArray;
        if (accountArray == null)
        {
            accountArray = new String[] {};
        }
        
        // 初始化下拉列表中处于不同位置的背景图片
        firstID = a.getResourceId(R.styleable.SpinnerView_lesson_spinner_first, 0);
        centerID = a.getResourceId(R.styleable.SpinnerView_lesson_spinner_center, 0);
        endID = a.getResourceId(R.styleable.SpinnerView_lesson_spinner_end, 0);
        a.recycle();
        mContext = context;
    }

    /**
     * 处理点击客户端的事件，刷新UI
     */
    private Handler handler = new Handler()
    {

        /**
         * 重写超类的方法
         * @param msg 消息对象
         * @see android.os.Handler#handleMessage(android.os.Message)
         * @since DBS V100
         */
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case 0:
                    int position = (Integer) msg.obj;
                    BackgroundResource(R.drawable.usercenter_button2_over);
                    refreshView();
                    popuView.onItemClick(null, position);
                    break;
                default:
                    break;
            }
            msg.recycle();
        };
    };


    /**
     * 给按钮赋值
     * @param textValue 显示文本信息
     * @since DBS V100
     */
    public void setTextValue(String textValue)
    {
        this.setText(textValue);
    }

    /**
     * 设置背景
     * @param ResourceId 资源ID
     * @since DBS V100
     */
    public void BackgroundResource(int ResourceId)
    {
        this.setBackgroundResource(ResourceId);
    }


    /**
     * 刷新数据
     * 
     * @since DBS V100
     */
    public void refreshView()
    {
        this.invalidate();
    }

    /**
     * 得到下拉列表的内容数组
     * @return 下拉列表的内容数组
     * @since DBS V100
     */
    public String[] getArray()
    {
        return accountArray;
    }

    /**
     * 对下拉列表内容赋值
     * @param array 下拉列表内容
     * @since DBS V100
     */
    public void setArray(String[] array)
    {
        this.accountArray = array;
    }

    /**
     * 得到窗口弹出监听引用
     * @return 窗口弹出监听引用
     * @since DBS V100
     */
    public SpinnserPopuViewListener getPopuView()
    {
        return popuView;
    }

    /**
     * 设置窗口弹出监听引用
     * @param popuView 窗口弹出监听引用
     * @since DBS V100
     */
    public void setPopuView(SpinnserPopuViewListener popuView)
    {
        this.popuView = popuView;
    }

    /**
     * 实现控件单击事件
     * @param v 控件的引用
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     * @since DBS V100
     */
    @Override
    public void onClick(View v)
    {

        if (!flag)
        {
            this.popuView.show(mViewFlipper, true);
            this.setBackgroundResource(R.drawable.usercenter_button2_down);
            flag = true;
        }
        else
        {
            this.setBackgroundResource(R.drawable.usercenter_button2_over);
            this.popuView.show(mViewFlipper, false);
            flag = false;
        }
    }

    /**
     * 显示对话框的方法
     * 
     * @since DBS V100
     */
    public void ShowPopupWindowMethod()
    {
        // 创建动画
        mViewFlipper = new ViewFlipper(mContext);
        mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.menu_in));
        mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.menu_out));

        // 加载布局
        layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

        // 加载数据
        ListView listView = new ListView(mContext);
        listView.setVerticalScrollBarEnabled(true);
        listView.setBackgroundColor(R.color.white);
        listView.setCacheColorHint(R.drawable.transparent);
        listView.setFadingEdgeLength(0);
        listView.setDivider(mContext.getResources().getDrawable(R.drawable.main_divider));
        listView.setDividerHeight(0);
        listView.setLayoutParams(new LayoutParams(260, 310));

        PopupWindowAdapter adapter = new PopupWindowAdapter(mContext, accountArray, 20, Color.BLUE, firstID, centerID, endID);
        listView.setAdapter(adapter);
        listView.setFocusable(true);
        layout.addView(listView);
        mViewFlipper.addView(layout);
    }

    /**
     * 
     * <功能简述> 控件弹出事件的监听接口
     * <功能详细描述> 主要实现下拉列表的弹出动画方式和选中某一选项的事件
     * @author xiangyuanmao
     * @version 1.0 2012-5-8
     * @since DBS V100
     */
    public interface SpinnserPopuViewListener
    {
        /**
         * 实现下拉列表的弹出动画方式
         * @param v 动画方式
         * @param isShow
         * @since DBS V100
         */
        public void show(ViewFlipper v, boolean isShow);

        /**
         * 某一选项被选中的事件处理
         * @param listView 下拉列表的引用
         * @param position 该选项在列表中的位置
         * @since DBS V100
         */
        public void onItemClick(ListView listView, int position);
    }

    /**
     * 
     * <功能简述> 单击事件监听实现
     * <功能详细描述>
     * @author xiangyuanmao
     * @version 1.0 2012-5-8
     * @since DBS V100
     */
    class OnclickListener implements OnClickListener
    {

        // 被点击选项的位置
        private int position;

        /**
         * 
         * 构造器
         * 类初始化时调用的构造器
         * @param position 被点击选项的位置
         * @since DBS V100
         */
        public OnclickListener(int position)
        {
            super();
            this.position = position;
        }

        /**
         * 实现单击事件处理
         * @param v 控件的引用
         * @see android.view.View.OnClickListener#onClick(android.view.View)
         * @since DBS V100
         */
        @Override
        public void onClick(View v)
        {

            BackgroundResource(R.drawable.usercenter_button2_over);
            refreshView();
            popuView.onItemClick(null, position);

            flag = false;
            popuView.show(null, flag);

        }

    }

    /**
     * <功能简述> 下拉类表内容适配器
     * <功能详细描述> 把普通的字符串数组转换为下拉列表可以显示的格式
     * @author xiangyuanmao
     * @version 1.0 2012-5-8
     * @since DBS V100
     */
    public class PopupWindowAdapter extends BaseAdapter
    {
        @SuppressWarnings("unused")
        private Context mContext = null; // 上下文
        private TextView[] title = null; // 元素数组
        @SuppressWarnings("unused")
        private int fontSize = 0;        // 字体大小
        @SuppressWarnings("unused")
        private int color;               // 颜色

        /**
         * 给ListView上的每一项设置背景
         *  firstId 第一项 
         *  centerID 中间部分 
         *  endID 最后一项
         */
        private int firstId;
        private int centerID;
        private int endID;

        /**
         * 
         * 构造器
         * 类初始化时调用的构造器
         * @param mContext 上下文
         * @param titles 标题数组，其实就是现实的内容
         * @param fontSize 字体大小
         * @param color 颜色
         * @param firstId 第一选项元素的背景资源ID
         * @param centerID 中间选项元素的背景资源ID
         * @param endID 最末选项元素的背景资源ID
         * @since DBS V100
         */
        public PopupWindowAdapter(Context mContext, String[] titles, int fontSize, int color, int firstId, int centerID, int endID)
        {
            super();
            this.mContext = mContext;
            this.title = new TextView[titles.length];
            this.fontSize = fontSize;
            this.color = color;
            this.firstId = firstId;
            this.centerID = centerID;
            this.endID = endID;

            for (int i = 0; i < titles.length; i++)
            {
                title[i] = new TextView(mContext);
                title[i].setText(titles[i]);
                title[i].setTextSize(fontSize);
                title[i].setTextColor(color);
                title[i].setGravity(Gravity.CENTER);
                title[i].setPadding(10, 10, 10, 10);
            }
        }

        /**
         * 得到下列表的总长度
         * @return 下列表的总长度
         * @see android.widget.Adapter#getCount()
         * @since DBS V100
         */
        @Override
        public int getCount()
        {

            return title.length;
        }

        /**
         * 根据下标索引获得该选项显示内容
         * @param position 下标索引
         * @return 目标选项的显示内容
         * @see android.widget.Adapter#getItem(int)
         * @since DBS V100
         */
        @Override
        public Object getItem(int position)
        {
            return title[position];
        }

        /**
         * 根据下标索引获得该选项显示ID
         * @param position 下标索引
         * @return 目标选项的ID
         * @see android.widget.Adapter#getItemId(int)
         * @since DBS V100
         */
        @Override
        public long getItemId(int position)
        {
            return title[position].getId();
        }

        /**
         * 得到某一选项的视图对象
         * @param position 选项索引
         * @param convertView 选项视图引用
         * @param parent 选项视图组引用
         * @return
         * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
         * @since DBS V100
         */
        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            View view = null;

//            if (convertView == null)
//            {
                view = title[position];

                /**
                 * 设置ListView的每一项的监听事件
                 */
                view.setOnClickListener(new OnclickListener(position));
                /**
                 * 为ListView上的每一项加背景
                 */
                if (position == 0)
                {
                    view.setBackgroundResource(firstId);
                }
                else if (position == title.length - 1)
                {
                    view.setBackgroundResource(endID);
                }
                else
                {
                    view.setBackgroundResource(centerID);
                }
//            }
//            else
//            {
//                view = convertView;
//            }
            return view;
        }
    }

    /**
     * 下拉列表是否弹出
     * @return 下拉列表是否弹出 
     * @since DBS V100
     */
    public boolean isFlag()
    {
        return flag;
    }

    /**
     * 设置下拉列表是否弹出
     * @param flag boolean型变量，是否弹出下拉列表
     * @since DBS V100
     */
    public void setFlag(boolean flag)
    {
        this.flag = flag;
    }

}