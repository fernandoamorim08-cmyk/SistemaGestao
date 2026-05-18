// ============================================================
// ARQUIVO: view/TelaTarefas.java
// ============================================================
package view;

import controller.ProjetoController;
import controller.TarefaController;
import controller.UsuarioController;
import enums.Prioridade;
import enums.StatusTarefa;
import exception.SistemaException;
import model.Projeto;
import model.Tarefa;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class TelaTarefas extends JFrame {

    private JTable            tabela;
    private DefaultTableModel modeloTabela;
    private JComboBox<String> cbProjeto;
    private List<Tarefa>      listaTarefas;
    private List<Projeto>     listaProjetos;

    private final TarefaController  tarefaCtrl   = new TarefaController();
    private final ProjetoController projetoCtrl  = new ProjetoController();
    private final UsuarioController usuarioCtrl  = new UsuarioController();
    private final Usuario           usuarioLogado;

    public TelaTarefas(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        setTitle("Gerenciamento de Tarefas");
        setSize(950, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        criarComponentes();
        carregarProjetos();
    }

    private void criarComponentes() {
        setLayout(new BorderLayout(5, 5));

        // Painel de filtro por projeto
        JPanel painelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        painelFiltro.setBackground(new Color(211, 84, 0));
        painelFiltro.add(new JLabel("Projeto:") {{ setForeground(Color.WHITE); }});
        cbProjeto = new JComboBox<>();
        cbProjeto.setPreferredSize(new Dimension(250, 28));
        painelFiltro.add(cbProjeto);

        JButton btnFiltrar  = criarBotao("🔍 Filtrar",      new Color(230, 126, 34));
        JButton btnNova     = criarBotao("➕ Nova Tarefa",   new Color(46, 204, 113));
        JButton btnConcluir = criarBotao("✅ Concluir",      new Color(52, 152, 219));
        JButton btnExcluir  = criarBotao("🗑 Excluir",      new Color(231, 76, 60));

        painelFiltro.add(btnFiltrar);
        painelFiltro.add(btnNova);
        painelFiltro.add(btnConcluir);
        painelFiltro.add(btnExcluir);
        add(painelFiltro, BorderLayout.NORTH);

        // Tabela de tarefas
        String[] cols = {"ID", "Título", "Responsável", "Data Limite", "Prioridade", "Status", "Atrasada?"};
        modeloTabela = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setRowHeight(22);

        // Coloriza linhas de tarefas atrasadas em vermelho claro
        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel && listaTarefas != null && row < listaTarefas.size()) {
                    if (listaTarefas.get(row).isAtrasada()) {
                        c.setBackground(new Color(255, 220, 220));  // Vermelho claro
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        });

        add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Legenda
        JPanel legenda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        legenda.setBackground(new Color(245, 245, 245));
        JLabel legAtrasada = new JLabel("  🔴 Linha vermelha = tarefa atrasada");
        legAtrasada.setFont(new Font("Arial", Font.ITALIC, 11));
        legenda.add(legAtrasada);
        add(legenda, BorderLayout.SOUTH);

        // Eventos
        btnFiltrar.addActionListener(e -> carregarTarefas());
        btnNova.addActionListener(e -> novaTarefa());
        btnConcluir.addActionListener(e -> marcarConcluida());
        btnExcluir.addActionListener(e -> excluirTarefa());
        cbProjeto.addActionListener(e -> carregarTarefas());
    }

    private void carregarProjetos() {
        listaProjetos = projetoCtrl.listarTodos();
        cbProjeto.removeAllItems();
        cbProjeto.addItem("-- Selecione um projeto --");
        listaProjetos.forEach(p -> cbProjeto.addItem(p.getId() + " - " + p.getNome()));
        if (!listaProjetos.isEmpty()) cbProjeto.setSelectedIndex(1);
        carregarTarefas();
    }

    private void carregarTarefas() {
        int idx = cbProjeto.getSelectedIndex() - 1;
        if (idx < 0 || listaProjetos.isEmpty()) { modeloTabela.setRowCount(0); return; }

        try {
            listaTarefas = tarefaCtrl.listarPorProjeto(listaProjetos.get(idx).getId());
            modeloTabela.setRowCount(0);
            for (Tarefa t : listaTarefas) {
                modeloTabela.addRow(new Object[]{
                    t.getId(), t.getTitulo(),
                    t.getResponsavel() != null ? t.getResponsavel().getNome() : "—",
                    t.getDataLimite() != null ? t.getDataLimite() : "—",
                    t.getPrioridade().getDescricao(),
                    t.getStatus().getDescricao(),
                    t.isAtrasada() ? "⚠ Sim" : "Não"
                });
            }
        } catch (SistemaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void novaTarefa() {
        int idxProjeto = cbProjeto.getSelectedIndex() - 1;
        if (idxProjeto < 0) { JOptionPane.showMessageDialog(this, "Selecione um projeto primeiro."); return; }
        Projeto projeto = listaProjetos.get(idxProjeto);

        List<Usuario> usuarios = usuarioCtrl.listarTodosOrdenados();
        String[] nomesUsuarios = usuarios.stream()
                .map(u -> u.getId() + " - " + u.getNome()).toArray(String[]::new);

        JTextField fTitulo = new JTextField(20);
        JTextField fDesc   = new JTextField(20);
        JTextField fData   = new JTextField(LocalDate.now().plusDays(7).toString(), 12);
        JComboBox<String> cbPrioridade = new JComboBox<>(
                java.util.Arrays.stream(Prioridade.values())
                .map(Prioridade::getDescricao).toArray(String[]::new));
        JComboBox<String> cbResponsavel = new JComboBox<>(nomesUsuarios);

        JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
        p.add(new JLabel("Título: *"));      p.add(fTitulo);
        p.add(new JLabel("Descrição:"));     p.add(fDesc);
        p.add(new JLabel("Data Limite (yyyy-MM-dd):")); p.add(fData);
        p.add(new JLabel("Prioridade:"));    p.add(cbPrioridade);
        p.add(new JLabel("Responsável:"));   p.add(cbResponsavel);

        if (JOptionPane.showConfirmDialog(this, p, "Nova Tarefa — " + projeto.getNome(),
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                Tarefa t = new Tarefa();
                t.setTitulo(fTitulo.getText().trim());
                t.setDescricao(fDesc.getText().trim());
                t.setProjeto(projeto);
                t.setDataLimite(LocalDate.parse(fData.getText().trim()));
                t.setPrioridade(Prioridade.values()[cbPrioridade.getSelectedIndex()]);
                t.setResponsavel(usuarios.get(cbResponsavel.getSelectedIndex()));

                tarefaCtrl.cadastrar(t);
                JOptionPane.showMessageDialog(this, "Tarefa criada com sucesso! ✅");
                carregarTarefas();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void marcarConcluida() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) { JOptionPane.showMessageDialog(this, "Selecione uma tarefa."); return; }
        Tarefa t = listaTarefas.get(linha);
        t.setStatus(StatusTarefa.CONCLUIDA);
        try {
            tarefaCtrl.atualizar(t);
            JOptionPane.showMessageDialog(this, "Tarefa marcada como concluída! ✅");
            carregarTarefas();
        } catch (SistemaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirTarefa() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return;
        Tarefa t = listaTarefas.get(linha);
        if (JOptionPane.showConfirmDialog(this,
                "Excluir tarefa '" + t.getTitulo() + "'?",
                "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                tarefaCtrl.excluir(t.getId());
                carregarTarefas();
            } catch (SistemaException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JButton criarBotao(String t, Color c) {
        JButton b = new JButton(t);
        b.setBackground(c); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}
