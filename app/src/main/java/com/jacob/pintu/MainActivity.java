package com.jacob.pintu;

import android.app.AlertDialog;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends FragmentActivity {

    private PinTuLayout mPintuLayout;
    private TextView mTextViewLevel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextViewLevel = (TextView) findViewById(R.id.textView_level);

        mPintuLayout = (PinTuLayout) findViewById(R.id.pintuLayout);
        mPintuLayout.setOnGameListener(new PinTuLayout.OnGameListener() {
            @Override
            public void gameStart(int level) {
                mTextViewLevel.setText("第 " + level + " 关");
            }

            @Override
            public void gameSuccess(int level) {
                Toast.makeText(MainActivity.this,"第"+level+" 关成功！",Toast.LENGTH_SHORT).show();
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setTitle("拼图游戏").setMessage("闯关成功").create();

            }
        });
    }
}
