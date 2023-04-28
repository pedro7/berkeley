import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Scanner;

public class BerkeleyServer {
    
    private static LocalTime localTime;

	private static int[] CalculateTimesToAdjust(int[] timeDifferences) {
		int averageTime = 0;
		for (int timeDifference : timeDifferences) {
			averageTime += timeDifference;
		}
		averageTime /= timeDifferences.length + 1;
		int[] timesToAdjust = new int[timeDifferences.length];
		for (int i = 0; i < timeDifferences.length; i++) {
			timesToAdjust[i] = timeDifferences[i] * -1 + averageTime;
		}
		Synchronize(averageTime);
		return timesToAdjust;
	}

	private static void Synchronize(int timeDifference) {
		int seconds = localTime.toSecondOfDay();
		localTime = LocalTime.ofSecondOfDay(seconds + timeDifference * 60);
	}

	public static void main(String[] args) throws NumberFormatException, IOException {

		String portNumber = args[0];
		int numberOfClients = Integer.parseInt(args[1]);
        localTime = LocalTime.parse(args[2]);

		ServerSocket serverSocket = new ServerSocket(Integer.parseInt(portNumber));
		ArrayList<Socket> sockets = new ArrayList<Socket>();

		for (int i = 0; i < numberOfClients; i++) {
			sockets.add(serverSocket.accept());
		}

		for (Socket socket : sockets) {
			PrintStream out = new PrintStream(socket.getOutputStream());
			out.println(localTime);
		}

		System.out.println();
		int[] timeDifferences = new int[numberOfClients];
        for (int i = 0; i < numberOfClients; i++) {
			Scanner in = new Scanner(sockets.get(i).getInputStream());
			int timeDifference = Integer.parseInt(in.nextLine());
			System.out.println("Client " + (i + 1) + " time difference: " + timeDifference);
        	timeDifferences[i] = timeDifference;
        }
		System.out.println();

		int[] timesToAdjust = CalculateTimesToAdjust(timeDifferences);
		for (int i = 0; i < timesToAdjust.length; i++) {
			PrintStream escritaCliente = new PrintStream(sockets.get(i).getOutputStream());
			System.out.println("Client " + (i + 1) + " time to adjust: " + timesToAdjust[i]);
			escritaCliente.println(timesToAdjust[i]);
		}
		System.out.println();
		System.out.println("New time: " + localTime);
	}
}
