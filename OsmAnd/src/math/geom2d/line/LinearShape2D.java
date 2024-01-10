/**
 * 
 */

package math.geom2d.line;

import math.geom2d.AffineTransform2D;
import math.geom2d.Point2D;
import math.geom2d.Vector2D;
import math.geom2d.circulinear.CirculinearCurve2D;

/**
 * A curve that can be inscribed in a straight line, like a ray, a straight
 * line, or a line segment. Classes implementing this interface can be
 * discontinuous, contrary to the interface LinearElement2D.
 * 
 * @author dlegland
 */
public interface LinearShape2D extends CirculinearCurve2D {

	/**
     * Returns the straight line that contains this linear shape.
     * The direction is the same, and if possible the direction vector
     * should be the same.
     *
     * @return the straight line that contains this linear shape
     */
    StraightLine2D supportingLine();

    /**
     * Returns the angle with axis (O,i), counted counter-clockwise. Result
     * is given between 0 and 2*pi.
     */
    double horizontalAngle();

    /**
     * Returns a point in the linear shape.
     *
     * @return a point in the linear shape.
     */
    Point2D origin();

    /**
     * Return one direction vector of the linear shape.
     *
     * @return a direction vector
     */
    Vector2D direction();

    /**
     * Returns the unique intersection with a linear shape. If the intersection
     * doesn't exist (parallel lines), returns null.
     */
    Point2D intersection(LinearShape2D line);

    /**
     * Transforms this linear shape.
     */
    LinearShape2D transform(AffineTransform2D trans);
}
