// ============================================================
// ARQUIVO: main/Main.java
//
// PONTO DE ENTRADA DA APLICAÇÃO
//
// Todo programa Java começa executando o método:
//   public static void main(String[] args)
//
// CONCEITO - SwingUtilities.invokeLater():
//   O Swing tem uma thread própria chamada EDT (Event Dispatch Thread).
//   Toda criação e modificação de componentes gráficos deve
//   acontecer nessa thread para evitar problemas de concorrência.
//   invokeLater() agenda a execução na EDT corretamente.
// ============================================================
package main;

import util.Conexao;
import view.TelaLogin;

import javax.swing.*;

/**
 * Classe principal — ponto de entrada do Sistema de Gestão.
 *
 * Para executar:
 *   1. Certifique-se de que o MySQL está rodando
 *   2. Execute o script banco_de_dados.sql
 *   3. Ajuste as credenciais em util/Conexao.java
 *   4. Execute esta classe
 */
public class Main {

    public static void main(String[] args) {

        // --------------------------------------------------------
        // Aplica o visual "Nimbus" — mais moderno que o padrão Java
        // --------------------------------------------------------
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Se Nimbus não estiver disponível, usa o padrão do sistema
            System.out.println("Look and Feel Nimbus não disponível. Usando padrão.");
        }

        // --------------------------------------------------------
        // Testa a conexão com o banco antes de abrir a interface
        // --------------------------------------------------------
        System.out.println("=== Sistema de Gestão de Projetos e Equipes ===");
        System.out.println("Versão 1.0 — ADS Acadêmico");
        System.out.println("Iniciando...");

        if (Conexao.getConexao() == null) {
            JOptionPane.showMessageDialog(null,
                "Não foi possível conectar ao banco de dados.\n\n" +
                "Verifique:\n" +
                "• MySQL está em execução\n" +
                "• Banco 'sistema_gestao' foi criado\n" +
                "• Credenciais em util/Conexao.java estão corretas",
                "Erro de Conexão",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // --------------------------------------------------------
        // Abre a tela de login na EDT (thread do Swing)
        // --------------------------------------------------------
        SwingUtilities.invokeLater(() -> {
            TelaLogin telaLogin = new TelaLogin();
            telaLogin.setVisible(true);
            System.out.println("Interface gráfica iniciada.");
        });
    }
}
