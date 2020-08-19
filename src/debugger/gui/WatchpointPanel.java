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

import com.sun.jdi.event.ModificationWatchpointEvent;
import com.sun.jdi.event.WatchpointEvent;

import debugger.JavaDebuggerPlugin;

import debugger.event.EventRequestListener;

import debugger.plugin.Application;
import debugger.plugin.ApplicationListener;
import debugger.plugin.DebuggerManager;
import debugger.plugin.DebuggerMessage;

import debugger.spec.EventSpec;
import debugger.spec.WatchpointSpec;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

public class WatchpointPanel  extends TabPanel implements ApplicationListener
{

  public WatchpointPanel()
  {
    Application.getInstance().addApplicationListener(this);
    List list  = Application.getInstance().getEventRequests(WatchpointSpec.class);
    watchPoints = new ArrayList(list.size());
    Iterator itr = list.iterator();
    while (itr.hasNext())
    {
      WatchpointSpec spec = (WatchpointSpec) itr.next();
      watchPoints.add(new WatchpointData(spec));
    }
    tableModel = new WatchpointModel();
  }
  
  public void debuggerSet()
  {
    debugger.addEventRequestListener(tableModel);
  }
  
  public void debuggerCleared()
  {
    debugger.removeEventRequestListener(tableModel);
    Application.getInstance().removeApplicationListener(this);
  }
  
  protected void createUI()
  {
    table = new JTable(tableModel);
    JScrollPane scroll = new JScrollPane(table);
    scroll.getViewport().setBackground(Color.white);
    panel.add(BorderLayout.CENTER, scroll);
    
    table.addMouseListener(mouseHandler);
    scroll.getViewport().addMouseListener(mouseHandler);
    popupMenu = GUIUtils.createPopupMenu("watches.popup", actions);
  }
  
  private static final int COL_COUNT = 3;
  private static final String[] COL_NAMES = 
  {
    "Class",
    "Member",
    "Value"
  };
  
  private final class WatchpointModel extends AbstractTableModel implements EventRequestListener
  {
    public final int getColumnCount()
    {
      return COL_COUNT;
    }
    
    public final int getRowCount()
    {
      return watchPoints.size();
    }

    public final Object getValueAt(int row, int col)
    {
      WatchpointData data = (WatchpointData) watchPoints.get(row);
      switch(col)
      {
        case 0:
          return data.watch.getClassName();
        case 1:
          return data.watch.getFieldName();
        case 2:
        default:
          return data.value;    
      }
    }
    
    public final String getColumnName(int col)
    {
      return COL_NAMES[col];
    }
       
    private boolean update(WatchpointSpec event)
    {
      boolean retVal = false;
      Iterator itr = watchPoints.iterator();
      while (itr.hasNext())
      {
        WatchpointData data = (WatchpointData) itr.next();
        if (data.watch.equals(event))
        {
          data.watch = event;
          if (event.isEnabled())
          {
            data.value = EMPTY;
          }
          else
          {
            data.value = DISABLED;
          }
          int index = watchPoints.indexOf(data);
          fireTableRowsUpdated(index, index);
          retVal = true;
          break;
        }
      }
      return retVal;
    }
    
    public void eventRequestHit(EventSpec event) {}
    
    public void eventRequestEnabled(EventSpec event)
    {
      if (event instanceof WatchpointSpec)
      {
        update( (WatchpointSpec)event);
      }
    }
    
    public void eventRequestDisabled(EventSpec event)
    {
      if (event instanceof WatchpointSpec)
      {
        update( (WatchpointSpec)event);
      }
    }
    
    public void eventRequestAdded(EventSpec spec)
    {
      if (spec instanceof WatchpointSpec)
      {
        WatchpointSpec watch = (WatchpointSpec) spec;
        if (! update( (WatchpointSpec)spec))
        {
          watchPoints.add(new WatchpointData(watch));
          int size = watchPoints.size();
          fireTableRowsInserted(size -1, size);      
        }
      }
    }
    
    public void eventRequestRemoved(EventSpec spec)
    {
      if (spec instanceof WatchpointSpec)
      {
        int index = watchPoints.indexOf(spec);
        if (index != -1 )
        {
          watchPoints.remove(index);
          fireTableStructureChanged();
        }
      }
    }
  }
  
