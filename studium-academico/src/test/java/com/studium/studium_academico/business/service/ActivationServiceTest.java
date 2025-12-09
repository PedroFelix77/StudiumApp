import com.studium.studium_academico.business.service.ActivationService;
import com.studium.studium_academico.business.service.EmailService;
import com.studium.studium_academico.infrastructure.entity.ActivationToken;
import com.studium.studium_academico.infrastructure.entity.Users;
import com.studium.studium_academico.infrastructure.repository.ActivationTokenRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;

@SpringBootTest(classes = ActivationService.class) // Carrega apenas o necessário
@Transactional // Ativa transações para o teste
class ActivationServiceTest {

    @Autowired
    private ActivationService activationService;

    @MockBean
    private ActivationTokenRepository activationTokenRepository;

    @MockBean
    private EmailService emailService;

    @Test
    void generateAndSendActivationLink_shouldSaveTokenAndCallEmail() {
        // Arrange
        Users user = new Users();
        user.setEmail("test@example.com");
        user.setId(UUID.randomUUID());

        ActivationToken expectedToken = new ActivationToken();
        expectedToken.setToken("mock-token-123");
        expectedToken.setUser(user);
        expectedToken.setExpiryDate(LocalDateTime.now().plusDays(1));

        when(activationTokenRepository.save(any(ActivationToken.class)))
                .thenReturn(expectedToken);

        // Act
        activationService.generateAndSendActivationLink(user);

        // Assert
        verify(activationTokenRepository, times(1))
                .save(any(ActivationToken.class));

        // O email deve ser enviado após o commit da transação
        // Para testar isso, podemos forçar o commit
        TestTransaction.flagForCommit();
        TestTransaction.end();

    }
}