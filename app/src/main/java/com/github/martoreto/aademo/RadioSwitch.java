package com.github.martoreto.aademo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.core.content.ContextCompat;

public class RadioSwitch extends RadioGroup {

    private float textSize = 0;
    private int bgSelected = 0;
    private int bgUnSelected = 0;
    private int selectedTextColor = 0;
    private int unSelectedTextColor = 0;

    public RadioSwitch(Context context) {
        super(context, null);
    }

    public RadioSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public static float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    private void init(Context context, AttributeSet atrs) {
        TypedArray t = context.obtainStyledAttributes(atrs, R.styleable.SegmentControlView);
        int arrayId = t.getResourceId(R.styleable.SegmentControlView_segmentEntries, R.array.dynamicValue);
        textSize = convertPixelsToDp(t.getDimensionPixelSize(R.styleable.SegmentControlView_segmentTextSize, 0), getContext());
        selectedTextColor = t.getResourceId(R.styleable.SegmentControlView_segmentSelectedTextColor, 0);
        unSelectedTextColor = t.getResourceId(R.styleable.SegmentControlView_segmentUnSelectedTextColor, 0);
        bgSelected = t.getResourceId(R.styleable.SegmentControlView_segmentSelectedBackground, 0);
        bgUnSelected = t.getResourceId(R.styleable.SegmentControlView_segmentUnSelectedBackground, 0);
        t.recycle();
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        setBackground(ContextCompat.getDrawable(getContext(), R.drawable.border_switch));
        String[] arrayOfValue = getResources().getStringArray(arrayId);
        if (arrayOfValue.length > 0) {
            for (String value : arrayOfValue) {
                createSegment(value);
            }
        }
    }

    public void setSwitchBackground(int bgSelected, int bgUnSelected) {
        this.bgSelected = bgSelected;
        this.bgUnSelected = bgUnSelected;
    }

    public void setSwitchTextColor(int selectedTextColor, int unSelectedTextColor) {
        this.selectedTextColor = selectedTextColor;
        this.unSelectedTextColor = unSelectedTextColor;
    }

    @Override
    public void setBackground(Drawable background) {
        super.setBackground(background);
    }

    public void changeSegment(String[] arrayOfValue) {
        if (arrayOfValue.length > 0) {
            for (String value : arrayOfValue) {
                createSegment(value);
            }
        }
    }

    public void createSegment(String name) {
        RadioButton radioButton = (RadioButton) LayoutInflater.from(getContext()).inflate(R.layout.radio_segment_control, (ViewGroup) getRootView(), false);
        radioButton.setTextColor(radioButton.isChecked() ? ContextCompat.getColor(radioButton.getContext(), getSelectedTextColor()) : ContextCompat.getColor(radioButton.getContext(), getUnSelectedTextColor()));
        radioButton.setBackground(radioButton.isChecked() ? ContextCompat.getDrawable(radioButton.getContext(), getBgSelected()) : ContextCompat.getDrawable(radioButton.getContext(), getBgUnSelected()));
        radioButton.setText(name);
        radioButton.setTag(name);
        if (textSize > 0) {
            radioButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }
        radioButton.setOnCheckedChangeListener((compoundButton, b) -> {
            RadioButton rb = (RadioButton) compoundButton;
            rb.setBackground(b ? ContextCompat.getDrawable(compoundButton.getContext(), getBgSelected()) : ContextCompat.getDrawable(compoundButton.getContext(), getBgUnSelected()));
            rb.setTextColor(b ? ContextCompat.getColor(compoundButton.getContext(), getSelectedTextColor()) : ContextCompat.getColor(compoundButton.getContext(), getUnSelectedTextColor()));
        });
        addView(radioButton);
    }

    public void setSelection(String name) {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) getChildAt(i);
                if (radioButton.getText().toString().trim().equalsIgnoreCase(name.trim())) {
                    radioButton.setChecked(true);
                }
            }
        }
    }

    public boolean getSelection(String name) {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) getChildAt(i);
                if (radioButton.getText().toString().trim().equalsIgnoreCase(name.trim())) {
                    return radioButton.isChecked();
                }
            }
        }
        return false;
    }

    public void changeText(String[] arrayOfString) {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) getChildAt(i);
                radioButton.setText(arrayOfString[i]);
            }
        }
    }

    private int getBgSelected() {
        if (bgSelected == 0) {
            return R.drawable.bg_switch_selected;
        } else {
            return bgSelected;
        }
    }

    private int getBgUnSelected() {
        if (bgUnSelected == 0) {
            return R.drawable.bg_switch_un_select;
        } else {
            return bgUnSelected;
        }
    }

    private int getSelectedTextColor() {
        if (selectedTextColor == 0) {
            return R.color.white;
        } else {
            return selectedTextColor;
        }
    }

    private int getUnSelectedTextColor() {
        if (unSelectedTextColor == 0) {
            return R.color.black;
        } else {
            return unSelectedTextColor;
        }
    }
}
