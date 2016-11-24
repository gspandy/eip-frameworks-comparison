# EIP Frameworks comparison

## Application flow diagram

![Send confirmation email flow][flow-diagram]

[flow-diagram]: eip-frameworks-resources/doc/SendConfirmationMailFlow.png

## Running the application

### Spring Integration (eip-frameworks-spring-integration subproject)

1. Check the email settings in the OrderConfirmationFlowConfiguration class
2. Build the application war and deploy it to your favourite servlet container
3. Use Curl too POST the request (use appropriate host name and port if needed):

```curl -i -H "Content-Type: text/xml; charset=UTF-8" -X POST -T order.xml http://localhost:8080/eip-frameworks-spring-integration/order/confirmation```

### Apache Camel (eip-frameworks-apache-camel subproject)

1. Check the email settings in the OrderConfirmationRouteBuilder class
2. Build and run the application using main method from Main class
3. Use Curl too POST the request:

```curl -i -H "Content-Type: text/xml; charset=UTF-8" -X POST -T order.xml http://localhost:8080/eip-frameworks-apache-camel/order/confirmation```