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

import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaPainter;

import java.awt.Graphics2D;
import java.awt.FontMetrics;
import javax.swing.Icon;

import java.util.HashMap;
import java.util.List;

import debugger.spec.SourceBreakpointSpec;

public class BreakpointHighlight extends CustomHighlight
{
  HashMap table;

  static final Icon enabledIcon;
  static final Icon disabledIcon;
  static final int height, width;
  

  static
  {
    enabledIcon = GUIUtils.createIcon("break");
    disabledIcon = GUIUtils.createIcon("disabledbreak");
    height = enabledIcon.getIconHeight();
    width = enabledIcon.getIconWidth();
  }

  public BreakpointHighlight(JEditTextArea textarea)
  {
    super(textarea);
    table = new HashMap();
  }

  public void update(List list)
  {
    table.clear();
    for(int i=0; i < list.size(); i++)
    {
      SourceBreakpointSpec spec = (SourceBreakpointSpec) list.get(i);
      Integer line = new Integer(spec.lineNumber());
      table.put(line, spec.isEnabled() ? Boolean.TRUE : Boolean.FALSE);
    }
  }


  public void add(int lineNo)
  {
    table.put(new Integer(lineNo), Boolean.TRUE);
    redraw(lineNo);
  }

  public void remove(int lineNo)
  {
    table.remove(new Integer(lineNo));
    redraw(lineNo);
  }

  public void update(int lineNo, boolean enabled)
  {
    table.put(new Integer(lineNo), Boolean.valueOf(enabled));
    redraw(lineNo);
  }

  private final boolean hasBreakpoint(int line)
  {
    return (table.get(new Integer(line + 1)) != null);
  }
  
  private final boolean isEnabled(int line)
  {
    Boolean value = (Boolean) table.get(new Integer(line + 1));
    return value.booleanValue();
  }

  public void paintValidLine(Graphics2D gfx, int screenLine, int physicalLine,
    int start, int end, int y)
  {
    TextAreaPainter painter = textarea.getPainter();
    FontMetrics fm = painter.getFontMetrics();
    int actualY = y + (fm.getHeight() - height)/2;


    //If there is a breakpoint on the current line. Show that
    if (hasBreakpoint(physicalLine) )
    {
      Icon icon;
      if (isEnabled(physicalLine))
        icon = enabledIcon;
      else
        icon = disabledIcon;
      icon.paintIcon(painter, gfx, 0, actualY);
    }
  }

}
