import java.util.ArrayList;
import java.util.List;

public class PersonalHeartbeatMessage {
    private List<String> personal_heartbeat_message;
    PersonalHeartbeatMessage(){
	  this.personal_heartbeat_message = new ArrayList<String>(0);
    }

	public void put_personal_heartbeat_message(List<String> new_value) {
		personal_heartbeat_message = new_value;
	}

	public List<String> get_personal_heartbeat_message() {
		return personal_heartbeat_message;
	}
}
