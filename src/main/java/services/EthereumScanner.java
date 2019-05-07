package services;

import okhttp3.OkHttpClient;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGetCode;
import org.web3j.protocol.http.HttpService;
import settings.Settings;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EthereumScanner {

    private Settings settings;

    private Web3j web3j;

    private ResultBuilder builder;


    public EthereumScanner() {
        this(Settings.instance());
    }

    public EthereumScanner(Settings setting) {
        if (setting == null)
            return;

        if (!setting.isInitialized())
            setting.initialize();

        this.settings = setting;
        HttpService http = new HttpService(setting.ethereumNodeUrl, new OkHttpClient());
        this.web3j = Web3j.build(http);
        this.builder = new ResultBuilder();
    }

    public void startScanning() {
        BigInteger startNumber = settings.startBlock;
        if (startNumber == null || startNumber.compareTo(BigInteger.ZERO) == 0)
            startNumber = getLastBlockNumber();

        BigInteger neededContracts = settings.neededContracts;
        if (neededContracts.compareTo(BigInteger.ZERO) <= 0) {
            neededContracts = null;
        }

        for (BigInteger blockIndex = startNumber; blockIndex.compareTo(BigInteger.ZERO) > 0; blockIndex = blockIndex.subtract(BigInteger.ONE)) {
            System.out.println("Analyzing block " + blockIndex);

            try {
                EthBlock.Block block = web3j
                        .ethGetBlockByNumber(new DefaultBlockParameterNumber(blockIndex), true)
                        .send().getBlock();

                if (block == null)
                    continue;

                List<EthBlock.TransactionResult> transactionResults = block.getTransactions();
                List<String> tos = getToValuesFromTransactions(transactionResults);
                tos = removeDuplicate(tos);

                List<String> contracts = new ArrayList<>();
                for (String address : tos) {
                    EthGetCode ethGetCode = web3j
                            .ethGetCode(address, DefaultBlockParameterName.LATEST)
                            .send();

                    if (!ethGetCode.hasError() && ethGetCode.getCode() != null)
                        contracts.add(address);
                }

                System.out.println("Found " + contracts.size() + " contracts in block " + blockIndex);
                BigInteger totalContracts = builder.appendToResult(contracts);
                System.out.println("The total list has " + totalContracts + " contracts");

                if (neededContracts != null && totalContracts.compareTo(neededContracts) >= 0) {
                    break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("-------------------------------------------------------");
        }
    }

    public String getClientVersion() {
        try {
            return web3j.web3ClientVersion().send().getWeb3ClientVersion();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public BigInteger getLastBlockNumber() {
        try {
            return web3j.ethBlockNumber().send().getBlockNumber();
        } catch (IOException e) {
            e.printStackTrace();
            return BigInteger.ZERO;
        }
    }


    private List<String> getToValuesFromTransactions(List<EthBlock.TransactionResult> transactionResults) {
        List<String> tos = new ArrayList<>();
        for (EthBlock.TransactionResult result : transactionResults) {
            EthBlock.TransactionObject transaction = (EthBlock.TransactionObject) result.get();
            String to = transaction.get().getTo();
            if (to != null)
                tos.add(to);
        }

        return tos;
    }

    private List<String> removeDuplicate(List<String> source) {
        return source.stream().distinct().collect(Collectors.toList());
    }

}
