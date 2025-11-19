package com.fiap.globalsolution.repository;

import com.fiap.globalsolution.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    UserDetails findByEmail(String email);

    /**
     * Chama a Stored Procedure PKG_PERFIS_E_TRILHAS.PR_INSERIR_USUARIO para inserir um novo usuário.
     * @param nome O nome do usuário.
     * @param email O email do usuário.
     * @param senhaHash O hash da senha do usuário.
     * @return O ID do usuário recém-criado.
     */
    @Procedure(procedureName = "PKG_PERFIS_E_TRILHAS.PR_INSERIR_USUARIO")
    Long prInserirUsuario(
        @Param("p_nome") String nome,
        @Param("p_email") String email,
        @Param("p_senha_hash") String senhaHash
    );
}
