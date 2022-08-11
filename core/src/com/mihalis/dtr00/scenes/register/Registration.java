package com.mihalis.dtr00.scenes.register;

import static com.mihalis.dtr00.constants.Constant.INVALID_RESPONSE;
import static com.mihalis.dtr00.hub.Resources.getLocales;
import static com.mihalis.dtr00.systemd.service.Processor.postTask;
import static com.mihalis.dtr00.systemd.service.Service.print;

import com.badlogic.gdx.utils.Array;
import com.mihalis.dtr00.systemd.service.FileManager;
import com.mihalis.dtr00.systemd.service.Networking;
import com.mihalis.dtr00.utils.ArrayFiller;
import com.mihalis.dtr00.utils.AsyncRequestHandler;
import com.mihalis.dtr00.utils.UserDevice;
import com.mihalis.dtr00.utils.UserSettings;

import java.util.Arrays;
import java.util.HashMap;

public abstract class Registration {
    // channels - кубики на реле (2, 4, 8 шт.)
    private int getCountOfRelayChannels(String relayStatus) {
        int relayChannels = 2;

        while (!relayStatus.contains(String.valueOf(relayChannels)) && relayChannels <= 16) {
            relayChannels *= 2;
        }
        return relayChannels;
    }

    private String getRandomPassword() {
        Array<String> latin = new Array<>(26);
        latin.addAll("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");
        latin.shuffle();
        return latin.toString("");
    }

    private UserSettings getPrimaryUserSettings(int countOfRelayChannels) {
        UserSettings userSettings = new UserSettings();
        userSettings.relayNames = new String[countOfRelayChannels];
        userSettings.relayDelays = new int[countOfRelayChannels];
        userSettings.disabledMask = new boolean[countOfRelayChannels][4];

        ArrayFiller.fill(userSettings.relayNames, (int index) -> getLocales().relay + " " + (index + 1));
        ArrayFiller.fill(userSettings.disabledMask, true);
        Arrays.fill(userSettings.relayDelays, 5);

        return userSettings;
    }

    UserDevice createUserJson(int countOfRelayChannels, boolean rememberRegistration) {
        UserDevice userDevice = new UserDevice();

        userDevice.rememberRegistration = rememberRegistration;
        userDevice.userSettings = getPrimaryUserSettings(countOfRelayChannels);
        userDevice.deviceName = Networking.getIpAddress();
        userDevice.countOfRelayChannels = countOfRelayChannels;

        return userDevice;
    }

    void login(String login, String password, boolean rememberRegistration) {
        String randomPassword = getRandomPassword();
        AsyncRequestHandler handler = new AsyncRequestHandler(3) {
            @Override
            public void action(HashMap<String, String> responses) {
                long responseWithUserPasswd = getLoginStatusFromResponse(responses.get(password));
                long responseWithRandomPasswd = getLoginStatusFromResponse(responses.get(randomPassword));

                if (responseWithUserPasswd == INVALID_RESPONSE || responseWithRandomPasswd == INVALID_RESPONSE) {
                    postTask(() -> {
                        throw new RuntimeException("Invalid response from relay: " +
                                responses.get(password) + ", " + responses.get(randomPassword));
                    });
                }
                if (responseWithUserPasswd > responseWithRandomPasswd) {
                    UserDevice userDevice = createUserJson(getCountOfRelayChannels(responses.get("relayStatus")), rememberRegistration);
                    FileManager.writeToJsonFile(Networking.getIpAddress(), userDevice);
                    onCorrect();
                } else {
                    onIncorrect();
                }
            }
        };

        Networking.login(login, password, handler);
        Networking.login(login, randomPassword, handler);
        Networking.getRelayStatus(handler);
    }

    private long getLoginStatusFromResponse(String responseString) {
//        Example: &0&, &302&/menu_page.html&
        for (String responsePiece : responseString.split("&")) {
            try {
                return Integer.parseInt(responsePiece);
            } catch (Exception exception) {
                print("Can't parse string in response:", responsePiece);
            }
        }

        return INVALID_RESPONSE;
    }

    abstract void onIncorrect();

    abstract void onCorrect();
}
