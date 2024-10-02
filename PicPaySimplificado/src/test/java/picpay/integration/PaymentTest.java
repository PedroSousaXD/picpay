package picpay.integration; // Pacote onde a classe está localizada

import static io.restassured.RestAssured.given; // Importa métodos estáticos da biblioteca RestAssured para realizar requisições
import static org.hamcrest.Matchers.hasItem; // Importa matchers para validação de itens em coleções
import static org.hamcrest.Matchers.is; // Importa matcher para comparação de igualdade

import org.eclipse.microprofile.rest.client.inject.RestClient; // Importa a anotação para injeção de cliente REST
import org.junit.jupiter.api.BeforeEach; // Importa a anotação para metodo que será executado antes de cada teste
import org.junit.jupiter.api.DisplayName; // Importa a anotação para definir um nome descritivo para os testes
import org.junit.jupiter.api.Test; // Importa a anotação para métodos de teste
import org.mockito.Mockito; // Importa a biblioteca Mockito para criar mocks

import io.quarkus.test.junit.QuarkusTest; // Importa a anotação para teste com Quarkus
import io.quarkus.test.InjectMock; // Importa a anotação para injeção de mocks
import io.restassured.http.ContentType; // Importa a classe para definir o tipo de conteúdo nas requisições
import Picpaysimplificado.api.dto.PaymentForm; // Importa a classe PaymentForm que representa o formulário de pagamento
import picpay.fixture.PaymentFixtures; // Importa a classe que contém métodos para criar dados de teste
import Picpaysimplificado.service.NotificationService; // Importa a interface do serviço de notificação
import Picpaysimplificado.service.TransactionValidatorService; // Importa a interface do serviço de validação de transações
import Picpaysimplificado.service.dto.NotificationResponse; // Importa a classe de resposta do serviço de notificação

@QuarkusTest // Anotação que indica que a classe é um teste Quarkus
public class PaymentTest {

  @InjectMock // Anotação para injetar um mock do NotificationService
  @RestClient // Indica que o mock é um cliente REST
  NotificationService notificationService;

  @InjectMock // Anotação para injetar um mock do TransactionValidatorService
  @RestClient // Indica que o mock é um cliente REST
  TransactionValidatorService transactionValidatorService;

  // Método que será executado antes de cada teste
  @BeforeEach
  void setup() {
    // Configura o comportamento do mock de notificação para retornar uma nova NotificationResponse
    Mockito.when(notificationService.sendNotification()).thenReturn(new NotificationResponse());
    // Configura o comportamento do mock de validação para retornar uma resposta de autorização
    Mockito.when(transactionValidatorService.validate()).thenReturn(PaymentFixtures.authorizedResponse());
  }

  // Teste para verificar se a transação é rejeitada quando o usuário não tem saldo suficiente
  @Test
  @DisplayName("Se o usuário não tiver uma quantia suficiente de dinheiro, a transação não será aceita e o usuário receberá um código de status HTTP 400.")
  public void insufficientMoney() {
    PaymentForm userToUser = PaymentFixtures.userToUser(10000.0); // Cria um formulário de pagamento com valor alto

    given().when().contentType(ContentType.JSON) // Inicia a requisição
      .body(userToUser) // Define o corpo da requisição com o PaymentForm
      .post("/transaction") // Faz um POST na rota /transaction
      .then() // Inicia a verificação da resposta
      .log().all() // Exibe os logs da requisição e resposta
      .statusCode(400) // Verifica se o status da resposta é 400 (Bad Request)
      .body("error", is("Dinheiro insuficiente para concluir esta transação.")); // Verifica a mensagem de erro
  }

  // Teste para verificar se a transação não é autorizada quando o serviço de validação não a aceita
  @Test
  @DisplayName("Se o serviço de validação de transações não autorizar um pagamento, a transação não deve ser concluída. E o usuário deve receber um status HTTP 401.")
  public void testUnauthorizedTransaction() {
    Mockito.reset(transactionValidatorService); // Reseta o mock do serviço de validação
    // Configura o comportamento do mock para retornar uma resposta de negação
    Mockito.when(transactionValidatorService.validate()).thenReturn(PaymentFixtures.unauthorizedResponse());

    PaymentForm userToUser = PaymentFixtures.userToUser(100.0); // Cria um formulário de pagamento com valor válido

    given().when().contentType(ContentType.JSON) // Inicia a requisição
      .body(userToUser) // Define o corpo da requisição com o PaymentForm
      .post("/transaction") // Faz um POST na rota /transaction
      .then() // Inicia a verificação da resposta
      .log().all() // Exibe os logs da requisição e resposta
      .statusCode(401) // Verifica se o status da resposta é 401 (Unauthorized)
      .body("error", is("Transaction not authorized.")); // Verifica a mensagem de erro
  }

  // Teste para verificar se o valor do pagamento deve ser maior que 0
  @Test
  @DisplayName("O valor do pagamento deve ser maior que 0")
  void invalidAmmout() {
    PaymentForm userToUser = PaymentFixtures.userToUser(0.0); // Cria um formulário de pagamento com valor 0

    given().when().contentType(ContentType.JSON) // Inicia a requisição
      .body(userToUser) // Define o corpo da requisição com o PaymentForm
      .post("/transaction") // Faz um POST na rota /transaction
      .then() // Inicia a verificação da resposta
      .log().all() // Exibe os logs da requisição e resposta
      .statusCode(400) // Verifica se o status da resposta é 400 (Bad Request)
      .body("parameterViolations.message", hasItem("O valor do pagamento deve ser maior que 0.")); // Verifica a mensagem de erro
  }
}
