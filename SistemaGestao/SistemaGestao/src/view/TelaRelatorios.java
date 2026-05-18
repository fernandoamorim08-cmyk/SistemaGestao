// ============================================================
// ARQUIVO: view/TelaRelatorios.java
//
// Tela de relatórios gerenciais do sistema.
// Usa JTabbedPane para organizar os relatórios em abas.
//
// CONCEITO - JTabbedPane:
//   Componente que organiza conteúdo em abas (como no navegador).
//   Cada aba pode ter seu próprio painel com componentes.
// ============================================================
package view;

import controller.ProjetoController;
import controller.TarefaController;
import controller.UsuarioController;
import enums.PerfilUsuario;
import exception.SistemaException;
import model.Projeto;
import model.Tarefa;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class TelaRelatorios extends JFrame {

    private final ProjetoController projetoCtrl = new ProjetoController();
    private final TarefaController  tarefaCtrl  = new TarefaController();
    private final UsuarioController usuarioCtrl = new UsuarioController();

    public TelaRelatorios() {
        setTitle("Relatórios Gerenciais");
        setSize(800, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        criarComponentes();
    }

    private void criarComponentes() {
        setLayout(new BorderLayout(5, 5));

        // Cabeçalho
        JPanel cab = new JPanel();
        cab.setBackground(new Color(192, 57, 43));
        JLabel lbl = new JLabel("📊  Relatórios Gerenciais");
        lbl.setFont(new Font("Arial", Font.BOLD, 18));
        lbl.setForeground(Color.WHITE);
        cab.add(lbl);
        add(cab, BorderLayout.NORTH);

        // JTabbedPane — abas de relatório
        JTabbedPane abas = new JTabbedPane();
        abas.setFont(new Font("Arial", Font.PLAIN, 13));

        abas.addTab("📁 Projetos em Andamento", criarAbaProjetosAndamento());
        abas.addTab("✅ Tarefas Pendentes",       criarAbaTarefasPendentes());
        abas.addTab("👤 Usuários por Perfil",    criarAbaUsuariosPerfil());
        abas.addTab("⚠ Tarefas Atrasadas",      criarAbaTarefasAtrasadas());

        add(abas, BorderLayout.CENTER);

        // Rodapé com botão atualizar
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAtualizar = new JButton("🔄 Atualizar Relatórios");
        btnAtualizar.setBackground(new Color(41, 128, 185));
        btnAtualizar.setForeground(Color.WHITE);
        btnAtualizar.setFocusPainted(false);
        btnAtualizar.addActionListener(e -> {
            getContentPane().removeAll();
            criarComponentes();
            revalidate();
            repaint();
        });
        rodape.add(btnAtualizar);
        add(rodape, BorderLayout.SOUTH);
    }

    // ---- ABA 1: Projetos em Andamento ----
    private JPanel criarAbaProjetosAndamento() {
        JPanel painel = new JPanel(new BorderLayout(5, 5));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        try {
            List<Projeto> projetos = projetoCtrl.listarEmAndamento();

            // Cartão de resumo
            JLabel resumo = new JLabel("Total de projetos em andamento: " + projetos.size(),
                    SwingConstants.CENTER);
            resumo.setFont(new Font("Arial", Font.BOLD, 14));
            resumo.setForeground(new Color(39, 174, 96));
            resumo.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
            painel.add(resumo, BorderLayout.NORTH);

            String[] cols = {"Nome do Projeto", "Início", "Previsão Fim", "Gerente"};
            DefaultTableModel modelo = new DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            JTable tabela = new JTable(modelo);
            tabela.setRowHeight(22);

            for (Projeto p : projetos) {
                modelo.addRow(new Object[]{
                    p.getNome(),
                    p.getDataInicio(),
                    p.getDataPrevisaoFim() != null ? p.getDataPrevisaoFim() : "Indefinida",
                    p.getGerente() != null ? p.getGerente().getNome() : "—"
                });
            }
            painel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        } catch (SistemaException ex) {
            painel.add(new JLabel("Erro ao carregar: " + ex.getMessage()));
        }
        return painel;
    }

    // ---- ABA 2: Tarefas Pendentes ----
    private JPanel criarAbaTarefasPendentes() {
        JPanel painel = new JPanel(new BorderLayout(5, 5));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        try {
            List<Tarefa> tarefas = tarefaCtrl.listarPendentes();

            JLabel resumo = new JLabel("Total de tarefas pendentes/em andamento: " + tarefas.size(),
                    SwingConstants.CENTER);
            resumo.setFont(new Font("Arial", Font.BOLD, 14));
            resumo.setForeground(new Color(230, 126, 34));
            resumo.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
            painel.add(resumo, BorderLayout.NORTH);

            String[] cols = {"Título", "Projeto", "Responsável", "Data Limite", "Prioridade", "Status"};
            DefaultTableModel modelo = new DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            JTable tabela = new JTable(modelo);
            tabela.setRowHeight(22);

            for (Tarefa t : tarefas) {
                modelo.addRow(new Object[]{
                    t.getTitulo(),
                    t.getProjeto() != null ? t.getProjeto().getNome() : "—",
                    t.getResponsavel() != null ? t.getResponsavel().getNome() : "—",
                    t.getDataLimite() != null ? t.getDataLimite() : "—",
                    t.getPrioridade().getDescricao(),
                    t.getStatus().getDescricao()
                });
            }
            painel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        } catch (SistemaException ex) {
            painel.add(new JLabel("Erro ao carregar: " + ex.getMessage()));
        }
        return painel;
    }

    // ---- ABA 3: Usuários por Perfil ----
    private JPanel criarAbaUsuariosPerfil() {
        JPanel painel = new JPanel(new BorderLayout(5, 5));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        try {
            Map<PerfilUsuario, List<Usuario>> mapa = usuarioCtrl.listarAgrupadosPorPerfil();

            // Painel de cartões com contagem por perfil (topo)
            JPanel cartoes = new JPanel(new GridLayout(1, 3, 10, 10));
            cartoes.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

            Color[] cores = {new Color(231, 76, 60), new Color(52, 152, 219), new Color(46, 204, 113)};
            int i = 0;
            for (Map.Entry<PerfilUsuario, List<Usuario>> entry : mapa.entrySet()) {
                JPanel cartao = new JPanel(new BorderLayout());
                cartao.setBackground(cores[i % cores.length]);
                cartao.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
                JLabel lblQtd = new JLabel(String.valueOf(entry.getValue().size()), SwingConstants.CENTER);
                lblQtd.setFont(new Font("Arial", Font.BOLD, 28));
                lblQtd.setForeground(Color.WHITE);
                JLabel lblNome = new JLabel(entry.getKey().getDescricao(), SwingConstants.CENTER);
                lblNome.setFont(new Font("Arial", Font.PLAIN, 13));
                lblNome.setForeground(new Color(220, 220, 220));
                cartao.add(lblQtd, BorderLayout.CENTER);
                cartao.add(lblNome, BorderLayout.SOUTH);
                cartoes.add(cartao);
                i++;
            }
            painel.add(cartoes, BorderLayout.NORTH);

            // Tabela completa de usuários
            String[] cols = {"Nome", "E-mail", "Cargo", "Perfil"};
            DefaultTableModel modelo = new DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            JTable tabela = new JTable(modelo);

            for (Map.Entry<PerfilUsuario, List<Usuario>> entry : mapa.entrySet()) {
                for (Usuario u : entry.getValue()) {
                    modelo.addRow(new Object[]{
                        u.getNome(), u.getEmail(), u.getCargo(), u.getPerfil().getDescricao()
                    });
                }
            }
            painel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        } catch (SistemaException ex) {
            painel.add(new JLabel("Erro: " + ex.getMessage()));
        }
        return painel;
    }

    // ---- ABA 4: Tarefas Atrasadas ----
    private JPanel criarAbaTarefasAtrasadas() {
        JPanel painel = new JPanel(new BorderLayout(5, 5));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        try {
            List<Tarefa> atrasadas = tarefaCtrl.listarAtrasadas();

            JLabel resumo = new JLabel("⚠  " + atrasadas.size() + " tarefa(s) com prazo vencido!",
                    SwingConstants.CENTER);
            resumo.setFont(new Font("Arial", Font.BOLD, 14));
            resumo.setForeground(atrasadas.isEmpty() ? new Color(39, 174, 96) : new Color(192, 57, 43));
            resumo.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
            painel.add(resumo, BorderLayout.NORTH);

            String[] cols = {"Título", "Projeto", "Responsável", "Data Limite", "Prioridade"};
            DefaultTableModel modelo = new DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            JTable tabela = new JTable(modelo);
            tabela.setRowHeight(22);

            for (Tarefa t : atrasadas) {
                modelo.addRow(new Object[]{
                    t.getTitulo(),
                    t.getProjeto() != null ? t.getProjeto().getNome() : "—",
                    t.getResponsavel() != null ? t.getResponsavel().getNome() : "—",
                    t.getDataLimite(),
                    t.getPrioridade().getDescricao()
                });
            }
            painel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        } catch (SistemaException ex) {
            painel.add(new JLabel("Erro: " + ex.getMessage()));
        }
        return painel;
    }
}
