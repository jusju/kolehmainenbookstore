package fi.haagahelia.kolehmainenbookstore;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import fi.haagahelia.kolehmainenbookstore.domain.AppUserRepository;
import fi.haagahelia.kolehmainenbookstore.service.UserDetailService;

public class UserDetailServiceTests {

    @Test
    public void unknownUsernameThrowsUsernameNotFoundException() {
        AppUserRepository repository = mock(AppUserRepository.class);

        when(repository.findByUsername("missinguser")).thenReturn(null);

        UserDetailService service =
                new UserDetailService(repository);

        assertThatThrownBy(() -> service.loadUserByUsername("missinguser"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
