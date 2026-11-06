package sorveteria;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Funcionarios extends JFrame {

    private final JTextField nome = new JTextField(), cargo = new JTextField(), telefone = new JTextField(), usuario = new JTextField();
    
    private final JPasswordField senha = new JPasswordField();
    
    private final DefaultTableModel modelo = new DefaultTableModel(new String[]{"ID", "Nome", "Cargo", "Telefone", "Usuário", "Senha"}, 0) {
        public boolean isCellEditable(int linha, int coluna) 
        { return false; }
    };

    private final JTable tabela = new JTable(modelo);

    public Funcionarios() {
        
        setTitle("Cadastro de Funcionários"); setSize(850, 500); setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        JPanel campos = new JPanel(new GridLayout(5, 2, 5, 5));
       
        campos.add(new JLabel("Nome:")); campos.add(nome);
        campos.add(new JLabel("Cargo:")); campos.add(cargo);
        campos.add(new JLabel("Telefone:")); campos.add(telefone);
        campos.add(new JLabel("Usuário:")); campos.add(usuario);
        campos.add(new JLabel("Senha:")); campos.add(senha);
        
        JPanel botoes = new JPanel();
        JButton salvar = new JButton("Cadastrar"), editar = new JButton("Editar"), excluir = new JButton("Excluir"), listar = new JButton("Listar / Atualizar"), limpar = new JButton("Limpar");
        
        for (JButton b : new JButton[]{salvar, editar, excluir, listar, limpar}) 
            botoes.add(b);
        
        JPanel topo = new JPanel(new BorderLayout()); topo.add(campos, BorderLayout.CENTER); topo.add(botoes, BorderLayout.SOUTH);
        
        add(topo, BorderLayout.NORTH); add(new JScrollPane(tabela), BorderLayout.CENTER);
        
        salvar.addActionListener(e -> gravar(false)); editar.addActionListener(e -> gravar(true));
        excluir.addActionListener(e -> excluir()); listar.addActionListener(e -> listar()); limpar.addActionListener(e -> limpar());
        
        tabela.getSelectionModel().addListSelectionListener(e -> {
            
            int l = tabela.getSelectedRow();
            
            if (!e.getValueIsAdjusting() && l >= 0) {
                nome.setText(valor(l, 1)); cargo.setText(valor(l, 2)); telefone.setText(valor(l, 3));
                usuario.setText(valor(l, 4)); senha.setText(valor(l, 5));
            }
        });

        listar();
    }
    
    private String valor(int l, int c) { Object v = modelo.getValueAt(l, c); return v == null ? "" : v.toString(); }
    
    private Integer id() { int l = tabela.getSelectedRow(); return l < 0 ? null : (Integer) modelo.getValueAt(l, 0); }
    
    private void limpar() { 

        tabela.clearSelection(); 

        for (JTextField f : new JTextField[]{nome, cargo, telefone, usuario, senha}) 
            f.setText(""); 
        }
    
    private void listar() {
        
        modelo.setRowCount(0);
        
        try (Connection c = Conexao.abrir(); Statement s = c.createStatement(); ResultSet r = s.executeQuery("SELECT * FROM funcionarios ORDER BY id")) {
            while (r.next()) modelo.addRow(new Object[]{r.getInt("id"), r.getString("nome"), r.getString("cargo"), r.getString("telefone"), r.getString("usuario"), r.getString("senha")});
        } 
        
        catch (SQLException e) { Conexao.erro(e); }

    }

    private void gravar(boolean edicao) {
        Integer codigo = id();

        if (edicao && codigo == null) { JOptionPane.showMessageDialog(this, "Selecione um funcionário."); 
                return; }

        if (nome.getText().isBlank() || cargo.getText().isBlank() || usuario.getText().isBlank() || senha.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Preencha nome, cargo, usuário e senha."); return;
        }

        String sql = edicao ? "UPDATE funcionarios SET nome=?, cargo=?, telefone=?, usuario=?, senha=? WHERE id=?" : "INSERT INTO funcionarios(nome,cargo,telefone,usuario,senha) VALUES(?,?,?,?,?)";
        
        try (Connection c = Conexao.abrir(); 
                PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, nome.getText().trim()); 
                p.setString(2, cargo.getText().trim()); 
                    p.setString(3, telefone.getText().trim());
                        p.setString(4, usuario.getText().trim()); p.setString(5, new String(senha.getPassword()));
            
                        if (edicao) p.setInt(6, codigo);
            p.executeUpdate(); limpar(); listar(); JOptionPane.showMessageDialog(this, "Funcionário salvo!");
        } 
        
        catch (SQLException e) { Conexao.erro(e); }

    }

    private void excluir() {
        Integer codigo = id();

        if (codigo == null) { JOptionPane.showMessageDialog(this, "Selecione um funcionário."); return; }
       
        if (JOptionPane.showConfirmDialog(this, "Excluir funcionário?", "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        
        try (Connection c = Conexao.abrir(); 
                PreparedStatement p = c.prepareStatement("DELETE FROM funcionarios WHERE id=?")) {
            p.setInt(1, codigo); p.executeUpdate(); limpar(); listar();
        } 
        
        catch (SQLException e) { Conexao.erro(e); }

    }
}
