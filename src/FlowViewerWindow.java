import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class FlowViewerWindow{

	private JTextField tf;
	private JComboBox combo;
	private JFrame f;
	private JMenuBar menu;
	private JPanel northContainer, centerContainer;
	private ArrayList<String> nodeList;
	private JScrollPane sp;
	private JTable jt;
	private ListSelectionModel select;
	private RESTConsumer r;

	public FlowViewerWindow(){

		f = new JFrame();
		f.setSize(500,413);
		f.setLocationRelativeTo(null);
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		ComboListener cL = new ComboListener();

		menu = new JMenuBar();

		f.setJMenuBar(menu);

		// Creacion del JTextField
		tf = new JTextField(20);

		// Creacion del JComboBox y añadir los items.
		getSwitches();

		combo = new JComboBox(nodeList.toArray());

		// Accion a realizar cuando el JComboBox cambia de item seleccionado.
		combo.addActionListener(cL);

		// Creacion de la ventana con los componentes

		northContainer = new JPanel();
		northContainer.setLayout(new FlowLayout());

		northContainer.add(combo);
		northContainer.add(tf);

		jt = new JTable();
		sp=new JScrollPane(jt); 

		centerContainer = new JPanel();
		centerContainer.add(sp);

		f.add(centerContainer, BorderLayout.CENTER);

		f.add(northContainer, BorderLayout.NORTH);


		f.setVisible(true);

	}

	public void getSwitches(){

		RESTConsumer r = new RESTConsumer();	
		try {
			nodeList = r.showTopology();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(f, "Failed to connect", "Alert", JOptionPane.WARNING_MESSAGE); 

		}	
	
	}

	public void getSwitchTables(String switchSelected){
		r = new RESTConsumer();
		ArrayList<int[]> flowTables = new ArrayList<int[]>();
		flowTables = r.getflowTables(switchSelected);
		paintTable(flowTables);	
	}


	private void paintTable(ArrayList<int[]> flowTables) {

		centerContainer.removeAll();

		String[] columnNames = { "ID", "Flow rules"};
		String[][] tableData = toArrayFromArrayList(flowTables);

		jt = new JTable(tableData, columnNames);
		jt.setRowSelectionAllowed(true);
		ListSelectionModel select= jt.getSelectionModel();  
		select.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);  
		select.addListSelectionListener(new ListSelectionListener() {  
			public void valueChanged(ListSelectionEvent e) {  
				if(e.getValueIsAdjusting()) {
					String selectedFlow = null;  
					int row = jt.getSelectedRow();  
					int column = jt.getSelectedColumn();  
					selectedFlow = jt.getValueAt(row, 0).toString();  
					System.out.println("Table element selected is: " + selectedFlow); 
					
					if(hasActiveFlows(row)){
						ActiveFlowsSelector afs = new ActiveFlowsSelector(r.getActiveFlows(selectedFlow), r.tablesWithActiveFlows, selectedFlow);
					}
				} 

			}  

			public boolean hasActiveFlows(int rowSelected){
				if(Integer.parseInt(jt.getValueAt(rowSelected, 1).toString()) == 0) {
					return false;
				}else {
					return true;
				}
			}
			
		});  

		sp=new JScrollPane(jt); 

		centerContainer = new JPanel();
		centerContainer.add(sp);

		f.add(centerContainer, BorderLayout.CENTER);

		f.revalidate();
		f.repaint();

	}

	public String[][] toArrayFromArrayList(ArrayList<int[]> a) {

		String[][] tableArray = new String[a.size()][2];
		for(int i=0;i<a.size();i++) {
			tableArray[i][0] = String.valueOf(a.get(i)[0]);
			tableArray[i][1] = String.valueOf(a.get(i)[1]);
		}
		return tableArray;
	}

	class ComboListener implements ActionListener{
		/** Listens to the combo box. */
		public void actionPerformed(ActionEvent e) {
			tf.setText(combo.getSelectedItem().toString());
			getSwitchTables(tf.getText());			

		}
	}


}
