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

import com.sun.jdi.LocalVariable;
import com.sun.jdi.StackFrame;

import com.sun.jdi.ObjectReference;
import com.sun.jdi.AbsentInformationException;

import com.sun.jdi.Value;
import java.util.List;


public class DebugStackFrameNode extends StackFrameNode
{


  public DebugStackFrameNode(StackFrame frame)
  {
    super(frame);
    setFrame(frame);
  }



  public void setFrame(StackFrame frame)
  {
    //If the node was loaded, remove all the children.
    setLoaded(false);
    
    if (frame != null)
    {
      setUserObject(frame);
    }
    else
    {
      setUserObject(DUMMY_NODE);
    }
  }
  
  public void populateChildren()
  {
    if (getStackFrame() != null)
    {
      addChildren();
    }
    
  }
  
  private void addChildren()
  {
    int next = 0;
    StackFrame frame = getStackFrame();
    ObjectReference thisObj = frame.thisObject();
    if (thisObj != null )
    {
      next = 1;
      insert(new ValueNode("this", thisObj, null), 0);
    } 
    
    try
    {
      List list = frame.visibleVariables();
      addChildren(list, next);
    }
    catch (AbsentInformationException ex)
    {
    }
  }
  
  public void addChild(Object obj, int index)
  {
    LocalVariable variable = (LocalVariable)obj;
    Value value = getStackFrame().getValue(variable);
    String name = variable.name();
    insert(new ValueNode(name, value, variable), index);
  }

  public boolean isLeaf()
  {
    return false;
  }

  public boolean equals(Object other)
  {
    if (other instanceof StackFrameNode)
    {
      StackFrameNode node = (StackFrameNode)other;
      return getStackFrame() == node.getStackFrame();
    }
    return false;
  }

}
