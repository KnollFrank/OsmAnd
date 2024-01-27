package org.labyrinth.footpath.core.positioner;

import com.google.common.primitives.Doubles;
import org.labyrinth.coordinate.Angle;
import org.labyrinth.footpath.graph.Edge;

import javax.measure.Quantity;
import javax.measure.quantity.Length;
import java.util.List;
import java.util.stream.IntStream;

import static com.google.common.primitives.Doubles.min;
import static tec.units.ri.quantity.Quantities.getQuantity;
import static tec.units.ri.unit.Units.METRE;

// adapted from the paper "FootPath: Accurate Map-based Indoor Navigation Using Smartphones" https://www.comsys.rwth-aachen.de/fileadmin/papers/2011/2011-IPIN-bitsch-footpath.pdf
public class PositionerBestFit {

    private final Angle[] mapStepDirectionByStepIndex;
    private double[][] penaltyByDetectedStepIndexAndMapStepIndex;
    private final Quantity<Length> stepSize;

    private int detectedNumberOfSteps;
    private Angle previousDetectedStepDirection;

    public PositionerBestFit(final List<Edge> edges, final Quantity<Length> stepSize) {
        this.stepSize = stepSize;
        this.mapStepDirectionByStepIndex = Initializer.getMapStepDirectionByStepIndex(edges, stepSize);
        initialize();
    }

    public void stepInDirection(final Angle stepDirection) {
        detectedNumberOfSteps++;
        updatePenaltyByMapStepIndexAndDetectedStepIndex(stepDirection, detectedNumberOfSteps);
    }

    public Quantity<Length> getCurrentCoveredLength() {
        return detectedNumberOfSteps == 0 ?
                getQuantity(0.0, METRE) :
                stepSize.multiply(getOneBasedArgMinMapStepIndex(getDetectedStepIndex(detectedNumberOfSteps)));
    }

    public void resetSteps() {
        initialize();
    }

    private void initialize() {
        this.detectedNumberOfSteps = 0;
        this.previousDetectedStepDirection = Angle.ZERO;
        this.penaltyByDetectedStepIndexAndMapStepIndex = Initializer.getPenaltyByDetectedStepIndexAndMapStepIndex(getNumberOfMapSteps());
    }

    private int getNumberOfMapSteps() {
        return mapStepDirectionByStepIndex.length;
    }

    private void updatePenaltyByMapStepIndexAndDetectedStepIndex(final Angle actualDetectedStepDirection,
                                                                 final int detectedNumberOfSteps) {
        final int currentDetectedStepIndex = getDetectedStepIndex(detectedNumberOfSteps);
        final int previousDetectedStepIndex = getDetectedStepIndex(detectedNumberOfSteps - 1);
        for (int oneBasedMapStepIndex = 1; oneBasedMapStepIndex < penaltyByDetectedStepIndexAndMapStepIndex[0].length; oneBasedMapStepIndex++) {
            penaltyByDetectedStepIndexAndMapStepIndex[currentDetectedStepIndex][oneBasedMapStepIndex] =
                    min(
                            penaltyByDetectedStepIndexAndMapStepIndex[previousDetectedStepIndex][oneBasedMapStepIndex - 1] +
                                    getPenalty(
                                            getMapStepDirectionByOneBasedStepIndex(oneBasedMapStepIndex),
                                            actualDetectedStepDirection,
                                            true),
                            penaltyByDetectedStepIndexAndMapStepIndex[currentDetectedStepIndex][oneBasedMapStepIndex - 1] +
                                    getPenalty(
                                            getMapStepDirectionByOneBasedStepIndex(oneBasedMapStepIndex),
                                            previousDetectedStepDirection,
                                            false),
                            penaltyByDetectedStepIndexAndMapStepIndex[previousDetectedStepIndex][oneBasedMapStepIndex] +
                                    getPenalty(
                                            getMapStepDirectionByOneBasedStepIndex(oneBasedMapStepIndex - 1),
                                            actualDetectedStepDirection,
                                            false));
        }
        penaltyByDetectedStepIndexAndMapStepIndex[0][0] = Double.POSITIVE_INFINITY;
        previousDetectedStepDirection = actualDetectedStepDirection;
    }

    private int getDetectedStepIndex(final int detectedNumberOfSteps) {
        return detectedNumberOfSteps % 2;
    }

    private int getOneBasedArgMinMapStepIndex(final int detectedStepIndex) {
        return getOneBasedArgMinMapStepIndex(penaltyByDetectedStepIndexAndMapStepIndex[detectedStepIndex]);
    }

    private int getOneBasedArgMinMapStepIndex(final double[] penaltyByMapStepIndex) {
        return IntStream
                .range(1, penaltyByMapStepIndex.length)
                .boxed()
                .min((i, j) -> Doubles.compare(penaltyByMapStepIndex[i], penaltyByMapStepIndex[j]))
                .orElse(0);
    }

    private Angle getMapStepDirectionByOneBasedStepIndex(final int oneBasedStepIndex) {
        final int zeroBasedStepIndex = oneBasedStepIndex - 1;
        return zeroBasedStepIndex >= 0 ? mapStepDirectionByStepIndex[zeroBasedStepIndex] : Angle.ZERO;
    }

    private double getPenalty(final Angle x, final Angle y, final boolean diagonal) {
        return getPenalty(x, y) + (diagonal ? 0 : 1.5);
    }

    private double getPenalty(final Angle x, final Angle y) {
        final double angle0To180 = x.get0To180DegreesDifferenceTo(y).to(Angle.Unit.DEGREES);
        if (angle0To180 < 45) {
            return 0;
        } else if (angle0To180 < 90) {
            return 1;
        } else if (angle0To180 < 120) {
            return 2;
        } else {
            return 10;
        }
    }
}
