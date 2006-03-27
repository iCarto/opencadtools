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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.InputEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.IOException;
import java.util.ArrayList;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.CADExtension;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.UtilFunctions;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.gui.cad.CADTool;
import com.iver.cit.gvsig.gui.cad.DefaultCADTool;
import com.iver.cit.gvsig.gui.cad.tools.smc.ScaleCADToolContext;
import com.iver.cit.gvsig.gui.cad.tools.smc.ScaleCADToolContext.ScaleCADToolState;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class ScaleCADTool extends DefaultCADTool {
    private ScaleCADToolContext _fsm;
    private Point2D firstPoint;
    private Point2D lastPoint;
	private Point2D scalePoint;
	private Double orr;
	private Double frr;
	private Double ore;
	private Double fre;

    /**
     * Crea un nuevo PolylineCADTool.
     */
    public ScaleCADTool() {
    }

    /**
     * M�todo de incio, para poner el c�digo de todo lo que se requiera de una
     * carga previa a la utilizaci�n de la herramienta.
     */
    public void init() {
        _fsm = new ScaleCADToolContext(this);
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet, double, double)
     */
    public void transition(double x, double y, InputEvent event) {
        _fsm.addPoint(x, y, event);
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet, double)
     */
    public void transition(double d) {
        _fsm.addValue(d);
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#transition(com.iver.cit.gvsig.fmap.layers.FBitSet, java.lang.String)
     */
    public void transition(String s) {
        _fsm.addOption(s);
    }

    /**
     * DOCUMENT ME!
     */
    public void selection() {
        ArrayList rowSelected=getSelectedRows();
        if (rowSelected.size() == 0 && !CADExtension.getCADTool().getClass().getName().equals("com.iver.cit.gvsig.gui.cad.tools.SelectionCADTool")) {
            CADExtension.setCADTool("selection");
            ((SelectionCADTool) CADExtension.getCADTool()).setNextTool(
                "scale");
        }
    }

    /**
     * Equivale al transition del prototipo pero sin pasarle como par�metro el
     * editableFeatureSource que ya estar� creado.
     *
     * @param x par�metro x del punto que se pase en esta transici�n.
     * @param y par�metro y del punto que se pase en esta transici�n.
     */
    public void addPoint(double x, double y,InputEvent event) {
        ScaleCADToolState actualState = (ScaleCADToolState) _fsm.getPreviousState();
        String status = actualState.getName();

        if (status.equals("Scale.PointMain")) {
				firstPoint = new Point2D.Double(x, y);
			    scalePoint = firstPoint;
		} else if (status.equals("Scale.ScaleFactorOrReference")) {
			PluginServices.getMDIManager().setWaitCursor();
			lastPoint = new Point2D.Double(x, y);

			//double w;
			//double h;
			//w = lastPoint.getX() - firstPoint.getX();
			//h = lastPoint.getY() - firstPoint.getY();

			try {
				double size=getCadToolAdapter().getMapControl().getViewPort().toMapDistance(getCadToolAdapter().getMapControl().getWidth());
				scale(firstPoint.distance(lastPoint)/(size/40));
			} catch (DriverIOException e) {
				e.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			PluginServices.getMDIManager().restoreCursor();
			clearSelection();
		} else if (status.equals("Scale.PointOriginOrScaleFactor")) {
			orr = new Point2D.Double(x, y);
		} else if (status.equals("Scale.EndPointReference")) {
			frr = new Point2D.Double(x, y);
		} else if (status.equals("Scale.OriginPointScale")) {
			ore = new Point2D.Double(x, y);
			firstPoint = ore;
		} else if (status.equals("Scale.EndPointScale")) {
			fre = new Point2D.Double(x, y);

			double distrr = orr.distance(frr);
			double distre = ore.distance(fre);
			double escalado = distre / distrr;

			try {
				scale(escalado);
			} catch (DriverIOException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			clearSelection();
		}

    }

    /**
     * M�todo para dibujar la lo necesario para el estado en el que nos
     * encontremos.
     *
     * @param g Graphics sobre el que dibujar.
     * @param x par�metro x del punto que se pase para dibujar.
     * @param y par�metro x del punto que se pase para dibujar.
     */
    public void drawOperation(Graphics g, double x, double y) {
		ScaleCADToolState actualState = ((ScaleCADToolContext) _fsm).getState();
		String status = actualState.getName();
		ArrayList selectedRow = getSelectedRows();
		Point2D currentPoint = new Point2D.Double(x, y);

		if (status.equals("Scale.ScaleFactorOrReference")) {
			for (int i = 0; i < selectedRow.size(); i++) {
				DefaultFeature fea = (DefaultFeature) ((DefaultRowEdited) selectedRow
						.get(i)).getLinkedRow();
				IGeometry geometry = fea.getGeometry().cloneGeometry();
				double size = getCadToolAdapter().getMapControl().getViewPort()
						.toMapDistance(
								getCadToolAdapter().getMapControl().getWidth());
				UtilFunctions.scaleGeom(geometry, firstPoint, firstPoint
						.distance(currentPoint)
						/ (size / 40), firstPoint.distance(currentPoint)
						/ (size / 40));
				geometry.draw((Graphics2D) g, getCadToolAdapter()
						.getMapControl().getViewPort(), CADTool.modifySymbol);
				drawLine((Graphics2D) g, firstPoint, currentPoint);
				PluginServices.getMainFrame().getStatusBar().setMessage(
						"5",
						"Factor = " + firstPoint.distance(currentPoint)
								/ (size / 40));

			}

		} else if (status.equals("Scale.EndPointScale")) {
			for (int i = 0; i < selectedRow.size(); i++) {
				DefaultFeature fea = (DefaultFeature) ((DefaultRowEdited) selectedRow
						.get(i)).getLinkedRow();
				IGeometry geometry = fea.getGeometry().cloneGeometry();

				double distrr = orr.distance(frr);
				double distre = ore.distance(currentPoint);
				double escalado = distre / distrr;

				UtilFunctions.scaleGeom(geometry, scalePoint, escalado,
						escalado);
				// geometry.scale(scalePoint, escalado, escalado);
				geometry.draw((Graphics2D) g, getCadToolAdapter()
						.getMapControl().getViewPort(), CADTool.modifySymbol);
				drawLine((Graphics2D) g, firstPoint, new Point2D.Double(x, y));

			}
		}
	}

    /**
	 * Add a diferent option.
	 *
	 * @param s
	 *            Diferent option.
	 */
    public void addOption(String s) {
    	ScaleCADToolState actualState = (ScaleCADToolState) _fsm.getPreviousState();
        String status = actualState.getName();
       if (status.equals("Scale.ScaleFactorOrReference")) {
			try {
				scale(2);
			} catch (DriverIOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.gui.cad.CADTool#addvalue(double)
     */
    public void addValue(double d) {
    	ScaleCADToolState actualState = (ScaleCADToolState) _fsm.getPreviousState();
        String status = actualState.getName();
        if (status.equals("Scale.ScaleFactorOrReference")) {
    			try {
    				scale(d);
    			} catch (DriverIOException e) {
    				e.printStackTrace();
    			} catch (IOException e1) {
    				e1.printStackTrace();
    			}

    	}
    }
    private void scale(double scaleFactor) throws DriverIOException, IOException {
    		VectorialEditableAdapter vea=getCadToolAdapter().getVectorialAdapter();
    		vea.startComplexRow();
    		ArrayList selectedRow=getSelectedRows();
    		for (int i = 0; i < selectedRow.size(); i++) {
				DefaultFeature fea = (DefaultFeature) ((DefaultRowEdited) selectedRow
						.get(i)).getLinkedRow().cloneRow();
				UtilFunctions.scaleGeom(fea.getGeometry(), scalePoint, scaleFactor, scaleFactor);
    				// df.getGeometry().scale(scalePoint, scaleFactor, scaleFactor);
    				vea.modifyRow(i, fea,getName());

    		}
    		vea.endComplexRow();
    	}

	public String getName() {
		return PluginServices.getText(this,"scale_");
	}
}
