#!/bin/sh

# This program is free software; you can redistribute it and/or modify it under the
# terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
# Foundation.
#
# You should have received a copy of the GNU Lesser General Public License along with this
# program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
# or from the Free Software Foundation, Inc.,
# 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
#
# This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
# without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
# See the GNU Lesser General Public License for more details.
#
# Copyright (c) 2009-2017 Hitachi Vantara..  All rights reserved.

# this script must be executed inside the pme folder

# **************************************************
# ** Set these to the location of your mozilla
# ** installation directory.  Use a Mozilla with
# ** Gtk2 and Fte enabled.
# **************************************************

# set MOZILLA_FIVE_HOME=/usr/local/mozilla
# set LD_LIBRARY_PATH=/usr/local/mozilla

# Try to guess xulrunner location - change this if you need to
MOZILLA_FIVE_HOME=$(find /usr/lib -maxdepth 1 -name xulrunner-[0-9]* | head -1)
LD_LIBRARY_PATH=${MOZILLA_FIVE_HOME}:${LD_LIBRARY_PATH}
export MOZILLA_FIVE_HOME LD_LIBRARY_PATH


# Fix for GTK Windows issues with SWT
export GDK_NATIVE_WINDOWS=1

# Fix overlay scrollbar bug with Ubuntu 11.04
export LIBOVERLAY_SCROLLBAR=0

# Fix menus not showing up on Ubuntu 14.04's unity
# Bug in: https://bugs.launchpad.net/ubuntu/+source/unity-gtk-module/+bug/1208019
export UBUNTU_MENUPROXY=0


# **************************************************
# ** Init BASEDIR                                 **
# **************************************************

BASEDIR=`dirname $0`
cd $BASEDIR
DIR=`pwd`
cd -


# **************************************************
# ** Platform specific libraries ...              **
# **************************************************


LIBPATH="NONE"
STARTUP="-jar launcher/pentaho-application-launcher.jar"


. "$DIR/set-pentaho-env.sh"
setPentahoEnv

case `uname -s` in 
  Darwin)
	LIBPATH=$BASEDIR/../libswt/osx64/
	OPT="-XstartOnFirstThread $OPT"
	;;


  Linux)
      ARCH=`uname -m`
    case $ARCH in
      x86_64)
        if $($_PENTAHO_JAVA -version 2>&1 | grep "64-Bit" > /dev/null )
        then
          LIBPATH=$BASEDIR/../libswt/linux/x86_64/
        else
          LIBPATH=$BASEDIR/../libswt/linux/x86/
        fi
        ;;

      i[3-6]86)
        LIBPATH=$BASEDIR/../libswt/linux/x86/
        ;;

      ppc)
        LIBPATH=$BASEDIR/../libswt/linux/ppc/
        ;;

      *)  
        echo "I'm sorry, this Linux platform [$ARCH] is not yet supported!"
        exit
        ;;
    esac
    ;;


  CYGWIN*)
    ./MetaEditor.bat
    # exit
    ;;


  *) 
    echo The Metadata Editor is not supported on this hosttype : `uname -s`
    exit
    ;;
esac 

export LIBPATH


# ******************************************************************
# ** Set java runtime options                                     **
# ** Change 2048m to higher values in case you run out of memory. **
# ******************************************************************

OPT="-Xms1024m -Xmx2048m -Djava.library.path=$LIBPATH $OPT"


# ***************
# ** Run...    **
# ***************
"$_PENTAHO_JAVA" $OPT $STARTUP -lib $LIBPATH "${1+$@}"
