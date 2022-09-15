package com.mihalis.dtr00.utils.jsonTypes;

public class UserDevice {
    public boolean rememberRegistration;
    public String login;
    public String password;
    public int countOfRelayChannels;

    public String deviceName;
    public UserSettings userSettings;

    @Override
    public String toString() {
        return super.toString() + ", fields: rememberRegistration " + rememberRegistration
                + ", login " + login + ", password " + password + ", countOfRelayChannels " + countOfRelayChannels
                + ", deviceName " + deviceName + ", userSettings" + userSettings;
    }
}
