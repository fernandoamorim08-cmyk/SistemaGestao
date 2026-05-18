// ============================================================
// ARQUIVO: view/TelaPrincipal.java
//
// Menu principal do sistema. Exibe botões de navegação
// para cada módulo (Usuários, Projetos, Equipes, Tarefas,
// Relatórios) e mostra informações do usuário logado.
// ============================================================
package view;

import controller.UsuarioController;
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Tela principal (menu) do sistema.
 * Ponto de navegação para todos os módulos.
 */
public class TelaPrincipal extends JFrame {

    private final Usuario usuarioLogado;

    public TelaPrincipal(Usuario usuario) {
        this.usuarioLogado = usuario;
        configurarJanela();
        criarComponentes();
    }

    private void configurarJanela() {
        setTitle("Sistema de Gestão de Projetos — Menu Principal");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(new Color(236, 240, 241));
    }

    private void criarComponentes() {
        setLayout(new BorderLayout(10, 10));

        // ---- CABEÇALHO ----
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setBackground(new Color(41, 128, 185));
        cabecalho.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblSistema = new JLabel("🗂  Sistema de Gestão de Projetos");
        lblSistema.setFont(new Font("Arial", Font.BOLD, 20));
        lblSistema.setForeground(Color.WHITE);

        JLabel lblUsuario = new JLabel("Olá, " + usuarioLogado.getNome() +
                " | " + usuarioLogado.getPerfil().getDescricao());
        lblUsuario.setFont(new Font("Arial", Font.PLAIN, 12));
        lblUsuario.setForeground(new Color(200, 230, 255));

        cabecalho.add(lblSistema, BorderLayout.WEST);
        cabecalho.add(lblUsuario, BorderLayout.EAST);
        add(cabecalho, BorderLayout.NORTH);

        // ---- GRADE DE BOTÕES (menu) ----
        JPanel painelBotoes = new JPanel(new GridLayout(2, 3, 15, 15));
        painelBotoes.setBackground(new Color(236, 240, 241));
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        // Cria botão de módulo com ícone e descrição
        painelBotoes.add(criarBotaoModulo("👤", "Usuários",
                "Cadastrar e gerenciar usuários",
                new Color(52, 152, 219),
                e -> new TelaUsuarios().setVisible(true)));

        painelBotoes.add(criarBotaoModulo("📁", "Projetos",
                "Gerenciar projetos",
                new Color(46, 204, 113),
                e -> new TelaProjetos(usuarioLogado).setVisible(true)));

        painelBotoes.add(criarBotaoModulo("👥", "Equipes",
                "Montar e gerenciar equipes",
                new Color(155, 89, 182),
                e -> new TelaEquipes().setVisible(true)));

        painelBotoes.add(criarBotaoModulo("✅", "Tarefas",
                "Acompanhar tarefas",
                new Color(230, 126, 34),
                e -> new TelaTarefas(usuarioLogado).setVisible(true)));

        painelBotoes.add(criarBotaoModulo("📊", "Relatórios",
                "Gerar relatórios gerenciais",
                new Color(231, 76, 60),
                e -> new TelaRelatorios().setVisible(true)));

        painelBotoes.add(criarBotaoModulo("🚪", "Sair",
                "Voltar para o login",
                new Color(127, 140, 141),
                e -> fazerLogout()));

        add(painelBotoes, BorderLayout.CENTER);

        // ---- RODAPÉ ----
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rodape.setBackground(new Color(189, 195, 199));
        rodape.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        JLabel lblRodape = new JLabel("Versão 1.0 — ADS Acadêmico  |  " +
                java.time.LocalDate.now().toString());
        lblRodape.setFont(new Font("Arial", Font.PLAIN, 11));
        lblRodape.setForeground(new Color(80, 80, 80));
        rodape.add(lblRodape);
        add(rodape, BorderLayout.SOUTH);
    }

    /**
     * Cria um botão de módulo estilizado com ícone, título e descrição.
     * Demonstra reutilização de código — um método cria vários botões iguais.
     */
    private JPanel criarBotaoModulo(String icone, String titulo,
                                     String descricao, Color cor,
                                     ActionListener acao) {
        JPanel painel = new JPanel(new BorderLayout(5, 5));
        painel.setBackground(cor);
        painel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        painel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblIcone = new JLabel(icone, SwingConstants.CENTER);
        lblIcone.setFont(new Font("Arial", Font.PLAIN, 32));

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblDesc = new JLabel("<html><center>" + descricao + "</center></html>",
                SwingConstants.CENTER);
        lblDesc.setFont(new Font("Arial", Font.PLAIN, 11));
        lblDesc.setForeground(new Color(220, 220, 220));

        JPanel centro = new JPanel(new GridLayout(3, 1, 3, 3));
        centro.setOpaque(false);
        centro.add(lblIcone);
        centro.add(lblTitulo);
        centro.add(lblDesc);
        painel.add(centro, BorderLayout.CENTER);

        // Adiciona efeito hover (escurece ao passar o mouse)
        painel.addMouseListener(new MouseAdapter() {
            Color corOriginal = cor;

            @Override
            public void mouseEntered(MouseEvent e) {
                painel.setBackground(cor.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                painel.setBackground(corOriginal);
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                acao.actionPerformed(new ActionEvent(painel, ActionEvent.ACTION_PERFORMED, ""));
            }
        });

        return painel;
    }

    private void fazerLogout() {
        new UsuarioController().fazerLogout();
        new TelaLogin().setVisible(true);
        this.dispose();
    }
}
