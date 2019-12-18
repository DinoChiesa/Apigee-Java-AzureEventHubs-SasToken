package com.google.apigee.edgecallouts.azureeventhubs;

import com.apigee.flow.execution.ExecutionContext;
import com.apigee.flow.execution.ExecutionResult;
import com.apigee.flow.message.MessageContext;
import java.util.HashMap;
import java.util.Map;
import mockit.Mock;
import mockit.MockUp;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class TestSasCallout {
  MessageContext msgCtxt;
  ExecutionContext exeCtxt;

  @BeforeTest()
  public void testSetup1() {

    msgCtxt =
        new MockUp<MessageContext>() {
          private Map<String,Object> variables;

          public void $init() {
              variables = new HashMap<String,Object>();
          }

          @Mock()
          @SuppressWarnings("unchecked")
          public <T> T getVariable(final String name) {
            if (variables == null) {
              variables = new HashMap<String,Object>();
            }
            T value = (T) variables.get(name);
            System.out.printf("getVariable(%s) ==> %s\n", name, (value!=null)?value.toString():"null");
            return (T) variables.get(name);
          }

          @Mock()
          @SuppressWarnings("unchecked")
          public boolean setVariable(final String name, final Object value) {
            if (variables == null) {
              variables = new HashMap<String,Object>();
            }
            if (name.endsWith(".stacktrace")) {
              System.out.printf("setVariable(%s, %s)\n", name, value.toString().substring(0,56)+"...");
            }
            else {
              System.out.printf("setVariable(%s, %s)\n", name, value.toString());
            }
            variables.put(name, value);
            return true;
          }

          @Mock()
          public boolean removeVariable(final String name) {
            if (variables == null) {
              variables = new HashMap<String,Object>();
            }
            if (variables.containsKey(name)) {
              variables.remove(name);
            }
            return true;
          }
        }.getMockInstance();

    exeCtxt = new MockUp<ExecutionContext>() {}.getMockInstance();
  }

  @Test()
  public void noKey() {
    Map<String,Object> m = new HashMap<String,Object>();
    m.put("resource-uri", "contoso.servicebus.windows.net/");
    SasCallout callout = new SasCallout(m);
    ExecutionResult result = callout.execute(msgCtxt, exeCtxt);

    String error = msgCtxt.getVariable("sas.error");

    Assert.assertEquals(result, ExecutionResult.ABORT);
    Assert.assertEquals(error, "key resolves to null or empty.");
  }

  @Test()
  public void noKeyName() {
    Map<String,Object> m = new HashMap<String,Object>();
    m.put("key", "A3AB6FEC-972B-4F5D-B99F-9DC5AAF83698-390AB533-9D76-4351-AEDD-020EC3057A11");
    m.put("resource-uri", "contoso.servicebus.windows.net/");
    SasCallout callout = new SasCallout(m);
    ExecutionResult result = callout.execute(msgCtxt, exeCtxt);

    String error = msgCtxt.getVariable("sas.error");

    Assert.assertEquals(result, ExecutionResult.ABORT);
    Assert.assertEquals(error, "configuration error: key-name resolves to an empty string");
  }

  @Test()
  public void noExpiry() {
    Map<String,Object> m = new HashMap<String,Object>();
    m.put("key", "A3AB6FEC-972B-4F5D-B99F-9DC5AAF83698-390AB533-9D76-4351-AEDD-020EC3057A11");
    m.put("key-name", "key1");
    m.put("resource-uri", "contoso.servicebus.windows.net/");
    SasCallout callout = new SasCallout(m);
    ExecutionResult result = callout.execute(msgCtxt, exeCtxt);

    String error = msgCtxt.getVariable("sas.error");

    Assert.assertEquals(result, ExecutionResult.ABORT);
    Assert.assertEquals(error, "configuration error: expiry resolves to an empty string");
  }

  @Test()
  public void good1() {
    Map<String,Object> m = new HashMap<String,Object>();
    m.put("key", "A3AB6FEC-972B-4F5D-B99F-9DC5AAF83698-390AB533-9D76-4351-AEDD-020EC3057A11");
    m.put("key-name", "key1");
    m.put("expiry", "7d");
    m.put("debug", "true");
    m.put("resource-uri", "contoso.servicebus.windows.net/");
    SasCallout callout = new SasCallout(m);
    ExecutionResult result = callout.execute(msgCtxt, exeCtxt);

    String error = msgCtxt.getVariable("sas.error");
    String token = msgCtxt.getVariable("sas.token");

    Assert.assertEquals(result, ExecutionResult.SUCCESS);
    Assert.assertNull(error);
    Assert.assertNotNull(token);
  }

  @Test()
  public void good2() {
    Map<String,Object> m = new HashMap<String,Object>();
    m.put("key", "Z1HrZzMnicrAJcxXpyEIieys1qNLGVVuR9mDL4U7mCE=");
    m.put("debug", "true");
    m.put("key-name", "default");
    m.put("expiry", "1d");
    m.put("resource-uri", "sas-test.servicebus.windows.net/sample");
    SasCallout callout = new SasCallout(m);
    ExecutionResult result = callout.execute(msgCtxt, exeCtxt);

    String error = msgCtxt.getVariable("sas.error");
    String token = msgCtxt.getVariable("sas.token");

    Assert.assertEquals(result, ExecutionResult.SUCCESS);
    Assert.assertNull(error);
    Assert.assertNotNull(token);
  }

  @Test()
  public void goodKnownSig() {
    String expectedToken = "SharedAccessSignature sr=sas-test.servicebus.windows.net%2Fsample&sig=sO2NhIxteAcFwiF0lV6FEJD%2BUR8lHCOZy5y3TG7IMHM%3D&se=1577124641&skn=default";
    Map<String,Object> m = new HashMap<String,Object>();
    m.put("key", "Z1HrZzMnicrAJcxXpyEIieys1qNLGVVuR9mDL4U7mCE=");

    m.put("debug", "true");
    m.put("key-name", "default");
    m.put("expiry", "7d");
    m.put("reference-time", "1576519841"); // without this, the policy uses "now"
    m.put("resource-uri", "sas-test.servicebus.windows.net/sample");
    SasCallout callout = new SasCallout(m);
    ExecutionResult result = callout.execute(msgCtxt, exeCtxt);

    String error = msgCtxt.getVariable("sas.error");
    String token = msgCtxt.getVariable("sas.token");

    Assert.assertEquals(result, ExecutionResult.SUCCESS);
    Assert.assertNull(error);
    Assert.assertNotNull(token);
    Assert.assertEquals(token, expectedToken);
  }
}
