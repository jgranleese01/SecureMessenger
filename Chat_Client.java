import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.text.*;

public class Chat_Client extends JFrame
{	// socket to communicate with server
    Socket socket;
   	// input stream - data sent by the server will be read from this stream
    ObjectInputStream clientInputStream;
    // output stream - data sent to the server will be written to this stream
    ObjectOutputStream clientOutputStream;
	
	// variables for the GUI components of the game
	Container c;
    ButtonHandler bHandler;
    NameButtonHandler nbHandler;
    JButton sendButton, logonButton, lRegisterButton, rRegisterButton, confirmationButton, createChatButton, addUserButton, removeUserButton;
    ArrayList<JButton> nameButtons = new ArrayList<JButton>();
    JPasswordField password, rPassword, rPasswordC;
    JTextField username, rName, rEmail;
    JTextPane outputArea,inputArea;
   	StyledDocument docInputArea, docOutputArea;
    Style style;
    
    JPanel namesPanel, nameButtonsPanel, sendButtonPanel, inputAreaPanel, logonFieldsPanel, logonButtonPanel, 
    registerFieldsPanel, registerButtonPanel, leftPanel, rightPanel, cCenterPanel, lowerPanel, outputAreaPanel,
    registeredPanel, nUserNamePanel;
    
    JLabel namesLabel, usernameLabel, passwordLabel, rNameLabel, rEmailLabel, rPasswordLabel, rPasswordCLabel, 
    registeredLabel, nUserNameLabel;
    
    //
    
    //String[] names = {"Arken", "Ben", "Darklark", "Free", "Group"};
    
    ArrayList<String> names = new ArrayList<String>();
    String recipients = "";
	
	int attempts = 0;
	boolean confirmed = false;
	
	AES aes = new AES();

	public Chat_Client()
	{	super("Chat_Client");
		addWindowListener
		(	new WindowAdapter()
			{	public void windowClosing(WindowEvent e)
				{	System.exit(0);
				}
			}
		);

      	c = getContentPane();
   		c.setLayout(new BorderLayout());

      	logonFieldsPanel = new JPanel();
		logonFieldsPanel.setLayout(new GridLayout(2,2,5,5));
		usernameLabel = new JLabel("Enter Username: ");
		logonFieldsPanel.add(usernameLabel);
		username = new JTextField(10);
		logonFieldsPanel.add(username);

		// GUI components for the password
		passwordLabel = new JLabel("Enter Password: ");
		logonFieldsPanel.add(passwordLabel);
		password = new JPasswordField(10);
		logonFieldsPanel.add(password);
		c.add(logonFieldsPanel,BorderLayout.CENTER);

		// panel for the logon button
		logonButtonPanel = new JPanel();
		logonButton = new JButton("Log On");
		lRegisterButton = new JButton("Register");
		bHandler = new ButtonHandler();
		logonButton.addActionListener(bHandler);
		lRegisterButton.addActionListener(bHandler);
		logonButtonPanel.add(logonButton);
		logonButtonPanel.add(lRegisterButton);
		c.add(logonButtonPanel, BorderLayout.SOUTH);

		setSize(300,125);
		setResizable(false);
		setVisible(true);
	}
	
	void setUpRegistration()
	{
		c.removeAll();
    	
    	registerFieldsPanel = new JPanel();
    	registerFieldsPanel.setLayout(new GridLayout(4,2,5,5));
    	rNameLabel = new JLabel("Enter Name: ");
    	registerFieldsPanel.add(rNameLabel);
    	rName = new JTextField(10);
    	registerFieldsPanel.add(rName);
    	rEmailLabel = new JLabel("Enter E-Mail Address: ");
    	registerFieldsPanel.add(rEmailLabel);
    	rEmail = new JTextField(10);
    	registerFieldsPanel.add(rEmail);
    	rPasswordLabel = new JLabel("Enter Password: ");
    	registerFieldsPanel.add(rPasswordLabel);
    	rPassword = new JPasswordField(10);
    	registerFieldsPanel.add(rPassword);
    	rPasswordCLabel = new JLabel("Re-Enter Password: ");
    	registerFieldsPanel.add(rPasswordCLabel);
    	rPasswordC = new JPasswordField(10);
    	registerFieldsPanel.add(rPasswordC);
    	
    	c.add(registerFieldsPanel, BorderLayout.CENTER);
    	
    	registerButtonPanel = new JPanel();
    	rRegisterButton = new JButton("Register");
    	bHandler = new ButtonHandler();
		rRegisterButton.addActionListener(bHandler);
		registerButtonPanel.add(rRegisterButton);
		c.add(registerButtonPanel, BorderLayout.SOUTH);
		
		setSize(300, 150);
		setResizable(false);
		setVisible(true);
	}
	
