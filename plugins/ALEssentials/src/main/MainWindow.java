package main;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import lejos.hardware.BrickFinder;
import lejos.hardware.BrickInfo;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;

public class MainWindow {

	private JFrame frame;
	private JFileChooser file;
	private JLabel path;
	private JTextField ip;
	private final ButtonGroup action;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable() {
			public void run()
			{
				try
				{
					MainWindow window = new MainWindow();
					window.frame.setTitle("leJOS EV3 Menu Update Tool");
					window.frame.setVisible(true);
				}
				catch (Exception e)
				{
					JOptionPane.showMessageDialog(null, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow()
	{
		ActionListener al = new FileSelection();
		
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		action = new ButtonGroup();
		UpdateType[] tps = UpdateType.values();
		JRadioButton[] buttons = new JRadioButton[tps.length];
		for(int i = 0; i < tps.length; i++)
		{
			buttons[i] = new JRadioButton(tps[i].toString());
			buttons[i].setBounds(20, 120 + 25 * i, 110, 25);
			buttons[i].setActionCommand("type");
			buttons[i].addActionListener(al);
			action.add(buttons[i]);
			panel.add(buttons[i]);
		}
		
		file = new JFileChooser();
		file.setFileFilter(new FileNameExtensionFilter("Java Archives", "jar"));
		
		path = new JLabel("[Select File]");
		path.setBounds(20, 20, 200, 25);
		panel.add(path);
		
		ip = new JTextField("10.0.1.1");
		ip.setToolTipText("EV3 IP Address or Name");
		ip.setBounds(20, 80, 100, 25);
		panel.add(ip);
		
		JButton sf = new JButton("Open");
		sf.setBounds(20, 45, 75, 25);
		sf.setActionCommand("select");
		sf.addActionListener(al);
		panel.add(sf);
		
		JButton upload = new JButton("Upload");
		upload.setBounds(100, 45, 100, 25);
		upload.setActionCommand("upload");
		upload.addActionListener(al);
		panel.add(upload);
		
		JButton reconnect = new JButton("Search");
		reconnect.setBounds(130, 80, 100, 25);
		reconnect.setActionCommand("search");
		reconnect.addActionListener(al);
		panel.add(reconnect);
	}
	
	public void openFileChooser()
	{
		file.showOpenDialog(frame);
	}
	
	public void uploadFile(UpdateType u)
	{
		try
		{
			Socket sck = new Socket(ip.getText(), UpdateServer.port);
			u.copyTo(sck.getOutputStream(), file.getSelectedFile().toPath());
			sck.close();
		}
		catch(IOException e)
		{
			JOptionPane.showMessageDialog(frame, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private class FileSelection implements ActionListener
	{
		private UpdateType u;
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(e.getActionCommand().equals("select"))
			{
				openFileChooser();
				if(file.getSelectedFile() != null)
				{
					path.setText(file.getSelectedFile().getAbsolutePath());
				}
			}
			else if(e.getActionCommand().equals("type"))
			{
				JRadioButton b = (JRadioButton)e.getSource();
				u = UpdateType.parse(b.getText());
			}
			else if(e.getActionCommand().equals("upload"))
			{
				if(action.getSelection() != null && !path.getText().equals("[Select File]"))
				{
					uploadFile(u);
				}
				else
				{
					JOptionPane.showMessageDialog(frame, "Select type and file, and then upload.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			else
			{
				BrickInfoExt[] br = BrickInfoExt.convert(BrickFinder.discover());
				BrickInfoExt sbr = (BrickInfoExt)JOptionPane.showInputDialog(frame, "Found EV3s:", "Select IP", JOptionPane.QUESTION_MESSAGE, null, br, br[0]);
				if(sbr != null)
				{
					ip.setText(sbr.ip);					
				}
			}
		}
	}
	
	private static class BrickInfoExt
	{
		public String name;
		public String ip;
		
		public BrickInfoExt(BrickInfo i)
		{
			this.name = i.getName();
			this.ip = i.getIPAddress();
		}
		
		@Override
		public String toString()
		{
			return name + '(' + ip + ')';
		}
		
		public static BrickInfoExt[] convert(BrickInfo[] bricks)
		{
			BrickInfoExt[] res = new BrickInfoExt[bricks.length];
			for(int i = 0; i < bricks.length; i++)
			{
				res[i] = new BrickInfoExt(bricks[i]);
			}
			return res;
		}
	}
}
