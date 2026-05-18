// ============================================================
// ARQUIVO: view/TelaProjetos.java
// ============================================================
package view;

import controller.ProjetoController;
import controller.UsuarioController;
import enums.StatusProjeto;
import exception.SistemaException;
import model.Projeto;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;

public class TelaProjetos extends JFrame {

    private JTable              tabela;
    private DefaultTableModel   modeloTabela;
    private List<Projeto>       listaProjetos;
    private final Usuario       usuarioLogado;

    private final ProjetoController  projetoCtrl  = new ProjetoController();
    private final UsuarioController  usuarioCtrl  = new UsuarioController();

    public TelaProjetos(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        setTitle("Gerenciamento de Projetos");
        setSize(900, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        criarComponentes();
        carregarDados();
    }

    private void criarComponentes() {
        setLayout(new BorderLayout(5, 5));

        // Barra de ferramentas
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        barra.setBackground(new Color(39, 174, 96));

        JButton btnNovo     = criarBotao("➕ Novo Projeto",    new Color(46, 204, 113));
        JButton btnEditar   = criarBotao("✏ Editar",           new Color(230, 126, 34));
        JButton btnExcluir  = criarBotao("🗑 Excluir",         new Color(231, 76, 60));
        JButton btnAtualizar= criarBotao("🔄 Atualizar",       new Color(52, 152, 219));

        barra.add(btnNovo); barra.add(btnEditar);
        barra.add(btnExcluir); barra.add(btnAtualizar);
        add(barra, BorderLayout.NORTH);

        // Tabela
        String[] cols = {"ID", "Nome do Projeto", "Início", "Previsão Fim", "Status", "Gerente"};
        modeloTabela = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(40);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(250);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Eventos
        btnNovo.addActionListener(e -> abrirFormularioProjeto(null));
        btnEditar.addActionListener(e -> editarSelecionado());
        btnExcluir.addActionListener(e -> excluirSelecionado());
        btnAtualizar.addActionListener(e -> carregarDados());
        tabela.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) editarSelecionado();
            }
        });
    }

    private void carregarDados() {
        try {
            listaProjetos = projetoCtrl.listarTodos();
            modeloTabela.setRowCount(0);
            for (Projeto p : listaProjetos) {
                modeloTabela.addRow(new Object[]{
                    p.getId(), p.getNome(),
                    p.getDataInicio(),
                    p.getDataPrevisaoFim() != null ? p.getDataPrevisaoFim() : "—",
                    p.getStatus().getDescricao(),
                    p.getGerente() != null ? p.getGerente().getNome() : "—"
                });
            }
        } catch (SistemaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirFormularioProjeto(Projeto projeto) {
        // Formulário inline simples com JOptionPane para manter o exemplo conciso
        List<Usuario> gerentes = usuarioCtrl.listarTodosOrdenados();
        String[] nomesGerentes = gerentes.stream()
                .map(u -> u.getId() + " - " + u.getNome()).toArray(String[]::new);

        JTextField  fNome     = new JTextField(projeto != null ? projeto.getNome() : "", 20);
        JTextField  fDesc     = new JTextField(projeto != null ? projeto.getDescricao() : "", 20);
        JTextField  fInicio   = new JTextField(projeto != null && projeto.getDataInicio() != null
                                    ? projeto.getDataInicio().toString() : LocalDate.now().toString(), 12);
        JTextField  fFim      = new JTextField(projeto != null && projeto.getDataPrevisaoFim() != null
                                    ? projeto.getDataPrevisaoFim().toString() : "", 12);
        JComboBox<String> cbStatus   = new JComboBox<>(
                java.util.Arrays.stream(StatusProjeto.values())
                .map(StatusProjeto::getDescricao).toArray(String[]::new));
        JComboBox<String> cbGerente  = new JComboBox<>(nomesGerentes);

        if (projeto != null) cbStatus.setSelectedItem(projeto.getStatus().getDescricao());

        JPanel painel = new JPanel(new GridLayout(0, 2, 8, 8));
        painel.add(new JLabel("Nome: *")); painel.add(fNome);
        painel.add(new JLabel("Descrição:")); painel.add(fDesc);
        painel.add(new JLabel("Início (yyyy-MM-dd): *")); painel.add(fInicio);
        painel.add(new JLabel("Previsão Fim (yyyy-MM-dd):")); painel.add(fFim);
        painel.add(new JLabel("Status:")); painel.add(cbStatus);
        painel.add(new JLabel("Gerente: *")); painel.add(cbGerente);

        int resultado = JOptionPane.showConfirmDialog(this, painel,
                projeto == null ? "Novo Projeto" : "Editar Projeto",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            try {
                Projeto p = projeto != null ? projeto : new Projeto();
                p.setNome(fNome.getText().trim());
                p.setDescricao(fDesc.getText().trim());
                p.setDataInicio(LocalDate.parse(fInicio.getText().trim()));
                if (!fFim.getText().trim().isEmpty())
                    p.setDataPrevisaoFim(LocalDate.parse(fFim.getText().trim()));
                p.setStatus(StatusProjeto.values()[cbStatus.getSelectedIndex()]);
                p.setGerente(gerentes.get(cbGerente.getSelectedIndex()));

                if (projeto == null) projetoCtrl.cadastrar(p);
                else projetoCtrl.atualizar(p);

                JOptionPane.showMessageDialog(this, "Projeto salvo com sucesso! ✅");
                carregarDados();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editarSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) { avisoSelecao(); return; }
        abrirFormularioProjeto(listaProjetos.get(linha));
    }

    private void excluirSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) { avisoSelecao(); return; }
        Projeto p = listaProjetos.get(linha);
        if (JOptionPane.showConfirmDialog(this,
                "Excluir projeto '" + p.getNome() + "'? Isso também excluirá as tarefas associadas.",
                "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                projetoCtrl.excluir(p.getId());
                JOptionPane.showMessageDialog(this, "Projeto excluído.");
                carregarDados();
            } catch (SistemaException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void avisoSelecao() {
        JOptionPane.showMessageDialog(this, "Selecione um item na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    private JButton criarBotao(String texto, Color cor) {
        JButton b = new JButton(texto);
        b.setBackground(cor); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}
