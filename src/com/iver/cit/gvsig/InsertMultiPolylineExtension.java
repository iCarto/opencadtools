/*
 * Copyright 2008 Deputaci�n Provincial de A Coru�a
 * Copyright 2009 Deputaci�n Provincial de Pontevedra
 * Copyright 2010 CartoLab, Universidad de A Coru�a
 *
 * This file is part of openCADTools, developed by the Cartography
 * Engineering Laboratory of the University of A Coru�a (CartoLab).
 * http://www.cartolab.es
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
 */
package com.iver.cit.gvsig;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.gui.cad.tools.MultiPolylineCADTool;
import com.iver.cit.gvsig.project.documents.view.gui.View;

/**
 * Extensi�n que gestiona la inserci�n de polil�neas en edici�n.
 * 
 * @author Isabel P�rez-Urria Lage [LBD]
 * @author Javier Est�vez [Cartolab]
 */
public class InsertMultiPolylineExtension extends BaseCADExtension {
    private final String iconPath = "images/icons/multipolilinea.png";
    private final String iconCode = "edition-insert-multipolyline";
    private final String cadToolCode = "_insert_multipolyline";

    @Override
    public void initialize() {
	tool = new MultiPolylineCADTool();
	CADExtension.addCADTool(cadToolCode, tool);
	registerIcon(iconCode, iconPath);
    }

    /**
     * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
     */
    @Override
    public void execute(String s) {
	CADExtension.initFocus();
	if (s.equals(cadToolCode)) {
	    CADExtension.setCADTool(cadToolCode, true);
	    View view = (View) PluginServices.getMDIManager().getActiveWindow();
	    MapControl mapControl = view.getMapControl();
	    CADExtension.getEditionManager().setMapControl(mapControl);
	}
	CADExtension.getCADToolAdapter().configureMenu();
    }
}
