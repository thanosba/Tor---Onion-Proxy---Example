package com.msopentech.thali.java.toronionproxy;

import com.msopentech.thali.toronionproxy.OsData;
import com.msopentech.thali.toronionproxy.WriteObserver;
import com.sun.nio.file.SensitivityWatchEventModifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;

/**
 * Watches to see if a particular file is changed
 */
public class JavaWatchObserver implements WriteObserver {
    private WatchService watchService;
    private WatchKey key;
    private File fileToWatch;
    private long lastModified;
    private long length;
    private static final Logger LOG = LoggerFactory.getLogger(WriteObserver.class);


    public JavaWatchObserver(File fileToWatch) throws IOException {
        if (fileToWatch == null || !fileToWatch.exists()) {
            throw new RuntimeException("fileToWatch must not be null and must already exist.");
        }
        this.fileToWatch = fileToWatch;
        lastModified = fileToWatch.lastModified();
        length = fileToWatch.length();

        watchService = FileSystems.getDefault().newWatchService();
        // Note that poll depends on us only registering events that are of type path
        if (OsData.getOsType() != OsData.OsType.Mac) {
            key = fileToWatch.getParentFile().toPath().register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);
        } else {
           
            key = fileToWatch.getParentFile().toPath().register(watchService, new WatchEvent.Kind[]
                    {StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY}, SensitivityWatchEventModifier.HIGH);
        }
    }

    @Override
    public boolean poll(long timeout, TimeUnit unit) {
        boolean result = false;
        try {
            long remainingTimeoutInNanos = unit.toNanos(timeout);
            while (remainingTimeoutInNanos > 0) {
                long startTimeInNanos = System.nanoTime();
                WatchKey receivedKey = watchService.poll(remainingTimeoutInNanos, TimeUnit.NANOSECONDS);
                long timeWaitedInNanos = System.nanoTime() - startTimeInNanos;

                if (receivedKey != null) {
                    if (receivedKey != key) {
                        throw new RuntimeException("This really shouldn't have happened. EEK!" + receivedKey.toString());
                    }

                    for (WatchEvent<?> event : receivedKey.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();

                        if (kind == StandardWatchEventKinds.OVERFLOW ) {
                            LOG.error("We got an overflow, there shouldn't have been enough activity to make that happen.");
                        }


                        Path changedEntry = (Path) event.context();
                        if (fileToWatch.toPath().endsWith(changedEntry)) {
                            result = true;
                            return result;
                        }
                    }

                    
                    if (!key.reset()) {
                        LOG.error("The key became invalid which should not have happened.");
                    }
                }

                if (timeWaitedInNanos >= remainingTimeoutInNanos) {
                    break;
                }

                remainingTimeoutInNanos -= timeWaitedInNanos;
            }

            
            result = (fileToWatch.lastModified() != lastModified) || (fileToWatch.length() != length);
            return result;
        } catch (InterruptedException e) {
            throw new RuntimeException("Internal error has caused JavaWatchObserver to not be reliable.", e);
        } finally {
            if (result) {
                try {
                    watchService.close();
                } catch (IOException e) {
                    LOG.debug("Attempt to close watchService failed.", e);
                }
            }
        }
    }
}