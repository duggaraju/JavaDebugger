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

package debugger.options;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

/**
 * A panel for the general UI options of the debugger.
 */
public class GeneralDebuggerOptions extends AbstractOptionPane implements DebuggerOptions
{

  public GeneralDebuggerOptions()
  {
    super("java.debugger.general");
  }
  
  static final String[] tabs = 
  {
    "io",
    "threads",
    "breakpoints",
    "watches",
    "data",
    "classes",
    "events"
  };
  
  JCheckBox[] tabSelection;
  JCheckBox showStartup;
  JCheckBox toolTips;
  
  protected void _init()
  {
    boolean showDialog = jEdit.getBooleanProperty(SHOW_STARTUP, true);
    showStartup = new JCheckBox("Show parameter dialog at startup", showDialog);
    addComponent(showStartup);
    
    boolean showToolTips = jEdit.getBooleanProperty(SHOW_TOOLTIP, true);
    toolTips = new JCheckBox("Show tooltips while debugging", showToolTips);
    addComponent(toolTips);
    
    addComponent(new JLabel("Debugger tabs:"));
    tabSelection = new JCheckBox[tabs.length];
    for (int i = 0; i < tabs.length; i++)
    {
      String label = jEdit.getProperty(tabs[i] + ".name");
      boolean selected = jEdit.getBooleanProperty(SHOWTAB + tabs[i], true);
      tabSelection[i] = new JCheckBox(label, selected);
      addComponent(tabSelection[i]);
    }
  }

  protected void _save()
  {
    jEdit.setBooleanProperty(SHOW_STARTUP, showStartup.isSelected());
    jEdit.setBooleanProperty(SHOW_TOOLTIP, toolTips.isSelected());
    for (int i = 0; i < tabs.length; i++)
    {
      String property = SHOWTAB + tabs[i];
      jEdit.setBooleanProperty(property, tabSelection[i].isSelected());
    }
  }

}
