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

package debugger.gui.renderer;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.UIManager;

import java.awt.Color;
import java.awt.Graphics;

public class CellRenderer extends JLabel
{

  protected boolean isSelected = false;
  protected Color selectionColor = UIManager.getColor("Tree.selectionBackground");
  

  /**
  * paint is subclassed to draw the background correctly.  JLabel
  * currently does not allow backgrounds other than white, and it
  * will also fill behind the icon.  Something that isn't desirable.
  */
  public void paint(Graphics g)
  {
    Color bColor = Color.white;   
    if(isSelected )
    {
      bColor = selectionColor;
    }
    else if(getParent() != null)
    {
      bColor = getParent().getBackground();
    }
    
    g.setColor(bColor);
    
    Icon  currentI = getIcon();
    if(currentI != null && getText() != null)
    {
      int offset = (currentI.getIconWidth() + getIconTextGap());
      g.fillRect(offset, 0, getWidth() - 1 - offset, getHeight() - 1);
    }
    else
    {
      g.fillRect(0, 0, getWidth()-1, getHeight()-1);
    }
    super.paint(g);
  }

}
