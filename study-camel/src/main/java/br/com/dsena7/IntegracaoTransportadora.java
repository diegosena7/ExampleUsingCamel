//package org.acme.camel;
//
//import org.apache.camel.builder.RouteBuilder;
//import org.apache.camel.component.http.HttpConstants;
//import org.apache.camel.support.builder.Namespaces;
//
///*
//Ao definir o uso do choice, caso após o procedimento da rota ainda for necessário utilizar uma próxima chamada
//deve-se utilizar o .end(). Caso não tenha continuação da chamada não há essa necessidade.
//
//Usando o Throttler podemos controlar a taxa de quantas mensagens ou a rapidez que estão fluindo para o endpoint.
//Assim, garantimos que um determinado endpoint não fique sobrecarregado e perca o SLA.
//
//Ao usarmos dentro do constexto do choice() algum EIP que tenha um retorno podemo perder o contexto do choice()
//e devemos usar o endChoice() para recuperar o seu contexto e seguir a rota.
// */
//public class IntegracaoTransportadora extends RouteBuilder {
//    @Override
//    public void configure() throws Exception {
//        var ns = new Namespaces("ns", "http://www.portalfiscal.inf.br/nfe");
//
//        from("file:{{diretorioDeEntrada}}?delay=5000")
//                .routeId("integracao-arquivo")
//                .log("Processando o arquivo: ${file:name}")
//                .setProperty("CNPJ", xpath("{{xpathCnpjTransportadora}}", ns))
//                .choice()
//                    .when(exchangeProperty("CNPJ").isEqualTo("1"))
//                        .to("file:{{diretorioTransportadora1}}?fileName=${date:now:HHmmss}_${file:name}")
//                    .when(exchangeProperty("CNPJ").isEqualTo("2"))
//                        .throttle(1).timePeriodMillis(5000).asyncDelayed()
//                        .setHeader(HttpConstants.HTTP_METHOD, constant("POST"))
//                        .setHeader(HttpConstants.HTTP_URI, constant("{{urlApiTransportadora2}}"))
//                        .setHeader(HttpConstants.HTTP_PATH, constant("nfes"))
//                        .setHeader(HttpConstants.CONTENT_TYPE).constant("application/xml")
//                        .to("http:servidorTransportadora2")
//                .endChoice()
//                .otherwise()
//                    .log("Transportadora não integrada")
//                .end();
//    }
//}