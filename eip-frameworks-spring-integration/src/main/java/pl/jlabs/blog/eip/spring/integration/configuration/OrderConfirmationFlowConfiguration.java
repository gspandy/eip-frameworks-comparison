package pl.jlabs.blog.eip.spring.integration.configuration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSelector;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.file.Files;
import org.springframework.integration.dsl.http.Http;
import org.springframework.integration.dsl.mail.Mail;
import org.springframework.integration.dsl.support.Transformers;
import org.springframework.integration.mail.MailHeaders;
import org.springframework.integration.transformer.support.HeaderValueMessageProcessor;
import org.springframework.integration.xml.selector.XmlValidatingMessageSelector;
import org.springframework.integration.xml.selector.XmlValidatingMessageSelector.SchemaType;

import pl.jlabs.blog.eip.spring.integration.component.CustomXPathHeaderValueMessageProcessor;

@Configuration
@EnableIntegration
public class OrderConfirmationFlowConfiguration {

    @Value("classpath:/order.xsd")
    private Resource orderXsd;
    
    @Value("classpath:/order.xslt")
    private Resource orderXslt;
    
    @Value("#{{ns:'http://www.j-labs.pl/blog/order'}}")
    private Map<String, String> namespaces;
    
    @Bean
    public IntegrationFlow sendConfirmationMailFlow() throws IOException {
        return IntegrationFlows.from(Http.inboundChannelAdapter("/order/confirmation")
                                .requestMapping(r -> r.methods(HttpMethod.POST)))
                .filter(xmlValidatingMessageSelector(), e -> e.discardFlow(
                                f -> f.handle(Files.outboundAdapter(new File("rejected")))))
                .enrichHeaders(h -> h.header(MailHeaders.SUBJECT, subjectMailHeaderValueMessageProcessor())
                                .header(MailHeaders.TO, fromMailHeaderValueMessageProcessor())
                                .header(MailHeaders.FROM, "Store admin <admin@store.com>")
                                .header(MailHeaders.CONTENT_TYPE, "text/html; charset=UTF-8"))
                .transform(Transformers.xslt(orderXslt))
                .handle(Mail.outboundAdapter("localhost")
                                .port(587)
                                .credentials("user", "pass")
                                .protocol("smtp")
                                .javaMailProperties(p -> p.put("mail.debug", "true")),
                        e -> e.id("sendMailEndpoint"))
                .get();
    }
    
    @Bean
    public MessageSelector xmlValidatingMessageSelector() throws IOException {
        return new XmlValidatingMessageSelector(orderXsd, SchemaType.XML_SCHEMA);
    }
    
    @Bean
    public HeaderValueMessageProcessor<String> fromMailHeaderValueMessageProcessor() {
        return new CustomXPathHeaderValueMessageProcessor("%s %s <%s>", namespaces, 
                "/ns:order/ns:client/ns:name", 
                "/ns:order/ns:client/ns:surname", 
                "/ns:order/ns:client/ns:email");
    }
    
    @Bean
    public HeaderValueMessageProcessor<String> subjectMailHeaderValueMessageProcessor() {
        return new CustomXPathHeaderValueMessageProcessor("Order confirmation %s", namespaces, 
                "/ns:order/@identifier");
    }
}
