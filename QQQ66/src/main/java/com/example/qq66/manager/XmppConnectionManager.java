package com.example.qq66.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smack.XMPPConnection;

import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.qq66.Const;

/**
 * XMPP管理类
 *
 * @author wzy 2015-9-8
 */
public class XmppConnectionManager {
    private static XmppConnectionManager manager;
    private XMPPConnection xmppConnection;

    private XmppConnectionManager() {
        initConnection();
    }

//	public synchronized static XmppConnectionManager getInstance() {
//		if (manager == null) {
//			manager = new XmppConnectionManager();
//		}
//		return manager;
//	}

    public static XmppConnectionManager getInstance() {
        if (manager == null) {
            synchronized (XmppConnectionManager.class) {
                if (manager == null) {

                    manager = new XmppConnectionManager();
                }
            }
        }
        return manager;
    }

    private void initConnection() {
        ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration(Const.HOST, Const.PORT);
        // 开启debug
        connectionConfiguration.setDebuggerEnabled(true);
        // 关闭安全验证登录方式
        connectionConfiguration.setSASLAuthenticationEnabled(false);
        // 数据是否使用安全模式
        connectionConfiguration.setSecurityMode(SecurityMode.disabled);
        // 设置数据是否压缩
        connectionConfiguration.setCompressionEnabled(false);
        // 是否给服务区发送在线状态
        connectionConfiguration.setSendPresence(true);
        // 设置是否重新自动连接服务器
        connectionConfiguration.setReconnectionAllowed(true);
        // asmack bug
        configure(ProviderManager.getInstance());
        xmppConnection = new XMPPConnection(connectionConfiguration);
    }

    public XMPPConnection getConnection() {
        if (xmppConnection != null) {
            try {
                if (!xmppConnection.isConnected()) {
                    xmppConnection.connect();
                }
            } catch (XMPPException e) {
                e.printStackTrace();
            }
            return xmppConnection;
        }
        return null;
    }

