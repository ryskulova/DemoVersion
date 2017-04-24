package com.example.mac.yandexdemo;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

import static com.example.mac.yandexdemo.R.layout.activity_main;
import static com.example.mac.yandexdemo.R.layout.view_list_of_words;

public class ListContent extends AppCompatActivity {

    DatabaseHelper myDB;
    private ImageButton reset2;
    private ImageButton fav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_list_of_words);
        reset2 = (ImageButton) findViewById(R.id.reset1);

        ListView listView = (ListView) findViewById(R.id.listview);
        myDB = new DatabaseHelper(this);
        fav = (ImageButton) findViewById(R.id.favorites);
        final GestureDetector  gestureDetector = new GestureDetector(new MyGestureDetector());
        findViewById(R.id.listview).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) return false;
                return false;
            }
        });

        // заполнить массив ArrayList <String> из базы данных, а затем просмотреть его
        ArrayList<String> theList = new ArrayList<>();
        theList.isEmpty();
        Cursor data = myDB.getListContents();
        if (data.getCount() == 0) {
            Toast.makeText(this, "There are no contents in this list!", Toast.LENGTH_LONG).show();
        } else {
            while (data.moveToNext()) {
                theList.add(data.getString(1));
                ArrayAdapter listAdapter = new ArrayAdapter<>(this, R.layout.row, R.id.rowItem, theList);
                listView.setAdapter(listAdapter);
            }
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.history:
                Intent historyIntent = new Intent(this, ListContent.class);
                startActivity(historyIntent);
                break;
            //переход пока неполный из-за багов в Sql
            case R.id.favorites:
                Intent favoritesIntent = new Intent(this, FavoriteList.class);
                startActivity(favoritesIntent);
                break;
            case R.id.delete:
               clearData();
        }
        return super.onOptionsItemSelected(item);
    }
   //метод чистки ListView
    private void clearData() {
            ArrayList<String> theList = new ArrayList<>();
            ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, theList);
            // clear the data
            theList.clear();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)  {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.history_and_chosen_action_bar, menu);
        return true;
    }

    //немного анимации для перехода жестами между Activities/точнее со второго activity переход на первый
    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            try {
                float slope = (e1.getY() - e2.getY()) / (e1.getX() - e2.getX());
                float angle = (float) Math.atan(slope);
                float angleInDegree = (float) Math.toDegrees(angle);
                // left to right
                if (e1.getX() - e2.getX() > 20 && Math.abs(velocityX) > 20) {
                    if ((angleInDegree < 45 && angleInDegree > -45)) {
                        startActivity(new Intent(ListContent.this, MainActivity.class));
                        ListContent.this.overridePendingTransition(
                                R.animator.slide_in_left, R.animator.slide_out_right);
                        finish();
                    }
                    // right to left fling
                } else if (e2.getX() - e1.getX() > 20
                        && Math.abs(velocityX) > 20) {
                    if ((angleInDegree < 45 && angleInDegree > -45)) {
                        startActivity(new Intent(ListContent.this, MainActivity.class));
                        ListContent.this.overridePendingTransition(
                                R.animator.slide_in_right, R.animator.slide_out_left);
                        finish();

                    }
                }
                return true;
            } catch (Exception e) {
                // nothing
            }
            return false;
        }
    }

}


