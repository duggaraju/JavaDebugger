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

import debugger.gui.tree.StackFrameNode;
import debugger.gui.tree.TreeNode;
import debugger.gui.treetable.AbstractTreeTableModel;
import debugger.gui.treetable.TreeTableModel;

import org.gjt.sp.jedit.jEdit;

public class ThreadModel extends AbstractTreeTableModel 
{

  private static final int COLUMN_COUNT = 3;
  
  static final String[] COLUMN_NAMES = 
  {
    "thread.column1",
    "thread.column2",
    "thread.column3"
  };
  
  static final Class[] COLUMN_CLASS = 
  {
    TreeTableModel.class,
    String.class,
    String.class
  };
  
  static String[] columnNames = new String[COLUMN_COUNT];
  
  static
  {
    for (int i = 0; i < COLUMN_COUNT ; i++)
    {
      columnNames[i] = jEdit.getProperty(COLUMN_NAMES[i], COLUMN_NAMES[i]);
    }
  }
  
  public ThreadModel(Object root)
  {
    super(root);
  }
  
  public int getColumnCount()
  {
    return COLUMN_COUNT;
  }
  
  public Class getColumnClass(int index)
  {
    return COLUMN_CLASS[index];
  }
  
  public String getColumnName(int index)
  {
    return columnNames[index];
  }
  
  
  public Object getValueAt(Object parent, int index)
  {
    String value = null;
    if (parent instanceof StackFrameNode)
    {
      StackFrameNode node = (StackFrameNode) parent;
      switch(index)
      {
        case 0:
          value = node.getLocation();
          break;
        case 1:
          value = node.getClassName();
          break;
        case 2:
          value = node.getMethod();
          break;
      }
    }
    return value;
  }
  
  public Object getChild(Object parent, int index)
  {
    TreeNode node = (TreeNode)parent;
    return node.getChildAt(index);
  }
  
  public int getChildCount(Object parent)
  {
    TreeNode node = (TreeNode)parent;
    return node.getChildCount();
  }
 
  public final void update(TreeNode node)
  {
    Object[] path = node.getPath();
    fireTreeStructureChanged(this, path, null, null);
  }
  

}