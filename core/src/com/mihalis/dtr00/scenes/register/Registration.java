package com.mihalis.dtr00.scenes.register;

import static com.mihalis.dtr00.constants.Constant.INVALID_RESPONSE;
import static com.mihalis.dtr00.hub.Resources.getLocales;
import static com.mihalis.dtr00.systemd.service.Processor.postTask;
import static com.mihalis.dtr00.systemd.service.Service.print;
import static com.mihalis.dtr00.systemd.service.networking.NetworkManager.getIpAddress;

import com.mihalis.dtr00.systemd.service.FileManager;
import com.mihalis.dtr00.systemd.service.networking.NetworkManager;
import com.mihalis.dtr00.utils.AsyncRequestHandler;
import com.mihalis.dtr00.utils.CollectionsManipulator;
import com.mihalis.dtr00.utils.jsonTypes.UserDevice;
import com.mihalis.dtr00.utils.jsonTypes.UserSettings;

import java.util.Arrays;
import java.util.HashMap;

public abstract class Registration {
    // channels - ports on relay (2, 4, 8 elems)
    private int getCountOfRelayChannels(String relayStatus) {
        int relayChannels = 2;

        while (!relayStatus.contains(String.valueOf(relayChannels)) && relayChannels <= 16) {
            relayChannels *= 2;
        }
        return relayChannels;
    }

    private UserSettings getPrimaryUserSettings(int countOfRelayChannels) {
        UserSettings userSettings = new UserSettings();
        userSettings.relayNames = new String[countOfRelayChannels];
        userSettings.relayDelays = new int[countOfRelayChannels];
        userSettings.disabledMask = new boolean[countOfRelayChannels][4];

        CollectionsManipulator.fill(userSettings.relayNames, (int index) -> getLocales().relay + " " + (index + 1));
        CollectionsManipulator.fill(userSettings.disabledMask, true);
        Arrays.fill(userSettings.relayDelays, 5);

        return userSettings;
    }

    UserDevice createUserJson(int countOfRelayChannels, boolean rememberRegistration, String login, String password) {
        UserDevice userDevice = new UserDevice();

        userDevice.login = login;
        userDevice.password = password;
        userDevice.rememberRegistration = rememberRegistration;
        userDevice.userSettings = getPrimaryUserSettings(countOfRelayChannels);
        userDevice.deviceName = getIpAddress();
        userDevice.countOfRelayChannels = countOfRelayChannels;

        return userDevice;
    }

    public void login(String login, String password, boolean rememberRegistration, boolean saveDeviceToJson) {
        AsyncRequestHandler handler = new AsyncRequestHandler(3) {
            @Override
            public void action(HashMap<String, String> responses) {
                long responseWithUserPasswd = getLoginStatusFromResponse(responses.get(password));
                long responseWithRandomPasswd = getLoginStatusFromResponse(responses.get(""));

                if (responseWithUserPasswd == INVALID_RESPONSE || responseWithRandomPasswd == INVALID_RESPONSE) {
                    postTask(() -> {
                        throw new RuntimeException("Invalid response from relay: " +
                                responses.get(password) + ", " + responses.get(""));
                    });
                }
                if (responseWithUserPasswd > responseWithRandomPasswd) {
                    if (saveDeviceToJson) {
                        UserDevice userDevice = createUserJson(getCountOfRelayChannels(responses.get("relayStatus")),
                                rememberRegistration, login, password);
                        FileManager.writeToJsonFile(getIpAddress(), userDevice);
                    }
                    onCorrect();
                } else {
                    onIncorrect();
                }
            }

            @Override
            public void onSocketTimeoutException() {
                super.onSocketTimeoutException();
                Registration.this.onSocketTimeoutException();
            }
        };

        if (isDemoInput(getIpAddress(), login, password)) {
            handler.action(getDemoResponse(password));
            return;
        }

        NetworkManager.login(login, password, handler);
        NetworkManager.login(login, "", handler);
        NetworkManager.getRelayStatus(handler); // get count of relay channels
    }

    private HashMap<String, String> getDemoResponse(String password) {
        HashMap<String, String> responses = new HashMap<>(2);
        responses.put(password, "&302&/menu_page.html&");
        responses.put("", "&0&");
        responses.put("relayStatus", "8");
        // 302 more then 0, so action will be successfully
        return responses;
    }

    private boolean isDemoInput(String ip, String login, String password) {
        return "admin".equals(ip) && "admin".equals(login) && "admin".equals(password);
    }

    private long getLoginStatusFromResponse(String responseString) {
//        Example: &0&, &302&/menu_page.html&
        for (String responsePiece : responseString.split("&")) {
            try {
                return Integer.parseInt(responsePiece);
            } catch (Exception exception) {
                print("Can't parse '", responsePiece, "' in response:", responseString);
            }
        }

        return INVALID_RESPONSE;
    }

    public abstract void onIncorrect();

    public abstract void onCorrect();

    public void onSocketTimeoutException() {

    }
}
