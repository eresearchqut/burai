Setting
=======

1. Unzip :doc:`the downloaded file <download>`

2. Copy it to any directory on your computer e.g.:

  - "C:\\BURAI1.3_Windows\\" for Windows
  - "/Applications/BURAI1.3.app" for Max OSX
  - "/usr/share/applications/BURAI1.3" for Ubuntu

3. Install `JRE1.8 or later version <https://java.com/download/>`_, if your computer does not have it.
   For Ubuntu, you can get libraries of Java as

  .. code-block:: c

       > sudo apt-get install openjdk-8-jdk
       > sudo apt-get install openjfx

4. If you want to use MPI for Mac OSX, the library of OpenMPI has to be installed through MacPorts as

  .. code-block:: c

       > sudo port install openmpi-gcc6
       > sudo port select --set mpi openmpi-gcc6-fortran

5. If you want to use precompiled executables of QE for Ubuntu,
   GFortran and OpenMPI have to be installed through apt-get as.

  .. code-block:: c

       > sudo apt-get install gfortran
       > sudo apt-get install openmpi-bin libopenmpi-dev

6. For Ubuntu, executing BURAI1.3/makeLauncher.sh yields the launcher BURAI.desktop .
