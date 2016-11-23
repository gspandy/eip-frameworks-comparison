package pl.jlabs.blog.eip.spring.integration.component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.springframework.integration.transformer.support.AbstractHeaderValueMessageProcessor;
import org.springframework.integration.xml.DefaultXmlPayloadConverter;
import org.springframework.integration.xml.XmlPayloadConverter;
import org.springframework.messaging.Message;
import org.springframework.xml.xpath.XPathExpressionFactory;
import org.w3c.dom.Node;

public class CustomXPathHeaderValueMessageProcessor extends AbstractHeaderValueMessageProcessor<String> {

    private static final XmlPayloadConverter converter = new DefaultXmlPayloadConverter();
    
    private String format;
    private Map<String, String> namespaces;
    private String[] xPathExpressions;

    public CustomXPathHeaderValueMessageProcessor(String format, String... xPathExpressions) {
        this(format, Collections.emptyMap(), xPathExpressions);
    }
    
    public CustomXPathHeaderValueMessageProcessor(String format, Map<String, String> namespaces,
            String... xPathExpressions) {
        this.format = format;
        this.namespaces = namespaces;
        this.xPathExpressions = xPathExpressions;
    }
    
    @Override
    public String processMessage(Message<?> message) {
        Node node = converter.convertToNode(message.getPayload());
        Object[] results = Arrays.asList(xPathExpressions)
                .stream()
                .map(expr -> XPathExpressionFactory.createXPathExpression(expr, namespaces))
                .map(expr -> expr.evaluateAsString(node))
                .toArray(size -> new String[size]);
        return String.format(format, results);
    }

}
