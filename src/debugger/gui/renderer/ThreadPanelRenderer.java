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

import com.sun.jdi.ThreadReference;
import com.sun.jdi.ThreadGroupReference;
import com.sun.jdi.StackFrame;

import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIManager;

import java.awt.Component;

import debugger.gui.GUIUtils;

public class ThreadPanelRenderer extends CellRenderer implements TreeCellRenderer
{

  protected static Icon threadIcon;
  protected static Icon threadGroupIcon;
  protected static Icon stackIcon;
  
 
  public ThreadPanelRenderer()
  {
    selectionColor = UIManager.getColor("Tree.selectionBackground");
  }
  
  static
  {
    threadIcon = GUIUtils.createIcon("thread");
    threadGroupIcon = GUIUtils.createIcon("threadgroup");
    stackIcon = GUIUtils.createIcon("stack");
  }

  public Component getTreeCellRendererComponent(JTree tree, Object value,
    boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
      Object object = node.getUserObject();

      setText(node.toString());

      if (object instanceof  ThreadReference)
      {
        setIcon(threadIcon);
      }
      else if (object instanceof ThreadGroupReference)
      {
        setIcon(threadGroupIcon);
      }
      else if (object instanceof StackFrame)
      {
        setIcon(stackIcon);
      }

      isSelected = selected;
      if (selected)
      {
        setBackground(selectionColor);
      }

      return this;
    }


}
