/* file : Boundary2D.java
 * 
 * Project : geometry
 *
 * ===========================================
 * 
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or (at
 * your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY, without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. if not, write to :
 * The Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 * 
 * Created on 25 dec. 2006
 *
 */

package math.geom2d.domain;

import math.geom2d.AffineTransform2D;
import math.geom2d.Point2D;

import java.util.Collection;

/**
 * A Boundary2D is the curve which defines the contour of a domain in the plane.
 * It is compound of one or several non-intersecting and oriented curves. Curves
 * composing the boundary implements the Contour2D interface.
 * 
 * @see Contour2D
 * @author dlegland
 */
public interface Boundary2D extends OrientedCurve2D {

    /**
     * Returns true if the point is 'inside' the domain bounded by the curve.
     *
     * @param pt a point in the plane
     * @return true if the point is on the left side of the curve.
     */
    boolean isInside(Point2D pt);

    /**
     * Overloads the declaration of continuousCurves to return a collection of
     * contours (instances of Contour2D).
     */
    Collection<? extends Contour2D> continuousCurves();

    /**
     * Returns the domain delimited by this boundary.
     *
     * @return the domain delimited by this boundary
     */
    Domain2D domain();

    /**
     * Forces the subclasses to return an instance of Boundary2D.
     */
    Boundary2D reverse();

    /**
     * Forces the subclasses to return an instance of Boundary2D.
     */
    Boundary2D transform(AffineTransform2D trans);

}
