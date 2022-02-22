package com.github.martoreto.aademo;

public class WifiListModel {
    int signalDrwable, signalStrength;
    String SSID, capabilities;
    boolean securityType, connected;

    public WifiListModel(int signalDrwable, String SSID, boolean securityType, boolean connected, String capabilities, int signalStrength) {
        this.signalDrwable = signalDrwable;
        this.SSID = SSID;
        this.securityType = securityType;
        this.connected = connected;
        this.capabilities = capabilities;
        this.signalStrength = signalStrength;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    public int getSignalDrwable() {
        return signalDrwable;
    }

    public void setSignalDrwable(int signalDrwable) {
        this.signalDrwable = signalDrwable;
    }

    public boolean isSecurityType() {
        return securityType;
    }

    public void setSecurityType(boolean securityType) {
        this.securityType = securityType;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public int getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(int signalStrength) {
        this.signalStrength = signalStrength;
    }
}
