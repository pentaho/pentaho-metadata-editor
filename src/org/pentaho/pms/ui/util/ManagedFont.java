/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.pms.ui.util;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

/**
 * Class to keep track of which font is a system font (managed by the OS) and which is not.
 * 
 * @author Matt
 * @since 2006-06-15
 *
 */
public class ManagedFont
{
    private Font font;
    private boolean systemFont;
    
    /**
     * @param font The font
     * @param systemFont true if this is a system font and doesn't need to be disposed off
     */
    public ManagedFont(Font font, boolean systemFont)
    {
        this.font = font;
        this.systemFont = systemFont;
    }

    /**
     * Create a new managed font by using fontdata
     * @param display the display to use
     * @param fontData The fontdata to create the font with.
     */
    public ManagedFont(Display display, FontData fontData)
    {
        this.font = new Font(display, fontData);
        this.systemFont = false;
    }

    /**
     * Free the managed resource if it hasn't already been done and if this is not a system font
     *
     */
    public void dispose()
    {
        // System color and already disposed off colors don't need to be disposed!
        if (!systemFont && !font.isDisposed())
        {
            font.dispose();
        }
    }

    /**
     * @return Returns the font.
     */
    public Font getFont()
    {
        return font;
    }

    /**
     * @return true if this is a system font.
     */
    public boolean isSystemFont()
    {
        return systemFont;
    }

    /**
     * @param font the font to set
     */
    public void setFont(Font font)
    {
        this.font = font;
    }

    /**
     * @param systemFont the systemFont to set
     */
    public void setSystemFont(boolean systemFont)
    {
        this.systemFont = systemFont;
    }
}
