import java.sql.Timestamp;

public class ProcessResponse {
    private String raspunsProcesare;
	private Timestamp processTimestamp;
    ProcessResponse(){
      raspunsProcesare = "notProcessed";
    }

	public void set_processTimestamp(Timestamp new_timestamp)
	{
		processTimestamp= new_timestamp;
	}

	public Timestamp get_processTimestamp()
	{
		return processTimestamp;
	}

	public synchronized void put_raspunsProcesare(String new_value) {
		if (raspunsProcesare == new_value)
			return;
		raspunsProcesare = new_value;
		notifyAll();
	}

	public synchronized String get_raspunsProcesare() {
		String result;
		if (raspunsProcesare.equals("notProcessed")) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		result = raspunsProcesare;
		return result;
	}
}
