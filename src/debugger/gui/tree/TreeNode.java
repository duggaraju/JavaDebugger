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

package debugger.gui.tree;

import javax.swing.tree.DefaultMutableTreeNode;

import java.util.List;
import java.util.Iterator;

/**
 * A Simple TreeNode class with support for lazy Loading of children.
 *
 */

public class TreeNode extends DefaultMutableTreeNode
{
  public static final Object DUMMY_NODE = "Unknown Object";
  boolean loaded = false;
  protected String name = null;

  public TreeNode(Object obj)
  {
    if (obj != null)
    {
      setUserObject(obj);
    }
    else
    {
      setUserObject(DUMMY_NODE);
      loaded = true;
    }
  }

  public final void addChildren(List list)
  {
    addChildren(list, 0);
  }

  public final void addChildren(List list, int startIndex)
  {
    Iterator itr = list.iterator();
    while (itr.hasNext())
    {
      addChild(itr.next(), startIndex++);
    }
    loaded = true;
  }

  public void addChild(Object obj, int index)
  {
    insert(TreeNodeFactory.createNode(obj), index);
  }

  public int getChildCount()
  {
    if (!loaded)
    {
      loaded = true;
      populateChildren();
    }
    int count = super.getChildCount();
    return count;
  }

  /**
   * Add the children to this node.
   * Subclasses need to overrride this method.
   */

  protected void populateChildren() {};

  public String toString()
  {
    if (name == null)
    {
      name = getUserObject().toString();
    }
    return name;
  }

  public void updateInfo(List list)
  {
    removeAllChildren();
    addChildren(list);
  }

  /** Tells whether the given node is leaf or not.
   *
   */

  public boolean isLeaf()
  {
    return false;
  }

  public final void reaload()
  {
    setLoaded(false);
  }
  
  protected void setLoaded(boolean value)
  {
    //If children were already loaded unload them.
    if (loaded)
    {
      removeAllChildren();
    }
    //set loaded to false so the are reloaded again.
    loaded  = value;
  }
  
  public boolean equals(Object obj)
  {
    if (obj instanceof TreeNode)
    {
      TreeNode node = (TreeNode)obj;
      return getUserObject().equals(node.getUserObject());
    }
    return false;
  }

  public TreeNode getChild(Object obj)
  {
    int count = getChildCount();
    for (int i=0; i < count; i++)
    {
      TreeNode node = (TreeNode) getChildAt(i);
      if (node.getUserObject().equals(obj)) {
        return node;
      }
    }
    return null;
  }

  /**
   * Get the Child which contains a given Object; Create one if not found.
   */

  public TreeNode getCreateChild(Object obj)
  {
    int count = getChildCount();
    TreeNode node;
    for (int i=0; i < count; i++)
    {
      node = (TreeNode) getChildAt(i);
      if (node.getUserObject().equals(obj)) {
        return node;
      }
    }
    node = TreeNodeFactory.createNode(obj);
    add(node);
    return node;
  }

  public TreeNode searchNode(Object obj)
  {
    java.util.Enumeration children = depthFirstEnumeration();
    while (children.hasMoreElements())
    {
      TreeNode node = (TreeNode)children.nextElement();
      if (node.getUserObject().equals(obj))
      {
        return node;
      }
    }
    return null;
  }
  
  public final Object getParentObject()
  {
    TreeNode parent = (TreeNode)getParent();
    return parent == null ? null : parent.getUserObject();
  }

}
