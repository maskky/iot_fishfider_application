package fishfinder.itkmitl.fishfinder;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import fishfinder.itkmitl.fishfinder.dialog.CustomLoading;
import fishfinder.itkmitl.fishfinder.model.Position;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StatusFragment extends Fragment {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("");
    private static String KEY = "1364d69e041a6537cb584debbcd644f4";
    private String weather = "";
    private TextView mapButton;
    private String result = "";
    private CustomLoading customLoading;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        customLoading = new CustomLoading(getContext());
        customLoading.showDialog();

        mapButton = getView().findViewById(R.id.status_findlocation);
        final TextView statusConnect = getView().findViewById(R.id.status_status);
        final TextView statusForecastWeather = getView().findViewById(R.id.status_forecast_weather);
        final TextView statusDesc = getView().findViewById(R.id.status_description);
        final TextView statusForecast = getView().findViewById(R.id.status_forecast);
        final TextView temp = getView().findViewById(R.id.status_temp);
        final TextView weatherStatus = getView().findViewById(R.id.status_weather);


        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_view, new MapFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Position position = dataSnapshot.child("position").getValue(Position.class);
                String lat = String.valueOf(position.getLat());
                String lng = String.valueOf(position.getLng());

                final int tempResult = dataSnapshot.child("temperature/temp").getValue(Integer.class);
                temp.setText(tempResult + " °C");
                final String url= "https://samples.openweathermap.org/data/2.5/weather?lat=" + lat +"&lon=" + lng + "&appid=" + KEY;
                Log.i("FB", "Lat : " + position.getLat() + " / " + "Lng : " + position.getLng());

                AsyncTask asyncTask = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url(url)
                                .build();
                        try {
                            Response response = client.newCall(request).execute();
                            JSONObject responseObject = new JSONObject(response.body().string());
                            JSONArray resultsArray = responseObject.getJSONArray("weather");
                            JSONObject jsonObject1 = resultsArray.getJSONObject(0);
                            int weatherStatus = Integer.parseInt(jsonObject1.optString("id"));
                            weather = checkWeatherStatus(weatherStatus);
                            result = forecastFishing(tempResult, weatherStatus);

                            Log.i("api", weather + "");
                            Log.i("api", resultsArray + "");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        weatherStatus.setText(weather);
                        String[] arrayResult = result.split(",");
                        Log.i("SS", arrayResult[1]);
                        statusForecast.setText(arrayResult[0]);
                        statusDesc.setText(arrayResult[1]);
                        customLoading.dismissDialog();
                        statusForecastWeather.setText("มีฝนฟ้าคะนอง, อุณหภูมิสูงสุด 34-37 องศาเซลเซียส,       ลมตะวันตกเฉียงใต้ ความเร็ว 10-15 กม./ชม.");
                    }


                }.execute();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_status, container, false);
    }

    String checkWeatherStatus(int weatherCode){
        String result = "";
        if (weatherCode >= 200 && weatherCode <= 232){
           result = "ฝนฟ้าคะนอง";
        }else if (weatherCode >= 300 && weatherCode <= 321){
            result = "ฝนตกปรอยๆ";
        }else if (weatherCode >= 500 && weatherCode <= 531){
            result = "ฝนตกหนัก";
        }else if (weatherCode >= 600 && weatherCode <= 622){
            result = "หิมะตก";
        }else if (weatherCode >= 701 && weatherCode <= 781){
            result = "มีหมอก";
        }else if (weatherCode >= 801 && weatherCode <= 804){
            result = "มีเมฆมาก";
        }else{
            result = "อากาศแจ่มใส";
        }
        return result;
    }

    String forecastFishing(int temperature, int weatherCode){
        String result = "";
        if (temperature > 1){
            if (weatherCode == 800 || weatherCode == 801){
                result = "มีโอกาสตกปลาได้น้อย ,เนื่องจากอุณหภูมิสูงและแดดแรง ทำให้ปลาส่วนใหญ่จะหลบไปอาศัยอยู่ในน้ำลึก";
            }else{
                result = "มีโอกาสตกปลาได้น้อย ,เนื่องจากอุณหภูมิสูงและแดดแรง ทำให้ปลาส่วนใหญ่จะหลบไปอาศัยอยู่ในน้ำลึก แต่มีแนวโน้มว่าอุณหภูมิของน้ำจะลดลงหลังจากนั้นปลาจะขึ้นมาที่ผิวน้ำ ทำให้มีโอกาสตกปลาได้สูง";
            }
        }else{
            result = "มีโอกาสตกปลาได้สูง ,เนื่องจากปลาจะขึ้นมาที่ผิวน้ำเนื่องจากอุณหภูมิต่ำ และต้องการออกซิเจนที่ผิวน้ำ";
        }
        return result;
    }

}
