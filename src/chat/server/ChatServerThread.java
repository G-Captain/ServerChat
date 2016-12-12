package chat.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import chat.data.ChatUser;

public class ChatServerThread extends Thread
{  private ChatServer       server    = null;
   private Socket           socket    = null;
   private int              ID        = -1;
   private DataInputStream  streamIn  =  null;
   private DataOutputStream streamOut = null;
   private String name = null;
   private String pass = null;
   private String recipient = null;
   private boolean connected = false;
   private int threadCount;

   public int getThreadCount() {
	   return threadCount;
	}
	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

   public ChatServerThread(ChatServer _server, Socket _socket, int count)
   {  super();
      server = _server;
      socket = _socket;
      ID     = socket.getPort();
      threadCount = count;
   }
   public void send(String msg)
   {   try
       {  streamOut.writeUTF(msg);
          streamOut.flush();
       }
       catch(IOException ioe)
       {  System.out.println(ID + " ERROR sending: " + ioe.getMessage());
          server.remove(ID);
          interrupt();//stop();
       }
   }
   public int getID()
   {  return ID;
   }

   public void run(){
	   System.out.println("Server Thread " + ID + " running.");
	   send("CONNECTED");
      while (true)
      {  try
         {
    	  String input = streamIn.readUTF();
    	  System.out.println(input);
    	  System.out.println(connected);
    	  	if (connected) {
				if (input.equals("MSG")){
	    	  		recipient = streamIn.readUTF();
	    	  		System.out.println(recipient);
	    	  		server.handle(recipient, streamIn.readUTF());
				}
			}else{
				userAuthorisation(input);
    	  	}
         }
         catch(IOException ioe)
         {  System.out.println(ID + " ERROR reading: " + ioe.getMessage());
            server.remove(ID);
            interrupt();//stop();
         }
      }
   }

   public void userAuthorisation(String text) throws IOException{
	   if (text.equals("NAME")) {
			name = streamIn.readUTF();
			System.out.println("imie to " + name);
		} else if (text.equals("PASS")) {
			pass = streamIn.readUTF();
			System.out.println("haslo to " + pass);
			for (ChatUser chatUser : ChatServer.chatUsers) {
				if (name.equals(chatUser.getName())){
					if (pass.equals(chatUser.getName())){
						chatUser.setClientThread(threadCount);
						send("AUTHORISED");
						connected = true;
						return;
					}else{
						send("WRONG");
						return;
					}
				}
			  }
		};
   }


public void open() throws IOException
   {  streamIn = new DataInputStream(new
                        BufferedInputStream(socket.getInputStream()));
      streamOut = new DataOutputStream(new
                        BufferedOutputStream(socket.getOutputStream()));
   }
   public void close() throws IOException
   {  if (socket != null)    socket.close();
      if (streamIn != null)  streamIn.close();
      if (streamOut != null) streamOut.close();
      connected = false;
   }
}
