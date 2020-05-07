package com.reducetechnologies.reduction.home_screen.ui.encyclopedia.main;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.Toast;

public class WebViewClickListener implements View.OnTouchListener {
    private WebView wv;
    private ViewGroup vg;
    private int position;

    public WebViewClickListener(WebView wv, ViewGroup vg, int position) {
        this.wv = wv;
        this.vg = vg;
        this.position = position;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_CANCEL:
                return true;
            case MotionEvent.ACTION_UP:
                sendClick();
                return true;
        }
        return false;
    }

    public void sendClick() {

//You can send item click event to listview onItemClick listener
        ListView lv = (ListView) vg;
        lv.performItemClick(wv, position, 0);

//You can do your action over here also

        Toast.makeText(wv.getContext(), "click made", Toast.LENGTH_LONG).show();
    }
}

