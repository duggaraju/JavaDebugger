/*
A plugin for jEdit which implements java debugger functionality.
Copyright (C) 2003  Krishna Prakash Duggaraju

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

package debugger.gui;

import debugger.core.Debugger;

import debugger.event.EventAdapter;

import debugger.plugin.DebuggerMessage;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.gjt.sp.jedit.ActionSet;

/**
 * A Helper class that encapsulates the common helper routines for the other
 * GUI panels in the debugger dockable.
 */
abstract class TabPanel extends EventAdapter
{
  public TabPanel()
  {
    createActions();
  }
  
  void setDebugger(Debugger debugger)
  {
    if (debugger != null &&  this.debugger == null)
    {
      this.debugger = debugger;
      debugger.addEventListener(this);
      debuggerSet();
    }
  }
  
  protected void debuggerSet() {}
  protected void debuggerCleared() {}
  
  void clearDebugger()
  {
    if (debugger != null)
    {
      debuggerCleared();
      debugger.removeEventListener(this);
      debugger = null;
    }
  }
  
  /** Helper class the provides support for Popup Menus. */
  private final class MouseHandler extends MouseAdapter
  {
    private final void showPopup(MouseEvent evt)
    {
       JPopupMenu menu = getPopupMenu(evt);
       if (menu != null)
       {
         menu.show(evt.getComponent(), evt.getX(), evt.getY());
       }
    }
    
    public void mousePressed(MouseEvent evt)
    {
      if(evt.isPopupTrigger())
      {
        showPopup(evt);
      }
    }
    
    public void mouseReleased(MouseEvent evt)
    {
      if(evt.isPopupTrigger())
      {
        showPopup(evt);
      }
    }
  }
  
  final JPanel getPanel()
  {
    if (panel == null)
    {
      panel = new JPanel(new BorderLayout());
      createUI();
    }
    return panel;
  }
  
  /**
   * Returns the popup menu for the given mouse event
   * @param event the event for which the popup menu is to be returned.
   * @return popmenu.
   * @return null if no popup is applicable.
   */
  protected JPopupMenu getPopupMenu(MouseEvent event)
  {
    return null;
  }
  
  
  void handleMessage(DebuggerMessage message)
  {
    if (message.getReason() == DebuggerMessage.SESSION_STARTING)
    {
      setDebugger(message.getSession());
    }
    if (message.getSession() == debugger)
    {
      handleDebuggerMessage(message);
    }
  }
  
  protected void handleDebuggerMessage(DebuggerMessage message)
  {}

  /** Derived classes can overrride this to create the requried popup actions */  
  protected void createActions() {}
  
  protected abstract void createUI();

  //Holds the UI. Sub classes will add elements to this.
  protected JPanel panel;
  protected ActionSet actions = new ActionSet();
  protected MouseHandler mouseHandler = new MouseHandler();
  protected Debugger debugger;
}
