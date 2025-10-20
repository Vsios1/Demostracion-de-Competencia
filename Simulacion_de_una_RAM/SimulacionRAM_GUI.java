import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SimulacionRAM_GUI extends JFrame {

    private byte[] RAM = new byte[8];
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtDireccion, txtValor;

    public SimulacionRAM_GUI() {
        setTitle("Simulación de Memoria RAM");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Tabla
        tableModel = new DefaultTableModel(new Object[]{"Dirección", "Valor"}, 0);
        table = new JTable(tableModel);
        actualizarTabla();
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Panel inferior
        JPanel panel = new JPanel(new FlowLayout());
        txtDireccion = new JTextField(5);
        txtValor = new JTextField(5);
        JButton btnEscribir = new JButton("Escribir");
        JButton btnLeer = new JButton("Leer");
        JButton btnLimpiar = new JButton("Limpiar");

        panel.add(new JLabel("Dirección:"));
        panel.add(txtDireccion);
        panel.add(new JLabel("Valor:"));
        panel.add(txtValor);
        panel.add(btnEscribir);
        panel.add(btnLeer);
        panel.add(btnLimpiar);

        add(panel, BorderLayout.SOUTH);

        // Eventos
        btnEscribir.addActionListener(e -> {
            try {
                int dir = Integer.parseInt(txtDireccion.getText());
                byte val = Byte.parseByte(txtValor.getText());
                RAM[dir] = val;
                actualizarTabla();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: dirección o valor inválido");
            }
        });

        btnLeer.addActionListener(e -> {
            try {
                int dir = Integer.parseInt(txtDireccion.getText());
                JOptionPane.showMessageDialog(this, "Valor en dirección " + dir + " = " + RAM[dir]);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: dirección inválida");
            }
        });

        btnLimpiar.addActionListener(e -> {
            for (int i = 0; i < RAM.length; i++) RAM[i] = 0;
            actualizarTabla();
        });
    }

    private void actualizarTabla() {
        tableModel.setRowCount(0);
        for (int i = 0; i < RAM.length; i++) {
            tableModel.addRow(new Object[]{i, RAM[i]});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SimulacionRAM_GUI().setVisible(true));
    }
}
