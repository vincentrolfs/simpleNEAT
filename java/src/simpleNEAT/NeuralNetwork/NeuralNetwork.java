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
     * @param connectionsSorted Must be sorted by innovationNumber. No two connections may both come out of the same
     *                          node and go into the same node. Must contain at least two connections.
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
        assert !nodeIdInNetwork(newNode.getInnovationNumber()) : "Saw same node innovationNumber twice";

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
        validateConnection(newConnection);

        // Using iterators for better performance with LinkedList
        ListIterator<Connection> iterator = _connectionsSorted.listIterator(_connectionsSorted.size());

        while (iterator.hasPrevious()) {
            Connection connection = iterator.previous();

            if (connection.getInnovationNumber() < newConnection.getInnovationNumber()) {
                iterator.next();
                iterator.add(newConnection);
                iterator.previous();
                break;
            }
        }

        if (!iterator.hasPrevious()) {
            iterator.add(newConnection);
        }

        addConnectionToLookup(newConnection);
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

    private void initializeNodes(ArrayList<Node> nodes){
        for (Node node : nodes){
            addNode(node);
        }
    }

    private void initializeConnections(LinkedList<Connection> connectionsSorted){
        int previousInnovationNumber = Integer.MIN_VALUE;
        for (Connection connection : connectionsSorted) {
            assert connection.getInnovationNumber() > previousInnovationNumber : "Connections not sorted by innovation number";

            previousInnovationNumber = connection.getInnovationNumber();
            validateConnection(connection);
            addConnectionToLookup(connection);
        }

        _connectionsSorted = connectionsSorted;
    }

    private void validateConnection(Connection connection){
        assert nodeIdInNetwork(connection.getNodeOutOfId()) && nodeIdInNetwork(connection.getNodeIntoId())
                : "Connection does not fit the topology of the network";
        assert !hasConnectionBetween(connection.getNodeOutOfId(), connection.getNodeIntoId())
                : "There already exists a connection in the network that comes out of the same node and goes into the same node as the new connection";
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
