import javax.imageio.ImageIO;
import javax.swing.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.glassfish.jersey.internal.util.Base64;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FlowCreatorWindow {

	private JButton createButton;


	private String[] defined = {"ID: ", "Flow name: ", "Table ID: ", "Priority: ", "Cookie: ", "Cookie mask: ", 
			"idle timeout: ", "hard timeout: "};
	private String[] matches = {"IPv4 Dest Addr: ", "IPv4 Src Addr: ", "IPv6 Dest Addr: ", "IPv6 Src Addr: ", "Ethernet type: ", 
			"MAC Dest Addr: ", "MAC Src Addr: ", "In Port: "};

	private String[] actions = {"Set IP Dest Addr: ", "Set IP Src Addr: ", "Set MAC Dest Addr: ", "Set MAC Src Addr: ",
			"Send to table: ", "Forward Port: ", "Drop: "};

	private String[] labels = {"ID: ", "Flow name: ", "Table ID: ", "Priority: ", "Cookie: ", "Cookie mask: ", 
			"idle timeout: ", "hard timeout: ", "IPv4 Dest Addr: ", "IPv4 Src Addr: ", "IPv6 Dest Addr: ", "IPv6 Src Addr: ", "Ethernet type: ", 
			"MAC Dest Addr: ", "MAC Src Addr: ", "In Port: ", "Set IP Dest Addr: ", "Set IP Src Addr: ", "Set MAC Dest Addr: ", "Set MAC Src Addr: ",
			"Send to table: ", "Forward Port: ", "Drop: "};

	private int numDefined = defined.length;
	private int numPairs = matches.length;
	private int numActions = actions.length;

	private JTextField[] textField = new JTextField[labels.length];
	private JTextField switchField;
	private JFrame f;

	public FlowCreatorWindow() {

		int contador = 0;

		JPanel p = new JPanel(new SpringLayout());

		JLabel switchSelected = new JLabel("Select switch: ", JLabel.TRAILING);
		switchSelected.setForeground(Color.RED);
		p.add(switchSelected);
		switchField = new JTextField(15);
		switchSelected.setLabelFor(switchField);
		p.add(switchField);



		for (int i = 0; i < numDefined; i++) {
			JLabel l = new JLabel(defined[i], JLabel.TRAILING);
			p.add(l);
			textField[contador] = new JTextField(15);
			l.setLabelFor(textField[contador]);
			p.add(textField[contador]);
			contador++;
		}  

		JLabel match = new JLabel("MATCH", JLabel.TRAILING);
		match.setForeground(Color.BLUE);
		JLabel pair = new JLabel();
		p.add(match);
		match.setLabelFor(pair);
		p.add(pair);

		for (int i = 0; i < numPairs; i++) {
			JLabel l = new JLabel(matches[i], JLabel.TRAILING);
			p.add(l);
			textField[contador] = new JTextField(15);
			l.setLabelFor(textField[contador]);
			p.add(textField[contador]);
			contador++;
		}

		match = new JLabel("APPLY ACTION", JLabel.TRAILING);
		match.setForeground(Color.BLUE);
		pair = new JLabel();
		p.add(match);
		match.setLabelFor(pair);
		p.add(pair);


		for (int i = 0; i < numActions; i++) {
			JLabel l = new JLabel(actions[i], JLabel.TRAILING);
			p.add(l);
			textField[contador] = new JTextField(15);
			l.setLabelFor(textField[contador]);
			p.add(textField[contador]);
			contador++;
		}

		createButton = new JButton("Create");
		ButtonListener bl = new ButtonListener();

	
		JLabel jl = new JLabel(); 
		jl.setPreferredSize(new Dimension(100,40));	
		p.add(jl);

		createButton.addActionListener(bl);
		p.add(createButton);

		//Lay out the panel.
		SpringUtilities.makeCompactGrid(p,
				numDefined + numPairs + numActions + 4, 2, //rows, cols
				6, 6,        //initX, initY
				6, 6);       //xPad, yPad

		//Create and set up the window.
		f = new JFrame("Flow Config");
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.setLocationRelativeTo(null);


		ScrollPane sp = new ScrollPane();

		sp.add(p);
		sp.setSize(400, 500);

		//Set up the content pane.
		p.setOpaque(true);  //content panes must be opaque
		f.setContentPane(sp);

		//Display the window.
		f.pack();
		f.setVisible(true);
	}


	class ButtonListener implements ActionListener
	{
		public void actionPerformed (ActionEvent e)
		{	
			if (e.getSource() == createButton){
				for (int i = 0 ; i < labels.length ; i++)
				{
					System.out.println(labels[i]+"-> "+textField[i].getText());
				}
				createFlowCallXML();

			}
		}
	}


	public void createFlowCallXML() {

		String xmlStringValue = null;
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			// Create flow root element 
			Element flowRootElement = doc.createElement("flow");
			doc.appendChild(flowRootElement);
			// set attribute to flow element
			Attr attr = doc.createAttribute("xmlns");
			attr.setValue("urn:opendaylight:flow:inventory");
			flowRootElement.setAttributeNode(attr);
			// Create strict Element
			Element strictElement = doc.createElement( "strict" );
			strictElement.appendChild( doc.createTextNode( "false" ) );
			flowRootElement.appendChild( strictElement );
			// Create tableid Element
			Element tableidElement = doc.createElement( "table_id" );
			tableidElement.appendChild( doc.createTextNode( textField[2].getText() ) );
			flowRootElement.appendChild( tableidElement );
			// Create id Element
			Element idElement = doc.createElement( "id" );
			idElement.appendChild( doc.createTextNode( textField[0].getText() ) );
			flowRootElement.appendChild( idElement );
			// Create priority Element
			Element priorityElement = doc.createElement( "priority" );
			priorityElement.appendChild( doc.createTextNode( textField[3].getText() ) );
			flowRootElement.appendChild( priorityElement );
			// Create idleTimeout Element
			Element idleElement = doc.createElement( "idle-timeout" );
			idleElement.appendChild( doc.createTextNode( textField[6].getText() ) );
			flowRootElement.appendChild( idleElement );
			// Create hardTimeout Element
			Element hardElement = doc.createElement( "hard-timeout" );
			hardElement.appendChild( doc.createTextNode( textField[7].getText() ) );
			flowRootElement.appendChild( hardElement );
			// Create cookie Element
			Element cookieElement = doc.createElement( "cookie" );
			cookieElement.appendChild( doc.createTextNode( textField[4].getText() ) );
			flowRootElement.appendChild( cookieElement );
			// Create cookiemask Element
			Element cookieMaskElement = doc.createElement( "cookie_mask" );
			cookieMaskElement.appendChild( doc.createTextNode( textField[5].getText() ) );
			flowRootElement.appendChild( cookieMaskElement );
			// Create flowName Element
			Element flowNameElement = doc.createElement( "flow-name" );
			flowNameElement.appendChild( doc.createTextNode( textField[1].getText() ) );
			flowRootElement.appendChild( flowNameElement );


			////////////////ACTIONS////////////////

			Element instructionsElement = doc.createElement( "instructions" );

			Element instructionElement = doc.createElement( "instruction" );
			instructionsElement.appendChild( instructionElement );

			Element orderElement = doc.createElement( "order" );
			instructionElement.appendChild( orderElement );
			orderElement.appendChild( doc.createTextNode( "0" ));

			//SEND TO TABLE
			if(!textField[20].getText().isEmpty()){

				Element sendToTableElement = doc.createElement( "go-to-table" );
				instructionElement.appendChild( sendToTableElement );

				Element tableElement = doc.createElement( "table_id" );			
				tableElement.appendChild( doc.createTextNode( textField[20].getText()));
				sendToTableElement.appendChild( tableElement );

			}

			if(!textField[21].getText().isEmpty() || !textField[19].getText().isEmpty() || !textField[18].getText().isEmpty() ||
					!textField[17].getText().isEmpty() || !textField[16].getText().isEmpty() || !textField[22].getText().isEmpty()){

				Element applyActionsElement = doc.createElement( "apply-actions" );
				instructionElement.appendChild( applyActionsElement );

				Element actionElement = doc.createElement( "action" );
				applyActionsElement.appendChild( actionElement );
				actionElement.appendChild( orderElement.cloneNode(true) );

				//FORWARD PORT
				if(!textField[21].getText().isEmpty()) {

					Element outActionElement = doc.createElement( "output-action" );
					actionElement.appendChild( outActionElement );

					Element outConnectorElement = doc.createElement( "output-node-connector" );
					Element maxLengthElement = doc.createElement( "max-length" );
					maxLengthElement.appendChild( doc.createTextNode("60"));

					outConnectorElement.appendChild( doc.createTextNode( textField[21].getText()));
					outActionElement.appendChild( outConnectorElement );
					outActionElement.appendChild( maxLengthElement );

				}

				//SET NEW MAC SRC ADDR
				if(!textField[19].getText().isEmpty()) {

					Element setMacSrcElement = doc.createElement( "set-dl-src-action" );
					actionElement.appendChild( setMacSrcElement );

					Element addrElement = doc.createElement( "address" );
					addrElement.appendChild( doc.createTextNode( textField[19].getText()));
					setMacSrcElement.appendChild( addrElement );

				}

				//SET NEW MAC DEST ADDR
				if(!textField[18].getText().isEmpty()) {

					Element setMacDstElement = doc.createElement( "set-dl-dst-action" );
					actionElement.appendChild( setMacDstElement );

					Element addrElement = doc.createElement( "address" );
					addrElement.appendChild( doc.createTextNode( textField[18].getText()));
					setMacDstElement.appendChild( addrElement );

				}

				//SET NEW IP SRC ADDR
				if(!textField[17].getText().isEmpty()) {

					Element setIpSrcElement = doc.createElement( "set-nw-src-action" );
					actionElement.appendChild( setIpSrcElement );

					Element addrElement = doc.createElement( "address" );
					addrElement.appendChild( doc.createTextNode( textField[17].getText()));
					setIpSrcElement.appendChild( addrElement );

				}

				//SET NEW IP DEST ADDR
				if(!textField[16].getText().isEmpty()) {

					Element setIpDstElement = doc.createElement( "set-nw-dst-action" );
					actionElement.appendChild( setIpDstElement );

					Element addrElement = doc.createElement( "address" );
					addrElement.appendChild( doc.createTextNode( textField[16].getText()));
					setIpDstElement.appendChild( addrElement );

				}

				//DROP
				if(!textField[22].getText().isEmpty()) {

					Element dropElement = doc.createElement( "drop-action" );
					actionElement.appendChild( dropElement );

				}


			}

			flowRootElement.appendChild( instructionsElement );





			///////////MATCH////////////////

			Element matchElement = doc.createElement( "match" );

			//ANY ETHERNET MATCH
			if(!textField[12].getText().isEmpty() || !textField[13].getText().isEmpty() || !textField[14].getText().isEmpty()) {

				Element etherMatchElement = doc.createElement( "ethernet-match" );
				matchElement.appendChild( etherMatchElement );

				//ETHERNET TYPE
				if(!textField[12].getText().isEmpty()) {		

					Element etherTypeElement = doc.createElement( "ethernet-type" );
					etherMatchElement.appendChild( etherTypeElement );

					Element typeElement = doc.createElement( "type" );
					etherTypeElement.appendChild( typeElement );
					typeElement.appendChild( doc.createTextNode( textField[12].getText() ) );

				}

				//ETHERNET DEST ADDR
				if(!textField[13].getText().isEmpty()){

					Element etherDestElement = doc.createElement( "ethernet-destination" );
					etherMatchElement.appendChild( etherDestElement );

					Element addressElement = doc.createElement( "address" );
					etherDestElement.appendChild( addressElement );

					addressElement.appendChild( doc.createTextNode( textField[13].getText() ) );

				}

				//ETHERNET SRC ADDR
				if(!textField[14].getText().isEmpty()){

					Element etherSrcElement = doc.createElement( "ethernet-source" );
					etherMatchElement.appendChild( etherSrcElement );

					Element addressElement = doc.createElement( "address" );
					etherSrcElement.appendChild( addressElement );

					addressElement.appendChild( doc.createTextNode( textField[14].getText() ) );

				}
			}

			//IPV6 SRC ADDR
			if(!textField[11].getText().isEmpty()){

				Element ipv6SrcElement = doc.createElement( "ipv6-source" );
				matchElement.appendChild( ipv6SrcElement );
				ipv6SrcElement.appendChild( doc.createTextNode( textField[11].getText() ) );

			}

			//IPV6 DEST ADDR
			if(!textField[10].getText().isEmpty()){

				Element ipv6DestElement = doc.createElement( "ipv6-destination" );
				matchElement.appendChild( ipv6DestElement );
				ipv6DestElement.appendChild( doc.createTextNode( textField[10].getText() ) );

			}

			//IPV4 SRC ADDR
			if(!textField[9].getText().isEmpty()){

				Element ipv4SrcElement = doc.createElement( "ipv4-source" );
				matchElement.appendChild( ipv4SrcElement );
				ipv4SrcElement.appendChild( doc.createTextNode( textField[9].getText() ) );

			}

			//IPV4 DEST ADDR
			if(!textField[8].getText().isEmpty()){

				Element ipv4DestElement = doc.createElement( "ipv4-destination" );
				matchElement.appendChild( ipv4DestElement );
				ipv4DestElement.appendChild( doc.createTextNode( textField[8].getText() ) );

			}

			//IN PORT
			if(!textField[15].getText().isEmpty()){

				Element inPortElement = doc.createElement( "in-port" );
				matchElement.appendChild( inPortElement );
				inPortElement.appendChild( doc.createTextNode( textField[15].getText() ) );

			}

			flowRootElement.appendChild( matchElement );


			// Transform Document to XML String
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));

			// Get the String value of final xml document
			xmlStringValue = writer.getBuffer().toString();
		} catch (ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
		}

		System.out.println(xmlStringValue);



		try {
			URL url = new URL("http://localhost:8181/restconf/config/opendaylight-inventory:nodes/node/"
					+switchField.getText()+"/flow-node-inventory:table/"+textField[2].getText()+"/flow/"+textField[0].getText());

			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setDoOutput(true);
			String userCredentials = "admin:admin";
			String basicAuth = "Basic " + new String(new Base64().encode(userCredentials.getBytes()));
			httpCon.setRequestProperty ("Authorization", basicAuth);
			httpCon.setRequestMethod("PUT");
			httpCon.setRequestProperty("Content-Type", "application/xml");

			OutputStreamWriter out = new OutputStreamWriter(
					httpCon.getOutputStream());
			out.write(xmlStringValue);
			out.close();
			httpCon.getInputStream();

			JOptionPane.showMessageDialog(f, "Flow sent to the controller", "Info", JOptionPane.OK_OPTION); 

		}catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
			JOptionPane.showMessageDialog(f, "Failed to connect", "Alert", JOptionPane.WARNING_MESSAGE); 
		}

	}
}

