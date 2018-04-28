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
 * <���ܼ���> �Զ���������˵������ڶ�̬��ʾ�ѵ�¼���û� 
 * <������ϸ����> ��������һ��Button��������button��ʹ��popwindow����һ��
 * �����б������һ��listview.�ؼ����ǵ�����������б��λ�á������Լ����� �б����ʾ���ȵ����⡣
 * @author xiangyuanmao
 * @version 1.0 2012-5-7
 * @since DBS V100
 */
public class SpinnerView extends Button implements OnClickListener
{

    /**
     * �����ģ����øÿؼ��Ľ��棨Activity��
     */
    private Context mContext = null;
    /**
     * �����б�������ʾ�����ݣ����Ѿ��ɹ���½��ϵͳ���˺��б�
     */
    private String[] accountArray = null;

    /**
     * �¼������ӿڣ��û������������ڵĶ���
     */
    private SpinnserPopuViewListener popuView = null;

    /**
     * ����
     */
    private LinearLayout layout = null;

    /**
     * ����Ч��
     */
    private ViewFlipper mViewFlipper = null;

    /**
     * ����
     */
    private int dataId; // ��ѡ�е�Ԫ�ص�ID
    private int firstID; // �����б��д��ڵ�һ��λ�õ�Ԫ�ص�ID
    private int centerID; // �����б���λ�ڵ�һ�������һ��Ԫ��֮���Ԫ��ID
    private int endID; // �����б������һ��Ԫ�صĵ�ID

    /**
     * ��ǵ����ť�Ƿ���ʧ
     */
    private boolean flag = false;

