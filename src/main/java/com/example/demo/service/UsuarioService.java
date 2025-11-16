package com.example.demo.service;

import com.example.demo.model.Usuario;
import java.util.Optional;

public interface UsuarioService {

    Usuario crearUsuario(Usuario usuario);

    Optional<Usuario> buscarPorUsername(String username);

    boolean existeUsername(String username);
}
