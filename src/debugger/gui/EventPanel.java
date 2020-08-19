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

import com.sun.jdi.event.Event;

import java.awt.BorderLayout;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JPopupMenu;

import java.awt.event.MouseEvent;
import org.gjt.sp.jedit.View;

public class EventPanel  extends TabPanel
{

  JTextArea output;
  JPopupMenu popup;

  public EventPanel()
  {
  }
  
  protected void createUI()
  {
    output = new JTextArea();
    output.setEditable(false);
    
    JScrollPane scroll = new JScrollPane(output);
    panel.add(scroll, BorderLayout.CENTER);
    
    popup = GUIUtils.createPopupMenu("events.popup", actions);
    output.addMouseListener(mouseHandler);    
  }

  public void event(Event evt)
  {
    output.append(evt.toString() + '\n' );
  }
  
  protected void createActions()
  {
    actions.addAction(new AbstractAction("events.clear")
    {
      public void invoke(View view)
      {
        output.setText("");      
      }
    });
  }
  
  protected JPopupMenu getPopupMenu(MouseEvent evt)
  {
     return popup;
  }
  
}
