import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ClientFactory extends JFrame
        implements KeyListener, ActionListener{
//TODO check strings
    private JButton addUser;
    private JTextField nickInput;
    private JLabel aboutNick;

    public ClientFactory(){
        initComponents();
        addUser.addActionListener(this);
        nickInput.addKeyListener(this);
    }

    private void initComponents(){
        aboutNick = new JLabel("Input user nick:");
        addUser = new JButton("Add user");
        nickInput = new JTextField();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Client Factory");

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        layout.setVerticalGroup(
                layout.createSequentialGroup()

                .addComponent(aboutNick)
                .addGap(10)
                .addComponent(nickInput, 0, 25, 25)
                .addGap(100)
                .addComponent(addUser)
        );

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(aboutNick)
                .addComponent(nickInput, 100, 100, Short.MAX_VALUE)
                .addComponent(addUser)
        );

        setSize(300, 300);
    }

    private void addUser(){

        if (!nickInput.getText().isEmpty()) {
            new Client(nickInput.getText()).setVisible(true);
            nickInput.setText("");
            return;
        }

        JOptionPane.showConfirmDialog(this,
                "Input correct nick",
                "Error",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        addUser();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
            addUser();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
