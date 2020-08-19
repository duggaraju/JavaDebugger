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

import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMStartEvent;

import debugger.core.OutputListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.gjt.sp.jedit.ActionSet;
import org.gjt.sp.jedit.View;

public class IOPanel  extends TabPanel  implements ActionListener
{

  JTextPane output;
  JTextField input;
  
  OutputReader inputListener;
  OutputReader outputListener;
  OutputReader errorListener;
  
  StyledDocument document;
  StyleContext context;
  
  JPopupMenu popupMenu;
  
  public IOPanel()
  {}
  
  protected void createUI()
  {
    input = new JTextField();
    input.addActionListener(this);
    
    document = new DefaultStyledDocument();
    createStyles();
    
    output = new JTextPane(document);
    output.setEditable(false);
    JScrollPane scroll = new JScrollPane(output);

    panel.add(BorderLayout.CENTER, scroll);
    panel.add(BorderLayout.SOUTH, input);
    
    popupMenu = GUIUtils.createPopupMenu(IO_POPUP, actions);
    output.addMouseListener(mouseHandler);    
  }
  
  public void vmStartEvent(VMStartEvent evt)
  {
    _addListeners();
  }
  
  public void vmDeathEvent(VMDeathEvent evt)
  {
    _removeListeners();
  }
  
  private final void createStyles()
  {
    context = new StyleContext();
    Style inputStyle = context.addStyle("input", null);
    StyleConstants.setForeground(inputStyle, Color.blue);
    StyleConstants.setItalic(inputStyle, true);
    
    Style outputStyle = context.addStyle("output", null);
    StyleConstants.setForeground(outputStyle, Color.black);

    Style errorStyle = context.addStyle("error", null);
    StyleConstants.setForeground(errorStyle, Color.red);    
  }
  
  private void _addListeners()
  {
    inputListener = new OutputReader(context.getStyle("input"));
    debugger.addEchoListener(inputListener);
    
    outputListener = new OutputReader(context.getStyle("output"));
    debugger.addOutputListener(outputListener);

    errorListener = new OutputReader(context.getStyle("error"));    
    debugger.addErrorListener(errorListener);    
  }
  
  private void _removeListeners()
  {
    debugger.removeEchoListener(inputListener);
    inputListener = null;
    
    debugger.removeOutputListener(outputListener);
    outputListener = null;
    
    debugger.removeErrorListener(errorListener);
    errorListener = null;
  }

  public void actionPerformed(ActionEvent evt)
  {
    debugger.outputLine(input.getText());
    input.setText("");
  }
  
  
  protected JPopupMenu getPopupMenu(MouseEvent evt)
  {
     return popupMenu;
  }

  public class OutputReader 
    implements OutputListener.STDERRListener, OutputListener.STDINListener, OutputListener.STDOUTListener
  {
    Style style;
    public OutputReader(Style lineStyle)
    {
      style = lineStyle;
    }

    public void outputLine(String line)
    {
      try
      {
        document.insertString(document.getLength(), line + '\n', style);
      }
      catch(Exception ex){}
    }
  }
  
  protected void createActions()
  {
    actions = new ActionSet(IO_POPUP);
    actions.addAction(new AbstractAction(IO_CLEAR)
    {
      public void invoke(View view)
      {
        output.setText("");        
      }
    });
  }
  
  static final String IO_POPUP = "io.popup";
  static final String IO_CLEAR = "io.clear";
}
