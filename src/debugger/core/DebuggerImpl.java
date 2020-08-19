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


import com.sun.jdi.Bootstrap;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.request.EventRequestManager;

import debugger.event.EventHandler;
import debugger.event.EventListener;
import debugger.event.EventRequestListener;

import debugger.spec.EventSpec;
import debugger.spec.SourceBreakpointSpec;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DebuggerImpl implements Debugger
{

  static final int MODE_LAUNCH = 1;
  static final int MODE_ATTACH = 2;
  static final int MODE_CONNECT = 3;
  
  static VirtualMachineManager vmmgr;

  protected boolean running = false;

  private VirtualMachine vmachine;
  private Connector  connector;

  private EventHandler ehandler;
  private StepHandler stepHandler;
  private IORedirector ior;
  private EventRequestHandler erqhandler;
  private SourceMapper sourceMapper;

  private static VirtualMachineManager getVirtualMachineManager()
  {
    if (vmmgr == null)
    {
      vmmgr = Bootstrap.virtualMachineManager();    
    }
    return vmmgr;
  }

  public DebuggerImpl()
  {
    ehandler = new EventHandler();
    stepHandler = new StepHandler();
    ior = new IORedirector();
    erqhandler = new EventRequestHandler();
    ehandler.addEventListener(stepHandler);
    ehandler.addEventListener(erqhandler);
  }

  public final boolean isRunning()
  {
    return ehandler.isConnected();
  }
  
  private static final Map getConnectorParameters(Connector connector, Map params)
  {
    Map map = connector.defaultArguments();

    Iterator keyIter = params.keySet().iterator();
    while (keyIter.hasNext())   
    {
      String key = (String) keyIter.next();
      Connector.Argument argument = (Connector.Argument)map.get(key);
      argument.setValue((String) params.get(key));
    }
    
    return map;

  }
  
  /** 
   * Launch a program for debugging.
   * @param launchParams contains the paramters for launching.
   * @throws VMLaunchException when the debugger fails to launch the program.
   * @see com.sun.jdi.connect.LaunchingConnector
   */

  public void launch(Map launchParams) throws DebuggerException
  {
    connector = getVirtualMachineManager().defaultConnector();
    Map map = getConnectorParameters(connector, launchParams);
    
    try
    {
      vmachine = ((LaunchingConnector)connector).launch(map);
    }
    catch (Exception ex)
    {
      throw new DebuggerException(ex);
    }

    doStartupProcessing();
  }

  public void attach(Map attachParams) throws DebuggerException
  {
    connector = (Connector) getVirtualMachineManager().attachingConnectors().get(0);
    Map map = getConnectorParameters(connector, attachParams);
    
    try
    {
      vmachine = ((AttachingConnector)connector).attach(map);
    }
    catch (Exception ex)
    {
      throw new DebuggerException(ex);
    }

    doStartupProcessing();
  }

  public void listen(Map listenParams)
  {
  }

  private final void doStartupProcessing()
  {
    ehandler.startHandler(vmachine, false);
    Process process = vmachine.process();
    if (process != null)
    {
      ior.setProcess(process);
    }

  }

  public void detach()
  {
    
    //Detach from process I/O
    ior.detach();

    //Stop the process.
    try
    {
      if (connector instanceof LaunchingConnector)
      {
        vmachine.exit(1);
      }
      else if (connector instanceof AttachingConnector)
      {
        vmachine.dispose();
      }
      else
      {
        //TBD
      }
    }
    catch (VMDisconnectedException ex)
    {
      //Cannot handle this.
    }

    erqhandler.reset();
    vmachine = null;
    
  }

  public final VirtualMachine virtualMachine()
  {
    return vmachine;
  }

  public final EventRequestManager eventRequestManager()
  {
    return vmachine.eventRequestManager();
  }

  public final EventHandler eventHandler()
  {
    return ehandler;
  }
  public final StepHandler stepHandler()
  {
    return stepHandler;
  }

  public final IORedirector getIORedirector()
  {
    return ior;
  }

  public final EventRequestHandler getEventRequestHandler()
  {
    return erqhandler;
  }

  public final void addEventListener(EventListener lsnr)
  {
    ehandler.addEventListener(lsnr);
  }

  public final void removeEventListener(EventListener lsnr)
  {
    ehandler.removeEventListener(lsnr);
  }

  public final void addEventRequest(EventSpec event)
  {
    if (event instanceof SourceBreakpointSpec)
    {
      SourceBreakpointSpec sbp = (SourceBreakpointSpec) event;
      sbp.setClassName(sourceMapper.getClassNameForFile(sbp.filename()));
    }
    erqhandler.addEventRequest(event);
  }

  public final void removeEventRequest(EventSpec event)
  {
    erqhandler.removeEventRequest(event);
  }
  
  public final void addEventRequestListener(EventRequestListener listener)
  {
    erqhandler.addEventRequestListener(listener);
  }
  
  public final void removeEventRequestListener(EventRequestListener listener)
  {
    erqhandler.removeEventRequestListener(listener);
  }

  public final void enableEventRequest(EventSpec event)
  {
    erqhandler.enableEventRequest(event);
  }
  
  public final void disableEventRequest(EventSpec event)
  {
    erqhandler.disableEventRequest(event);
  }

  public final void stepOver()
  {
    stepHandler.stepOver();
  }
  
  public final void stepIn()
  {
    stepHandler.stepInto();
  }
  
  public final void stepOut()
  {
    stepHandler.stepOut();
  }
  
  public final void resume()
  {
    vmachine.resume();
  }
  
  public final void suspend()
  {
    vmachine.suspend();
  }
  
  
  //IO Handling
  public void addEchoListener(OutputListener listener)
  {
    ior.addEchoListener(listener);
  }
  public void removeEchoListener(OutputListener listener)
  {
    ior.removeEchoListener(listener);
  }

  public void addOutputListener(OutputListener listener)
  {
    ior.addOutputListener(listener);
  }
  public void removeOutputListener(OutputListener listener)
  {
    ior.removeOutputListener(listener);
  }

  public void addErrorListener(OutputListener listener)
  {
    ior.addErrorListener(listener);
  }
  
  public void removeErrorListener(OutputListener listener)
  {
    ior.removeErrorListener(listener);
  }

  public void outputLine(String line)
  {
    ior.outputLine(line);
  }
  
  public SourceMapper getSourceMapper()
  {
    return sourceMapper;
  }
  
  public void setSourceMapper(SourceMapper mapper)
  {
    sourceMapper = mapper;
  }
  
  public List getTopLevelThreadGroups()
  {
    return vmachine.topLevelThreadGroups();
  }
  
  public List getAllClasses()
  {
    return vmachine.allClasses();
  }
  
}
