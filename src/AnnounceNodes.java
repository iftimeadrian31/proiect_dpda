public class AnnounceNodes {
    private boolean is_start;
    AnnounceNodes(){
      is_start = false;
    }

  public synchronized void put_is_start(boolean new_value) {
	if (is_start == new_value)
		return;
    if (is_start != false) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
    is_start = new_value;
		notify();
	}

  	public synchronized boolean get_is_start() {
		boolean result;
		if (is_start == false) {
			try {
				wait();
        is_start = false;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		result = is_start;
    notify();
		return result;
	}
}
