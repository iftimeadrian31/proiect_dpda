import java.net.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.io.*;

public class MulticastClient {
	
	public static void main(String[] args) throws IOException {
		InetAddress group = InetAddress.getByName("230.0.0.1");
		int port = 4446;
		int tcp_server_port = _get_tcp_server_port();
		String cluster_node_id = getAlphaNumericString(6);

		ConcurrentHashMap<String, List<String>> heartbeat_messages= new ConcurrentHashMap<>();
		ConcurrentLinkedQueue<String>requestClientList = new ConcurrentLinkedQueue<String>();

		ProcessResponse process_response = new ProcessResponse();
		AnnounceNodes announce_nodes = new AnnounceNodes();
		DataTransfer data_transfer = new DataTransfer();
		PersonalIdentifier personal_identifier = new PersonalIdentifier(cluster_node_id+"_"+tcp_server_port);
		PersonalHeartbeatMessage personal_heartbeat_message = new PersonalHeartbeatMessage();
		
		Thread heartbeat_listener = new HeartbeatListener(port, group, heartbeat_messages, data_transfer, requestClientList, process_response);
		Thread heartbeat_sender = new HeartbeatSender(port, group, heartbeat_messages, personal_identifier, personal_heartbeat_message);
		Thread tcp_server = new TCPServer(tcp_server_port, process_response, announce_nodes, requestClientList, heartbeat_messages);
		Thread process_sender = new ProcessSender(port, group, announce_nodes, process_response);
		Thread tcp_processor_client = new TCPProcessorClient(heartbeat_messages, tcp_server_port, personal_identifier, data_transfer, process_response, personal_heartbeat_message, requestClientList);
		
		tcp_server.start();
		heartbeat_listener.start();
		heartbeat_sender.start();
		process_sender.start();
		tcp_processor_client.start();
		
		while(true){
			if (heartbeat_messages.size()!=0)
			{
				System.out.println("\nHeart-Beat");
				Iterator<Entry<String, List<String>>> it = heartbeat_messages.entrySet().iterator();				
				while (it.hasNext())
				{
					String next_node_id = it.next().getKey();
					List<String> next_message = heartbeat_messages.get(next_node_id);
					System.out.println(next_node_id+":"+next_message);
						
				}
				try{
					TimeUnit.SECONDS.sleep(1);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		}

	}
	
	private static int _get_tcp_server_port()
	{
		Scanner reader = new Scanner(System.in);
		System.out.println("Enter a number: ");
		int n = reader.nextInt();
		reader.close();
		return n;
	}

	static String getAlphaNumericString(int n) {

		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
				+ "0123456789"
				+ "abcdefghijklmnopqrstuvxyz";

		StringBuilder sb = new StringBuilder(n);

		for (int i = 0; i < n; i++) {

			int index = (int) (AlphaNumericString.length()
					* Math.random());

			sb.append(AlphaNumericString
					.charAt(index));
		}

		return sb.toString();
	}

}