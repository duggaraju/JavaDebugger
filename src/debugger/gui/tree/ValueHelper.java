/*
A plugin for jEdit which implements java debugger functionality.
Copyright (C) 2004  Krishna Prakash Duggaraju

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

import com.sun.jdi.CharType;
import com.sun.jdi.ClassType;
import com.sun.jdi.DoubleType;
import com.sun.jdi.FloatType;
import com.sun.jdi.IntegerType;
import com.sun.jdi.LongType;
import com.sun.jdi.ShortType;
import com.sun.jdi.Type;
import com.sun.jdi.Value;

public final class ValueHelper 
{

  public static Value createValue(Type type, String value)
  {
    Value retValue = null;
    if (type instanceof IntegerType)
    {
      int intValue = Integer.valueOf(value).intValue();  
      retValue = type.virtualMachine().mirrorOf(intValue);
    }
    else if (type instanceof FloatType)
    {
      float floatValue = Float.valueOf(value).floatValue();
      retValue = type.virtualMachine().mirrorOf(floatValue);
    }
    else if (type instanceof DoubleType)
    {
      float doubleValue = Double.valueOf(value).floatValue();
      retValue = type.virtualMachine().mirrorOf(doubleValue);      
    }
    else if (type instanceof LongType)
    {
      float longValue = Long.valueOf(value).longValue();
      retValue = type.virtualMachine().mirrorOf(longValue);
    }
    else if (type instanceof ShortType)
    {
      short shortValue = Short.valueOf(value).shortValue();
      retValue = type.virtualMachine().mirrorOf(shortValue);
    }
    else if (type instanceof CharType)
    {
      char charValue = value.charAt(0);
      retValue = type.virtualMachine().mirrorOf(charValue);      
    }
    else if (type instanceof ClassType)
    {
      if (type.name().equals("java.lang.String"))
      {
        retValue = type.virtualMachine().mirrorOf(value);
      }
    }
    return retValue;
  }
}