	void regError(int index)
	{
		if(attempts <= 3)
		{
			switch(index)
			{
			case 1:
				 JOptionPane.showMessageDialog(registerFieldsPanel, "Password must contain at least 1 lower-case letter", "Registration Error",
					        JOptionPane.WARNING_MESSAGE);
				 rPasswordLabel.setForeground(Color.RED);
				 rPasswordCLabel.setForeground(Color.RED);
				break;
			case 2:
				JOptionPane.showMessageDialog(registerFieldsPanel, "Password must contain at least 1 upper-case letter", "Registration Error",
				        JOptionPane.WARNING_MESSAGE);
				 rPasswordLabel.setForeground(Color.RED);
				 rPasswordCLabel.setForeground(Color.RED);
				break;
			case 3:
				JOptionPane.showMessageDialog(registerFieldsPanel, "Password must contain at least 1 number", "Registration Error",
				        JOptionPane.WARNING_MESSAGE);
				 rPasswordLabel.setForeground(Color.RED);
				 rPasswordCLabel.setForeground(Color.RED);
				break;
			case 4:
				JOptionPane.showMessageDialog(registerFieldsPanel, "Please enter a valid email address containing '@'", "Registration Error",
				        JOptionPane.WARNING_MESSAGE);
				rEmailLabel.setForeground(Color.RED);
				break;
			case 5:
				JOptionPane.showMessageDialog(registerFieldsPanel, "Passwords are not the same", "Registration Error",
				        JOptionPane.WARNING_MESSAGE);
				 rPasswordLabel.setForeground(Color.RED);
				 rPasswordCLabel.setForeground(Color.RED);
				break;
			case 6:
				JOptionPane.showMessageDialog(registerFieldsPanel, "Please enter a valid name", "Registration Error",
				        JOptionPane.WARNING_MESSAGE);
				rNameLabel.setForeground(Color.RED);
				break;
			case 7:
				JOptionPane.showMessageDialog(registerFieldsPanel, "Please enter a valid email address", "Registration Error",
				        JOptionPane.WARNING_MESSAGE);
				rEmailLabel.setForeground(Color.RED);
				break;
			case 8:
				JOptionPane.showMessageDialog(registerFieldsPanel, "Please enter a valid password", "Registration Error",
				        JOptionPane.WARNING_MESSAGE);
				rPasswordLabel.setForeground(Color.RED);
				break;
			case 9:
				JOptionPane.showMessageDialog(registerFieldsPanel, "Please re-enter your password", "Registration Error",
				        JOptionPane.WARNING_MESSAGE);
				rPasswordCLabel.setForeground(Color.RED);
				break;
			case 10:
				 JOptionPane.showMessageDialog(registerFieldsPanel, "Password must contain between 8 and 15 characters", "Registration Error",
					        JOptionPane.WARNING_MESSAGE);
				 rPasswordLabel.setForeground(Color.RED);
				 rPasswordCLabel.setForeground(Color.RED);
				break;
			case 11:
				JOptionPane.showMessageDialog(registerFieldsPanel, "This email address is already registered to another account, please use another", "Registration Error",
				        JOptionPane.WARNING_MESSAGE);
				rEmailLabel.setForeground(Color.RED);
				break;
			}
		}
	}
	
	void registered(String uName)
	{
		c.remove(registerFieldsPanel);
    	c.remove(registerButtonPanel);
    	
    	registeredPanel = new JPanel();
    	registeredPanel.setLayout(new GridLayout(3,1,5,5));
    	registeredLabel = new JLabel("<html><br><center>Congratulations! <br>You are now registered with an account for this application. "
    			+ "Please take note of your new username(below) as it will be required to login</center></html>", SwingConstants.CENTER);
    	registeredPanel.add(registeredLabel);
    	nUserNameLabel = new JLabel(uName, SwingConstants.CENTER);
    	nUserNameLabel.setBackground(Color.white);
    	nUserNameLabel.setOpaque(true);
    	registeredPanel.add(nUserNameLabel);
    	
    	confirmationButton = new JButton("Return to Log In");
		bHandler = new ButtonHandler();
		confirmationButton.addActionListener(bHandler);
		registeredPanel.add(confirmationButton);
		
		c.add(registeredPanel, BorderLayout.CENTER);
    	
    	setSize(300, 300);
		setResizable(false);
		setVisible(true);
	}

