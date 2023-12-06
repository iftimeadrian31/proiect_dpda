public class DataTransfer {
    private boolean is_start;
    DataTransfer(){
      is_start = false;
    }

	public synchronized void put_is_start(boolean new_value) {
		is_start = new_value;
	}

	public synchronized boolean get_is_start() {
		return is_start;
	}
}
