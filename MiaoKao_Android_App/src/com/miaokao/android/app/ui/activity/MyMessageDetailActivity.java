package com.miaokao.android.app.ui.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.miaokao.android.app.R;
import com.miaokao.android.app.entity.MKMessage;
import com.miaokao.android.app.ui.BaseActivity;

/**
 * @TODO 消息详情页
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2015-12-26 下午2:25:12
 */
public class MyMessageDetailActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_my_message_detail);

		MKMessage message = (MKMessage) getIntent().getExtras().getSerializable("message");
		if (message != null) {
			initTopBarLeftAndTitle(R.id.my_message_detail_common_actionbar, message.getMer_name());

			((TextView) findViewById(R.id.my_message_detail_content_txt)).setText(message.getContent());
		}
	}


}