    /**
     * 为了解决asmack潜在bug需要实现的方法
     *
     * @param pm
     */
    public void configure(ProviderManager pm) {
        pm.addIQProvider("query", "jabber:iq:private", new PrivateDataManager.PrivateDataIQProvider());
        // Time
        try {
            pm.addIQProvider("query", "jabber:iq:time", Class.forName("org.jivesoftware.smackx.packet.Time"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Roster Exchange
        pm.addExtensionProvider("x", "jabber:x:roster", new RosterExchangeProvider());
        // Message Events
        pm.addExtensionProvider("x", "jabber:x:event", new MessageEventProvider());
        // Chat State
        pm.addExtensionProvider("active", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("composing", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("paused", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("inactive", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        pm.addExtensionProvider("gone", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
        // XHTML
        pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im", new XHTMLExtensionProvider());
        // Group Chat Invitations
        pm.addExtensionProvider("x", "jabber:x:conference", new GroupChatInvitation.Provider());
        // Service Discovery # Items //解析房间列表
        pm.addIQProvider("query", "http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());
        // Service Discovery # Info //某一个房间的信息
        pm.addIQProvider("query", "http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());
        // Data Forms
        pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());
        // MUC User
        pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user", new MUCUserProvider());
        // MUC Admin
        pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin", new MUCAdminProvider());
        // MUC Owner
        pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner", new MUCOwnerProvider());
        // Delayed Delivery
        pm.addExtensionProvider("x", "jabber:x:delay", new DelayInformationProvider());
        // Version
        try {
            pm.addIQProvider("query", "jabber:iq:version", Class.forName("org.jivesoftware.smackx.packet.Version"));
        } catch (ClassNotFoundException e) {
            // Not sure what's happening here.
        }
        // VCard
        pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());
        // Offline Message Requests
        pm.addIQProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageRequest.Provider());
        // Offline Message Indicator
        pm.addExtensionProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageInfo.Provider());
        // Last Activity
        pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());
        // User Search
        pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());
        // SharedGroupsInfo
        pm.addIQProvider("sharedgroup", "http://www.jivesoftware.org/protocol/sharedgroup", new SharedGroupsInfo.Provider());
        // JEP-33: Extended Stanza Addressing
        pm.addExtensionProvider("addresses", "http://jabber.org/protocol/address", new MultipleAddressesProvider());
        pm.addIQProvider("si", "http://jabber.org/protocol/si", new StreamInitiationProvider());
        pm.addIQProvider("query", "http://jabber.org/protocol/bytestreams", new BytestreamsProvider());
        pm.addIQProvider("query", "jabber:iq:privacy", new PrivacyProvider());
        pm.addIQProvider("command", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider());
        pm.addExtensionProvider("malformed-action", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.MalformedActionError());
        pm.addExtensionProvider("bad-locale", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadLocaleError());
        pm.addExtensionProvider("bad-payload", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadPayloadError());
        pm.addExtensionProvider("bad-sessionid", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadSessionIDError());
        pm.addExtensionProvider("session-expired", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.SessionExpiredError());

    }

    /**
     * 实现登录功能
     */
    public void login(String name, String pwd, Handler handler) {
        try {

            XMPPConnection connection = getConnection();
            if (!connection.isConnected()) {
                // 仅仅是连接到服务器
                connection.connect();
            }
            //判断是否已经登录，如果已经登录就直接设置登录成功
            if (!connection.isAuthenticated()) {
                connection.login(name, pwd);
            }
            // 代表登录成功
            handler.sendEmptyMessage(Const.MSG_LOGIN_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = Const.MSG_LOGIN_ERROR;
            msg.obj = e;
            handler.sendMessage(msg);
        }
    }

    /**
     * 用户注册
     *
     * @param name
     * @param pwd
     * @param mHandler
     */
    public void regist(String name, String pwd, Handler handler) {
        try {

            XMPPConnection connection = getConnection();

            if (!connection.isConnected()) {
                connection.connect();
            }
            /**
             * 通过XMPPConnection获取账户管理器
             */
            AccountManager accountManager = connection.getAccountManager();
            accountManager.createAccount(name, pwd);
            handler.sendEmptyMessage(Const.MSG_REGIST_SUCCESS);

        } catch (XMPPException e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = Const.MSG_REGIST_ERROR;
            int code = e.getXMPPError().getCode();
            if (code == 409) {
                msg.obj = "该用户名已经存在，请换一个用户名";
            } else {
                msg.obj = e;
            }
            handler.sendMessage(msg);
        }

    }

    /**
     * 注销当前用户
     */
    public void logout() {
        getConnection().disconnect();
    }

    /**
     * 获取当前登录用户的所有分组
     */
    public ArrayList<RosterGroup> getGroups() {
        Roster roster = getConnection().getRoster();
        return new ArrayList<>(roster.getGroups());
    }

    /**
     * 添加好友到指定的分组中
     *
     * @param name
     * @param group
     * @param handler
     * @return
     */
    public boolean addNewFriend(String name, String group, Handler handler) {

        if (searchUserInRoster(name)) {
            handler.sendEmptyMessage(Const.MSG_ADD_NEW_FRIEND_CONFLICT);
            return false;
        }
        boolean b = searchUserInServer(name);
        if (!b) {
            handler.sendEmptyMessage(Const.MSG_ADD_NEW_FRIEND_NOT_EXIT);
            return false;
        }
        Roster roster = getConnection().getRoster();

        String user = name + "@" + getConnection().getServiceName();
        try {
            //给自己的通讯录中添加好友
            roster.createEntry(user, name, new String[]{group});
            handler.sendEmptyMessage(Const.MSG_ADD_NEW_FRIEND_SUCCESS);
            return true;
        } catch (XMPPException e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = Const.MSG_ADD_NEW_FRIEND_ERROR;
            msg.obj = e;
            handler.sendMessage(msg);
            return false;
        }
    }

    /**
     * 去服务器中查询是否存在该用户
     *
     * @param name
     * @return Form searchForm = search.getSearchForm();
     * Form answerForm = searchForm.createAnswerForm();
     * answerForm.setAnswer("last", "DeMoro"); ReportedData data =
     * search.getSearchResults(answerForm); // Use Returned Data
     */
    private boolean searchUserInServer(String name) {
        try {
            UserSearchManager search = new UserSearchManager(getConnection());
            String searchService = "search." + getConnection().getServiceName();
            Form searchForm = search.getSearchForm(searchService);
            Form answerForm = searchForm.createAnswerForm();
            //告诉表单，我们需要Username数据
            answerForm.setAnswer("Username", true);
            answerForm.setAnswer("search", name);
            ReportedData searchResults = search.getSearchResults(answerForm, searchService);
            Iterator<Row> iterator = searchResults.getRows();
            while (iterator.hasNext()) {
                ReportedData.Row row = (ReportedData.Row) iterator.next();
                Iterator values = row.getValues("Username");
                String userString = values.next().toString();
                Log.d("tag", userString);
                if (name.equals(userString)) {
                    return true;
                }
            }
        } catch (XMPPException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 从当前用户的通讯录中查找好友
     *
     * @param name
     * @return
     */
    private boolean searchUserInRoster(String name) {
        Roster roster = getConnection().getRoster();
        Collection<RosterEntry> entries = roster.getEntries();
        for (RosterEntry entry : entries) {
            if (name.equals(entry.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 创建新分组
     *
     * @param group
     * @param handler
     * @return
     */
    public boolean addNewGroup(String name, Handler handler) {
        Roster roster = getConnection().getRoster();
        RosterGroup group = roster.getGroup(name);
        if (group != null) {
            handler.sendEmptyMessage(Const.MSG_ADD_NEW_GROUP_EXIT);
            return false;
        }
        RosterGroup createGroup = roster.createGroup(name);
        RosterEntry rosterEntry = roster.getEntry("admin@" + getConnection().getServiceName());
        try {
            createGroup.addEntry(rosterEntry);
        } catch (Exception e) {
            e.printStackTrace();
            handler.sendEmptyMessage(Const.MSG_ADD_NEW_GROUP_ERROR);
            return false;
        }
        handler.sendEmptyMessage(Const.MSG_ADD_NEW_GROUP_SUCCESS);

        return true;
    }

}
