package com.playman.warcraft;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.kongqw.rockerlibrary.view.RockerView;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * @author Robod
 */
public class WarcraftController extends AppCompatActivity implements View.OnClickListener {

    public static String BT_ADDRESS = "address";

    private Vibrator vibrator = null; //震动的服务类

    /**
     * SPP服务UUID号
     */
    private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    private String deviceAddress = null;

    /**
     * 蓝牙通信socket
     */
    private BluetoothSocket socket = null;
    private BluetoothDevice device = null;
    private BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

    /**
     * 输出流
     */
    private OutputStream os;

    private String up;
    private String down;
    private String left;
    private String right;
    private String left_up;
    private String right_up;
    private String left_down;
    private String right_down;
    private String start;
    private String stop;
    private String speed_up;
    private String speed_down;

    private Button startBtn;
    private Button stopBtn;
    private Button speedUpBtn;
    private Button speedDownBtn;
    private Button resetButton;

    private RockerView rockerView;
    private TextView directionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.controller_layout);

        Intent intent = getIntent();
        deviceAddress = intent.getStringExtra(BT_ADDRESS);

        vibrator = (Vibrator)this.getSystemService(VIBRATOR_SERVICE);  //震动的服务类

        initParameters();
        initButton();
        initRocker();

        if (deviceAddress != null && socket == null) {
            device = adapter.getRemoteDevice(deviceAddress);
            try {
                socket = device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
                os = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        vibrator.vibrate(200);
        switch (v.getId()) {
            case R.id.start:
                writeIntoOutputstream(start);
                break;
            case R.id.stop:
                writeIntoOutputstream(stop);
                break;
            case R.id.speed_up:
                writeIntoOutputstream(speed_up);
                break;
            case R.id.speed_down:
                writeIntoOutputstream(speed_down);
                break;
            case R.id.reset:
                default:break;
        }
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        AlertDialog.Builder dialog = new AlertDialog.Builder(WarcraftController.this);
        dialog.setMessage("你确定要退出吗");
        dialog.setCancelable(false);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initRocker() {
        rockerView = findViewById(R.id.direction_rocker);
        // 设置回调模式
        rockerView.setCallBackMode(RockerView.CallBackMode.CALL_BACK_MODE_STATE_CHANGE);
        // 监听摇动方向
        rockerView.setOnShakeListener(RockerView.DirectionMode.DIRECTION_8, new RockerView.OnShakeListener() {
            @Override
            public void onStart() {
                vibrator.vibrate(300);
            }

            @Override
            public void direction(RockerView.Direction direction) {
                directionText.setText("摇动方向: " + getDirection(direction));
            }

            @Override
            public void onFinish() {

            }
        });
    }

    private void initButton() {
        directionText = findViewById(R.id.direction_text);

        startBtn = findViewById(R.id.start);
        startBtn.setOnClickListener(this);
        stopBtn = findViewById(R.id.stop);
        stopBtn.setOnClickListener(this);
        speedUpBtn = findViewById(R.id.speed_up);
        speedUpBtn.setOnClickListener(this);
        speedDownBtn = findViewById(R.id.speed_down);
        speedDownBtn.setOnClickListener(this);
        resetButton = findViewById(R.id.reset);
        resetButton.setOnClickListener(this);
    }

    private String getDirection(RockerView.Direction direction){
        String message = null;
        switch (direction) {
            case DIRECTION_LEFT:
                message = "左";
                writeIntoOutputstream(left);
                break;
            case DIRECTION_RIGHT:
                message = "右";
                writeIntoOutputstream(right);
                break;
            case DIRECTION_UP:
                message = "上";
                writeIntoOutputstream(up);
                break;
            case DIRECTION_DOWN:
                message = "下";
                writeIntoOutputstream(down);
                break;
            case DIRECTION_UP_LEFT:
                message = "左上";
                writeIntoOutputstream(left_up);
                break;
            case DIRECTION_UP_RIGHT:
                message = "右上";
                writeIntoOutputstream(right_up);
                break;
            case DIRECTION_DOWN_LEFT:
                message = "左下";
                writeIntoOutputstream(left_down);
                break;
            case DIRECTION_DOWN_RIGHT:
                message = "右下";
                writeIntoOutputstream(right_down);
                break;
            default:
                break;
        }
        return message;
    }

    /**
     * 向输出流里面写数据
     * @param data
     */
    private void writeIntoOutputstream(String data) {
        try {
            os.write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  初始化SharedPreferences里的参数信息
     */
    private void initParameters() {
        SharedPreferences preferences = getSharedPreferences("DIRECTION_PARAMETERS" , MODE_PRIVATE);
        //八个方位参数
        up = preferences.getString("up" , "up");
        down = preferences.getString("down" , "down");
        left = preferences.getString("left" , "left");
        right = preferences.getString("right" , "right");
        left_up = preferences.getString("left_up","left_up");
        right_up = preferences.getString("right_up" , "right_up");
        left_down = preferences.getString("left_down" , "left_down");
        right_down = preferences.getString("right_down" , "right_down");

        //启动，停止；加速，减速
        start = preferences.getString("start" , "start");
        stop = preferences.getString("stop" , "stop");
        speed_up = preferences.getString("speed_up" , "speed_up");
        speed_down = preferences.getString("speed_down" , "speed_down");
    }
}
