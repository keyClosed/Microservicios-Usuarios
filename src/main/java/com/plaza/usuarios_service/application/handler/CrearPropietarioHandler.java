package com.plaza.usuarios_service.application.handler;
import com.plaza.usuarios_service.application.dto.request.CrearPropietarioRequest;
import com.plaza.usuarios_service.application.mapper.UsuarioMapper;
import com.plaza.usuarios_service.domain.model.Usuario;
import com.plaza.usuarios_service.domain.usecase.CrearPropietarioUseCase;
import org.springframework.stereotype.Service;
import com.plaza.usuarios_service.application.dto.response.UsuarioResponse;
@Service
public class CrearPropietarioHandler {

    private final CrearPropietarioUseCase useCase;

    public CrearPropietarioHandler(CrearPropietarioUseCase useCase) {
        this.useCase = useCase;
    }

    public UsuarioResponse ejecutar(CrearPropietarioRequest request) {

        // 1️⃣ Validación de edad
        if (!request.esMayorDeEdad()) {
            throw new IllegalArgumentException("El usuario debe ser mayor de edad");
        }

        // 2️⃣ Mapear Request -> Modelo de dominio
        Usuario usuario = UsuarioMapper.toModel(request);

        // 3️⃣ Ejecutar el caso de uso (valida documento, correo, guarda, encripta)
        Usuario usuarioGuardado = useCase.ejecutar(usuario);

        // 4️⃣ Mapear Modelo -> Response
        return UsuarioMapper.toResponse(usuarioGuardado);
    }
}