/*
A plugin for jEdit which implements java debugger functionality.
Copyright (C) 2005  Krishna Prakash Duggaraju

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

package debugger.core;

import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.Type;
import com.sun.jdi.Value;
import com.sun.jdi.event.LocatableEvent;

import java.util.StringTokenizer;

/**
 * Evlautes a given expression in the context of a stack frame.
 */
public final class ContextEvaluator 
{
  public ContextEvaluator(StackFrame stack)
  {
    stackframe = stack;
  }
  
  public ContextEvaluator(LocatableEvent event) throws IncompatibleThreadStateException
  {
    this(event.thread().frame(0));
  }
  
  public String evaluate(String expression) throws Exception
  {
    StringTokenizer tokenizer = new StringTokenizer(expression, ".");
    
    Value currentValue = null;
    
    while (tokenizer.hasMoreTokens())
    {
      String token = tokenizer.nextToken();
      if (token.equals("this"))
      {
        currentValue  = stackframe.thisObject();
      }
      else
      {
        if(currentValue == null)
        {
          LocalVariable variable = stackframe.visibleVariableByName(token);
          currentValue = stackframe.getValue(variable);
        }
        else
        {
          Type type = currentValue.type();
          if (type instanceof ReferenceType)
          {
            Field field = ((ReferenceType)type).fieldByName(token);
            currentValue = ((ObjectReference)currentValue).getValue(field);
          }
        }
      }
    }
    
    StringBuffer buffer = new StringBuffer(expression);
    buffer.append('=');    
    buffer.append(currentValue);
    return buffer.toString();
  }
  
  private StackFrame stackframe;
}