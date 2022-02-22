package com.github.martoreto.aademo;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.app.NotificationCompat;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.apps.auto.sdk.CarActivity;
import com.google.android.apps.auto.sdk.CarToast;
import com.google.android.apps.auto.sdk.CarUiController;
import com.google.android.apps.auto.sdk.MenuController;
import com.google.android.apps.auto.sdk.MenuItem;
import com.google.android.apps.auto.sdk.StatusBarController;
import com.google.android.apps.auto.sdk.notification.CarNotificationExtender;

import java.util.ArrayList;
import java.util.List;

public class MainCarActivity extends CarActivity {
    private static final String TAG = "MainCarActivity";

    static final String MENU_HOME = "home";
    static final String MENU_DEBUG = "debug";
    static final String MENU_DEBUG_LOG = "log";
    static final String MENU_DEBUG_TEST_NOTIFICATION = "test_notification";

    private static final String FRAGMENT_DEMO = "demo";
    private static final String FRAGMENT_LOG = "log";

    private static final String CURRENT_FRAGMENT_KEY = "app_current_fragment";

    private static final int TEST_NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "car";
   public WifiManager mWifiManager;
   public ListView list;

    private String mCurrentFragmentTag;
    private Handler mHandler = new Handler();
   private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @SuppressLint("ResourceType")
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
               if(ApplicationPreference.getString("st", "", getApplicationContext()).equals("true")){
                   ApplicationPreference.setString("st",  "false", getApplicationContext());

                   CarToast.makeText(c, ApplicationPreference.getString("ssid","",getApplicationContext())+ "", Toast.LENGTH_SHORT).show();
                   ArrayAdapter<String> m_adapter;

                   ArrayList<String> m_listItems = new ArrayList<String>();
                   m_adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1, m_listItems);
                   list.setAdapter(m_adapter);
                   list = (ListView)findViewById(R.id.listview);
                   String[] wifis = {ApplicationPreference.getString("ssid","",getApplicationContext())+ "\n"};
                   list.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                           android.R.layout.simple_list_item_1,wifis));
                   EditText et = (EditText) findViewById(R.id.editTextTextPersonName);

//                   final String input = et.getText().toString();
                   m_listItems.add(et.toString());

               }else {
                List<ScanResult> mScanResults = mWifiManager.getScanResults();
                String[] wifis = new String[mScanResults.size()];
                for(int i = 0; i < mScanResults.size(); i++){
                    wifis[i] = ((mScanResults.get(i)).SSID+"\n");
                }
                list = (ListView)findViewById(R.id.listview);
                list.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_list_item_1,wifis));

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        list.getItemAtPosition(i);
//                        final EditText input = new EditText(c);
                        ApplicationPreference.setString("st",  "true", getApplicationContext());

                        ApplicationPreference.setString("ssid", list.getItemAtPosition(i) + "", getApplicationContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//                        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//                        switchToFragment(FRAGMENT_LOG);

                        onReceive(c,intent);

                        CarToast.makeText(c, list.getItemAtPosition(i) + "", Toast.LENGTH_SHORT).show();
                    }











                });

            }




            }
            }

    };
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        registerReceiver(mWifiScanReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mWifiManager.startScan();

        setTheme(R.style.AppTheme_Car);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            mChannel.setDescription(getString(R.string.notification_channel_description));
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(mChannel);
        }

        setContentView(R.layout.activity_car_main);
        Button buttonname;
        String initialFragmentTag1 = FRAGMENT_DEMO;
//        buttonname = (Button) findViewById(R.id.button) ;
//        buttonname.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                switchToFragment(initialFragmentTag1);
//            }
//        });
                //as I understand it, the "**this**" denotes the current `view(focus)` in the android program

        CarUiController carUiController = getCarUiController();
        carUiController.getStatusBarController().showTitle();

        FragmentManager fragmentManager = getSupportFragmentManager();
        DemoFragment demoFragment = new DemoFragment();
        LogFragment logFragment = new LogFragment();
