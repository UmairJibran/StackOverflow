package com.example.stackoverflow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RequestQueue mQueue;
    Button btnFetch;
    EditText questionTag,score;
    ListView listView;
    ArrayList<String> arrayList;
    ArrayAdapter<String> stringArrayAdapter;
    ArrayAdapter<String> urlAdapter;
    ArrayList<String> urls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        questionTag = findViewById(R.id.questionTag);
        score = findViewById(R.id.questionScore);

        arrayList = new ArrayList<>();

        btnFetch = findViewById(R.id.fetchAPI);
        listView = findViewById(R.id.listView);

        mQueue = Volley.newRequestQueue(this);

        btnFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int questionScore = 0;
                String tag = "android";
                try{
                    questionScore = Integer.parseInt(score.getText().toString(),10);
                    tag = questionTag.getText().toString();
                }catch (Exception e){
                    e.printStackTrace();
                }
                jsonParse((Math.max(questionScore, 5)),tag);
                stringArrayAdapter = new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1,arrayList);
                urlAdapter = new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1,urls);
                listView.setAdapter(stringArrayAdapter);
            }
        });

        try{
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String url = urlAdapter.getItem(position);
                    Intent intent = new Intent(MainActivity.this,MainActivity2.class);
                    intent.putExtra("url",url);
                    startActivity(intent);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void jsonParse(final int score,String tag) {
        Toast.makeText(this, "Showing Results for " + tag + " with minimum score of " + score, Toast.LENGTH_SHORT).show();
        final String qTag = tag;
        String url = "https://api.stackexchange.com/2.2/questions?site=stackoverflow";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("items");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject item = jsonArray.getJSONObject(i);
                                String title = item.getString("title");
                                int questionScore = item.getInt("score");
                                String tags = item.getString("tags");
                                String url = item.getString("link");
                                    if(tags.matches(".*"+qTag+".*") && questionScore >= score){
                                        arrayList.add(title);
                                        urls.add(url);
                                    }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }
}