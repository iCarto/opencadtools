
//
// Vicente Caballero Navarro


package com.iver.cit.gvsig.gui.cad.tools.smc;

import com.iver.cit.gvsig.gui.cad.tools.CopyCADTool;
import java.awt.event.InputEvent;

public final class CopyCADToolContext
    extends statemap.FSMContext
{
//---------------------------------------------------------------
// Member methods.
//

    public CopyCADToolContext(CopyCADTool owner)
    {
        super();

        _owner = owner;
        setState(Copy.FirstPointToMove);
        Copy.FirstPointToMove.Entry(this);
    }

    public void addOption(String s)
    {
        _transition = "addOption";
        getState().addOption(this, s);
        _transition = "";
        return;
    }

    public void addPoint(double pointX, double pointY, InputEvent event)
    {
        _transition = "addPoint";
        getState().addPoint(this, pointX, pointY, event);
        _transition = "";
        return;
    }

    public CopyCADToolState getState()
        throws statemap.StateUndefinedException
    {
        if (_state == null)
        {
            throw(
                new statemap.StateUndefinedException());
        }

        return ((CopyCADToolState) _state);
    }

    protected CopyCADTool getOwner()
    {
        return (_owner);
    }

//---------------------------------------------------------------
// Member data.
//

    transient private CopyCADTool _owner;

//---------------------------------------------------------------
// Inner classes.
//

    public static abstract class CopyCADToolState
        extends statemap.State
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected CopyCADToolState(String name, int id)
        {
            super (name, id);
        }

        protected void Entry(CopyCADToolContext context) {}
        protected void Exit(CopyCADToolContext context) {}

        protected void addOption(CopyCADToolContext context, String s)
        {
            Default(context);
        }

        protected void addPoint(CopyCADToolContext context, double pointX, double pointY, InputEvent event)
        {
            Default(context);
        }

        protected void Default(CopyCADToolContext context)
        {
            throw (
                new statemap.TransitionUndefinedException(
                    "State: " +
                    context.getState().getName() +
                    ", Transition: " +
                    context.getTransition()));
        }

    //-----------------------------------------------------------
    // Member data.
    //
    }

    /* package */ static abstract class Copy
    {
    //-----------------------------------------------------------
    // Member methods.
    //

    //-----------------------------------------------------------
    // Member data.
    //

        //-------------------------------------------------------
        // Statics.
        //
        /* package */ static Copy_Default.Copy_FirstPointToMove FirstPointToMove;
        /* package */ static Copy_Default.Copy_SecondPointToMove SecondPointToMove;
        private static Copy_Default Default;

        static
        {
            FirstPointToMove = new Copy_Default.Copy_FirstPointToMove("Copy.FirstPointToMove", 0);
            SecondPointToMove = new Copy_Default.Copy_SecondPointToMove("Copy.SecondPointToMove", 1);
            Default = new Copy_Default("Copy.Default", -1);
        }

    }

    protected static class Copy_Default
        extends CopyCADToolState
    {
    //-----------------------------------------------------------
    // Member methods.
    //

        protected Copy_Default(String name, int id)
        {
            super (name, id);
        }

        protected void addOption(CopyCADToolContext context, String s)
        {
            CopyCADTool ctxt = context.getOwner();

            if (s.equals("Cancelar"))
            {
                boolean loopbackFlag =
                    context.getState().getName().equals(
                        Copy.FirstPointToMove.getName());

                if (loopbackFlag == false)
                {
                    (context.getState()).Exit(context);
                }

                context.clearState();
                try
                {
                    ctxt.end();
                }
                finally
                {
                    context.setState(Copy.FirstPointToMove);

                    if (loopbackFlag == false)
                    {
                        (context.getState()).Entry(context);
                    }

                }
            }
            else
            {
                super.addOption(context, s);
            }

            return;
        }

    //-----------------------------------------------------------
    // Inner classse.
    //


        private static final class Copy_FirstPointToMove
            extends Copy_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Copy_FirstPointToMove(String name, int id)
            {
                super (name, id);
            }

            protected void Entry(CopyCADToolContext context)
            {
                CopyCADTool ctxt = context.getOwner();

                ctxt.selection();
                ctxt.setQuestion("COPIAR" + "\n" +
		"Precisar punto de desplazamiento");
                ctxt.setDescription(new String[]{"Cancelar"});
                return;
            }

            protected void addPoint(CopyCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                CopyCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setQuestion("Precisar segundo punto del desplazamiento");
                    ctxt.setDescription(new String[]{"Cancelar"});
                    ctxt.addPoint(pointX, pointY);
                }
                finally
                {
                    context.setState(Copy.SecondPointToMove);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

        private static final class Copy_SecondPointToMove
            extends Copy_Default
        {
        //-------------------------------------------------------
        // Member methods.
        //

            private Copy_SecondPointToMove(String name, int id)
            {
                super (name, id);
            }

            protected void addPoint(CopyCADToolContext context, double pointX, double pointY, InputEvent event)
            {
                CopyCADTool ctxt = context.getOwner();


                (context.getState()).Exit(context);
                context.clearState();
                try
                {
                    ctxt.setDescription(new String[]{"Cancelar"});
                    ctxt.addPoint(pointX, pointY);
                    ctxt.end();
                    ctxt.refresh();
                }
                finally
                {
                    context.setState(Copy.FirstPointToMove);
                    (context.getState()).Entry(context);
                }
                return;
            }

        //-------------------------------------------------------
        // Member data.
        //
        }

    //-----------------------------------------------------------
    // Member data.
    //
    }
}
