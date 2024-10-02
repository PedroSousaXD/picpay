package Picpaysimplificado.config;

// Importa exceções específicas e classes necessárias do JAX-RS
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

// Importa classes para manipulação de coleções
import java.util.HashMap;
import java.util.Map;

// Anotação que indica que esta classe é um provedor de exceções
@Provider
public class ErrorsHandler implements ExceptionMapper<Exception> {

    // Método que mapeia exceções para respostas HTTP
    @Override
    public Response toResponse(Exception exception) {

        // Define o status padrão da resposta como erro interno do servidor
        Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;

        // Cria um mapa para armazenar informações sobre o erro
        Map<String, String> error = new HashMap<>();

        // Adiciona a mensagem da exceção ao mapa
        error.put("error", exception.getMessage());

        // Verifica o tipo da exceção para definir o status apropriado
        if (exception instanceof IllegalArgumentException) {
            // Se for um argumento ilegal, define o status como BAD_REQUEST (400)
            status = Response.Status.BAD_REQUEST;
        } else if (exception instanceof NotAuthorizedException) {
            // Se a exceção for de não autorização, define o status como UNAUTHORIZED (401)
            status = Response.Status.UNAUTHORIZED;
        }

        // Retorna a resposta com o status e o corpo do erro em formato JSON
        return Response.status(status).entity(error).build();
    }

}
