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
package com.iver.cit.gvsig;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.cad.tools.ArcCADTool;
import com.iver.cit.gvsig.gui.cad.tools.LineCADTool;
import com.iver.cit.gvsig.project.documents.view.gui.View;

/**
 * Extensi?n que gestiona la inserci?n de l?neas en edici?n.
 * 
 * @author Vicente Caballero Navarro
 */
public class InsertLineExtension extends BaseCADExtension {
    protected View view;
    protected LineCADTool line;
    protected MapControl mapControl;

    @Override
    public void initialize() {
	line = new LineCADTool();
	CADExtension.addCADTool("_line", line);
	registerIcon("edition-insert-line", "images/icons/linea.png");

	ArcCADTool arc = new ArcCADTool();
	CADExtension.addCADTool("_arc", arc);
	registerIcon("edition-insert-arc", "images/icons/arco.png");
    }

    /**
     * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
     */
    @Override
    public void execute(String s) {
	CADExtension.initFocus();
	if (s.equals("_line") || s.equals("_arc")) {
	    CADExtension.setCADTool(s, true);
	    CADExtension.getEditionManager().setMapControl(mapControl);
	}
	CADExtension.getCADToolAdapter().configureMenu();
    }

    /**
     * @see com.iver.andami.plugins.IExtension#isEnabled()
     */
    @Override
    public boolean isEnabled() {

	try {
	    if (EditionUtilities.getEditionStatus() == EditionUtilities.EDITION_STATUS_ONE_VECTORIAL_LAYER_ACTIVE_AND_EDITABLE) {
		view = (View) PluginServices.getMDIManager().getActiveWindow();
		mapControl = view.getMapControl();
		if (CADExtension.getEditionManager().getActiveLayerEdited() == null) {
		    return false;
		}
		FLyrVect lv = (FLyrVect) CADExtension.getEditionManager()
			.getActiveLayerEdited().getLayer();
		if (line.isApplicable(lv.getShapeType())) {
		    return true;
		}
	    }
	} catch (ReadDriverException e) {
	    NotificationManager.addError(e.getMessage(), e);
	}
	return false;
    }
}
