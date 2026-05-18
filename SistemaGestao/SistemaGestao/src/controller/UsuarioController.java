// ============================================================
// ARQUIVO: controller/UsuarioController.java
//
// CONCEITO - ARQUITETURA MVC (Model-View-Controller):
//   MVC divide o sistema em 3 camadas:
//
//   MODEL      → Classes de dados (Usuario, Projeto, etc.)
//                Representam o "quê" do sistema.
//
//   VIEW       → Telas Swing (formulários, tabelas, botões)
//                O que o usuário vê e interage.
//
//   CONTROLLER → Lógica de negócio (validações, regras)
//                "Cérebro" que conecta Model e View.
//
//   Fluxo: View chama Controller → Controller usa DAO →
//          DAO acessa Banco → retorna para Controller → View exibe.
//
// CONCEITO - PILHA (Stack):
//   Usada para registrar o histórico de ações do usuário.
//   Funciona como uma pilha de pratos: o último que entra
//   é o primeiro que sai (LIFO - Last In, First Out).
//
// CONCEITO - FILA (Queue):
//   Usada para enfileirar notificações.
//   Funciona como uma fila de banco: o primeiro que entra
//   é o primeiro que sai (FIFO - First In, First Out).
//
// CONCEITO - ALGORITMO DE ORDENAÇÃO:
//   Ordenamos listas usando Comparable e Comparator,
//   que definem critérios de comparação entre objetos.
// ============================================================
package controller;

import dao.UsuarioDAO;
import enums.PerfilUsuario;
import exception.SistemaException;
import model.Usuario;

import java.util.*;

/**
 * Controller responsável pela lógica de negócio dos Usuários.
 * Recebe chamadas da View, aplica regras e usa o DAO para persistir.
 */
public class UsuarioController {

    // DAO para acesso ao banco de dados
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // --------------------------------------------------------
    // CONCEITO - PILHA (Stack)
    // Registra histórico de operações (para possível "desfazer")
    // Stack usa LIFO: push() empilha, pop() desempilha o último
    // --------------------------------------------------------
    private final Stack<String> historicoAcoes = new Stack<>();

    // --------------------------------------------------------
    // CONCEITO - FILA (Queue / LinkedList)
    // Armazena mensagens de notificação em ordem de chegada
    // Queue usa FIFO: offer() enfileira, poll() remove o primeiro
    // --------------------------------------------------------
    private final Queue<String> filaNotificacoes = new LinkedList<>();

    // Usuário atualmente autenticado no sistema
    private static Usuario usuarioLogado;

    // ========================================================
    // AUTENTICAÇÃO
    // ========================================================

    /**
     * Realiza o login do usuário.
     * @param login — login digitado
     * @param senha — senha digitada
     * @return Usuario autenticado
     * @throws SistemaException se credenciais inválidas
     */
    public Usuario fazerLogin(String login, String senha) {
        // Validação básica dos campos
        if (login == null || login.trim().isEmpty()) {
            throw new SistemaException("Login não pode ser vazio.");
        }
        if (senha == null || senha.trim().isEmpty()) {
            throw new SistemaException("Senha não pode ser vazia.");
        }

        // Tenta autenticar no banco
        Usuario usuario = usuarioDAO.autenticar(login.trim(), senha.trim());

        if (usuario == null) {
            throw new SistemaException("Login ou senha inválidos. Verifique e tente novamente.");
        }

        // Guarda o usuário logado e registra na pilha
        usuarioLogado = usuario;
        historicoAcoes.push("Login realizado: " + usuario.getNome() + " em " + new Date());
        filaNotificacoes.offer("Bem-vindo, " + usuario.getNome() + "!");

        return usuario;
    }

    public void fazerLogout() {
        historicoAcoes.push("Logout: " + (usuarioLogado != null ? usuarioLogado.getNome() : "?"));
        usuarioLogado = null;
    }

    public static Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    // ========================================================
    // CRUD DE USUÁRIOS
    // ========================================================

    /**
     * Cadastra um novo usuário após validações de negócio.
     */
    public void cadastrar(Usuario usuario) {
        // Validações de negócio (regras que não dependem do banco)
        validarUsuario(usuario);

        // Verifica duplicidade de CPF e login (regras de negócio)
        List<Usuario> existentes = usuarioDAO.listarTodos();
        for (Usuario u : existentes) {
            if (u.getCpf().equals(usuario.getCpf())) {
                throw new SistemaException("Já existe um usuário com este CPF.");
            }
            if (u.getLogin().equals(usuario.getLogin())) {
                throw new SistemaException("Este login já está em uso. Escolha outro.");
            }
        }

        usuarioDAO.inserir(usuario);
        historicoAcoes.push("Usuário cadastrado: " + usuario.getNome());
        filaNotificacoes.offer("Usuário '" + usuario.getNome() + "' cadastrado com sucesso!");
    }

