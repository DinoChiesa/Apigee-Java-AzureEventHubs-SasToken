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
    <Property name="key-name">{shared_access_key_name}</Property>
  </Properties>
  <ClassName>com.google.apigee.edgecallouts.azureeventhubs.SasCallout</ClassName>
  <ResourceURL>java://apigee-azure-eventhubs-sas-callout-20191218.jar</ResourceURL>
</JavaCallout>
```

The key is the shared access key" provided by Azure, and typically looks something like this:
`B1OrZzY26crAJcxXpyEIaqbs7qNLGWXuR9mDL4U7mC4=`

The output is emitted into a context variable `sas.token`.  You can then use
this token to build an authorization header appropriate for your eventhub
service, like this:

```
<AssignMessage name='AM-1'>
  <Set>
    <Headers>
      <Header name='Authorization'>{sas.token}</Header>
      ...
    </Headers>
    <Payload contentType='application/json'> ... </Payload>
    <Verb>POST</Verb>
    <Path>/foo/bar</Path>
  </Set>
  ...
</AssignMessage>
```


## Policy Properties

| property name    | description                                                                                                                              |
|------------------|------------------------------------------------------------------------------------------------------------------------------------------|
| `key`            | required. The string representing the shared key.                                                                                        |
| `key-encoding`   | optional. One of: {`hex`, `base16`, `base64`, `none`}. This affects how the policy decodes the key from the key string. base16 is an alias for hex. The default is "none". |
| `key-name`       | required. The name of the shared key.                                                                                                    |
| `resource-uri`   | required. The URI to sign. It's  not clear to me whether this URI should include the scheme (https) or not. It seems to work without it. |
| `expiry`         | required. The expiry interval, expressed as a relative time. An integer followed by a letter {s,m,h,d}: 7d = 7 days. 5h = 5 hours.       |
| `reference-time` | optional. An epoch-second value to use in place of "now" for computing the absolute expiry.                                              |


## Notes

One thing I noticed: the signature generated from C# or Powershell code will be
different than the signature generated from this Java callout. In fact, for a
given combination of {uri, expiry, and key}, the signature produces by this Java
callout matches that produced by the JavaScript and the Java code [on
Microsoft's
website](https://docs.microsoft.com/en-us/azure/event-hubs/authenticate-shared-access-signature). But
the signature generated from C# or Powershell will be different.

The reason for the difference: URI
encoding in nodejs and Java produces a %2F (uppercase F) to encode the slash
character, while .NET (C#, Powershell, etc) uses %2f (lowercase f). The RFC 3986
says these encodings are equivalent, and they are treated as equivalent by
EventHub. Both signatures are valid.

But the signature from C# or Powershell will be different because the byte encoding is
different for that one character. Both are valid.

## Building

You don't need to build the code to use it. If you like you can rebuild with
this command:

```
mvn clean package
```


## License

This code is Copyright (c) 2019 Google LLC, and is released under the Apache Source License v2.0. For information see the [LICENSE](LICENSE) file.

## Disclaimer

This example is not an official Google product, nor is it part of an official Google product.
