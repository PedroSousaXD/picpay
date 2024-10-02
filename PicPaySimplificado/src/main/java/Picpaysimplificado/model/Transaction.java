package Picpaysimplificado.model;

// Importa serviços e classes necessárias
import Picpaysimplificado.service.NotificationService;
import Picpaysimplificado.service.TransactionValidatorService;
import Picpaysimplificado.service.dto.NotificationResponse;
import Picpaysimplificado.service.dto.TransactionValitadorResponse;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.NotAuthorizedException;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.hibernate.annotations.CreationTimestamp;
import org.postgresql.shaded.com.ongres.scram.common.util.Preconditions;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

// Anotação que define a classe como uma entidade JPA
@Entity
// Especifica o nome da tabela no banco de dados
@Table(name = "picpay_transaction")
public class Transaction extends PanacheEntityBase {

    // Campo que representa a chave primária da transação
    @Id
    private UUID id;

    // Método que gera um ID único antes da persistência
    @PrePersist
    void generateId() {
        this.id = UUID.randomUUID();
    }

    // Relacionamento Many-to-One com o usuário pagador
    @ManyToOne
    private User payer;

    // Relacionamento Many-to-One com o usuário recebedor
    @ManyToOne
    private User payee;

    // Valor da transação, deve ser positivo
    @Positive
    private BigDecimal value;

    // Timestamp da criação da transação, gerado automaticamente
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime timestamp;

    // Construtor protegido para JPA
    protected Transaction() {
    }

    // Construtor que inicializa a transação com pagador, recebedor e valor
    public Transaction(User payer, User payee, @Positive BigDecimal value) {
        // Valida se o pagador pode realizar o pagamento
        Preconditions.checkArgument(payer.canPay(), "This user can not pay, only receive payments.");
        // Valida se o valor do pagamento é maior que zero
        Preconditions.checkArgument(value.compareTo(BigDecimal.ZERO) > 0, "Payment value must be greater than 0.");

        // Inicializa os campos da transação
        this.payer = payer;
        this.payee = payee;
        this.value = value;
        this.timestamp = LocalDateTime.now();
    }

    // Método que confirma a transação
    @Transactional
    public void confirm() {
        // Injeta os serviços necessários
        TransactionValidatorService validatorService = CDI.current()
                .select(TransactionValidatorService.class, RestClient.LITERAL).get();
        NotificationService notificationService = CDI.current().select(NotificationService.class, RestClient.LITERAL)
                .get();

        // Valida a transação
        TransactionValitadorResponse validate = validatorService.validate();
        // Lança exceção se a transação não for autorizada
        if (!validate.authorized()) {
            throw new NotAuthorizedException("Transaction not authorized.", "auth-service");
        }

        // Persiste a transação no banco de dados
        this.persist();

        // Envia uma notificação de forma assíncrona
        CompletableFuture.runAsync(() -> {
            NotificationResponse notificationResponse = notificationService.sendNotification();
            System.out.println(notificationResponse);
            //implementar logica de retry ou salvar quando a notificação falhar
        });
    }

    // Métodos getters para acessar os campos da transação
    public UUID getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
