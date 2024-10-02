package picpay.integration;

// Importações necessárias para o teste
import io.restassured.http.ContentType;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import Picpaysimplificado.api.dto.PaymentForm;
import picpay.fixture.PaymentFixtures;
import Picpaysimplificado.service.NotificationService;
import Picpaysimplificado.service.TransactionValidatorService;
import Picpaysimplificado.service.dto.NotificationResponse;

import static io.restassured.RestAssured.expect;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest // Indica que este é um teste de integração utilizando o Quarkus
public class ShopkeeperPaymentTest {

    @InjectMock // Injeta um mock do serviço de notificação
    @RestClient
    NotificationService notificationService;

    @InjectMock // Injeta um mock do serviço de validação de transações
    @RestClient
    TransactionValidatorService transactionValidatorService;

    @BeforeEach // Método que será executado antes de cada teste
    void setup() {
        // Define o comportamento do mock para retornar uma resposta padrão
        Mockito.when(notificationService.sendNotification()).thenReturn(new NotificationResponse());
        Mockito.when(transactionValidatorService.validate()).thenReturn(PaymentFixtures.authorizedResponse());
    }

    @Test
    @DisplayName("Shopkeepers should receive payments from users.") // Nome do teste
    void receiveFromUser() {
        // Cria um formulário de pagamento para um usuário pagar um comerciante
        PaymentForm request = PaymentFixtures.userToShopkeeper(100.0);

        // Realiza uma requisição POST e valida a resposta esperada
        given().when().contentType(ContentType.JSON).body(request).post("/transaction").then().log().all()
                .statusCode(200) // Verifica se o status é 200 (OK)
                .body("id", notNullValue(), "timestamp", notNullValue()); // Verifica se o id e o timestamp estão presentes na resposta
    }

    @Test
    @DisplayName("Shopkeepers should not make payments, only receive.") // Nome do teste
    void shoopkeeperInvalidPayment() {
        // Cria formulários de pagamento inválidos (comerciante para comerciante e comerciante para usuário)
        PaymentForm request1 = PaymentFixtures.shopkeeperToShopkeeper(100.0);
        PaymentForm request2 = PaymentFixtures.shopkeeperToUser(100.0);

        // Valida que a primeira requisição retorna um erro 400 com a mensagem apropriada
        expect()
                .statusCode(400)
                .body("error", CoreMatchers.is("Este usuário não pode pagar, apenas receber pagamentos."))
                .given().when().contentType(ContentType.JSON)
                .body(request1)
                .post("/transaction");

        // Valida que a segunda requisição também retorna um erro 400 com a mesma mensagem
        expect()
                .statusCode(400)
                .body("error", CoreMatchers.is("Este usuário não pode pagar, apenas receber pagamentos."))
                .given().when().contentType(ContentType.JSON)
                .body(request2)
                .post("/transaction");
    }
}
