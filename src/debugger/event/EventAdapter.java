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

package debugger.event;

import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.ClassUnloadEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.ThreadDeathEvent;
import com.sun.jdi.event.ThreadStartEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.event.WatchpointEvent;

public abstract class EventAdapter implements EventListener
{
  public void vmStartEvent(VMStartEvent event) {}

  public void vmDeathEvent(VMDeathEvent event) {}
  public void vmDisconnectEvent(VMDisconnectEvent event) {}

  public void threadStartEvent(ThreadStartEvent event) {}
  public void threadDeathEvent(ThreadDeathEvent event) {}

  public void classPrepareEvent(ClassPrepareEvent event) {}
  public void classUnloadEvent(ClassUnloadEvent event) {}

  public void breakpointEvent(BreakpointEvent event) {}
  public void watchpointEvent(WatchpointEvent event) {}
  public void exceptionEvent(ExceptionEvent event) {}

  public void stepEvent(StepEvent event) {}
  public void methodEntryEvent(MethodEntryEvent event) {}
  public void methodExitEvent(MethodExitEvent event) {}

  public void event(Event event) {}
  public void locatableEvent(LocatableEvent event) {}

  public void vmInterrupted() {}
  public void vmSuspended() {}
}
