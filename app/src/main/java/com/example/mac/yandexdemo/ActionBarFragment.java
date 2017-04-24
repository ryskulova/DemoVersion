package com.example.mac.yandexdemo;

import android.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;

/**
 * Created by mac on 21.04.17.
 */

public class ActionBarFragment extends Fragment {

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.android_action_bar_spinner_menu, menu);
    }
}
