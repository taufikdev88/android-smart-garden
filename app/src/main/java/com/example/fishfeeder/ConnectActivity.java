package com.example.fishfeeder;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Pattern;

public class ConnectActivity extends AppCompatActivity {
    private String server = "ws://13.67.75.133:8080";

    EditText edtUniqueid;
    EditText edtUniquekey;
    EditText edtServer;
    Button btnConnent;
    CheckBox ccbRemember;
    TextView txtChangeServer;

    RelativeLayout mainLayout;
    AnimationDrawable animationDrawable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        edtUniqueid = findViewById(R.id.edtUniqueid);
        edtUniquekey = findViewById(R.id.edtUniquekey);
        btnConnent = findViewById(R.id.btnConnect);
        ccbRemember = findViewById(R.id.ccbRemember);
        txtChangeServer = findViewById(R.id.txtChangeServer);

        mainLayout = findViewById(R.id.containerConnect);
        animationDrawable = (AnimationDrawable) mainLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(1000);

        btnConnent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtUniqueid.getText().toString().trim().isEmpty()){
                    edtUniqueid.findFocus();
                    Toast.makeText(ConnectActivity.this, "Robot ID Must Be Filled", Toast.LENGTH_LONG).show();
                    return;
                }
                if(edtUniquekey.getText().toString().trim().isEmpty()){
                    edtUniquekey.findFocus();
                    Toast.makeText(ConnectActivity.this, "Robot Key Myst Be Filled", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("config.ini", MODE_PRIVATE));
                    JSONObject jsonObject = new JSONObject();
                    if(ccbRemember.isChecked()){
                        jsonObject.put("uniqueid", edtUniqueid.getText().toString());
                        jsonObject.put("uniquekey", edtUniquekey.getText().toString());
                        jsonObject.put("server", server);
                        outputStreamWriter.write(jsonObject.toString());
                    } else {
                        outputStreamWriter.write("{}");
                    }
                    outputStreamWriter.close();
                }
                catch (JSONException e){
                    Log.d("DEBUG","JSON EXCEPTION");
                    e.printStackTrace();
                }
                catch (IOException e){
                    Log.d("DEBUG", "FILE WRITE FAILED");
                    e.printStackTrace();
                }

                Intent stream = new Intent(ConnectActivity.this, StreamActivity.class);
                stream.putExtra("uniqueid", edtUniqueid.getText().toString());
                stream.putExtra("uniquekey", edtUniquekey.getText().toString());
                stream.putExtra("server", server);
                startActivity(stream);
            }
        });

        txtChangeServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    LayoutInflater inflater = getLayoutInflater();
                    AlertDialog.Builder builder = new AlertDialog.Builder(ConnectActivity.this);
                    builder.setTitle("Server Address");
                    builder.setMessage("Use protocol and port instead ! Example: ws://example.com:8080");

                    @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.dialog_server, null);
                    edtServer = dialogView.findViewById(R.id.edtServer);
                    edtServer.setText(server);

                    builder.setView(dialogView);
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            boolean matches = Pattern.matches("^(wss?://)([0-9]{1,3}(?:\\.[0-9]{1,3}){3}|[^/]+):([0-9]{1,5})$", edtServer.getText().toString().trim());
                            if(matches){
                                server = edtServer.getText().toString().trim();
                                dialog.dismiss();
                            } else {
                                Toast.makeText(ConnectActivity.this, "Invalid server address", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                catch (Exception e){
                    Log.d("DEBUG", "ERROR: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        checkIniFile();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(animationDrawable != null && animationDrawable.isRunning()){
            animationDrawable.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(animationDrawable != null && !animationDrawable.isRunning()){
            animationDrawable.start();
        }
    }

    private void checkIniFile() {
        String config = "";
        try {
            InputStream inputStream = getApplicationContext().openFileInput("config.ini");
            if(inputStream != null){
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null){
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                config = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e){
            Log.d("DEBUG", "File Not Found");
            e.printStackTrace();
        }
        catch (IOException e){
            Log.d("DEBUG", "Cannot Read File");
            e.printStackTrace();
        }

        if(!config.trim().isEmpty()){
            try {
                JSONObject jsonObject = new JSONObject(config.trim());
                edtUniqueid.setText(jsonObject.getString("uniqueid"));
                edtUniquekey.setText(jsonObject.getString("uniquekey"));
                server = jsonObject.getString("server");
                ccbRemember.setChecked(true);
            }
            catch (JSONException e){
                Log.d("DEBUG", "Invalid json text");
                e.printStackTrace();
            }

        }
    }
}
