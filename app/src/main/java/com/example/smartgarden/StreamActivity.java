package com.example.smartgarden;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartgarden.adapter.Command;
import com.example.smartgarden.adapter.DataPosisi;
import com.example.smartgarden.customWidget.myImageView;

public class StreamActivity extends AppCompatActivity {
    private myImageView imageView;
    private Command command = new Command();

    private static TextView txtInfo;
    private EditText edtH;
    private EditText edtV;

    public static DataPosisi dataPosisi;

    public static boolean isActive = false;

    public static void updateInfo(String info){
        txtInfo.setText(info);
    }

    @Override
    protected void onStart() {
        isActive = true;
        super.onStart();
    }

    @Override
    protected void onStop() {
        isActive = false;
        super.onStop();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Intent intent = getIntent();

        command.setId(intent.getStringExtra("id"));
        command.setKey(intent.getStringExtra("key"));
        dataPosisi = new DataPosisi();

        setContentView(R.layout.activity_stream);

        txtInfo = findViewById(R.id.streamTxtInformation);
        Button btnUp = findViewById(R.id.streamBtnUp);
        Button btnDown = findViewById(R.id.streamBtnDown);
        Button btnRight = findViewById(R.id.streamBtnRight);
        Button btnLeft = findViewById(R.id.streamBtnLeft);
        Button btnGo = findViewById(R.id.streamBtnGo);
        edtH = findViewById(R.id.streamEdtH);
        edtV = findViewById(R.id.streamEdtV);

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtH.getText().toString().isEmpty()) edtH.setText("0");
                if(edtV.getText().toString().isEmpty()) edtV.setText("0");

                command.setCommand(dataPosisi.go(Integer.valueOf(edtH.getText().toString()), Integer.valueOf(edtV.getText().toString())));
                new Thread(new ConnectActivity.SendThread(command.toString())).start();
            }
        });
        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                command.setCommand(dataPosisi.up());
                new Thread(new ConnectActivity.SendThread(command.toString())).start();
            }
        });
        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                command.setCommand(dataPosisi.down());
                new Thread(new ConnectActivity.SendThread(command.toString())).start();
            }
        });
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                command.setCommand(dataPosisi.right());
                new Thread(new ConnectActivity.SendThread(command.toString())).start();
            }
        });
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                command.setCommand(dataPosisi.left());
                new Thread(new ConnectActivity.SendThread(command.toString())).start();
            }
        });

        imageView = findViewById(R.id.streamCanvas);
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int[] data = imageView.getPosition(event.getX(), event.getY());
                Log.d("DEBUG", data[0] + " ," + data[1]);

                imageView.posRefX = data[0];
                imageView.posRefY = data[1];
                imageView.invalidate();

                command.setCommand(dataPosisi.goByGrid(data[0], data[1], imageView.getSize()[0], imageView.getSize()[1]));
                new Thread(new ConnectActivity.SendThread(command.toString())).start();
                return false;
            }
        });

        command.setCommand(dataPosisi.go(0,0));
        new Thread(new ConnectActivity.SendThread(command.toString())).start();
    }
}
