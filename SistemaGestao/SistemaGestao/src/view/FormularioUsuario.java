// ============================================================
// ARQUIVO: view/FormularioUsuario.java
//
// Formulário modal para cadastro e edição de usuários.
// JDialog é uma janela de diálogo (janela filha do JFrame).
// ============================================================
package view;

import controller.UsuarioController;
import enums.PerfilUsuario;
import exception.SistemaException;
import model.Usuario;

import javax.swing.*;
import java.awt.*;

/**
 * Formulário de cadastro/edição de usuário.
 * É um JDialog (janela modal — bloqueia a janela pai enquanto aberta).
 */
public class FormularioUsuario extends JDialog {

    private JTextField  campoNome, campoCpf, campoEmail, campoCargo, campoLogin;
    private JPasswordField campoSenha;
    private JComboBox<PerfilUsuario> comboPerfil;
    private JButton     btnSalvar, btnCancelar;

    private final UsuarioController controller;
    private final Usuario usuarioEditando;  // null = novo usuário

    /**
     * @param parent          — janela pai (TelaUsuarios)
     * @param usuarioEditando — usuário a editar, ou null para novo
     * @param controller      — controller de negócio
     */
    public FormularioUsuario(JFrame parent, Usuario usuarioEditando,
                             UsuarioController controller) {
        super(parent, true);  // true = modal (bloqueia a janela pai)
        this.usuarioEditando = usuarioEditando;
        this.controller = controller;

        boolean isEdicao = (usuarioEditando != null);
        setTitle(isEdicao ? "Editar Usuário" : "Novo Usuário");
        setSize(420, 420);
        setLocationRelativeTo(parent);
        setResizable(false);

        criarComponentes(isEdicao);

        // Se for edição, preenche os campos com os dados existentes
        if (isEdicao) preencherCampos(usuarioEditando);
    }

    private void criarComponentes(boolean isEdicao) {
        setLayout(new BorderLayout(10, 10));

        // ---- FORMULÁRIO ----
        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        form.add(new JLabel("Nome Completo: *"));
        campoNome  = new JTextField();
        form.add(campoNome);

        form.add(new JLabel("CPF: *"));
        campoCpf   = new JTextField();
        form.add(campoCpf);

        form.add(new JLabel("E-mail: *"));
        campoEmail = new JTextField();
        form.add(campoEmail);

        form.add(new JLabel("Cargo:"));
        campoCargo = new JTextField();
        form.add(campoCargo);

        form.add(new JLabel("Login: *"));
        campoLogin = new JTextField();
        form.add(campoLogin);

        form.add(new JLabel("Senha: *"));
        campoSenha = new JPasswordField();
        form.add(campoSenha);

        form.add(new JLabel("Perfil: *"));
        // JComboBox é um dropdown com as opções do Enum
        comboPerfil = new JComboBox<>(PerfilUsuario.values());
        form.add(comboPerfil);

        // Nota sobre campos obrigatórios
        JLabel lblObrig = new JLabel("  * Campos obrigatórios");
        lblObrig.setFont(new Font("Arial", Font.ITALIC, 11));
        lblObrig.setForeground(Color.GRAY);
        form.add(lblObrig);

        add(form, BorderLayout.CENTER);

        // ---- BOTÕES ----
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        btnCancelar = new JButton("Cancelar");
        btnSalvar   = new JButton(isEdicao ? "💾 Atualizar" : "💾 Cadastrar");
        btnSalvar.setBackground(new Color(46, 204, 113));
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setFocusPainted(false);

        painelBotoes.add(btnCancelar);
        painelBotoes.add(btnSalvar);
        add(painelBotoes, BorderLayout.SOUTH);

        // ---- EVENTOS ----
        btnSalvar.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose());
    }

    // Preenche campos com dados do usuário existente
    private void preencherCampos(Usuario u) {
        campoNome.setText(u.getNome());
        campoCpf.setText(u.getCpf());
        campoEmail.setText(u.getEmail());
        campoCargo.setText(u.getCargo());
        campoLogin.setText(u.getLogin());
        campoSenha.setText(u.getSenha());
        comboPerfil.setSelectedItem(u.getPerfil());
    }

    // Coleta dados dos campos e chama o Controller
    private void salvar() {
        try {
            Usuario usuario = (usuarioEditando != null) ? usuarioEditando : new Usuario();

            usuario.setNome(campoNome.getText().trim());
            usuario.setCpf(campoCpf.getText().trim());
            usuario.setEmail(campoEmail.getText().trim());
            usuario.setCargo(campoCargo.getText().trim());
            usuario.setLogin(campoLogin.getText().trim());
            usuario.setSenha(new String(campoSenha.getPassword()).trim());
            usuario.setPerfil((PerfilUsuario) comboPerfil.getSelectedItem());

            // Decide se é cadastro novo ou atualização
            if (usuarioEditando == null) {
                controller.cadastrar(usuario);
                JOptionPane.showMessageDialog(this,
                        "Usuário cadastrado com sucesso! ✅",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                controller.atualizar(usuario);
                JOptionPane.showMessageDialog(this,
                        "Usuário atualizado com sucesso! ✅",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }

            dispose();  // Fecha o formulário

        } catch (SistemaException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(), "Erro de Validação", JOptionPane.ERROR_MESSAGE);
        }
    }
}
