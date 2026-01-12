package br.com.matheus.mypatrimony.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewPasswordDTO {

    @NotBlank(message = "Login não pode estar vazio")
    private String login;

    @NotBlank(message = "Código de verificação não pode estar vazio")
    private String code;

    @NotBlank(message = "Senha não pode estar vazia")
    @Size(min = 6, max = 15, message = "A senha deve conter de 6 a 15 caracteres")
    private String password;

}