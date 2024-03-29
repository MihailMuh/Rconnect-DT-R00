package com.mihalis.dtr00.hub;

import static com.mihalis.dtr00.systemd.service.Watch.delta;

import com.mihalis.dtr00.systemd.service.Processor;
import com.mihalis.dtr00.systemd.service.Service;

public abstract class BaseHub {
    protected final AssetManagerSuper assetManager;

    public BaseHub(AssetManagerSuper assetManager) {
        this.assetManager = assetManager;
    }

    public void loadInCycle() {
        while (!assetManager.isFinished()) {
            Service.sleep((int) (delta * 2 * 1000));
            Processor.postToGDX(assetManager::update);
        }
    }

    public void boot() {

    }
}
