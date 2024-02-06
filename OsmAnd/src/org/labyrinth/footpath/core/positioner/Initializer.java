package org.labyrinth.footpath.core.positioner;

import static org.labyrinth.common.MeasureUtils.toMetres;

import org.labyrinth.coordinate.Angle;
import org.labyrinth.footpath.graph.Edge;

import java.util.List;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

class Initializer {

    public static double[][] getPenaltyByDetectedStepIndexAndMapStepIndex(final int numberOfMapSteps) {
        final int numberOfDetectedStepsToConsider = 2;
        final double[][] penaltyByDetectedStepIndexAndMapStepIndex = new double[numberOfDetectedStepsToConsider][numberOfMapSteps + 1];
        for (int detectedStepIndex = 0; detectedStepIndex < penaltyByDetectedStepIndexAndMapStepIndex.length; detectedStepIndex++) {
            for (int mapStepIndex = 0; mapStepIndex < penaltyByDetectedStepIndexAndMapStepIndex[0].length; mapStepIndex++) {
                if (detectedStepIndex == 0 && mapStepIndex == 0) {
                    penaltyByDetectedStepIndexAndMapStepIndex[detectedStepIndex][mapStepIndex] = 0;
                } else if (detectedStepIndex == 0 || mapStepIndex == 0) {
                    penaltyByDetectedStepIndexAndMapStepIndex[detectedStepIndex][mapStepIndex] = Double.POSITIVE_INFINITY;
                }
            }
        }
        return penaltyByDetectedStepIndexAndMapStepIndex;
    }

    public static Angle[] getMapStepDirectionByStepIndex(final List<Edge> edges, final Quantity<Length> stepSize) {
        final AccumulatedTrackLengthAnglePair[] accumulatedTrackLengthAnglePairByEdgeIndex = getAccumulatedTrackLengthAnglePairs(edges);
        final double totalTrackLength = accumulatedTrackLengthAnglePairByEdgeIndex[edges.size() - 1].accumulatedTrackLength;
        final Angle[] mapStepDirectionByStepIndex = new Angle[getNumberOfSteps(totalTrackLength, stepSize)];
        for (int stepIndex = 0; stepIndex < mapStepDirectionByStepIndex.length; stepIndex++) {
            final int edgeIndex4TrackLength = getEdgeIndex4TrackLength(accumulatedTrackLengthAnglePairByEdgeIndex, getTrackLengthByStartOfStepIndex(stepIndex, stepSize));
            mapStepDirectionByStepIndex[stepIndex] = accumulatedTrackLengthAnglePairByEdgeIndex[edgeIndex4TrackLength].angle;
        }
        return mapStepDirectionByStepIndex;
    }

    private static AccumulatedTrackLengthAnglePair[] getAccumulatedTrackLengthAnglePairs(final List<Edge> edges) {
        final AccumulatedTrackLengthAnglePair[] accumulatedTrackLengthAnglePairByEdgeIndex =
                new AccumulatedTrackLengthAnglePair[edges.size()];
        double accumulatedTrackLength = 0;
        for (int edgeIndex = 0; edgeIndex < edges.size(); edgeIndex++) {
            final Edge edge = edges.get(edgeIndex);
            accumulatedTrackLength += toMetres(edge.getLength());
            accumulatedTrackLengthAnglePairByEdgeIndex[edgeIndex] =
                    new AccumulatedTrackLengthAnglePair(accumulatedTrackLength, edge.getDirection());
        }
        return accumulatedTrackLengthAnglePairByEdgeIndex;
    }

    private static int getNumberOfSteps(final double lengthInMeters, final Quantity<Length> stepSize) {
        return (int) (lengthInMeters / toMetres(stepSize));
    }

    private static double getTrackLengthByStartOfStepIndex(final int stepIndex, final Quantity<Length> stepSize) {
        return toMetres(stepSize) * stepIndex;
    }

    private static int getEdgeIndex4TrackLength(final AccumulatedTrackLengthAnglePair[] accumulatedTrackLengthAnglePairByEdgeIndex,
                                                final double trackLength) {
        int edgeIndex = 0;
        while (accumulatedTrackLengthAnglePairByEdgeIndex[edgeIndex].accumulatedTrackLength <= trackLength) {
            edgeIndex++;
        }
        return edgeIndex;
    }
}
