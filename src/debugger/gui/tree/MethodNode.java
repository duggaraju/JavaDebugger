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

import com.sun.jdi.Method;
import java.util.Iterator;
import java.util.List;

public class MethodNode extends TreeNode
{

  public MethodNode(Method method)
  {
    super(method);
    StringBuffer buffer = new StringBuffer();
    buffer.append(method.returnTypeName()).append(' ');
    buffer.append(method.name()).append('(');
    List args = method.argumentTypeNames();
    Iterator itr = args.iterator();
    if (itr.hasNext())
    {
      buffer.append(itr.next());
    }
    while (itr.hasNext())
    {
      buffer.append(',').append(itr.next());
    }
    buffer.append(')');
    name = buffer.toString();
  }

  public final boolean isLeaf()
  {
    return true;
  }
}
