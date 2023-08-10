package algonquin.cst2335.pate0323;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;

import algonquin.cst2335.pate0323.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    Button btn = null;
    protected RequestQueue queue = null;
    protected String cityName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        btn = binding.loginButton;
        queue = Volley.newRequestQueue(this);

        btn.setOnClickListener(vw -> {
            cityName = binding.cityField.getText().toString();
            String stringURL = "https://api.openweathermap.org/data/2.5/weather?q="
                    + URLEncoder.encode(cityName)
                    + "&appid=4c6f889d1262aa52817df7b6de2ce1b9&units=metric";

            @SuppressLint({"DefaultLocale", "StringFormatMatches"})
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, stringURL, null,
                    (response) -> {
                        try {
                            JSONObject mainObject = response.getJSONObject("main");

                            JSONArray weatherArray = response.getJSONArray("weather");
                            JSONObject position = weatherArray.getJSONObject(0);

                            String description = position.getString("description");
                            String iconName = position.getString("icon");

                            double current = mainObject.getDouble("temp");
                            double min = mainObject.getDouble("temp_min");
                            double max = mainObject.getDouble("temp_max");
                            int humidity = mainObject.getInt("humidity");
                            String imageUrl = "https://openweathermap.org/img/w/" + iconName + ".png";

                            runOnUiThread(() -> {

                                binding.temp.setText(getString(R.string.temp, current));
                                binding.temp.setVisibility(View.VISIBLE);

                                binding.min.setText(getString(R.string.min, min));
                                binding.min.setVisibility(View.VISIBLE);

                                binding.max.setText(getString(R.string.max, max));
                                binding.max.setVisibility(View.VISIBLE);

                                binding.humidity.setText(getString(R.string.humidity, humidity));
                                binding.humidity.setVisibility(View.VISIBLE);

                                binding.description.setText(description);
                                binding.description.setVisibility(View.VISIBLE);

                                ImageRequest imgReq = new ImageRequest(imageUrl, bitmap -> {
                                    binding.weatherIcon.setImageBitmap(bitmap);
                                    binding.weatherIcon.setVisibility(View.VISIBLE);
                                    FileOutputStream fOut;
                                    try {
                                        int i = 0;
                                        fOut = openFileOutput(iconName + ".png", Context.MODE_PRIVATE);
                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                                        fOut.flush();
                                        fOut.close();
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();

                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }, 1024, 1024, ImageView.ScaleType.CENTER, null,
                                        (error) -> {
                                            int j = 0;
                                        });

                                queue.add(imgReq);

                            });
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    },
                    (error) -> {
                        int i = 0;
                    });
            queue.add(request);
        });
    }

}