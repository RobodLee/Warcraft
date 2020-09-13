package com.playman.warcraft;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @author Robod
 */
public class SettingActivity extends AppCompatActivity {

    private static final String TAG = "SettingActivity";

    private EditText up;
    private EditText down;
    private EditText left;
    private EditText right;
    private EditText left_up;
    private EditText right_up;
    private EditText left_down;
    private EditText right_down;
    private EditText start;
    private EditText stop;
    private EditText speed_up;
    private EditText speed_down;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        init();

    }

    private void init() {
        up = findViewById(R.id.up_text);
        down = findViewById(R.id.down_text);
        left = findViewById(R.id.left_text);
        right = findViewById(R.id.right_text);
        left_up = findViewById(R.id.left_up_text);
        right_up = findViewById(R.id.right_up_text);
        left_down = findViewById(R.id.left_down_text);
        right_down = findViewById(R.id.right_down_text);
        start = findViewById(R.id.start_text);
        stop = findViewById(R.id.stop_text);
        speed_up = findViewById(R.id.speed_up_text);
        speed_down = findViewById(R.id.speed_down_text);

        SharedPreferences preferences = getSharedPreferences("DIRECTION_PARAMETERS" , MODE_PRIVATE);
        up.setText(preferences.getString("up" , "未设置"));
        down.setText(preferences.getString("down" , "未设置"));
        left.setText(preferences.getString("left" , "未设置"));
        right.setText(preferences.getString("right" , "未设置"));
        left_up.setText(preferences.getString("left_up","未设置"));
        right_up.setText(preferences.getString("right_up" , "未设置"));
        left_down.setText(preferences.getString("left_down" , "未设置"));
        right_down.setText(preferences.getString("right_down" , "未设置"));
        start.setText(preferences.getString("start" , "未设置"));
        stop.setText(preferences.getString("stop" , "未设置"));
        speed_up.setText(preferences.getString("speed_up" , "未设置"));
        speed_down.setText(preferences.getString("speed_down" , "未设置"));
    }

    public void save(View view) {
        SharedPreferences.Editor editor = getSharedPreferences("DIRECTION_PARAMETERS" , MODE_PRIVATE).edit();
        editor.putString("up" , up.getText().toString());
        editor.putString("down" , down.getText().toString());
        editor.putString("left" , left.getText().toString());
        editor.putString("right" , right.getText().toString());
        editor.putString("left_up" , left_up.getText().toString());
        editor.putString("left_down" , left_down.getText().toString());
        editor.putString("right_up" , right_up.getText().toString());
        editor.putString("right_down" , right_down.getText().toString());
        editor.putString("start" , start.getText().toString());
        editor.putString("stop" , stop.getText().toString());
        editor.putString("speed_up" , speed_up.getText().toString());
        editor.putString("speed_down" , speed_down.getText().toString());

        editor.apply();
        //MyLogger.d(TAG , "保存成功");
        Toast.makeText(this , "保存成功" , Toast.LENGTH_SHORT).show();
        finish();
    }
}
