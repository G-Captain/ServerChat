package chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import chat.data.ChatUser;

public class ChatServer implements Runnable
{
	private ArrayList<ChatServerThread> activeClients = new ArrayList<ChatServerThread>();
	protected static ArrayList<ChatUser> chatUsers = new ArrayList<ChatUser>();
   private ServerSocket server = null;
   private Thread       thread = null;
   private int clientCount = 0;
   private static final int PORT = 9003;
   /**
    * The set of all names of clients in the chat room.  Maintained
    * so that we can check that new clients are not registering name
    * already in use.
    */
   private static HashSet<String> names = new HashSet<String>();

   private static HashMap<String, String> users = new HashMap<String, String>();

   public ChatServer()
   {  try
      {  server = new ServerSocket(PORT);
         System.out.println("Server started: " + server);
         start(); }
      catch(IOException ioe)
      {  System.out.println("Can not bind to port " + PORT + ": " + ioe.getMessage()); }
   }
   public void run()
   {  while (thread != null)
      {  try
         {  System.out.println("Waiting for a client ...");
            addThread(server.accept()); }
         catch(IOException ioe)
         {  System.out.println("Server accept error: " + ioe); stop(); }
      }
   }
   public void start()
   {  if (thread == null)
      {  thread = new Thread(this);
         thread.start();
      }
   }
   public void stop()
   {  if (thread != null)
      {  thread.interrupt();//stop();
         thread = null;
      }
   }
   private ChatServerThread findClient(int number)
   {  /*for (int i = 0; i < clientCount; i++)
         if (clients[i].getID() == ID)
            return i;
      return -1;*/
		for (ChatServerThread chatServerThread : activeClients) {
			if (chatServerThread.getThreadCount() == number) {
				return chatServerThread;
			}
		}
		return null;
	}

   public synchronized void handle(String recipient, String input)
   {
	   int threadNumber = -1;
	   for (ChatUser chatUser : chatUsers) {
			if (recipient.equals(chatUser.getName())){
				threadNumber = chatUser.getClientThread();
				if (threadNumber != -1) {
					findClient(threadNumber).send(input);
				}
				break;
			}
	   }
   }

   public synchronized void remove(int ID){
	   for (ChatServerThread chatServerThread : activeClients) {
			if (chatServerThread.getThreadCount() == ID) {
				try {
					chatServerThread.close(); }
		        catch(IOException ioe){
		        	 System.out.println("Error closing thread: " + ioe);
		        }
				chatServerThread.interrupt();//stop();
		    }
			activeClients.remove(chatServerThread);
		}
	}

   private void addThread(Socket socket) {
	   ChatServerThread client = new ChatServerThread(this, socket, clientCount);
	   activeClients.add(client);
         try
         {  client.open();
            client.start();
            clientCount++; }
         catch(IOException ioe)
         {  System.out.println("Error opening thread: " + ioe); }
   }

   public static void main(String args[])
   {	System.out.println("main");
	   chatUsers.add(new ChatUser("Julia", "Julia"));
	   chatUsers.add(new ChatUser("Ian", "Ian"));
	   chatUsers.add(new ChatUser("Sue",  "Sue"));
	   chatUsers.add(new ChatUser("Matthew", "Matthew"));
	   chatUsers.add(new ChatUser("Hannah", "Hannah"));
	   chatUsers.add(new ChatUser("Stephan",  "Stephan"));
	   chatUsers.add(new ChatUser("Denise", "Denise"));
   		ChatServer server = new ChatServer();
   }
}