package com.example.fishfeeder;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.TextViewCompat;

import com.example.fishfeeder.adapter.WsCommand;
import com.example.fishfeeder.adapter.WsRegister;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketState;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class StreamActivity extends AppCompatActivity {
    WebSocket webSocket;
    String server = "ws://13.67.75.133:8080";
    private String wtState = "false";

    WsRegister wsRegister = new WsRegister("unik", "pass");
    WsCommand wsCommand = new WsCommand("F");

    ImageButton btnLayoutPh, btnLayoutFeed, btnLayoutLamp;
    Switch swLamp;
    Button btnFeed;
    TextView txtPh;
    TextView txtState;
    FloatingActionButton fabBack;
    ConstraintLayout layoutMenu, layoutPh, layoutLamp, layoutfeed;

    private void createWebSocketClient(){
        URI uri;
        try {
            uri = new URI(server);
        }
        catch (URISyntaxException e){
            e.printStackTrace();
            return;
        }
        try {
            webSocket = new WebSocketFactory()
                    .setConnectionTimeout(5000)
                    .createSocket(uri);
        }
        catch (IOException e){
            Log.d("DEBUG", "IO Exception: " + e.getMessage());
        }
        catch (Exception e){
            Log.d("DEBUG", "ERROR: " + e.getMessage());
        }

        webSocket.addListener(new WebSocketAdapter(){
            @Override
            public void onStateChanged(WebSocket websocket, WebSocketState newState) {
                Log.d("DEBUG","newState Change: "+newState.name());
                txtState.setText(String.format("Connection State: %s", newState.name()));
            }

            @Override
            public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
                Log.d("DEBUG", "CONNECTED");
                Log.d("DEBUG", "Trying register: " + wsRegister.toString());
                websocket.sendText(wsRegister.toString());
            }

            @Override
            public void onConnectError(WebSocket websocket, WebSocketException exception) {
                Log.d("DEBUG", "ERROR CONNECTING");
                Toast.makeText(StreamActivity.this, "Error while connecting to server", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onTextMessage(WebSocket websocket, String text) {
                try {
                    JSONObject jsonObject = new JSONObject(text);
                    if(!wtState.equals(jsonObject.getString("lamp"))){
                        wtState = jsonObject.getString("lamp");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(wtState.equals("true")){
                                    swLamp.setChecked(true);
                                } else {
                                    swLamp.setChecked(false);
                                }
                            }
                        });
                    }
                    final String ph = jsonObject.getString("ph");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtPh.setText(ph);
                        }
                    });
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onUnexpectedError(WebSocket websocket, WebSocketException cause) {
                Toast.makeText(StreamActivity.this, "Unexpected Error: " + cause.getMessage(), Toast.LENGTH_LONG).show();
                backToConnectPage();
            }
        });

        try {
            webSocket.connectAsynchronously();
        }
        catch (Exception e){
            Log.d("DEBUG","ERROR CONNECT2: " + e.getMessage());
        }
    }

    private void backToConnectPage(){
        Intent connect = new Intent(StreamActivity.this, ConnectActivity.class);
        startActivity(connect);
        finish();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_wrapper);

        txtPh = findViewById(R.id.txtPh);
        TextViewCompat.setAutoSizeTextTypeWithDefaults(txtPh, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        txtState = findViewById(R.id.txtState);
        swLamp = findViewById(R.id.swLamp);
        btnFeed = findViewById(R.id.btnFeed);
        fabBack = findViewById(R.id.fabBack);
        layoutMenu = findViewById(R.id.layout_home);
        layoutPh = findViewById(R.id.layout_ph);
        layoutfeed = findViewById(R.id.layout_feed);
        layoutLamp = findViewById(R.id.layout_lamp);
        btnLayoutFeed = findViewById(R.id.btnOpenFeed);
        btnLayoutLamp = findViewById(R.id.btnOpenLamp);
        btnLayoutPh = findViewById(R.id.btnOpenPH);

        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layoutMenu.getVisibility() == View.GONE){
                    layoutMenu.setVisibility(View.VISIBLE);
                    layoutfeed.setVisibility(View.GONE);
                    layoutLamp.setVisibility(View.GONE);
                    layoutPh.setVisibility(View.GONE);
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(StreamActivity.this);
                    builder.setMessage("Do yout want to end session ?")
                            .setTitle("Confirm Exit");
                    builder.setPositiveButton("Yes, I do", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            StreamActivity.super.onBackPressed();
                            finish();
                        }
                    });
                    builder.setNegativeButton("No, I Don't", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        btnLayoutPh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutMenu.setVisibility(View.GONE);
                layoutfeed.setVisibility(View.GONE);
                layoutLamp.setVisibility(View.GONE);
                layoutPh.setVisibility(View.VISIBLE);
            }
        });

        btnLayoutLamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutMenu.setVisibility(View.GONE);
                layoutfeed.setVisibility(View.GONE);
                layoutLamp.setVisibility(View.VISIBLE);
                layoutPh.setVisibility(View.GONE);
            }
        });

        btnLayoutFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutMenu.setVisibility(View.GONE);
                layoutfeed.setVisibility(View.VISIBLE);
                layoutLamp.setVisibility(View.GONE);
                layoutPh.setVisibility(View.GONE);
            }
        });

        swLamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wsCommand.setCommand("TOGGLELAMP");
                webSocket.sendText(wsCommand.toString());
            }
        });

        btnFeed.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    btnFeed.setTranslationX((float) 3.0);
                    btnFeed.setTranslationY((float) 3.0);
                    wsCommand.setCommand("FEED");
                    webSocket.sendText(wsCommand.toString());
                    return true;
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    btnFeed.setTranslationX((float) -3.0);
                    btnFeed.setTranslationY((float) -3.0);
                }
                return false;
            }
        });

        Intent intent = getIntent();
        String uniqueid = intent.getStringExtra("uniqueid");
        String uniquekey = intent.getStringExtra("uniquekey");
        server = intent.getStringExtra("server");
        wsRegister.setUniqueid(uniqueid);
        wsRegister.setUniquekey(uniquekey);

        createWebSocketClient();
    }

    @Override
    protected void onDestroy() {
        wsCommand.setCommand(getString(R.string.command_stop));
        webSocket.sendText(wsCommand.toString());
        if(webSocket.isOpen()) webSocket.disconnect();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(StreamActivity.this);
        builder.setMessage("Do yout want to end session ?")
                .setTitle("Confirm Exit");
        builder.setPositiveButton("Yes, I do", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                StreamActivity.super.onBackPressed();
                finish();
            }
        });
        builder.setNegativeButton("No, I Don't", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
