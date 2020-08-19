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

import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.StepRequest;

import debugger.event.EventAdapter;

import java.util.List;

public class StepHandler extends EventAdapter 
{
  ThreadReference curThread;

  public StepHandler()
  {
  }

  protected void step(int stepType)
  {
    if (curThread == null)
      return;
    clearPreviousStepRequest(curThread);
    int depth = StepRequest.STEP_LINE;
    VirtualMachine vm = curThread.virtualMachine();
    EventRequestManager evmgr = vm.eventRequestManager();
    StepRequest request = evmgr.createStepRequest(curThread, 
      depth , stepType);
    request.addCountFilter(1);
    request.enable();
    vm.resume();
  }

  public final void stepOver()
  {
      step(StepRequest.STEP_OVER);
  }

  public final void stepInto()
  {
      step(StepRequest.STEP_INTO);
  }

  public final void stepOut()
  {
      step(StepRequest.STEP_OUT);
  }

  public void clearPreviousStepRequest(ThreadReference trf)
  {
    EventRequestManager evmgr = trf.virtualMachine().eventRequestManager();
    List requests = evmgr.stepRequests();
    int size = requests.size();
    for (int i=0; i < size; i++) {
      StepRequest request = (StepRequest) requests.get(i);
      if (request.thread().equals(trf)) {
        evmgr.deleteEventRequest(request);
      }
    }
  }

  public void event (Event evt)
  {
    if (evt instanceof LocatableEvent)  {
      LocatableEvent levt = (LocatableEvent) evt;
      curThread = levt.thread();
    }else {
      curThread = null;
    }
  }

}
