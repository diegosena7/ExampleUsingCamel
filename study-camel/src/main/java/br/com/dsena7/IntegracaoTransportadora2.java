package br.com.dsena7;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http.HttpConstants;
import org.apache.camel.http.base.HttpOperationFailedException;

/*
Aqui trabalhamos com exceção manipulada com o handled(true) e isso impede a execução total da rota, finaliza
onde ocorre a exceção. Usamos o maximumRedeliveries com a quantidade de retentativas de execução da rota.
Podemos usar uma propriedade chamada redeliveryDelay(1) que por padrão é de 1 segundo
Usamos o useOriginalMessage() que não altera o padrão da msg, mesmo que haja alterações
Caso haja necessidade de manter a continuidade da rota mesmo com erro podemos usar a propriedade continued(true)
 */
public class IntegracaoTransportadora2 extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        onException(HttpOperationFailedException.class)
                .useOriginalMessage()
//                .continued(true)
                .handled(true)
                .maximumRedeliveries(2)
                .redeliveryDelay(1)
                .to("file:{{diretorioTransportadora2Erro}}")
                .process(exchange -> {
                    var excecao = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, HttpOperationFailedException.class);
                    exchange.getMessage().setBody(excecao.getResponseBody());
                })
                .to("file:{{diretorioTransportadora2Erro}}?fileName=${file:name}.erro");

        from("direct:integracaoTransportadora2")
                .routeId("integracao-arquivo-transportadora2")
                .throttle(1).timePeriodMillis(5000).asyncDelayed()
                .setHeader(HttpConstants.HTTP_METHOD, constant("POST"))
                .setHeader(HttpConstants.HTTP_URI, constant("{{urlApiTransportadora2}}"))
                .setHeader(HttpConstants.HTTP_PATH, constant("nfes"))
                .setHeader(HttpConstants.CONTENT_TYPE).constant("application/xml")
                .to("http:servidorTransportadora2");
    }
}
