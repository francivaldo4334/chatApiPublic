package br.com.confchat.api.viewmodel;

import java.util.UUID;

public class DeviceViewModel {
    private String name;

    public boolean isThisDevice() {
        return isThisDevice;
    }

    public void setThisDevice(boolean thisDevice) {
        isThisDevice = thisDevice;
    }

    private boolean isThisDevice;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    private UUID id;
}
