package com.miaokao.android.app.util;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.coracle_photopicker_library.utils.FilePathUtils;
import com.miaokao.android.app.R;
import com.miaokao.android.app.util.DownloadFileManager.DownloadListenner;

/**
 * @TODO 
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2016-3-28 下午4:07:25 
 */
public class VideoPlayUtil implements OnPreparedListener {

	private ProgressBar mProgressBar;
	private VideoView mVideoView;
	private MediaController mMediaController;
	private int mPositionWhenPaused;
	private String mFilePath;
	
	public void init(Context context, View view, String url) {
		
		initView(context, view);
		
		mMediaController = new MediaController((Activity) context);
		mVideoView.setMediaController(mMediaController);
		mVideoView.setOnPreparedListener(this);
		mVideoView.setFocusableInTouchMode(false);
		mVideoView.setFocusable(false);
		mVideoView.setEnabled(false);
		
		String status = DownloadFileManager.mDownloadFiles.get(url);
		if("1".equals(status)) {
			// 直接播放
			String fileName = DownloadFileManager.getFileName(url);
			mFilePath = FilePathUtils.getDefaultFilePath(context) + "/" + fileName;
			mVideoView.setVideoPath(mFilePath);
		}
		if(!"0".equals(status)) {
			if(TextUtils.isEmpty(status) || "-1".equals(status)) {
				mProgressBar.setVisibility(View.VISIBLE);
				// 去下载
				DownloadFileManager.downloadFile(context, url, new DownloadListenner() {
					@Override
					public void downloadProgress(String path, int progress) {
						
					}
					
					@Override
					public void downloadFinish(String path, File saveFile) {
						mProgressBar.setVisibility(View.GONE);
						mFilePath = saveFile.getAbsolutePath();
						mVideoView.setVideoPath(mFilePath);
					}
					
					@Override
					public void downloadError(String path) {
						
					}
					
					@Override
					public void downloadBegin(String path, int allSize) {
						
					}
				});
			}
		}
		
	}
	
	private void initView(final Context context, View view) {
		mVideoView = (VideoView) view.findViewById(R.id.coach_detail_videoview);
		mProgressBar = (ProgressBar) view.findViewById(R.id.coach_detail_video_progress);
		
		mVideoView.post(new Runnable() {
			@Override
			public void run() {
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mVideoView.getWidth()
						- PubUtils.dip2px(context, 6), mVideoView.getHeight());
				params.addRule(RelativeLayout.CENTER_IN_PARENT);
				mVideoView.setLayoutParams(params);
			}
		});
	}
	
	public void hide() {
		if(mMediaController != null) {
			mMediaController.hide();
		}
	}
	
	public void onStart() {
		if(mVideoView != null && !TextUtils.isEmpty(mFilePath)) {
			mVideoView.setVideoPath(mFilePath);
			mVideoView.start();
		}
    }

	public void onPause() {
		if(mVideoView != null && !TextUtils.isEmpty(mFilePath)) {
			mPositionWhenPaused = mVideoView.getCurrentPosition();
			mVideoView.stopPlayback();
		}
    }

    public void onResume() {
    	if(mVideoView != null && !TextUtils.isEmpty(mFilePath)) {
    		if(mPositionWhenPaused >= 0) {
    			mVideoView.seekTo(mPositionWhenPaused);
    			mVideoView.start();
    			mPositionWhenPaused = -1;
    		}
    	}
    }
    
    public void onStop() {
    	if(mVideoView != null && !TextUtils.isEmpty(mFilePath)) {
    		mVideoView.stopPlayback();
    		mVideoView = null;
    	}
    }

	@Override
	public void onPrepared(MediaPlayer mp) {
		if(mVideoView != null && !TextUtils.isEmpty(mFilePath)) {
			mVideoView.start();
		}
	}
}
