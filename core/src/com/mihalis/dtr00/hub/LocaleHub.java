package com.mihalis.dtr00.hub;

import static com.mihalis.dtr00.constants.Assets.LOCALES;
import static com.mihalis.dtr00.systemd.service.Service.print;

import com.badlogic.gdx.assets.loaders.I18NBundleLoader.I18NBundleParameter;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Locale;

public class LocaleHub extends BaseHub {
    public String ipAddress;
    public String authorization;
    public String enterIpDevice;
    public String login;
    public String password;
    public String rememberMe;
    public String apply;
    public String relay;
    public String incorrectLoginPasswd;
    public String successfullySaved;
    public String deviceConnectionErr;
    public String socketTimeout;
    public String boostInternet;
    public String addDevice;
    public String enter;
    public String deviceName;
    public String yourDevices;
    public String on, off, pulse;
    public String changeLayout;
    public String settings;
    public String enabled, disabled;
    public String enabledAt, seconds;
    public String save, cancel;
    public String currentIP;
    public String delay;
    public String filledAllFields;

    public LocaleHub(AssetManagerSuper assetManager) {
        super(assetManager);

        print("System language:", Locale.getDefault().getLanguage());
        loadNewLocale(Locale.getDefault().getLanguage());
    }

    @Override
    public void boot() {
        getLocale();
    }

    public void loadNewLocale(String language) {
        assetManager.load(LOCALES, I18NBundle.class, new I18NBundleParameter(new Locale(language), "UTF-8"));
    }

    public void getLocale() {
        final I18NBundle locale = assetManager.get(LOCALES, I18NBundle.class);

        ipAddress = locale.get("ipAddress");
        authorization = locale.get("authorization");
        enterIpDevice = locale.get("enterIpDevice");
        login = locale.get("login");
        password = locale.get("password");
        rememberMe = locale.get("rememberMe");
        apply = locale.get("apply");
        relay = locale.get("relay");
        incorrectLoginPasswd = locale.get("incorrectLoginPasswd");
        successfullySaved = locale.get("successfullySaved");
        deviceConnectionErr = locale.get("deviceConnectionErr");
        socketTimeout = locale.get("socketTimeout");
        boostInternet = locale.get("boostInternet");
        addDevice = locale.get("addDevice");
        enter = locale.get("enter");
        deviceName = locale.get("deviceName");
        yourDevices = locale.get("yourDevices");
        on = locale.get("on");
        off = locale.get("off");
        pulse = locale.get("pulse");
        changeLayout = locale.get("changeLayout");
        settings = locale.get("settings");
        enabled = locale.get("enabled");
        disabled = locale.get("disabled");
        enabledAt = locale.get("enabledAt");
        seconds = locale.get("seconds");
        save = locale.get("save");
        cancel = locale.get("cancel");
        currentIP = locale.get("currentIP");
        delay = locale.get("delay");
        filledAllFields = locale.get("filledAllFields");

        assetManager.unload(LOCALES);
    }
}
