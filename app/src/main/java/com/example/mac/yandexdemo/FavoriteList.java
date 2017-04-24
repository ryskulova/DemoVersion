package com.example.mac.yandexdemo;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

/**
 * Created by mac on 21.04.17.
 */

public class FavoriteList extends AppCompatActivity {
     ListView listView;
  //реализация класса не полная
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites);
        listView = (ListView)findViewById(R.id.listview2);
        Intent intent = getIntent();


    }

}



