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

import com.sun.jdi.ReferenceType;
import com.sun.jdi.ArrayType;


public class ReferenceNode extends TreeNode
{

  private boolean isArray = false;

  public ReferenceNode(ReferenceType ref)
  {
    super(ref);
    name = ref.name();
    int index = name.lastIndexOf('.');
    if (index != -1)
    {
      name = name.substring(index+1);
    }
    if (ref instanceof ArrayType)
      isArray = true;
  }

  public void populateChildren()
  {
    ReferenceType rt = (ReferenceType) getUserObject();
    try
    {
      addChildren(rt.allFields());
      addChildren(rt.allMethods());
    }
    catch(Exception ex)
    {
      name += "<Class not yet Loaded !!>";
      return;
    }
  }

  public boolean isLeaf()
  {
    return isArray;
  }

}
