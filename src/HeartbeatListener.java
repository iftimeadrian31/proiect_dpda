import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.sql.Timestamp;


public class HeartbeatListener extends Thread {

    public MulticastSocket socket;
    public volatile ConcurrentHashMap<String, List<String>> heartbeat_messages;
    public  DataTransfer data_transfer;
    ConcurrentLinkedQueue<String>requestClientList;
    public ProcessResponse process_response;
    public HeartbeatListener(int port, InetAddress group, ConcurrentHashMap<String, List<String>> heartbeat_messages, DataTransfer data_transfer,
     ConcurrentLinkedQueue<String>requestClientList, ProcessResponse process_response)
    {
        try{
            this.socket = new MulticastSocket(port);
            socket.joinGroup(group);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.heartbeat_messages = heartbeat_messages;
        this.data_transfer = data_transfer;
        this.requestClientList = requestClientList;
        this.process_response = process_response;
    }
    public void run()
    {
        Timestamp timestamp;
        byte[] buf = null;
        buf = new byte[256];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        try{
            while(true)
                {
                    socket.receive(packet);
                    //se afiseaza continutul pachetului
                    String s = new String(packet.getData(), packet.getOffset(), packet.getLength());
                    if (s.contains("process"))
                    {
                        data_transfer.put_is_start(true);
                        if (!requestClientList.contains("node"))
                        {
                            requestClientList.add("node");
                        }
                        Timestamp timestamp_of_process = Timestamp.valueOf(s.split("_")[1]);
                        process_response.set_processTimestamp(timestamp_of_process);
                    }
                    else
                    {
                        timestamp = new Timestamp(System.currentTimeMillis());
                        String[] list_of_words = s.split("_");
                        String identifier = list_of_words[0]+"_"+list_of_words[1];
                        if (!heartbeat_messages.containsKey(identifier))
                        {
                            List<String> empty_list = new ArrayList<String>();
                            empty_list.add(timestamp.toString());
                            if (list_of_words.length==4)
                            {
                                empty_list.add(list_of_words[2]);
                                empty_list.add(list_of_words[3]);
                            }
                            heartbeat_messages.put(identifier, empty_list);
                        }
                        else
                        {
                            List<String> message_list = heartbeat_messages.get(identifier);
                            message_list.set(0,timestamp.toString());
                            if(list_of_words.length==4)
                            {
                                if (message_list.size()==3)
                                {
                                    message_list.remove(2);
                                    message_list.remove(1);
                                }
                                message_list.add(list_of_words[2]);
                                message_list.add(list_of_words[3]);
                            }
                            heartbeat_messages.put(identifier, message_list);
                        }
                    }
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}
