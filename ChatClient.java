import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

final class ChatClient {
    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private Socket socket;

    private final String server;
    private final String username;
    private final int port;

    /* ChatClient constructor
     * @param server - the ip address of the server as a string
     * @param port - the port number the server is hosted on
     * @param username - the username of the user connecting
     */
    private ChatClient(String server, int port, String username) {
        try
        {
            if (username == null)
            {
                throw new IllegalArgumentException("Username is null?");
            }
        }
        catch (IllegalArgumentException iae)
        {
            System.out.println(iae.getMessage());
        }
        this.server = server;
        this.port = port;
        this.username = username;
    }

    /**
     * Attempts to establish a connection with the server
     * @return boolean - false if any errors occur in startup, true if successful
     */
    private boolean start() {
        // Create a socket
        try {
            socket = new Socket(server, port);
            System.out.println("Connected: " + socket);
        } catch (IOException e) {
            System.out.println("Client could not be started. Server probably not running right now.");
            return false;
        }

        // Attempt to create output stream
        try {
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // Attempt to create input stream
        try {
            sInput = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // Create client thread to listen from the server for incoming messages
        Runnable r = new ListenFromServer();
        Thread t = new Thread(r);
        t.start();

        // After starting, send the clients username to the server.
        try {
            sOutput.writeObject(username);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }


    /*
     * Sends a string to the server
     * @param msg - the message to be sent
     */
    private void sendMessage(ChatMessage msg) {
        try {
            sOutput.writeObject(msg);
            sOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*
     * To start the Client use one of the following command
     * > java ChatClient
     * > java ChatClient username
     * > java ChatClient username portNumber
     * > java ChatClient username portNumber serverAddress
     *
     * If the portNumber is not specified 1500 should be used
     * If the serverAddress is not specified "localHost" should be used
     * If the username is not specified "Anonymous" should be used
     */
    public static void main(String[] args) {
        // Get proper arguments and override defaults
        ChatClient client = null;
        String server = "localhost";
        String username = "Anonymous";
        int port = 1500;
        if (args.length < 1)
        {

        }
        if(args.length>0 && args[0]!=null)
        {
            username = args[0];
        }
        if(args.length>1 && args[1]!=null)
        {
            try {
                port = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException nfe)
            {
                System.out.println("Argument entered is not an intger for port number");
            }
        }
        if(args.length>2 && args[2]!=null)
        {
            server = args[2];
        }
        client = new ChatClient(server,port,username);
        client.start();
        Scanner s = new Scanner(System.in);
        while (s.hasNextLine())
        {
            String input = s.nextLine();
            if(input.startsWith("/"))
            {
                String compare = input.toLowerCase();
                if(compare.startsWith("/logout"))
                {
                    client.sendMessage(new ChatMessage(1,"",""));
                    System.exit(0);
                }
                else if(input.startsWith("/msg"))
                {
                    String[] inputer = input.split(" ",3);
                    client.sendMessage(new ChatMessage(2,inputer[2],inputer[1]));
                }
                else if(input.startsWith("/list"))
                {
                    client.sendMessage(new ChatMessage(3,"",""));
                }
                else if (input.startsWith("/ttt"))
                {
                    String turn = "";
                    String[] inputer = input.split(" ");
                    if(inputer.length>2)
                    {
                        turn = inputer[2];
                    }
                    client.sendMessage(new ChatMessage(4,turn,inputer[1]));
                }
            }
            else
            {
                client.sendMessage(new ChatMessage(0,input,""));
            }

        }


        // Create your client and start it

        // Send an empty message to the server
    }


    /*
     * This is a private class inside of the ChatClient
     * It will be responsible for listening for messages from the ChatServer.
     * ie: When other clients send messages, the server will relay it to the client.
     */
    private final class ListenFromServer implements Runnable {
        public void run() {
            try {
                while (true) {
                    String msg = (String) sInput.readObject();
                    System.out.print(msg);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}