// ============================================================
// ARQUIVO: model/Usuario.java
//
// CONCEITO - CLASSE E OBJETO:
//   Uma classe é um "molde" ou "planta baixa" que define
//   como os objetos serão criados. Por exemplo, a classe
//   Usuario define que todo usuário terá nome, CPF, e-mail etc.
//   Um objeto é uma "instância" da classe — um usuário real
//   criado a partir desse molde.
//
// CONCEITO - ENCAPSULAMENTO:
//   Os atributos são declarados como "private" (privados),
//   ou seja, só podem ser acessados de dentro da própria classe.
//   Para lê-los ou alterá-los de fora, usamos métodos públicos
//   chamados getters (get = obter) e setters (set = definir).
//   Isso protege os dados e garante que regras sejam aplicadas.
//
// RELAÇÃO COM BANCO DE DADOS:
//   Cada atributo desta classe corresponde a uma coluna na
//   tabela "usuarios" do banco de dados.
// ============================================================
package model;

import enums.PerfilUsuario;
import java.time.LocalDateTime;

/**
 * Representa um usuário do sistema.
 * Esta classe é uma entidade (Entity) — mapeia a tabela "usuarios".
 */
public class Usuario {

    // --------------------------------------------------------
    // ATRIBUTOS (private = encapsulamento)
    // Cada atributo guarda uma informação do usuário
    // --------------------------------------------------------
    private int id;                      // Identificador único (PK no banco)
    private String nome;                 // Nome completo
    private String cpf;                  // CPF no formato 000.000.000-00
    private String email;                // E-mail válido
    private String cargo;                // Cargo na empresa
    private String login;                // Login para acesso
    private String senha;                // Senha (em produção: usar hash)
    private PerfilUsuario perfil;        // ENUM: ADMINISTRADOR, GERENTE ou COLABORADOR
    private boolean ativo;               // Se o usuário está ativo no sistema
    private LocalDateTime criadoEm;      // Data e hora de criação

    // --------------------------------------------------------
    // CONSTRUTOR PADRÃO (sem parâmetros)
    // Necessário para o JDBC instanciar objetos via reflexão
    // --------------------------------------------------------
    public Usuario() {
        this.ativo = true;               // Por padrão, usuário começa ativo
        this.criadoEm = LocalDateTime.now();
    }

    // --------------------------------------------------------
    // CONSTRUTOR COMPLETO
    // Permite criar um usuário já com todos os dados
    // --------------------------------------------------------
    public Usuario(String nome, String cpf, String email, String cargo,
                   String login, String senha, PerfilUsuario perfil) {
        this();                          // Chama o construtor padrão primeiro
        this.nome   = nome;
        this.cpf    = cpf;
        this.email  = email;
        this.cargo  = cargo;
        this.login  = login;
        this.senha  = senha;
        this.perfil = perfil;
    }

    // --------------------------------------------------------
    // GETTERS E SETTERS
    // Métodos públicos para acessar e alterar os atributos
    // --------------------------------------------------------

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) {
        // Validação simples: nome não pode ser vazio
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio.");
        }
        this.nome = nome.trim();
    }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public PerfilUsuario getPerfil() { return perfil; }
    public void setPerfil(PerfilUsuario perfil) { this.perfil = perfil; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    // --------------------------------------------------------
    // toString()
    // Método herdado de Object, sobrescrito para exibir dados
    // úteis quando imprimimos o objeto (ex: System.out.println)
    // --------------------------------------------------------
    @Override
    public String toString() {
        return "Usuario{" +
               "id=" + id +
               ", nome='" + nome + '\'' +
               ", email='" + email + '\'' +
               ", perfil=" + perfil +
               '}';
    }
}