    /**
     * ������ 
     * @param context ��ǰ������
     * @param attrs �ؼ����Լ���
     */
    public SpinnerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mContext = context; // ָ����ǰ�ؼ�������
        getAttrValues(context, attrs); // ��layout�����ļ��϶�ȡ�������
        this.setOnClickListener(this); // ���ؼ���ӵ����¼�����
        ShowPopupWindowMethod();       // �����ؼ��ķ�ʽ
    }

    /**
     * ������
     * @param context ��ǰ������
     */
    public SpinnerView(Context context)
    {
        super(context);
        mContext = context;// ָ����ǰ�ؼ�������
        this.setOnClickListener(this);// ���ؼ���ӵ����¼�����
        ShowPopupWindowMethod();// �����ؼ��ķ�ʽ
    }

    /**
     * ������
     * @param context ��ǰ������
     * @param attrs �ؼ����Լ���
     * @param defStyle ��ʾ���
     */
    public SpinnerView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        getAttrValues(context, attrs); // ��layout�����ļ��϶�ȡ�������
        this.setOnClickListener(this);// ���ؼ���ӵ����¼�����
        ShowPopupWindowMethod();// �����ؼ��ķ�ʽ
    }

    /**
     * ȡ�������ļ��е�����
     * @param context ������
     * @param attrs �ؼ�����
     */
    private void getAttrValues(Context context, AttributeSet attrs)
    {

        // ��ȡ�������ݶ���
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SpinnerView);
        dataId = a.getResourceId(R.styleable.SpinnerView_textInfo, 0);
        
        // ��ȡ�Ѿ��ɹ���½���˺�������Ϊ�����б����ݵ�����Դ
        accountArray = MyCarActivity.accountsArray;
        if (accountArray == null)
        {
            accountArray = new String[] {};
        }
        
        // ��ʼ�������б��д��ڲ�ͬλ�õı���ͼƬ
        firstID = a.getResourceId(R.styleable.SpinnerView_lesson_spinner_first, 0);
        centerID = a.getResourceId(R.styleable.SpinnerView_lesson_spinner_center, 0);
        endID = a.getResourceId(R.styleable.SpinnerView_lesson_spinner_end, 0);
        a.recycle();
        mContext = context;
    }

    /**
     * �������ͻ��˵��¼���ˢ��UI
     */
    private Handler handler = new Handler()
    {

        /**
         * ��д����ķ���
         * @param msg ��Ϣ����
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
     * ����ť��ֵ
     * @param textValue ��ʾ�ı���Ϣ
     * @since DBS V100
     */
    public void setTextValue(String textValue)
    {
        this.setText(textValue);
    }

    /**
     * ���ñ���
     * @param ResourceId ��ԴID
     * @since DBS V100
     */
    public void BackgroundResource(int ResourceId)
    {
        this.setBackgroundResource(ResourceId);
    }


    /**
     * ˢ������
     * 
     * @since DBS V100
     */
    public void refreshView()
    {
        this.invalidate();
    }

    /**
     * �õ������б����������
     * @return �����б����������
     * @since DBS V100
     */
    public String[] getArray()
    {
        return accountArray;
    }

    /**
     * �������б����ݸ�ֵ
     * @param array �����б�����
     * @since DBS V100
     */
    public void setArray(String[] array)
    {
        this.accountArray = array;
    }

    /**
     * �õ����ڵ�����������
     * @return ���ڵ�����������
     * @since DBS V100
     */
    public SpinnserPopuViewListener getPopuView()
    {
        return popuView;
    }

    /**
     * ���ô��ڵ�����������
     * @param popuView ���ڵ�����������
     * @since DBS V100
     */
    public void setPopuView(SpinnserPopuViewListener popuView)
    {
        this.popuView = popuView;
    }

    /**
     * ʵ�ֿؼ������¼�
     * @param v �ؼ�������
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
     * ��ʾ�Ի���ķ���
     * 
     * @since DBS V100
     */
    public void ShowPopupWindowMethod()
    {
        // ��������
        mViewFlipper = new ViewFlipper(mContext);
        mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.menu_in));
        mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.menu_out));

        // ���ز���
        layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

        // ��������
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
     * <���ܼ���> �ؼ������¼��ļ����ӿ�
     * <������ϸ����> ��Ҫʵ�������б�ĵ���������ʽ��ѡ��ĳһѡ����¼�
     * @author xiangyuanmao
     * @version 1.0 2012-5-8
     * @since DBS V100
     */
    public interface SpinnserPopuViewListener
    {
        /**
         * ʵ�������б�ĵ���������ʽ
         * @param v ������ʽ
         * @param isShow
         * @since DBS V100
         */
        public void show(ViewFlipper v, boolean isShow);

        /**
         * ĳһѡ�ѡ�е��¼�����
         * @param listView �����б������
         * @param position ��ѡ�����б��е�λ��
         * @since DBS V100
         */
        public void onItemClick(ListView listView, int position);
    }

    /**
     * 
     * <���ܼ���> �����¼�����ʵ��
     * <������ϸ����>
     * @author xiangyuanmao
     * @version 1.0 2012-5-8
     * @since DBS V100
     */
    class OnclickListener implements OnClickListener
    {

        // �����ѡ���λ��
        private int position;

        /**
         * 
         * ������
         * ���ʼ��ʱ���õĹ�����
         * @param position �����ѡ���λ��
         * @since DBS V100
         */
        public OnclickListener(int position)
        {
            super();
            this.position = position;
        }

        /**
         * ʵ�ֵ����¼�����
         * @param v �ؼ�������
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
     * <���ܼ���> �����������������
     * <������ϸ����> ����ͨ���ַ�������ת��Ϊ�����б������ʾ�ĸ�ʽ
     * @author xiangyuanmao
     * @version 1.0 2012-5-8
     * @since DBS V100
     */
    public class PopupWindowAdapter extends BaseAdapter
    {
        @SuppressWarnings("unused")
        private Context mContext = null; // ������
        private TextView[] title = null; // Ԫ������
        @SuppressWarnings("unused")
        private int fontSize = 0;        // �����С
        @SuppressWarnings("unused")
        private int color;               // ��ɫ

        /**
         * ��ListView�ϵ�ÿһ�����ñ���
         *  firstId ��һ�� 
         *  centerID �м䲿�� 
         *  endID ���һ��
         */
        private int firstId;
        private int centerID;
        private int endID;

        /**
         * 
         * ������
         * ���ʼ��ʱ���õĹ�����
         * @param mContext ������
         * @param titles �������飬��ʵ������ʵ������
         * @param fontSize �����С
         * @param color ��ɫ
         * @param firstId ��һѡ��Ԫ�صı�����ԴID
         * @param centerID �м�ѡ��Ԫ�صı�����ԴID
         * @param endID ��ĩѡ��Ԫ�صı�����ԴID
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
         * �õ����б���ܳ���
         * @return ���б���ܳ���
         * @see android.widget.Adapter#getCount()
         * @since DBS V100
         */
        @Override
        public int getCount()
        {

            return title.length;
        }

        /**
         * �����±�������ø�ѡ����ʾ����
         * @param position �±�����
         * @return Ŀ��ѡ�����ʾ����
         * @see android.widget.Adapter#getItem(int)
         * @since DBS V100
         */
        @Override
        public Object getItem(int position)
        {
            return title[position];
        }

        /**
         * �����±�������ø�ѡ����ʾID
         * @param position �±�����
         * @return Ŀ��ѡ���ID
         * @see android.widget.Adapter#getItemId(int)
         * @since DBS V100
         */
        @Override
        public long getItemId(int position)
        {
            return title[position].getId();
        }

        /**
         * �õ�ĳһѡ�����ͼ����
         * @param position ѡ������
         * @param convertView ѡ����ͼ����
         * @param parent ѡ����ͼ������
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
                 * ����ListView��ÿһ��ļ����¼�
                 */
                view.setOnClickListener(new OnclickListener(position));
                /**
                 * ΪListView�ϵ�ÿһ��ӱ���
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
     * �����б��Ƿ񵯳�
     * @return �����б��Ƿ񵯳� 
     * @since DBS V100
     */
    public boolean isFlag()
    {
        return flag;
    }

    /**
     * ���������б��Ƿ񵯳�
     * @param flag boolean�ͱ������Ƿ񵯳������б�
     * @since DBS V100
     */
    public void setFlag(boolean flag)
    {
        this.flag = flag;
    }

}