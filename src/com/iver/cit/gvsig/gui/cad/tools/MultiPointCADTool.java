/* gvSIG. Sistema de Informaci?n Geogr?fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib??ez, 50
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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.InputEvent;
import java.util.ArrayList;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.cad.exception.CommandException;
import com.iver.cit.gvsig.gui.cad.tools.smc.MultiPointCADToolContext;
import com.iver.cit.gvsig.gui.cad.tools.smc.MultiPointCADToolContext.MultiPointCADToolState;

/**
 * DOCUMENT ME!
 * 
 * @author Vicente Caballero Navarro
 */
public class MultiPointCADTool extends InsertionCADTool {
    private MultiPointCADToolContext _fsm;
    protected ArrayList points = new ArrayList();

    /**
     * Crea un nuevo PointCADTool.
     */
    public MultiPointCADTool() {

    }

    /**
     * M?todo de incio, para poner el c?digo de todo lo que se requiera de una
     * carga previa a la utilizaci?n de la herramienta.
     */
    @Override
    public void init() {
	if (_fsm == null) {
	    _fsm = new MultiPointCADToolContext(this);
	}
    }

    @Override
    public void clear() {
	points.clear();
	_fsm = new MultiPointCADToolContext(this);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param x
     *            DOCUMENT ME!
     * @param y
     *            DOCUMENT ME!
     * @param sel
     *            DOCUMENT ME!
     */
    @Override
    public void transition(double x, double y, InputEvent event) {
	_fsm.addPoint(x, y, event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap
     * .layers.FBitSet, double)
     */
    @Override
    public void transition(double d) {
	_fsm.addValue(d);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap
     * .layers.FBitSet, java.lang.String)
     */
    @Override
    public void transition(String s) throws CommandException {
	if (!super.changeCommand(s)) {
	    if (s.equals(PluginServices.getText(this, "removePoint"))) {
		_fsm.removePoint(null, points.size());
	    } else {
		_fsm.addOption(s);
	    }
	}
    }

    /**
     * Equivale al transition del prototipo pero sin pasarle como par? metro el
     * editableFeatureSource que ya estar? creado.
     * 
     * @param sel
     *            Bitset con las geometr?as que est?n seleccionadas.
     * @param x
     *            par?metro x del punto que se pase en esta transici?n.
     * @param y
     *            par?metro y del punto que se pase en esta transici?n.
     */
    @Override
    public void addPoint(double x, double y, InputEvent event) {
	MultiPointCADToolState actualState = (MultiPointCADToolState) _fsm
		.getPreviousState();
	String status = actualState.getName();

	points.add(new double[] { x, y });
	// addGeometry(ShapeFactory.createPoint2D(x, y));
    }

    public void removePoint(InputEvent event) {

	MultiPointCADToolState currentState = (MultiPointCADToolState) _fsm
		.getPreviousState();
	String status = currentState.getName();

	if (status.equals("MultiPoint.FirstPoint")) {
	    points.clear();
	} else {
	    points.remove(points.size() - 1);
	}
    }

    /**
     * M?todo para dibujar la lo necesario para el estado en el que nos
     * encontremos.
     * 
     * @param g
     *            Graphics sobre el que dibujar.
     * @param selectedGeometries
     *            BitSet con las geometr?as seleccionadas.
     * @param x
     *            par?metro x del punto que se pase para dibujar.
     * @param y
     *            par?metro x del punto que se pase para dibujar.
     */
    @Override
    public void drawOperation(Graphics g, double x, double y) {
	int num = points.size();
	double[] xs = new double[num];
	double[] ys = new double[num];
	for (int i = 0; i < num; i++) {
	    double[] p = (double[]) points.get(i);
	    xs[i] = p[0];
	    ys[i] = p[1];
	}
	ShapeFactory.createMultipoint2D(xs, ys).draw((Graphics2D) g,
		getCadToolAdapter().getMapControl().getViewPort(),
		DefaultCADTool.geometrySelectSymbol);
	ShapeFactory.createPoint2D(x, y).draw((Graphics2D) g,
		getCadToolAdapter().getMapControl().getViewPort(),
		DefaultCADTool.geometrySelectSymbol);
    }

    /**
     * Add a diferent option.
     * 
     * @param sel
     *            DOCUMENT ME!
     * @param s
     *            Diferent option.
     */
    @Override
    public void addOption(String s) {
	// TODO Auto-generated method stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iver.cit.gvsig.gui.cad.CADTool#addvalue(double)
     */
    @Override
    public void addValue(double d) {
	// TODO Auto-generated method stub
    }

    @Override
    public String getName() {
	return PluginServices.getText(this, "multipoint_");
    }

    @Override
    public String toString() {
	return "_multipoint";
    }

    @Override
    public boolean isApplicable(int shapeType) {
	switch (shapeType) {
	case FShape.MULTIPOINT:
	    return true;
	}
	return false;
    }

    public void endGeometry() {
	int num = points.size();
	double[] xs = new double[num];
	double[] ys = new double[num];
	for (int i = 0; i < num; i++) {
	    double[] p = (double[]) points.get(i);
	    xs[i] = p[0];
	    ys[i] = p[1];
	}
	addGeometry(ShapeFactory.createMultipoint2D(xs, ys));
	end();
    }

    @Override
    public void end() {
	points.clear();
	super.end();
    }

    @Override
    public void drawOperation(Graphics g, ArrayList pointList) {
	// TODO Auto-generated method stub

    }

    @Override
    public boolean isMultiTransition() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public void transition(InputEvent event) {
	_fsm.removePoint(event, points.size());
    }
}
