package sorveteria;

import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Produtos extends JFrame {
    
    private final JTextField nome = new JTextField(), descricao = new JTextField(), preco = new JTextField(), estoque = new JTextField();
    
    private final DefaultTableModel modelo = new DefaultTableModel(new String[]{"ID", "Nome", "Descrição", "Preço", "Estoque"}, 0) {
        public boolean isCellEditable(int l, int c) { return false; }
    };
    
    private final JTable tabela = new JTable(modelo);
    
    public Produtos() {
        
        setTitle("Cadastro de Sorvetes"); setSize(750, 450); setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        JPanel campos = new JPanel(new GridLayout(4, 2, 5, 5));
        
        campos.add(new JLabel("Nome:")); campos.add(nome);
        campos.add(new JLabel("Descrição:")); campos.add(descricao);
        campos.add(new JLabel("Preço (ex: 12.50):")); campos.add(preco);
        campos.add(new JLabel("Estoque:")); campos.add(estoque);
        
        JPanel botoes = new JPanel();
        JButton cadastrar = new JButton("Cadastrar"), editar = new JButton("Editar"), excluir = new JButton("Excluir"), listar = new JButton("Listar / Atualizar"), limpar = new JButton("Limpar");
        
        for (JButton b : new JButton[]{cadastrar, editar, excluir, listar, limpar}) 
            botoes.add(b);
        
        JPanel topo = new JPanel(new BorderLayout()); topo.add(campos, BorderLayout.CENTER); topo.add(botoes, BorderLayout.SOUTH);
        
        add(topo, BorderLayout.NORTH); add(new JScrollPane(tabela), BorderLayout.CENTER);
        
        cadastrar.addActionListener(e -> gravar(false)); editar.addActionListener(e -> gravar(true));
        excluir.addActionListener(e -> excluir()); listar.addActionListener(e -> listar()); limpar.addActionListener(e -> limpar());
        
        tabela.getSelectionModel().addListSelectionListener(e -> {
            
            int l = tabela.getSelectedRow();
            
            if (!e.getValueIsAdjusting() && l >= 0) {
                nome.setText(valor(l, 1)); descricao.setText(valor(l, 2)); 
                preco.setText(valor(l, 3)); estoque.setText(valor(l, 4));
            }

        });

        listar();
    }

    private String valor(int l, int c) { Object v = modelo.getValueAt(l, c); return v == null ? "" : v.toString(); }
    
    private Integer id() { int l = tabela.getSelectedRow(); return l < 0 ? null : (Integer) modelo.getValueAt(l, 0); }
    
    private void limpar() { tabela.clearSelection(); 
        for (JTextField f : new JTextField[]{nome, descricao, preco, estoque}) f.setText(""); }

    private void listar() {
        
        modelo.setRowCount(0);
        
        try (Connection c = Conexao.abrir(); Statement s = c.createStatement(); 
                ResultSet r = s.executeQuery("SELECT * FROM produtos ORDER BY id")) {
            
                    while (r.next()) 
                        modelo.addRow(new Object[]{r.getInt("id"), r.getString("nome"), r.getString("descricao"), r.getBigDecimal("preco"), r.getInt("estoque")});
        } 
        
        catch (SQLException e) { Conexao.erro(e); }

    }

    private void gravar(boolean edicao) {
        
        Integer codigo = id();
        if (edicao && codigo == null) { JOptionPane.showMessageDialog(this, "Selecione um produto."); return; }
        
        try {
            
            if (nome.getText().isBlank()) throw new IllegalArgumentException("Informe o nome.");
            
            BigDecimal valor = new BigDecimal(preco.getText().trim().replace(',', '.'));
            int quantidade = Integer.parseInt(estoque.getText().trim());
            
            if (valor.signum() < 0 || quantidade < 0) throw new IllegalArgumentException("Preço e estoque não podem ser negativos.");
            
            String sql = edicao ? "UPDATE produtos SET nome=?, descricao=?, preco=?, estoque=? WHERE id=?" : "INSERT INTO produtos(nome,descricao,preco,estoque) VALUES(?,?,?,?)";
            
            try (Connection c = Conexao.abrir(); PreparedStatement p = c.prepareStatement(sql)) {
                p.setString(1, nome.getText().trim()); 
                p.setString(2, descricao.getText().trim()); 
                p.setBigDecimal(3, valor); 
                p.setInt(4, quantidade);
                
                if (edicao) p.setInt(5, codigo);

                p.executeUpdate(); limpar(); listar(); JOptionPane.showMessageDialog(this, "Produto salvo!");
            }
        } 
        
        catch (SQLException | IllegalArgumentException e) { Conexao.erro(e); }

    }

    private void excluir() {
        Integer codigo = id();
        
        if (codigo == null) { JOptionPane.showMessageDialog(this, "Selecione um produto."); 
            return; }
        
        if (JOptionPane.showConfirmDialog(this, "Excluir produto?", "Confirmar", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) 
            return;
        
        try (Connection c = Conexao.abrir(); PreparedStatement p = c.prepareStatement("DELETE FROM produtos WHERE id=?")) {
            p.setInt(1, codigo); p.executeUpdate(); limpar(); listar();
        } 
        
        catch (SQLException e) 
            { Conexao.erro(e); }

    }
}
