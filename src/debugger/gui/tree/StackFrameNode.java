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

import com.sun.jdi.Location;
import com.sun.jdi.StackFrame;


public class StackFrameNode extends TreeNode
{

  public StackFrameNode(StackFrame frame)
  {
    super(frame);
    if (frame != null)
      updateValues();
  }

  public boolean isLeaf()
  {
    return true;
  }
  
  public final StackFrame getStackFrame()
  {
    if (getUserObject() instanceof StackFrame)
    {
      return (StackFrame)getUserObject();
    }
    return null;
  }
  
  public boolean equals(Object other)
  {
    if (other instanceof StackFrameNode)
    {
      return getUserObject() == ((StackFrameNode)other).getUserObject();
    }
    return false;
  }

  private final void updateValues()
  {
    Location stackLoc = getStackFrame().location();
    className = stackLoc.declaringType().name();
    methodName = stackLoc.method().name();
    try
    {
      location = stackLoc.sourceName() + ':' + stackLoc.lineNumber();
    }
    catch(Exception e)
    {
      location = stackLoc.toString();  
    }
  }
  
  public final String getClassName()
  {
    return className;
  }
  
  public final String getMethod()
  {
    return methodName;
  }
  
  public final String getLocation()
  {
    return location;
  }
  
  public String toString()
  {
    return location;
  }
  
  private String className = null;
  private String methodName = null;
  private String location = null;
}
