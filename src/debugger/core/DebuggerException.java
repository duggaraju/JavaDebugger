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

package debugger.core;

public class DebuggerException extends Exception 
{
  public DebuggerException(String message)
  {
    super(message);
  }
  
  public DebuggerException(Throwable throwable)
  {
    super(throwable);
  }
  
  public DebuggerException(String message, Throwable throwable)
  {
    super(message, throwable);
  }
}