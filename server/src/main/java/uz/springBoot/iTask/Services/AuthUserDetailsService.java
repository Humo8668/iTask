package uz.springBoot.iTask.Services;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import uz.springBoot.iTask.Controllers.UserController;

import java.util.Arrays;
import java.util.List;

@Component
public class AuthUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        uz.springBoot.iTask.DataClass.User user = UserController.getUserByLogin(username);

        if(user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        List<SimpleGrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("admin"));

        return new User(user.getLogin(), user.getPasswordHash(), authorities);
    }
}
