Server is written in D

A built copy of the program is available in this folder. It is built for Linux Mint 17.1.

To build a copy of the program, I suggest that you use dub.
Following instructions are geared towards Ubuntu distros

You will need a copy of DMD, the Digital Mars Compiler for the D lanaguage.
  ```bash
  sudo apt-get install dmd
  ```
Or, you can install it from the following .deb file:
  http://downloads.dlang.org/releases/2.x/2.067.0/dmd_2.067.0-0_amd64.deb

Download the tarball for dub here:
  http://code.dlang.org/files/dub-0.9.23-linux-x86_64.tar.gz
  
To extract,
  ```bash
  tar -xvzf dub-0.9.23-linux-x86_64.tar.gz
  ```
  
You will then need to add it to your PATH
  ```bash
  nano ~/.profile
  ```
  
Add this line to the end:
  ```bash
  export PATH=${PATH}:/path/to/dub/
  ```

Log out and in to reload .profile
  

To build the server:
Pull the repo, navigate to the 'tannoyserver' directory in a terminal, and run
  ```bash
  dub run
  ```
  
Dub will resolve all dependancies and store them in ~/.dub
Please ensure that you have both .JSON files in the folder as that is what dub uses to resolve dependancies.


Instructions on how to query the server are in ./source/server.d
