# Azure Event Hubs SAS Token Callout

This directory contains Java source code for a callout which produces a
SAS token for Azure Event Hubs.  Also it includes a sample proxy.



- [Java source](./callout) - Java code, as well as instructions for how to build the Java code.
- [example proxy](./example-bundle) - an example API Proxy for Apigee Edge that shows how to use the resulting Java callout.


The API Proxy subdirectory here includes the pre-built JAR file. Therefore you
do not need to build the Java code in order to use this callout. However, you may wish to modify this code for your own purposes. In that case, you will modify the Java code, re-build, then copy that JAR into the appropriate apiproxy/resources/java directory for the API Proxy.


## Usage

Deploy the proxy bundle, and the invoke it like this: 
```
ORG=myorg
ENV=myenv
curl -i https://$ORG-$ENV.apigee.net/azure-eventhubs-sastoken/token -X POST -d '' 

```

## Policy Configuration

Here's an example policy configuration:

```
<JavaCallout name='Java-GenerateSasToken-1'>
  <Properties>
    <Property name="resource-uri">https://contoso.servicebus.windows.net/</Property>
    <Property name="expiry">7d</Property>
    <Property name="key">{private.encodedkey}</Property>
    <Property name="key-name">{encodedkey_name}</Property>
    <Property name="key-encoding">base64</Property>
  </Properties>
  <ClassName>com.google.apigee.edgecallouts.azureeventhubs.SasCallout</ClassName>
  <ResourceURL>java://apigee-azure-eventhubs-sas-callout-20191217.jar</ResourceURL>
</JavaCallout>
```

`key-encoding` is optional and can take values like `hex`, `base16` (which is an alias for hex),
`base64`, or `none`. It tells the policy how to decode the key from the given
key string. 


## Building

```
mvn clean package
```


## License

This code is Copyright (c) 2019 Google LLC, and is released under the Apache Source License v2.0. For information see the [LICENSE](LICENSE) file.

## Disclaimer

This example is not an official Google product, nor is it part of an official Google product.




