package com.example.smartgarden;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartgarden.adapter.Command;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ConnectActivity extends AppCompatActivity {
    private Socket socket;
    private Command command = new Command();

    EditText edtId;
    EditText edtKey;
    EditText edtServer;

    Button btnConnect;
    CheckBox ccbRemember;

    Thread receiveThread;

    @Override
    protected void onDestroy() {
        if(socket.isConnected()){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(receiveThread.isAlive()){
            receiveThread.interrupt();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        edtServer = findViewById(R.id.edtServer);
        edtId = findViewById(R.id.edtUniqueid);
        edtKey = findViewById(R.id.edtUniquekey);
        btnConnect = findViewById(R.id.btnConnect);
        ccbRemember = findViewById(R.id.ccbRemember);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(edtId.getText().toString().trim().isEmpty()){
                edtId.findFocus();
                Toast.makeText(ConnectActivity.this, "Id Must Be Filled", Toast.LENGTH_LONG).show();
                return;
            }
            if(edtKey.getText().toString().trim().isEmpty()){
                edtKey.findFocus();
                Toast.makeText(ConnectActivity.this, "Key Myst Be Filled", Toast.LENGTH_LONG).show();
                return;
            }

            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("config.ini", MODE_PRIVATE));
                JSONObject jsonObject = new JSONObject();
                if(ccbRemember.isChecked()){
                    jsonObject.put("id", edtId.getText().toString());
                    jsonObject.put("key", edtKey.getText().toString());
                    jsonObject.put("server", edtServer.getText().toString());
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

            command.setCommand("register");
            command.setId(edtId.getText().toString().trim());
            command.setKey(edtKey.getText().toString().trim());

            new Thread(new ConnectThread(edtServer.getText().toString().trim())).start();
            }
        });
        checkIniFile();
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
                edtId.setText(jsonObject.getString("id"));
                edtKey.setText(jsonObject.getString("key"));
                edtServer.setText(jsonObject.getString("server"));
                ccbRemember.setChecked(true);
            }
            catch (JSONException e){
                Log.d("DEBUG", "Invalid json text");
                e.printStackTrace();
            }
        }
    }

    private static PrintWriter output;
    private BufferedReader input;
    public class ConnectThread implements Runnable {
        private String server;

        public ConnectThread(String server){
            this.server = server;
        }

        @Override
        public void run() {
            try {
                InetAddress inetAddress = InetAddress.getByName(server);
                socket = new Socket(inetAddress, 5000);
                output = new PrintWriter(socket.getOutputStream());
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ConnectActivity.this, "Connected", Toast.LENGTH_LONG).show();
                    }
                });

                receiveThread = new Thread(new ReceiveThread());
                receiveThread.start();
                new Thread(new SendThread(command.toString())).start();
            } catch (final UnknownHostException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ConnectActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } catch (final SocketException e){
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ConnectActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                if(receiveThread != null){
                    receiveThread.interrupt();
                }
            } catch (final IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ConnectActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    public class ReceiveThread implements Runnable {
        @Override
        public void run() {
            while(true){
                if(receiveThread.isInterrupted()){
                    break;
                }
                try {
                    final String message = input.readLine();
                    if(message != null){
                        Log.d("DEBUG", "Got message: " + message);
                        final JSONObject jsonObject = new JSONObject(message);
                        if(jsonObject.has("X_POSITION")){
//                            StreamActivity.dataPosisi.update(jsonObject.getInt("X_POSITION_CM"), jsonObject.getInt("Y_POSITION_CM"));
//                            StreamActivity.updateInfo(StreamActivity.dataPosisi.toString());
                        }
                        if(jsonObject.has("error")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Toast.makeText(ConnectActivity.this, jsonObject.getString("error"),Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else if(jsonObject.has("success")){
                            if(!StreamActivity.isActive){
                                Log.d("DEBUG", "Opening stream activity");
                                Intent intent = new Intent(ConnectActivity.this, StreamActivity.class);
                                intent.putExtra("server", edtServer.getText().toString().trim());
                                intent.putExtra("id", edtId.getText().toString().trim());
                                intent.putExtra("key", edtKey.getText().toString().trim());
                                startActivity(intent);
                            }
                        }
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static class SendThread implements Runnable {
        private String message;
        public SendThread(String message) {
            this.message = message;
        }
        @Override
        public void run() {
            output.write(message);
            output.flush();
        }
    }
}