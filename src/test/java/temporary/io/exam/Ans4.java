package temporary.io.exam;

import java.io.*;
import java.util.Hashtable;

/**
 * Created by jeff on 4/11/19.
 */
public class Ans4 {



    public static void main (String[] args) throws java.lang.Exception
    {
        class Node {
            int value ;
            Node nextNode ;

            public Node(int val) {
                value = val;
            }

            public int getValue() {
                return value ;
            }

            public void setNextNode(Node nNode) {
                nextNode = nNode ;
            }

            public Node getNextNode() {
                return nextNode ;
            }
        }

        class NodeManager {
            Hashtable<Integer, Node> htNode = null;
            private Node head ;

            public NodeManager() {
                htNode = new Hashtable<>() ;
            }

            public void createNode(int data1, int data2) {
                if(head == null) {
                    Node node1 = new Node(data1) ;
                    Node node2 = new Node(data2) ;
                    node1.setNextNode(node2);
                    head = node1 ;

                    htNode.put(data1, node1);
                    htNode.put(data2, node2);
                } else {
                    Node node1 = this.getNode(data1);
                    node1.setNextNode(this.getNode(data2));
                }

                htNode.containsKey(data1);
            }

            private Node getNode(int data) {
                if(htNode.containsKey(data)) {
                    return htNode.get(data);
                }

                Node newNode = new Node(data);
                htNode.put(data, newNode) ;

                return newNode ;
            }

            public boolean hasCycle() {
                return this.hasCycle(head) ;
            }

            private boolean hasCycle(Node head) {
                Node tortoise = head;
                Node hare = head ;

                do{
                    if(hare == null || hare.getNextNode() == null) return false ;
                    hare = hare.getNextNode().getNextNode();
                    tortoise = tortoise.getNextNode();
                } while(hare != tortoise) ;
                return true ;
            }

        }

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input = br.readLine();
//        System.out.println(input);

        int cntInput = Integer.parseInt(input) ;
        NodeManager nm = new NodeManager();
        for(int i=0;i<cntInput;i++) {
            input = br.readLine();

            String[] tokens = input.split(" ");
            if(tokens.length == 2) {
                nm.createNode(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]));
            }
        }

        System.out.println(nm.hasCycle());
    }
}
