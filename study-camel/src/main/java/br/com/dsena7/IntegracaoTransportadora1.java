package br.com.dsena7;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.GenericFileOperationFailedException;

/*
Aqui trabalhamos com exceção manipulada com o handled(true) e isso impede a execução total da rota, finaliza
onde ocorre a exceção. Usamos o maximumRedeliveries com a quantidade de retentativas de execução da rota.
Usamos o errorHandler para capturar qualquer tipo de exceção, já o onException para exceções específicas.
Com o errorHandler podemos usar o deadLetterChannel para direcionar a chamada de acordo com a necessidade
para uma determinada rota e/ou arquivo.
Podemos usar uma exceção que retenta toda a rota em caso de erro, sem tratamento que seria usando a
propriedade .errorHandler(noErrorHandler())
OBS: Ambas as abordagens de tratamento de exceção podem ser usadas em conjunto.
 */
public class IntegracaoTransportadora1 extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        errorHandler(deadLetterChannel("file:{{diretorioTransportadora1Erro}}")
                .useOriginalMessage()
                .maximumRedeliveries(2));

        onException(GenericFileOperationFailedException.class)
                .useOriginalMessage()
                .handled(true)
                .maximumRedeliveries(2)
                .to("file:{{diretorioTransportadora1Erro}}");

        from("direct:integracaoTransportadora1")
                .routeId("integracao-arquivo-transportadora1")
                .errorHandler(noErrorHandler())
                .to("file:{{diretorioTransportadora1}}?fileName=${date:now:HHmmss}_${file:name}");
    }
}
