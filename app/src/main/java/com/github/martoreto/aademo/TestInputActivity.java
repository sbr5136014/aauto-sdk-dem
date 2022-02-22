package com.github.martoreto.aademo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.apps.auto.sdk.CarActivity;

public class TestInputActivity extends CarActivity {

    Button addABtn;
    TextView textViewFakeInput;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testinput_activity);

        addABtn = (Button) findViewById(R.id.addABtn);
        textViewFakeInput = (TextView) findViewById(R.id.textViewFakeInput);

        addABtn.setOnClickListener(view -> {
            CharSequence text = textViewFakeInput.getText();
            String s = text.toString();
            s += "A";
            textViewFakeInput.setText(s);
        });

    }
}
