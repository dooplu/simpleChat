// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
    
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
      if(message.charAt(0) == '#')
      {
        handleCommand(message);
      }
      else
      {
        sendToServer(message);
      }
      
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }

  private void handleCommand(String command)
  {
    String argument = "";
    try
    {
      argument = command.split(" ")[1];
    } catch(ArrayIndexOutOfBoundsException e)
    {
    }
    command = command.split(" ")[0];

    switch (command) {
      case "#quit":
        this.quit();
        break;
      case "#logoff":
        if(this.isConnected()) 
        {
          try{
          this.closeConnection();
          } catch (IOException e)
          {
            clientUI.display("fail on close: " + e);
            this.quit();
          }
        }
        break;
      case "#sethost":
        if(!this.isConnected())
        {
          this.setHost(argument);
        } else { clientUI.display("must be logged out first!"); }
        break;
      case "#setport":
        if(!this.isConnected()) { this.setPort(Integer.parseInt(argument)); }
        else { clientUI.display("must be logged out first!"); }
        break;
      case "#login":
        if(!this.isConnected()) 
        {
          try
          {
            this.openConnection();
          } catch (IOException e)
          {
            clientUI.display("failed to open connection: " + e);
            this.quit();
          }
        } else { clientUI.display("you are already logged in!");}
        break;
      case "#gethost":
        clientUI.display(this.getHost());
        break;
      case "#getport":
        clientUI.display(Integer.toString(getPort()));
        break;
      case "#help":
        clientUI.display("#quit            closes the chat program");
        clientUI.display("#logoff          disconnect from the server without quitting");
        clientUI.display("#sethost <host>  sets the host if not currently connected");
        clientUI.display("#setport <port>  sets the port if not currently connected");
        clientUI.display("#login           connect to the server");
        clientUI.display("#gethost         displays the current host");
        clientUI.display("#getport         displays the current port");
        break;
      default:
        clientUI.display("unknown command, type #help to view a list of commands");
        break;
    }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }


/**
	 * Hook method called each time an exception is thrown by the client's
	 * thread that is waiting for messages from the server. The method may be
	 * overridden by subclasses.
	 * 
	 * @param exception
	 *            the exception raised.
	 */
  @Override
	protected void connectionException(Exception exception) {
    clientUI.display("Server went down.");
    this.quit();
	}


  /**
	 * Hook method called after the connection has been closed. The default
	 * implementation does nothing. The method may be overriden by subclasses to
	 * perform special processing such as cleaning up and terminating, or
	 * attempting to reconnect.
	 */
  @Override
	protected void connectionClosed() {
    clientUI.display("Connection closed");
	}
}

//End of ChatClient class
