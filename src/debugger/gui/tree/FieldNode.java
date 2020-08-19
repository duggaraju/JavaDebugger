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

import com.sun.jdi.Field;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.PrimitiveType;
import com.sun.jdi.Value;

public class FieldNode extends TreeNode implements TreeTableNode
{

  public FieldNode(Field fld)
  {
    super(fld);
    field = fld;
    name = fld.typeName() + ' ' + fld.name();
  }

  public boolean isLeaf()
  {
    Value value = getFieldValue();
    return value == null ? true : value.type() instanceof PrimitiveType;
  }
  
  public String getName()
  {
    return field.name();
  }
  
  public String getType()
  {
    return field.typeName();
  }
  
  public String getValue()
  {
    Value value = getFieldValue();
    return value == null ? "null" : value.toString();
  }
  
  public final Value getFieldValue()
  {
    if (fieldValue != null)
    {
      Object parent = getParentObject();
      if (parent != null && parent instanceof ObjectReference)
      {
        ObjectReference reference = (ObjectReference)parent;
        fieldValue = reference.getValue(field);
      }
    }
    return fieldValue;
  }
  
  public final void setValue(String value)
  {
    Value mirrorValue = ValueHelper.createValue(fieldValue.type(), value);
    Object parent = getParentObject();
    if (parent != null && parent instanceof ObjectReference)
    {
      ObjectReference reference = (ObjectReference)parent;
      try
      {
        reference.setValue(field, mirrorValue);
      }
      catch(Exception ex)
      {
      }
    }    
  }
  private Value fieldValue;
  private Field field;
}
