package Picpaysimplificado.api.dto;

// Importa a classe Transaction do modelo
import Picpaysimplificado.model.Transaction;

// Importa a classe LocalDateTime, que representa uma data e hora sem fuso horário
import java.time.LocalDateTime;

// Classe que representa a resposta de um pagamento
public class PaymentResponse {

    // ID da transação
    public final String id;

    // Timestamp que indica quando a transação foi criada
    public final LocalDateTime timestamp;

    // Construtor que recebe uma transação e inicializa os campos da resposta
    public PaymentResponse(Transaction transaction) {
        // Converte o ID da transação para String
        this.id = transaction.getId().toString();
        // Obtém o timestamp da transação
        this.timestamp = transaction.getTimestamp();
    }
}
