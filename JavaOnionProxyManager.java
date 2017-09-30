package com.msopentech.thali.java.toronionproxy;

import com.msopentech.thali.toronionproxy.OnionProxyContext;
import com.msopentech.thali.toronionproxy.OnionProxyManager;

import java.io.File;

public class JavaOnionProxyManager extends OnionProxyManager {
    public JavaOnionProxyManager(OnionProxyContext onionProxyContext) {
        super(onionProxyContext);
    }

    @Override
    protected boolean setExecutable(File f) {
        return f.setExecutable(true);
    }
}