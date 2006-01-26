/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ib��ez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *      +34 963862235
 *   gvsig@gva.es
 *      www.gvsig.gva.es
 *
 *    or
 *
 *   IVER T.I. S.A
 *   Salamanca 50
 *   46005 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */
package com.iver.cit.gvsig.gui.cad.tools;

import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.gui.cad.CADTool;
import com.iver.cit.gvsig.gui.cad.tools.smc.RectangleCADToolContext;
import com.iver.cit.gvsig.gui.cad.tools.smc.RectangleCADToolContext.RectangleCADToolState;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class RectangleCADTool extends DefaultCADTool {
    private RectangleCADToolContext _fsm;
    private Point2D firstPoint;
    private Point2D lastPoint;

    /**
     * Crea un nuevo LineCADTool.
     */
    public RectangleCADTool() {
        _fsm = new RectangleCADToolContext(this);
    }

    /**
     * M�todo de incio, para poner el c�digo de todo lo que se requiera de una
     * carga previa a la utilizaci�n de la herramienta.
     */
    public void init() {
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#end()
     */
    public void end() {
        _fsm = new RectangleCADToolContext(this);
        firstPoint = null;
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet, double, double)
     */
    public void transition(FBitSet sel, double x, double y) {
        _fsm.addPoint(sel, x, y);
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet, double)
     */
    public void transition(FBitSet sel, double d) {
        //_fsm.addvalue(sel,d);
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet, java.lang.String)
     */
    public void transition(FBitSet sel, String s) {
        _fsm.addOption(sel, s);
    }

    /**
     * Equivale al transition del prototipo pero sin pasarle como par� metro el
     * editableFeatureSource que ya estar� creado.
     *
     * @param sel Bitset con las geometr�as que est�n seleccionadas.
     * @param x par�metro x del punto que se pase en esta transici�n.
     * @param y par�metro y del punto que se pase en esta transici�n.
     */
    public void addPoint(FBitSet sel, double x, double y) {
        RectangleCADToolState actualState = (RectangleCADToolState) _fsm.getPreviousState();

        String status = actualState.getName();

        if (status.equals("ExecuteMap.Initial")) {
            firstPoint = new Point2D.Double(x, y);
        } else if (status == "ExecuteMap.First") {
            lastPoint = new Point2D.Double(x, y);

            GeneralPathX elShape = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD,
                    2);
            elShape.moveTo(firstPoint.getX(), firstPoint.getY());
            elShape.lineTo(lastPoint.getX(), firstPoint.getY());
            elShape.lineTo(lastPoint.getX(), lastPoint.getY());
            elShape.lineTo(firstPoint.getX(), lastPoint.getY());
            elShape.lineTo(firstPoint.getX(), firstPoint.getY());
            addGeometry(ShapeFactory.createPolyline2D(elShape));
            firstPoint = (Point2D) lastPoint.clone();
        } else if (status == "ExecuteMap.Second") {
            lastPoint = new Point2D.Double(x, y);

            GeneralPathX elShape = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD,
                    2);
            elShape.moveTo(firstPoint.getX(), firstPoint.getY());
            elShape.lineTo(lastPoint.getX(), firstPoint.getY());

            if (((lastPoint.getY() <= firstPoint.getY()) &&
                    (lastPoint.getX() <= firstPoint.getX())) ||
                    ((lastPoint.getY() > firstPoint.getY()) &&
                    (lastPoint.getX() > firstPoint.getX()))) {
                elShape.lineTo(lastPoint.getX(),
                    firstPoint.getY() + (lastPoint.getX() - firstPoint.getX()));
                elShape.lineTo(firstPoint.getX(),
                    firstPoint.getY() + (lastPoint.getX() - firstPoint.getX()));
            } else {
                elShape.lineTo(lastPoint.getX(),
                    firstPoint.getY() - (lastPoint.getX() - firstPoint.getX()));
                elShape.lineTo(firstPoint.getX(),
                    firstPoint.getY() - (lastPoint.getX() - firstPoint.getX()));
            }

            elShape.lineTo(firstPoint.getX(), firstPoint.getY());
            addGeometry(ShapeFactory.createPolyline2D(elShape));
            firstPoint = (Point2D) lastPoint.clone();
        }
    }

    /**
     * M�todo para dibujar la lo necesario para el estado en el que nos
     * encontremos.
     *
     * @param g Graphics sobre el que dibujar.
     * @param selectedGeometries BitSet con las geometr�as seleccionadas.
     * @param x par�metro x del punto que se pase para dibujar.
     * @param y par�metro x del punto que se pase para dibujar.
     */
    public void drawOperation(Graphics g, FBitSet selectedGeometries, double x,
        double y) {
        RectangleCADToolState actualState = _fsm.getState();
        String status = actualState.getName();

        if (status == "ExecuteMap.First") {
            GeneralPathX elShape = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD,
                    4);
            elShape.moveTo(firstPoint.getX(), firstPoint.getY());
            elShape.lineTo(x, firstPoint.getY());
            elShape.lineTo(x, y);
            elShape.lineTo(firstPoint.getX(), y);
            elShape.lineTo(firstPoint.getX(), firstPoint.getY());
            ShapeFactory.createPolyline2D(elShape).draw((Graphics2D) g,
                getCadToolAdapter().getMapControl().getViewPort(),
                CADTool.modifySymbol);
        } else if (status == "ExecuteMap.Second") {
            GeneralPathX elShape = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD,
                    4);
            elShape.moveTo(firstPoint.getX(), firstPoint.getY());
            elShape.lineTo(x, firstPoint.getY());

            if (((y <= firstPoint.getY()) && (x <= firstPoint.getX())) ||
                    ((y > firstPoint.getY()) && (x > firstPoint.getX()))) {
                elShape.lineTo(x, firstPoint.getY() + (x - firstPoint.getX()));
                elShape.lineTo(firstPoint.getX(),
                    firstPoint.getY() + (x - firstPoint.getX()));
                elShape.lineTo(firstPoint.getX(), firstPoint.getY());
            } else {
                elShape.lineTo(x, firstPoint.getY() - (x - firstPoint.getX()));
                elShape.lineTo(firstPoint.getX(),
                    firstPoint.getY() - (x - firstPoint.getX()));
                elShape.lineTo(firstPoint.getX(), firstPoint.getY());
            }

            ShapeFactory.createPolyline2D(elShape).draw((Graphics2D) g,
                getCadToolAdapter().getMapControl().getViewPort(),
                CADTool.modifySymbol);
        }
    }

    /**
     * Add a diferent option.
     *
     * @param sel DOCUMENT ME!
     * @param s Diferent option.
     */
    public void addOption(FBitSet sel, String s) {
        RectangleCADToolState actualState = (RectangleCADToolState) _fsm.getPreviousState();
        String status = actualState.getName();

        if (status == "ExecuteMap.First") {
            if (s.equals("C") || s.equals("c")) {
                //Opci�n correcta
            }
        }
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#addvalue(double)
     */
    public void addValue(FBitSet sel, double d) {
    }
}