package com.cnlaunch.mycar.im;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.cnlaunch.mycar.R;
import com.cnlaunch.mycar.common.ui.CustomDialog;
import com.cnlaunch.mycar.im.action.FaceManager;
import com.cnlaunch.mycar.im.common.ImConstant;
import com.cnlaunch.mycar.im.common.ImConstant.FriendKeys;
import com.cnlaunch.mycar.im.common.ImMsgIds;
import com.cnlaunch.mycar.im.common.ImMsgObserver;
import com.cnlaunch.mycar.im.common.ImMsgQueue;
import com.cnlaunch.mycar.im.common.JsonConvert;
import com.cnlaunch.mycar.im.database.FriendsUtils;
import com.cnlaunch.mycar.im.model.ConditionSearchFriendModel;
import com.cnlaunch.mycar.im.model.IMMyFriendComModel;
import com.cnlaunch.mycar.im.model.IMSearchFriendListModel;
import com.cnlaunch.mycar.im.model.ImSession;
import com.cnlaunch.mycar.im.model.PaginationRecord;
import com.cnlaunch.mycar.im.model.UserModel;

public class AddFriendActivity extends ImBaseActivity
{
    private Button button_search_by_nickname;
    private Button button_search_by_ccno;
    private Button button_search_by_online_list;

    private EditText edittext_keyword;
    private TextView textview_no_result;
    private ListView listview_search_result;

    private ProgressDialog mUpdateOnlineListProgressDialog;
    private CustomDialog mAddFriendDialog;

    private List<HashMap<String, Object>> mSearchResultList;
    private SimpleAdapter mListAdapter;

    private Context mContext;

    private ImMsgObserver mOnlineListUpdateObserver;

