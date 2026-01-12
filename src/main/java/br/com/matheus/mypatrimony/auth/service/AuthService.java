package br.com.matheus.mypatrimony.auth.service;

import br.com.matheus.mypatrimony.auth.dto.ForgotPasswordDTO;
import br.com.matheus.mypatrimony.auth.dto.LoginDTO;
import br.com.matheus.mypatrimony.auth.dto.NewPasswordDTO;
import br.com.matheus.mypatrimony.auth.dto.SessionLoginDTO;
import br.com.matheus.mypatrimony.auth.repository.ILoginRepository;
import br.com.matheus.mypatrimony.auth.repository.Login;
import br.com.matheus.mypatrimony.auth.repository.LoginType;
import br.com.matheus.mypatrimony.general.enums.Status;
import br.com.matheus.mypatrimony.general.service.CodeGeneratorService;
import br.com.matheus.mypatrimony.general.service.DateService;
import br.com.matheus.mypatrimony.mail.dto.EmailDTO;
import br.com.matheus.mypatrimony.mail.service.MailService;
import br.com.matheus.mypatrimony.message.ApiMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class AuthService {
    @Autowired
    private ILoginRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CodeGeneratorService codeGeneratorService;

    @Autowired
    private MailService mailService;

    @Autowired
    private DateService dateService;

    @Value("${spring.mail.username}")
    private String mail;

    public ResponseEntity signUp(LoginDTO dto){

        Optional<Login> loginOptional = repository.findByLogin(dto.getLogin());

        if(loginOptional.isPresent())
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        Login login = Login.builder()
                .login(dto.getLogin())
                .email(dto.getEmail())
                .insertionDate(new Date())
                .name(dto.getName())
                .password(passwordEncoder.encode(dto.getPassword()))
                .status(Status.ACTIVE)
                .build();

        if(repository.count() == 0)
            login.setType(LoginType.ADMIN);
        else
            login.setType(LoginType.COMMON);

        repository.save(login);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public ResponseEntity forgotPassword(ForgotPasswordDTO dto){

        Optional<Login> loginOptional = repository.findByLogin(dto.getLogin());

        if(loginOptional.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiMessage(HttpStatus.NOT_FOUND, "Usuário não existe"));

        final Login login = loginOptional.get();
        if(!dto.getEmail().equalsIgnoreCase(login.getEmail()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiMessage(HttpStatus.BAD_REQUEST, "E-mail não pertence ao usuário"));

        addCodeForgotPassword(login);
        repository.save(login);
        sendMail(login);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiMessage(HttpStatus.OK, "E-mail enviado para " + login.getEmail()));
    }

    public ResponseEntity newPassword(NewPasswordDTO dto) {

        Optional<Login> loginOptional = repository.findByLogin(dto.getLogin());

        if(!loginOptional.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiMessage(HttpStatus.NOT_FOUND, "Usuário não existe"));

        Date now = new Date();

        final Login login = loginOptional.get();



        if(Objects.isNull(login.getCode()) || Objects.isNull(login.getExpirationDate()) || !dto.getCode().equals(login.getCode()) || now.after(login.getExpirationDate()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiMessage(HttpStatus.BAD_REQUEST, "Código inválido"));

        login.setExpirationDate(null);
        login.setCode(null);
        login.setPassword(passwordEncoder.encode(dto.getPassword()));

        repository.save(login);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiMessage(HttpStatus.OK, "Senha alterada com sucesso."));
    }


    private void addCodeForgotPassword(Login login){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 1);

        login.setExpirationDate(calendar.getTime());
        login.setCode(codeGeneratorService.generateCodeEmail(10));
    }

    private void sendMail(Login login){
        final String format = "dd/MM/yyyy HH:mm";
        final String content = new StringBuilder()
                .append("Olá ").append(login.getName())
                .append("\n").append("Seu código de recuperação é: ")
                .append(login.getCode()).append(" válido até ")
                .append(dateService.formatterDate(login.getExpirationDate(), format)).toString();

       final EmailDTO emailDTO = EmailDTO.builder()
                .from(mail)
                .content(content)
                .to(login.getEmail())
                .subject("Esqueci minha senha")
                .build();

        mailService.sendMail(emailDTO);
    }

    public ResponseEntity check(){
        final String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<Login> loginOptional = repository.findByLogin(currentUserName);
        final Login login = loginOptional.get();

        final SessionLoginDTO session = new SessionLoginDTO();
        session.setId(login.getId());
        session.setLogin(login.getLogin());
        session.setName(login.getName());

        return ResponseEntity.status(HttpStatus.OK)
                .body(session);
    }
}
