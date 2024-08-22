package ru.spring.tkrylova.blackcreek.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekUser;
import ru.spring.tkrylova.blackcreek.servce.BlackCreekUserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegistrationController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class RegistrationControllerTest {

    @MockBean
    private BlackCreekUserService blackCreekUserService;

    @InjectMocks
    private RegistrationController registrationController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void showRegistrationForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("account/register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    public void registerUser_Success() throws Exception {
        String pwd = "Sassword1!";
        BlackCreekUser user = new BlackCreekUser();
        user.setLogin("testuser");
        user.setEmail("testuser@mail.com");
        user.setPassword(pwd);
        user.setConfirmPassword(pwd);

        when(blackCreekUserService.isLoginTaken(user.getLogin())).thenReturn(false);
        when(blackCreekUserService.isEmailTaken(user.getEmail())).thenReturn(false);
        when(blackCreekUserService.saveUser(any(BlackCreekUser.class))).thenReturn(user);

        mockMvc.perform(post("/register")
                        .param("login", user.getLogin())
                        .param("email", user.getEmail())
                        .param("firstName", user.getLogin())
                        .param("lastName", user.getLogin())
                        .param("password", user.getPassword())
                        .param("confirmPassword", user.getConfirmPassword()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(blackCreekUserService).saveUser(any(BlackCreekUser.class));
    }

    @Test
    public void registerUser_LoginTaken() throws Exception {
        String pwd = "password";
        BlackCreekUser user = new BlackCreekUser();
        user.setLogin("testuser");
        user.setPassword(pwd);
        user.setConfirmPassword(pwd);

        when(blackCreekUserService.isLoginTaken(user.getLogin())).thenReturn(true);

        mockMvc.perform(post("/register")
                        .param("login", user.getLogin())
                        .param("password", user.getPassword())
                        .param("confirmPassword", user.getConfirmPassword()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/register"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Login is already taken."));
    }

    @Test
    public void registerUser_EmailAlreadyTaken() throws Exception {
        String pwd = "password";
        BlackCreekUser user = new BlackCreekUser();
        user.setLogin("testuser");
        user.setEmail("test@example.com");
        user.setPassword(pwd);
        user.setConfirmPassword(pwd);

        when(blackCreekUserService.isLoginTaken(user.getLogin())).thenReturn(false);
        when(blackCreekUserService.isEmailTaken(user.getEmail())).thenReturn(true);

        mockMvc.perform(post("/register")
                        .param("login", user.getLogin())
                        .param("email", user.getEmail())
                        .param("password", user.getPassword())
                        .param("confirmPassword", user.getConfirmPassword()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/register"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Email is already taken."));
    }

    @Test
    public void registerUser_PasswordsDoNotMatch() throws Exception {
        BlackCreekUser user = new BlackCreekUser();
        user.setLogin("testuser");
        user.setFirstName("test");
        user.setLastName("user");
        user.setLogin("testuser@mail.com");
        user.setPassword("password");
        user.setConfirmPassword("differentpassword");

        mockMvc.perform(post("/register")
                        .param("login", user.getLogin())
                        .param("email", user.getEmail())
                        .param("password", user.getPassword())
                        .param("confirmPassword", user.getConfirmPassword()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/register"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Passwords do not match."));
    }

    @Test
    public void registerUser_PasswordsEmpty() throws Exception {
        BlackCreekUser user = new BlackCreekUser();
        user.setLogin("testuser");
        user.setFirstName("test");
        user.setLastName("user");
        user.setLogin("testuser@mail.com");
        user.setConfirmPassword("differentpassword");

        mockMvc.perform(post("/register")
                        .param("login", user.getLogin())
                        .param("email", user.getEmail())
                        .param("password", user.getPassword())
                        .param("confirmPassword", user.getConfirmPassword()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/register"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Passwords do not match."));
    }

    @Test
    public void registerUser_PasswordConfirmEmpty() throws Exception {
        BlackCreekUser user = new BlackCreekUser();
        user.setLogin("testuser");
        user.setFirstName("test");
        user.setLastName("user");
        user.setLogin("testuser@mail.com");
        user.setPassword("differentpassword");

        mockMvc.perform(post("/register")
                        .param("login", user.getLogin())
                        .param("email", user.getEmail())
                        .param("password", user.getPassword())
                        .param("confirmPassword", user.getConfirmPassword()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/register"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Passwords do not match."));
    }

    @Test
    public void registerUser_HasErrors() throws Exception {
        String pwd = "Sassword1!";
        BlackCreekUser user = new BlackCreekUser();
        user.setLogin("testuser");
        user.setEmail("email");
        user.setPassword(pwd);
        user.setConfirmPassword(pwd);

        when(blackCreekUserService.saveUser(any(BlackCreekUser.class))).thenReturn(null);

        mockMvc.perform(post("/register")
                        .param("login", user.getLogin())
                        .param("email", user.getEmail())
                        .param("password", user.getPassword())
                        .param("confirmPassword", user.getConfirmPassword()))
                .andExpect(status().isOk())
                .andExpect(view().name("account/register"))
                .andExpect(model().attributeExists("error"));
    }
}
