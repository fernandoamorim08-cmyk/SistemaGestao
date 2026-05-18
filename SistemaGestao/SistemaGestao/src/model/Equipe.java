// ============================================================
// ARQUIVO: model/Equipe.java
// ============================================================
package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa uma equipe de trabalho.
 *
 * CONCEITO - AGREGAÇÃO:
 *   A Equipe agrega Usuários como membros. Se a equipe for
 *   excluída, os usuários continuam existindo no sistema.
 *   Isso é Agregação: o todo (equipe) e as partes (membros)
 *   podem existir independentemente.
 */
public class Equipe {

    private int id;
    private String nome;
    private String descricao;
    private LocalDateTime criadoEm;

    // AGREGAÇÃO: Lista de membros (Usuários)
    // A equipe "tem" membros, mas cada membro existe por si só
    private List<Usuario> membros;

    public Equipe() {
        this.criadoEm = LocalDateTime.now();
        this.membros  = new ArrayList<>();
    }

    public Equipe(String nome, String descricao) {
        this();
        this.nome      = nome;
        this.descricao = descricao;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public List<Usuario> getMembros() { return membros; }
    public void setMembros(List<Usuario> membros) { this.membros = membros; }

    public void adicionarMembro(Usuario usuario) {
        if (!membros.contains(usuario)) {
            membros.add(usuario);
        }
    }

    public void removerMembro(Usuario usuario) {
        membros.remove(usuario);
    }

    public int getTotalMembros() {
        return membros.size();
    }

    @Override
    public String toString() {
        return nome + " (" + getTotalMembros() + " membros)";
    }
}
