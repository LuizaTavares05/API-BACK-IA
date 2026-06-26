package br.com.chatiabe.application.port.inbound;

import br.com.chatiabe.application.dto.*;

public interface AuthUseCase {
    LoginResponse login(LoginRequest request);
    RegisterResponse register(RegisterRequest request);
}
