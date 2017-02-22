package com.miaokao.android.app.widget;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.miaokao.android.app.R;

/**
 * 自定义提示框
 * @author ouyangbo
 *
 */
public class DialogTips extends Dialog implements android.view.View.OnClickListener {
	
	private Context mContext;
	
	private TextView mTitleTxt;
	private TextView mMessageTxt;
	
	private Button mOkBt;
	private Button mCancelBt;
	
	private onDialogOkListenner mOkListenner;
	private onDialogCancelListenner mCancelListenner;
	
	public DialogTips(Context context) {
		this(context, "");
	}
	
	public DialogTips(Context context, String message) {
		this(context, "", message);
	}
	
	public DialogTips(Context context, String title, String message) {
		super(context, R.style.signin_dialog_style);
		
		mContext = context;
		init();
		
		setTitle(title);
		setMessage(message);
		
		mOkBt.setVisibility(View.VISIBLE);
	}
	
	private void init() {
		setContentView(R.layout.dialog_tips);
		this.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT; // 必须加这句
		
		mTitleTxt = (TextView) findViewById(R.id.dialog_title);
		mMessageTxt = (TextView) findViewById(R.id.dialog_message);
		
		mOkBt = (Button) findViewById(R.id.dialog_ok);
		mCancelBt = (Button) findViewById(R.id.dialog_cancel);
		
		mOkBt.setOnClickListener(this);
		mCancelBt.setOnClickListener(this);
	}

	public void setTitle(String title) {
		if(TextUtils.isEmpty(title)) {
			title = mContext.getString(R.string.dialog_tips);
		}
		mTitleTxt.setText(title);
	}
	
	public void setMessage(String message) {
		mMessageTxt.setText(message);
	}
	
	public void setOkListenner(onDialogOkListenner listenner) {
		setOkListenner("", listenner);
	}

	public void setOkListenner(String buttonTxt, onDialogOkListenner listenner) {
		if(!TextUtils.isEmpty(buttonTxt)) {
			mOkBt.setText(buttonTxt);
		}
		
		mOkBt.setVisibility(View.VISIBLE);
		this.mOkListenner = listenner;
	}
	
	public void setCancelListenner(onDialogCancelListenner listenner) {
		setCancelListenner("", listenner);
	}
	
	
	public void setCancelListenner(String buttonTxt, onDialogCancelListenner listenner) {
		if(!TextUtils.isEmpty(buttonTxt)) {
			mCancelBt.setText(buttonTxt);
		}
		
		mCancelBt.setVisibility(View.VISIBLE);
		findViewById(R.id.dialog_cancel_line).setVisibility(View.VISIBLE);
		this.mCancelListenner = listenner;
	}
	
	public interface onDialogOkListenner {
		public void onClick();
	}

	public interface onDialogCancelListenner {
		public void onClick();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_ok:
			if(mOkListenner != null) {
				mOkListenner.onClick();
			}
			dismiss();
			break;
		case R.id.dialog_cancel:
			if(mCancelListenner != null) {
				mCancelListenner.onClick();
			}
			dismiss();
			break;

		}
	}
	
}