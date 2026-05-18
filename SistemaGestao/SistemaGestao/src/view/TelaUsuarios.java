// ============================================================
// ARQUIVO: view/TelaUsuarios.java
//
// Tela de cadastro, edição e listagem de usuários.
// Usa JTable para exibir dados em formato de tabela.
//
// CONCEITO - JTable:
//   JTable exibe dados em linhas e colunas.
//   Usa um TableModel para definir os dados e colunas.
//   DefaultTableModel é a implementação padrão.
// ============================================================
package view;

import controller.UsuarioController;
import enums.PerfilUsuario;
import exception.SistemaException;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Tela para gerenciamento de usuários (CRUD completo).
 */
public class TelaUsuarios extends JFrame {

    // Componentes principais
    private JTable      tabela;
    private DefaultTableModel modeloTabela;
    private JTextField  campoBusca;
    private JButton     btnNovo, btnEditar, btnDesativar, btnBuscar, btnAtualizar;

    // Controller da camada de negócio
    private final UsuarioController controller = new UsuarioController();

    // Lista em memória (carregada do banco)
    private List<Usuario> listaUsuarios;

    public TelaUsuarios() {
        configurarJanela();
        criarComponentes();
        carregarDados();
    }

    private void configurarJanela() {
        setTitle("Gerenciamento de Usuários");
        setSize(850, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void criarComponentes() {
        setLayout(new BorderLayout(5, 5));

        // ---- BARRA DE FERRAMENTAS (topo) ----
        JPanel barraFerramentas = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        barraFerramentas.setBackground(new Color(41, 128, 185));

        btnNovo = criarBotao("➕ Novo", new Color(46, 204, 113));
        btnEditar = criarBotao("✏ Editar", new Color(230, 126, 34));
        btnDesativar = criarBotao("🗑 Desativar", new Color(231, 76, 60));
        btnAtualizar = criarBotao("🔄 Atualizar", new Color(52, 152, 219));

        barraFerramentas.add(btnNovo);
        barraFerramentas.add(btnEditar);
        barraFerramentas.add(btnDesativar);
        barraFerramentas.add(new JSeparator(SwingConstants.VERTICAL));
        barraFerramentas.add(btnAtualizar);

        // Campo de busca
        JLabel lblBusca = new JLabel("Buscar:");
        lblBusca.setForeground(Color.WHITE);
        campoBusca = new JTextField(18);
        btnBuscar = criarBotao("🔍", new Color(44, 62, 80));

        barraFerramentas.add(lblBusca);
        barraFerramentas.add(campoBusca);
        barraFerramentas.add(btnBuscar);

        add(barraFerramentas, BorderLayout.NORTH);

        // ---- TABELA DE USUÁRIOS ----
        // Colunas da tabela (nomes exibidos no cabeçalho)
        String[] colunas = {"ID", "Nome", "CPF", "E-mail", "Cargo", "Perfil", "Ativo"};

        // DefaultTableModel gerencia os dados da tabela
        // O "false" no final impede edição direta nas células
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;  // Células não editáveis — edição só pelo formulário
            }
        };

        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setReorderingAllowed(false);  // Impede reordenar colunas

        // Ajusta largura das colunas
        tabela.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabela.getColumnModel().getColumn(5).setPreferredWidth(100);
        tabela.getColumnModel().getColumn(6).setPreferredWidth(50);

        // JScrollPane permite rolar quando há muitas linhas
        JScrollPane scrollPane = new JScrollPane(tabela);
        add(scrollPane, BorderLayout.CENTER);

        // ---- RODAPÉ COM CONTAGEM ----
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rodape.setBackground(new Color(189, 195, 199));
        rodape.add(new JLabel("Clique duplo na linha para editar"));
        add(rodape, BorderLayout.SOUTH);

        // ---- EVENTOS ----
        btnNovo.addActionListener(e -> abrirFormulario(null));
        btnEditar.addActionListener(e -> editarSelecionado());
        btnDesativar.addActionListener(e -> desativarSelecionado());
        btnAtualizar.addActionListener(e -> carregarDados());
        btnBuscar.addActionListener(e -> buscar());
        campoBusca.addActionListener(e -> buscar());

        // Clique duplo na linha abre edição
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) editarSelecionado();
            }
        });
    }

    // ---- Carrega dados do banco na tabela ----
    private void carregarDados() {
        try {
            listaUsuarios = controller.listarTodosOrdenados();
            preencherTabela(listaUsuarios);
        } catch (SistemaException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void preencherTabela(List<Usuario> lista) {
        modeloTabela.setRowCount(0);  // Limpa a tabela

        for (Usuario u : lista) {
            // Adiciona uma linha com os dados do usuário
            modeloTabela.addRow(new Object[]{
                u.getId(),
                u.getNome(),
                u.getCpf(),
                u.getEmail(),
                u.getCargo(),
                u.getPerfil().getDescricao(),
                u.isAtivo() ? "✅ Sim" : "❌ Não"
            });
        }
    }

    // ---- Busca por nome ----
    private void buscar() {
        String texto = campoBusca.getText().trim();
        try {
            List<Usuario> resultado = controller.buscarPorNome(texto);
            preencherTabela(resultado);
        } catch (SistemaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    // ---- Abre formulário para novo ou edição ----
    private void abrirFormulario(Usuario usuario) {
        FormularioUsuario form = new FormularioUsuario(this, usuario, controller);
        form.setVisible(true);
        carregarDados();  // Recarrega após fechar o formulário
    }

    // ---- Edita o usuário selecionado na tabela ----
    private void editarSelecionado() {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário para editar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Busca o usuário correspondente à linha selecionada
        Usuario usuario = listaUsuarios.get(linhaSelecionada);
        abrirFormulario(usuario);
    }

    // ---- Desativa o usuário selecionado ----
    private void desativarSelecionado() {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário para desativar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario usuario = listaUsuarios.get(linhaSelecionada);

        // Pede confirmação antes de desativar
        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Deseja desativar o usuário '" + usuario.getNome() + "'?",
                "Confirmar Desativação",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                controller.desativar(usuario.getId());
                JOptionPane.showMessageDialog(this, "Usuário desativado com sucesso!");
                carregarDados();
            } catch (SistemaException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ---- Helper: cria botão estilizado ----
    private JButton criarBotao(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setBackground(cor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
