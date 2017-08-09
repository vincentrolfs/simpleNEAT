package simpleNEAT.NeuralNetwork;

import java.util.*;

class NeuralNetwork {

    // Format for nodes: Input nodes, then output nodes, then hidden nodes. Require ArrayList for perfromance reasons
    private ArrayList<Node> _nodes;
    // Require LinkedList for performance reasons
    private LinkedList<Connection> _connectionsSorted;
    private int _amountInputNodes;
    private int _amountOutputNodes;

    private Map<Integer, Set<Integer>> _connectedIntoLookup;
    private Double _fitness;

    /**
     * @param nodes Format: input nodes, then output nodes, then hidden nodes. Nodes must have distinct innovation numbers.
     *              Must contain at least two nodes.
     * @param connectionsSorted Connections must have distinct innovationNumbers.
     *                          No two connections may both come out of the same node and go into the same node.
     *                          Must contain at least two connections.
     * @param amountInputNodes Must be at least 1.
     * @param amountOutputNodes Must be at least 1.
     */
    NeuralNetwork(ArrayList<Node> nodes, LinkedList<Connection> connectionsSorted, int amountInputNodes, int amountOutputNodes) {
        assert nodes.size() >= 2 && connectionsSorted.size() >= 1 && amountInputNodes >= 1 && amountOutputNodes >= 1;

        _nodes = new ArrayList<>();
        _connectionsSorted = new LinkedList<>();
        _amountInputNodes = amountInputNodes;
        _amountOutputNodes = amountOutputNodes;

        _connectedIntoLookup = new HashMap<>();
        _fitness = null;

        initializeNodes(nodes);
        initializeConnections(connectionsSorted);
    }

    ArrayList<Node> getNodes() {
        return _nodes;
    }

    LinkedList<Connection> getConnectionsSorted() {
        return _connectionsSorted;
    }

    /**
     * Adds newNode to the nodes of this network. Ensures {@code getNodes().get(getNodes.size() - 1) == newNode}
     * @param newNode Must have innovationNumber that is not already used in the nodes of this network.
     */
    void addNode(Node newNode) {
        assert !nodeIdInNetwork(newNode.getInnovationNumber());

        _connectedIntoLookup.put(newNode.getInnovationNumber(), new HashSet<>());
        _nodes.add(newNode);
    }

    /**
     * Adds newConnection to the connections of this network at the appropriate position.
     * Ensures {@code hasConnectionBetween(newConnection.getNodeOutOfId(), newConnection.getNodeIntoId())}.
     * @param newConnection There must not exist a connection in the network that comes out of the same node
     *                      and goes into the same node as newConnection.
     *                      Must fit the topology of the network, i.e. newConnection.getNodeOutOfId() and
     *                      newConnection.getNodeIntoId() must be innovation numbers of nodes present in the network.
     */
    void addConnection(Connection newConnection) {
        assert nodeIdInNetwork(newConnection.getNodeOutOfId()) && nodeIdInNetwork(newConnection.getNodeIntoId())
                : "Connection does not fit the topology of the network";
        assert !hasConnectionBetween(newConnection.getNodeOutOfId(), newConnection.getNodeIntoId())
                : "There already exists a connection in the network that comes out of the same node and goes into the same node as the new connection";

        // Using iterators for better performance with LinkedList
        ListIterator<Connection> iterator = _connectionsSorted.listIterator();
        boolean connectionInserted = false;

        // Pre-check if newConnection has the highest innovationNumber ever seen for performance reasons
        if (!hasHighestInnovationNumber(newConnection))
        {
            while (iterator.hasNext()) {
                Connection connection = iterator.next();

                if (connection.getInnovationNumber() >= newConnection.getInnovationNumber()) {
                    iterator.previous();
                    iterator.add(newConnection);
                    iterator.next();
                    connectionInserted = true;
                    break;
                }
            }
        }

        if (!connectionInserted) {
            _connectionsSorted.add(newConnection);
        }

        addConnectionToLookup(newConnection);
    }

    private boolean hasHighestInnovationNumber(Connection newConnection) {
        int amountOfConnections = _connectionsSorted.size();
        return amountOfConnections == 0 || _connectionsSorted.get(amountOfConnections - 1).getInnovationNumber() < newConnection.getInnovationNumber();
    }

    /**
     * @param nodeOutOfId The innovation number of the node in the network that the connection in question goes out of.
     * @param nodeIntoId The innovation number of a node in the network that the connection in question goes into.
     */
    boolean hasConnectionBetween(Integer nodeOutOfId, Integer nodeIntoId){
        return     nodeIdInNetwork(nodeOutOfId)
                && nodeIdInNetwork(nodeIntoId)
                && _connectedIntoLookup.get(nodeOutOfId).contains(nodeIntoId);
    }

    /**
     * @param nodeId The innovation number of a node.
     */
    boolean nodeIdInNetwork(Integer nodeId){
        return _connectedIntoLookup.containsKey(nodeId);
    }

    private void initializeNodes(List<Node> nodes){
        for (Node node : nodes){
            addNode(node);
        }
    }

    private void initializeConnections(List<Connection> connections){
        for (Connection connection : connections){
            addConnection(connection);
        }
    }

    /**
     * @param connection Must fit the topology of the network, i.e. newConnection.getNodeOutOfId() and
     *                   newConnection.getNodeIntoId() must be innovation numbers of nodes present in the network.
     */
    private void addConnectionToLookup(Connection connection){
        Integer nodeOutOfId = connection.getNodeOutOfId();
        Integer nodeIntoId = connection.getNodeIntoId();

        _connectedIntoLookup.get(nodeOutOfId).add(nodeIntoId);
    }

}
