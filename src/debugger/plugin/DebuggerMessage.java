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

package debugger.plugin;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.EBMessage;

import debugger.core.Debugger;
import debugger.JavaDebuggerPlugin;

public class DebuggerMessage extends EBMessage
{
  
  public static final Object SESSION_STARTING = "SESSION STARTING";
  public static final Object SESSION_STARTED = "SESSION STARTED";
  public static final Object SESSION_TERMINATED = "SESSION STOPPED";
  public static final Object SESSION_INTERRUPTED = "SESSION INTERRUPTED";
  public static final Object SESSION_RESUMED = "SESSION RESUMED";
  public static final Object SHOW_SOURCE = "SHOW SOURCE";
  public static final Object EVENT_HIT = "EVENT_HIT";
  public static final Object STACK_FRAME_CHANGED = "STACK FRAME CHANGED";

  private final Object what;
  private final Object info;

  public DebuggerMessage(View source, Object message, Object additionalInfo)
  {
    super(source);
    what = message;
    info = additionalInfo;
  }
  
  public final View getView()
  {
    return (View)getSource();
  }
  
  public final Debugger getSession()
  {
    return JavaDebuggerPlugin.getPlugin().getDebuggerManager(getView()).getDebugger();
  }

  public final Object getReason()
  {
    return what;
  }
  
  public final Object getInfo()
  {
    return info;
  }
  
  public final String toString()
  {
    return what.toString();
  }
}
