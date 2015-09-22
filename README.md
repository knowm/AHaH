## AHaH!

A machine learning framework based on Anti-Hebbian and Hebbian (AHaH) neural plasticity.

## Description 

The AHaH! project is a set of tools that can be used to solve a wide range of artificial intelligence 
and machine learning problems. All key functionality is based on operations that can be attained 
through use of an Anti-Hebbian and Hebbian (AHaH) Node. An AHaH Node is a perceptron neuron operating 
the AHaH plasticity rule. The AHaH Node has been mapped to physical memristor circuitry 
and NPU development is ongoing. By restricting machine learning algorithms to functions that can 
be attained with the AHaH Node, the AHaH! software provides a bridge between the CPU of today and 
the NPUs of tomorrow.

The AHaH Node is universal. Its attractor states are universal logic gates and optimal classification 
boundaries. It can operate in supervised, semi-supervised or unsupervised modes and has shown 
solutions to classification, unsupervised feature extraction and clustering, motor control, and 
combinatorial optimization. Capabilities such as tracking of non-stationary statistic and unsupervised 
healing have also been demonstrated. The AHaH node is a "building block" from which a universe of 
machine learning capabilities are now emerging. Currently the AHaH! project has specific modules targeting 
the following areas of machine learning:

1. Metastable Switch Memristor Model
1. Functional and Circuit-based AHaH Node Models
1. Supervised and Unsupervised Classification
1. Unsupervised Feature Extraction and Clustering
1. Unsupervised Robotic Actuation
1. Combinatorial Optimization
1. Signal Prediction and Forecasting

## License

All software is copyright (c) 2013 M. Alexander Nugent Consulting and licensed under the 
M. Alexander Nugent Consulting Research License Agreement.

See LICENSE.txt for more details.

Some source files for working with the MNIST data in the module 'ahah-samples' (MnistManager.java, MnistDbFile.java, MnistImageFile.java, and MnistLabelFile.java) are copyright alex pankov and we've included the source code in compliance with the 'Artistic License'. (https://code.google.com/p/mnist-tools/ß)

## Building

AHaH! is built with Maven.

    cd path/to/ahah-parent
    
#### Install to local repo

    mvn clean install
    
#### maven-license-plugin

    mvn license:check
    mvn license:format
    mvn license:remove
    
#### JavaDocs

    mvn javadoc:aggregate 

## Running the Software

All the sample applications that are part of the AHaH! project can be run from the command line, and a Java JRE 
version 6 or higher is needed. For each sample app, a description, tips for running the app, and the argument list
is given in the corresponding source code. Each app is configured to take a list of parameters which you can use to 
tweak and experiment with. The default argument values are in parentheses. 

## Questions or Help

Please email M. Alexander Nugent Consulting at i@alexnugent.name for any questions or licensing inquiries.

## More Info
Project Site: <http://knowm.org/open-source/ahah/> 
Example Code: <http://knowm.org/open-source/ahah/ahah-example-code> 
Change Log: <http://knowm.org/open-source/ahah/ahah-change-log>
Java Docs: <http://knowm.org/javadocs/ahah/index.html>
