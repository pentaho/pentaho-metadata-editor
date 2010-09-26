#!/bin/sh

 
# this script must be executed inside the pme folder

DIR_REL=`dirname $0`
cd $DIR_REL
DIR=`pwd`

# **************************************************
# ** Set these to the location of your mozilla
# ** installation directory.  Use a Mozilla with
# ** Gtk2 and Fte enabled.
# **************************************************

export MOZILLA_FIVE_HOME=/usr/local/mozilla
export LD_LIBRARY_PATH=/usr/local/mozilla

# **************************************************
# ** Core libraries:                              **
# **************************************************

CLASSPATH=.

# This will get the versioned pentaho-meta.jar
for i in `ls ./lib/*.jar`
do
  CLASSPATH=${CLASSPATH}:${i}
done

CLASSPATH=$CLASSPATH:libswt/commands.jar
CLASSPATH=$CLASSPATH:libswt/common.jar
CLASSPATH=$CLASSPATH:libswt/jface.jar
CLASSPATH=$CLASSPATH:libswt/runtime.jar

# **************************************************
# ** JDBC & other libraries used:                 **
# **************************************************

for f in `find libext`
do
  CLASSPATH=$CLASSPATH:$f
done

# **************************************************
# ** Platform specific libraries ...              **
# **************************************************

. "$DIR/set-pentaho-env.sh"
setPentahoEnv

LIBPATH="NONE"

case `uname -s` in 
  AIX)
    LIBPATH=libswt/aix/
    ;;

  SunOS) 
    LIBPATH=libswt/solaris/
    ;;

  Darwin)
	echo "Starting Metadata Editor using 'metadata-editor.sh' from OS X is not supported."
	echo "Please start using 'Metadata Editor 32-bit' or"
	echo "'Metadata Editor 64-bit' as appropriate."
	exit
	;;


  Linux)
      ARCH=`uname -m`
    case $ARCH in
      x86_64)
        if $($_PENTAHO_JAVA -version 2>&1 | grep "64-Bit" > /dev/null )
        then
          LIBPATH=libswt/linux/x86_64/
        else
          LIBPATH=libswt/linux/x86/
        fi
        ;;

      i[3-6]86)
        LIBPATH=libswt/linux/x86/
        ;;

      ppc)
        LIBPATH=libswt/linux/ppc/
        ;;

      *)  
        echo "I'm sorry, this Linux platform [$ARCH] is not yet supported!"
        exit
        ;;
    esac
    ;;

  FreeBSD)
      ARCH=`uname -m`
    case $ARCH in
      x86_64)
        LIBPATH=libswt/freebsd/x86_64/
        echo "I'm sorry, this Linux platform [$ARCH] is not yet supported!"
        exit
        ;;

      i[3-6]86)
        LIBPATH=libswt/freebsd/x86/
        ;;

      ppc)
        LIBPATH=libswt/freebsd/ppc/
        echo "I'm sorry, this Linux platform [$ARCH] is not yet supported!"
        exit
        ;;

      *)  
        echo "I'm sorry, this Linux platform [$ARCH] is not yet supported!"
        exit
        ;;
    esac
    ;;

  HP-UX) 
    LIBPATH=libswt/hpux/
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

if [ "$LIBPATH" != "NONE" ]
then
  for f in `find $LIBPATH -name '*.jar'`
  do
    CLASSPATH=$CLASSPATH:$f
  done
fi


# ******************************************************************
# ** Set java runtime options                                     **
# ** Change 256m to higher values in case you run out of memory.  **
# ******************************************************************

OPT="-Xmx256m -cp $CLASSPATH -Djava.library.path=$LIBPATH"

# ***************
# ** Run...    **
# ***************

"$_PENTAHO_JAVA" $OPT org.pentaho.pms.ui.MetaEditor "$1" "$2" "$3" "$4" "$5" "$6" "$7" "$8" "$9"
cd -
