import java.io.IOException;
import java.net.*;

public class ProcessSender extends Thread {

	private DatagramSocket socket;
	private int port;
	private InetAddress group;
	private volatile AnnounceNodes announce_nodes;
	private ProcessResponse process_response;
	public ProcessSender(int port, InetAddress group, AnnounceNodes announce_nodes, ProcessResponse process_response)
	{
        try{
        this.socket = new DatagramSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
		this.port = port;
		this.group = group;
        this.announce_nodes = announce_nodes;
		this.process_response = process_response;
	}

	public void run(){

		// se creeaza un socket pe un port oarecare
		try {
			// trimite un pachet catre toti clientii din grup
			byte[] buf = null;
			DatagramPacket packet = null;
			while(true)
			{
				try{
                    announce_nodes.get_is_start();
					String s = "process_"+process_response.get_processTimestamp();
					buf = s.getBytes();
					packet = new DatagramPacket(buf, buf.length, group, port);
					socket.send(packet);
                    announce_nodes.put_is_start(false);	
				}catch (IOException e) {
            	e.printStackTrace();
        		}
			}
		} finally {
			socket.close();
		}
	}
}