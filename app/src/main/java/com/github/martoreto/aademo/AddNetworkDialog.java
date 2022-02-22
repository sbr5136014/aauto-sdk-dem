package com.github.martoreto.aademo;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputLayout;

public class AddNetworkDialog {

    private final Context context;
    private final OnClick onClick;
    private EditText etSSID, etPassword;
    private TextView spSecurity, tvNone, tvWep, tvWpa;
    private PopupWindow popUp;
    private TextView tvSave, tvCancel;
    private LinearLayout llSecurity;
    private TextInputLayout tilPassword;

    public AddNetworkDialog(Context context, OnClick onClick) {
        this.context = context;
        this.onClick = onClick;
    }

    public void showDialog(View rootView) {
        if (rootView != null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View mView = inflater.inflate(R.layout.dialog_add_network, null, true);
            popUp = new PopupWindow(mView, MATCH_PARENT, rootView.getHeight(), false);
            popUp.setOutsideTouchable(false);
            popUp.setElevation(20);

            ConstraintLayout clRoot = mView.findViewById(R.id.clRoot);
            tilPassword = mView.findViewById(R.id.tilPassword);
            tvCancel = mView.findViewById(R.id.tvCancel);
            tvSave = mView.findViewById(R.id.tvSave);
            etSSID = mView.findViewById(R.id.etSSID);
            etPassword = mView.findViewById(R.id.etPassword);
            spSecurity = mView.findViewById(R.id.spSecurity);
            tvNone = mView.findViewById(R.id.tvNone);
            tvWep = mView.findViewById(R.id.tvWep);
            tvWpa = mView.findViewById(R.id.tvWpa);
            llSecurity = mView.findViewById(R.id.llSecurity);

            spSecurity.setOnClickListener(view -> {
                if (llSecurity.getVisibility() == View.VISIBLE) {
                    llSecurity.setVisibility(View.GONE);
                } else {
                    llSecurity.setVisibility(View.VISIBLE);
                }
            });
            tvNone.setOnClickListener(view -> {
                spSecurity.setText(tvNone.getText());
                llSecurity.setVisibility(View.GONE);
                tilPassword.setVisibility(View.GONE);
            });
            tvWep.setOnClickListener(view -> {
                spSecurity.setText(tvWep.getText());
                llSecurity.setVisibility(View.GONE);
                tilPassword.setVisibility(View.VISIBLE);
            });
            tvWpa.setOnClickListener(view -> {
                spSecurity.setText(tvWpa.getText());
                llSecurity.setVisibility(View.GONE);
                tilPassword.setVisibility(View.VISIBLE);
            });

            tvSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validate()) {
                        String ssid = etSSID.getText().toString();
                        WifiListModel wifiListModel;
                        if (spSecurity.getText().equals(context.getResources().getString(R.string.none))) {
                            wifiListModel = new WifiListModel(-1, ssid, false, false, "NONE", 1);
                        } else if (spSecurity.getText().equals(context.getResources().getString(R.string.wep))) {
                            wifiListModel = new WifiListModel(-1, ssid, true, false, "WEP", 1);
                        } else {
                            wifiListModel = new WifiListModel(-1, ssid, true, false, "WPA", 1);
                        }
                        onClick.onAddNetwork(wifiListModel, etPassword.getText().toString());
                        dismiss();
                    }
                }
            });

            tvCancel.setOnClickListener(v -> dismiss());

           /* popUp.setOnDismissListener(() -> {
                BaseActivity.setCurrentViewForPopUp(null);
            });*/

            int topSpace = (int) Utilities.convertDpToPixel(15, context); //Top Header space
            popUp.showAtLocation(rootView, Gravity.TOP, 0, topSpace);
        }
    }

    private boolean validate() {
        if (etSSID.getText().toString().isEmpty()) {
            setShakeAnimation(etSSID);
            return false;
        } else if (!spSecurity.getText().equals(context.getResources().getString(R.string.none)) && etPassword.getText().toString().isEmpty()) {
            setShakeAnimation(tilPassword);
            return false;
        }
        return true;
    }

    public boolean isShowing() {
        return popUp != null && popUp.isShowing();
    }

    public void dismiss() {
        if (isShowing()) popUp.dismiss();
    }

    public interface OnClick {
        void onAddNetwork(WifiListModel wifiListModel, String password);
    }

    public void setShakeAnimation(View view) {
        if (view != null) {
            Animation shake = AnimationUtils.loadAnimation(context.getApplicationContext(), R.anim.anim_shake);
            view.startAnimation(shake);
        }
    }
}
