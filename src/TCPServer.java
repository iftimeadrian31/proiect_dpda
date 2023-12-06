import java.io.*;
import java.net.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class TCPServer extends Thread {
    public ServerSocket server = null;
    public int tcp_server_port;
    public volatile AnnounceNodes announce_nodes;
    public volatile ProcessResponse process_response;
    public ConcurrentLinkedQueue<String> requestClientList;
    public ConcurrentHashMap<String, List<String>> heartbeat_messages;
    TCPServer(int tcp_server_port, ProcessResponse process_response, AnnounceNodes announce_nodes, ConcurrentLinkedQueue<String> requestClientList,
     ConcurrentHashMap<String, List<String>> heartbeat_messages){
        this.tcp_server_port = tcp_server_port;
        this.process_response = process_response;
        this.announce_nodes = announce_nodes;
        this.requestClientList = requestClientList;
        this.heartbeat_messages = heartbeat_messages;
    }
    public void run()
    {
        try {

            server = new ServerSocket(tcp_server_port);
            System.out.println("Serverul a pornit");
            server.setReuseAddress(true);
            System.out.println("Serverul asteapta un client");
            ExecutorService executor = Executors.newFixedThreadPool(5);
            while (true) {

                Socket client = server.accept();

                TCPListener clientSock = new TCPListener(client, process_response, announce_nodes, requestClientList, heartbeat_messages);
                executor.execute(clientSock);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
