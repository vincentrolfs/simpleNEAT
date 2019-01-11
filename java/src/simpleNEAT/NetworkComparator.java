package simpleNEAT;

import simpleNEAT.InnovationNumberObjectPairIterator.InnovationNumberObjectPairIterator;
import simpleNEAT.InnovationNumberObjectPairIterator.Pair;
import simpleNEAT.NeuralNetwork.Connection;
import simpleNEAT.NeuralNetwork.NeuralNetwork;
import simpleNEAT.NeuralNetwork.Node;
import java.util.List;
import java.util.Set;

public class NetworkComparator {

    private final double _nodeActivationSteepnessDifferenceFactor;
    private final double _nodeBiasDifferenceFactor;
    private final double _connectionWeightDifferenceFactor;

    private final double _disjointNodesFactor;
    private final double _disjointConnectionsFactor;

    public NetworkComparator(double nodeActivationSteepnessDifferenceFactor, double nodeBiasDifferenceFactor, double connectionWeightDifferenceFactor, double disjointNodesFactor, double disjointConnectionsFactor) {
        _nodeActivationSteepnessDifferenceFactor = nodeActivationSteepnessDifferenceFactor;
        _nodeBiasDifferenceFactor = nodeBiasDifferenceFactor;
        _connectionWeightDifferenceFactor = connectionWeightDifferenceFactor;
        _disjointNodesFactor = disjointNodesFactor;
        _disjointConnectionsFactor = disjointConnectionsFactor;
    }

    public double calculateDistance(NeuralNetwork network0, NeuralNetwork network1){
        double nodeDistance = calculateNodeDistance(network0.getNodesSorted(), network1.getNodesSorted());
        double connectionDistance = calculateConnectionDistance(network0.getConnectionsSorted(), network1.getConnectionsSorted());

        return nodeDistance + connectionDistance;
    }

    private double calculateNodeDistance(List<Node> nodes0, List<Node> nodes1) {
        InnovationNumberObjectPairIterator<Node> iterator = new InnovationNumberObjectPairIterator<>(nodes0, nodes1);
        double nodeDistance = 0;

        while (iterator.hasNext()){
            Pair<Node> pair = iterator.next();

            if (pair.hasNullElement()){
                nodeDistance += _disjointNodesFactor;
            } else {
                nodeDistance += compareNodes(pair.get(0), pair.get(1));
            }
        }

        return nodeDistance;
    }

    private double compareNodes(Node node0, Node node1) {
        double distance = 0;

        distance += _nodeBiasDifferenceFactor * Math.abs(node0.getBias() - node1.getBias());
        distance += _nodeActivationSteepnessDifferenceFactor * Math.abs(node0.getActivationSteepness() - node1.getActivationSteepness());

        return distance;
    }

    private double calculateConnectionDistance(List<Connection> connections0, List<Connection> connections1) {
        InnovationNumberObjectPairIterator<Connection> iterator = new InnovationNumberObjectPairIterator<>(connections0, connections1);
        double connectionDistance = 0;

        while (iterator.hasNext()){
            Pair<Connection> pair = iterator.next();

            if (pair.hasNullElement() || pair.get(0).isDisabled() != pair.get(1).isDisabled()){
                connectionDistance += _disjointConnectionsFactor;
            } else {
                connectionDistance += compareConnections(pair.get(0), pair.get(1));
            }
        }

        return connectionDistance;
    }

    private double compareConnections(Connection connection0, Connection connection1) {
        return _connectionWeightDifferenceFactor * Math.abs(connection0.getWeight() - connection1.getWeight());
    }
}
