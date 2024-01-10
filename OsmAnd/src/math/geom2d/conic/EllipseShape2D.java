/**
 * 
 */
package math.geom2d.conic;

import math.geom2d.AffineTransform2D;
import math.geom2d.Point2D;
import math.geom2d.domain.SmoothContour2D;

import java.util.Collection;

/**
 * A common interface for Circle2D and Ellipse2D.
 * @author dlegland
 *
 */
public interface EllipseShape2D extends SmoothContour2D, Conic2D {

    // ===================================================================
    // methods specific to EllipseShape2D interface

	/**
     * Returns center of the ellipse shape.
     */
    Point2D center();

    /**
     * Returns true if this ellipse shape is similar to a circle, i.e. has
     * same length for both semi-axes.
     */
    boolean isCircle();

    /**
     * If an ellipse shape is direct, it is the boundary of a convex domain.
     * Otherwise, the complementary of the bounded domain is convex.
     */
    boolean isDirect();

	// ===================================================================
    // methods of Curve2D interface

    EllipseShape2D reverse();

    Collection<? extends EllipseShape2D> continuousCurves();

    // ===================================================================
    // methods of Shape2D interface

    EllipseShape2D transform(AffineTransform2D trans);
    
}
