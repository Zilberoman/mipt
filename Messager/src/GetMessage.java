import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

class GetMessage implements Runnable {
    private Socket client;
    private JTextArea textArea;

    GetMessage(Socket client, JTextArea textArea) {
        this.client = client;
        this.textArea = textArea;
    }

    @Override
    public void run() {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String line;
        try {
            while (true) {
                line = in.readLine();

                if (line == null)
                    break;

                appendText(line);
            }
        }
        catch (IOException e){}
        finally {
            try {
                in.close();
            }
            catch (IOException e){}
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