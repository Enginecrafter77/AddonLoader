package addonloader.betamenu;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class RemoteTerminal {
	
	public static JFrame window;
	public static JSplitPane root_panel;
	public static JSplitPane input_panel;
	public static JScrollPane output_scroll;
	public static JScrollBar output_scroll_vertical;
	public static JButton input_send;
	public static JTextField input;
	public static JTextArea output;
	
	public static UpdaterThread updater;
	public static Socket channel;
	public static PrintStream channel_out;
	public static Scanner channel_in;
	
	public static void main(String[] args)
	{
		try
		{
			window = new JFrame();
			root_panel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			input_panel = new JSplitPane();
			input_send = new JButton("Send");
			input = new JTextField();
			output_scroll = new JScrollPane();
			output_scroll_vertical = output_scroll.getVerticalScrollBar();
			output = new JTextArea();
			
			FlushAction action = new FlushAction();
			input_send.addActionListener(action);
			input.addKeyListener(action);
			output.setEditable(false);
			output.setBackground(Color.BLACK);
			output.setForeground(Color.GREEN);
			
			root_panel.setResizeWeight(1);
			input_panel.setResizeWeight(1);
			root_panel.setLeftComponent(output_scroll);
			root_panel.setRightComponent(input_panel);
			input_panel.setLeftComponent(input);
			input_panel.setRightComponent(input_send);
			
			output_scroll.setViewportView(output);
			window.add(root_panel);
			window.setSize(300, 300);
			window.setTitle("Remote Terminal");
			
			updater = new UpdaterThread();
			channel = new Socket("192.168.1.177", 8765);
			channel_out = new PrintStream(channel.getOutputStream());
			channel_in = new Scanner(channel.getInputStream());
			
			output.append("[CONNECTED]\n");
			window.setVisible(true);
			updater.start();
			
			while(updater.isAlive())
			{
				if(channel_in.hasNextLine())
				{
					output.append(channel_in.nextLine() + "\n");
					output_scroll_vertical.setValue(output_scroll_vertical.getMaximum());
				}
			}
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
	}
	
	public static void flush_input()
	{
		channel_out.println(input.getText());
		output.append("> " + input.getText() + "\n");
		input.setText("");
	}
	
	public static class UpdaterThread extends Thread
	{
		@Override
		public void run()
		{
			try
			{
				while(!channel.isInputShutdown() && window.isVisible()) Thread.sleep(250);
				window.setVisible(false);
				this.interrupt();
				if(channel.isConnected()) channel.close();
				System.exit(0);
			}
			catch(Exception exc)
			{
				exc.printStackTrace();
			}
		}
	}
	
	public static class FlushAction implements ActionListener, KeyListener
	{		
		@Override
		public void actionPerformed(ActionEvent event)
		{
			flush_input();
		}

		@Override
		public void keyPressed(KeyEvent event)
		{
			if(event.getKeyCode() == 10) flush_input();
		}
		
		@Override
		public void keyTyped(KeyEvent arg0)
		{}

		@Override
		public void keyReleased(KeyEvent arg0)
		{}
	}
	
}
