package com.example.jingleweather;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherapp2.R;

public class MainActivity extends BaseActivity {

	private Button sendButton;
	private EditText editText;
	private TextView textView, textView1, textView2, textView3, textView4,
			textView5;
	private String city;
	private String url;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				try {
					JSONObject jsonObject = new JSONObject(msg.obj.toString());
					JSONObject jsonObject2 = jsonObject.getJSONObject("data");
					String city = jsonObject2.getString("city");
					String aqi;
					try {
						aqi = jsonObject2.getString("aqi");// 有些城市没有空气质量
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						aqi = "";
					}
					String wendu = jsonObject2.getString("wendu");
					String ganmao = jsonObject2.getString("ganmao");
					JSONArray forecasts = jsonObject2.getJSONArray("forecast");
					String today = "今日：【" + city + "】" + "\n" + "温度:" + wendu
							+ "℃" + "\n" + "空气AQI:" + aqi + "\n" + "感冒情况:"
							+ ganmao;
					textView.setText(today);
					TextView[] textViews = { textView1, textView2, textView3,
							textView4, textView5 };

					for (int j = 0; j < forecasts.length(); j++) {
						JSONObject jsonObject21 = forecasts.getJSONObject(j);
						String fengxiang = jsonObject21.getString("fengxiang");
						String fengli = jsonObject21.getString("fengli");
						String high = jsonObject21.getString("high");
						String type = jsonObject21.getString("type");
						String low = jsonObject21.getString("low");
						String date = jsonObject21.getString("date");
						String forecastString = "[" + date + "]:" + type + "\n"
								+ "温度区间:" + low + "~~" + high + "\n" + "风向:"
								+ fengxiang + "\n" + "风力:" + fengli;
						if (j % 2 == 0) {
							textViews[j].setTextColor(Color
									.parseColor("#ff0000"));

						}
						textViews[j].setText(forecastString);
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// textView.setText(msg.obj.toString());
				break;
			case 2:
				Toast.makeText(MainActivity.this, "网络错误，请重新设置网络！",
						Toast.LENGTH_LONG).show();
				break;

			default:
				break;
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		sendButton = (Button) findViewById(R.id.sendButton);
		textView = (TextView) findViewById(R.id.weather);
		textView1 = (TextView) findViewById(R.id.weather1);
		textView2 = (TextView) findViewById(R.id.weather2);
		textView3 = (TextView) findViewById(R.id.weather3);
		textView4 = (TextView) findViewById(R.id.weather4);
		textView5 = (TextView) findViewById(R.id.weather5);

		editText = (EditText) findViewById(R.id.city);
		sendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				try {
					city = URLEncoder.encode(editText.getText().toString(),
							"utf-8");
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				url = "http://wthrcdn.etouch.cn/weather_mini?city=" + city;
				HttpUtil.sendHttpRequestByConnection(url,
						new HttpCallbackListener() {

							@Override
							public void onFinish(String response) {
								// TODO Auto-generated method stub

								Message msg = Message.obtain();
								msg.what = 1;
								msg.obj = response;
								handler.sendMessage(msg);

							}

							@Override
							public void onError(Exception e) {
								// TODO Auto-generated method stub
								Log.e("exception", "网络错误");

								Message msg = Message.obtain();
								msg.what = 2;
								handler.sendMessage(msg);

							}
						});

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.about_menu) {
			Intent intent = new Intent(MainActivity.this, AboutActivity.class);
			startActivity(intent);
			return true;
		} else if (id == R.id.exit_menu) {
			ActivityCollector.finishAllActivity();
		}
		return super.onOptionsItemSelected(item);
	}
}
