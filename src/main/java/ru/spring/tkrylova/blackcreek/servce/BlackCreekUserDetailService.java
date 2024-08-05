package ru.spring.tkrylova.blackcreek.servce;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekUser;
import ru.spring.tkrylova.blackcreek.repository.BlackCreekUserRepository;

import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class BlackCreekUserDetailService implements UserDetailsService {
    final private BlackCreekUserRepository blackCreekUserRepository;

    public BlackCreekUserDetailService(BlackCreekUserRepository blackCreekUserRepository) {
        this.blackCreekUserRepository = blackCreekUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        BlackCreekUser blackCreekUser = blackCreekUserRepository.findByLogin(login).orElseThrow(() -> {
            log.atError().log("User not found");
            return new UsernameNotFoundException("");
        });
        GrantedAuthority authority = new SimpleGrantedAuthority(blackCreekUser.getUserRole().getRoleType().name());
        return new User(login, blackCreekUser.getPassword(), Set.of(authority));
    }

    public Optional<BlackCreekUser> getUserByLogin(String login) {
        return blackCreekUserRepository.findByLogin(login);
    }
}
