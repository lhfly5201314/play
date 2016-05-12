package com.example.qq66.activity;

import org.jivesoftware.smack.XMPPConnection;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.qq66.Const;
import com.example.qq66.MainActivity;
import com.example.qq66.R;
import com.example.qq66.manager.XmppConnectionManager;
import com.example.qq66.utils.ActivityUtils;
import com.example.qq66.utils.MyTextUtils;

/**
 * 
 * @author wzy 2015-9-8
 *
 */
public class LoginActivity extends BaseActivity {
	
	
	private EditText et_name;
	private EditText et_pwd;
	private XmppConnectionManager manager;
	@Override
	public void initView() {
		setContentView(R.layout.activity_login);
		et_name = (EditText) findViewById(R.id.et_name);
		et_pwd = (EditText) findViewById(R.id.et_pwd);
	}
	@Override
	protected void initData() {
		manager = XmppConnectionManager.getInstance();
		String name = getSPData(Const.SP_KEY_NAME);
		String pwd = getSPData(Const.SP_KEY_PWD);
		et_name.setText(TextUtils.isEmpty(name)?"":name);
		et_pwd.setText(TextUtils.isEmpty(pwd)?"":pwd);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				XMPPConnection connection = manager.getConnection();
				if (connection.isAuthenticated()) {
					ActivityUtils.startActivity(LoginActivity.this,MainActivity.class,true);
				}
			}
		}).start();
		
		
	}
	/**
	 * 跳转到注册界面
	 * @param view
	 */
	public void gotoRegist(View view){
		ActivityUtils.startActivity(this,RegistActivity.class);
	}
	/**
	 * 执行登录逻辑
	 * @param view
	 */
	public void login(View view){
		final String name = et_name.getEditableText().toString().trim();
		final String pwd = et_pwd.getEditableText().toString().trim();
		if (MyTextUtils.isEmpty(name,pwd)) {
			Toast.makeText(this, "输入的内容不能为空！", Toast.LENGTH_SHORT).show();
			return ;
		}
		saveData(Const.SP_KEY_NAME, name);
		saveData(Const.SP_KEY_PWD, pwd);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				//登录 注册  添加好友 聊天的功能
				manager.login(name,pwd,mHandler);
			}
		}).start();
		
	}

}
