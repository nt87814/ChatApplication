import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/***************************************************************************************
        *    Title: Medium source code
        *    Author: Pavindu Lakshan
        *    Date: 2020
        *    Code version: 1.0
        *    Availability: https://medium.com
        *
 ***************************************************************************************/

class MessageSender implements Runnable {
    // The server port to which the client socket is going to connect
    public final static int SERVICE_PORT = 50001;
    private DatagramSocket clientSocket;
    private String hostname;

    MessageSender(DatagramSocket s, String h) {
        // Instantiate client socket. No need to bind to a specific port
        clientSocket = s;
        hostname = h;
    }

    private void sendMessage(String s) throws Exception {
        byte[] sendingDataBuffer;
        // Converting data to bytes and storing them in the sending buffer
        sendingDataBuffer = s.getBytes();

        // Get the IP address of the server
        InetAddress IPAddress = InetAddress.getByName(hostname);
        // Creating a UDP packet
        DatagramPacket sendingPacket = new DatagramPacket(sendingDataBuffer, sendingDataBuffer.length, IPAddress, SERVICE_PORT);
        // sending UDP packet to the server
        clientSocket.send(sendingPacket);
    }

    public void run() {
        boolean connected = false;
        do {
            try {
                sendMessage("GREETINGS");
                connected = true;
            } catch (Exception e) {

            }
        } while (!connected);

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                while (!in.ready()) {
                    Thread.sleep(100);
                }
                sendMessage(in.readLine());
            } catch(Exception e) {
                System.err.println(e);
            }
        }
    }
}

class MessageReceiver implements Runnable {
    DatagramSocket clientSocket;
    byte receivingDataBuffer[];
    MessageReceiver(DatagramSocket s) {
        clientSocket = s;
        receivingDataBuffer = new byte[1024];
    }
    public void run() {
        while (true) {
            try {
                // Get the server response
                DatagramPacket recevingPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
                clientSocket.receive(recevingPacket);
                String received = new String(recevingPacket.getData(), 0, recevingPacket.getLength());
                // Printing the received data
                System.out.println(received);
            } catch(Exception e) {
                System.err.println(e);
            }
        }
    }
}

public class UDPClient{

    public static void main(String[] args) throws IOException{

        String host = null;
        if (args.length < 1) {
            System.out.println("Usage: java ChatClient <server_hostname>");
            System.exit(0);
        } else {
            host = args[0];
        }
            //InetAddress IPAddress = InetAddress.getByName("localhost");

            // Closing the socket connection with the server
            //clientSocket.close();

        DatagramSocket socket = new DatagramSocket();
        MessageReceiver r = new MessageReceiver(socket);
        MessageSender s = new MessageSender(socket, host);
        Thread rt = new Thread(r);
        Thread st = new Thread(s);
        rt.start(); st.start();
    }
}
