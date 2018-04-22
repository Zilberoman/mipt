import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;

class AcceptClient implements Runnable {
    private Socket client;
    private JTextArea textArea;
    private LinkedList<AcceptClient> messageTo;
    private PrintWriter out = null;

    AcceptClient(Socket client, JTextArea textArea, LinkedList<AcceptClient> clients) {
        this.client = client;
        this.textArea = textArea;
        messageTo = clients;
    }

    public PrintWriter getOut(){
        return out;
    }

    public Socket getClient(){
        return client;
    }

    public void run() {
        String line = null;
        BufferedReader in = null;

        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("in or out failed");
        }

        try {
            while (true) {
                line = in.readLine();

                if (line == null)
                    break;

                String message = line.split(":", 2)[1];

                for (AcceptClient client : messageTo) {
                    if (client.getClient() == this.client)
                        out.println("You:" + message);
                    else
                        client.getOut().println(line);
                }

                appendText(line);
            }
        }
        catch (IOException e){}
        finally {
                try {
                    in.close();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public synchronized void appendText(String line) {
        textArea.append(line + "\n");
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            client.close();
        }
        catch (IOException e){
            System.out.println("Can't close socket");
        }
    }
}