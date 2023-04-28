import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalTime;
import java.util.Scanner;

public class BerkeleyClient {
    
	private static LocalTime localTime;

	private static int GetTimeDifference(LocalTime localTime) {
		int clientMinutes = BerkeleyClient.localTime.toSecondOfDay() / 60;
		int serverMinutes = localTime.toSecondOfDay() / 60;
		return clientMinutes - serverMinutes;
	}

	private static void Synchronize(int timeToAdjust) {
		int seconds = localTime.toSecondOfDay();
		localTime = LocalTime.ofSecondOfDay(seconds + timeToAdjust * 60);
	}
	
	public static void main(String[] args) throws NumberFormatException, UnknownHostException, IOException {

		String portNumber = args[0];
		localTime = LocalTime.parse(args[1]);

		Socket socket = new Socket("127.0.0.1", Integer.parseInt(portNumber));
		PrintStream out = new PrintStream(socket.getOutputStream());
		Scanner in = new Scanner(socket.getInputStream());

		System.out.println();
		while (in.hasNext()) {
			int timeDifference = GetTimeDifference(LocalTime.parse(in.nextLine()));
			out.println(timeDifference);
			System.out.println("Before: " + localTime);
			int timeToAdjust = Integer.parseInt(in.nextLine());
			System.out.println("Time difference: " + timeToAdjust);
			Synchronize(timeToAdjust);
			System.out.println("After: " + localTime);
		}
		socket.close();
	}
}
