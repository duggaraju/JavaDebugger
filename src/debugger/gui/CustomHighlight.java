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

package debugger.gui;

import javax.swing.SwingUtilities;

import org.gjt.sp.jedit.textarea.JEditTextArea;
import org.gjt.sp.jedit.textarea.TextAreaExtension;

public abstract class CustomHighlight extends TextAreaExtension implements Runnable
{

  protected JEditTextArea textarea = null;

  public CustomHighlight(JEditTextArea ta)
  {
    textarea = ta;
  }


  /** Helper routine to force redraw of a line */
  public final void redraw(int lineno)
  {
    textarea.invalidateLine(lineno);
    try
    {
      SwingUtilities.invokeLater(this);
    }
    catch(Exception ex) 
    {
    }
  }

  public void run()
  {
    textarea.repaint();
  }

}
