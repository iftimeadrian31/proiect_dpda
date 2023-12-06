import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

class TCPListener implements Runnable {
    public final Socket clientSocket;
    public volatile ProcessResponse process_response;
    public volatile AnnounceNodes announce_nodes;
    public ConcurrentLinkedQueue<String> requestClientList;
    public String raspuns;
    public ConcurrentHashMap<String, List<String>> heartbeat_messages;
    public TCPListener(Socket socket, ProcessResponse process_response,  AnnounceNodes announce_nodes, ConcurrentLinkedQueue<String> requestClientList,
    ConcurrentHashMap<String, List<String>> heartbeat_messages) {
        this.clientSocket = socket;
        this.process_response = process_response;
        this.announce_nodes = announce_nodes;
        this.requestClientList = requestClientList;
        this.raspuns = "";
        this.heartbeat_messages = heartbeat_messages;
    }

    public void run() {
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String line = in.readLine();
            String command = line.split("_")[0];
            switch(command){
                case "start-process":
                    String id = line.split("_")[1];
                    start_process(out, id);
                    break;
                case "echo-message":
                    echo_message(out, line);
                    break; 
                default:
                    break;

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void start_process(PrintWriter out, String id){
        
        Timestamp process_timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println("Un client extern (id="+id+") a initiat o cerere de processare");
        if (requestClientList.size()==0)
        {   
            Iterator<String> key_iterator = heartbeat_messages.keySet().iterator();
            while(key_iterator.hasNext())
            {
                String node_id = key_iterator.next();
                List<String> messages = heartbeat_messages.get(node_id);
                if (messages.size()==3)
                {
                    messages.remove(2);
                    messages.remove(1);
                    process_response.put_raspunsProcesare("notProcessed");
                }
            }
            process_response.set_processTimestamp(process_timestamp);
            System.out.println("Nu exista alta cerere in progres");
            requestClientList.add(id);
            announce_nodes.put_is_start(true);
            raspuns = process_response.get_raspunsProcesare();
            out.println(raspuns+"_"+process_response.get_processTimestamp().toString());
            requestClientList.remove(id);
            if (requestClientList.size()==0)
            {
                process_response.put_raspunsProcesare("notProcessed");
            }
        }
        else
        {
            Iterator<String> key_iterator = heartbeat_messages.keySet().iterator();
            while(key_iterator.hasNext())
            {
                String node_id = key_iterator.next();
                List<String> messages = heartbeat_messages.get(node_id);
                if (messages.size()==3)
                {
                    messages.remove(2);
                    messages.remove(1);
                    process_response.put_raspunsProcesare("notProcessed");
                }
            }
            System.out.println("Exista alta cerere in progres");
            requestClientList.add(id);
            raspuns = process_response.get_raspunsProcesare();
            out.println(raspuns+"_"+process_response.get_processTimestamp().toString());
            requestClientList.remove(id);
            if (requestClientList.size()==0)
            {
                process_response.put_raspunsProcesare("notProcessed");
            }
        }
    }

    private void echo_message(PrintWriter out, String request_body){
        System.out.println("Un nod din cluster a trimis un mesaj de echo");
        out.println(request_body);

    }
}