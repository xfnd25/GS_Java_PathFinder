package com.fiap.globalsolution.domain;

import com.fiap.globalsolution.domain.enums.StatusTrilha;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "TRILHA_APRENDIZAGEM")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrilhaAprendizagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TRILHA")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PERFIL", referencedColumnName = "ID_PERFIL")
    private Perfil perfil;

    @Column(name = "TITULO_OBJETIVO", nullable = false)
    private String tituloObjetivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private StatusTrilha status;

    @Lob
    @Column(name = "DADOS_JSON_IA")
    private String dadosJsonIA;
}
