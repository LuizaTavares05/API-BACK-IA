package br.com.chatiabe.application.port.inbound;

import br.com.chatiabe.application.dto.LoginRequest;
import br.com.chatiabe.application.dto.LoginResponse;
import br.com.chatiabe.application.dto.RegisterRequest;
import br.com.chatiabe.application.dto.RegisterResponse;

public interface AuthUseCase {

    LoginResponse login(LoginRequest request);

    RegisterResponse register(RegisterRequest request);
}
