import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

final class ChatServer {
    private static int uniqueId = 0;
    // Data structure to hold all of the connected clients
    private final List<ClientThread> clients = new ArrayList<>();
    private final List<TicTacToeGame> games = new ArrayList<>();
    private final int port;            // port the server is hosted on
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();

    /**
     * ChatServer constructor
     *
     * @param port - the port the server is being hosted on
     */
    private ChatServer(int port) {
        this.port = port;
    }

    /*
     * This is what starts the ChatServer.
     * Right now it just creates the socketServer and adds a new ClientThread to a list to be handled
     */
    private void start() {
        try {
            Date date = new Date();
            String dateFormat = new SimpleDateFormat("HH:mm:ss").format(date)+" ";

            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println( dateFormat+ "Server waiting for client on port " + port+".");
            while (true) {
                date = new Date();
                dateFormat = new SimpleDateFormat("HH:mm:ss").format(date)+" ";
                Socket socket = serverSocket.accept();
                Runnable r = new ClientThread(socket, uniqueId++);
                Thread t = new Thread(r);
                System.out.println(dateFormat+((ClientThread) r).username+" has connected.");
                if(this.alreadyExists(((ClientThread) r).username))
                {
                    System.out.println("Client with username already exists. Disconnecting client.");
                    ((ClientThread) r).writeMessage("Sorry, username already exists.");
                    this.remove(((ClientThread) r).getId());
                }
                clients.add((ClientThread) r);
                t.start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcast(String message) {
        for (ClientThread i : clients) {
            synchronized (lock1) {
                i.writeMessage( message);
            }
        }
        Date date = new Date();
        String dateFormat = new SimpleDateFormat("HH:mm:ss").format(date)+" ";
        System.out.println(dateFormat+ message);
    }

    private void remove(int id) {
        for (ClientThread i : clients) {
            synchronized (lock2) {
                if (i.getId() == id) {
                    clients.remove(i);
                    i.close();
                    System.out.println(i.username+" has been removed");
                }
            }
        }
    }
    private boolean alreadyExists(String username)
    {
        for(ClientThread i : clients)
        {
            if(i.username.equals(username))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        return false;
    }

    /**
     * Sample code to use as a reference for Tic Tac Toe
     * <p>
     * directMessage - sends a message to a specific username, if connected
     *
     * @param message  - the string to be sent
     * @param username - the user the message will be sent to
     */
    /*private synchronized void directMessage(String message, String username) {
        String time = sdf.format(new Date());
        String formattedMessage = time + " " + message + "\n";
        System.out.print(formattedMessage);

        for (ClientThread clientThread : clients) {
            if (clientThread.username.equalsIgnoreCase(username)) {
                clientThread.writeMsg(formattedMessage);
            }
        }
    }*/


    /*
     *  > java ChatServer
     *  > java ChatServer portNumber
     *  If the port number is not specified 1500 is used
     */
    public static void main(String[] args) {
       ChatServer server = null;
       int port = 1500;
        if (args.length < 1) {}
        else {
            try{
            port = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException e)
            {
                System.out.println("Argument wasn't an integer to be port number.");
            }
        }
        server = new ChatServer(port);
        server.start();
    }


    /*
     * This is a private class inside of the ChatServer
     * A new thread will be created to run this every time a new client connects.
     */
    private final class ClientThread implements Runnable {
        Socket socket;                  // The socket the client is connected to
        ObjectInputStream sInput;       // Input stream to the server from the client
        ObjectOutputStream sOutput;     // Output stream to the client from the server
        String username;                // Username of the connected client
        ChatMessage cm;                 // Helper variable to manage messages
        int id;

        /*
         * socket - the socket the client is connected to
         * id - id of the connection
         */
        private ClientThread(Socket socket, int id) {
            this.id = id;
            this.socket = socket;
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());
                username = (String) sInput.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        /*
         * This is what the client thread actually runs.
         */
        @Override
        public void run() {
            // Read the username sent to you by client
            boolean running = true;
            while (true) {
                Date date = new Date();
                String dateFormat = new SimpleDateFormat("HH:mm:ss").format(date)+" ";
                try {
                    cm = (ChatMessage) sInput.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if (cm.getType() == ChatMessage.MESSAGE) {
                    broadcast(username + ": " + cm.getMsg());
                } else if (cm.getType() == ChatMessage.DM) {
                    boolean usernameExists = false;
                    for (ClientThread i : clients) {
                        if (i.username.equals(cm.getRecipeint())) {
                            i.writeMessage(this.username + "->" + i.username + ": " + cm.getMsg());
                            this.writeMessage(this.username + "->" + i.username + ": " + cm.getMsg());
                            System.out.println(dateFormat+this.username + "->" + i.username + ": " + cm.getMsg());
                            usernameExists = true;
                        }
                    }
                    if (!usernameExists) {
                        writeMessage("Username doesnot exist.");
                        System.out.println(dateFormat+"Username doesnot exist.");
                    }
                } else if (cm.getType() == ChatMessage.LOGOUT) {
                    remove(this.getId());
                    running = false;
                }else if (cm.getType() == ChatMessage.LIST) {
                    for (ClientThread i : clients) {
                        if (!i.username.equals(this.username)) {
                            writeMessage(i.username + "\n");
                        }
                    }
                } else if (cm.getType() == ChatMessage.TICTACTOE) {
                    boolean usernameExists = false;
                    String user = null;
                    boolean gameExists = false;
                    for (ClientThread i : clients) {
                        if (i.username.equals(cm.getRecipeint())) {
                            usernameExists = true;
                            if (!cm.getMsg().matches("\\d+") || cm.getMsg() == "") {
                                TicTacToeGame game = new TicTacToeGame(this.username, i.username);
                                writeMessage("You started a TicTacToe game with " + i.username);
                                i.writeMessage(this.username + " started a TicTacToe game with you");
                                System.out.println(dateFormat + this.username + " started a TicTacToe game with " + i.username);
                                games.add(game);
                                user = i.username;
                            } else if (cm.getMsg().matches("\\d+")) {
                                for (TicTacToeGame ttt : games) {
                                    boolean firstCase = (ttt.getPlayer1().equals(this.username) && ttt.getPlayer2().equals(i.username));
                                    boolean secondCase = (ttt.getPlayer2().equals(this.username) && ttt.getPlayer1().equals(i.username));
                                    if (firstCase || secondCase) {
                                        gameExists = true;
                                        usernameExists = true;
                                        if (ttt.playerOneMove() && cm.getRecipeint().equals(ttt.getPlayer2()) && ttt.isValidMove(Integer.parseInt(cm.getMsg()))) {
                                            ttt.takeTurn(Integer.parseInt(cm.getMsg()));
                                            writeMessage(ttt.toString() + "\n" + "Game board with " + i.username);
                                            i.writeMessage(ttt.toString() + "\n" + "Game board with " + this.username);
                                        } else if (!ttt.playerOneMove() && cm.getRecipeint().equals(ttt.getPlayer1()) && ttt.isValidMove(Integer.parseInt(cm.getMsg()))) {
                                            ttt.takeTurn(Integer.parseInt(cm.getMsg()));
                                            writeMessage(ttt.toString() + "\n" + "Game board with " + i.username);
                                            i.writeMessage(ttt.toString() + "\n" + "Game board with " + this.username);
                                        } else {
                                            writeMessage("Illegal Move");
                                            System.out.println(dateFormat + "Illegal Move");
                                        }
                                        if (ttt.isGameOver()) {
                                            if (ttt.isTied() == 1) {
                                                writeMessage("Game Tied");
                                                i.writeMessage("Game tied");
                                                System.out.println(dateFormat + "Game Tied");
                                            } else if (ttt.getWinner() != ' ') {
                                                if (ttt.getWinner() == 'X') {
                                                    writeMessage(this.username + " is the winner");
                                                    i.writeMessage(this.username + " is the winner");
                                                    System.out.println(dateFormat + this.username + " is the winner");
                                                    games.remove(ttt);
                                                } else if (ttt.getWinner() == 'O') {
                                                    writeMessage(i.username + " is the winner");
                                                    i.writeMessage(i.username + " is the winner");
                                                    System.out.println(dateFormat + i.username + " is the winner");
                                                    games.remove(ttt);
                                                }
                                            }
                                        }
                                    }
                                }
                                if (!gameExists) {
                                    writeMessage("Initialize game first please");
                                    System.out.println(dateFormat + "Initialize game first please");
                                }
                            }
                        }

                        }if (!usernameExists) {
                        writeMessage("Username doesnot exist.");
                        System.out.println(dateFormat + "Username doesnot exist.");
                    }
                }


                // Send message back to the client
            }
        }

        private boolean writeMessage(String msg) {
            Date date = new Date();
            String dateFormat = new SimpleDateFormat("HH:mm:ss").format(date)+" ";
            if (socket == null) {
                return false;
            } else {
                try {
                    sOutput.writeObject(dateFormat+msg+"\n");
                    sOutput.flush();
                } catch (IOException io) {
                    io.printStackTrace();
                }
                return true;
            }
        }
        private ChatMessage readMessage()
        {
            try {
                return (ChatMessage) sInput.readObject();
            }
            catch (IOException | ClassNotFoundException ioe)
            {
               ioe.printStackTrace();
            }
            return null;
        }

        public int getId() {
            return id;
        }
        private void close() {
            try {
                if (sOutput != null) sOutput.close();
                if (sInput != null) sInput.close();
                if (socket != null) socket.close();
            } catch (IOException ieo) {
            }
        }
    }
}