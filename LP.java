 /*-******************************************
 *  Rahil Sharma                             *
 *  Multi-thread Label Propogation Algorithm *
 *  (Main Class LP)                          *
 *  Modified version                         *
 *  Scalable to Multicore Architcture        *
 *  Date : 18th October, 2014 (version 1.0)  * 
 *********************************************/

 
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


class NODE {
int label_name;
int id;
Set<Integer> neighbors;

public NODE (int id, int label_name) { 
this.label_name = label_name;
this.id = id;
this.neighbors = new HashSet<Integer>();
}
/*-************************* Node methods defined ***********************-*/
public int get_id() {
return id;
}

public void set_label_name(int label_name) {
this.label_name = label_name;
}

public int get_label_name() {
return label_name;
}

public void set_neighbors(Set<Integer> neighbors) {
this.neighbors = neighbors;
}

public void append_neighbors(int id) {
this.neighbors.add(Integer.valueOf(id));
}

public Set<Integer> get_neighbors() {
return neighbors;
}
}

/*-****************************** main class *****************************/
/* Read input file, write output file, Community Detection Algorithm     */
/*-***********************************************************************/

public class LP {
Vector<NODE> node_list;
Vector<Integer> ordered_nodes;

public LP(){ //default constructor
}

/*-************************** Read input file ***************************-*/
/*Format : "node_id node_id" and input file has edge list of the graph.   */
/*-**********************************************************************-*/

public void readinput(int total_nodes, String input_file) throws IOException {
FileReader input = new FileReader(input_file);
BufferedReader br = new BufferedReader(input);
node_list = new Vector<NODE> (total_nodes);
ordered_nodes = new Vector<Integer> (total_nodes);

for(int i = 0; i < total_nodes; i++) {
   node_list.add(new NODE(i,i));                       //adding all the nodes to the node list
   ordered_nodes.add(i);                               //Preserving order of nodes
}
System.out.println(total_nodes + " nodes added.");
String input_lines = br.readLine();
while(input_lines != null) {
    String[] split = input_lines.split("--"); 		//spliting each lines by spaces
    int v1 = Integer.valueOf(split[0]);      		//first half be node v1
    int v2 = Integer.valueOf(split[1]);      		//second half be node v2
    node_list.get(v1).append_neighbors(v2);  		//add v2 in the neighborlist of v1
    node_list.get(v2).append_neighbors(v1);  		//add v1 in the neighborlist of v2
    input_lines = br.readLine();             		//next line
}   
/*-**************************Neighbor list complete ***********************/
br.close();
}

/*-************************** Write output file ***************************-*/
/*                       Format : "node_id community label"                 */
/*-************************************************************************-*/

public void final_communities(String file) throws IOException {
Map<Integer,Integer> assign_label = new HashMap<Integer,Integer>();
int label_count = 0;
for(int i = 0; i < node_list.size(); i++) {
   int label = node_list.get(i).get_label_name();
   Integer r = assign_label.get(Integer.valueOf(label));
   if(r == null) {
     label_count++;
     assign_label.put(Integer.valueOf(label), Integer.valueOf(label_count));
}
}
System.out.println("communities = "+label_count);
/* label_count communities found */
FileOutputStream fso = new FileOutputStream(file);
OutputStreamWriter fileWriter = new OutputStreamWriter(fso,Charset.forName("UTF-8"));
NODE node;
for(int i = 0; i < node_list.size(); i++) {
   node = node_list.get(i);
   fileWriter.write(node.get_id()+"--"+assign_label.get(Integer.valueOf(node.get_label_name())).intValue() +"\n");
}
System.out.println("DONE");
fileWriter.close();
fso.close();
}


/*-************************** Community Detection ***********************-*/
/*Multi-Threading can also be adapted to multi-processor architecture     */
/*-**********************************************************************-*/

public void communityDetection(int total_threads) throws IOException, ExecutionException, InterruptedException {
ExecutorService threadPool = Executors.newFixedThreadPool(total_threads);
Vector<findDominantLabel> dominantLabel_Calc = new Vector<findDominantLabel>(total_threads);
for(int i = 0; i < total_threads; i++) {
   dominantLabel_Calc.add(new findDominantLabel(node_list));
}
//int iteration = 0;
int label_change = 100; //number of nodes change labels (unstable configuration)
while(label_change > 0) {
   label_change = 0;
   Collections.shuffle(ordered_nodes);
   //PARALLELISM
   for(int i = 0; i < node_list.size(); i += total_threads) {    //for all nodes
      for(int j = 0; j < total_threads; j++) {                   //blocks of total threads number of nodes run paralley together
         if((i+j) < node_list.size())                            // if there are enough nodes(= number of threads) to run parallely
           /*pull each of the j threads from vector (dominantLabel_Calc) and link
             each node from the ordered list to a thread (one to one mapping) */  
            dominantLabel_Calc.get(j).link_node_to_process(ordered_nodes.get(i+j).intValue()); 
          else
            dominantLabel_Calc.get(j).link_node_to_process(-1);
}   
       List<Future<Boolean>> result = threadPool.invokeAll(dominantLabel_Calc);
       for(int k = 0; k < result.size(); k++) {
          Boolean b = result.get(k).get();
          if(b != null && b.booleanValue() == true) {
             label_change++;
             if(label_change == 1)
             // System.out.print("once more");
               break;
}            
}
}
}
System.out.println("Communities found");
threadPool.shutdown();
}

/*-************************** Main***************************************-*/
/*Change number of threads here, (also other parameters)                  */
/*-**********************************************************************-*/


public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
 LP algo = new LP();
 int total_nodes = 37000; 		                // Total nodes in the input graph
 int total_threads = 12;                                // Total threads to use
 
 algo.readinput(total_nodes, "enron_community.txt");
 long startTime = System.currentTimeMillis();
 algo.communityDetection(total_threads); 
 long endTime   = System.currentTimeMillis();
 long totalTime = endTime - startTime;
 
 System.out.println("Time_Taken" + totalTime/1000.0);
 algo.final_communities("Pooya.txt");
}
}
