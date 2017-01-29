package app.shome.ir.shome;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Random;

import app.shome.ir.shome.service.ServiceDelegate;
import app.shome.ir.shome.service.Services;
import app.shome.ir.shome.ui.LoginActivity;

public class SettingActivity extends AppCompatActivity implements ServiceDelegate {


    ImageButton scanHub;
    RelativeLayout layout;
    Button start, checkConnection;
    Handler handler;
    EditText ip;
    //    EditText port;
    int dp;
    //    int portNO = -1;
    String ipp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting2);
        ip = (EditText) findViewById(R.id.ip);
        ip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                start.setBackgroundColor(Color.RED);
                start.setClickable(false);
            }
        });
//        port = (EditText) findViewById(R.id.portNum);
//        port.setText(""+Const.LOCAL_PORT);
        scanHub = (ImageButton) findViewById(R.id.scanHub);
        start = (Button) findViewById(R.id.start);
        checkConnection = (Button) findViewById(R.id.checkconnection);
        checkConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean ok = true;
                if (ip.getText().toString().trim().length() == 0) {
                    Toast.makeText(SettingActivity.this, "IP Empty", Toast.LENGTH_SHORT).show();
                    ok = false;
                }
                if (ok) {

                    new Services.CheckConnection(Const.CHECK_CONNECTION_REQUEST, SettingActivity.this, ip.getText().toString().trim(), Const.LOCAL_PORT).execute();
                }

            }
        });

        layout = (RelativeLayout) findViewById(R.id.layout);
        handler = new Handler(Looper.getMainLooper());
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        dp = Math.round(200 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        start.setClickable(false);
        start.setBackgroundColor(Color.RED);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                start(ip.getText().toString(), Const.LOCAL_PORT);
            }
        });
        final Animation rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_rotation);
        scanHub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                layout.removeAllViews();
                scanHub.setBackgroundResource(R.drawable.radar2);
                rotation.setRepeatCount(Animation.INFINITE);
                rotation.setDuration(3000);
                scanHub.startAnimation(rotation);
                scanHub.setClickable(false);
                new Services.DetectLocalServer(Const.DETECT_SERVER_REQUEST, SettingActivity.this, Const.LOCAL_PORT).execute();


            }
        });
    }

    private void addServer(final String ip) {
        ImageView v = new ImageView(this);
        RelativeLayout.LayoutParams a = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        Random random = new Random();
        a.topMargin = random.nextInt(dp);
        a.leftMargin = random.nextInt(dp);
        v.setImageResource(R.drawable.seekbar_normal);
        v.setLayoutParams(a);
        layout.addView(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start(ip, Const.LOCAL_PORT);

            }
        });

    }

    private void start(String ip, int port) {
        SHomeApplication.LOCAL_IP = ip;
        SHomeApplication.LOCAL_PORT = port;
        SHomeApplication.save();
        Intent a = new Intent(SettingActivity.this, LoginActivity.class);
        startActivity(a);
    }

    @Override
    public void onPostResult(int requestCode, final int status, final String date) {
        if (requestCode == Const.DETECT_SERVER_REQUEST) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (date != null)
                        addServer(date);
                    else {
                        scanHub.setClickable(true);
                        Animation animation = scanHub.getAnimation();
                        if (animation != null)
                            scanHub.setBackgroundColor(Color.TRANSPARENT);
                        animation.cancel();
                    }


                }
            });

        } else if (requestCode == Const.CHECK_CONNECTION_REQUEST) {
            if (status == Const.OK_STATUS) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        start.setClickable(true);
                        start.setBackgroundColor(Color.CYAN);
                    }
                });
            } else {

            }
        }
    }
}
