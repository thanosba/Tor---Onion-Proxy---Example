package javaonionproxy;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import javax.swing.text.Utilities;

/**
 *
 * @author thanosbalas
 */
public class JavaOnionProxy {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        int totalSecondsPerTorStartup = 4 * 60;
        int totalTriesPerTorStartup = 5;
        int hiddenServicePort = 80;
        int localPort = 9343;
        String fileStorageLocation = "torfiles";
        OnionProxyManager onionProxyManager = new JavaOnionProxyManager(new JavaOnionProxyContext(Files.createTempDirectory(fileStorageLocation).toFile()));
        // Start the Tor Onion Proxy
        if (onionProxyManager.startWithRepeat(totalSecondsPerTorStartup, totalTriesPerTorStartup) == false) {
        return;
        }
        // Start a hidden service listener
        String onionAddress = onionProxyManager.publishHiddenService(hiddenServicePort, localPort);
        Socket clientSocket =Utilities.socks4aSocketConnection(onionAddress, hiddenServicePort, "127.0.0.1", localPort);

    }
    
}
