package com.iver.cit.gvsig.project.documents.table.operators;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;

import com.iver.cit.gvsig.ExpresionFieldExtension;
import com.iver.cit.gvsig.project.documents.table.AbstractOperator;
import com.iver.cit.gvsig.project.documents.table.IOperator;

/**
 * @author Vicente Caballero Navarro
 */
public class EndsWith extends AbstractOperator{

	public String addText(String s) {
		return toString()+"("+s+",\"\")";
	}
	public String toString() {
		return "endsWith";
	}
	public void eval(BSFManager interpreter) throws BSFException {
		interpreter.eval(ExpresionFieldExtension.BEANSHELL,null,-1,-1,"boolean endsWith(String value1,String value2){return value1.endsWith(value2);};");
	}
	public boolean isEnable() {
		return (getType()==IOperator.STRING);
	}

}
