#!/bin/sh

 
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
# ** Change 256m to higher values in case you run out of memory.  **
# ******************************************************************

OPT="-Xmx256m -Djava.library.path=$LIBPATH $OPT"


# ***************
# ** Run...    **
# ***************
"$_PENTAHO_JAVA" $OPT $STARTUP -lib $LIBPATH "${1+$@}"