    void setUpChatClient(boolean chatting)
    {	// remove iniial GUI components (textfield, password field, logon button)
		c.removeAll();
		
		if(!chatting)
       		// if the user has not logged on an error message will be displayed
       		c.add(new JTextArea("Logon unsuccessful"));
		else
		{	// if the user has logged on the message service GUI will be set up
			c.setLayout(new BorderLayout());
			leftPanel = new JPanel(new GridLayout(4,1,0,0));
			leftPanel.setBackground(Color.WHITE);
         	rightPanel = new JPanel(new GridLayout(2,1));
			
          	// name buttons enable user to choose message recipient(s)
			nameButtonsPanel = new JPanel(new GridLayout(nameButtons.size(),1));
			nbHandler = new NameButtonHandler();
            
			outputAreaPanel = new JPanel();
			outputAreaPanel.setBackground(Color.WHITE);
			// messages from the server will be displayed in this JTextPane
          	outputArea = new JTextPane();
        	outputArea.setEditable(false);
         	Dimension d = new Dimension(300,150);
        	outputArea.setPreferredSize(d);
        	docOutputArea = (StyledDocument) outputArea.getDocument();
        	style = docOutputArea.addStyle("StyleName", null);
        	JScrollPane outputScrollPane = new JScrollPane(outputArea);
          	outputAreaPanel.add(outputScrollPane);
         	rightPanel.add(outputAreaPanel);

			inputAreaPanel = new JPanel();
			inputAreaPanel.setBackground(Color.WHITE);
			
        	d = new Dimension(300,60);
        	// text messages will be entered into this JTextPane
        	inputArea = new JTextPane();
        	inputArea.setPreferredSize(d);
        	docInputArea = (StyledDocument) inputArea.getDocument();
        	style = docInputArea.addStyle("StyleName", null);
       	 	JScrollPane scrollPane = new JScrollPane(inputArea);
        	inputAreaPanel.add(scrollPane);
			
			// the send button enables user to send a text message
          	sendButtonPanel = new JPanel();
          	sendButtonPanel.setBackground(Color.WHITE);
        	bHandler = new ButtonHandler();
        	sendButton = new JButton("Send");
        	sendButton.addActionListener(bHandler);
        	sendButtonPanel.add(sendButton);
        	inputAreaPanel.add(sendButtonPanel);
			rightPanel.add(inputAreaPanel);
			
			// the createChat button enables a user to create a separate chat
			bHandler = new ButtonHandler();
			createChatButton = new JButton("Create Chat");
			createChatButton.addActionListener(bHandler);
			leftPanel.add(createChatButton);
						
			// addUser button adds a user to the new chat
			bHandler = new ButtonHandler();
			addUserButton = new JButton("Add user");
			addUserButton.addActionListener(bHandler);
			leftPanel.add(addUserButton);
						
			//removeUser button removes a user from the chat					
			bHandler = new ButtonHandler();
			removeUserButton = new JButton("Remove user");
			removeUserButton.addActionListener(bHandler);
			leftPanel.add(removeUserButton);			

          	c.add(rightPanel, BorderLayout.CENTER);
         	c.add(leftPanel, BorderLayout.WEST);
			setSize(425, 475);
		}
		setResizable(false);
        setVisible(true);
    }

    void changeNameButton(JButton button, Color c)
    {   /* change the colour of the text on a name 
           button - red indicates that this friend
           is a recipient of next message */
    	button.setForeground(c);
    }

    void changeNameButtons(Color c)
    {	/* change the colour of the text on all the 
           name buttons */
    	for(int r = 0; r < nameButtons.size(); r++)
           changeNameButton(nameButtons.get(r), c);
    }

    void changeNameButtons()
    {   /* disable or enable each name button - a
           button is enabled if that friend is online,
           otherwise it is disabled */
    	if(nameButtons.size() != 0)
    	{
    		for(int i = 0; i < nameButtons.size(); i++)
	    	{
	    		nameButtons.get(i).setText(names.get(i));
	    		nameButtons.get(i).setName(names.get(i));
				nameButtons.get(i).addActionListener(nbHandler);
				nameButtonsPanel.add(nameButtons.get(i));
	        }
	        
    		leftPanel.add(nameButtonsPanel);
    		c.revalidate();
    	}
    }

