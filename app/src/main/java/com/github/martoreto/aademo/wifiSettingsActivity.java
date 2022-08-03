


package com.github.martoreto.aademo;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.inputmethodservice.KeyboardView;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.apps.auto.sdk.CarActivity;
import com.google.android.apps.auto.sdk.CarToast;
import com.google.android.apps.auto.sdk.CarUiController;
import com.google.android.apps.auto.sdk.MenuController;
import com.google.android.apps.auto.sdk.MenuItem;
import com.google.android.apps.auto.sdk.StatusBarController;
import com.google.android.apps.auto.sdk.notification.CarNotificationExtender;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class wifiSettingsActivity extends CarActivity implements WifiAndBatteryInfo, WifiListAdapter.ClickInterface, View.OnClickListener {
    View inPassLay;
    //     private final AddNetworkDialog.OnClick onClick;
    private EditText etSSID, etPassword;
    private TextView spSecurity, tvNone, tvWep, tvWpa;
    private PopupWindow popUp;
    private TextView tvSave, tvCancel;
    private LinearLayout llSecurity;
    private TextInputLayout tilPassword;
    private RecyclerView rvWifi;
    private WifiListAdapter wifiListAdapter;
    private ArrayList<WifiListModel> wifiList;
    private TextView no_item_found;
    private RadioSwitch rsWifiOnOff;
    private ImageView imvRefresh;
    private RotateAnimation rotate;
    public WifiManager mWifiManager;
    private boolean isWifiEnabled;
    public boolean isScanning = false;
    TextView wp1;
    Button a,b ,c,d,e,f,i,j,k,l,l1,m,n,o,p,q,r,s,t,u,v0,w,x,y,z,g,h,m1,m2,SHIFT,del;

    BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                isScanning = false;
                List<ScanResult> mScanResults = mWifiManager.getScanResults();
                wifiList(mScanResults);

                WifiInfo info = mWifiManager.getConnectionInfo();
                String ssid = info.getSSID().replaceAll("^\"|\"$", "");
                boolean isFound = false;
                for (final ScanResult wifiNetwork : mScanResults) {
                    boolean connected = ssid.equals(wifiNetwork.SSID);
                    if (connected) {
                        isFound = true;
                        wifiStatus(WifiManager.calculateSignalLevel(wifiNetwork.level, 5));
                        break;
                    }
                }

                if (!isFound) {
                    wifiStatus(0);
                }
            }
        }
    };
    BroadcastReceiver wifiOnOffReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (wifiEnabled()) {
                if (!isScanning) {
                    isScanning = true;
                    new Handler().postDelayed(() -> {
                        if (mWifiManager != null) {
                            mWifiManager.startScan();
                        }
                    }, 2000);
                    turnOnOff(true);
                }
            } else {
                wifiList(null);
                wifiStatus(0);
                turnOnOff(false);
            }
        }
    };

    private static boolean checkWifiNegotiation(WifiManager wifiManager, int netId) {
        boolean startedHandshake = false;
        boolean successful = false;

        for (int i = 0; i < 30; i++) {
            Utilities.sleep(300);

            SupplicantState currentState = wifiManager.getConnectionInfo().getSupplicantState();
            if (!startedHandshake && currentState.equals(SupplicantState.FOUR_WAY_HANDSHAKE)) {
                startedHandshake = true;
            } else if (startedHandshake) {
                if (currentState.equals(SupplicantState.DISCONNECTED)) {
                    break;
                } else if (currentState.equals(SupplicantState.COMPLETED)) {
                    successful = true;
                    break;
                }
            }
            wifiManager.enableNetwork(netId, true);
        }

        if (!successful && wifiManager.getConnectionInfo().getSupplicantState().equals(SupplicantState.COMPLETED)) {
            successful = true;
        }

        return successful;
    }

    /*private void onOffWiFi(boolean onOffState) {
        no_item_found.setVisibility(View.VISIBLE);
        if (onOffState) {
            no_item_found.setText(getResources().getText(R.string.please_wait));
            staticHandler.postDelayed(() -> mWifiManager.startScan(), 2000);
        } else {
            no_item_found.setText(getResources().getText(R.string.turn_on_wifi));
            clearList();
        }
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        initViews();
        setupEvents();

        registerReceivers();

        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiEnabled()) {
            new Handler().postDelayed(() -> mWifiManager.startScan(), 2000);
        }

        boolean b = wifiEnabled();
        if (b) {
            rsWifiOnOff.setSelection("ON");
            refresh();
        } else {
            rsWifiOnOff.setSelection("OFF");
        }

    }

    public boolean wifiEnabled() {
        WifiManager mng = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return mng.isWifiEnabled();
    }

    private void clearList() {
        if (wifiList != null) {
            wifiList.clear();
            wifiListAdapter.clearList();
        }
    }

    public void refreshList(List<ScanResult> mScanResults) {
        wifiList = new ArrayList<>();

        if (mScanResults == null) {
            no_item_found.setText(getResources().getText(R.string.turn_on_wifi));
            no_item_found.setVisibility(View.VISIBLE);
            rvWifi.setVisibility(View.GONE);
            return;
        }

        setRecycler(wifiList);

        WifiInfo info = mWifiManager.getConnectionInfo();
        String ssid = info.getSSID().replaceAll("^\"|\"$", "");
        for (final ScanResult wifiNetwork : mScanResults) {
            boolean connected = ssid.equals(wifiNetwork.SSID);
            if (connected) {
                wifiList.add(0, new WifiListModel(getSignalDrwableDark(WifiManager.calculateSignalLevel(wifiNetwork.level, 5)),
                        wifiNetwork.SSID, getSecurityType(wifiNetwork.capabilities), connected, wifiNetwork.capabilities,
                        WifiManager.calculateSignalLevel(wifiNetwork.level, 5)));
            } else {
                if (!wifiNetwork.SSID.isEmpty()) {
                    WifiListModel model = new WifiListModel(getSignalDrwableDark(WifiManager.calculateSignalLevel(wifiNetwork.level, 5)),
                            wifiNetwork.SSID, getSecurityType(wifiNetwork.capabilities), connected, wifiNetwork.capabilities,
                            WifiManager.calculateSignalLevel(wifiNetwork.level, 5));
                    wifiList.add(model);
                }
            }
        }
        setRecycler(wifiList);
        imvRefresh.clearAnimation();
    }

    public int getSignalDrwableDark(int db) {
        int id = 0;
        switch (db) {
            case 0:
                id = (R.drawable.ic_wifi_level_zero_black);
                break;
            case 1:
                id = (R.drawable.ic_wifi_level_one_black);
                break;
            case 2:
                id = (R.drawable.ic_wifi_level_two_black);
                break;
            case 3:
                id = (R.drawable.ic_wifi_level_three_black);
                break;
            case 4:
                id = (R.drawable.ic_wifi_level_four_black);
                break;
        }
        return id;
    }

    private void setRecycler(ArrayList<WifiListModel> wifiList) {
        ArrayList<WifiListModel> finalWifiList = new ArrayList<>();
        if (wifiList.isEmpty()) {
            no_item_found.setText(getResources().getText(R.string.no_wifi));
            no_item_found.setVisibility(View.VISIBLE);
            rvWifi.setVisibility(View.GONE);
        } else {
            no_item_found.setVisibility(View.GONE);
            rvWifi.setVisibility(View.VISIBLE);

            HashSet<String> wifiSSIDs = new HashSet<>();
            WifiListModel connectedWifi = wifiList.remove(0);
            Collections.sort(wifiList, (wm1, wm2) -> wm2.getSignalStrength() - wm1.getSignalStrength());
            wifiList.add(0, connectedWifi);
            for (WifiListModel model : wifiList) {
                if (!wifiSSIDs.contains(model.getSSID())) {
                    finalWifiList.add(model);
                }
                wifiSSIDs.add(model.getSSID());
            }
        }
        wifiListAdapter.setData(finalWifiList);
    }

    private boolean getSecurityType(String capabilities) {
        if (capabilities.toUpperCase().contains("WEP") ||
                capabilities.toUpperCase().contains("WPA") ||
                capabilities.toUpperCase().contains("WPA2")) {
            return true;
        }
        return false;
    }

    private void initViews() {
        wifiList = new ArrayList<>();
        rvWifi = (RecyclerView) findViewById(R.id.rvWifi);
        no_item_found = (TextView) findViewById(R.id.no_item_found);
        rsWifiOnOff = (RadioSwitch) findViewById(R.id.rsWifiOnOff);

        inPassLay = findViewById(R.id.inPassLay);

        imvRefresh = (ImageView) findViewById(R.id.imvRefresh);

        no_item_found.setText(getResources().getText(R.string.please_wait));
        rvWifi.setLayoutManager(new LinearLayoutManager(this));

        wifiListAdapter = new WifiListAdapter(this);
        wifiListAdapter.setData(wifiList);
        rvWifi.setAdapter(wifiListAdapter);

        imvRefresh.setOnClickListener(this);
    }

    private void setupEvents() {
        rsWifiOnOff.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton checkedRadioButton = rsWifiOnOff.findViewById(radioGroup.getCheckedRadioButtonId());

            if (!checkedRadioButton.isPressed()) {
                return;
            }

            try {
                if (checkedRadioButton.getText().toString().equalsIgnoreCase(getResources().getString(R.string.on))) {
                    isWiFiOn(true);
                } else if (checkedRadioButton.getText().toString().equalsIgnoreCase(getResources().getString(R.string.off))) {
                    isWiFiOn(false);
                }
            } catch (Exception ignored) {

            }
        });
    }

    @Override
    public void forgotPassword() {
        showForgotPassDialog();
    }

    private void refresh() {
        no_item_found.setText(getResources().getText(R.string.please_wait));
        no_item_found.setVisibility(View.VISIBLE);
        rvWifi.setVisibility(View.GONE);
        clearList();
        if (mWifiManager != null) {
            mWifiManager.startScan();
        }
    }

    private void showForgotPassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        builder.setTitle("Are you sure you want to Forget this Network");
        builder.setPositiveButton("Yes", (dialog, id) -> {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            int networkId = wifiManager.getConnectionInfo().getNetworkId();
            wifiManager.removeNetwork(networkId);
            wifiManager.saveConfiguration();
            refresh();
            dialog.dismiss();
        })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    dialog.dismiss();
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClickConnectWifi(WifiListModel wifiListModel) {
        if (wifiListModel.isConnected()) {
            return;
        }

        if (!wifiListModel.isSecurityType()) {
            connectWiFi("", wifiListModel);
            return;
        }

        PasswordDialog(wifiListModel);
    }

    @Override
    public void addWifiNetwork() {
        AddNetworkDialog addNetworkDialog = new AddNetworkDialog(this, (wifiListModel, password) -> {
            connectWiFi(password, wifiListModel);
        });
        addNetworkDialog.showDialog(findViewById(R.id.clRoot));
    }

    @SuppressLint("StaticFieldLeak")
    public void connectWiFi(String password, WifiListModel wifiListModel) {
        no_item_found.setVisibility(View.VISIBLE);
        no_item_found.setText(getResources().getText(R.string.please_wait));
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", wifiListModel.getSSID());
        wifiConfig.priority = 40;

        try {
            if (wifiListModel.getCapabilities().toUpperCase().contains("WEP")) {
                wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                wifiConfig.hiddenSSID = true;
                if (password.matches("^[0-9a-fA-F]+$")) {
                    wifiConfig.wepKeys[0] = password;
                } else {
                    wifiConfig.wepKeys[0] = "\"".concat(password).concat("\"");
                }
                wifiConfig.wepTxKeyIndex = 0;
            } else if (wifiListModel.getCapabilities().toUpperCase().contains("WPA") ||
                    wifiListModel.getCapabilities().toUpperCase().contains("WPA2")) {
                wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                wifiConfig.hiddenSSID = true;
                wifiConfig.preSharedKey = "\"" + password + "\"";
            } else {
                wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                wifiConfig.allowedAuthAlgorithms.clear();
                wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                wifiConfig.hiddenSSID = true;
            }

            int netId = mWifiManager.addNetwork(wifiConfig);
            mWifiManager.disconnect();
            mWifiManager.enableNetwork(netId, true);
            mWifiManager.reconnect();

            new AsyncTask<String, String, Boolean>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    no_item_found.setText(getResources().getText(R.string.please_wait));
                    no_item_found.setVisibility(View.VISIBLE);
                    rvWifi.setVisibility(View.GONE);
                }

                @Override
                protected Boolean doInBackground(String... params) {
                    return checkWifiNegotiation(mWifiManager, netId);
                }

                protected void onPostExecute(Boolean result) {
                    no_item_found.setVisibility(View.GONE);
                    rvWifi.setVisibility(View.VISIBLE);
                    if (result) {
                        refresh();
                    } else {
                        no_item_found.setVisibility(View.GONE);
                        showPopup(findViewById(R.id.clRoot), "Authentication Failed", true);
                    }
                }
            }.execute();

            try {
                mWifiManager.addNetwork(wifiConfig);
            } catch (Exception e) {

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showPopup(View viewById, String s, boolean b) {
        CarToast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public void PasswordDialog(WifiListModel wifiListModel) {


        inPassLay.setVisibility(View.VISIBLE);

        TextView wifiName, cancelTxt, connectTxt;
        ScrollView sVRoot;
        ConstraintLayout clPassword;
        clPassword = inPassLay.findViewById(R.id.clPassword);
        sVRoot = inPassLay.findViewById(R.id.sVRoot);
        cancelTxt = inPassLay.findViewById(R.id.cancelTxt);
        connectTxt = inPassLay.findViewById(R.id.connectTxt);
        wifiName = inPassLay.findViewById(R.id.wifiName);
        q = inPassLay.findViewById(R.id.q);
       w = inPassLay.findViewById(R.id.w);
        e = inPassLay.findViewById(R.id.e);
        r = inPassLay.findViewById(R.id.r);
        t = inPassLay.findViewById(R.id.t);
        y = inPassLay.findViewById(R.id.y);
        u = inPassLay.findViewById(R.id.u);
        i = inPassLay.findViewById(R.id.i);
        o = inPassLay.findViewById(R.id.o);
        p = inPassLay.findViewById(R.id.p);
        a = inPassLay.findViewById(R.id.a);
        s = inPassLay.findViewById(R.id.s);
        d = inPassLay.findViewById(R.id.d);
        f = inPassLay.findViewById(R.id.f);
        g = inPassLay.findViewById(R.id.g);
        h = inPassLay.findViewById(R.id.h);
        j = inPassLay.findViewById(R.id.j);
        k = inPassLay.findViewById(R.id.k);
        l = inPassLay.findViewById(R.id.l);
        l1 = inPassLay.findViewById(R.id.l);
        z = inPassLay.findViewById(R.id.z);
        x = inPassLay.findViewById(R.id.x);
        c = inPassLay.findViewById(R.id.c);
        v0 = inPassLay.findViewById(R.id.v);
        b = inPassLay.findViewById(R.id.b);
        n = inPassLay.findViewById(R.id.n);
        m = inPassLay.findViewById(R.id.m);
        m1 = inPassLay.findViewById(R.id.m1);
        m2= inPassLay.findViewById(R.id.m2);
        del= inPassLay.findViewById(R.id.del);
        SHIFT= inPassLay.findViewById(R.id.shift);
        wifiName.setText(wifiListModel.getSSID());
        EditText edtPassword = inPassLay.findViewById(R.id.edit_network_password);
        CheckBox showPassword = inPassLay.findViewById(R.id.do_not_show);
        q.setOnClickListener(onclick);
        w.setOnClickListener(onclick);
        e.setOnClickListener(onclick);
        r.setOnClickListener(onclick);
        t.setOnClickListener(onclick);
        y.setOnClickListener(onclick);
        u.setOnClickListener(onclick);
        i.setOnClickListener(onclick);
        o.setOnClickListener(onclick);
        p.setOnClickListener(onclick);
        l.setOnClickListener(onclick);
        l1.setOnClickListener(onclick);
        k.setOnClickListener(onclick);
        h.setOnClickListener(onclick);
        g.setOnClickListener(onclick);
        j.setOnClickListener(onclick);
        f.setOnClickListener(onclick);
        d.setOnClickListener(onclick);
        s.setOnClickListener(onclick);
        a.setOnClickListener(onclick);
        m.setOnClickListener(onclick);
        n.setOnClickListener(onclick);
        f.setOnClickListener(onclick);
        b.setOnClickListener(onclick);
        v0.setOnClickListener(onclick);
        c.setOnClickListener(onclick);
        x.setOnClickListener(onclick);
        z.setOnClickListener(onclick);
        e.setOnClickListener(onclick);
        m1.setOnClickListener(onclick);
        m2.setOnClickListener(onclick);


      
        
        
        SHIFT.setOnClickListener(onclickm);
        del.setOnClickListener(onclickm);
//        View.OnClickListener mCorkyListener = new OnClickListener() {
//
//        };
//
//        q.setOnClickListener(new View.OnClickListener(){
//
//        });

        final int INPUT_TYPE_DEFAULT;


        edtPassword.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {

                final Dialog dialog = new Dialog(getApplicationContext()); // Context, this, etc.
                dialog.setContentView(R.layout.keyboard);
                dialog.setTitle("Keyboard");
                dialog.show();

                CarToast.makeText(this, "Got the focus", Toast.LENGTH_LONG).show();
            } else {
                CarToast.makeText(getApplicationContext(), "Lost the focus", Toast.LENGTH_LONG).show();

            }

        });

        edtPassword.setText("");
        setEvents(edtPassword, sVRoot, clPassword);

        connectTxt.setOnClickListener(view -> {
            if (TextUtils.isEmpty(edtPassword.getText())) {
                showPopup(sVRoot, "please enter pass", true);
                return;
            }
            wifiSettingsActivity.this.connectWiFi(edtPassword.getText().toString(), wifiListModel);
            inPassLay.setVisibility(View.GONE);
        });

        cancelTxt.setOnClickListener(view -> inPassLay.setVisibility(View.GONE));
        showPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {

                // hide password
                edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                edtPassword.setSelection(edtPassword.getText().length());
            } else {
//                showDialog(findViewById(R.id.clRoot));
//                CarToast.makeText(this, "Got the focus", Toast.LENGTH_LONG).show();
                // show password

                edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                edtPassword.setSelection(edtPassword.getText().length());
            }
        });

    }

    private void setEvents(EditText edtPassword, ScrollView sVRoot, ConstraintLayout clPassword) {
        edtPassword.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                focusOnView(sVRoot, clPassword);
            }
        });

        edtPassword.setOnClickListener(view -> {
            focusOnView(sVRoot, clPassword);
        });

        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                focusOnView(sVRoot, clPassword);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void focusOnView(final ScrollView scroll, final View view) {
        new Handler().post(() -> {
            scroll.smoothScrollTo(0, view.getTop() - 30);
        });
    }

    private void isWiFiOn(boolean b) {
        no_item_found.setVisibility(View.VISIBLE);
        if (b) {
            no_item_found.setText(getResources().getText(R.string.please_wait));
            mWifiManager.setWifiEnabled(true);
            isWifiEnabled = true;
//            staticHandler.postDelayed(() -> mWifiManager.startScan(), 2000);
            new Handler().postDelayed(() -> mWifiManager.startScan(), 2000);
        } else {
            no_item_found.setText(getResources().getText(R.string.turn_on_wifi));
            mWifiManager.setWifiEnabled(false);
            isWifiEnabled = false;
            clearWifiList();
        }
    }
//public void onclick(View view)
    // Create an anonymous implementation of OnClickListener
    private View.OnClickListener onclick = new View.OnClickListener() {
        public void onClick(View v) {

            String BB = ((Button) findViewById(v.getId())).getText().toString();
            EditText etPassword = inPassLay.findViewById(R.id.edit_network_password);
            etPassword.setText(etPassword.getText()+BB);//etPassword.getText()+
            CarToast.makeText(getApplicationContext(),BB+"",Toast.LENGTH_SHORT).show();
            // do something when the button is clicked 
            // Yes we will handle click here but which button clicked??? We don't know

        };
    public void onclick(View v) {
        String BB = ((Button) findViewById(v.getId())).getText().toString();
        CarToast.makeText(getBaseContext(),BB+"",Toast.LENGTH_SHORT).show();
        // do something when the button is clicked
        // Yes we will handle click here but which button clicked??? We don't know

    };
    };
    private View.OnClickListener onclickm = new View.OnClickListener() {
        public void onClick(View v) {
            String RR = ((Button) findViewById(v.getId())).getText().toString();

            switch (RR) {
                case "SHIFT":
            }
            q.setText(RR);
            w.setText(RR);
            e.setText(RR);
            r.setText(RR);
            t.setText(RR);
            y.setText(RR);
            u.setText(RR);
            i.setText(RR);
            o.setText(RR);
            p.setText(RR);
            l.setText(RR);
            l1.setText(RR);
            k.setText(RR);
            h.setText(RR);
            g.setText(RR);
            j.setText(RR);
            f.setText(RR);
            d.setText(RR);
            s.setText(RR);
            a.setText(RR);
            m.setText(RR);
            n.setText(RR);
            f.setText(RR);
            b.setText(RR);
            v0.setText(RR);
            c.setText(RR);
            x.setText(RR);
            z.setText(RR);
            e.setText(RR);
            m1.setText(RR);
            m2.setText(RR);
            EditText etPassword = inPassLay.findViewById(R.id.edit_network_password);
            etPassword.setText(etPassword.getText()+RR);
        };
        public void onclick(View v) {
            String BB = ((Button) findViewById(v.getId())).getText().toString();
            CarToast.makeText(getBaseContext(),BB+"",Toast.LENGTH_SHORT).show();
            // do something when the button is clicked
            // Yes we will handle click here but which button clicked??? We don't know

        };
    };

    private void clearWifiList() {
        if (wifiList != null) {
            wifiList.clear();
            wifiListAdapter.clearList();
        }
    }

    @Override
    public void batteryStatus(boolean isCharging, int batteryPercentage) {

    }

    @Override
    public void wifiStatus(int strength) {

    }

    @Override
    public void turnOnOff(boolean isOn) {
        if (isOn) {
            rsWifiOnOff.setSelection(getResources().getString(R.string.on));
        } else {
            rsWifiOnOff.setSelection(getResources().getString(R.string.off));
        }
    }

    @Override
    public void wifiList(List<ScanResult> wifiList) {
        refreshList(wifiList);
    }



public void kb(View v){
    CarToast.makeText(this,"ughylihg",Toast.LENGTH_SHORT).show();

}
    @Override
    public void onClick(View view) {
        CarToast.makeText(this,"ughylihg",Toast.LENGTH_SHORT).show();

//        String ida = view.getId().
        switch (view.getId()) {
            case R.id.imvRefresh:
                if (rsWifiOnOff.getSelection(getResources().getString(R.string.on))) {
                    refresh();
                    startRotateRefresh();
                }
                break;
            default:
                break;
        }
    }

    private void startRotateRefresh() {
        rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(900);
        rotate.setRepeatCount(Animation.INFINITE);
        imvRefresh.startAnimation(rotate);
    }

    public void showDialog(View rootView) {
        if (rootView != null) {
            LayoutInflater inflater = LayoutInflater.from(this);
            View mView = inflater.inflate(R.layout.keyboard, null, true);
            popUp = new PopupWindow(mView, MATCH_PARENT, rootView.getHeight(), false);

            popUp.setOutsideTouchable(true);
            popUp.setElevation(20);
            wp1 = (TextView) mView.findViewById(R.id.wp);

//
//            w = (TextView) mView.findViewById(R.id.w);
//            e = (TextView) mView.findViewById(R.id.e);
//            r = (TextView) mView.findViewById(R.id.r);
//            q = (TextView) mView.findViewById(R.id.q);
//            t = (TextView) mView.findViewById(R.id.t);
            q.setOnClickListener(V -> {
                wp1.setText(wp1.getText()+"q");
                CarToast.makeText(getApplicationContext(), "Got the focus", Toast.LENGTH_LONG).show();

            });
            w.setOnClickListener(q -> {
                wp1.setText(wp1.getText()+"w");
                popUp.dismiss();
                CarToast.makeText(getApplicationContext(), "tsgrhys", Toast.LENGTH_LONG).show();

            });
            t.setOnClickListener(q -> {
                wp1.setText(wp1.getText()+"t");
                popUp.dismiss();
                CarToast.makeText(getApplicationContext(), "tsgrhys", Toast.LENGTH_LONG).show();

            });
            e.setOnClickListener(q -> wp1.setText(wp1.getText()+"e"));
            r.setOnClickListener(q -> wp1.setText(wp1.getText()+"r"));



//            spSecurity.setOnClickListener(view -> {
//                if (llSecurity.getVisibility() == View.VISIBLE) {
//                    llSecurity.setVisibility(View.GONE);
//                } else {
//                    llSecurity.setVisibility(View.VISIBLE);
//                }
//            });
//            tvNone.setOnClickListener(view -> {
//                spSecurity.setText(tvNone.getText());
//                llSecurity.setVisibility(View.GONE);
//                tilPassword.setVisibility(View.GONE);
//            });
//            tvWep.setOnClickListener(view -> {
//                spSecurity.setText(tvWep.getText());
//                llSecurity.setVisibility(View.GONE);
//                tilPassword.setVisibility(View.VISIBLE);
//            });
//            tvWpa.setOnClickListener(view -> {
//                spSecurity.setText(tvWpa.getText());
//                llSecurity.setVisibility(View.GONE);
//                tilPassword.setVisibility(View.VISIBLE);
//            });
//
//            tvSave.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                }
//            });


           /* popUp.setOnDismissListener(() -> {
                BaseActivity.setCurrentViewForPopUp(null);
            });*/

            int topSpace = (int) Utilities.convertDpToPixel(15, getApplicationContext()); //Top Header space
            popUp.showAtLocation(rootView, Gravity.TOP, 0, topSpace);

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceivers();
    }

    private void registerReceivers() {
        registerReceiver(mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        registerReceiver(wifiOnOffReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
    }

    private void unregisterReceivers() {
        unregisterReceiver(mWifiScanReceiver);
        unregisterReceiver(wifiOnOffReceiver);
    }

//     @Override
//     public void onClick(View view) {
//    }

}