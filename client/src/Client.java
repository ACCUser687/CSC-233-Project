
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static Socket client;
    private static int port = 1234;
    private static boolean trigger = true;
    volatile private static String message;
    private static String id = "Client 1";

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        InetAddress host = InetAddress.getLocalHost();
        ObjectOutputStream oos;
        ObjectInputStream ois;
        //for (int i = 0; i < 5; i++) {z
        while (trigger){
            client = new Socket(host.getHostName(), port);
            oos = new ObjectOutputStream(client.getOutputStream());
            System.out.println("Sending mode...");
            //if (i == 4) oos.writeObject("//exit");
            //else

            Scanner input = new Scanner(System.in);
            System.out.println("Enter your message: ");
            message = input.nextLine();

            oos.writeObject(message);
            ois = new ObjectInputStream(client.getInputStream());
            String message = (String) ois.readObject();

            System.out.println("Message: " + message);
            ois.close();
            oos.close();
            Thread.sleep(1000);

            if (message.equalsIgnoreCase("//exit")) trigger = false;
        }
    }
}
