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

package debugger.gui.renderer;

import debugger.spec.ExceptionBreakpointSpec;
import debugger.spec.MethodBreakpointSpec;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.Icon;
import java.awt.Component;

import debugger.spec.EventSpec;
import debugger.spec.SourceBreakpointSpec;

import debugger.gui.GUIUtils;

public class BreakpointRenderer extends CellRenderer implements ListCellRenderer
{

  static Icon bpIcon;
  static Icon activeIcon;
  static Icon disabledIcon;
  int activeIndex = -1;
  
  static
  {
    bpIcon = GUIUtils.createIcon("break");
    activeIcon = GUIUtils.createIcon("activebreak");
    disabledIcon = GUIUtils.createIcon("disabledbreak");   
  }

  public final void setActiveIndex(int index)
  {
    activeIndex = index;  
  }
  
  public final int getActiveIndex()
  {
    return activeIndex;
  }
  
  public Component getListCellRendererComponent(JList list, Object value,
    int index, boolean selected, boolean hasFocus)
  {
    EventSpec request = (EventSpec) value;
    Icon icon = bpIcon;
    if (!request.isEnabled())
    {
      icon = disabledIcon;
    }
    else if (activeIndex != -1 && activeIndex == index)
    {
      icon = activeIcon;
    }
    setIcon(icon);
    
    isSelected = selected;
    String labelText = null;

    if (request instanceof SourceBreakpointSpec)
    {
      SourceBreakpointSpec sbp  = (SourceBreakpointSpec)request;
      String fileName = sbp.filename();
      int lineNo = sbp.lineNumber();
      labelText = fileName + ':' + lineNo;
    }
    else if (request instanceof ExceptionBreakpointSpec)
    {
      ExceptionBreakpointSpec ebp = (ExceptionBreakpointSpec)request;
      labelText = "Exception: "+ ebp.getClassName();
    }
    else if (request instanceof MethodBreakpointSpec)
    {
      MethodBreakpointSpec mbp = (MethodBreakpointSpec) request;
      labelText = mbp.getClassName() + ':' + mbp.getMethodName();
    }
    setText(labelText);
    
    return this;
  }

}