package pl.jlabs.blog.eip.apache.camel.main;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

import pl.jlabs.blog.eip.apache.camel.route.OrderConfirmationRouteBuilder;

public class Main {
    
    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.addRoutes(new OrderConfirmationRouteBuilder());
        context.start();
    }
}
