package picpay.fixture; // Pacote onde a classe está localizada

import java.math.BigDecimal; // Importa a classe BigDecimal para manipulação de valores monetários
import java.util.List; // Importa a classe List para trabalhar com listas de objetos

import Picpaysimplificado.api.dto.PaymentForm; // Importa a classe PaymentForm que representa o formulário de pagamento
import Picpaysimplificado.model.User; // Importa a classe User que representa um usuário
import Picpaysimplificado.model.UserType; // Importa a enumeração UserType que define os tipos de usuários
import Picpaysimplificado.service.dto.TransactionValitadorResponse; // Importa a classe de resposta do validador de transações

public class PaymentFixtures {

    // Metodo para criar uma resposta de transação autorizada
    public static TransactionValitadorResponse authorizedResponse() {
        TransactionValitadorResponse response = new TransactionValitadorResponse(); // Instancia a resposta
        response.message = "Autorizado"; // Define a mensagem de autorização

        return response; // Retorna a resposta criada
    }

    // Metodo para criar uma resposta de transação não autorizada
    public static TransactionValitadorResponse unauthorizedResponse() {
        TransactionValitadorResponse response = new TransactionValitadorResponse(); // Instancia a resposta
        response.message = "Denied"; // Define a mensagem de negação

        return response; // Retorna a resposta criada
    }

    // Metodo para criar um PaymentForm a partir dos IDs do pagador e do recebedor e um valor
    public static PaymentForm of(Long idPayer, Long idPayee, double value) {
        PaymentForm form = new PaymentForm(); // Instancia o formulário de pagamento
        form.payer = idPayer; // Define o ID do pagador
        form.payee = idPayee; // Define o ID do recebedor
        form.value = BigDecimal.valueOf(value); // Converte o valor para BigDecimal

        return form; // Retorna o formulário criado
    }

    // Metodo para criar um PaymentForm de um usuário para outro usuário
    public static PaymentForm userToUser(double ammount) {
        List<User> users = User.list("type", UserType.DEFAULT); // Busca usuários do tipo DEFAULT
        if (users.size() < 2) { // Verifica se há pelo menos 2 usuários
            throw new IllegalStateException("There is no 2 users registered in database to make this payment."); // Lança exceção se não houver
        }

        long idPayer = users.get(0).getId(); // Obtém o ID do primeiro usuário
        long idPayee = users.get(1).getId(); // Obtém o ID do segundo usuário

        return of(idPayer, idPayee, ammount); // Cria e retorna o PaymentForm
    }

    // Metodo para criar um PaymentForm de um usuário para um comerciante
    public static PaymentForm userToShopkeeper(double ammount) {
        User client = User.find("type", UserType.DEFAULT).firstResult(); // Busca o primeiro usuário do tipo DEFAULT
        User shoopkeeper = User.find("type", UserType.SHOPKEEPER).firstResult(); // Busca o primeiro comerciante

        return of(client.getId(), shoopkeeper.getId(), ammount); // Cria e retorna o PaymentForm
    }

    // Metodo para criar um PaymentForm de um comerciante para um usuário
    public static PaymentForm shopkeeperToUser(double ammount) {
        User client = User.find("type", UserType.DEFAULT).firstResult(); // Busca o primeiro usuário do tipo DEFAULT
        User shoopkeeper = User.find("type", UserType.SHOPKEEPER).firstResult(); // Busca o primeiro comerciante

        return of(shoopkeeper.getId(), client.getId(), ammount); // Cria e retorna o PaymentForm
    }

    // Metodo para criar um PaymentForm de um comerciante para outro comerciante
    public static PaymentForm shopkeeperToShopkeeper(double ammount) {
        List<User> shoopkeepers = User.list("type", UserType.SHOPKEEPER); // Busca todos os comerciantes
        if (shoopkeepers.size() < 2) { // Verifica se há pelo menos 2 comerciantes
            throw new IllegalStateException("There is no 2 shoopkeepers registered in database to make this payment."); // Lança exceção se não houver
        }

        return of(shoopkeepers.get(0).getId(), shoopkeepers.get(1).getId(), ammount); // Cria e retorna o PaymentForm
    }

}
