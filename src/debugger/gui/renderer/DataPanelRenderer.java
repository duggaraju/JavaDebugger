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

import javax.swing.tree.TreeCellRenderer;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import java.awt.Component;

import debugger.gui.tree.TreeNode;
import debugger.gui.GUIUtils;

public class DataPanelRenderer extends CellRenderer implements TreeCellRenderer
{

  protected static Icon objectIcon;
  protected static Icon primitiveIcon;
  
  EmptyBorder defaultBorder = new EmptyBorder(1, 1, 1, 1);
 
  static
  {
    objectIcon = GUIUtils.createIcon("object");
    primitiveIcon = GUIUtils.createIcon("primitive");
  }

  public void setIcon(Icon icon)
  {
    
  }

  public Component getTreeCellRendererComponent(JTree tree, Object value,
    boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
      TreeNode node = (TreeNode)value;
      setText(node.toString());

      isSelected = selected;
      if (selected)
      {
        setBackground(selectionColor);
      }

      if (hasFocus)
      {
        setBorder(UIManager.getBorder("Table.focusCellHighlightBorder")); 
      }
      else
      {
        setBorder(defaultBorder);
      }
      return this;
    }


}
