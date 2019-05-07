import services.EthereumScanner;
import settings.Settings;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;


public class Application {

    public static void main(String[] args) throws IOException {
        Settings appSettings = Settings.instance();
        appSettings.initialize();

        EthereumScanner scanner = new EthereumScanner(appSettings);
        String version = scanner.getClientVersion();
        System.out.println("Connected to Ethereum client version: " + version);

        BigInteger number = scanner.getLastBlockNumber();
        System.out.println("Last Ethereum block: " + number);

        System.out.println("Start scanning");
        scanner.startScanning();
        System.out.println("Scan process terminated");
    }

}