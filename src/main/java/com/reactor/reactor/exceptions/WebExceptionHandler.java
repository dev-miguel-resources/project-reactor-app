package com.reactor.reactor.exceptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.web.WebProperties.Resources;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebExceptionHandler extends AbstractErrorWebExceptionHandler {

    // Constructor de la Clase
    // ErrorAtributes: atributos del error capturado
    // Resources: Recursos web usados
    // Context: El contexto de Spring
    // ServerCodeConfigurer: Serializar las respuestas
    public WebExceptionHandler(ErrorAttributes errorAttributes, Resources resources,
            ApplicationContext applicationContext, ServerCodecConfigurer configurer) {
        super(errorAttributes, resources, applicationContext);
        setMessageWriters(configurer.getWriters());// Configurar escrituras de serialización en un formato de salida
    }

    // Define la función que interceptará TODAS las rutas que lancen errores
    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    // Método que genera la respuesta personalizada de los errores
    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {

        // Obtener el error real lanzado por la aplicación
        Throwable error = getError(request);

        // Validamos si es un error de validación de campos
        // WebExchangeBindException: Clase que controla errores en escenarios reactivos
        if (error instanceof WebExchangeBindException validationException) {

            // Convertimos los errores de campos a una lista de mapas con "field" y
            // "message"
            List<Map<String, String>> errors = validationException.getFieldErrors().stream()
                    .map(fieldError -> Map.of(
                            "field", fieldError.getField(), // Nombre del campo con error
                            "message", fieldError.getDefaultMessage() // Mensaje personalizado
                    ))
                    .collect(Collectors.toList());

            // Creamos el cuerpo de respuesta con los errores y el código 400
            Map<String, Object> response = new HashMap<>();
            response.put("errors", errors); // Lista de errores formateadas
            response.put("status", HttpStatus.BAD_REQUEST.value()); // Código de error HTTP

            // Devolvemos la respuesta con los errores en formato JSON y status 400
            return ServerResponse.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(response));
        }

        // Si no es un error de validación, usamos el manejo genérico
        Map<String, Object> generalError = getErrorAttributes(request, ErrorAttributeOptions.defaults());

        Map<String, Object> customError = new HashMap<>();

        // Extraemos el código de estado (status) desde los atributos del error
        int statusCode = Integer.parseInt(String.valueOf(generalError.get("status")));

        // Convertimos el código númerico a HttpStatus (o 500 si es inválido)

        HttpStatus httpStatus = HttpStatus.resolve(statusCode);

        if (httpStatus == null) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        // Incluimos el código de error y el código de la respuesta
        customError.put("message", error.getMessage());
        customError.put("status", httpStatus.value());

        // Devolvemos la respuesta para errores genéricos en formato JSON
        return ServerResponse.status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(customError));
    }

}
