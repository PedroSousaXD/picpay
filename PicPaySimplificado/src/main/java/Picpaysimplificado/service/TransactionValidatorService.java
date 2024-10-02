package Picpaysimplificado.service;

// Importa as classes necessárias para a aplicação
import jakarta.enterprise.context.ApplicationScoped; // Para definir o escopo de aplicação
import jakarta.ws.rs.GET; // Para indicar que o método realiza uma requisição GET
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient; // Para registrar a interface como um cliente REST
import Picpaysimplificado.service.dto.TransactionValitadorResponse; // DTO para a resposta da validação

// Indica que esta classe é um bean CDI com escopo de aplicação
@ApplicationScoped
// Registra esta interface como um cliente REST com a chave de configuração "transaction-validator"
@RegisterRestClient(configKey = "transaction-validator")
public interface TransactionValidatorService {

    // Metodo que faz uma chamada GET para validar uma transação
    @GET
    TransactionValitadorResponse validate();
}
