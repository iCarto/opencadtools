package com.iver.cit.gvsig;

import java.io.File;
import java.net.URL;
import java.sql.Types;

import org.geotools.data.FeatureStore;
import org.geotools.data.FeatureWriter;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.AttributeType;
import org.geotools.feature.AttributeTypeFactory;
import org.geotools.feature.FeatureType;
import org.geotools.feature.FeatureTypeBuilder;

import com.hardcode.gdbms.engine.values.NullValue;
import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.fmap.FMap;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.gui.View;
import com.iver.cit.gvsig.project.ProjectView;
import com.iver.cit.gvsig.writers.WriterGT2;
import com.vividsolutions.jts.geom.LineString;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class StopEditingToGT2Shp implements Extension {
    /**
     * @see com.iver.andami.plugins.Extension#inicializar()
     */
    public void inicializar() {
    }

    /**
     * @see com.iver.andami.plugins.Extension#execute(java.lang.String)
     */
    public void execute(String s) {
        com.iver.andami.ui.mdiManager.View f = PluginServices.getMDIManager()
                                                             .getActiveView();

        View vista = (View) f;
        ProjectView model = vista.getModel();
        FMap mapa = model.getMapContext();
            FLayers layers = mapa.getLayers();
            if (s.equals("STOPEDITING")){
            for (int i = 0; i < layers.getLayersCount(); i++) {
                if (layers.getLayer(i) instanceof FLyrVect &&
                        layers.getLayer(i).isEditing()) {
                    FLyrVect lv = (FLyrVect) layers.getLayer(i);
                    stopEditing(lv);

                    return;
                }
            }
            }
            PluginServices.getMainFrame().enableControls();
    }

    /**
     * @see com.iver.andami.plugins.Extension#isEnabled()
     */
    public boolean isEnabled() {
        return true;
    }

    
    
    private Class getClassBySqlTYPE(int type)
    {
        switch (type)
        {
            case Types.SMALLINT:
                return Integer.class;
            case Types.INTEGER:
            	return Integer.class;
            case Types.BIGINT:
            	return Integer.class;
            case Types.BOOLEAN:
            	return Boolean.class;
            case Types.DECIMAL:
            	return Double.class;
            case Types.DOUBLE:
            	return Double.class;
            case Types.FLOAT:
            	return Float.class;
            case Types.CHAR:
            	return Character.class;
            case Types.VARCHAR:
            	return String.class;
            case Types.LONGVARCHAR:
            	return String.class;
        }
        return NullValue.class;
    }
    
    /**
     * DOCUMENT ME!
     */
    public void stopEditing(FLyrVect layer) {
        try {
            // WriterGT2Shp writer = new WriterGT2Shp(layer);
        	AttributeType geom = AttributeTypeFactory.newAttributeType("the_geom", LineString.class);
        	
        	int numFields = layer.getRecordset().getFieldCount() + 1;
        	AttributeType[] att = new AttributeType[numFields];
        	att[0] = geom;
        	for (int i=1; i < numFields; i++)
        	{
        		att[i] = AttributeTypeFactory.newAttributeType(
        				layer.getRecordset().getFieldName(i-1),
        				getClassBySqlTYPE(layer.getRecordset().getFieldType(i-1))); 
        	}
        	FeatureType featType = FeatureTypeBuilder.newFeatureType(att,"prueba");
        	
        	File file = new File("c:/prueba.shp");
			URL theUrl = file.toURL();
			ShapefileDataStore dataStore = new ShapefileDataStore(theUrl);
			dataStore.createSchema(featType);
			
			String featureName = dataStore.getTypeNames()[0];
			FeatureStore featStore = (FeatureStore) dataStore.getFeatureSource(featureName);
			
			// Necesitamos crear de verdad los ficheros antes de usarlos para meter las features
			FeatureWriter featWriter = dataStore.getFeatureWriterAppend(featureName, featStore.getTransaction());
			featWriter.close();
			// Aqu� ya tenemos un fichero vac�o, listo para usar.
			
			
			WriterGT2 writer = new WriterGT2(featStore);
			
            VectorialEditableAdapter vea = (VectorialEditableAdapter) layer.getSource();
            vea.stopEdition(writer);
            layer.setSource(vea.getOriginalAdapter());
            layer.setEditing(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @see com.iver.andami.plugins.Extension#isVisible()
     */
    public boolean isVisible() {
        com.iver.andami.ui.mdiManager.View f = PluginServices.getMDIManager()
                                                             .getActiveView();

        if (f == null) {
            return false;
        }

        if (f.getClass() == View.class) {
            View vista = (View) f;
            ProjectView model = vista.getModel();
            FMap mapa = model.getMapContext();

            FLayers capas = mapa.getLayers();

            for (int i = 0; i < capas.getLayersCount(); i++) {
                if (capas.getLayer(i) instanceof FLyrVect &&
                        capas.getLayer(i).isEditing()) {
                    return true;
                }
            }

            return false;
        }

        return false;
    }
}
