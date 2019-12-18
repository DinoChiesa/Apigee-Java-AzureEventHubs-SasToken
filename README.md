# Azure Event Hubs SAS Token Callout

This directory contains Java source code for a callout which produces a
SAS token for Azure Event Hubs.  Also it includes a sample proxy.

- [Java source](./callout) - Java code, as well as instructions for how to build the Java code.
- [example proxy](./example-bundle) - an example API Proxy for Apigee Edge that shows how to use the resulting Java callout.

The API Proxy subdirectory here includes the pre-built JAR file. Therefore you
do not need to build the Java code in order to use this callout. However, you
may wish to modify this code for your own purposes. In that case, you will
modify the Java code, re-build, then copy that JAR into the appropriate
apiproxy/resources/java directory for the API Proxy.

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
    <Property name="resource-uri">contoso.servicebus.windows.net/hub1</Property>
    <Property name="expiry">7d</Property>
    <Property name="key">{private.shared_access_key}</Property>
    <Property name="key-name">{encodedkey_name}</Property>
  </Properties>
  <ClassName>com.google.apigee.edgecallouts.azureeventhubs.SasCallout</ClassName>
  <ResourceURL>java://apigee-azure-eventhubs-sas-callout-20191217.jar</ResourceURL>
</JavaCallout>
```

## Policy Properties

| property name    | description |
|------------------|--------------|
| `key`            | required. The string representing the shared key. |
| `key-encoding`   | optional. One of: {`hex`, `base16`, `base64`, `none`}. It tells the policy how to decode the key from the given key string. base16 is an alias for hex. The default is "none". |
| `key-name`       | required. The name of the shared key. |
| `resource-uri`   | required. The URI to sign.  Not clear whether this should include the scheme (http or https) or not. It seems to work either way. |
| `expiry`         | required. The expiry interval, expressed as a relative time. An integer followed by a letter {s,m,h,d}: 7d = 7 days. 5h = 5 hours. |
| `reference-time` | optional. An epoch-second value to use in place of "now" for computing the absolute expiry. |


## Building

```
mvn clean package
```


## License

This code is Copyright (c) 2019 Google LLC, and is released under the Apache Source License v2.0. For information see the [LICENSE](LICENSE) file.

## Disclaimer

This example is not an official Google product, nor is it part of an official Google product.
