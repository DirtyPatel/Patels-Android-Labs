package algonquin.cst2335.pate0323;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.w( "MainActivity", "In onCreate() - Loading Widgets" );
        SharedPreferences prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        prefs.getString("VariableName", "");
        String emailAddress = prefs.getString("LoginName", "");


        Button loginButton = findViewById(R.id.loginButton);
        EditText emailEditText = findViewById(R.id.emailEditText);
        Intent nextPage = new Intent( MainActivity.this, SecondActivity.class);
        loginButton.setOnClickListener( clk-> {
            Intent: nextPage.putExtra( "EmailAddress", emailEditText.getText().toString() );
            startActivity(nextPage);
            String email = emailEditText.getText().toString();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("email", email);
            editor.apply();
        } );
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.w( "MainActivity", "In onStart() - The application is now visible on screen." );
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w( "MainActivity", "In onResume() - The application is now responding to user input" );
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w( "MainActivity", "In onPause() - The application no longer responds to user input" );
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w( "MainActivity", "In onStop() - The application is no longer visible." );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w( "MainActivity", "In onDestroy() - Any memory used by the application is freed." );
    }
}