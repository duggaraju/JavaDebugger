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

package debugger.options;


public interface DebuggerOptions 
{

  public static final String VM_NAME = "options.jdebugger.vmName";
  public static final String VM_ARGS = "options.jdebugger.vmArgs";  
  
  public static final String CLASS_NAME = "options.jdebugger.className";  
  public static final String CLASS_ARGS = "options.jdebugger.classArgs";  
  
  public static final String CLASSPATH = "options.jdebugger.classpath";
  public static final String SOURCEPATH = "options.jdebugger.sourcepath";
  public static final String CUSTOM_CLASSPATH = "options.jdebugger.custompath";
  
  public static final String PROGRAM_TYPE = "options.jdebugger.programType"; 
  public static final String APPLET_CLASS = "options.jdebugger.appletClass";

  public static final String SHOW_STARTUP = "options.jdebugger.showStartup";
  public static final String SHOW_TOOLTIP =  "options.jdebugger.showTooltip";
  
  public static final String SHOWTAB = "options.jdebugger.showtab_";
}
