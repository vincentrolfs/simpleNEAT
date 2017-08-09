package simpleNEAT;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

public class NeuralNetwork {

    // Format for nodes: Input nodes, then output nodes, then hidden nodes. Require ArrayList for perfromance reasons
    private ArrayList<Node> _nodes;
    // Require LinkedList for performance reasons
    private LinkedList<Connection> _connectionsSorted;
    private int _amountInputNodes;
    private int _amountOutputNodes;
    private Double _fitness;

    /**
     * @param nodes Format: input nodes, then output nodes, then hidden nodes. Nodes must be distinct. Must satisfy {@code nodes.size() >= 2}
     * @param connectionsSorted Must be sorted by innovationNumber. Connections must be distinct. Must satisfy {@code connectionsSorted.size() >= 1}
     * @param amountInputNodes Must be at least 1.
     * @param amountOutputNodes Must be at least 1.
     */
    public NeuralNetwork(ArrayList<Node> nodes, LinkedList<Connection> connectionsSorted, int amountInputNodes, int amountOutputNodes) {
        assert nodes.size() >= 2 && connectionsSorted.size() >= 1 && amountInputNodes >= 1 && amountOutputNodes >= 1;

        _nodes = nodes;
        _connectionsSorted = connectionsSorted;
        _amountInputNodes = amountInputNodes;
        _amountOutputNodes = amountOutputNodes;
        _fitness = null;
    }

    public ArrayList<Node> getNodes() {
        return _nodes;
    }

    public LinkedList<Connection> getConnectionsSorted() {
        return _connectionsSorted;
    }

    /**
     * Adds newNode to the nodes of this network. Ensures {@code getNodes().get(getNodes.size() - 1) == newNode}
     * @param newNode Must have innovationNumber that is not already used in the nodes of this network
     */
    public void addNode(Node newNode) {
        _nodes.add(newNode);
    }

    /**
     * Adds newConnection to the connections of this network at the appropriate position. Ensures {@code getConnectionsSorted().contains(newConnection)}.
     * Ensures isConnectedInto() behaves correctly for affected nodes
     * @param newConnection There must not exist a connection in the network with the same endpoints and the same direction.
     *                      newConnection.getNodeOutOf() and .getNodeInt() must be innovation numbers of nodes present in the network.
     */
    public void addConnection(Connection newConnection) {
        // Using iterators for better performance with LinkedList
        ListIterator<Connection> iterator = _connectionsSorted.listIterator();
        boolean connectionInserted = false;

        while (iterator.hasNext()) {
            Connection connection = iterator.next();

            assert connection.getNodeOutOf() != newConnection.getNodeOutOf() || connection.getNodeInto() != newConnection.getNodeInto()
                    : "There must not exist a connection in the network with the same endpoints and the same direction";

            if (!connectionInserted && connection.getInnovationNumber() >= newConnection.getInnovationNumber()) {
                iterator.add(newConnection);
                connectionInserted = true;
            }
        }

        if (!connectionInserted) {
            _connectionsSorted.add(newConnection);
        }

        modifyNodesForNewConnection(newConnection);
    }

    private void modifyNodesForNewConnection(Connection newConnection) {
        boolean nodeOutOfFound = false;
        boolean nodeIntoFound = false;

        for (Node node : _nodes) {
            if (node.getInnovationNumber() == newConnection.getNodeOutOf()){
                node.markConnectedInto(newConnection.getNodeInto());
                nodeOutOfFound = true;
            }
            if (node.getInnovationNumber() == newConnection.getNodeInto()){
                nodeIntoFound = true;
            }

            if (nodeOutOfFound && nodeIntoFound){
                break;
            }
        }

        assert nodeOutOfFound && nodeIntoFound : "Newly added connection does not fit the topology of the network";
    }

}
