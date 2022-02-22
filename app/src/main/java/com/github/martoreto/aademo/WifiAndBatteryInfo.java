package com.github.martoreto.aademo;

import android.net.wifi.ScanResult;

import java.util.List;

public interface WifiAndBatteryInfo {
    void batteryStatus(boolean isCharging, int batteryPercentage);
    void wifiStatus(int strength);
    void turnOnOff(boolean isOn);
    void wifiList(List<ScanResult> wifiList);
}
