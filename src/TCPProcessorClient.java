
import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

class TCPProcessorClient extends Thread {
    public ConcurrentHashMap<String, List<String>> heartbeat_messages;
    public int tcp_server_port;
    public PersonalIdentifier personal_identifier;
    public DataTransfer data_transfer;
    ProcessResponse process_response ;
    PersonalHeartbeatMessage personal_heartbeat_message;
    public ConcurrentLinkedQueue<String>requestClientList;
    public TCPProcessorClient(ConcurrentHashMap<String, List<String>> heartbeat_messages, int tcp_server_port, PersonalIdentifier personal_identifier, DataTransfer data_transfer,
    ProcessResponse process_response, PersonalHeartbeatMessage personal_heartbeat_message, ConcurrentLinkedQueue<String>requestClientList ) {
        this.heartbeat_messages = heartbeat_messages;
        this.tcp_server_port = tcp_server_port;
        this.personal_identifier = personal_identifier;
        this.data_transfer = data_transfer;
        this.process_response = process_response;
        this.personal_heartbeat_message = personal_heartbeat_message;
        this.requestClientList = requestClientList;
    }

    public void run() {
        while(true){
            Boolean is_start = data_transfer.get_is_start();
            if(is_start == true)
            {
                String cpu_process_time =_get_cpu_process_time();
                
                List<String> data_transfers = _get_data_transfer_time();
                
                
                List<String> heartbeat_message = personal_heartbeat_message.get_personal_heartbeat_message();
                if (heartbeat_message.size()==2)
                {
                    heartbeat_message.remove(1);
                    heartbeat_message.remove(0);
                }
                heartbeat_message.add("'CPU':" +cpu_process_time.toString());
                heartbeat_message.add("'DataTransfer':"+ data_transfers.toString());
                personal_heartbeat_message.put_personal_heartbeat_message(heartbeat_message);
                boolean condition = false;
                while(!condition)
                {
                    condition = true;
                    Iterator<Entry<String, List<String>>> it = heartbeat_messages.entrySet().iterator();				
                    while (it.hasNext())
                    {
                        List <String> next_list = it.next().getValue();
                        if (next_list.size() == 1)
                        {
                            condition = false;
                        }
                            
                    }
                }
                System.out.println(_get_structura_cluster());
                process_response.put_raspunsProcesare(_get_structura_cluster());
                data_transfer.put_is_start(false);
                if (requestClientList.contains("node"))
                {
                    requestClientList.remove("node");
                }
            }
        }
        
    }


    private String _get_structura_cluster()
    {   
        return heartbeat_messages.toString();
    }

    private String _get_cpu_process_time()
    {
        double n=500000000.0;
        int sum=0;
        Long difference_in_miliseconds;
        Timestamp start_timestamp = new Timestamp(System.currentTimeMillis());
        for (double i=0.0; i <n;i++)
        {
            sum+=i;
        }
        Timestamp end_timestamp = new Timestamp(System.currentTimeMillis());
        difference_in_miliseconds = end_timestamp.getTime()-start_timestamp.getTime();
        return n+" in "+difference_in_miliseconds.toString()+" ms";
    }
    private List<String> _get_data_transfer_time()
    {
        List<String> data_transfers = new ArrayList<String>();
            for (String key : heartbeat_messages.keySet())
            {
                int tcp_port = Integer.parseInt(key.split("_")[1]);
                if (tcp_port == tcp_server_port)
                {    
                    continue;
                }
                try (Socket socket = new Socket("localhost", tcp_port)) {

                System.out.println("Trimit cerere data transfer catre nodul cu portul "+tcp_port);

                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                Long difference_in_miliseconds;
                int size = 1024*1024*1024;
                Timestamp start_timestamp = new Timestamp(System.currentTimeMillis());
                out.println("echo-message_"+new byte[1024*1024*1024]);
                out.flush();
                Timestamp end_timestamp = new Timestamp(System.currentTimeMillis());
                difference_in_miliseconds = end_timestamp.getTime()-start_timestamp.getTime();
                System.out.println("Nodul cu portul "+tcp_port+" a returnat mesajul de echo");
                data_transfers.add(size+" in "+difference_in_miliseconds/2+" ms");
                } catch (IOException e) {
                    e.printStackTrace();
                }   
            }
        return data_transfers;
    }
}
