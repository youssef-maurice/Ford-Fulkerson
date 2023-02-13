import java.util.*;

public class FordFulkerson {

    public static ArrayList<Integer> recur_DFS(Integer source, Integer destination, WGraph graph, ArrayList<Integer> path){
        path.add(source);
        if(path.contains(destination)){
            return path;
        }
        for(Edge edge: graph.getEdges()){
            if(edge.nodes[0]==source && edge.flow< edge.weight){
                if(path.contains(edge.nodes[1])){
                    continue;
                }
                recur_DFS(edge.nodes[1], destination, graph, path);
            }
        }
        return path;
    }

    public static ArrayList<Integer> pathDFS(Integer source, Integer destination, WGraph graph){
        ArrayList<Integer> path = new ArrayList<Integer>();
        ArrayList<Integer> empty_path = new ArrayList<Integer>();
        ArrayList<Integer> final_path = new ArrayList<Integer>();
        ArrayList<Integer> final_path2 = new ArrayList<Integer>();
        ArrayList<Integer> first_path = recur_DFS( source, destination, graph, path);
        ArrayList<Integer> copy_first_path = new ArrayList<Integer>(first_path);
        if(!(first_path.contains(destination))){
            return empty_path;
        }
        for (int i = 0; i < copy_first_path.size()-1; i++) {
            Edge edge = graph.getEdge(copy_first_path.get(i), copy_first_path.get(i + 1));
            if(edge==null && copy_first_path.get(i)!=destination){
                int index = first_path.indexOf(copy_first_path.get(i));
                first_path.remove(index);
            }
        }
        for(int num: first_path){
            final_path.add(num);
            if(num==destination){
                break;
            }
        }
        for(int i = 0; i < final_path.size()-1; i++){
            Edge edge = graph.getEdge(final_path.get(i), final_path.get(i + 1));
            if(edge!=null){
                final_path2.add(final_path.get(i));
            }
        }
        if(!(final_path2.contains(destination))){
            final_path2.add(destination);
        }
        return final_path2;
    }

    public static WGraph residual_graph(WGraph graph){
        WGraph g = new WGraph();
        g.setSource(graph.getSource());
        g.setDestination(graph.getDestination());
        for(Edge edge: graph.getEdges()){
            if(edge.flow < edge.weight){
                Edge for_edge = new Edge(edge.nodes[0], edge.nodes[1], edge.weight - edge.flow);
                //for_edge.flow= edge.flow;
                g.addEdge(for_edge);

            }
            if(edge.flow>0){
                Edge back_edge = new Edge(edge.nodes[1], edge.nodes[0], edge.flow);
                //back_edge.flow= edge.flow;
                back_edge.direction=-1;
                g.addEdge(back_edge);
            }
        }
        return g;
    }

    public static WGraph augmentation(WGraph og_graph, WGraph res_graph, ArrayList<Integer> path){
        int min_cap= Integer.MAX_VALUE;
        for(int i= 0; i< path.size()-1; i++){
            Edge res_edge = res_graph.getEdge(path.get(i), path.get(i + 1));
            if(res_edge.weight-res_edge.flow<min_cap){
                min_cap= res_edge.weight-res_edge.flow;
            }
        }
        //CHANGE THE PART OF THE BACKEDGE, MAKE SURE IT CHANGES IN OG GRAPH
        for(int i= 0; i< path.size()-1; i++){
            Edge res_edge = res_graph.getEdge(path.get(i), path.get(i + 1));
            if(res_edge.direction==1){
                Edge og_edge = og_graph.getEdge(res_edge.nodes[0], res_edge.nodes[1]);
                og_edge.flow=og_edge.flow+min_cap;
            }
            if(res_edge.direction==-1){
                Edge og_edge = og_graph.getEdge(res_edge.nodes[1], res_edge.nodes[0]);
                og_edge.flow=og_edge.flow-min_cap;
            }
        }
        return og_graph;
    }

    public static ArrayList<Integer> get_bottleneck_val(WGraph res_graph, ArrayList<Integer> path, ArrayList<Integer> bottleneck_vals){
        int min_cap= Integer.MAX_VALUE;
        for(int i= 0; i< path.size()-1; i++){
            Edge res_edge = res_graph.getEdge(path.get(i), path.get(i + 1));
            if(res_edge.weight-res_edge.flow<min_cap){
                min_cap= res_edge.weight-res_edge.flow;
            }
        }
        bottleneck_vals.add(min_cap);
        return bottleneck_vals;
    }

    public static String fordfulkerson( WGraph graph){
        String answer="";
        int maxFlow = 0;
        /* TODO YOUR CODE STARTS HERE	*/
        ArrayList<Integer> bottleneck_vals= new ArrayList<Integer>();
        int source = graph.getSource();
        int dest = graph.getDestination();
        LinkedList<WGraph> all_graphs= new LinkedList<WGraph>();
        WGraph res_graph = residual_graph(graph);
        while (pathDFS(res_graph.getSource(), res_graph.getDestination(), res_graph).size()!=0){
            ArrayList<Integer> path = pathDFS(res_graph.getSource(), res_graph.getDestination(), res_graph);
            graph = augmentation(graph, res_graph, path);
            all_graphs.addLast(graph);
            bottleneck_vals= get_bottleneck_val(res_graph, path, bottleneck_vals);
            res_graph=residual_graph(graph);
        }
        for(int num: bottleneck_vals){
            maxFlow= maxFlow+ num;
        }
        if(maxFlow==0){
            for(Edge edge: graph.getEdges()){
                edge.weight= 0;
            }
            answer += maxFlow + "\n" + graph.toString();
            return answer;
        }
        WGraph last_graph = all_graphs.getLast();
        for(Edge edge: last_graph.getEdges()){
            edge.weight= edge.flow;
        }
        answer += maxFlow + "\n" + last_graph.toString();
        return answer;
    }
}

