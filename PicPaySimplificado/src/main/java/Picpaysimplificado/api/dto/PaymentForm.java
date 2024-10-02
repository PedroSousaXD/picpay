package Picpaysimplificado.api.dto;

// Importa a classe User do modelo e anotações de validação
import Picpaysimplificado.model.User;
import jakarta.validation.constraints.NotNull; // Para validar que o campo não pode ser nulo
import jakarta.validation.constraints.Positive; // Para validar que o valor deve ser positivo

import java.math.BigDecimal; // Importa a classe BigDecimal para representar valores monetários

// Classe que representa a forma de pagamento
public class PaymentForm {
    // O valor do pagamento deve ser positivo
    @Positive(message = "Payment value must be greater than 0.")
    public BigDecimal value;

    // Identificador do pagador (não pode ser nulo)
    @NotNull
    public Long payer;

    // Identificador do recebedor (não pode ser nulo)
    @NotNull
    public Long payee;

    // Construtor padrão
    public PaymentForm() {
    }

    // Método para obter o usuário que faz o pagamento a partir do ID do pagador
    public User toPayer() {
        return User.findById(this.payer); // Busca o usuário pelo ID
    }

    // Método para obter o usuário que recebe o pagamento a partir do ID do recebedor
    public User toPayee() {
        return User.findById(this.payee); // Busca o usuário pelo ID
    }
}
