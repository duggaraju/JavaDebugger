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

package debugger;

import debugger.plugin.Application;
import debugger.plugin.DebuggerManager;

import java.util.Hashtable;

import java.util.Iterator;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.MiscUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.msg.ViewUpdate;

public class JavaDebuggerPlugin extends EBPlugin
{

  private Hashtable views;

  public JavaDebuggerPlugin()
  {
    //Initialize the static variable which is handle to this plugin;
    plugin = this;
  }

  public void handleMessage(EBMessage message)
  {
    if (message instanceof ViewUpdate)
    {
      ViewUpdate vu = (ViewUpdate) message;
      View view = vu.getView();
      Object what = vu.getWhat();

      if (what == ViewUpdate.CREATED)
      {
        addView(view);
      }
      else if (what == ViewUpdate.CLOSED)
      {
        removeView(view);
      }

    }
  }

  public void start()
  {
    if(!MiscUtilities.isToolsJarAvailable())
    {
      throw new RuntimeException("Debugger Plugin Requires JDK (not JRE) 1.3 or above");
    }
    //Initialize required data structures
    views = new Hashtable();
    
    //For all the already open views create a debugger manager
    View[] openViews = jEdit.getViews();
    for (int i = 0; i < openViews.length; i++)
    {
      if (views.get(openViews[i]) == null)
      {
        addView(openViews[i]);
      }
    }
  }

  public void stop()
  {
    Iterator iterator  = views.keySet().iterator();
    while (iterator.hasNext())
    {
      View view = (View) iterator.next();
      DebuggerManager manager = (DebuggerManager) views.get(view);
      manager.close();
    }
    
    views.clear();
    Application.getInstance().close();
    
    views = null;
  }

  private final void addView(View view)
  {
    if (views.get(view) == null)
    {
      DebuggerManager manager = new DebuggerManager(view);
      views.put(view, manager);
    }
  }
  
  private final void removeView(View view)
  {
    DebuggerManager manager = (DebuggerManager) views.remove(view);
    if (manager != null)
    {
      manager.close();    
    }
  }
  
  public final DebuggerManager getDebuggerManager(View view)
  {
    return (DebuggerManager) views.get(view);
  }
  

  /**
   * Returns the instance of JavaDebuggerPlugin running.
   * Since the plugin is a singleton this method helps to get the handle
   * to the singleton
   */

  private static JavaDebuggerPlugin plugin;

  public static JavaDebuggerPlugin getPlugin()
  {
    return plugin;
  }

}
