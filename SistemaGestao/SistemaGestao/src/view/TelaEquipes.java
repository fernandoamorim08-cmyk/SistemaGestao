// ============================================================
// ARQUIVO: view/TelaEquipes.java
// ============================================================
package view;

import controller.EquipeController;
import controller.UsuarioController;
import exception.SistemaException;
import model.Equipe;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class TelaEquipes extends JFrame {

    private JTable            tabela;
    private DefaultTableModel modeloTabela;
    private List<Equipe>      listaEquipes;

    private final EquipeController  equipeCtrl  = new EquipeController();
    private final UsuarioController usuarioCtrl = new UsuarioController();

    public TelaEquipes() {
        setTitle("Gerenciamento de Equipes");
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        criarComponentes();
        carregarDados();
    }

    private void criarComponentes() {
        setLayout(new BorderLayout(5, 5));

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        barra.setBackground(new Color(142, 68, 173));

        JButton btnNova    = criarBotao("➕ Nova Equipe",  new Color(155, 89, 182));
        JButton btnGerenciar = criarBotao("👥 Membros",   new Color(52, 152, 219));
        JButton btnExcluir = criarBotao("🗑 Excluir",     new Color(231, 76, 60));
        JButton btnAtualizar = criarBotao("🔄",           new Color(44, 62, 80));

        barra.add(btnNova); barra.add(btnGerenciar);
        barra.add(btnExcluir); barra.add(btnAtualizar);
        add(barra, BorderLayout.NORTH);

        String[] cols = {"ID", "Nome da Equipe", "Descrição", "Membros"};
        modeloTabela = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        btnNova.addActionListener(e -> criarEquipe());
        btnGerenciar.addActionListener(e -> gerenciarMembros());
        btnExcluir.addActionListener(e -> excluirSelecionada());
        btnAtualizar.addActionListener(e -> carregarDados());
    }

    private void carregarDados() {
        try {
            listaEquipes = equipeCtrl.listarTodas();
            modeloTabela.setRowCount(0);
            for (Equipe e : listaEquipes) {
                modeloTabela.addRow(new Object[]{
                    e.getId(), e.getNome(), e.getDescricao(), e.getTotalMembros() + " membro(s)"
                });
            }
        } catch (SistemaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void criarEquipe() {
        JTextField fNome = new JTextField(20);
        JTextField fDesc = new JTextField(20);
        JPanel p = new JPanel(new GridLayout(2, 2, 8, 8));
        p.add(new JLabel("Nome: *")); p.add(fNome);
        p.add(new JLabel("Descrição:")); p.add(fDesc);

        if (JOptionPane.showConfirmDialog(this, p, "Nova Equipe",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                Equipe equipe = new Equipe(fNome.getText().trim(), fDesc.getText().trim());
                equipeCtrl.cadastrar(equipe);
                JOptionPane.showMessageDialog(this, "Equipe criada com sucesso! ✅");
                carregarDados();
            } catch (SistemaException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void gerenciarMembros() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) { JOptionPane.showMessageDialog(this, "Selecione uma equipe."); return; }
        Equipe equipe = equipeCtrl.buscarPorId(listaEquipes.get(linha).getId());

        List<Usuario> todos    = usuarioCtrl.listarTodosOrdenados();
        List<Usuario> membros  = equipe.getMembros();

        // Exibe lista de membros atuais
        StringBuilder sb = new StringBuilder("<html><b>Membros de: " + equipe.getNome() + "</b><br><br>");
        if (membros.isEmpty()) sb.append("Nenhum membro ainda.<br>");
        else membros.forEach(m -> sb.append("• ").append(m.getNome()).append("<br>"));
        sb.append("</html>");

        // Selecionar usuário para adicionar
        String[] opcoes = todos.stream()
                .map(u -> u.getId() + " - " + u.getNome()).toArray(String[]::new);
        String escolhido = (String) JOptionPane.showInputDialog(this,
                sb.toString() + "\n\nAdicionar membro:",
                "Gerenciar Membros", JOptionPane.PLAIN_MESSAGE,
                null, opcoes, opcoes.length > 0 ? opcoes[0] : null);

        if (escolhido != null) {
            int userId = todos.get(java.util.Arrays.asList(opcoes).indexOf(escolhido)).getId();
            try {
                equipeCtrl.adicionarMembro(equipe.getId(), userId);
                JOptionPane.showMessageDialog(this, "Membro adicionado! ✅");
                carregarDados();
            } catch (SistemaException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void excluirSelecionada() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) return;
        Equipe e = listaEquipes.get(linha);
        if (JOptionPane.showConfirmDialog(this,
                "Excluir equipe '" + e.getNome() + "'?",
                "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                equipeCtrl.excluir(e.getId());
                carregarDados();
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
