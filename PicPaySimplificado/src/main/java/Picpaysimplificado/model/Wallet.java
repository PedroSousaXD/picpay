package Picpaysimplificado.model;

import org.postgresql.shaded.com.ongres.scram.common.util.Preconditions; // Importa utilitários para pré-condições
import io.quarkus.hibernate.orm.panache.PanacheEntityBase; // Importa a classe base para entidades Panache

import jakarta.persistence.*; // Importa as anotações JPA
import jakarta.transaction.Transactional; // Importa a anotação para métodos transacionais
import jakarta.validation.constraints.Positive; // Importa a anotação para validação de valores positivos

import java.math.BigDecimal; // Importa a classe BigDecimal para operações financeiras

@Entity // Indica que esta classe é uma entidade JPA
@Table(name = "picpay_wallet") // Define o nome da tabela no banco de dados
public class Wallet extends PanacheEntityBase {

    @Id // Marca este campo como a chave primária
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Define que o ID será gerado pelo banco de dados
    private Long id; // Identificador único da carteira

    private BigDecimal balance; // Saldo atual da carteira

    @OneToOne // Define um relacionamento um-para-um com a entidade User
    private User owner; // Proprietário da carteira

    // Construtor padrão protegido, necessário para JPA
    protected Wallet() {
    }

    // Construtor que permite criar uma nova carteira com saldo inicial e proprietário
    public Wallet(BigDecimal balance, User owner) {
        this.balance = balance; // Inicializa o saldo da carteira
        this.owner = owner; // Define o proprietário da carteira
    }

    @Transactional // Indica que este metodo deve ser executado dentro de uma transação
    public void withdraw(BigDecimal value) {
        // Verifica se o valor a ser retirado é menor ou igual ao saldo
        Preconditions.checkArgument(balance.compareTo(value) > -1, "Saldo insuficiente para completar esta transação.");

        this.balance = balance.subtract(value); // Atualiza o saldo após a retirada
        this.persist(); // Persiste a alteração no banco de dados
    }

    @Transactional // Indica que este metodo deve ser executado dentro de uma transação
    public void deposit(BigDecimal value) {
        // Verifica se o valor a ser depositado é positivo
        Preconditions.checkArgument(value.compareTo(BigDecimal.ZERO) > 0, "Você só pode incrementar valores positivos.");

        this.balance = balance.add(value); // Atualiza o saldo após o depósito
        this.persist(); // Persiste a alteração no banco de dados
    }

    @Transactional // Indica que este metodo deve ser executado dentro de uma transação
    public Transaction transfer(@Positive BigDecimal value, Wallet payeeWallet) {
        // Verifica se o proprietário pode pagar
        Preconditions.checkArgument(this.owner.canPay(), "Este usuário não pode pagar, apenas receber pagamentos.");
        // Verifica se o valor a ser transferido é positivo
        Preconditions.checkArgument(value.compareTo(BigDecimal.ZERO) > 0, "O valor do pagamento deve ser maior que 0.");

        this.withdraw(value); // Retira o valor da carteira de origem
        payeeWallet.deposit(value); // Deposita o valor na carteira de destino

        // Cria uma nova transação para registrar a transferência
        Transaction transaction = new Transaction(this.owner, payeeWallet.owner, value);
        transaction.confirm(); // Confirma a transação

        return transaction; // Retorna a transação criada
    }
}
