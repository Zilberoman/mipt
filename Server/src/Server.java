
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Scanner;

public class Server extends JFrame
        implements WindowListener, ActionListener, KeyListener {

    private JTextArea textArea;
    private JButton startServer;
    private JButton stopServer;
    private JLabel portInfo;
    private JScrollPane scrollPane;
    private ServerSocket serverSocket = null;
    private JTextField portInput;
    private LinkedList<AcceptClient> clients = new LinkedList<>();
    int portNumb;

    public Server(){
        initComponents();
        addWindowListener(this);
        startServer.addActionListener(this);
        stopServer.addActionListener(this);
        portInput.addKeyListener(this);
    }

    private void initComponents(){

        textArea = new JTextArea();
        startServer = new JButton("Start Server");
        stopServer = new JButton("Stop Server");
        scrollPane = new JScrollPane(textArea);
        portInfo = new JLabel("Port: ");
        portInput = new JTextField();

        textArea.setEditable(false);
        stopServer.setEnabled(false);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addGroup(layout.createSequentialGroup()

                            .addComponent(portInfo)
                            .addComponent(portInput)
                        )

                        .addGroup(layout.createSequentialGroup()

                                .addComponent(startServer)
                                .addComponent(stopServer)
                        )

                        .addComponent(scrollPane)
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()

                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(portInfo)
                            .addComponent(portInput, 0, 25, 25)
                        )

                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(startServer)
                            .addComponent(stopServer)
                        )

                        .addComponent(scrollPane)
        );

        setSize(300, 300);
        setTitle("Server name: localHost");
    }

    private void Start(){
        try {
            portNumb = Integer.parseInt(portInput.getText());
            serverSocket = new ServerSocket(portNumb);
            textArea.append("Server with port - "+ portNumb + " start\n");
            while (true) {
                AcceptClient worker;
                worker = new AcceptClient(serverSocket.accept(), textArea, clients);
                clients.add(worker);
                Thread t = new Thread(worker);
                t.start();
            }
        }
        catch (SocketException e){
            textArea.append("Server with port - " + portNumb + " shut down\n");
            stopServer.setEnabled(false);
            startServer.setEnabled(true);
        }
        catch (IllegalArgumentException e){
            textArea.append("Can't create server with port " + portNumb + "\n");
            stopServer.setEnabled(false);
            startServer.setEnabled(true);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean getPort(){
        return new Scanner(portInput.getText()).hasNextInt();
    }

    private void closeServer(){
        for (AcceptClient client : clients) {
            try {
                client.getOut().println("You are disconnected, because server shut down");
                client.getClient().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (serverSocket != null)
            try{
                serverSocket.close();
            }
            catch (Exception ex){
                System.out.println("Can't close socket");
            }
    }

    private void Prepare(){
        startServer.setEnabled(false);
        stopServer.setEnabled(true);

        Thread t = new Thread(this::Start);
        t.start();
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        closeServer();
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startServer && getPort()){
            Prepare();

            return;
        }

        if (e.getSource() == stopServer) {
            portInput.setEditable(true);
            stopServer.setEnabled(false);
            startServer.setEnabled(true);
            closeServer();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER && getPort())
            Prepare();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
