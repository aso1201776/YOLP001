package jp.ac.st.asojuku.yolp001;

import jp.co.yahoo.android.maps.GeoPoint;
import jp.co.yahoo.android.maps.MapController;
import jp.co.yahoo.android.maps.MapView;
import jp.co.yahoo.android.maps.PinOverlay;
import jp.co.yahoo.android.maps.routing.RouteOverlay;
import jp.co.yahoo.android.maps.routing.RouteOverlay.RouteOverlayListener;
import jp.co.yahoo.android.maps.weather.WeatherOverlay;
import jp.co.yahoo.android.maps.weather.WeatherOverlay.WeatherOverlayListener;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends Activity implements LocationListener, WeatherOverlayListener, RouteOverlayListener, MapView.MapTouchListener {

	//LocationManagerを準備
	LocationManager mLocationManager = null;
	//MapViewを準備
	MapView mMapView = null;

	//直前の緯度(1000000倍精度)
	int lastLatitude = 0;
	//直前の経度(1000000倍精度)
	int lastLongitube = 0;

	//雨雲レーダー表示用のオーバーレイクラス変数を準備
	WeatherOverlay mWeatherOverlay = null;

	//ルート検索Overlay
	RouteOverlay mRouteOverlay = null;
	//開始位置のピン
	PinOverlay mPinOverlay = null;
	//出発地
	GeoPoint mStartPos;
	//目的地
	GeoPoint mGoalPos;
	//プログレスダイアログ
	ProgressDialog mProgDialog = null;
	//距離表示用テキストビュー
	TextView mDistLabel = null;
	//メニュークリア
	private static final int MENUITEM_CLEAR = 1;

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO 自動生成されたメソッド・スタブ
		//クリアメニューを追加
		menu.removeItem(MENUITEM_CLEAR);
		menu.add(0,MENUITEM_CLEAR, 0, "クリア");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO 自動生成されたメソッド・スタブ
		//メニュー選択処理
		switch (item.getItemID()) {
			case MENUITEM_CLEAR:
				//地図上からルートと距離表示をクリア
				if(mMapView != null) {
					mMapView.removeOverlay(mRouteOverlay);
					mRouteOverlay = null;
					if(mDistLabel != null) mDistLabel.setVisibility(View.INVISIBLE);
				}
				return true;
		}
		return false;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO 自動生成されたメソッド・スタブ

		//緯度の所得
		double lat = location.getLatitude();
		int latitude = (int)(lat * 1000000);
		//経度の取得
		double lon = location.getLatitude();
		int longitube = (int)(lon * 1000000);

		//緯度と経度のいずれかが直前の値と誤差が出れば、画面を更新(100で割ってもともとの緯度経度少数4桁、100mくらいの誤差にする)
		if(latitude/1000 != this.lastLatitude/1000 || longitube/1000 != this.lastLongitube/1000){
			//緯度経度情報(GeoPoint)の生成
			GeoPoint gp = new GeoPoint(latitude,longitube);
			//地図本体を取得
			MapController c = mMapView.getMapController();
			//地図本体にGeoPointを設定
			c.setCenter(gp);

			//今回の緯度経度を覚える
			this.lastLatitude = latitude;
			this.lastLongitube = longitube;
		}

	}

	@Override
	public boolean finishRouteSearch(RouteOverlay arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void errorUpdateWeather(WeatherOverlay arg0, int arg1) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void finishUpdateWeather(WeatherOverlay arg0) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public boolean onLongPress(MapView arg0, Object arg1, PinOverlay arg2, GeoPoint arg3) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public boolean onPinchIn(MapView arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public boolean onPinchOut(MapView arg0) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public boolean onTouch(MapView arg0, MotionEvent arg1) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	public boolean errorRouteSearch(RouteOverlay arg0, int arg1) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//現在位置
		mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,0,this);
		//プログレスダイアログを表示
		mProgDialog = new ProgressDialog(this);
		mProgDialog.setMessage("現在位置取得中");
		mProgDialog.show();

	}

	@Override
	protected void onResume() {
		// TODO 自動生成されたメソッド・スタブ
		super.onResume();

		//地図表示用のYahooライブラリview部品を用意
		mMapView = new MapView(this,"dj0zaiZpPTdhZ1hERlB4QU01ViZzPWNvbnN1bWVyc2VjcmV0Jng9Mjg-");
		//ズームボタンを画面にON
		mMapView.setBuiltInZoomControls(true);
		//地図縮尺バーを画面にON
		mMapView.setScalebar(true);

		//ここから、手動で地図をセット
		//渋谷駅の緯度経度のGeoPointを手書きで設定
		double lat = 35.658516;
		double lon = 139.7071773;
		GeoPoint gp = new GeoPoint((int)(lat * 1000000),(int)(lon * 1000000));
		//地図本体を取得
		MapController c = mMapView.getMapController();

		//地図本体にGeoPointを設定
		c.setCenter(gp);
		//地図本体のズームを３に設定
		c.setZoom(3);
		//地図本体を画面にセット
		setContentView(mMapView);

		//ここからGPSの使用
		//LocationManagerを取得
		mLocationManager =
				(LocationManager)getSystemService(Context.LOCATION_SERVICE);

		//位置測定のためのGPS制度や使用消費電力を設定するふるいにかけるためのCriteriaオブジェクトを生成
		Criteria criteria = new Criteria();

		//Accuracyを指定(低精度)
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);

		//PowerRequirementを指定(低消費電力)
		criteria.setPowerRequirement(Criteria.POWER_LOW);

		//位置情報を伝達してくれるロケーションプロバイダの所得
		String provider = mLocationManager.getBestProvider(criteria, true);

		//位置情報のイベントリスナーであるLocatuionlISTENERを登録
		mLocationManager.requestLocationUpdates(provider, 0, 0, this);

		//ここから、雨雲レーダー処理
		//雨雲レーダー用のオーバーレイ(WeatherOverlay)設定処理
		mWeatherOverlay = new WeatherOverlay(this);

		//雨雲レーダーの更新間隔を、分単位で指定
		mWeatherOverlay.startAutoUpdate(1);

		//MapViewにWeatherOverlayを追加
		mMapView.getOverlays().add(mWeatherOverlay);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
