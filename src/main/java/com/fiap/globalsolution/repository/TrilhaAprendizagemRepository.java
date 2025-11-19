package com.fiap.globalsolution.repository;

import com.fiap.globalsolution.domain.TrilhaAprendizagem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TrilhaAprendizagemRepository extends JpaRepository<TrilhaAprendizagem, Long> {

    /**
     * Chama a Stored Procedure PKG_PERFIS_E_TRILHAS.PR_INSERIR_TRILHA para iniciar uma nova trilha.
     * @param idUsuario O ID do usuário para o qual a trilha será criada.
     * @param tituloObjetivo O objetivo de carreira do usuário.
     * @return O ID da trilha recém-criada.
     */
    @Procedure(procedureName = "PKG_PERFIS_E_TRILHAS.PR_INSERIR_TRILHA")
    Long prInserirTrilha(
        @Param("p_id_usuario") Long idUsuario,
        @Param("p_titulo_objetivo") String tituloObjetivo
    );

    /**
     * Busca todas as trilhas de aprendizagem de forma paginada.
     * @param pageable Objeto de paginação.
     * @return Uma página de trilhas de aprendizagem.
     */
    Page<TrilhaAprendizagem> findAll(Pageable pageable);
}
