# EthereumScanner

This project is an application that allow for the scanning of the blockchain Ethereum with the goal to collect all contracts.
The list of contracts found is written to a json file.

# Settings

The application has a configuration file called appsettings.json. In this file 4 parameters are defined:
  * <b>Ethereum node url</b>, that is the url of a generic Ethereum Node. The default is an Infura account
  * <b>Start block</b>, indicates the block from which the research start, from this starting block the process go back until the block 0. If this value is setted to 0 (default) the process starts from the last added block
  * <b>Output file</b>, the path where to write the results as a json file
  * <b>Needed contracts</b>, the number of required contracts as result (default 10.000)

# Deploy

The project can be imported in IntelliJ IDEA and tested with the included Run Configuration. 
Otherwise it can be executed manually using the jar file under the "/dist" directory.

The command for the manual execution is:
```java -jar ethereumscanner.jar```

Before execute the project is important to edit the settings file. Almost certainly the application will not work if you don't change the "Output file" value.
Based on the chosen execution method the appsettings.json file to modify is different: if you use the manual execution you must edit the appsettings.json on under the "/dist" dir, otherwise the file to edit is the one you find under the resources directory.
