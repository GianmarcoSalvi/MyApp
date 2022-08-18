package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.myapplication.model.User;
import com.example.myapplication.utility.ConnectionReceiver;
import com.example.myapplication.utility.NetworkUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private EditText usernameField, emailField;
    private CheckBox rememberMe;
    private Retrofit retrofit;
    private User user;
    private BroadcastReceiver connectionReceiver;
    private boolean connected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Handling connection status
        connectionReceiver = new ConnectionReceiver();
        broadcastIntent();

        // Linking XML items to Java Objects
        rememberMe = (CheckBox) findViewById(R.id.remember_me);
        usernameField = (EditText) findViewById(R.id.user);
        emailField = (EditText) findViewById(R.id.email);

        // Handling shared preferences (remember me)

        SharedPreferences preferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        usernameField.setText(preferences.getString("username", ""));
        emailField.setText(preferences.getString("email", ""));
        rememberMe.setChecked(preferences.getBoolean("remember", false));


        // Creating retrofit instance
        retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

    }

    public void broadcastIntent() {
        registerReceiver(connectionReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public void signIn(View view) {
        Context context = view.getContext();
        connected = checkConnection(context);
        if(!connected){
            showAlert(context, "CONNECTION ERROR", "Please verify your connection and retry");
            return;
        }

        String username = usernameField.getText().toString();
        String email = emailField.getText().toString();

        ServiceAPI service = retrofit.create(ServiceAPI.class);

        rememberMe();

        Map<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("email", email);
        Call<List<User>> userResponseCall = service.getUser(params);

        userResponseCall.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    List<User> userResponse = response.body();

                    if (!userResponse.isEmpty()) {
                        user = (User) userResponse.get(0);

                        // new activity
                        Intent intent = new Intent(view.getContext(), ProgenitoresActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("user_id", user.getId());
                        startActivity(intent);

                    } else {
                        showAlert(context, "LOGIN ERROR", "Please verify your data and retry");
                    }
                } else {
                    Log.e("Error", "onResponse" + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
            }
        });

    }

    public void rememberMe() {
        if (rememberMe.isChecked()) {
            SharedPreferences preferences = getSharedPreferences("data", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("username", usernameField.getText().toString());
            editor.putString("email", emailField.getText().toString());
            editor.putBoolean("remember", rememberMe.isChecked());
            editor.commit();
        } else {
            deleteSharedPreferences("data");
        }
    }

    public static boolean checkConnection(Context context){
        String status = NetworkUtil.getConnectivityStatusString(context);
        if(status.equals("No connection available")) return false;
        else return true;
    }

    public static void showAlert(Context context, String title, String msg){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .show();
    }

}