//        TextView wif = (TextView) findViewById(R.id.textView4);
        mWifiManager.startScan();
        fragmentManager.beginTransaction()

                .add(R.id.fragment_container, demoFragment, FRAGMENT_DEMO)
                .detach(demoFragment)
                .add(R.id.fragment_container, logFragment, FRAGMENT_LOG)
                .detach(logFragment)
                .commitNow();

        String initialFragmentTag = FRAGMENT_DEMO;
        if (bundle != null && bundle.containsKey(CURRENT_FRAGMENT_KEY)) {
            initialFragmentTag = bundle.getString(CURRENT_FRAGMENT_KEY);
        }
        assert initialFragmentTag != null;
        switchToFragment(initialFragmentTag);

        ListMenuAdapter mainMenu = new ListMenuAdapter();
        mainMenu.setCallbacks(mMenuCallbacks);
        mainMenu.addMenuItem(MENU_HOME, new MenuItem.Builder()
                .setTitle("Add Wi-Fi")
                .setType(MenuItem.Type.ITEM)
                .build());
        mainMenu.addMenuItem(MENU_DEBUG, new MenuItem.Builder()
                .setTitle(mWifiScanReceiver.getResultCode()+" ")
                .setType(MenuItem.Type.SUBMENU)
                .build());

        ListMenuAdapter debugMenu = new ListMenuAdapter();
        debugMenu.setCallbacks(mMenuCallbacks);

        debugMenu.addMenuItem(MENU_DEBUG_LOG, new MenuItem.Builder()
                .setTitle(getString(R.string.menu_exlap_stats_log_title))
                .setType(MenuItem.Type.ITEM)
                .build());
        debugMenu.addMenuItem(MENU_DEBUG_TEST_NOTIFICATION, new MenuItem.Builder()
                .setTitle(mWifiScanReceiver.toString())
                .setType(MenuItem.Type.ITEM)
                .build());
        mainMenu.addSubmenu(MENU_DEBUG, debugMenu);

        MenuController menuController = carUiController.getMenuController();
        menuController.setRootMenuAdapter(mainMenu);
//        menuController.showMenuButton();
        StatusBarController statusBarController = carUiController.getStatusBarController();
//        statusBarController.setAppBarAlpha(0.5f);
//        statusBarController.setAppBarBackgroundColor(0xffff0000);

        getSupportFragmentManager().registerFragmentLifecycleCallbacks(mFragmentLifecycleCallbacks,
                false);
    }


    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putString(CURRENT_FRAGMENT_KEY, mCurrentFragmentTag);
        super.onSaveInstanceState(bundle);
    }

    private final ListMenuAdapter.MenuCallbacks mMenuCallbacks = new ListMenuAdapter.MenuCallbacks() {
        @Override
        public void onMenuItemClicked(String name) {
            switch (name) {
                case MENU_HOME:
                    switchToFragment(FRAGMENT_DEMO);
                    break;
                case MENU_DEBUG_LOG:
                    switchToFragment(FRAGMENT_LOG);
                    break;
                case MENU_DEBUG_TEST_NOTIFICATION:
                    showTestNotification();
                    break;
            }
        }

        @Override
        public void onEnter() {
        }

        @Override
        public void onExit() {
            updateStatusBarTitle();
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        switchToFragment(mCurrentFragmentTag);
    }

    private void switchToFragment(String tag) {
        if (tag.equals(mCurrentFragmentTag)) {
            return;
        }
        FragmentManager manager = getSupportFragmentManager();
        Fragment currentFragment = mCurrentFragmentTag == null ? null : manager.findFragmentByTag(mCurrentFragmentTag);
        Fragment newFragment = manager.findFragmentByTag(tag);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (currentFragment != null) {
            transaction.detach(currentFragment);
        }
        transaction.attach(newFragment);
        transaction.commit();
        mCurrentFragmentTag = tag;
    }

    private final FragmentManager.FragmentLifecycleCallbacks mFragmentLifecycleCallbacks
            = new FragmentManager.FragmentLifecycleCallbacks() {
        @Override
        public void onFragmentStarted(FragmentManager fm, Fragment f) {
            updateStatusBarTitle();
        }
    };

    private void updateStatusBarTitle() {
        CarFragment fragment = (CarFragment) getSupportFragmentManager().findFragmentByTag(mCurrentFragmentTag);
        getCarUiController().getStatusBarController().setTitle(fragment.getTitle());
    }

    private void showTestNotification() {
        CarToast.makeText(this, "Will show notification in 5 seconds", Toast.LENGTH_SHORT).show();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Notification notification = new NotificationCompat.Builder(MainCarActivity.this,
                        NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Test notification")
                        .setContentText("This is a test notification")
                        .setAutoCancel(true)
                        .extend((NotificationCompat.Extender) new CarNotificationExtender.Builder()
                                .setTitle("Test")
                                .setSubtitle("This is a test notification")
                                .setActionIconResId(R.mipmap.ic_launcher)
                                .setThumbnail(CarUtils.getCarBitmap(MainCarActivity.this,
                                        R.mipmap.ic_launcher, R.color.car_primary, 128))
                                .setShouldShowAsHeadsUp(true)
                                .build())
                        .build();

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                assert notificationManager != null;
                notificationManager.notify(TAG, TEST_NOTIFICATION_ID, notification);

                CarNotificationSoundPlayer soundPlayer = new CarNotificationSoundPlayer(
                        MainCarActivity.this, R.raw.bubble);
                soundPlayer.play();
            }
        }, 5000);
    }
}
