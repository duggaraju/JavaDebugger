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

import com.sun.jdi.PrimitiveValue;
import com.sun.jdi.Value;
import javax.swing.table.TableCellRenderer;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.UIManager;

import java.awt.Component;

import debugger.gui.GUIUtils;
import debugger.gui.tree.ValueNode;


public class TreeTableRenderer extends CellRenderer implements TableCellRenderer
{

  protected static Icon threadIcon;
  protected static Icon threadGroupIcon;
  protected static Icon stackIcon;
  protected static Icon objectIcon;
  protected static Icon primitiveIcon;
  
 
  public TreeTableRenderer()
  {
    selectionColor = UIManager.getColor("Tree.selectionBackground");
  }
  
  static
  {
    objectIcon = GUIUtils.createIcon("object");
    primitiveIcon = GUIUtils.createIcon("primitive");
  }

  public Component getTableCellRendererComponent(JTable table, Object value,
    boolean selected, boolean hasFocus, int row, int col)
    {
      if (col == 0)
      {
        if (value instanceof ValueNode)
        {
          ValueNode node = (ValueNode) value;
          setText(node.getName());
          Value nodeValue = ((ValueNode)value).getNodeValue();
          if (value instanceof PrimitiveValue)
          {
            setIcon(primitiveIcon);            
          }
          else
          {
            setIcon(objectIcon);
          }
        }
      }
      else
      {
        setText(value.toString());
      }

      isSelected = selected;
      if (selected)
      {
        setBackground(selectionColor);
      }

      return this;
    }

}
