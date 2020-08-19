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

package debugger.spec;

import com.sun.jdi.Field;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.request.EventRequestManager;

public final class WatchpointSpec extends EventSpec
{
  private String fieldName;
  private boolean stopOnAccess = false;

  public WatchpointSpec(String className, String memberName)
  {
    this (className, memberName, false);
  }
  
  public WatchpointSpec(String className, String memberName, boolean stopOnAccess)
  {
    super(className);
    this.stopOnAccess = stopOnAccess;
    fieldName = memberName;
  }

  public boolean matches(ReferenceType rt)
  {
    return rt.name().equals(klass);
  }

  public void createRequest(ReferenceType rt) throws Exception
  {
    Field field = rt.fieldByName(fieldName);
    if (field == null)
    {
      throw new Exception("No such field " + fieldName + " in class " + klass);
    }
    EventRequestManager mgr = rt.virtualMachine().eventRequestManager();
    if (stopOnAccess)
    {
      request = mgr.createAccessWatchpointRequest(field);
    }
    else
    {
      request = mgr.createModificationWatchpointRequest(field);
    }
  }
  
  public final boolean equals(Object other)
  {
    if (other instanceof WatchpointSpec)
    {
      WatchpointSpec watch = (WatchpointSpec)other;
      return fieldName.equals(watch.fieldName) 
        &&  (klass.equals(watch.klass)) && (stopOnAccess == watch.stopOnAccess);
    }
    return false;
  }

  public final String getFieldName()
  {
    return fieldName;
  }
  
  public String toString()
  {
    return "Watchpoint on " + fieldName + " of " + klass;
  }
  
}
