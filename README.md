# PhyDyn: Epidemiological modelling in BEAST.

Igor Siveroni and Erik Volz.
Department of Infectious Disease Epidemiology, Imperial College London.

## Description
[PhyDyn](https://github.com/mrc-ide/PhyDyn/wiki) is a BEAST2 package for performing Bayesian phylogenetic inference under models that deal with structured populations with complex population dynamics.
This package enables simultaneous estimation of epidemiological parameters and pathogen phylogenies. The package implements a coalescent model for a large class of epidemic processes specified by a deterministic nonlinear dynamical system. Genealogies are specified as timed phylogenetic trees in which lineages are associated with the distinct subpopulation in which they are sampled. Epidemic models are defined by a series of ordinary differential equations (ODEs) specifying the rates that new lineages introduced in the population (birth matrix) and the rates at which migrations, or transition between states occur (migration matrix). The ODE syntax allows the user to define and use complex mathematical expressions such as  polynomials and trigonometric functions. Currently, PhyDyn works with deterministic demographic models. Future versions may incorporate other population models, methods for simulating trees conditional on an epidemic process and include semi-parametric epidemiological models.

**Current version: 1.3.7 **

## System Requirements

PhyDyn is a BEAST2 package. The latest version of the package (**v1.3.7**) has been compiled against BEAST v2.6.2.

PhyDyn requires:
- BEAST2 v2.6.0 or greater or, preferably, BEAST v2.6.2 or greater.
- Java JDK (JDK 8 recommended for OSX) - required by BEAST2. Note that there's the option to download Java and BEAST2 in a single bundle.

BEAST2 is available for Windows, OSX and Linux.  Instructions for download and installation can be found in the [BEAST2 site](https://www.beast2.org).


## Installation guide

The easiest way to install PhyDyn is with BEAST2's package manager via BEAUti.<br>
BEAUti, a graphical user-interface (GUI) application for generating BEAST XML files, is distributed together with BEAST2. Instructions on how to install/un-install BEAST2 packages with BEAUti can be found at the [Managing Packages](https://www.beast2.org/managing-packages/) page from the BEAST2 site.

We also provide a zipped distribution of the package (`dist/PhyDyn.v1.3.6.zip`) that can be installed by hand following the instructions in [here](https://www.beast2.org/managing-packages/) .


<!--  However, the package (v.1.3.7) can also be installed by hand. -->
<!-- or, alternatively, examples can be run using  PhyDyn's standalone version. -->

<!--
### Installation by-hand
The zipped distribution of the package is located in the `dist/` directory. The latest distribution is `dist/PhyDyn.v1.3.6.zip`.
Instructions on how to install Beast packages by hand can be found [here](https://www.beast2.org/managing-packages/). The steps are:
* Locate the directory where the current Beast version  keeps add-ons and packages. In Linux, it's usually `/users/<username>/.beast/2.6`. Move to that directory, create a directory for PhyDyn, and then move inside the new directory:
```
> cd /users/<username>/.beast/2.6
> mkdir PhyDyn
> cd PhyDyn
```
* Copy the zipped distribution to the package's directory (current directory). Unzip the file.
```
> cp path-to-file/PhyDyn.v1.3.7.zip .
> unzip PhyDyn.v1.3.7.zip
```
That's all. Beast should recognize the package next time it runs.

 -->
<!--
### Standalone

PhyDyn's standalone version is bundled with BEAST 2.6.2.
In order to run an example with the package's stand-alone version, `jars/phydynv1.3.6.jar`, type the following:
```
    java -jar phydynv1.3.6.jar examplefile.xml
```
PhyDyn Beauti templates will not be accesible with this method.
-->

## Demo / examples

PhyDyn analyses are provided as `xml` files and executed by running BEAST2 - they are, after all, BEAST2 analyses. For example, let's consider the PhyDyn xml file  `examples/SIR3/SIR3example.xml` that specifies a three-deme SIR model and  generates the corresponding SIR3 trajectory for time interval [0,50] (no MCC sampling involved). After making sure the xml file is in our current directory, we can execute the PhyDyn analysis by typing
```
> beast SIR3example.xml
```
or by running the BEAST GUI, selecting the desired file and hitting the `Run` button. The analysis generates a trajectory log file named `trajSIR3.csv`, which can be used to generate a plot (`trajSIR3.csv.png`) by typing
```
Rscript plotTraj.R trajSIR3.csv
```
You need R for this part. The `trajSIR3.csv.png` file is also included in `examples/SIR3`.


Below, we provide links to tutorials and complete examples.

Tutorials:
* SIR2 model: Model construction and trajectory generation ([tutorial](https://github.com/mrc-ide/PhyDyn/wiki/Tutorial-SIR2-Trajectory))
* SIR2 model: Computing the likelihood ([tutorial](https://github.com/mrc-ide/PhyDyn/wiki/Tutorial-SIR2-Treelikelihood)).


Examples:
* [Ebola](https://github.com/mrc-ide/PhyDyn/wiki/Ebola-Example): A super-spreading SEIR model applied to Ebola (with fixed tree).
* [Influenza](https://github.com/mrc-ide/PhyDyn/wiki/Influenza-Example ): Flu model with sequence data (tree sampling).

## Instructions for use

Check the PhyDyn [Wiki](https://github.com/mrc-ide/PhyDyn/wiki) for detailed documentation.

## Contact

Any questions, bug reports or suggestions please email to Igor Siveroni at i.siveroni@imperial.ac.uk.

## License

This software is free (as in freedom). With the exception of the libraries on which it depends, it is made available under the terms of the GNU General Public Licence version 3, which is contained in the this directory in the file named COPYING.

The following libraries are bundled with PhyDyn:

* [ANTLR](http://www.antlr.org/) : Another Tool for Language recognition
* Apache Commons (http://commons.apache.org/)
* jblas (http://jblas.org) : Linear Algebra for Java

## Funding

Work on this project is made possible by support from the MRC Centre for Outbreak Analysis and Modelling, and [MIDAS](http://www.epimodels.org/), Models of Infectious Disease Agent Study (grant NIH MIDAS U01 GM110749).


## References
* [1] Volz EM, 2012, [Complex population dynamics and the coalescent under neutrality](http://www.genetics.org/content/190/1/187), Genetics, Vol:190, ISSN:0016-6731, Pages:187-201
* [2] Erik Volz and Igor Siveroni, 2018, [Bayesian phylodynamic inference with complex models](https://journals.plos.org/ploscompbiol/article?id=10.1371/journal.pcbi.1006546). PLOS COMPUTATIONAL BIOLOGY, Vol: 14, ISSN: 1553-7358 .
