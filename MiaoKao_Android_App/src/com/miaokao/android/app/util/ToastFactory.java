package com.miaokao.android.app.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * 解决Toast重复出现多次，保持全局只有一个Toast实例
 *
 * @author adison
 */
public class ToastFactory {

    private static Toast toast = null;
    private static Toast customToast = null;

    public static Toast getToast(Context context, String text) {
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        }
        toast.setText(text);

        return toast;
    }

    public static Toast showToastCenter(Context context, String text) {
        if (customToast == null) {
            customToast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        }
//        View view = LayoutInflater.from(context).inflate(R.layout.include_chat_voice_short, null);
//        customToast.setView(view);

        customToast.setText(text);

        customToast.setGravity(Gravity.CENTER, 0, 0);
        customToast.setDuration(800);
        return customToast;
    }

    public static void cancelToast() {
        if (toast != null) {
            toast.cancel();
        }
        if(customToast != null) {
            customToast.cancel();
        }
    }

}
