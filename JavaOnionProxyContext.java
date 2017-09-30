package com.msopentech.thali.java.toronionproxy;

import com.msopentech.thali.toronionproxy.OnionProxyContext;
import com.msopentech.thali.toronionproxy.WriteObserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class JavaOnionProxyContext extends OnionProxyContext {

    public JavaOnionProxyContext(File workingDirectory) {
        super(workingDirectory);
    }

    @Override
    public WriteObserver generateWriteObserver(File file) {
        try {
            return new JavaWatchObserver(file);
        } catch (IOException e) {
            throw new RuntimeException("Could not create JavaWatchObserver", e);
        }
    }

    @Override
    protected InputStream getAssetOrResourceByName(String fileName) throws IOException {
        return getClass().getResourceAsStream("/" + fileName);
    }

    @Override
    public String getProcessId() {
        String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        return processName.split("@")[0];
    }
}