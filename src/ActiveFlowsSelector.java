import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;


public class ActiveFlowsSelector {
	
	private JFrame f;
	private JPanel northContainer;
	private JComboBox combo;
	private ArrayList<JsonObject> flowsInformation;
	private String tableNumber;
	
	public ActiveFlowsSelector(ArrayList<Integer> flowsNumbers, ArrayList<JsonObject> tablesWithActiveFlows, String numberOfTable) {		
		
		f = new JFrame();
		f.setSize(500,413);
		f.setLocationRelativeTo(null);
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		flowsInformation = new ArrayList<>();
		flowsInformation = tablesWithActiveFlows;
		
		tableNumber = new String();
		tableNumber = numberOfTable;
		
		ComboListener cL = new ComboListener();
		combo = new JComboBox(flowsNumbers.toArray());

		// Accion a realizar cuando el JComboBox cambia de item seleccionado.
		combo.addActionListener(cL);
		
		northContainer = new JPanel();
		northContainer.setLayout(new FlowLayout());

		northContainer.add(combo);
		
		f.add(northContainer, BorderLayout.NORTH);
		
		f.setVisible(true);
	}
	
	class ComboListener implements ActionListener{
		/** Listens to the combo box. */
		public void actionPerformed(ActionEvent e) {
			System.out.println(combo.getSelectedItem());
			
			for(int i=0;i<flowsInformation.size();i++) {
				if(flowsInformation.get(i).getInt("id") == Integer.parseInt(tableNumber)) {
					
					JsonArray jsa = flowsInformation.get(i).getJsonArray("flow");
					for(int j=0;j<jsa.size();j++) {
						try {
							//Filtramos los flujos que añade la controladora cuyo id contiene letras
						
							if(Integer.parseInt(combo.getSelectedItem().toString()) == Integer.parseInt(jsa.get(j).asJsonObject().getString("id"))) {
								
								JTextPane text = new JTextPane();	
								/*Instanciamos una clase SimpleAttributeSet, que guardará los atributos 
								 para un determinado texto: si es negrita, cursiva, fuente, etc. 
								*/
								SimpleAttributeSet attrs = new SimpleAttributeSet();
								
								//Texto en negrita + nueva línea
								textoNegrita(attrs, text, prettyPrintJSON(jsa.get(j).toString()));
								nuevaLinea(text);
								
								JScrollPane scrollPane = new JScrollPane(text);
								
								f.add(scrollPane, BorderLayout.CENTER);
								
								f.revalidate();
								f.repaint();
								
							}
						
							
							
							
						}catch(NumberFormatException nfe) {
							
						}
				
					}
				}
			}	
		}
		
		//Método para texto en negrita
		private void textoNegrita(SimpleAttributeSet attrs, JTextPane text, String string){
	        
			/*Para modificar el valor de estos atributos, nos ayuda la clase StyleConstants. 
			Esta clase tiene muchos métodos para cambiar valores a una clase SimpleAttributeSet. 
			En este caso concreto hemos usado setBold() para ponerlo en negrita.
			*/
			StyleConstants.setBold(attrs, true);
	        
			/*Obtenemos el StyledDocument, que es lo que el JTextPane tiene dentro y 
			representa al texto que estamos viendo.
			El StyledDocument tiene un método insert() que admite tres parámetros:
				- Posición en la que se quiere insetar el texto dentro del documento.
				- El texto
				- Los atributos del texto.
			Como queremos insertar al final, la posición es justo la longitud del texto,
			esto se obtiene con el método getLength().
			*/
			try {
				text.getStyledDocument().insertString(
					text.getStyledDocument().getLength(), string, attrs);
			} catch (BadLocationException ex) {
				
			}

		}
	    
		//Método para texto en rojo
		private void textoRojo(SimpleAttributeSet attrs, JTextPane text, String string){
	        
			StyleConstants.setForeground(attrs, Color.red);
	        
			try {
				text.getStyledDocument().insertString(
					text.getStyledDocument().getLength(), string, attrs);
			} catch (BadLocationException ex) {
				
			}

		}
	    
		//Método para cambiar de línea
		private void nuevaLinea(JTextPane text){

			try {
				text.getStyledDocument().insertString(
					text.getStyledDocument().getLength(),
					System.getProperty("line.separator"), null);
			} catch (BadLocationException ex) {
			
			}
	        
		}
		
		
		
		
		/**
		 * A simple implementation to pretty-print JSON file.
		 *
		 * @param unformattedJsonString
		 * @return
		 */
		public String prettyPrintJSON(String unformattedJsonString) {
		  StringBuilder prettyJSONBuilder = new StringBuilder();
		  int indentLevel = 0;
		  boolean inQuote = false;
		  for(char charFromUnformattedJson : unformattedJsonString.toCharArray()) {
		    switch(charFromUnformattedJson) {
		      case '"':
		        // switch the quoting status
		        inQuote = !inQuote;
		        prettyJSONBuilder.append(charFromUnformattedJson);
		        break;
		      case ' ':
		        // For space: ignore the space if it is not being quoted.
		        if(inQuote) {
		          prettyJSONBuilder.append(charFromUnformattedJson);
		        }
		        break;
		      case '{':
		      case '[':
		        // Starting a new block: increase the indent level
		        prettyJSONBuilder.append(charFromUnformattedJson);
		        indentLevel++;
		        appendIndentedNewLine(indentLevel, prettyJSONBuilder);
		        break;
		      case '}':
		      case ']':
		        // Ending a new block; decrese the indent level
		        indentLevel--;
		        appendIndentedNewLine(indentLevel, prettyJSONBuilder);
		        prettyJSONBuilder.append(charFromUnformattedJson);
		        break;
		      case ',':
		        // Ending a json item; create a new line after
		        prettyJSONBuilder.append(charFromUnformattedJson);
		        if(!inQuote) {
		          appendIndentedNewLine(indentLevel, prettyJSONBuilder);
		        }
		        break;
		      default:
		        prettyJSONBuilder.append(charFromUnformattedJson);
		    }
		  }
		  return prettyJSONBuilder.toString();
		}

		/**
		 * Print a new line with indention at the beginning of the new line.
		 * @param indentLevel
		 * @param stringBuilder
		 */
		private void appendIndentedNewLine(int indentLevel, StringBuilder stringBuilder) {
		  stringBuilder.append("\n");
		  for(int i = 0; i < indentLevel; i++) {
		    // Assuming indention using 2 spaces
		    stringBuilder.append("  ");
		  }
		}
		
		
	}

}
