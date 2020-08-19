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
import java.awt.Component;

import debugger.gui.GUIUtils;
import debugger.gui.tree.TreeNode;
import debugger.gui.tree.MethodNode;
import debugger.gui.tree.FieldNode;
import debugger.gui.tree.ReferenceNode;


public class ClassPanelRenderer extends CellRenderer implements TreeCellRenderer
{

  protected static Icon packageIcon;
  protected static Icon classIcon;
  protected static Icon interfaceIcon;
  protected static Icon methodIcon;
  protected static Icon fieldIcon;
  protected static Icon constructorIcon;

  static
  {
    //Load the icons required.
    packageIcon = GUIUtils.createIcon("package");
    classIcon = GUIUtils.createIcon("class");
    methodIcon = GUIUtils.createIcon("method");
    constructorIcon = GUIUtils.createIcon("constructor");
    fieldIcon = GUIUtils.createIcon("field");
  }
  
  public Component getTreeCellRendererComponent(JTree tree, Object value,
    boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
      TreeNode node = (TreeNode)value;
      
      Icon icon = null;
      String text = node.toString();
      
      if (node instanceof ReferenceNode)
      {
        icon = classIcon;
      }
      else if (node instanceof MethodNode)
      {
        icon = methodIcon;
      }
      else if(node instanceof FieldNode)
      {
        icon = fieldIcon;
      }
      else
      {
        icon = packageIcon;
      }
      
      setText(text);
      setIcon(icon);
      isSelected = selected;
      
      return this;
    }

}
