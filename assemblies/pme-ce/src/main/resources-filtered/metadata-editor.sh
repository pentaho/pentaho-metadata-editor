#!/bin/sh
# ******************************************************************************
#
# Pentaho
#
# Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
#
# Use of this software is governed by the Business Source License included
# in the LICENSE.TXT file.
#
# Change Date: 2028-08-13
# ******************************************************************************


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

# Fix GTK 3 issues with SWT
export SWT_GTK3=0

# Fix overlay scrollbar bug with Ubuntu 11.04
export LIBOVERLAY_SCROLLBAR=0

# Fix menus not showing up on Ubuntu 14.04's unity
# Bug in: https://bugs.launchpad.net/ubuntu/+source/unity-gtk-module/+bug/1208019
export UBUNTU_MENUPROXY=0


# **************************************************
# ** Init BASEDIR                                 **
# **************************************************

BASEDIR=`dirname $0`
CURRENTDIR="."
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
    ARCH=`uname -m`
	if [ -z "$IS_KITCHEN" ]; then
		OPT="-XstartOnFirstThread $OPT"
	fi
	case $ARCH in
		x86_64)
			if $($_PENTAHO_JAVA -version 2>&1 | grep "64-Bit" > /dev/null )
                            then
			  LIBPATH=$CURRENTDIR/../libswt/osx64/:$CURRENTDIR/../native-lib/osx64/:$CURRENTDIR/native-lib/osx64/
                            else
			  LIBPATH=$CURRENTDIR/../libswt/osx/:$CURRENTDIR/../native-lib/osx/:$CURRENTDIR/native-lib/osx/
                            fi
			;;
    arm64)
        if $($_PENTAHO_JAVA -version 2>&1 | grep "version \"1\.8\..*" > /dev/null )
                              then
          echo "I'm sorry, this Mac platform [$ARCH] is not supported in Java 8"
          exit
                              else
          LIBPATH=$CURRENTDIR/../libswt/osx64_aarch/:$CURRENTDIR/../native-lib/osx64_aarch/:$CURRENTDIR/native-lib/osx64_aarch/
                              fi
      ;;
		i[3-6]86)
			LIBPATH=$CURRENTDIR/../libswt/osx/:$CURRENTDIR/../native-lib/osx/:$CURRENTDIR/native-lib/osx/
			;;

		*)
			echo "I'm sorry, this Mac platform [$ARCH] is not yet supported!"
			echo "Please try starting using 'Data Integration 32-bit' or"
			echo "'Data Integration 64-bit' as appropriate."
			exit
			;;
	esac
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

JAVA_ADD_OPENS=
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/sun.net.www.protocol.jar=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/java.lang=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/java.io=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/java.lang.reflect=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/java.net=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/java.security=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/java.util=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/sun.net.www.protocol.file=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/sun.net.www.protocol.ftp=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/sun.net.www.protocol.http=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/sun.net.www.protocol.https=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/sun.reflect.misc=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.management/javax.management=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.management/javax.management.openmbean=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.naming/com.sun.jndi.ldap=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/java.math=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/sun.nio.ch=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.base/java.nio=ALL-UNNAMED"
JAVA_ADD_OPENS="$JAVA_ADD_OPENS --add-opens=java.security.jgss/sun.security.krb5=ALL-UNNAMED"
OPT="$OPT $JAVA_ADD_OPENS -Djava.locale.providers=COMPAT,SPI"


# ***************
# ** Run...    **
# ***************
OS=`uname -s | tr '[:upper:]' '[:lower:]'`
if [ $OS = "linux" ]; then
    "$_PENTAHO_JAVA" $OPT $STARTUP -lib $LIBPATH "${1+$@}" 2>&1 | grep -viE "Gtk-WARNING|GLib-GObject|GLib-CRITICAL|^$"
else
    "$_PENTAHO_JAVA" $OPT $STARTUP -lib $LIBPATH "${1+$@}"
fi
