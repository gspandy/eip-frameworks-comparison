package pl.jlabs.blog.eip.apache.camel.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;

public class OrderConfirmationRouteBuilder extends RouteBuilder {

    private static Namespaces namespaces = new Namespaces("ns", "http://www.j-labs.pl/blog/order");
    
    @Override
    public void configure() throws Exception {
        from("netty4-http:http://0.0.0.0:8080/eip-frameworks-apache-camel/order/confirmation?httpMethodRestrict=POST")
            .convertBodyTo(String.class)
            .doTry()
                .to("validator:order.xsd")
                .to("direct:setProperties")
                .setHeader("Subject").simple("Order confirmation ${exchangeProperty[orderIdentifier]}")
                .setHeader("To").simple("${exchangeProperty[clientName]} ${exchangeProperty[clientSurname]} <${exchangeProperty[clientEmail]}>")
                .setHeader("From").constant("Store admin <admin@store.com>")
                .setHeader("Content-type").constant("text/html; charset=UTF-8")
                .to("xslt:order.xslt")
                .to("smtp://localhost:587?username=admin&password=secret&debugMode=true")
            .doCatch(org.apache.camel.ValidationException.class)
                .to("file:rejected")
            .end();
        
        from("direct:setProperties")
            .setProperty("orderIdentifier").xpath("/ns:order/@identifier", namespaces)
            .setProperty("clientName").xpath("/ns:order/ns:client/ns:name/text()", namespaces)
            .setProperty("clientSurname").xpath("/ns:order/ns:client/ns:surname/text()", namespaces)
            .setProperty("clientEmail").xpath("/ns:order/ns:client/ns:email/text()", namespaces)
            .end();
            
    }
}
