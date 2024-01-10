/**
 * File: 	CirculinearBoundary2D.java
 * Project: javaGeom
 * 
 * Distributed under the LGPL License.
 *
 * Created: 11 mai 09
 */
package math.geom2d.circulinear;

import math.geom2d.Box2D;
import math.geom2d.curve.CurveSet2D;
import math.geom2d.domain.Boundary2D;
import math.geom2d.transform.CircleInversion2D;

import java.util.Collection;


/**
 * A Boundary which is composed of Circulinear elements.
 *
 * @author dlegland
 */
public interface CirculinearBoundary2D extends CirculinearCurve2D, Boundary2D {

	// ===================================================================
	// redefines declaration of some interfaces

	CirculinearDomain2D domain();

	CirculinearBoundary2D parallel(double d);

	Collection<? extends CirculinearContour2D> continuousCurves();

	CurveSet2D<? extends CirculinearContinuousCurve2D> clip(Box2D box);

	CirculinearBoundary2D transform(CircleInversion2D inv);

	CirculinearBoundary2D reverse();
}
