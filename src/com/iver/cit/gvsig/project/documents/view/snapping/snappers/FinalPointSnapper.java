package com.iver.cit.gvsig.project.documents.view.snapping.snappers;

import java.awt.Graphics;
import java.awt.geom.Point2D;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.core.Handler;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.project.documents.view.snapping.AbstractSnapper;
import com.iver.cit.gvsig.project.documents.view.snapping.ISnapperVectorial;

public class FinalPointSnapper extends AbstractSnapper implements
	ISnapperVectorial {
    @Override
    public Point2D getSnapPoint(Point2D point, IGeometry geom,
	    double tolerance, Point2D lastPointEntered) {
	Point2D resul = null;
	Handler[] handlers = geom.getHandlers(IGeometry.SELECTHANDLER);
	// Point2D initPoint=handlers[0].getPoint();
	// Point2D endPoint=handlers[handlers.length-1].getPoint();
	// if (initPoint.equals(endPoint))
	// return resul;

	double minDist = tolerance;
	// double dist = initPoint.distance(point);
	// if (dist < minDist) {
	// resul = initPoint;
	// minDist = dist;
	// }
	//
	// dist = endPoint.distance(point);
	// if (dist < minDist) {
	// resul = endPoint;
	// }

	for (int j = 0; j < handlers.length; j++) {
	    Point2D handlerPoint = handlers[j].getPoint();
	    double dist = handlerPoint.distance(point);
	    if ((dist < minDist)) {
		resul = handlerPoint;
		minDist = dist;
	    }
	}

	return resul;
    }

    @Override
    public String getToolTipText() {
	return PluginServices.getText(this, "final_point");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iver.cit.gvsig.gui.cad.snapping.ISnapper#draw(java.awt.Graphics,
     * java.awt.geom.Point2D)
     */
    @Override
    public void draw(Graphics g, Point2D pPixels) {
	g.setColor(getColor());
	// g.drawRect((int) (pPixels.getX() - 6),
	// (int) (pPixels.getY() - 6), 12, 12);
	// g.drawRect((int) (pPixels.getX() - 3),
	// (int) (pPixels.getY() - 3), 6, 6);
	// g.setColor(Color.MAGENTA);
	// g.drawRect((int) (pPixels.getX() - 4),
	// (int) (pPixels.getY() - 4), 8, 8);
	int half = getSizePixels() / 2;
	g.drawRect((int) (pPixels.getX() - half),
		(int) (pPixels.getY() - half), getSizePixels(), getSizePixels());
    }
}
