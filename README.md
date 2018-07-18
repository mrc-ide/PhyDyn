# PhyDyn: Epidemiological modelling in BEAST.

Igor Siveroni and Erik Volz.
Department of Infectious Disease Epidemiology, Imperial College London.

## Description
[PhyDyn](https://github.com/mrc-ide/PhyDyn/wiki) is a BEAST2 package for performing Bayesian phylogenetic inference under models that deal with structured populations with complex population dynamics.
This package enables simultaneous estimation of epidemiological parameters and pathogen phylogenies. The package implements a coalescent model for a large class of epidemic processes specified by a deterministic nonlinear dynamical system. Genealogies are specified as timed phylogenetic trees in which lineages are associated with the distinct subpopulation in which they are sampled. Epidemic models are defined by a series of ordinary differential equations (ODEs) specifying the rates that new lineages introduced in the population (birth matrix) and the rates at which migrations, or transition between states occur (migration matrix). The ODE syntax allows the user to define and use complex mathematical expressions such as  polynomials and trigonometric functions. Currently, PhyDyn works with deterministic demographic models. Future versions may incorporate other population models, methods for simulating trees conditional on an epidemic process and include semiparametric epidemiological models.

**Current version: 1.3.0**

## Distribution and Installation

The latest version of the package (**v1.3.0**) is currently available from the Beast repository. It has been compiled against BEAST v2.5.0.

The package (v.1.3.0) can also be installed by hand or, alternatively, examples can be run using  PhyDyn's standalone version.

### Installation by-hand
The zipped distribution of the package is located in the `dist/` directory. The latest distribution is `dist/PhyDyn.v1.3.0.zip`.
Instructions on how to install Beast packages by hand can be found [here](https://www.beast2.org/managing-packages/). The steps are:
* Locate the directory where the current Beast version  keeps add-ons and packages. In Linux, it's usually `/users/<username>/.beast/2.5`. Move to that directory, create a directory for PhyDyn, and then move inside the new directory:
```
> cd /users/<username>/.beast/2.4
> mkdir PhyDyn
> cd PhyDyn
```
* Copy the zipped distribution to the package's directory (current directory). Unzip the file.
```
> cp path-to-file/PhyDyn.v1.3.0.zip .
> unzip PhyDyn.v1.3.0.zip
```
That's all. Beast should recognize the package next time it runs.


### Standalone

PhyDyn's standalone version is bundled with BEAST 2.5.0.
In order to run an example with the package's stand-alone version, `jars/phydynv1.3.0.jar`, type the following:
```
    java -jar phydynv1.3.0.jar examplefile.xml
```


## Documentation and Examples

* Check the PhyDyn [wiki](https://github.com/mrc-ide/PhyDyn/wiki) for detailed documentation.
* [Ebola](https://github.com/mrc-ide/PhyDyn/wiki/Ebola-Example): A super-spreading SEIR model applied to Ebola.
* [SIR3](examples/SIR3) : An example of a 3-deme population model and the use of the TrajectoryOut class.
* [Influenza](examples/influenza): Flu model with fixed trees and sequence data.


## Contact

Any questions, bug reports or suggestions please email to Igor Siveroni at i.siveroni@imperial.ac.uk.

## License

This software is free (as in freedom). With the exception of the libraries on which it depends, it is made available under the terms of the GNU General Public Licence version 3, which is contained in the this directory in the file named COPYING.

The following libraries are bundled with PhyDyn:

* [ANTLR](http://www.antlr.org/) : Another Tool for Language recorgnition
* Apache Commons (http://commons.apache.org/)
* jblas (http://jblas.org) : Linear Algebra for Java

Work on this project is made possible by support from the MRC Centre for Outbreak Analysis and Modelling, and [MIDAS](http://www.epimodels.org/), Models of Infectious Disease Agent Study (grant NIH MIDAS U01 GM110749).


## References
* [1] Volz EM, 2012, [Complex population dynamics and the coalescent under neutrality](http://www.genetics.org/content/190/1/187), Genetics, Vol:190, ISSN:0016-6731, Pages:187-201
* [2] Erik Volz and Igor Siveroni, 2018, [Bayesian phylodynamic inference with complex models](https://www.biorxiv.org/content/early/2018/02/19/268052). Submitted for publication.

