/**
 * 
 */

package math.geom2d.line;

import math.geom2d.AffineTransform2D;
import math.geom2d.Box2D;
import math.geom2d.circulinear.CirculinearElement2D;
import math.geom2d.curve.CurveSet2D;

/**
 * A continuous linear shape, like a straight line, a line segment or a ray.
 *
 * @author dlegland
 */
public interface LinearElement2D extends CirculinearElement2D, LinearShape2D {

    LinearElement2D transform(AffineTransform2D trans);

    LinearElement2D subCurve(double y0, double t1);

    CurveSet2D<? extends LinearElement2D> clip(Box2D box);
}
