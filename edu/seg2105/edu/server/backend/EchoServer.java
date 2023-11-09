package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import edu.seg2105.client.common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  ChatIF serverUI;
  String loginKey = "id";
  boolean loginReceived = false;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF serverUI) 
  {
    super(port);
    this.serverUI = serverUI;
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
    String message = msg.toString();
    System.out.println("Message received: " + msg + " from " + client);
    if(message.startsWith("#login") && !loginReceived)
    {
      String loginID = message.split(" ")[1];
      client.setInfo(loginKey, loginID);
      loginReceived = true;
    } else if(message.startsWith("#login") && loginReceived)
    {
      try {
        client.sendToClient("you have already logged in!");
        client.close();
      } catch (IOException e) {
        serverUI.display("failed to close client");
        serverUI.display(e.toString());
      }
      
    }
    {
      this.sendToAllClients((String)client.getInfo(loginKey) + ": " + msg);
    }
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }


  //Class methods ***************************************************
  


  /**
   * Hook method called each time a new client connection is
   * accepted. The default implementation does nothing.
   * @param client the connection connected to the client.
   */
  protected void clientConnected(ConnectionToClient client) {
    System.out.println("a new client has connected!");;
  }

  /**
   * Hook method called each time a client disconnects.
   * The default implementation does nothing. The method
   * may be overridden by subclasses but should remains synchronized.
   *
   * @param client the connection with the client.
   */
  synchronized protected void clientDisconnected(
    ConnectionToClient client) {
      System.out.println(" a client has disconnected!");
    }

    public void handleMessageFromServerUI(String message)
  {
   
    if(message.charAt(0) == '#')
    {
      handleCommand(message);
    }
    else
    {
      this.sendToAllClients("SERVER MSG> " + message);
      serverUI.display("SERVER MSG> " + message);
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
        try
        {
          this.close();
        } catch (IOException e)
        {
          serverUI.display("fail on quit");
          serverUI.display(e.toString());
        }
        System.exit(0);
        break;
      case "#stop":
        if(this.isListening())
        {
          this.stopListening();
        }
        break;
      case "#close":
        this.stopListening();
        try
        {
          this.close();
        } catch (IOException e)
        {
          serverUI.display("fail on close");
          serverUI.display(e.toString());
        }
        break;
      case "#setport":
        if(!this.isListening())
        {
          this.setPort(Integer.parseInt(argument));
        } else {serverUI.display("server must be closed first!");}
        break;
      case "#start":
        if(!this.isListening())
        {
          try 
          {
            this.listen();
          } catch (IOException e) {
            serverUI.display("fail on start");
            serverUI.display(e.toString());
          }
        }
        break;
      case "#getport":
        serverUI.display(Integer.toString(this.getPort()));
        break;
      case "#help":
        serverUI.display("#quit            closes the server");
        serverUI.display("#stop            stop listening for new clients");
        serverUI.display("#close           stop listening for new clients and disconnect all clients");
        serverUI.display("#setport <port>  sets the port if not currently running");
        serverUI.display("#getport         displays the current port");
        break;
      default:
        serverUI.display("unknown command, type #help to view list of commands");
        break;
    }
  }
}
//End of EchoServer class
