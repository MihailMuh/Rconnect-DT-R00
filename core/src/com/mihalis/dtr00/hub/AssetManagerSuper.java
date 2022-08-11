package com.mihalis.dtr00.hub;

import static com.badlogic.gdx.Application.ApplicationType.Desktop;
import static com.mihalis.dtr00.Settings.SHOW_ASSET_MANAGER_LOGS;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Logger;
import com.mihalis.dtr00.systemd.service.Processor;

public class AssetManagerSuper extends AssetManager {
    public final FileHandleResolver resolver;

    public AssetManagerSuper() {
        super();

        if (SHOW_ASSET_MANAGER_LOGS) getLogger().setLevel(Logger.DEBUG);

        Texture.setAssetManager(this);
        resolver = getFileHandleResolver();
    }

    public void loadAtlas(String path) {
        load(path, TextureAtlas.class);
    }

    @Override
    public void finishLoading() {
        while (!update()) {
        }
    }

    @Override
    public synchronized void unload(String fileName) {
        if (Gdx.app.getType() == Desktop) {
            Processor.postToGDX(() -> super.unload(fileName));
        } else {
            super.unload(fileName);
        }
    }
}
