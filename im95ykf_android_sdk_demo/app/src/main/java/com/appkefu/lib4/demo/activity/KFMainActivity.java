package com.appkefu.lib4.demo.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.appkefu.appkefu_kefu_newchat.R;
import com.appkefu.lib.interfaces.KFAPIs;
import com.appkefu.lib.interfaces.KFCallBack;
import com.appkefu.lib.service.KFXmppManager;
import com.appkefu.lib.ui.activity.KFWebBrowserActivity;
import com.appkefu.lib.utils.KFConstants;
import com.appkefu.lib.utils.KFLog;
import com.appkefu.lib4.demo.adapter.ApiAdapter;
import com.appkefu.lib4.demo.entity.ApiEntity;
import com.appkefu.lib4.demo.utils.Constants;

import java.util.ArrayList;


public class KFMainActivity extends BaseActivity {

    /**
     * 注意：开发者将SDK嵌入到自己的应用中之后，至少要修改两处：
     * 1.appkey
     * 2.客服工作组名称(参见函数：startChat)
     */

    private TextView mTitle;
    private ListView mApiListView;
    private ArrayList<ApiEntity> mApiArray;
    private ApiAdapter mAdapter;
    //监听：连接状态、即时通讯消息、客服在线状态
    private BroadcastReceiver mXmppreceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //监听：连接状态
            if (action.equals(KFConstants.ACTION_XMPP_CONNECTION_CHANGED))//监听链接状态
            {
                updateStatus(intent.getIntExtra("new_state", 0));
            }
            //监听：即时通讯消息
            else if (action.equals(KFConstants.ACTION_XMPP_MESSAGE_RECEIVED))//监听消息
            {
                //消息内容
                String body = intent.getStringExtra("body");
                //消息来自于
                String from = intent.getStringExtra("from");
                KFLog.i("消息来自于:" + from + " 消息内容:" + body);
                KFLog.i("未读消息数目：" + KFAPIs.getUnreadMessageCount(Constants.COM_ID,Constants.WORK_GROUP_ID, KFMainActivity.this));

                //显示未读消息数目
                ApiEntity entity = mApiArray.get(8);
                entity.setUnreadMessageCounts(String.valueOf(KFAPIs.getUnreadMessageCount(Constants.COM_ID,Constants.WORK_GROUP_ID, KFMainActivity.this)));
                mAdapter.notifyDataSetChanged();
            }
            //客服工作组在线状态
            else if (action.equals(KFConstants.ACTION_XMPP_WORKGROUP_ONLINESTATUS)) {
                String fromWorkgroupName = intent.getStringExtra("from");
                String onlineStatus = intent.getStringExtra("onlinestatus");

                KFLog.d("客服工作组:" + fromWorkgroupName + " 在线状态:" + onlineStatus);//online：在线；offline: 离线

                ApiEntity entity = mApiArray.get(0);
                if (onlineStatus.equals("online")) {
                    entity.setApiName(getString(R.string.chat_with_kefu_1) + "(在线)");
                    KFLog.d("online:" + entity.getApiName());
                } else {
                    entity.setApiName(getString(R.string.chat_with_kefu_1) + "(离线)");
                    KFLog.d("offline:" + entity.getApiName());
                }
                mApiArray.set(0, entity);
                mAdapter.notifyDataSetChanged();

                entity = mApiArray.get(1);
                if (onlineStatus.equals("online")) {
                    entity.setApiName(getString(R.string.chat_with_kefu_2) + "(在线)");
                    KFLog.d("online:" + entity.getApiName());
                } else {
                    entity.setApiName(getString(R.string.chat_with_kefu_2) + "(离线)");
                    KFLog.d("offline:" + entity.getApiName());
                }
                mApiArray.set(1, entity);
                mAdapter.notifyDataSetChanged();

                entity = mApiArray.get(2);
                if (onlineStatus.equals("online")) {
                    entity.setApiName(getString(R.string.chat_with_e_commence) + "(在线)");
                    KFLog.d("online:" + entity.getApiName());
                } else {
                    entity.setApiName(getString(R.string.chat_with_e_commence) + "(离线)");
                    KFLog.d("offline:" + entity.getApiName());
                }
                mApiArray.set(2, entity);
                mAdapter.notifyDataSetChanged();
            }
            //使用第二种登录方式
            else if (action.equals(KFConstants.ACTION_XMPP_REGISTER_RESULT)) {

                Boolean result = intent.getBooleanExtra("result", false);
                KFLog.i("result:"+result);
                if (result) {
                    Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        KFLog.i("onCreate");
        //登录方式(允许IP服务器登录方式)
        //KFAPIs.enableIPServerMode(true, this);

        //访客登录（带udid)
        KFAPIs.visitorLogin(this);

        //UserID登录，传入user_id, 注意user_id中只能包含数字、字母和下划线_,不能含有汉字,默认密码:"appkefu"
        //KFAPIs.loginWithUserID("hl95zxkf", this);

        //第三中登录方式，指定user_id,password调用注册接口注册一个用户，一个user_id只能注册一次，不能重复注册
/*      String  userid="hl95zxkf";
        String  password="hl95hl95";
        KFAPIs.registerWithUsernameAndPassword(userid, password, this);
        //注册用户之后，使用user_id调用登录接口登录
        KFAPIs.loginWithUsernameAndPassword(userid, password, this);*/

        //初始化view
        initView();

    }

    @Override
    protected void onStart() {
        super.onStart();
        KFLog.i("onStart");
        IntentFilter intentFilter = new IntentFilter();
        //监听网络连接变化情况
        intentFilter.addAction(KFConstants.ACTION_XMPP_CONNECTION_CHANGED);
        //监听消息
        intentFilter.addAction(KFConstants.ACTION_XMPP_MESSAGE_RECEIVED);
        //工作组在线状态
        intentFilter.addAction(KFConstants.ACTION_XMPP_WORKGROUP_ONLINESTATUS);
        //监听第二种登录方式
        //intentFilter.addAction(KFConstants.ACTION_XMPP_REGISTER_RESULT);
        //注册xmpp广播接收器
        registerReceiver(mXmppreceiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        KFLog.i("onResume");
        //显示未读消息数目
        ApiEntity entity = mApiArray.get(8);
        entity.setUnreadMessageCounts(String.valueOf(KFAPIs.getUnreadMessageCount(Constants.COM_ID,Constants.WORK_GROUP_ID, KFMainActivity.this)));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        KFLog.i("onStop");
        if (mXmppreceiver != null) {
            unregisterReceiver(mXmppreceiver);
           // mXmppreceiver = null;
        }
    }


    private void initView() {
        //界面标题
        mTitle = (TextView) findViewById(R.id.demo_title);
        mApiListView = (ListView) findViewById(R.id.api_list_view);
        mApiArray = new ArrayList<ApiEntity>();
        mAdapter = new ApiAdapter(this, mApiArray);
        mApiListView.setAdapter(mAdapter);

        ApiEntity entity = new ApiEntity(1, getString(R.string.chat_with_kefu_1));
        mApiArray.add(entity);
        entity = new ApiEntity(2, getString(R.string.chat_with_kefu_2));
        mApiArray.add(entity);
        entity = new ApiEntity(3, getString(R.string.chat_with_e_commence));
        mApiArray.add(entity);
        entity = new ApiEntity(4, getString(R.string.chat_with_robot));
        mApiArray.add(entity);
        entity = new ApiEntity(5, getString(R.string.set_user_tags));
        mApiArray.add(entity);
        entity = new ApiEntity(6, getString(R.string.clear_message_records));
        mApiArray.add(entity);
        entity = new ApiEntity(7, getString(R.string.show_faq));
        mApiArray.add(entity);
        entity = new ApiEntity(8, getString(R.string.chat_with_leavemessage));
        mApiArray.add(entity);
        entity = new ApiEntity(9, getString(R.string.unread_message_count));
        //显示未读消息数目
        entity.setUnreadMessageCounts(String.valueOf(KFAPIs.getUnreadMessageCount(Constants.COM_ID,Constants.WORK_GROUP_ID, KFMainActivity.this)));
        mApiArray.add(entity);
        //wapchat
        entity = new ApiEntity(10, getString(R.string.chat_with_wap));
        mApiArray.add(entity);

        mAdapter.notifyDataSetChanged();

        mApiListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int index, long id) {
                // TODO Auto-generated method stub
                ApiEntity entity = mApiArray.get(index);
                switch (entity.getId()) {
                    //普通模式请求客服
                    case 1:
                        startChat();
                        break;
                    //普通模式请求客服
                    case 2:
                        startChat2();
                        break;
                    //电商模式请求客服
                    case 3:
                        startECChat();
                        break;
                    //默认机器人应答
                    case 4:
                        startChatRobot();
                        break;
                    //设置用户标签
                    case 5:
                        showTagList();
                        break;
                    //清空手机上的聊天记录
                    case 6:
                        clearMessages();
                        break;
                    //常见问题FAQ
                    case 7:
                        showFAQ();
                        break;
                    //留言页面
                    case 8:
                        leaveMessage();
                        break;
                    //未读消息数
                    case 9:
                        getUnreadMessageCount();
                        break;
                    //wapchat
                    case 10:
                        chatWithWap();
                        break;
                    default:
                        break;
                }
            }
        });
    }


    private void startChat() {
        //VIP用户接口, 允许设置客服头像
        //Bitmap kefuAvatarBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.kefu);
        //Bitmap userAvatarBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.user);
        //
        KFAPIs.startChat(this,
                //1. 企业id，请在管理后台查看
                Constants.COM_ID,
                //2. 客服工作组名称(请务必保证大小写一致)，请在管理后台分配
                Constants.WORK_GROUP_ID,
                //3. 会话界面标题，可自定义
                Constants.CHAT_SESSION_TITLE,
                //4. 附加信息，在成功对接客服之后，会自动将此信息发送给客服,如果不想发送此信息，可以将此信息设置为""或者null
                null,
                //5. 是否显示自定义菜单,如果设置为显示,请务必首先在管理后台设置自定义菜单,请务必至少分配三个且只分配三个自定义菜单,
                // 多于三个的暂时将不予显示,显示:true, 不显示:false
                true,
                //6. 默认显示消息数量
                5,
                //7. 修改默认客服头像，如果不想修改默认头像，设置此参数为null
                //修改SDK自带的头像有两种方式，1.直接替换appkefu_message_toitem和appkefu_message_fromitem.xml里面的头像，2.传递网络图片自定义
                null,
                //8. 修改默认用户头像, 如果不想修改默认头像，设置此参数为null
                "http://im.95ykf.com/AppKeFu/images/user-avatar.png",
                //9. 默认机器人应答
                true,
                //用户在关闭对话的时候是否强制满意度评价, true：是; false：否
                false,
//				"03",
                null);

    }

    //
    private void startChat2() {
        //VIP用户接口, 允许设置客服头像
        KFAPIs.startChat(this,
                //1. 企业id，请在管理后台查看
                Constants.COM_ID,
                //2. 客服工作组名称(请务必保证大小写一致)，请在管理后台分配
                Constants.WORK_GROUP_ID,
                //3. 会话界面标题，可自定义
                Constants.CHAT_SESSION_TITLE,
                //4. 附加信息，在成功对接客服之后，会自动将此信息发送给客服,如果不想发送此信息，可以将此信息设置为""或者null
                null,
                //5. 是否显示自定义菜单,如果设置为显示,请务必首先在管理后台设置自定义菜单,请务必至少分配三个且只分配三个自定义菜单,
                // 多于三个的暂时将不予显示,显示:true, 不显示:false
                false,
                //6. 默认显示消息数量
                0,
                //7. 修改默认客服头像，如果不想修改默认头像，设置此参数为null
                //修改SDK自带的头像有两种方式，1.直接替换appkefu_message_toitem和appkefu_message_fromitem.xml里面的头像，2.传递网络图片自定义
                null,
                //8. 修改默认用户头像, 如果不想修改默认头像，设置此参数为null
                null,
                //9. 默认机器人应答
                false,
                //用户在关闭对话的时候是否强制满意度评价, true：是; false：否
                false,
//				"03",
                //10. 会话页面右上角回调函数
                new KFCallBack() {
                    @Override
                    public Boolean useTopRightBtnDefaultAction() {
//                        return true;
                          return false;
                    }
                    @Override
                    public void OnChatActivityTopRightButtonClicked() {
                        // TODO Auto-generated method stub
                        Toast.makeText(KFMainActivity.this, "右上角回调接口调用", Toast.LENGTH_SHORT).show();
                        //测试右上角回调接口调用
                        showTagList();
                    }

                    @Override
                    public void OnECGoodsImageViewClicked(String imageViewURL) {
                        // TODO Auto-generated method stub
                        Toast.makeText(KFMainActivity.this, "OnECGoodsImageViewClicked", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void OnECGoodsTitleDetailClicked(String titleDetailString) {
                        // TODO Auto-generated method stub
                        Toast.makeText(KFMainActivity.this, "OnECGoodsIntroductionClicked", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void OnECGoodsPriceClicked(String priceString) {
                        // TODO Auto-generated method stub
                        Toast.makeText(KFMainActivity.this, "OnECGoodsPriceClicked", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void OnEcGoodsInfoClicked(String callbackId) {
                        // TODO Auto-generated method stub
                    }

                    /**
                     * 用户点击会话页面下方“常见问题”按钮时，是否使用自定义action，如果返回true,
                     * 则默认action将不起作用，会调用OnFaqButtonClicked函数
                     */
                    public Boolean userSelfFaqAction() {
                        return true;
                    }

                    /**
                     * 用户点击“常见问题”按钮时，自定义action回调函数接口
                     */
                    @Override
                    public void OnFaqButtonClicked() {
                        Toast.makeText(KFMainActivity.this, "点击常见问题自定义回调接口", Toast.LENGTH_SHORT).show();
                    }

                }
        );

    }

    //3.电商模式请求
    private void startECChat() {
        KFAPIs.startECChat(this,
                //1. 企业id，请在管理后台查看
                Constants.COM_ID,
                //2. 客服工作组名称(请务必保证大小写一致)，请在管理后台分配
                Constants.WORK_GROUP_ID,
                //3. 会话界面标题，可自定义
                Constants.WORK_GROUP_ID,
                //4. 附加信息，在成功对接客服之后，会自动将此信息发送给客服;
                //   如果不想发送此信息，可以将此信息设置为""或者null
                //null,
                "九五在线客服  200000元  <img src=\"http://im.95ykf.com/AppKeFu/images/logo.png\">",
                 //"<a href=\"http://item.jd.com/3379941.html\" target=\"_blank\"><span class=\"list-tmb\"><img src=\"http://img10.360buyimg.com/n6/jfs/t3265/14/3071930784/221342/49b4680e/57eba0feN87f5c864.jpg\" height=\"60\" width=\"60\"></span><span class=\"list-txt\"><span>商品编号：3379941</span><span>商品名称：魅族 魅蓝note3 移动定制版 16GB 灰色 移动联通4G手机 双卡双待</span></span></a>",
                //5. 是否显示自定义菜单,如果设置为显示,请务必首先在管理后台设置自定义菜单,
                //	请务必至少分配三个且只分配三个自定义菜单,多于三个的暂时将不予显示
                //	显示:true, 不显示:false
                true,
                //6. 默认显示消息数量
                5,
                //7. 修改默认客服头像，如果不想修改默认头像，设置此参数为null
                null,
                //8. 修改默认用户头像, 如果不想修改默认头像，设置此参数为null
                null,
                //9. 默认机器人应答
                false,
                //10. 是否显示商品详情，显示：true；不显示：false
                true,
                //11.商品详情图片
                "http://im.95ykf.com/AppKeFu/images/logo.png",
                //12.商品详情简介
                "九五在线客服",
                //13.商品详情价格
                "200000元",
                //14.商品网址链接
                "http://im.95ykf.com",
                //15.点击商品详情布局回调参数
                "goodsCallbackId",
                //16.退出对话的时候是否强制评价，强制：true，不评价：false
                false,
                //true,
                //16. 会话页面右上角回调函数
                new KFCallBack() {

                    /**
                     * 16.是否使用对话界面右上角默认动作. 使用默认动作返回：true, 否则返回false
                     */
                    @Override
                    public Boolean useTopRightBtnDefaultAction() {
                        return true;
                    }

                    /**
                     * 17.点击对话界面右上角按钮动作，依赖于 上面一个函数的返回结果
                     */
                    @Override
                    public void OnChatActivityTopRightButtonClicked() {
                        // TODO Auto-generated method stub
                        Log.d("KFMainActivity", "右上角回调接口调用");
                        Toast.makeText(KFMainActivity.this, "右上角回调接口调用", Toast.LENGTH_SHORT).show();

                    }

                    /**
                     * 18.点击商品详情图片回调函数
                     */
                    @Override
                    public void OnECGoodsImageViewClicked(String imageViewURL) {
                        // TODO Auto-generated method stub

                        Log.d("KFMainActivity", "OnECGoodsImageViewClicked" + imageViewURL);

                    }

                    /**
                     * 19.点击商品详情简介回调函数
                     */
                    @Override
                    public void OnECGoodsTitleDetailClicked(String titleDetailString) {
                        // TODO Auto-generated method stub
                        Log.d("KFMainActivity", "OnECGoodsTitleDetailClicked" + titleDetailString);

                    }

                    /**
                     * 20.点击商品详情价格回调函数
                     */
                    @Override
                    public void OnECGoodsPriceClicked(String priceString) {
                        // TODO Auto-generated method stub
                        Log.d("KFMainActivity", "OnECGoodsPriceClicked" + priceString);

                    }

                    /**
                     * 21.点击商品详情布局回调函数
                     */
                    @Override
                    public void OnEcGoodsInfoClicked(String callbackId) {
                        // TODO Auto-generated method stub
                        Log.d("KFMainActivity", "OnEcGoodsInfoClicked" + callbackId);

                    }

                    /**
                     * 用户点击会话页面下方“常见问题”按钮时，是否使用自定义action，如果返回true,则默认action将不起作用，会调用OnFaqButtonClicked函数
                     */
                    public Boolean userSelfFaqAction() {
                        return false;
                    }

                    /**
                     * 用户点击“常见问题”按钮时，自定义action回调函数接口
                     */
                    @Override
                    public void OnFaqButtonClicked() {
                        Log.d("KFMainActivity", "OnFaqButtonClicked");
                    }

                });

    }

    //4.默认智能机器人应答
    private void startChatRobot() {
        KFAPIs.startChat(this,
                //1. 企业id，请在管理后台查看
                Constants.COM_ID,
                //2. 客服工作组名称(请务必保证大小写一致)，请在管理后台分配
                Constants.WORK_GROUP_ID,
                //3. 会话界面标题，可自定义
                Constants.WORK_GROUP_ID,
                //4. 附加信息，在成功对接客服之后，会自动将此信息发送给客服;
                //如果不想发送此信息，可以将此信息设置为""或者null
                null,
                //5. 是否显示自定义菜单,如果设置为显示,请务必首先在管理后台设置自定义菜单,
                //请务必至少分配三个且只分配三个自定义菜单,多于三个的暂时将不予显示
                //显示:true, 不显示:false
                true,
                //6. 默认显示消息数量
                5,
                //修改SDK自带的头像有两种方式：
                //1.直接替换appkefu_message_toitem和appkefu_message_fromitem.xml里面的头像
                //2.传递网络图片自定义
                //"http://47.90.33.185/images/kefu-avatar.png",
                //7. 修改默认客服头像，如果不想修改默认头像，设置此参数为null
                null,
                //8. 修改默认用户头像, 如果不想修改默认头像，设置此参数为null
                "http://47.90.33.185/images/user-avatar.png",
                //9. 默认机器人应答
                true,
                //10.用户在关闭对话的时候是否强制满意度评价, true：是; false：否
                false,
//				"03",
                null);
    }

    //5.显示标签列表
    private void showTagList() {
        Intent intent = new Intent(this, TagListActivity.class);
        startActivity(intent);
    }

    //6.清空手机上的聊天记录
    private void clearMessages() {
        //此处填写 客服工作组名称
        KFAPIs.clearMessageRecords(Constants.COM_ID,Constants.WORK_GROUP_ID, this);
        Toast.makeText(this, "清空聊天记录", Toast.LENGTH_LONG).show();
    }

    //7.常见问题FAQ
    private void showFAQ() {
        KFAPIs.showFAQ(this, Constants.COM_ID,Constants.WORK_GROUP_ID);
    }

    //8.客服中心
    private void kefuCenter() {
        KFAPIs.kefuCenter(this, Constants.COM_ID,Constants.WORK_GROUP_ID);
    }

    /*//9.用户反馈
    private void feedback() {
        KFAPIs.feedBack(this, //Context, 上下文
                "wgdemo", //工作组ID
                "用户反馈", //会话窗口标题
                false, //是否显示自定义菜单
                5, //默认显示历史消息记录条数
                null, //自定义客服头像URL链接
                null);//自定义用户头像URL链接
    }*/

    //留言页面
    private void leaveMessage() {
        KFAPIs.startLeaveMessage(this,Constants.COM_ID,Constants.WORK_GROUP_ID);
    }

    //未读消息数目
    private void  getUnreadMessageCount() {
        int counts= KFAPIs.getUnreadMessageCount(Constants.COM_ID,Constants.WORK_GROUP_ID,this);
        Toast.makeText(this,"未读消息数目："+counts,Toast.LENGTH_SHORT);
    }

    //wap请求客服
    private void chatWithWap() {
        Intent intent = new Intent(this, KFWebBrowserActivity.class);
        intent.putExtra("ismenu", true);
        intent.putExtra("title", "wapChat");
        intent.putExtra("url", "http://im.95ykf.com/AppKeFu/float/wap/chat.php?comid="+Constants.COM_ID+"&wg="+Constants.WORK_GROUP_ID+"&robot=false&hidenav=true");
        startActivity(intent);
    }

    //根据监听到的连接变化情况更新界面显示
    private void updateStatus(int status) {
        KFLog.d("updateStatus(status):"+status);
        switch (status) {
            case KFXmppManager.CONNECTED:
                mTitle.setText("九五在线客服(Demo)");

                //查询客服工作组在线状态，返回结果在BroadcastReceiver中返回
                KFAPIs.checkKeFuIsOnlineAsync(Constants.COM_ID,Constants.WORK_GROUP_ID, this);
                KFAPIs.checkKeFuIsOnlineAsync(Constants.COM_ID,Constants.WORK_GROUP_ID, this);

                break;
            case KFXmppManager.DISCONNECTED:
                mTitle.setText("九五在线客服(Demo)(未连接)");
                break;
            case KFXmppManager.CONNECTING:
                mTitle.setText("九五在线客服(Demo)(登录中...)");
                break;
            case KFXmppManager.DISCONNECTING:
                mTitle.setText("九五在线客服(Demo)(登出中...)");
                break;
            case KFXmppManager.WAITING_TO_CONNECT:
            case KFXmppManager.WAITING_FOR_NETWORK:
                mTitle.setText("九五在线客服(Demo)(等待中)");
                break;
            default:
                throw new IllegalStateException();
        }
    }

}

















