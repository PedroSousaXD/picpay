package Picpaysimplificado.service.dto;

// Classe que representa a resposta do validador de transações
public class TransactionValitadorResponse {

    // Mensagem que indica o status da autorização
    public String message;

    // Construtor padrão
    public TransactionValitadorResponse() {
    }

    // Método que verifica se a transação foi autorizada
    public boolean authorized() {
        return "Authorized".equalsIgnoreCase(message);
    }
}
