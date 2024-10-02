package Picpaysimplificado.api;

// Importa a classe BigDecimal, que é utilizada para operações aritméticas de precisão com números decimais
import java.math.BigDecimal;

// Importa anotações para gerenciar transações e validações
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

// Importa classes de DTO e modelo para o controlador de pagamento
import Picpaysimplificado.api.dto.PaymentForm;
import Picpaysimplificado.api.dto.PaymentResponse;
import Picpaysimplificado.model.Transaction;
import Picpaysimplificado.model.User;

// Define a classe PaymentController como um recurso JAX-RS
@Path("/transaction")  // Mapeia a URL base para transações
@Produces(MediaType.APPLICATION_JSON)  // Define que o controlador produz respostas em formato JSON
@Consumes(MediaType.APPLICATION_JSON)   // Define que o controlador consome requisições em formato JSON
public class PaymentController {

    // Mapeia o método payment para o método HTTP POST
    @POST
    @Transactional  // Indica que o método deve ser executado dentro de uma transação
    public Response payment(@Valid PaymentForm form) {
        // Converte o ID do pagador para um objeto User
        User payer = form.toPayer();
        // Converte o ID do recebedor para um objeto User
        User payee = form.toPayee();
        // Obtém o valor do pagamento
        BigDecimal value = form.value;

        // Realiza a transação de pagamento
        Transaction transaction = payer.pay(value, payee);

        // Retorna uma resposta HTTP com os detalhes da transação em formato JSON
        return Response.ok(new PaymentResponse(transaction)).build();
    }
}
