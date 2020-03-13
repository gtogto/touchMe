package com.example.android.bluetoothlegatt_touchMe.com;

import android.view.View;
import android.widget.TextView;

import com.example.android.bluetoothlegatt_touchMe.R;

public class ViewHolder {

    public TextView mTextView1;
    public TextView mTextView2;

    public ViewHolder(View view) {
        mTextView1 = view.findViewById(R.id.textView_node);
        mTextView2 = view.findViewById(R.id.textView_root);
    }

}
