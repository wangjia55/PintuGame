package com.jacob.pintu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends FragmentActivity {

    private PintuLayout mPintuLayout;
    private TextView mTextViewLevel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextViewLevel = (TextView) findViewById(R.id.textView_level);

        mPintuLayout = (PintuLayout) findViewById(R.id.pintuLayout);
        mPintuLayout.setOnGameListener(new PintuLayout.OnGameListener() {
            @Override
            public void gameStart(int level) {
                mTextViewLevel.setText("第 " + level + " 关");
            }

            @Override
            public void gameSuccess(int level) {
                Toast.makeText(MainActivity.this,"第"+level+" 关成功！",Toast.LENGTH_SHORT).show();
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("拼图游戏").setMessage("闯关成功")
                        .setPositiveButton("下一关",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mPintuLayout.nextLevel();
                            }
                        }).create();
                dialog.show();
            }
        });
    }
}
