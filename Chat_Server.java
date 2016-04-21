import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Chat_Server extends JFrame
{   // total number of clients that can be logged on to the message service
	final int NO_OF_CLIENTS = 4; 
	final int LOGON_ATTEMPTS = 3;
	// this ArrayList will store the client threads
	ArrayList<Chat_ServerThread> chatClients = new ArrayList<Chat_ServerThread>();
   	// arrays containing valid users
	ArrayList<User> users = new ArrayList<User>();
	//Array for locked users
	ArrayList<String> LockedUsers = new ArrayList<String>();
	
	boolean lockedOut[] = new boolean[LOGON_ATTEMPTS];
	
	Logger logger = Logger.getLogger("MyLog");  
    FileHandler fh;

   // GUI components
    JTextArea outputArea;
    // any other declaration
    private ServerSocket serverSocket;
    
    AES aes = new AES();

    public Chat_Server()
    {	super("Chat_Server");
        addWindowListener
		(   new WindowAdapter()
            {	public void windowClosing(WindowEvent e)
				{   
            		System.exit(0);
				}
			}
		);

		try
		{   // get a serversocket
			serverSocket = new ServerSocket(7500);
		}
		catch(IOException e) // thrown by ServerSocket
		{   System.out.println(e);
            System.exit(1);
		}

        // create and add GUI components
		Container c = getContentPane();
		c.setLayout(new FlowLayout());
		// add text output area
		outputArea = new JTextArea(17,30);
		outputArea.setEditable(false);
		outputArea.setLineWrap(true);
		outputArea.setWrapStyleWord(true);
		c.add(outputArea);
		c.add(new JScrollPane(outputArea));
        setSize(360,310);
		setResizable(false);
		setVisible(true);
		
		users.add(new User("James Granleese","james@granleese", aes.Encrypt("Passw0rd"), 0, users));
		users.add(new User("Nathaniel Hamilton","nathaniel@hamilton", aes.Encrypt("Passw0rd"), 0, users));
		users.add(new User("Catherine Anne","catherine@anne", aes.Encrypt("Passw0rd"), 0, users));
		
		for(int i = 0; i < users.size(); i++)
		{
			System.out.println(users.get(i).getName() + " : " + users.get(i).getUserName());
		}
		
		 try {  
			 	FileWriter fw = new FileWriter("ChatLog.log");
		        fh = new FileHandler("ChatLog.log");  
		        logger.addHandler(fh);
		        SimpleFormatter formatter = new SimpleFormatter();  
		        fh.setFormatter(formatter);  
		        logger.setUseParentHandlers(false);
		 	} catch (SecurityException e) {  
		        e.printStackTrace();  
		    } catch (IOException e) {  
		        e.printStackTrace();  
		    }  
    }

    void getClients()
    {	// add message to server output area
        addOutput("Waiting for others to join you...");
		int userCount = 0;
		int runCommand = 0;
		int logonattempts = 0;
		while(userCount < NO_OF_CLIENTS)
		{   try
    		{	/* client has attempted to get a connection to server,
                   create a socket to communicate with this client */
				Socket socket = serverSocket.accept();
                // get input & output streams
				ObjectInputStream clientInputStream = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream clientOutputStream = new ObjectOutputStream(socket.getOutputStream());	
                
                boolean register = (Boolean)clientInputStream.readObject();
                if(register)
                {
                	String eNewName = (String)clientInputStream.readObject();
                	String eNewEmail = (String)clientInputStream.readObject();
                	String eNewPassword = (String)clientInputStream.readObject();
                	
                	String newName = aes.Decrypt(eNewName);
                	String newEmail = aes.Decrypt(eNewEmail);
                	
                	boolean valid = true;
                	
                	if(User.exists(1, newEmail, users))
                		valid = false;
                	
                	while(!valid)
                	{
                		clientOutputStream.writeObject(new String("!Register"));
                		Boolean x = (Boolean)clientInputStream.readObject();
                		eNewName = (String)clientInputStream.readObject();
                    	eNewEmail = (String)clientInputStream.readObject();
                    	eNewPassword = (String)clientInputStream.readObject();
                    	
                    	newName = aes.Decrypt(eNewName);
                    	newEmail = aes.Decrypt(eNewEmail);
                    	
                    	valid = true;
                    	
                    	if(User.exists(1, newEmail, users))
                    		valid = false;
                	}
                	
                	if(valid)
                	{
                		User newUser = new User(newName, newEmail, eNewPassword, 0, users);
                		users.add(newUser);
                		
                		clientOutputStream.writeObject(new String("Register"));
                		
                		String sUserName = aes.Encrypt(newUser.getUserName());
             
                		clientOutputStream.writeObject(sUserName);
                		
                		addOutput(newUser.getName() + " has been registered with username '" + newUser.getUserName() + "'");
                	}
                	
                	Boolean confirmed = (Boolean)clientInputStream.readObject();
                	
                	
                	if(confirmed)
                	{  
	               		valid = true;
	               		int pos = User.findPos(1, newEmail, users);
	               		
	               		addOutput("Client " + newName + " is known as " + users.get(pos).getChattag());
	               		users.get(pos).setOnline(true);
	               		clientOutputStream.writeObject(new Boolean(true));
	               		Chat_ServerThread client = new Chat_ServerThread(clientInputStream, clientOutputStream, users.get(pos).getName(), users.get(pos).getChattag());
						chatClients.add(client);	
						client.start();	
						userCount++;
	               		
                	}
                }
                	
                else
                {
                	// add message to server output area
	                addOutput("Logging in...");
	               
	                // read encrypted username from input stream & decrypt
	                String uem = (String)clientInputStream.readObject();
					String uname = aes.Decrypt(uem);
					
					// read encrypted password from input stream & decrypt
					String pem = (String)clientInputStream.readObject();
					String pword = aes.Decrypt(pem);	
					
					// add messages to server output area
					addOutput("Decrypted Username: " + uname);
	              	addOutput("Decrypted Password: " + pword);
	              		
	             	boolean valid = false;
	              	int pos = User.findPos(0, uname, users);
	              	
	              	if(pos >= -1)
					{    if (LockedUsers.contains(users.get(pos).getChattag())){
               			addOutput("User has been locked out");
               			
               		}
               		else{
	              		if(users.get(pos).getPassword().equals(pem) && !users.get(pos).getOnline())
						{	addOutput("Login details received from client " + (userCount+1) + ", " + uname + " are valid");
							addOutput("Client " + uname + " is known as " + users.get(pos).getChattag());
	                   		valid = true;
	                   		users.get(pos).setOnline(true);
	                   		clientOutputStream.writeObject(new String("Logon"));
							// send Boolean value  true to client
	                   		clientOutputStream.writeObject(new Boolean(true));	
							// add this new thread to the array list
	                   		Chat_ServerThread client = new Chat_ServerThread(clientInputStream, clientOutputStream, users.get(pos).getName(), users.get(pos).getChattag());
							chatClients.add(client);	
							// start thread - execution of the thread will begin at method run
							client.start();	
							userCount++;
						}
						}
	            	}
	           		
	              	if(!valid)
	              	{   /* user is not registered therefore write a Boolean value
						   false to the output stream */
	              		clientOutputStream.writeObject(new String("Logon"));
	           			clientOutputStream.writeObject(new Boolean(false));
	                 	addOutput("Login details received from client " + (userCount+1) + ", " + uname + " are invalid");
	                 	logonattempts++;
	                 	addOutput(""+logonattempts);
	                 	if (logonattempts == 3){
		              		addOutput("3 Incorrect logon attempts, user "+uname+"will be locked out for 30 minutes");
		              		lockedOut[pos] = true;
		              		LockedUsers.add(uname);
		              		Timer timer = new Timer();
		              		timer.schedule(new TimerTask() {
		              			 @Override
		              			  public void run() {
		              			    LockedUsers.remove(pos);
		              			    
		              			  }
		              		}, 1*60*1000);
		              	}
	              	}
	              	
                }
                
    		}
            catch(ClassNotFoundException e) // thrown by method readObject
			{	System.out.println(e);
				System.exit(1);
			}
            catch(IOException e) // thrown by Socket
            {	System.out.println(e);
                System.exit(1);
            }
		}
    }
    
    void addOutput(String s)
    {	// add message to text output area
        outputArea.append(s + "\n");
        outputArea.setCaretPosition(outputArea.getText().length());
        logger.info(s);
    }

    // main method of class Chat_Server
    public static void main(String args[])
    {	Chat_Server chatServer = new Chat_Server();
        chatServer.getClients();
    }

    // beginning of class Chat_ServerThread
    private class Chat_ServerThread extends Thread
    {	// What to declare?
    	ObjectInputStream threadInputStream;
    	ObjectOutputStream threadOutputStream;
    	String clientName, chatName;

        public Chat_ServerThread(ObjectInputStream in, ObjectOutputStream out, String cname, String ctName)
        {   // initialise input stream
        	threadInputStream = in;
            // initialise output stream
        	threadOutputStream = out;
            // initialise user name
            clientName = cname;
            // initialise chat name
            chatName = ctName;
  		}

        CompressedMessage getCompressedMessage(String str)
        {   // create & return a compressed message
        	CompressedMessage cm = new CompressedMessage(str);
        	return cm;
        }

        // when method start() is called thread execution will begin in this method
        public void run()
  		{   try
            {	//send greeting to this client
                threadOutputStream.writeObject(getCompressedMessage("Welcome to the chat server " + clientName));
                // inform this client of other clients online
                threadOutputStream.writeObject(getCompressedMessage("online" + getChatClients()));
                // output to server window
                addOutput(clientName + " known as " + chatName + " has joined");
                // inform other clients that this client has joined
  				sendMessage("join" + chatName);

                boolean quit = false, broadcast = false;
                // this loop will continue until the client quits the chat service
				while(!quit)
				{   // read next compressed message from client
                    CompressedMessage cm = (CompressedMessage)threadInputStream.readObject();
                    // decompress message
                    String fromClient = cm.decompress();
                    // find position of separating character
                    int foundPos = fromClient.indexOf('#');
                    // list of recipients for message
                    String sendTo = fromClient.substring(0,foundPos);
                    // message to be sent to recipients
                    String message = fromClient.substring(foundPos+1);

                    // if the message is "quit" then this client wishes to leave the chat service
                    if((message.trim()).equals("quit"))
                    {   // add message to server output area
                        addOutput(clientName + " has " + message);
                        // inform other clients that this client has quit
                        sendMessage("quit" + chatName);
                        //send "goodbye" message to this client
                        threadOutputStream.writeObject(getCompressedMessage("goodbye" + chatName));
						// remove this client from the list of clients
						remove(chatName);
						int pos = User.findPos(2, chatName, users);
						users.get(pos).setOnline(false);
						
						//end the loop
						quit = true;
                    }
                    else
                    {   // add message to server output area
                        addOutput(clientName + " >> " + message);
                        // split string to separate recipients names
                        String[] recipients = sendTo.split(",\\s*");
                        addOutput(sendTo);
                        ArrayList<String> chattags = new ArrayList<String>();
                        for(int i = 0; i < users.size(); i++)
                        	chattags.add(users.get(i).getChattag());
                        String[] chattag = new String[chattags.size()];
                        chattag = chattags.toArray(chattag);
                        // sort this array to use binarySearch
                        Arrays.sort(recipients);
                        Arrays.sort(chattag);
                        // identify if this message is to be sent to all other clients
                        foundPos = Arrays.binarySearch(recipients, chattag[chattag.length-1]);
                        if(foundPos >= 0){
                           // send this message to all other clients
                            sendMessage(chatName + " >> " + message);
                        }
                        else
                            // send this message to all clients in recipients array
                            sendMessage(chatName + " >> " + message, recipients);
                    }
                } // end while

                // close input stream
				threadInputStream.close();
				// close output stream
				threadOutputStream.close();
            }
            catch(IOException e) // thrown by method readObject, writeObject, close
            {	System.out.println(e);
				System.exit(1);
            }
            catch(ClassNotFoundException e) // thrown by method readObject
            {	System.out.println(e);
				System.exit(1);
            }
        }

        /* this method returns a list of the names of all the 
           clients currently joined excluding this client */
        String getChatClients()
        {   String allClients = "";
            synchronized(chatClients)
            {	/* traverse list of threads & add value of 
                   instance variable name of each thread */
                for (Chat_ServerThread sThread : chatClients)
                {   if(!sThread.chatName.equals(chatName))
                        allClients += sThread.chatName + ",";
                }
            }
            if(allClients.equals(""))
                allClients = "none";
            return allClients;
		}

        /* this method sends the current message to all the 
           clients currently joined excluding this client */
        void sendMessage(String str)
        {   synchronized(chatClients)
            {	/* traverse list of threads & send message 
                   to all clients */
                for (Chat_ServerThread sThread : chatClients)
                {   if(!sThread.chatName.equals(chatName))
                    {   try
                        {   sThread.threadOutputStream.writeObject(getCompressedMessage(str));
                        }
                        catch(IOException e) // thrown by method writeObject
                        {   System.out.println(e);
                            System.exit(1);
                        }
                    }
				}
            }
		}

        /* this method sends the current message to all the 
           clients in the recipients array */
        void sendMessage(String str, String[] rec)
        {   synchronized(chatClients)
            {	/* traverse list of threads - if a match is found
            	   with recipients name send message to that client */
                for(Chat_ServerThread sThread : chatClients)
                {   if(Arrays.binarySearch(rec, sThread.chatName) >= 0)
                    {   try
                        {	sThread.threadOutputStream.writeObject(getCompressedMessage(str));
                        }
                        catch(IOException e) // thrown by method writeObject
                        {	System.out.println(e);
                            System.exit(1);
                        }
                    }
				}
            }
        }

		/* this method removes this client's thread 
           from the list */
        void remove(String name)
        {   synchronized(chatClients)
            {   int pos = -1;
            	/* traverse list of threads & find position of 
            	   this client's thread in the list */
                for(int i = 0; i < chatClients.size(); i++)
                {   if(chatClients.get(i).chatName.equals(name))
                        pos = i;
                }
				if(pos != -1)
                    // remove this client's thread
                    chatClients.remove(pos);
            }
	}
    } // end of class Chat_ServerThread
} // end of class Chat_Server
