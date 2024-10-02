package Picpaysimplificado.service;

// Importações necessárias
import jakarta.enterprise.context.ApplicationScoped; // Anotação para definir o escopo da aplicação
import jakarta.ws.rs.GET; // Anotação para definir um método HTTP GET
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient; // Anotação para registrar um cliente REST

import Picpaysimplificado.service.dto.NotificationResponse; // Importa o DTO que representa a resposta de notificação

// Define a interface NotificationService como um cliente REST
@ApplicationScoped // Indica que a classe deve ser tratada como um bean de escopo de aplicação
@RegisterRestClient(configKey = "notification") // Registra a interface como um cliente REST com a configuração especificada
public interface NotificationService {

    // Define um metodo que faz uma chamada GET para enviar uma notificação
    @GET
    NotificationResponse sendNotification();
}
