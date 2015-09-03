 /*-******************************************
 *  Rahil Sharma                             *
 *  Multi-thread Label Propogation Algorithm *
 *  (findDominantLabel class)                *
 *  Modified version                         *
 *  Scalable to Multicore Architcture        *
 *  Date : 18th October, 2014 (version 1.0)  * 
 *********************************************/


/*-************************** Find Dominant Label ***********************-*/
/* If we submit a 'Callable' object to an 'Executor' object of type       */
/* java.util.concurrent.Future is returned (This can be used to check     */
/* the status of 'Callableand retrive results from callable')             */
/* EACH LINE OF CODE IN THE ALGORITHM COMMENTED FOR BETTER UNDERSTANDING  */
/* THIS CLASS METHODS ARE RUN PARALLELLY IN EACH THREADS                  */
/*-**********************************************************************-*/

import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.Random;

public class findDominantLabel implements Callable<Boolean> {
private Vector<Integer> dominant_label;
private Vector<Integer> label_count;
private int node_id;
private Vector<NODE> node_list;
private Random randGen;

public findDominantLabel(Vector<NODE> node_list) {
dominant_label = new Vector<Integer>();
label_count = new Vector<Integer>(node_list.size());                 //vector of size nodelist
for(int i = 0; i < node_list.size(); i++) {                          // to prevent Index out of bounds exception
   label_count.add(Integer.valueOf(0));
}
randGen = new Random();                                              // to prevent Null point exception
this.node_list = node_list;

}

public void link_node_to_process(int node_id) {
this.node_id = node_id;
}

@Override
public Boolean call() {  
if(node_id == -1)                                                    // if node Id not present (no node left)
  return Boolean.FALSE;
boolean run = false;
Collections.fill(label_count, Integer.valueOf(0));                   // Initializing label_count with same label as the node 
dominant_label.clear();
NODE curr_node = node_list.get(node_id);
int maximum_count = 0;
for (Integer neighborId : curr_node.get_neighbors()) {               // for all neighbors
    int neighbor_label = node_list.get(neighborId).get_label_name(); // get label of the neighbor from node list
    if(neighbor_label == 0)
       continue;                                                    // no label assignment yet, just the initialization done so far
    int neighbor_label_count = label_count.get(neighbor_label) + 1; //total neighbors wih same label (neighbor_label)+ itself (1)
    label_count.set(neighbor_label, neighbor_label_count);          //replace the neighbor_label(index_num) with its total count in the neighborhood
    if(maximum_count < neighbor_label_count) {  		              //some other neighbor label has max count                   
        maximum_count = neighbor_label_count;     	  	              //replace max count by that neighbor label count
        dominant_label.clear();                 		              //clear the previous dominant label 
        dominant_label.add(neighbor_label);      		              //make neighbor_label as the new dominant label (since it has max count)
}
    else if(maximum_count == neighbor_label_count) {
        dominant_label.add(neighbor_label);   
}
} 
if(dominant_label.size() > 0) {                                        // more than 1 dominant label among the neighbors
  //int rand = randGen.nextInt(dominant_label.size());
  //rand = dominant_label.get(rand);
   Integer maximum_label = Collections.max(dominant_label);            // choose the maximum label       
   if(label_count.get(curr_node.get_label_name()) != maximum_count) {  //is the current label dominant label
      run = true;                                                      //current label not dominant
}
curr_node.set_label_name(maximum_label);    
}
return Boolean.valueOf(run);
}
}
