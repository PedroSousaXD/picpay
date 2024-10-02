package picpay.integration;

// Importações necessárias para o teste
import static org.hamcrest.CoreMatchers.notNullValue;
import static io.restassured.RestAssured.given;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import io.restassured.http.ContentType;
import Picpaysimplificado.api.dto.PaymentForm;
import picpay.fixture.PaymentFixtures;
import Picpaysimplificado.service.NotificationService;
import Picpaysimplificado.service.TransactionValidatorService;
import Picpaysimplificado.service.dto.NotificationResponse;

// Indica que esta é uma classe de teste Quarkus
@QuarkusTest
public class UserPaymentTest {

    @InjectMock // Injeta um mock do serviço de notificação
    @RestClient
    NotificationService notificationService;

    @InjectMock // Injeta um mock do serviço de validação de transações
    @RestClient
    TransactionValidatorService transactionValidatorService;

    // Método executado antes de cada teste
    @BeforeEach
    public void setup() {
        // Configura o comportamento do mock para retornar uma resposta padrão para as notificações
        Mockito.when(notificationService.sendNotification()).thenReturn(new NotificationResponse());
        // Configura o comportamento do mock para validar a transação como autorizada
        Mockito.when(transactionValidatorService.validate()).thenReturn(PaymentFixtures.authorizedResponse());
    }

    // Teste que verifica se um usuário pode pagar a outro usuário, dado que ele tem dinheiro suficiente
    @Test
    @DisplayName("An user should pay other users if he has sufficient money.") // Nome do teste
    public void testUserToUserPayment() {
        // Cria um formulário de pagamento onde um usuário paga outro usuário
        PaymentForm userToUser = PaymentFixtures.userToUser(100.0);

        // Realiza a requisição HTTP POST
        given()
                .contentType(ContentType.JSON) // Define o tipo de conteúdo da requisição como JSON
                .body(userToUser) // Define o corpo da requisição com o formulário de pagamento
                .when()
                .post("/transaction") // Faz a chamada para o endpoint de transação
                .then()
                .log().all() // Loga os detalhes da resposta para facilitar a depuração
                .statusCode(200) // Verifica se o status da resposta é 200 (OK)
                .body(
                        "id", notNullValue(), // Verifica se o "id" da transação não é nulo
                        "timestamp", notNullValue() // Verifica se o "timestamp" da transação não é nulo
                );
    }

    // Teste que verifica se um usuário pode pagar a um comerciante, dado que ele tem dinheiro suficiente
    @Test
    @DisplayName("An user should pay shopkeepers if he has sufficient money.") // Nome do teste
    public void testUserToShopkeeperPayment() {
        // Cria um formulário de pagamento onde um usuário paga a um comerciante
        PaymentForm userToUser = PaymentFixtures.userToShopkeeper(100.0);

        // Realiza a requisição HTTP POST
        given().when().contentType(ContentType.JSON)
                .body(userToUser) // Define o corpo da requisição
                .post("/transaction") // Faz a chamada para o endpoint de transação
                .then()
                .log().all() // Loga os detalhes da resposta
                .statusCode(200) // Verifica se o status da resposta é 200 (OK)
                .body(
                        "id", notNullValue(), // Verifica se o "id" da transação não é nulo
                        "timestamp", notNullValue() // Verifica se o "timestamp" da transação não é nulo
                );
    }

}