    void changeFriends(String n, boolean b)
    {   // change a friend's "online" status
    	n = n.trim();
    	
    	if(b)
    	{
    		if(!names.contains(n))
    		{
    			names.add(n);
    			nameButtons.add(new JButton());
    		}
    	} else {
    		names.remove(n);
    		nameButtons.remove(0);
    	}
    		
        // call method to update buttons
        changeNameButtons();
    }
    
    void addOutput(String str)
    {   /* this will split the message string into words using the space
           character as a delimiter, the words will be stored in consecutive 
           elements of array "words" */
        String[] words = str.split(" \\s*");
        try
        {	// travese array, taking each word in turn
        	for(int i = 0; i < words.length; i++)
        	{	/* if the first character of this word is $ this indicates that
            	    this string represents an image in the text message */
        		if(words[i].charAt(0) == '$')
            	{	/* the remainder of this word will be a number indicating the 
            	       array element in which the image is stored - retrieve this 
            	       number from the string */
            		String position = words[i].substring(1, words[i].length());
                	// cast this string number to an int value 
                	int pos = Integer.parseInt(position);
                	
            	}
            	else
               		// otherwise add the next text word to the text output area
               		docOutputArea.insertString(docOutputArea.getLength(), words[i] + " ", null);
        	}
        	// add a newline character to the text output area
        	docOutputArea.insertString(docOutputArea.getLength(), " \n", null);
        	// set the caret position in the text output area
        	outputArea.setCaretPosition(docOutputArea.getLength());
        }
      	catch(BadLocationException ee)
     	{   System.out.println(ee);
			System.exit(1);
    	}
    }
    
    void closeChatClient()
    {   // user has quit the message service - disable GUI components
    	// disable send message button
    	sendButton.setEnabled(false);
        // disable all name buttons using for loop
    	for(int i = 0; i < nameButtons.size(); i++)
        {
        	nameButtons.get(i).setEnabled(false);
        }
		// set input area to prevent text entry
        inputArea.setEditable(false);
    }

    void sendLoginDetails()
	{	try
		{	// get username from text field and encrypt
			String eUname = aes.Encrypt(username.getText());
        	// get password from password field and encrypt
			String ePword = aes.Encrypt(new String(password.getPassword()));
          	Boolean register = false;
          	clientOutputStream.writeObject(register);
			// send encrypted username to server
			clientOutputStream.writeObject(eUname);
			// send encrypted password to server
			clientOutputStream.writeObject(ePword);
     	}
		catch(IOException e) // thrown by methods writeObject
		{	
			System.out.println(e);
			System.exit(1);
		}
	}
    
    void checkRegistrationDetails()
    {
    	boolean capL = false;
    	boolean capU = false;
    	boolean number = false;
    	boolean valEmail = false;
    	boolean pEqual = true;
    	boolean name = false;
    	boolean email = false;
    	boolean password = true;
    	boolean passwordC = true;
    	boolean passwordL = false;
    	
    	int pL = 0;
    	
    	if(rName.getText().isEmpty())
    		name = true;
    	
    	if(rEmail.getText().isEmpty())
    		email = true;
    	
    	for (char c : rPassword.getPassword())
    		if(c == ' ')
    			password = false;
    	
    	for (char c : rPasswordC.getPassword())
    		if(c == ' ')
    			passwordC = false;
    	
    	if(!Arrays.equals(rPassword.getPassword(), rPasswordC.getPassword()))
        			pEqual = false;
    	
    	for(int i = 0; i < rPassword.getPassword().length; i++)
    		pL++;
    	
    	if(pL > 7 && pL < 16)
	    	passwordL = true;
    		
    	for (char c : (new String(rPassword.getPassword())).toCharArray()) 
    	{
    	      if (Character.isUpperCase(c))
    	        capU = true;
    	      else if (Character.isLowerCase(c))
    	        capL = true;
    	      else if (Character.isDigit(c))
    	        number = true;
    	}
    	
    	for (char c : rEmail.getText().toCharArray()) 
    	{
    	      if (c == '@')
    	    	  valEmail = true;
    	}
    	
    	if(capL && capU && number && valEmail && pEqual && passwordL && !name && !email && password && passwordC)
    		sendRegistrationDetails();
    	
    	else if(!capL)
    	{
    		regError(1);
    		attempts++;
    	}
    		
    	else if(!capU)
    	{
    		regError(2);
    		attempts++;
    	}
    		
    	else if(!number)
    	{
    		regError(3);
    		attempts++;
    	}
    		
    	else if(!valEmail)
    	{
    		regError(4);
    		attempts++;
    	}
    		
    	else if(!pEqual)
    	{
    		regError(5);
    		attempts++;
    	}
    		
    	else if(name)
    	{
    		regError(6);
    		attempts++;
    	}
    		
    	else if(email)
    	{
    		regError(7);
    		attempts++;
    	}
    		
    	else if(!password)
    	{
    		regError(8);
    		attempts++;
    	}
    		
    	else if(!passwordC)
    	{
    		regError(9);
    		attempts++;
    	}
    	
    	else if(!passwordL)
    	{
    		regError(10);
    		attempts++;
    	}
    		
    }
    
