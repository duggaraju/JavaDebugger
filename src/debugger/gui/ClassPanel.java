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

import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ArrayType;

import debugger.plugin.DebuggerMessage;
import java.util.StringTokenizer;
import java.util.List;

import java.awt.BorderLayout;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.JTree;
import javax.swing.JScrollPane;

import debugger.gui.tree.TreeNode;
import debugger.gui.tree.TreeNodeFactory;

import debugger.gui.renderer.ClassPanelRenderer;

public class ClassPanel  extends TabPanel
{
  public ClassPanel()
  {
    root = new TreeNode("");
    model = new DefaultTreeModel(root);
  }
  
  protected void createUI()
  {
    JTree tree = new JTree(model);
    tree.setRootVisible(false);
    tree.setShowsRootHandles(true);
    tree.setCellRenderer(new ClassPanelRenderer());

    JScrollPane scroll = new JScrollPane(tree);
    panel.add(scroll, BorderLayout.CENTER);    
  }

  private void clear()
  {
    root.removeAllChildren();
    model.nodeStructureChanged(root);
  }


  private void update(List list)
  {
    for (int i = 0; i < list.size(); i++)
    {
      Object obj = list.get(i);
      if (! (obj instanceof ArrayType))
      {
        ReferenceType rt = (ReferenceType) obj;
        String name = rt.name();
	if (name.indexOf('.') != -1)
	{
	  StringTokenizer tokenizer = new StringTokenizer(name, ".");
          TreeNode node = root;
          while (tokenizer.hasMoreTokens())
          {
            String token = tokenizer.nextToken();
            if (tokenizer.countTokens() != 0)
            {
              node = node.getCreateChild(token);
            }
	  }
	  node.getCreateChild(rt);
        }
	else
	{
	  root.getCreateChild(rt);
	}
      }
    }
  }

  public void vmDisconnectEvent(VMDisconnectEvent evt)
  {
    clear();
  }

  public void vmDeathEvent(VMDeathEvent evt)
  {
    clear();
  }

  public void locatableEvent(LocatableEvent event)
  {
    update(event.virtualMachine().allClasses());
    model.nodeStructureChanged(root);
  }
  
  protected void handleDebuggerMessage(DebuggerMessage message)
  {
    if (message.getReason() == DebuggerMessage.SESSION_INTERRUPTED)
    {
      update(message.getSession().getAllClasses());
    }
  }

  TreeNode root;
  DefaultTreeModel model;
}

