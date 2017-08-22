package simpleNEAT.NeuralNetwork;

import java.util.*;

public class NeuralNetwork {

    // Format for nodes: Input nodes, then output nodes, then hidden nodes. Require ArrayList for performance reasons
    private ArrayList<Node> _nodes;
    // Require LinkedList for performance reasons
    private LinkedList<Connection> _connectionsSorted;
    private int _amountInputNodes;
    private int _amountOutputNodes;

    private Map<Integer, Set<Integer>> _connectedIntoLookup;
    private Double _fitness;

    /**
     * @param nodes             Format: input nodes, then output nodes, then hidden nodes. Nodes must have distinct innovation numbers.
     *                          Must contain at least two nodes.
     * @param connectionsSorted Must be sorted by innovationNumber. No two connections may both come out of the same
     *                          node and go into the same node. (This implies that no two connection may have the same
     *                          innovation numbers).
     * @param amountInputNodes  Must be at least 1.
     * @param amountOutputNodes Must be at least 1.
     */
    public NeuralNetwork(ArrayList<Node> nodes, LinkedList<Connection> connectionsSorted, int amountInputNodes, int amountOutputNodes) {
        assert nodes.size() >= 2 && amountInputNodes >= 1 && amountOutputNodes >= 1;

        initializeNodes(nodes);
        initializeConnections(connectionsSorted);

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

    public int getAmountInputNodes() {
        return _amountInputNodes;
    }

    public int getAmountOutputNodes() {
        return _amountOutputNodes;
    }

    public Double getFitness() {
        return _fitness;
    }

    public void setFitness(Double fitness) {
        _fitness = fitness;
    }

    /**
     * Adds newNode to the nodes of this network. Ensures {@code getNodes().get(getNodes.size() - 1) == newNode}
     * @param newNode Must have innovationNumber that is not already used in the nodes of this network.
     */
    public void addNode(Node newNode) {
        assert !isNodeIdInNetwork(newNode.getInnovationNumber()) : "Saw same node innovationNumber twice";

        _connectedIntoLookup.put(newNode.getInnovationNumber(), new HashSet<>());
        _nodes.add(newNode);
    }

    /**
     * Adds newConnection to the connections of this network at the appropriate position.
     * Ensures {@code hasConnectionBetween(newConnection.getNodeOutOfId(), newConnection.getNodeIntoId())}.
     * @param newConnection There must not exist a connection in the network that comes out of the same node
     *                      and goes into the same node as newConnection. (This implies that no connection with the same
     *                      innovation number as newConnection may exist in the network).
     *                      Must fit the topology of the network, i.e. newConnection.getNodeOutOfId() and
     *                      newConnection.getNodeIntoId() must be innovation numbers of nodes present in the network.
     */
    public void addConnection(Connection newConnection) {
        validateConnection(newConnection);

        // Using iterators for better performance with LinkedList
        ListIterator<Connection> iterator = _connectionsSorted.listIterator(_connectionsSorted.size());

        while (iterator.hasPrevious()) {
            Connection connection = iterator.previous();

            if (connection.getInnovationNumber() <= newConnection.getInnovationNumber()) {
                assert connection.getInnovationNumber() != newConnection.getInnovationNumber()
                        : "Innovation number of new connection already present in network";

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
     * @param nodeIntoId  The innovation number of a node in the network that the connection in question goes into.
     */
    public boolean hasConnectionBetween(Integer nodeOutOfId, Integer nodeIntoId) {
        return isNodeIdInNetwork(nodeOutOfId)
                && isNodeIdInNetwork(nodeIntoId)
                && _connectedIntoLookup.get(nodeOutOfId).contains(nodeIntoId);
    }

    /**
     * @param nodeId The innovation number of a node.
     */
    public boolean isNodeIdInNetwork(Integer nodeId) {
        return _connectedIntoLookup.containsKey(nodeId);
    }

    private void initializeNodes(ArrayList<Node> nodes) {
        _nodes = new ArrayList<>();
        _connectedIntoLookup = new HashMap<>();

        for (Node node : nodes) {
            addNode(node);
        }
    }

    private void initializeConnections(LinkedList<Connection> connectionsSorted) {
        int previousInnovationNumber = Integer.MIN_VALUE;
        for (Connection connection : connectionsSorted) {
            assert connection.getInnovationNumber() > previousInnovationNumber : "Connections not sorted by innovation number";

            previousInnovationNumber = connection.getInnovationNumber();
            validateConnection(connection);
            addConnectionToLookup(connection);
        }

        _connectionsSorted = connectionsSorted;
    }

    private void validateConnection(Connection connection) {
        assert isNodeIdInNetwork(connection.getNodeOutOfId()) && isNodeIdInNetwork(connection.getNodeIntoId())
                : "Connection does not fit the topology of the network";
        assert !hasConnectionBetween(connection.getNodeOutOfId(), connection.getNodeIntoId())
                : "There already exists a connection in the network that comes out of the same node and goes into the same node as the new connection";
    }

    /**
     * @param connection Must fit the topology of the network, i.e. newConnection.getNodeOutOfId() and
     *                   newConnection.getNodeIntoId() must be innovation numbers of nodes present in the network.
     */
    private void addConnectionToLookup(Connection connection) {
        Integer nodeOutOfId = connection.getNodeOutOfId();
        Integer nodeIntoId = connection.getNodeIntoId();

        _connectedIntoLookup.get(nodeOutOfId).add(nodeIntoId);
    }

}
