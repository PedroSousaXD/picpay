package Picpaysimplificado.model;

// Importações necessárias para JPA, validações e tipos de dados
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Anotação que define a classe como uma entidade JPA
@Entity
// Especifica o nome da tabela no banco de dados
@Table(name = "picpay_user")
public class User extends PanacheEntityBase {

    // Campo que representa a chave primária da tabela
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Validação de e-mail, deve ser único na tabela
    @Email
    @Column(unique = true)
    private String email;

    // Validação de CPF, deve ser único na tabela
    @CPF
    @Column(unique = true)
    private String cpf;

    // Validação de CNPJ, deve ser único na tabela
    @CNPJ
    @Column(unique = true)
    private String cnpj;

    // Senha criptografada do usuário, não pode estar vazia
    @NotEmpty
    private String encryptedPassword;

    // Tipo do usuário, usando enumeração para definir o tipo
    @Enumerated(EnumType.STRING)
    private UserType type;

    // Relacionamento um-para-um com a carteira do usuário
    @OneToOne(mappedBy = "owner", cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Wallet wallet;

    // Timestamp de criação, gerado automaticamente
    @CreationTimestamp
    private LocalDateTime createdAt;

    // Timestamp de atualização, gerado automaticamente
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Construtor protegido para uso interno (JPA)
    protected User() {
    }

    // Construtor que inicializa o usuário com tipo, e-mail, documento e senha
    public User(UserType type, @Email String email, String document, @NotEmpty String encryptedPassword) {
        this.type = type;
        this.email = email;
        this.encryptedPassword = encryptedPassword;

        // Define CPF ou CNPJ com base no tipo de usuário
        if (UserType.DEFAULT.equals(type)) {
            this.cpf = document; // Usuário do tipo DEFAULT utiliza CPF
        } else {
            this.cnpj = document; // Outros tipos utilizam CNPJ
        }
    }

    // Metodo que verifica se o usuário pode realizar pagamentos
    public boolean canPay() {
        return UserType.DEFAULT.equals(this.type);
    }

    // Metodo que realiza um pagamento, transferindo valor da carteira do usuário
    public Transaction pay(BigDecimal value, User payee) {
        Wallet payeeWallet = payee.wallet; // Obtém a carteira do recebedor
        return this.wallet.transfer(value, payeeWallet); // Realiza a transferência
    }

    // Metodos getters para acessar os campos da entidade
    public Long getId() {
        return id;
    }
}