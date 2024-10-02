package Picpaysimplificado.service.dto;

// Classe que representa a resposta de uma notificação
public class NotificationResponse {

    // Mensagem da notificação
    public String message;

    // Construtor padrão
    public NotificationResponse() {
    }

    // Método para representar a classe como uma String
    @Override
    public String toString() {
        return "NotificationResponse [message=" + message + "]";
    }
}
