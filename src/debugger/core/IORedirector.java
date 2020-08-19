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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.util.EventListener;
import javax.swing.event.EventListenerList;


final class IORedirector implements OutputListener
{
  
  private EventListenerList listeners = new EventListenerList();
  private PrintWriter input;
  private volatile boolean running = false;

  final void addOutputListener(OutputListener lsnr)
  {
    listeners.add(STDOUTListener.class, lsnr);
  }

  final void removeOutputListener(OutputListener lsnr)
  {
    listeners.remove(STDOUTListener.class, lsnr);
  }

  final void addErrorListener(OutputListener lsnr)
  {
    listeners.add(STDERRListener.class, lsnr);
  }

  final void removeErrorListener(OutputListener lsnr)
  {
    listeners.remove(STDERRListener.class, lsnr);
  }

  final void addEchoListener(OutputListener lsnr)
  {
    listeners.add(STDINListener.class, lsnr);
  }

  final void removeEchoListener(OutputListener lsnr)
  {
    listeners.remove(STDINListener.class, lsnr);
  }

  final void setProcess(Process process)
  {
    running = true;
    InputStream out = process.getInputStream();
    InputStream err = process.getErrorStream();
    OutputStream in = process.getOutputStream();
    input = new PrintWriter (new OutputStreamWriter(in), true);

    LoopReader outlr = new LoopReader(out, STDOUTListener.class);
    LoopReader errlr  = new LoopReader(err, STDERRListener.class);
  }

  public final void detach()
  {
    running = false;
  }

  public synchronized void outputLine(String line)
  {
    if (running)
    {
      EventListener[] list = listeners.getListeners(STDINListener.class);
      input.println(line);
      for (int i=0; i < list.length; i++)
      {
        ((OutputListener) list[i]).outputLine(line);;
      }  
    }
  }

  /** Inner class which keeps reading in a Loop;*/
  private class LoopReader extends Thread
  {
    BufferedReader reader;
    Class listenerType;
    EventListener[] list;

    public LoopReader(InputStream stream, Class type)
    {
      listenerType = type;
      list = listeners.getListeners(listenerType);
      reader = new BufferedReader(new InputStreamReader(stream));
      setPriority(Thread.MIN_PRIORITY);
      start();
    }

    public void run()
    {
      try
      {
        while (running)
        {
          String line = reader.readLine();
          if (line == null)
            break;
          if (listeners.getListenerCount(listenerType) != list.length)
          {
            list = listeners.getListeners(listenerType);
          }
          for (int i=0; i < list.length ; i++)
          {
            ((OutputListener) list[i]).outputLine(line);
          }
        }
      }
      catch (IOException ex)
      {
        ex.printStackTrace();
      }
    }
  }

}
