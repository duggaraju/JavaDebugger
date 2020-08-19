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

import debugger.core.SourceMapper;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javacore.JavaCorePlugin;
import javacore.ClasspathSource;

import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.io.VFS;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.jEdit;

/**
 * Source Mapper is a helper class to search for source files while stepping through code.
 * Uses the archive plugin to search to zips and other archives.
 */
public final class PluginSourceMapper implements SourceMapper
{

  private static final String PROTOCOL = "archive:";
  private static final class PathEntry
  {
    final File file;
    final boolean isArchive;
    String pathPrefix = ""; //Prefix to the files in the archive.
    
    PathEntry(File file, boolean archive)
    {
      this.file = file;
      isArchive = archive;
    }

    PathEntry(File file)
    {
      this(file, false);
    }
    
    public String getVFSName(String suffix)
    {
      StringBuffer buffer = new StringBuffer();
      if (isArchive)
      {
        buffer.append(PROTOCOL);
      }
      buffer.append(file.getAbsolutePath());
      
      if (isArchive)
      {
        buffer.append('!');
      }
      
      if (suffix.charAt(0) != '/' || suffix.charAt(0) != File.separatorChar)
      {
        buffer.append(File.separatorChar);
      }
      buffer.append(suffix);
      
      return buffer.toString();
    }
  }

  private ClasspathSource pathSource;
  private String path;
  private List pathList =new ArrayList();
  private boolean autoUpdate;

  public PluginSourceMapper(ClasspathSource source, boolean update)
  {
    pathSource = source;
    autoUpdate = update;
    update();
  }
  
  public PluginSourceMapper(ClasspathSource source)
  {
    this(source, true);
  }
  
  public PluginSourceMapper()
  {
    this(JavaCorePlugin.getClasspathSource());
  }

  private synchronized final void update()
  {
    String oldPath = path;
    path = pathSource.getSourcepath();
    if (path == null || path.length() == 0)
    {
      path = pathSource.getClasspath();
    }
    
    if (oldPath == null || !oldPath.equals(path) )
    {
      update(path);
    }
  }
  
  private synchronized void update(String sourcePath)
  {
    pathList.clear();

    //included the src.zip if possbile.
    File javaDir = new File(System.getProperty("java.home"));
    String srcZip =  javaDir.getParent() + File.separatorChar +  "src.zip";
    sourcePath += File.pathSeparator + srcZip;
    
    StringTokenizer tokenizer = new StringTokenizer(sourcePath, File.pathSeparator);
    while (tokenizer.hasMoreTokens())
    {
      String token = tokenizer.nextToken();
      File file = new File(token);
      if (file.isDirectory() && file.canRead())
      {
        pathList.add(new PathEntry(file, false));
      }
      else if ( token.endsWith(".jar") || token.endsWith(".zip"))
      {
        if (file.canRead())
          pathList.add(new PathEntry(file, true));
      }
    }
    
  }

  /**
   * Returns the File object with the give name.
   */

  public final String getSourceFile(String suffix)
  {
    if (autoUpdate)
    {
      update();
    }
    String file = null;
 
    View view = jEdit.getActiveView();
      
    Iterator iter = pathList.iterator();
    while(iter.hasNext())
    {
      PathEntry entry = (PathEntry) iter.next();
      String vfsName = entry.getVFSName(suffix);
      VFS vfs = VFSManager.getVFSForPath(vfsName);
      System.err.println("VFS for suffix=" + suffix + " vfsName=" + vfsName);
      if (vfs != null)
      {
        Object session = vfs.createVFSSession(vfsName, view);
        try
        {
	  VFS.DirectoryEntry vfsFile = vfs._getDirectoryEntry(session, vfsName, view);
          System.err.println("Searching for " + suffix + " got " + vfsFile);
          if (vfsFile != null)
          {
            file = vfsName;
            break;
          }
        }
        catch(IOException ex)
        {        
	  ex.printStackTrace();
        }
      }
      else
      {
        System.err.println("No VFS found... returning null");
      }
    }

    return file;
  }

  public final String getClassNameForFile(String filename)
  {
    File file = new File(filename);
    try
    {
      filename = file.getCanonicalPath();
    }
    catch(IOException ex)
    {
      filename = file.getAbsolutePath();
    }
    
    Iterator iter = pathList.iterator();
    while(iter.hasNext())
    {
      PathEntry entry = (PathEntry) iter.next();
      String entryPath = entry.file.getAbsolutePath();
      if (filename.startsWith(entryPath))
      {
        String className = filename.substring(entryPath.length() + 1, filename.length() - 5);
        return className.replace(File.separatorChar, '.');
      }
    }
    return null;
  }
}
