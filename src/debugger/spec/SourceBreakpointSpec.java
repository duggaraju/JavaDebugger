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

import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.request.EventRequestManager;

import java.io.File;
import java.util.List;

public class SourceBreakpointSpec extends BreakpointSpec
{
  String filename;
  int lineNumber;

  public SourceBreakpointSpec(String klass, String file, int line)
  {
    super(klass);
    filename = file;
    lineNumber = line;
  }
  
  public SourceBreakpointSpec(String file, int line)
  {
    this (null, file, line);
    
    int startIndex = file.lastIndexOf(File.separatorChar);
    startIndex = (startIndex == -1) ? 0 : startIndex + 1;
      
    int endIndex = file.lastIndexOf('.');
    if (endIndex == -1)
      endIndex = file.length();
      
    klass = "*" + file.substring(startIndex, endIndex);
  }

  public boolean matches(ReferenceType rt)
  {
    try
    {
      String source = rt.sourceName();
      return filename.endsWith(source);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      return rt.name().startsWith(klass);
    }
  }

  public void createRequest(ReferenceType rt) throws Exception
  {
    List locs = rt.locationsOfLine(lineNumber);
    if (locs.size() > 0)
    {
      Location loc = (Location)locs.get(0);
      EventRequestManager evmgr = rt.virtualMachine().eventRequestManager();
      request = evmgr.createBreakpointRequest(loc);
    }
  }

  public final String filename()
  {
    return filename;
  }

  public final int lineNumber()
  {
    return lineNumber;
  }

  public boolean equals(Object obj)
  {
    if (obj instanceof SourceBreakpointSpec)
    {
      SourceBreakpointSpec other = (SourceBreakpointSpec) obj;
      return ( other.lineNumber == lineNumber && other.filename.equals(filename) );
    }
    return false;
  }

  public String toString()
  {
    return "Breakpoint at " + filename + ":" + lineNumber;
  }
}
