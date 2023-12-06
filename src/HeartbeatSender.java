import java.io.IOException;
import java.net.*;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class HeartbeatSender extends Thread {

	public volatile ConcurrentHashMap<String, List<String>>heartbeat_messages;
	public DatagramSocket socket;
	public int port;
	public InetAddress group;
	public volatile PersonalIdentifier personal_identifier;
	public PersonalHeartbeatMessage personal_heartbeat_message;
	public HeartbeatSender(int port, InetAddress group, ConcurrentHashMap<String, List<String>> heartbeat_messages, PersonalIdentifier personal_identifier,
	PersonalHeartbeatMessage personal_heartbeat_message)
	{
        try{
        this.socket = new DatagramSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
		this.heartbeat_messages = heartbeat_messages;
		this.port = port;
		this.group = group;
		this.personal_identifier = personal_identifier;
		this.personal_heartbeat_message = personal_heartbeat_message;
	}

	public void run(){

		// se creeaza un socket pe un port oarecare
		try {
			// trimite un pachet catre toti clientii din grup
			int round=1;
			byte[] buf = null;
			DatagramPacket packet = null;
			while(true)
			{
				String identifier = personal_identifier.get_personal_identifier();
				List<String> heartbeat_message = personal_heartbeat_message.get_personal_heartbeat_message();
				String message;
				if (heartbeat_message.size()==2)
				{
					message = identifier+"_"+heartbeat_message.get(0)+"_"+heartbeat_message.get(1);
				}
				else
				{
					message = identifier;
				}
				buf = message.getBytes();
				packet = new DatagramPacket(buf, buf.length, group, port);
				try{
					socket.send(packet);	
				}catch (IOException e) {
            	e.printStackTrace();
        		}
				
				if (round == 3)
				{
					Timestamp min_timestamp = Timestamp.from(new Timestamp(System.currentTimeMillis()).toInstant().minusSeconds(5));
					Iterator<Entry<String, List<String>>> it = heartbeat_messages.entrySet().iterator();

					while (it.hasNext())
					{

						Timestamp current_timestamp = Timestamp.valueOf(it.next().getValue().get(0));
						if (compare(min_timestamp, current_timestamp))
							it.remove();
					}
					round=1;

				}
				round++;
				try{
					TimeUnit.SECONDS.sleep(1);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
				
			}
		} finally {
			socket.close();
		}
	}

	public boolean compare(Timestamp t1, Timestamp t2) {

        long l1 = t1.getTime();
        long l2 = t2.getTime();
        if (l2 < l1)
        return true;
        else 
		return false;
    }
}