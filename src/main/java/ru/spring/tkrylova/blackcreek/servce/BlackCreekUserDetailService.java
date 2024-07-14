package ru.spring.tkrylova.blackcreek.servce;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekUser;
import ru.spring.tkrylova.blackcreek.repository.BlackCreekRepository;

import java.util.Set;

@Service
public class BlackCreekUserDetailService implements UserDetailsService {
    final private BlackCreekRepository blackCreekRepository;

    public BlackCreekUserDetailService(BlackCreekRepository blackCreekRepository) {
        this.blackCreekRepository = blackCreekRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        BlackCreekUser blackCreekUser = blackCreekRepository.findByLogin(login).orElseThrow(()->new UsernameNotFoundException(""));
        GrantedAuthority authority = new SimpleGrantedAuthority(blackCreekUser.getUserRole().getRoleType().name());
        return new User(login, blackCreekUser.getPassword(), Set.of(authority));
    }
}
