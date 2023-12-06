import java.io.*;
import java.net.*;
import java.util.*;

class TCPClient {

    public static void main(String[] args) {
        int tcpServerPort;
        tcpServerPort = _get_tcp_server_port();
        

        try (Socket socket = new Socket("localhost", tcpServerPort)) {

            System.out.println("Conexiunea cu unul din noduri s-a realizat");

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String id = getAlphaNumericString(8);
            out.println("start-process_"+id);
            out.flush();

            System.out.println("Serverul a raspuns " + in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
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
