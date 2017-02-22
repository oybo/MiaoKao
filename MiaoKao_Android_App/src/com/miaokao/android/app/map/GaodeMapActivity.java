package com.miaokao.android.app.map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.miaokao.android.app.R;
import com.miaokao.android.app.ui.BaseActivity;

public class GaodeMapActivity extends BaseActivity implements LocationSource, AMapLocationListener,
		OnCheckedChangeListener {

	private MapView mMapView;
	private AMap mAMap;
	private AMapLocationClient mlocationClient;
	private AMapLocationClientOption mLocationOption;
	private OnLocationChangedListener mListener;
	private LatLng mLatLng;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_gaode_map);

		initView();
		mMapView.onCreate(savedInstanceState);// 此方法必须重写
		initMap();
	}

	private void initMap() {
		if (mAMap == null) {
			mAMap = mMapView.getMap();

			setUpMap();
		}
	}

	private void setUpMap() {
		mAMap.setLocationSource(this);// 设置定位监听
		mAMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
		mAMap.setMyLocationEnabled(mLatLng == null ? true :false);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		// 设置定位的类型为定位模式，参见类AMap。
		mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

		UiSettings uiSettings = mAMap.getUiSettings();
		uiSettings.setTiltGesturesEnabled(false);// 设置地图是否可以倾斜
		uiSettings.setScaleControlsEnabled(true);// 设置地图默认的比例尺是否显示
		uiSettings.setZoomControlsEnabled(false);
		
		if(mLatLng != null) {
			addMarker("", mLatLng, "");
		}
	}

	private void initView() {
		Intent intent = getIntent();
		String latitude = intent.getStringExtra("latitude");
		String longitude = intent.getStringExtra("longitude");
		String title = intent.getStringExtra("title");

		initTopBarLeftAndTitle(R.id.gd_map_common_actionbar, title);

		mMapView = (MapView) findViewById(R.id.map);

		if(!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude)) {
			mLatLng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
		}
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
		deactivate();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
		if (null != mlocationClient) {
			mlocationClient.onDestroy();
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {

	}

	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (mListener != null && amapLocation != null) {
			if (amapLocation != null && amapLocation.getErrorCode() == 0) {
				mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
				
				LatLng latLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
				addMarker("", latLng, "");
				
				// 定位成功就关闭它
				deactivate();
			} else {
				String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
				System.out.println(errText);
			}
		}
	}

	/**
	 * 往地图上添加marker
	 * 
	 * @param latLng
	 */
	private void addMarker(String title, LatLng latLng, String address) {
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.position(latLng);
		markerOptions.title(title);
		markerOptions.snippet(address);
		Bitmap bmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.location_tips);
		markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bmp));
		mAMap.clear();

		mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
		Marker locationMarker = mAMap.addMarker(markerOptions);
		drawCircleOnMap(latLng);
//		locationMarker.showInfoWindow();
	}
	
	/**
	 * 画半径值
	 * 
	 * @param Color_00000 设置半透明色
	 * @param locationLatLng 封装好的经纬度对象
	 */
	private void drawCircleOnMap(LatLng locationLatLng) {
		Circle circle = mAMap.addCircle(new CircleOptions()
		// 圆圈半径
				.center(locationLatLng).radius(600).strokeColor(Color.GREEN)// 圆圈颜色
				// 圆圈宽度
				.fillColor(Color.BLUE).strokeWidth(1));
		circle.setFillColor(Color.argb(15, 0, 0, 180));
	}
	
	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mlocationClient == null) {
			mlocationClient = new AMapLocationClient(this);
			mLocationOption = new AMapLocationClientOption();
			// 设置定位监听
			mlocationClient.setLocationListener(this);
			// 设置为高精度定位模式
			mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
			// 设置定位参数
			mlocationClient.setLocationOption(mLocationOption);
			// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
			// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
			// 在定位结束后，在合适的生命周期调用onDestroy()方法
			// 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
			mlocationClient.startLocation();
		}
	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
		if (mlocationClient != null) {
			mlocationClient.stopLocation();
			mlocationClient.onDestroy();
		}
		mlocationClient = null;
	}

}
