package sorveteria;

import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class Login extends JFrame {
    
    private final JTextField usuario = new JTextField(15);
    private final JPasswordField senha = new JPasswordField(15);

    public Login() {
        
        setTitle("Sorveteria - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(350, 190);
        setLocationRelativeTo(null);
        
        JPanel painel = new JPanel(new GridLayout(3, 2, 8, 8));
        
        painel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        painel.add(new JLabel("Usuário:")); painel.add(usuario);
        painel.add(new JLabel("Senha:")); painel.add(senha);
        
        JButton entrar = new JButton("Entrar");
        
        painel.add(new JLabel()); painel.add(entrar);
        add(painel);

        entrar.addActionListener(e -> entrar());
        getRootPane().setDefaultButton(entrar);
    }

    private void entrar() {
        String sql = "SELECT id FROM funcionarios WHERE usuario=? AND senha=?";
        try (Connection c = Conexao.abrir(); PreparedStatement p = c.prepareStatement(sql)) {
            
            p.setString(1, usuario.getText().trim());
            p.setString(2, new String(senha.getPassword()));
            
            try (ResultSet r = p.executeQuery()) {
                
                if (r.next()) {
                    new Menu().setVisible(true);
                    dispose();
                } 
                
                else JOptionPane.showMessageDialog(this, "Usuário ou senha incorretos.");
            }
        } 
        
        catch (SQLException e) { Conexao.erro(e); }

    }
}
