package com.example.qq66.fragment;

import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.example.qq66.Const;
import com.example.qq66.R;
import com.example.qq66.activity.LoginActivity;
import com.example.qq66.manager.XmppConnectionManager;
import com.example.qq66.utils.ActivityUtils;

public class DongtaiFragment extends BaseFragment {
    private XmppConnectionManager manager;

    private TextView tv_name;

    @Override
    protected View initView() {
        View view = View.inflate(getActivity(), R.layout.fragment_dongtai, null);
        view.findViewById(R.id.rl_exit).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.logout();
                ActivityUtils.startActivity(getActivity(), LoginActivity.class, true);
            }
        });
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        return view;
    }

    protected void initData() {
        String name = getSPData(Const.SP_KEY_NAME);
        tv_name.setText(TextUtils.isEmpty(name) ? "" : name);
        manager = XmppConnectionManager.getInstance();
    }

    ;
}
