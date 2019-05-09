import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
 
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;
 
@SuppressWarnings("serial")
public class JavaClient extends JFrame implements ActionListener, KeyListener,
        FocusListener {
 
    // Extra variables
    static String message = "";
    static String userName = "";
    static String iPAddress = null;
 
    // Networking Variables
    static Socket socket = null;
    static PrintWriter writer = null;
 
    // // Graphics Variables
    static JTextArea msgRec = new JTextArea(100, 50);
    static JTextArea msgSend = new JTextArea(100, 50);
    JButton send = new JButton("Send");
    JScrollPane pane2, pane1;
 
    public JavaClient() {
        super("Client");
        setBounds(0, 0, 400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);
 
        msgRec.setEditable(false);
        msgRec.setBackground(Color.BLACK);
        msgRec.setForeground(Color.WHITE);
        msgRec.addFocusListener(this);
        msgRec.setText("");
 
        msgRec.setWrapStyleWord(true);
        msgRec.setLineWrap(true);
 
        pane2 = new JScrollPane(msgRec);
        pane2.setBounds(0, 0, 400, 200);
        pane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(pane2);
 
        msgSend.setBackground(Color.LIGHT_GRAY);
        msgSend.setForeground(Color.BLACK);
        msgSend.setLineWrap(true);
        msgSend.setWrapStyleWord(true);
 
        msgSend.setText("Enter Text Here");
        msgSend.addFocusListener(this);
        msgSend.addKeyListener(this);
 
        pane1 = new JScrollPane(msgSend);
        pane1.setBounds(0, 200, 400, 200);
        pane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(pane1);
 
        send.setBounds(0, 400, 400, 40);
        add(send);
        send.addActionListener(this);
 
 
        addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
 
                if (!msgRec.getText().equals("")) {
                    System.out.println("");
 
                    writer.println("\t<<<" + userName + ": Focusing to Comunication>>>");
                    writer.flush();
 
                }
            }
 
            @Override
            public void windowLostFocus(WindowEvent e) {
                if (!msgRec.getText().equals("")) {
                    writer.println("\t<<<" + userName + ": Ignoring to Comunication>>>");
                    writer.flush();
                }
            }
 
        });
 
        if ((userName) != null) {
            setVisible(true);
        } else {
            System.exit(0);
        }
    }
 
   
    public static void main(String[] args) throws Exception {
        
 
        userName = JOptionPane.showInputDialog("User Name (Client)");
        iPAddress = JOptionPane.showInputDialog("Enter Server IpAddress");
 
        // swing thread
        (new Thread(new Runnable() {
            public void run() {
                new JavaClient();
 
            }
 
        })).start();
 
        socket = new Socket(iPAddress, 8888);
        msgRec.setText("Connected!");
 
        // listening port thread
        (new Thread(new Runnable() {
            public void run() {
 
                try {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
 
                    String line = null;
                    boolean testFlag = true;
                    while ((line = reader.readLine()) != null) {
                        msgRec.append("\n" + line);
                        cursorUpdate();
 
                        if (!reader.ready()) {
                            testFlag = true;
                        }
                    }
 
                } catch (IOException ee) {
                    try {
                        socket.close();
                    } catch (IOException eee) {
                        eee.printStackTrace();
                    }
                    ee.printStackTrace();
                }
            }
        })).start();
 
        try {
            writer = new PrintWriter(socket.getOutputStream(), true);
 
        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException eee) {
            }
        }
    }
 
    //KeyBoardEvents
 
    @Override
    public void keyTyped(KeyEvent e) {
    }
 
    @Override
    public void keyReleased(KeyEvent e) {
    }
 
    @Override
    public void keyPressed(KeyEvent e) {
 
        if ((e.getKeyCode() == KeyEvent.VK_ENTER) && e.isShiftDown()) {
            msgSend.append("\n");
 
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            sendMessage();
        }
 
        else if ((e.getKeyCode() == KeyEvent.VK_X) && e.isControlDown()) {
            System.exit(0);
        }
    }
 
    // FocusEvents
    @Override
    public void focusGained(FocusEvent e) {
 
        if (e.getSource() == msgRec) {
            if (!(msgRec.getText().equals("") || msgRec.getText().equals(
                    "Connected!"))) {
 
                writer.println("\t ***" + userName
                        + ": The Msg is being read......***");
                writer.flush();
            }
        } else if (e.getSource() == msgSend) {
            // Set Mesg sending area clear
            if (msgSend.getText().equals("Enter Text Here")) {
                msgSend.setText("");
            } else {
                writer.println("\t ***" + userName
                        + ": The Msg is being typed......***");
                writer.flush();
            }
 
        }
    }
 
    @Override
    public void focusLost(FocusEvent e) {
    }
 
    private void sendMessage() {
        writer.println(userName + " :" + msgSend.getText());
 
        msgRec.append("\nMe: " + msgSend.getText());
        writer.flush();
        cursorUpdate();
 
        msgSend.setText("");
        msgSend.setCaretPosition(0);
    }
 
    private static void cursorUpdate() {
        // Update cursor position
        DefaultCaret caret = (DefaultCaret) msgRec.getCaret();
        caret.setDot(msgRec.getDocument().getLength());
 
        DefaultCaret caret2 = (DefaultCaret) msgSend.getCaret();
        caret2.setDot(msgSend.getDocument().getLength());
    }


	@Override
	public void actionPerformed(ActionEvent e) {
		
		
	}
}