    void sendRegistrationDetails()
    {
    	try
    	{
    		Boolean register = true;
	    	String regName = aes.Encrypt(rName.getText());
	    	String regEmail = aes.Encrypt(rEmail.getText());
	    	String regPassword = aes.Encrypt(new String(rPassword.getPassword()));
	    	
	    	clientOutputStream.writeObject(register);
	    	clientOutputStream.writeObject(regName);
	    	clientOutputStream.writeObject(regEmail);
	    	clientOutputStream.writeObject(regPassword);
    	}
    	catch(IOException e)
		{	
    		System.out.println(e);
			System.exit(1);
		}
    	
    }

    void getConnections()
    {	try
        {   // initialise a socket and get a connection to server
    		socket = new Socket(InetAddress.getLocalHost(), 7500);
            // get input & output object streams
    		clientOutputStream = new ObjectOutputStream(socket.getOutputStream());
            clientInputStream = new ObjectInputStream(socket.getInputStream());
            /* create a new thread of Chat_ClientThread, sending input
               stream variable as a parameter */
            Chat_ClientThread thread = new Chat_ClientThread(clientInputStream);
            // start thread - execution will begin at method run
            thread.start();
        }
        catch(UnknownHostException e) // thrown by method getLocalHost
        {   System.out.println(e);
            System.exit(1);
        }
        catch(IOException e) // thrown by methods ObjectOutputStream, ObjectInputStream
        {   System.out.println(e);
            System.exit(1);
        }
    }

    void sendMessage(String str)
    {   try
		{   /* if you have not chosen any recipients this message will be 
		       sent to all friends by default */
            if(recipients.equals(""))
            {
            	for(int i = 0; i > names.size(); i++)
            	{
            		recipients = recipients + names.get(i) + ",";
            	}
            }
            
            // separate recipients and the message by inserting # character  
            str = recipients + "#" + str;
            // compress message
          	CompressedMessage cm = new CompressedMessage(str);
            cm.compress();
            // send message to server
            clientOutputStream.writeObject(cm);
            // clear recipients for next message
            recipients = "";
            // clear the input area
         	inputArea.setText("");
           	// change the colour of text on name buttons of friends online
            changeNameButtons(Color.BLACK);
        }
        catch(IOException e) // thrown by method writeObject
        {   System.out.println(e);
            System.exit(1);
        }
    }

    void closeStreams()
    {	
    	try
    	{
    		clientInputStream.close();
    		clientOutputStream.close();
    		//close socket too
    		socket.close();
    	}
        catch(IOException e) // thrown by method close
        {   System.out.println(e);
            System.exit(1);
        }
    }

    public static void main(String args[])
    {	
    	Chat_Client gameClient = new Chat_Client();
        gameClient.getConnections();
    }


    private class Chat_ClientThread extends Thread
    {	ObjectInputStream threadInputStream;

        public Chat_ClientThread(ObjectInputStream in)
        {   // initialise input stream
            threadInputStream = in;
        }