  public void watchpointEvent(WatchpointEvent event)
  {
    EventSpec spec = (EventSpec) event.request().getProperty(EventSpec.EVENT_SPEC);
    int index = watchPoints.indexOf(spec);

    if (index != -1)
    {
      String value = event.valueCurrent() == null ? "null" : event.valueCurrent().toString();
      if (event instanceof ModificationWatchpointEvent)
      {
        ModificationWatchpointEvent mwp = (ModificationWatchpointEvent)event;
        String newValue = mwp.valueToBe() == null ? "null" : mwp.valueToBe().toString();
        value += " -> " + newValue;
      }
    
      WatchpointData data = (WatchpointData) watchPoints.get(index);
      data.value = value;
      tableModel.fireTableRowsUpdated(index, index);
      table.setRowSelectionInterval(index, index);
    }
  }

  //Application Listener methods.
  public void eventAdded(EventSpec event)
  {
    tableModel.eventRequestAdded(event);
  }
  
  public void eventRemoved(EventSpec event)
  {
    tableModel.eventRequestRemoved(event);
  }
  
  public void eventModified(EventSpec event)
  {
    if (event instanceof WatchpointSpec)
    {
      tableModel.update((WatchpointSpec)event);
    }
  }

  
  protected void createActions()
  {
    actions.addAction(new WatchAction(ADD_WATCH));
    actions.addAction(new WatchAction(REMOVE_WATCH));
    actions.addAction(new WatchAction(TOGGLE_WATCH));
  }
  
  protected JPopupMenu getPopupMenu(MouseEvent evt)
  {
    int row = table.getSelectedRow();
    if ( row != -1 )
    {
      EventSpec spec = (EventSpec) ((WatchpointData)watchPoints.get(row)).watch;
      JMenuItem mi = (JMenuItem) popupMenu.getComponent(TOGGLE_INDEX);
      if (spec.isEnabled() )
      {
        mi.setText(DISABLE_TEXT);
      }
      else
      {
        mi.setText(ENABLE_TEXT);
      }
    }
    return popupMenu;
  }
  
  class WatchAction extends AbstractAction
  {
    public WatchAction(String name)
    {
      super(name);
    }
    
    public void invoke(View view)
    {
      DebuggerManager manager = JavaDebuggerPlugin.getPlugin().getDebuggerManager(view);
      if (getName().equals(ADD_WATCH))
      {
        WatchpointUI ui = new WatchpointUI();
        if (ui.getWatch() != null)
        {
          int size = watchPoints.size();
          WatchpointSpec watch = ui.getWatch();
          watchPoints.add(new WatchpointData(watch));
          manager.addEventRequest(watch);
        }
      }
      else if (getName().equals(REMOVE_WATCH))
      {
        int row = table.getSelectedRow();
        if (row != -1)
        {
          WatchpointData data = (WatchpointData) watchPoints.remove(row);
          manager.removeEventRequest(data.watch);
        }        
      }
      else if (getName().equals(TOGGLE_WATCH))
      {
        int row = table.getSelectedRow();
        if (row != -1)
        {
          EventSpec event = ((WatchpointData) watchPoints.get(row)).watch;
          if (event.isEnabled())
          {
            manager.disableEventRequest(event);
          }
          else
          {
            manager.enableEventRequest(event);
          }
        }                
      }
    }
  }
  
  public void handleDebuggerMessage(DebuggerMessage message)
  {
    if (message.getReason() == DebuggerMessage.SESSION_RESUMED)
    {
      clearData();
    }
  }
  
  private final void clearData()
  {
    Iterator itr = watchPoints.iterator();
    while (itr.hasNext())
    {
      WatchpointData data = (WatchpointData) itr.next();
      data.value = EMPTY;
    }
  }
  
  private static final class WatchpointData
  {
    WatchpointSpec watch;
    String value;
    
    WatchpointData(WatchpointSpec spec)
    {
      watch = spec;
      value = spec.isEnabled() ? EMPTY : DISABLED;
    }
    
    public boolean equals(Object obj)
    {
      if (obj instanceof WatchpointData)
      {
        WatchpointData other = (WatchpointData)obj;
        return (other.watch.equals(watch));
      }
      else if (obj instanceof WatchpointSpec)
      {
        return watch.equals(obj);
      }
      return false;
    }
  }
  private static final String ADD_WATCH = "addwatch";
  private static final String REMOVE_WATCH = "removewatch";
  private static final String TOGGLE_WATCH = "enablewatch";
  private static final int TOGGLE_INDEX = 2;

  static final String ENABLE_TEXT = jEdit.getProperty("enablewatch.label", "Enable");
  static final String DISABLE_TEXT = jEdit.getProperty("disablewatch.label", "Disable");
  
  private static final String DISABLED = "<Disabled>";
  private static final String EMPTY = "";
  
  private List watchPoints;
  
  private JPopupMenu popupMenu;
  private JTable table;
  private WatchpointModel tableModel;

}

