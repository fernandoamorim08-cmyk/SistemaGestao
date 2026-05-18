// ============================================================
// ARQUIVO: view/TelaLogin.java
//
// CONCEITO - JAVA SWING:
//   Swing é a biblioteca gráfica do Java para criar janelas,
//   botões, campos de texto e outros componentes visuais.
//   Principais componentes usados:
//     JFrame    → janela principal
//     JPanel    → painel container para organizar componentes
//     JLabel    → rótulo de texto
//     JTextField→ campo de texto editável
//     JButton   → botão clicável
//     JPasswordField → campo de senha (oculta caracteres)
//
// CONCEITO - EVENTOS (ActionListener):
//   Em Swing, "ouvimos" ações do usuário (clique, tecla pressionada)
//   registrando um ActionListener. Quando o evento ocorre,
//   o método actionPerformed() é chamado automaticamente.
//
// PADRÃO MVC APLICADO:
//   Esta classe é a VIEW — ela só exibe e captura dados.
//   A lógica de autenticação fica no UsuarioController.
// ============================================================
package view;
import javax.swing.BorderFactory;

import controller.UsuarioController;
import exception.SistemaException;
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Tela de login do sistema.
 * Primeira tela que o usuário vê ao abrir o sistema.
 */
public class TelaLogin extends JFrame {

    // --------------------------------------------------------
    // Componentes da tela (atributos da View)
    // --------------------------------------------------------
    private JTextField  campoLogin;
    private JPasswordField campoSenha;
    private JButton     botaoEntrar;
    private JLabel      labelMensagem;

    // Controller responsável pela autenticação
    private final UsuarioController usuarioController = new UsuarioController();

    // --------------------------------------------------------
    // Construtor — monta a tela ao ser criado
    // --------------------------------------------------------
    public TelaLogin() {
        configurarJanela();
        criarComponentes();
        configurarEventos();
    }

    // --------------------------------------------------------
    // Configurações básicas da janela (JFrame)
    // --------------------------------------------------------
    private void configurarJanela() {
        setTitle("Sistema de Gestão de Projetos — Login");
        setSize(420, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Fecha ao clicar no X
        setLocationRelativeTo(null);    // Centraliza na tela
        setResizable(false);            // Impede redimensionar
        getContentPane().setBackground(new Color(45, 62, 80));
    }

    // --------------------------------------------------------
    // Criação e posicionamento dos componentes visuais
    // --------------------------------------------------------
    private void criarComponentes() {
        // Usamos BorderLayout para organizar em zonas (NORTH, CENTER, SOUTH...)
        setLayout(new BorderLayout());

        // --- Painel do título (topo) ---
        JPanel painelTitulo = new JPanel();
        painelTitulo.setBackground(new Color(41, 128, 185));

        // JPanel não possui o método setPadding().
        // Para criar espaçamento interno (padding), use uma EmptyBorder:
        painelTitulo.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JLabel labelTitulo = new JLabel("🗂  Sistema de Gestão");
        labelTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        labelTitulo.setForeground(Color.WHITE);
        painelTitulo.add(labelTitulo);

        // --- Painel central com formulário ---
        JPanel painelForm = new JPanel(new GridBagLayout());
        painelForm.setBackground(new Color(52, 73, 94));
        painelForm.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // GridBagConstraints define posição e tamanho de cada componente
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);  // Espaçamento entre componentes
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Label "Login"
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblLogin = new JLabel("Login:");
        lblLogin.setForeground(Color.WHITE);
        lblLogin.setFont(new Font("Arial", Font.PLAIN, 13));
        painelForm.add(lblLogin, gbc);

        // Campo de texto para login
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        campoLogin = new JTextField(15);
        campoLogin.setFont(new Font("Arial", Font.PLAIN, 13));
        painelForm.add(campoLogin, gbc);

        // Label "Senha"
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel lblSenha = new JLabel("Senha:");
        lblSenha.setForeground(Color.WHITE);
        lblSenha.setFont(new Font("Arial", Font.PLAIN, 13));
        painelForm.add(lblSenha, gbc);

        // Campo de senha (oculta o que é digitado)
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        campoSenha = new JPasswordField(15);
        campoSenha.setFont(new Font("Arial", Font.PLAIN, 13));
        painelForm.add(campoSenha, gbc);

        // Botão Entrar
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        botaoEntrar = new JButton("Entrar →");
        botaoEntrar.setBackground(new Color(46, 204, 113));
        botaoEntrar.setForeground(Color.WHITE);
        botaoEntrar.setFont(new Font("Arial", Font.BOLD, 14));
        botaoEntrar.setFocusPainted(false);
        botaoEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        painelForm.add(botaoEntrar, gbc);

        // Label para mensagens de erro/sucesso
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 5, 5, 5);
        labelMensagem = new JLabel("", SwingConstants.CENTER);
        labelMensagem.setFont(new Font("Arial", Font.ITALIC, 12));
        labelMensagem.setForeground(new Color(231, 76, 60));
        painelForm.add(labelMensagem, gbc);

        // Adiciona painéis à janela
        add(painelTitulo, BorderLayout.NORTH);
        add(painelForm, BorderLayout.CENTER);

        // Rodapé
        JLabel rodape = new JLabel("© 2024 Sistema Acadêmico — ADS", SwingConstants.CENTER);
        rodape.setForeground(new Color(127, 140, 141));
        rodape.setBorder(BorderFactory.createEmptyBorder(5, 0, 8, 0));
        rodape.setBackground(new Color(45, 62, 80));
        rodape.setOpaque(true);
        add(rodape, BorderLayout.SOUTH);
    }

    // --------------------------------------------------------
    // Configuração dos eventos (o que acontece ao clicar, teclar...)
    // --------------------------------------------------------
    private void configurarEventos() {

        // Evento do botão Entrar
        botaoEntrar.addActionListener(e -> tentarLogin());

        // Pressionar Enter no campo senha também faz login
        campoSenha.addActionListener(e -> tentarLogin());

        // Pressionar Enter no campo login move o foco para senha
        campoLogin.addActionListener(e -> campoSenha.requestFocus());
    }

    // --------------------------------------------------------
    // Lógica de login (chama o Controller)
    // --------------------------------------------------------
    private void tentarLogin() {
        String login = campoLogin.getText().trim();
        // getPassword() retorna char[] por segurança; convertemos para String
        String senha = new String(campoSenha.getPassword()).trim();

        try {
            // Delega ao Controller (MVC: View não contém lógica de negócio)
            Usuario usuario = usuarioController.fazerLogin(login, senha);

            // Login bem-sucedido: abre o menu principal
            new TelaPrincipal(usuario).setVisible(true);
            this.dispose();  // Fecha esta janela de login

        } catch (SistemaException ex) {
            // Exibe mensagem de erro sem fechar a tela
            labelMensagem.setText(ex.getMessage());
            campoSenha.setText("");
            campoSenha.requestFocus();
        }
    }

    // Método auxiliar para adicionar padding ao painel
    // (JPanel não tem método setPadding nativo, usamos Border)
    // Nota: o método abaixo é um helper didático
}