    List<HashMap<String, Integer>> mFaceList = new ArrayList<HashMap<String, Integer>>();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_add_friend, R.layout.custom_title);
        setCustomeTitleLeft(R.string.im_title_add_friend);
        setCustomeTitleRight("");

        mContext = this;

        findView();
        addListener();
        createMsgObserver();

    }

    @Override
    public void onPause()
    {
        unRegisterMsgObserver();
        super.onPause();
    }

    @Override
    public void onResume()
    {
        registerMsgObserver();
        super.onResume();
    }

    @Override
    public void onDestroy()
    {
        if (mAddFriendDialog != null)
        {
            mAddFriendDialog.dismiss();
        }
        super.onDestroy();
    }

    private void findView()
    {
        button_search_by_nickname = (Button) findViewById(R.id.button_search_by_nickname);
        button_search_by_ccno = (Button) findViewById(R.id.button_search_by_ccno);
        button_search_by_online_list = (Button) findViewById(R.id.button_search_by_online_list);

        edittext_keyword = (EditText) findViewById(R.id.edittext_keyword);
        textview_no_result = (TextView) findViewById(R.id.textview_no_result);
        listview_search_result = (ListView) findViewById(R.id.listview_search_result);

        findViewById(R.id.im_textbutton_menu_add_friend).setBackgroundResource(R.drawable.manager_toolbar_bg_selected);
    }

    private void showUpdateListProgressDialog()
    {
        if (mUpdateOnlineListProgressDialog == null)
        {
            mUpdateOnlineListProgressDialog = new ProgressDialog(mContext);
        }
        mUpdateOnlineListProgressDialog.setMessage(mContext.getResources().getString(R.string.im_getting_online_list));
        mUpdateOnlineListProgressDialog.show();
    }

    private void addListener()
    {
        button_search_by_nickname.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                String keywrod = edittext_keyword.getText().toString();
                if (keywrod.length() > 0)
                {
                    searchFriendByNickName(keywrod);
                }
                else
                {
                    Toast.makeText(mContext, R.string.im_please_enter_keyword, Toast.LENGTH_SHORT).show();
                }
            }
        });

        button_search_by_ccno.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                String keywrod = edittext_keyword.getText().toString();
                if (keywrod.length() > 0)
                {
                    searchFriendByCcno(keywrod);
                }
                else
                {
                    Toast.makeText(mContext, R.string.im_please_enter_keyword, Toast.LENGTH_SHORT).show();
                }

            }
        });

        button_search_by_online_list.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                showUpdateListProgressDialog();
                ImMsgQueue.addMessage(ImMsgIds.ORDER_UPDATE_ONLINE_LIST);
            }
        });

        mSearchResultList = new ArrayList<HashMap<String, Object>>();
        mListAdapter = new SimpleAdapter(this, mSearchResultList, R.layout.im_online_list_item, new String[] { ImConstant.LastChatKeys.NICKNAME, ImConstant.LastChatKeys.CCNO, }, new int[] {
            R.id.im_list_item_nickname, R.id.im_list_item_ccno })
        {

            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                View v = super.getView(position, convertView, parent);
                ImageView imageview_user_face = (ImageView) v.findViewById(R.id.imageview_user_face);
                imageview_user_face.setImageResource(FaceManager.getUserFace((String) mSearchResultList.get(position).get(ImConstant.LastChatKeys.FACEID)));
                return v;
            }

        };
        listview_search_result.setAdapter(mListAdapter);
        listview_search_result.setOnItemClickListener(new OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id)
            {
                final String useruid = (String) mSearchResultList.get(position).get(ImConstant.LastChatKeys.USERUID);
                final String cc = (String) mSearchResultList.get(position).get(ImConstant.LastChatKeys.CCNO);
                
//                if (FriendsUtils.isMyFriend(mContext, useruid))
//                {
//                    Toast.makeText(mContext, R.string.im_is_allready_your_friend, Toast.LENGTH_SHORT).show();
//                    return;
//                }
                if (isFriend(cc))
                {
                    Toast.makeText(mContext, R.string.im_is_allready_your_friend, Toast.LENGTH_SHORT).show();
                    return;
                }
                mAddFriendDialog = new CustomDialog(mContext);
                mAddFriendDialog.setTitle(R.string.im_title_add_friend);
                mAddFriendDialog.setMessage(R.string.im_ensure_to_build_friendship);
                mAddFriendDialog.setNegativeButton(R.string.im_no, new OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {
                        mAddFriendDialog.dismiss();

                    }
                });

                mAddFriendDialog.setPositiveButton(R.string.im_yes, new OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {
                        Message msg = new Message();
                        msg.what = ImMsgIds.ORDER_ADD_FRIEND;
                        Bundle data = new Bundle();
                        data.putString(FriendKeys.USERUID, useruid);
                        msg.setData(data);
                        ImMsgQueue.getInstance().addMessage(msg);
                        mAddFriendDialog.dismiss();
                    }
                });
                mAddFriendDialog.show();
            }
        });
    }

    private boolean isFriend(String uuid)
    {
        boolean flag = false;
        List<IMMyFriendComModel> friendList = ImSession.getInstence().getFriendList();
        if (friendList != null && friendList.size() > 0)
        {
            for (IMMyFriendComModel imMyFriendComModel : friendList)
            {
                if (imMyFriendComModel.getCCNo().equals(uuid))
                {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    private void searchFriendByNickName(String keywrod)
    {
        ConditionSearchFriendModel model = new ConditionSearchFriendModel();
        model.setNickName(keywrod);
        getResultFromWebService(model);

    }

    private void searchFriendByCcno(String keywrod)
    {
        ConditionSearchFriendModel model = new ConditionSearchFriendModel();
        model.setCcNo(keywrod);
        getResultFromWebService(model);
    }

    private void getResultFromWebService(final ConditionSearchFriendModel model)
    {
        new Thread()
        {
            @Override
            public void run()
            {
                if (model == null)
                {
                    throw new IllegalArgumentException(mContext.getString(R.string.im_para_not_be_null));
                }

                HttpPost httpRequest = new HttpPost(ImConstant.WEB_SERVER_SEARCH_FRIEND);
                try
                {
                    httpRequest.setEntity(new UrlEncodedFormEntity(model.getNameValuePairList(), HTTP.UTF_8));
                    HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
                    if (httpResponse.getStatusLine().getStatusCode() == 200)
                    {
                        String result = EntityUtils.toString(httpResponse.getEntity());

                        PaginationRecord paginationRecord = JsonConvert.fromJson(result, PaginationRecord.class);
                        final ArrayList<IMSearchFriendListModel> modelList = paginationRecord != null ? paginationRecord.getModelList() : null;

                        new Handler(getMainLooper()).post(new Runnable()
                        {

                            @Override
                            public void run()
                            {
                                freshSearchResultList(modelList);
                            }
                        });

                    }
                    else
                    {
                        Log.e("AddFriend", mContext.getResources().getString(R.string.im_net_error_can_not_get_friend_list) + "code:" + httpResponse.getStatusLine().getStatusCode());
                    }
                }
                catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
                catch (ClientProtocolException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    private void createMsgObserver()
    {
        mOnlineListUpdateObserver = new ImMsgObserver(ImMsgIds.REPLY_ONLINE_LIST_UPDATED, this)
        {

            @Override
            public void dealMessage(Message msg)
            {
                freshOnlineList();
            }
        };
    }

    public void freshSearchResultList(ArrayList<IMSearchFriendListModel> modelList)
    {
        mSearchResultList.clear();
        if (modelList != null && modelList.size() > 0)
        {
            for (IMSearchFriendListModel model : modelList)
            {
                // 将自己排除
                if (model.getUserUID().equals(ImSession.getInstence().getUseruid()))
                {
                    continue;
                }

                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put(ImConstant.LastChatKeys.NICKNAME, model.getNickName());
                map.put(ImConstant.LastChatKeys.CCNO, model.getCCNo());
                map.put(ImConstant.LastChatKeys.FACEID, String.valueOf(model.getFaceID()));
                map.put(ImConstant.LastChatKeys.USERUID, model.getUserUID());
                mSearchResultList.add(map);
            }
            mListAdapter.notifyDataSetChanged();
            textview_no_result.setVisibility(View.GONE);
            listview_search_result.setVisibility(View.VISIBLE);
        }
        else
        {
            textview_no_result.setVisibility(View.VISIBLE);
            listview_search_result.setVisibility(View.GONE);
        }
        hideUpdateListProgressDialog();

    }

    private void freshOnlineList()
    {
        List<UserModel> onlineUsers = ImSession.getInstence().getOnlineUsers();
        mSearchResultList.clear();
        if (onlineUsers != null && onlineUsers.size() > 0)
        {
            for (UserModel user : onlineUsers)
            {
                // 将自己排除
                if (user.getUseruid().equals(ImSession.getInstence().getUseruid()))
                {
                    continue;
                }

                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put(ImConstant.LastChatKeys.NICKNAME, user.getNickname());
                map.put(ImConstant.LastChatKeys.CCNO, user.getCcno());
                map.put(ImConstant.LastChatKeys.FACEID, String.valueOf(user.getFaceId()));
                map.put(ImConstant.LastChatKeys.USERUID, user.getUseruid());
                mSearchResultList.add(map);
            }
            mListAdapter.notifyDataSetChanged();
            textview_no_result.setVisibility(View.GONE);
            listview_search_result.setVisibility(View.VISIBLE);
        }
        else
        {
            textview_no_result.setVisibility(View.VISIBLE);
            listview_search_result.setVisibility(View.GONE);
        }
        hideUpdateListProgressDialog();

    }

    private void hideUpdateListProgressDialog()
    {
        if (mUpdateOnlineListProgressDialog != null)
        {
            mUpdateOnlineListProgressDialog.dismiss();
        }
    }

    private void registerMsgObserver()
    {
        unRegisterMsgObserver();
        ImMsgQueue.getInstance().registerObserver(mOnlineListUpdateObserver);
    }

    private void unRegisterMsgObserver()
    {
        ImMsgQueue.getInstance().unRegisterObserver(mOnlineListUpdateObserver);
    }

    protected void searchFriend(String nickname, String ccno)
    {

    }

}