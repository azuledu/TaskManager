# Task Manager

### Create a native executable file

Install GraalVM with [SDKMAN](https://sdkman.io/) or directly
from [GraalVM web](https://www.graalvm.org/latest/docs/getting-started):

```
sudo mkdir /usr/local/java
cd /usr/local/java
sudo wget https://download.oracle.com/graalvm/22/latest/graalvm-jdk-22_linux-x64_bin.tar.gz
sudo tar zxvf graalvm-jdk-22_linux-x64_bin.tar.gz
sudo rm graalvm-jdk-22_linux-x64_bin.tar.gz
sudo update-alternatives --install "/usr/bin/java" "java" "/usr/local/java/graalvm-jdk-22.0.2+9.1/bin/java" 1
sudo update-alternatives --config java  # Choose GraalVM
export PATH=/usr/local/java/graalvm-jdk-22.0.2+9.1/bin:$PATH  # Put this line in ~/.bashrc
source ~/.bashrc
java -version
native-image --version
```

The `native-image` tool, from GraalVM, depends on the local toolchain. To install it:

- Linux: `sudo apt install build-essential zlib1g-dev`
- MacOS: `xcode-select --install`
- Windows: Install Visual Studio 2022 version 17.6.0 or later, and Microsoft Visual C++ (MSVC).

Compile the project and **build a native executable file** at one step:  
`mvn -Pnative package`

The native executable, named `tm`, is created in the `target/` directory of the project.
Run the executable:  
`./target/tm`

### Bash autocompletion

To enable Bash autocompletion for the new `tm` executable:

```
sudo apt install bash-completion
sudo cp tm-bash-completion.sh /etc/bash_completion.d/tm
source /etc/bash_completion
```