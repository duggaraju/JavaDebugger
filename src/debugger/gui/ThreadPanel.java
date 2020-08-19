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

import com.sun.jdi.Location;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadGroupReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.event.LocatableEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;

import debugger.gui.renderer.ThreadPanelRenderer;
import debugger.gui.tree.StackFrameNode;
import debugger.gui.tree.TreeNode;
import debugger.gui.treetable.JTreeTable;

import debugger.plugin.DebuggerMessage;
import debugger.plugin.EventDispatcher;
import debugger.plugin.SourceLocation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.View;

public class ThreadPanel  extends TabPanel
{
  private TreeNode root;
  private JPopupMenu threadPopupMenu;
  private JPopupMenu stackPopupMenu;
  
  private JTreeTable threadTree;
  private ThreadModel threadModel;

  public ThreadPanel()
  {
    root = new TreeNode(null);
    threadModel = new ThreadModel(root);
  }
  
  protected void createUI()
  {
    threadTree = new JTreeTable(threadModel);
    JTree tree = threadTree.getTree();
    tree.setCellRenderer(new ThreadPanelRenderer());
    tree.setRootVisible(false);
    tree.setShowsRootHandles(true);
    tree.setExpandsSelectedPaths(true);
    threadTree.addMouseListener(mouseHandler);
    
    JScrollPane scroll = new JScrollPane(threadTree);
    scroll.getViewport().setBackground(Color.white);
    panel.add(scroll, BorderLayout.CENTER);

    stackPopupMenu = GUIUtils.createPopupMenu("stack.popup", actions);    
    threadPopupMenu = GUIUtils.createPopupMenu("thread.popup", actions);    
  }

  /**
   * Clear the Thread tree after program termination. 
   */
  protected void clear()
  {
    root.removeAllChildren();
    threadModel.update(root);
  }

  private final void updateThreadList()
  {
    //update the thred tree
    List threadList = debugger.getTopLevelThreadGroups();
    root.updateInfo(threadList);
    threadModel.update(root);    
  }
  
  public void vmInterrupted()
  {
    updateThreadList();
  }
  
  public void locatableEvent(LocatableEvent evt)
  {
    updateThreadList();
    
    ThreadReference thread = evt.thread();
    ArrayList list = new ArrayList();
    list.add(thread);
    
    ThreadGroupReference tgr = thread.threadGroup();
    do
    {
      list.add(tgr);
      tgr = tgr.parent();
    }
    while( tgr != null);
    
    //Now we have path to current frame.
    int size = list.size();
    TreeNode[] path = new TreeNode[size + 1];
    TreeNode node = root;

    path[0] = node ;
    for (int i = 0; i < size ; i++)
    {
      node = node.getChild(list.get(size - (i +1 )));
      path[i + 1] = node;
    }

    TreePath treepath = new TreePath(path);
    setSelection(treepath);
    
  }


  private final void setSelection(final TreePath treepath)
  {
    
    SwingUtilities.invokeLater( new Runnable()
    {
      public void run()
      {
        JTree tree = threadTree.getTree();
        tree.expandPath(treepath);
        int row = tree.getRowForPath(treepath);
        if (row != -1)
        {
          row++;
          GUIUtils.makeCellVisible(threadTree, row, 0);
          threadTree.setRowSelectionInterval(row, row);
        }
      }
    });
  }
  
  private final void sendStackChangedMessage(View view, StackFrame frame)
  {
    if (frame != null)
    {
      EditBus.send(
        new DebuggerMessage(view, DebuggerMessage.STACK_FRAME_CHANGED, frame));
    }
  }
  
  private final void sendSourceMessage(View view, StackFrame frame)
  {
    Location location = frame.location();
    SourceLocation srcLocation = EventDispatcher.getDebuggerLocation(debugger, location);
    EditBus.send(new DebuggerMessage(view, DebuggerMessage.SHOW_SOURCE, srcLocation));     
  }

  public void vmDeathEvent(VMDeathEvent evt)
  {
    clear();
  }

  public void vmDisconnectEvent(VMDisconnectEvent evt)
  {
    clear();
  }

  private final StackFrame getSelectedFrame()
  {
    TreeNode node = (TreeNode) threadTree.getTree().getSelectionPath().getLastPathComponent();
    Object obj = node.getUserObject();
    if (obj instanceof StackFrame)
    {
      return (StackFrame)obj;
    }
    return null;
  }
  
  protected JPopupMenu getPopupMenu(MouseEvent event)
  {
    TreeNode node = (TreeNode) threadTree.getTree().getSelectionPath().getLastPathComponent();
    if (node instanceof StackFrameNode)
    {
      return stackPopupMenu;
    }
    else
    {
      return threadPopupMenu;
    }
  }
  
  private final class ThreadAction extends EditAction
  {
    public ThreadAction(String name)
    {
      super(name);
    }
    public String getCode()
    {
      return null;
    }
    
    public void invoke(View view)
    {
      String name = getName();
      if (name.equals(SOURCE_FRAME))
      {
        sendSourceMessage(view, getSelectedFrame());
      }
      else if (name.equals(POPUP_FRAME))
      {
        StackFrame stackFrame = getSelectedFrame();
        ThreadReference thread = stackFrame.thread();
        try
        {
          List frames = thread.frames();
          int index = frames.indexOf(stackFrame);
          if (index == 0 )
          {
            return;
          }
          StackFrame newFrame = (StackFrame) frames.get(index - 1);
          thread.popFrames(newFrame);

          TreePath newPath = threadTree.getTree().getSelectionPath().getParentPath();
          int row = threadTree.getTree().getRowForPath(newPath);
          TreeNode threadNode = (TreeNode) newPath.getLastPathComponent();
          threadNode.reaload();
          threadModel.update(threadNode);
          
          setSelection(newPath);
          stackFrame = (StackFrame)thread.frame(0);
          sendSourceMessage(view, stackFrame);
          sendStackChangedMessage(view, stackFrame);        
        }
        catch(Exception ex)
        {
          ex.printStackTrace();
        }
      }
      else if (name.equals(SELECT_FRAME))
      {
        sendStackChangedMessage(view, getSelectedFrame());        
      }
    }
  }
  
  public void handleDebuggerMessage(DebuggerMessage message)
  {
    if (message.getReason() == DebuggerMessage.SESSION_INTERRUPTED)
    {
      threadTree.setEnabled(true);
      updateThreadList();
    }
    else if (message.getReason() == DebuggerMessage.SESSION_RESUMED)
    {
      threadTree.setEnabled(false);
    }
  }
  
  protected void createActions()
  {
    actions.addAction(new ThreadAction(SELECT_FRAME));
    actions.addAction(new ThreadAction(SOURCE_FRAME));
    actions.addAction(new ThreadAction(POPUP_FRAME));
  }
  
  static final String SOURCE_FRAME = "source-frame";
  static final String POPUP_FRAME = "popup-frame";
  static final String SELECT_FRAME = "select-frame";
  
}
