import java.io.StringReader;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

public class RESTConsumer {

	public static final String USER = "admin";
	public static final String PASSWORD = "admin";
	ArrayList<JsonObject> tablesWithActiveFlows;


	public int selectMenu() {
		System.out.println("Seleccione una opción:");
		System.out.println("1. Mostrar switches conectados a la red");
		System.out.println("1. Anyadir un flujo");


		Scanner sc = new Scanner(System.in);

		int option = sc.nextInt();

		sc.close();
		return option;	
	}

	public ArrayList<String> showTopology() throws Exception{

		String url = "http://localhost:8181/restconf/operational/opendaylight-inventory:nodes/";

		// 1.Create a client
		HttpAuthenticationFeature authentication = HttpAuthenticationFeature.basic(USER, PASSWORD);

		Client client = ClientBuilder.newClient();

		client.register(authentication);

		// 2.Set a target to client
		WebTarget target = client.target(url);

		// 3.Get response
		Response resp = target.request(MediaType.APPLICATION_JSON).get();
	

		//need to buffer entity, in order to read the entity multiple times from the Response's InputStream
		resp.bufferEntity(); 
		String responseStringJSON = resp.readEntity(String.class);

		System.out.println(responseStringJSON);

		//Convert the String with JSON data to a JSON object
		JsonReader jsonReader = Json.createReader(new StringReader(responseStringJSON));
		JsonObject object = jsonReader.readObject();
		jsonReader.close();
		
		JsonObject netTopo = object.getJsonObject("nodes");

		JsonArray topo = netTopo.getJsonArray("node");

		ArrayList<String> nodeList = new ArrayList<>();

		for(int i=0;i<topo.size();i++) {

			//Information of each node
			JsonObject nodo = topo.getJsonObject(i);

			//Node name(ID)
			String nodeID = nodo.getString("id");
			System.out.println(nodeID);

			nodeList.add(nodeID);
		}

		return nodeList;
		
	}

	public ArrayList<int[]> getflowTables(String switchSelected){

		String url = "http://localhost:8181/restconf/operational/opendaylight-inventory:nodes/node/"+switchSelected+"/";

		// 1.Create a client

		HttpAuthenticationFeature authentication = HttpAuthenticationFeature.basic(USER, PASSWORD);

		Client client = ClientBuilder.newClient();

		client.register(authentication);

		// 2.Set a target to client
		WebTarget target = client.target(url);		

		// 3.Get response		
		Response resp = target.request(MediaType.APPLICATION_JSON).get();

		//need to buffer entity, in order to read the entity multiple times from the Response's InputStream
		resp.bufferEntity(); 
		String responseStringJSON = resp.readEntity(String.class);

		System.out.println(responseStringJSON);

		//Convert the String with JSON data to a JSON object
		JsonReader jsonReader = Json.createReader(new StringReader(responseStringJSON));
		JsonObject object = jsonReader.readObject();
		jsonReader.close();

		JsonArray nodeTables = object.getJsonArray("node").asJsonArray().getJsonObject(0).asJsonObject().getJsonArray("flow-node-inventory:table").asJsonArray();


		System.out.println(nodeTables.size());
		//System.out.println(nodeTables);

		ArrayList<int[]> nodeTablesID = new ArrayList<>();
		tablesWithActiveFlows = new ArrayList<JsonObject>();


		for(int i = 0; i < nodeTables.size(); i++) {

			JsonObject tabla = nodeTables.getJsonObject(i);

			String nodeTablesStatus = tabla.get("opendaylight-flow-table-statistics:flow-table-statistics").asJsonObject().get("active-flows").toString();

			int tableID = tabla.getInt("id");

			int[] entry = new int[2];
			entry[0] = tableID;
			entry[1] = Integer.parseInt(nodeTablesStatus);


			if(entry[1] != 0) {
				//Si la tabla contiene flujos activos los guardamos
				tablesWithActiveFlows.add(tabla);
			}

			nodeTablesID.add(entry);
		}
		return nodeTablesID;
	}

	//METODO QUE LLAMAMOS AL CLICAR SOBRE UNA TABLA CON FLUJOS ACTIVOS
	//DEBEMOS EXTRAER LOS FLUJOS DE LA TABLA CON ID QUE RECIBAMOS COMOPARANDOLOS CON L0S DE TABLESWITHACTIVEFLOWS
	//DEVOLVEREMOS UNA LISTA DE LOS ID DE LOS FLUJOS ACTIVOS QUE NO SEAN DE LA CONTROLADORA

	public ArrayList<Integer> getActiveFlows(String numberOfTable) {

		ArrayList<Integer> flowsID = new ArrayList<>();
		for(int i=0;i<tablesWithActiveFlows.size();i++) {
			if(tablesWithActiveFlows.get(i).getInt("id") == Integer.parseInt(numberOfTable)) {
				JsonArray jsa = tablesWithActiveFlows.get(i).getJsonArray("flow");
				for(int j=0;j<jsa.size();j++) {
					try {
						//Filtramos los flujos que añade la controladora
						flowsID.add(Integer.parseInt(jsa.get(j).asJsonObject().getString("id")));

					}catch(NumberFormatException nfe) {

					}

				}
			}
		}
		return flowsID;
	}
}
