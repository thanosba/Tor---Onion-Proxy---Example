package com.msopentech.thali.local.toronionproxy;

import com.msopentech.thali.java.toronionproxy.JavaOnionProxyContext;
import com.msopentech.thali.java.toronionproxy.JavaOnionProxyManager;
import com.msopentech.thali.toronionproxy.OnionProxyManager;
import junit.framework.TestCase;

import java.io.IOException;
import java.nio.file.Files;

public class TorOnionProxyTestCase extends TestCase {
    public OnionProxyManager getOnionProxyManager(String workingSubDirectoryName) {
        try {
            return new JavaOnionProxyManager(
                    new JavaOnionProxyContext(
                            Files.createTempDirectory(workingSubDirectoryName).toFile()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void testTorOnionProxyTestCaseSetupProperly() {
       
    }
}