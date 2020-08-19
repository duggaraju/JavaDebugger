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

import debugger.JavaDebuggerPlugin;

import debugger.core.Debugger;

import debugger.plugin.DebuggerMessage;

import java.awt.BorderLayout;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.util.Log;

/**
 * Implements a debugger dockable window 
 */
public class DebugWindow extends JPanel implements EBComponent
{

  private static final String [] PANEL_NAMES =
  {
    "io",
    "threads",
    "breakpoints",
    "data",
    "watches",
    "classes",
    "events"
  };
  
  private static final Class[] PANEL_CLASSES =
  {
    IOPanel.class,
    ThreadPanel.class,
    BreakpointPanel.class,
    DataPanel.class,
    WatchpointPanel.class,
    ClassPanel.class,
    EventPanel.class
  };

  public DebugWindow(View view)
  {
    super(new BorderLayout());
    this.view = view;
    createUI();    

    EditBus.addToBus(this);
  }



  private final void createUI()
  {
    panelList = new ArrayList();
    JTabbedPane tabpane = new JTabbedPane(SwingConstants.TOP);

    for (int i=0; i < PANEL_CLASSES.length; i++)
    {
      try
      {
        String property = "options.jdebugger.showtab_" + PANEL_NAMES[i];
        boolean isShown = jEdit.getBooleanProperty(property, true);
        if (isShown)
        {
          TabPanel panel = (TabPanel) PANEL_CLASSES[i].newInstance();
          panelList.add(panel);
          Icon icon = GUIUtils.createIcon(PANEL_NAMES[i]);
          String name = jEdit.getProperty(PANEL_NAMES[i] + ".name");
          String tip = jEdit.getProperty(PANEL_NAMES[i] + ".tooltip");
          tabpane.addTab(name, icon, panel.getPanel(), tip);
        }
      }
      catch(Exception ex)
      {
        Log.log(Log.ERROR, JavaDebuggerPlugin.getPlugin(), ex);
      }
    }

    add(BorderLayout.CENTER, tabpane);
  }

  private final void setDebugger(Debugger debugger)
  {
    Iterator itr = panelList.iterator();
    while (itr.hasNext())
    {
      TabPanel panel = (TabPanel) itr.next();
      panel.setDebugger(debugger);
    }
  }

  private final void clearDebugger()
  {
    Iterator itr = panelList.iterator();
    while (itr.hasNext())
    {
      TabPanel panel = (TabPanel) itr.next();
      panel.clearDebugger();
    }
  }

  //We can make each of the UI panels an EBCompoenent but this is more simple...
  public void handleMessage(EBMessage message)
  {
    if (message instanceof DebuggerMessage && message.getSource() == view)
    {
      DebuggerMessage dmesg = (DebuggerMessage) message;
      
      Iterator itr = panelList.iterator();
      while (itr.hasNext())
      {
        TabPanel panel = (TabPanel) itr.next();
        panel.handleMessage(dmesg);
      }      
    }
  }

  public final void removeNotify()
  {
    clearDebugger();
    close();
    super.removeNotify();
  }
  
  public final void close()
  {
    EditBus.removeFromBus(this);
  }
  
  private View view;
  private ArrayList panelList;
}
