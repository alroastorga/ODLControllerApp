import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.glassfish.jersey.internal.util.Base64;

public class StartWindow{

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new StartWindow();
			}
		});


	}

	private JButton createFlowsButton, showFlowsButton, deleteFlowsButton;
	private JFrame welcomeWindow;

	public StartWindow(){

		try {

			BufferedImage myImage;
			myImage = ImageIO.read(new File("images/intro.jpg"));

			welcomeWindow = new JFrame("Welcome");
			welcomeWindow.setContentPane(new ImagePanel(myImage));

			welcomeWindow.setSize(500,413);
			welcomeWindow.setLocationRelativeTo(null);
			welcomeWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			welcomeWindow.setLayout(new GridBagLayout());

			ButtonListener bL = new ButtonListener();

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridwidth = GridBagConstraints.REMAINDER;

			createFlowsButton = new JButton("Create flow");
			createFlowsButton.addActionListener(bL);

			showFlowsButton = new JButton("Show flows");
			showFlowsButton.addActionListener(bL);

			deleteFlowsButton = new JButton("Delete flows");
			deleteFlowsButton.addActionListener(bL);

			welcomeWindow.add(showFlowsButton, gbc);

			welcomeWindow.add(createFlowsButton, gbc);

			welcomeWindow.add(deleteFlowsButton, gbc);

			welcomeWindow.setVisible(true);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	class ButtonListener implements ActionListener
	{
		public void actionPerformed (ActionEvent e)
		{	
			if (e.getSource() == createFlowsButton){

				FlowCreatorWindow fc = new FlowCreatorWindow();

			}
			if (e.getSource() == showFlowsButton) {
				FlowViewerWindow f = new FlowViewerWindow();
				
			}
			if (e.getSource() == deleteFlowsButton) {

				JTextField switchid = new JTextField();
				JTextField switchtable = new JTextField();
				JTextField flowNumber = new JTextField();

				Object[] message = {
						"Switch:", switchid,
						"Table:", switchtable,
						"Flow ID;", flowNumber

				};
				int option = JOptionPane.showConfirmDialog(welcomeWindow, message, "Delete", JOptionPane.OK_CANCEL_OPTION);
				if (option == JOptionPane.OK_OPTION) {
					try {
						URL url = new URL("http://localhost:8181/restconf/config/opendaylight-inventory:nodes/node/"+switchid.getText()+"/flow-node-inventory:table/"+switchtable.getText()
										+"/flow/"+flowNumber.getText());

						HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
						httpCon.setDoOutput(true);
						String userCredentials = "admin:admin";
						String basicAuth = "Basic " + new String(new Base64().encode(userCredentials.getBytes()));
						httpCon.setRequestProperty ("Authorization", basicAuth);
						httpCon.setRequestProperty(
								"Content-Type", "application/xml" );
						httpCon.setRequestMethod("DELETE");
						httpCon.connect();

					} catch (MalformedURLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			}

		}
	}


	class ImagePanel extends JComponent {
		private Image image;
		public ImagePanel(Image image) {
			this.image = image;
		}
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(image, 0, 0, this);
		}
	}
}
