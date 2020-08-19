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

package debugger.core;

/** 
 * This class Represents an instance of Debugger.
 */


import debugger.event.EventListener;
import debugger.event.EventRequestListener;
import debugger.spec.EventSpec;

import java.util.List;
import java.util.Map;

public interface Debugger 
{
  /** 
   * Launch a program for debugging.
   * @param launchParams contains the paramters for launching.
   * @throws VMLaunchException when the debugger fails to launch the program.
   * @see com.sun.jdi.connect.LaunchingConnector
   */

  public void launch(Map launchParams) throws DebuggerException;

  /**
   * Attaches the debugger to a remote process.
   * @param attachParams
   * @throws debugger.core.DebuggerException
   */
  public void attach(Map attachParams) throws DebuggerException;

  /**
   * Stop debugging and detach from the debugee process. If the process was lauched
   * by the debugger then kill the process.
   */
  public void detach();

  /**
   * Indicates wether the debugger is running or not currnetly 
   */
  public boolean isRunning();
  
  public void suspend();
  public void resume();
  
  public void addEventListener(EventListener listener);
  public void removeEventListener(EventListener listener);
  
  public void addEventRequest(EventSpec event);
  public void removeEventRequest(EventSpec event);

  public void addEventRequestListener(EventRequestListener listener);
  public void removeEventRequestListener(EventRequestListener listener);

  public void enableEventRequest(EventSpec event);
  public void disableEventRequest(EventSpec event);
  
  public void stepIn();
  public void stepOut();
  public void stepOver();
  
  //IO Handling
  public void addEchoListener(OutputListener listener);
  public void removeEchoListener(OutputListener listener);

  public void addOutputListener(OutputListener listener);
  public void removeOutputListener(OutputListener listener);

  public void addErrorListener(OutputListener listener);
  public void removeErrorListener(OutputListener listener);
  
  public void outputLine(String line);
  //SourceMapping.
  public void setSourceMapper(SourceMapper mapper);
  public SourceMapper getSourceMapper();
  
  public List getTopLevelThreadGroups();
  public List getAllClasses();
}