        // when method start() is called thread execution will begin in this method
        public void run()
        {   try
            {	/* read Boolean value sent by server - it is converted to
			  	   a primitive boolean value */
				String logon = (String)threadInputStream.readObject();
				boolean chatting = false;
				
				if(logon.equals(new String("Logon")))
					chatting = (Boolean)threadInputStream.readObject();
				
				else if(logon.equals(new String("Register")))
				{
					String eUN = (String)threadInputStream.readObject();
					String uN = aes.Decrypt(eUN);
					registered(uN);
					chatting = (Boolean)threadInputStream.readObject();
				}
				
				else if(logon.equals(new String("!Register")))
				{
					regError(11);
					logon = (String)threadInputStream.readObject();
					
					while(!logon.equals("Register"))
					{
						regError(11);
						logon = (String)threadInputStream.readObject();
					}
					
					String eUN = (String)threadInputStream.readObject();
					String uN = aes.Decrypt(eUN);
					registered(uN);
					chatting = (Boolean)threadInputStream.readObject();
				}
				
				if(chatting)
				{
					setUpChatClient(chatting);
					if(!chatting)
					 	// call method to close input & output streams & socket
						closeStreams();
					else
					{	// this loop will continue until this client quits the chat service
	                	while(chatting)
	                	{   // read next compressed message from server
	                		CompressedMessage cm = (CompressedMessage)clientInputStream.readObject();
	                    	// decompressed message
	                		String message = cm.decompress();
	                    	// if this client has quit the server will send this last message
	                    	if((message.trim()).substring(0,7).equals("goodbye"))
	                    	{	// chatClient should be closed
	                    		closeChatClient();
	                    		//end loop to close streams & socket
	                    		chatting = false;
	                    	}
	                    	else
	                    	{   if(message.substring(0,4).equals("join"))
	                        	{   /* if the first word in the message is "join" then another friend
	                               	   has joined the message service, retrieve the name of friend
	                                   and enable their name button in GUI */
	                    			changeFriends(message.substring(4,message.length()), true);
	                            	// output message in output area
	                            	addOutput(message.substring(4,message.length()) + " has joined");
	                            	
	                        	}
	                        	else
	                        	{   if(message.substring(0,4).equals("quit"))
	                            	{   /* if the first word in the message is "quit" then a friend
	                                       has quit the message service, retrieve the name of friend
	                                       and disable their name button in GUI */
	                                	changeFriends(message.substring(4,message.length()), false);
	                                	// output message in output area
	                                	addOutput(message.substring(4,message.length()) + " has quit");
	                            	}
	                            	else
	                            	{   if(message.substring(0,6).equals("online"))
	                                	{   /* if the first word in the message is "online" then this client
	                                           has just joined the chat service and this message lists
	                                           the names of all other friends that are online */
	                                    	// split string to separate names of friends online
	                                    	String[] online = message.substring(6,message.length()).split(",\\s*");
	                                    	if(!online[0].equals("none"))
	                                    	{   for(int i = 0; i < online.length; i++)
	                                    		{
	                                    			changeFriends(online[i], true);
	                                    		}   
	                                    	}
	                                    	// output message in output area
	                                    	addOutput("Your friends online : " + message.substring(6,message.length()-1));
	                                	}
	                                	else
	                                    	// output message in output area
	                                    	addOutput(message);
	                            	} // end else
	                        	} // end else
	                    	} // end else
						} // end while
					} // end else
				}
				// call method to change the client GUI
            } // end try
            catch(IOException e) // thrown by method readObject
            {	System.out.println(e);
                System.exit(1);
            }
            catch(ClassNotFoundException e) // thrown by method readObject
            {	System.out.println(e);
                System.exit(1);
            }
		} // end method run
    } // end of class Chat_ClientThread

    private class NameButtonHandler implements ActionListener
    {   // if any of the name buttons are clicked execution will continue in this method
        public void actionPerformed(ActionEvent e)
        {   // loop to identify which of the name buttons were clicked
            for(int r = 0; r < nameButtons.size(); r++)
            {	
            	if(e.getSource() == nameButtons.get(r))
            	{
            		ArrayList<String> rec = new ArrayList<String>();
            		if(!rec.contains(nameButtons.get(r).getName()))
            		{
            			recipients += nameButtons.get(r).getName() + ",";
            			changeNameButton(nameButtons.get(r), Color.RED);
            			rec.add( nameButtons.get(r).getName());
            		}

            	}    
            }
            // add this friend's name to recipients list
            
          	
            }
    }  // end of class NameButtonHandler

    private class ButtonHandler implements ActionListener
	{	// if the logon or send buttons are clicked execution will continue in this method
		public void actionPerformed(ActionEvent e)
		{	if(e.getSource() == logonButton)
				/* if the logon button is clicked call method to 
				   send the login details to the server */
				sendLoginDetails();
			else if(e.getSource() == sendButton)
				/* if the send button is clicked call method to 
				   send the message to the server */
            	sendMessage(inputArea.getText());
			else if(e.getSource() == lRegisterButton)
				setUpRegistration();
			else if(e.getSource() == rRegisterButton)
				checkRegistrationDetails();
			else if(e.getSource() == confirmationButton)
			{
				confirmed = true;
				try {
					clientOutputStream.writeObject(confirmed);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}		
		}
	}  // end of class ButtonHandler
} // end of class Chat_Client