    /**
     * Atualiza dados de um usuário existente.
     */
    public void atualizar(Usuario usuario) {
        validarUsuario(usuario);
        usuarioDAO.atualizar(usuario);
        historicoAcoes.push("Usuário atualizado: " + usuario.getNome());
    }

    /**
     * Desativa (exclusão lógica) um usuário.
     * Somente ADMINISTRADOR pode desativar.
     */
    public void desativar(int id) {
        verificarPermissaoAdmin();
        usuarioDAO.desativar(id);
        historicoAcoes.push("Usuário desativado. ID: " + id);
    }

    // ========================================================
    // CONSULTAS
    // ========================================================

    /**
     * Retorna todos os usuários, ORDENADOS por nome.
     *
     * CONCEITO - ALGORITMO DE ORDENAÇÃO:
     *   Usamos Collections.sort() com um Comparator que define
     *   o critério de comparação. Aqui ordenamos por nome
     *   ignorando maiúsculas/minúsculas.
     *   O Java usa internamente o algoritmo TimSort (eficiente).
     */
    public List<Usuario> listarTodosOrdenados() {
        List<Usuario> lista = usuarioDAO.listarTodos();

        // Ordena pelo nome (A → Z), ignorando case
        Collections.sort(lista, (u1, u2) ->
            u1.getNome().compareToIgnoreCase(u2.getNome())
        );

        return lista;
    }

    /**
     * Busca usuários por nome (busca parcial).
     *
     * CONCEITO - ALGORITMO DE BUSCA:
     *   Delegamos a busca ao banco (LIKE), mas poderíamos
     *   também fazer busca linear na lista em memória:
     *   percorremos cada elemento verificando se contém o texto.
     */
    public List<Usuario> buscarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return listarTodosOrdenados();
        }
        return usuarioDAO.buscarPorNome(nome.trim());
    }

    /**
     * Lista usuários por perfil para o relatório.
     */
    public Map<PerfilUsuario, List<Usuario>> listarAgrupadosPorPerfil() {
        // Map organiza dados em pares chave→valor
        Map<PerfilUsuario, List<Usuario>> mapa = new LinkedHashMap<>();

        for (PerfilUsuario perfil : PerfilUsuario.values()) {
            List<Usuario> usuarios = usuarioDAO.listarPorPerfil(perfil);
            mapa.put(perfil, usuarios);
        }

        return mapa;
    }

    // ========================================================
    // NOTIFICAÇÕES (uso da Fila)
    // ========================================================

    /**
     * Retorna e remove a próxima notificação da fila.
     * Usa FIFO: a primeira mensagem adicionada sai primeiro.
     */
    public String proximaNotificacao() {
        return filaNotificacoes.poll();  // poll() retorna null se fila vazia
    }

    public boolean temNotificacoes() {
        return !filaNotificacoes.isEmpty();
    }

    /**
     * Retorna o histórico de ações da pilha (sem remover).
     */
    public Stack<String> getHistoricoAcoes() {
        return historicoAcoes;
    }

    // ========================================================
    // VALIDAÇÕES PRIVADAS
    // ========================================================

    private void validarUsuario(Usuario u) {
        if (u.getNome() == null || u.getNome().trim().isEmpty())
            throw new SistemaException("Nome é obrigatório.");
        if (u.getCpf() == null || u.getCpf().trim().isEmpty())
            throw new SistemaException("CPF é obrigatório.");
        if (u.getEmail() == null || !u.getEmail().contains("@"))
            throw new SistemaException("E-mail inválido.");
        if (u.getLogin() == null || u.getLogin().trim().length() < 3)
            throw new SistemaException("Login deve ter no mínimo 3 caracteres.");
        if (u.getSenha() == null || u.getSenha().length() < 6)
            throw new SistemaException("Senha deve ter no mínimo 6 caracteres.");
        if (u.getPerfil() == null)
            throw new SistemaException("Perfil é obrigatório.");
    }

    private void verificarPermissaoAdmin() {
        if (usuarioLogado == null ||
            usuarioLogado.getPerfil() != PerfilUsuario.ADMINISTRADOR) {
            throw new SistemaException("Apenas Administradores podem realizar esta operação.");
        }
    }
}
