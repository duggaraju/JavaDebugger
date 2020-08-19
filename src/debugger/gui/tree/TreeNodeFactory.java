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
import com.sun.jdi.ThreadGroupReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.Field;
import com.sun.jdi.Method;
import com.sun.jdi.Value;

public abstract class TreeNodeFactory
{
  public static TreeNode createNode(Object obj)
  {
    if (obj instanceof ThreadGroupReference)
    {
      return new ThreadGroupNode((ThreadGroupReference) obj);
    }
    else if (obj instanceof ThreadReference)
    {
      return new ThreadNode((ThreadReference) obj);
    }
    else if (obj instanceof StackFrame)
    {
      return new StackFrameNode((StackFrame)obj);
    }
    else if (obj instanceof Field)
    {
      return new FieldNode( (Field) obj);
    }
    else if (obj instanceof Method)
    {
      return new MethodNode((Method) obj);
    }
    else if (obj instanceof ReferenceType)
    {
      return new ReferenceNode((ReferenceType) obj);
    }
    else if (obj instanceof LocalVariable)
    {
      return new LocalVariableNode((LocalVariable)obj);
    }
    return new TreeNode(obj);
  }

  public static TreeNode createNode(String name, Object obj)
  {
    if (obj instanceof Value)
    {
      return new ValueNode(name, (Value) obj, null);      
    }
    return new TreeNode(obj);
  }

}
