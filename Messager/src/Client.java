
import javax.swing.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class Client extends JFrame
        implements KeyListener, ActionListener, WindowListener {

    private JTextField textField;
    private JTextField portInput;
    private JTextArea messages;
    private JScrollPane forMessages;
    private JButton sendButton;
    private JButton disconnectButton;
    private JButton connectButton;
    private JLabel port;
    private JLabel infoMessages;
    private Socket clientSocket = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private String nickName;
    private GetMessage getMessage;

    public Client(String name){
        nickName = name;
        initComponents();
        textField.addKeyListener(this);
        sendButton.addActionListener(this);
        connectButton.addActionListener(this);
        disconnectButton.addActionListener(this);
        portInput.addKeyListener(this);
        addWindowListener(this);
    }

    private void initComponents(){
        infoMessages = new JLabel("Messages:");
        textField = new JTextField();
        port = new JLabel("Port: ");
        sendButton = new JButton("Send");
        disconnectButton = new JButton("Disconnect");
        connectButton = new JButton("Connect");
        portInput = new JTextField();
        messages = new JTextArea();
        forMessages = new JScrollPane(messages);


        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(nickName);

        portInput.setName("port");
        textField.setColumns(30);
        disconnectButton.setEnabled(false);
        sendButton.setEnabled(false);
        messages.setEditable(false);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createSequentialGroup()

                    .addComponent(port)

                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(portInput, 0, 75, 75)
                            .addComponent(connectButton)
                            .addComponent(disconnectButton)
                        )

                        .addComponent(infoMessages)
                        .addComponent(forMessages)

                        .addGroup(layout.createSequentialGroup()
                            .addComponent(textField)
                            .addComponent(sendButton)
                        )
                    )
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)

                        .addComponent(port)
                        .addComponent(portInput)
                        .addComponent(connectButton)
                        .addComponent(disconnectButton)
                    )

                    .addComponent(infoMessages)
                    .addComponent(forMessages, 0, 200, Short.MAX_VALUE)

                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(textField, 0, 25 , 25)
                            .addComponent(sendButton)
                    )
        );

        pack();
    }

    private void checkConnection(){
        while (!clientSocket.isClosed());
        accessToButtons(true, false, false);
    }

    private void connect(){
        try {
            if (!new Scanner(portInput.getText()).hasNextInt()){
                JOptionPane.showConfirmDialog(
                        this,
                        "Input correct port",
                        "Error",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            clientSocket = new Socket("localHost", Integer.parseInt(portInput.getText()));
            getMessage = new GetMessage(clientSocket, messages);
            new Thread(getMessage).start();
            new Thread(this::checkConnection).start();
            accessToButtons(false, true, true);
            sendMessage(" Connected");
        }
        catch (ConnectException e){
            JOptionPane.showConfirmDialog(this,
                    "Cann't connect to server with same port",
                    "Error",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.ERROR_MESSAGE
            );
        }
        catch (IllegalArgumentException e){
            JOptionPane.showConfirmDialog(this,
                    "Input correct port number",
                    "Error",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.ERROR_MESSAGE
            );
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() throws IOException {
        if (!clientSocket.isClosed())
            sendMessage(" - have been disconnected");
        try {
            Thread.sleep(3);
        }
        catch (InterruptedException e){}

        in.close();
        out.close();
        clientSocket.close();
        setTitle(nickName);
        accessToButtons(true, false, false);
    }

    private void sendMessage(String text){
        if (text.isEmpty()){
            JOptionPane.showConfirmDialog(this,
                    "Input something",
                    "Warning",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out.println(nickName + ": " + text);
            textField.setText("");

        }catch (SocketException e){
            JOptionPane.showConfirmDialog(this,
                    "Firstly need to connect",
                    "Warning",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void accessToButtons(boolean con, boolean discon, boolean send){
        connectButton.setEnabled(con);
        disconnectButton.setEnabled(discon);
        sendButton.setEnabled(send);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sendButton){
            sendMessage(textField.getText());
            return;
        }

        if (e.getSource() == connectButton){
            connect();
            return;
        }

        if (clientSocket == null)
            return;

        try {
            disconnect();
        } catch (IOException e1) {
            System.out.println("Can't close socket");
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        JTextField source = (JTextField) e.getSource();

        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (source.getName() == null) {
                sendMessage(textField.getText());
                return;
            }

            if (clientSocket == null || clientSocket.isClosed())
                connect();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        if (clientSocket == null)
            return;

        try{
            disconnect();
        }
        catch (IOException ex){
            System.out.println("Can't close socket");
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
