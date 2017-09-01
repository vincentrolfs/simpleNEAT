package simpleNEAT.NeuralNetwork;

import java.util.*;

public class NeuralNetwork {

    private LinkedList<Node> _nodesSorted;
    private LinkedList<Connection> _connectionsSorted;
    private int _amountInputNodes;
    private int _amountOutputNodes;

    private Map<Integer, Set<Integer>> _connectedIntoLookup;
    private Double _fitness;

    /**
     * @param nodesSorted       Must be sorted by innovation number. Must be in order input nodes, output ndoes, hidden nodes.
     *                          Nodes must have distinct innovation numbers.
     *                          Must contain at least two nodesSorted.
     * @param connectionsSorted Must be sorted by innovationNumber. No two connections may both come out of the same
     *                          node and go into the same node. (This implies that no two connection may have the same
     *                          innovation numbers).
     * @param amountInputNodes  Must be at least 1.
     * @param amountOutputNodes Must be at least 1.
     */
    public NeuralNetwork(LinkedList<Node> nodesSorted, LinkedList<Connection> connectionsSorted, int amountInputNodes, int amountOutputNodes) {
        assert nodesSorted.size() >= 2 && amountInputNodes >= 1 && amountOutputNodes >= 1;

        _connectedIntoLookup = new HashMap<>();

        _nodesSorted = nodesSorted;
        processInitialNodes();

        _connectionsSorted = connectionsSorted;
        processInitialConnections();

        _amountInputNodes = amountInputNodes;
        _amountOutputNodes = amountOutputNodes;

        _fitness = null;
    }

    /**
     * Returns all nodes in the format: Input nodes, then output nodes, then hidden nodes.
     */
    public LinkedList<Node> getNodesSorted() {
        return _nodesSorted;
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
     * Adds newNode to the nodes of this network at the appropriate position.
     * Ensures {@code isNodeIdInNetwork(newNode.getInnovationNumber())}.
     * @param newNode The new node innovation number must not be already present in the network.
     */
    public void addNode(Node newNode) {
        assert !isNodeIdInNetwork(newNode.getInnovationNumber()) :
                "The new node innovation number is already present in the network";

        addNodeToLookup(newNode);
        addInnovationNumberObjectToSortedList(newNode, _nodesSorted);
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
        processNewConnection(newConnection);

        addInnovationNumberObjectToSortedList(newConnection, _connectionsSorted);
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

    private void processInitialNodes() {
        int previousInnovationNumber = Integer.MIN_VALUE;

        for (Node node : _nodesSorted) {
            assert node.getInnovationNumber() > previousInnovationNumber : "Nodes not sorted by innovation number";
            previousInnovationNumber = node.getInnovationNumber();

            addNodeToLookup(node);
        }
    }

    private void addNodeToLookup(Node newNode){
        _connectedIntoLookup.put(newNode.getInnovationNumber(), new HashSet<>());
    }

    private void processInitialConnections() {
        int previousInnovationNumber = Integer.MIN_VALUE;

        for (Connection connection : _connectionsSorted) {
            assert connection.getInnovationNumber() > previousInnovationNumber : "Connections not sorted by innovation number";
            previousInnovationNumber = connection.getInnovationNumber();

            processNewConnection(connection);
        }
    }

    private void processNewConnection(Connection connection){
        validateConnection(connection);
        addConnectionToLookup(connection);
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

    private <T extends InnovationNumberObject> void addInnovationNumberObjectToSortedList(T objectToInsert, List<T> list) {
        ListIterator<T> iterator = list.listIterator(list.size());

        while (iterator.hasPrevious()) {
            InnovationNumberObject someObject = iterator.previous();

            if (someObject.getInnovationNumber() <= objectToInsert.getInnovationNumber()) {
                assert someObject.getInnovationNumber() != objectToInsert.getInnovationNumber()
                        : "Innovation number of new object already present in network";

                iterator.next();
                iterator.add(objectToInsert);
                iterator.previous();
                break;
            }
        }

        if (!iterator.hasPrevious()) {
            iterator.add(objectToInsert);
        }
    }

}
