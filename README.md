# PhyDyn: Epidemiological modelling in BEAST.

Igor Siveroni and Erik Volz.
Department of Infectious Disease Epidemiology, Imperial College London.

## Description
[PhyDyn](https://github.com/mrc-ide/PhyDyn/wiki) is a BEAST2 package for performing Bayesian phylogenetic inference under models that deal with structured populations with complex population dynamics.
This package enables simultaneous estimation of epidemiological parameters and pathogen phylogenies. The package implements a coalescent model for a large class of epidemic processes specified by a deterministic nonlinear dynamical system. Genealogies are specified as timed phylogenetic trees in which lineages are associated with the distinct subpopulation in which they are sampled. Epidemic models are defined by a series of ordinary differential equations (ODEs) specifying the rates that new lineages introduced in the population (birth matrix) and the rates at which migrations, or transition between states occur (migration matrix). The ODE syntax allows the user to define and use complex mathematical expressions such as  polynomials and trigonometric functions. Currently, PhyDyn works with deterministic demographic models. Future versions may incorporate other population models, methods for simulating trees conditional on an epidemic process and include semiparametric epidemiological models.

## Distribution and Installation

The package is not currently available from the Beast repository i.e. it can't be installed with Beauti's package manager. The package must be installed by hand or, alternatively, examples can be run using the PhyDyn's standalone version.

### Installation by-hand
The zipped distribution of the package is located in the `dist/` directory. The latest distribution is `dist/PhyDyn.v1.0.0.zip`.
Instructions on how to install Beast packages by hand can be found [here](https://www.beast2.org/managing-packages/). The steps are:
* Locate the directory where the current Beast version  keeps add-ons and packages. In Linux, it's usually `/users/<username>/.beast/2.4`. Move there, create a directory for PhyDyn, and then move inside the new directory:
```
> cd /users/<username>/.beast/2.4
> mkdir phydyn
> cd phydyn
```
* Copy the zipped distribution to the package's directory (current directory). Unzip the file.
```
> cp path-to-file/PhyDyn.v1.0.0.zip .
> unzip PhyDyn.v1.0.0.zip
```

### Standalone

In order to run an example with the package's stand-alone version, `jars/phydyn.jar`, type the following:
```
    java -jar examplefile.xml
```


## Documentation and Examples

* Check the PhyDyn [wiki](https://github.com/mrc-ide/PhyDyn/wiki) for detailed documentation.
* [SIR3](examples/SIR3) : An example of a 3-deme population model and the use of the TrajectoryOut class.
* [Root States](examples/roosStates): A constant population model (constant rates) with tree sampling. Logging of root state probabilities.

## License

This software is free (as in freedom). With the exception of the libraries on which it depends, it is made available under the terms of the GNU General Public Licence version 3, which is contained in the this directory in the file named COPYING.

The following libraries are bundled with MultiTypeTree:

* [ANTLR](http://www.antlr.org/) : Another Toll for Language recorgnition
* Apache Commons (http://commons.apache.org/)
* jblas (http://jblas.org) : Linear Algebra for Java

Work on this project is made possible by support from the MRC Centre for Outbreak Analysis and Modelling, and [MIDAS](http://www.epimodels.org/), Models of Infectious Disease Agent Study (grant NIH MIDAS U01 GM110749).


## References
* [1] Volz EM, 2012, Complex population dynamics and the coalescent under neutrality, Genetics, Vol:190, ISSN:0016-6731, Pages:187-201
