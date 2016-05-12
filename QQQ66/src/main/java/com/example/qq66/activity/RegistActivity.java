package com.example.qq66.activity;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.qq66.Const;
import com.example.qq66.R;
import com.example.qq66.manager.XmppConnectionManager;
import com.example.qq66.utils.MyTextUtils;

/**
 * 
 * @author wzy 2015-9-8
 * 
 */
public class RegistActivity extends BaseActivity {
	private EditText et_name;
	private EditText et_pwd;
	private XmppConnectionManager manager;

	@Override
	public void initView() {
		setContentView(R.layout.activity_regist);
		et_name = (EditText) findViewById(R.id.et_name);
		et_pwd = (EditText) findViewById(R.id.et_pwd);
	}

	@Override
	protected void initData() {
		manager = XmppConnectionManager.getInstance();
	}

	/**
	 * 实现注册逻辑
	 * 
	 * @param view
	 */
	public void regist(View view) {
		final String name = et_name.getEditableText().toString().trim();
		final String pwd = et_pwd.getEditableText().toString().trim();
		if (MyTextUtils.isEmpty(name, pwd)) {
			Toast.makeText(this, "输入的内容不能为空！", Toast.LENGTH_SHORT).show();
			return;
		}
		saveData(Const.SP_KEY_NAME, name);
		saveData(Const.SP_KEY_PWD, pwd);

		//注册也是耗时的操作，要在子线程中做
		new Thread(new Runnable() {

			@Override
			public void run() {
				manager.regist(name, pwd, mHandler);
			}
		}).start();

	}

}
