# Simulation Modeling with SimPy

This repository is for converting Barry Nelson, Fundamentals and Methods of Stochastic Programming, Chapter 4 of Simulation Programming with VBAsim so that the examples are made to use Python and the SimPy simulation library.  It is intended to both enable those using the textbook to use it with Python as well as to provide examples of good use of the SimPy simulation library.  In particular, as the SimPy simulation library is suitable for production use, unlike the other examples provided this library can be used without caveats.  Therefore, this chapter will also provide examples of use of Python as a scientific computing environment.  Additional versions are planned for R and Julia using simmmer and SimJulia respectively.

VBASim and JavaSim are copyright to Barry Nelson.
PythonSim, RSim, and Simpy3 are copyright to Louis Luangkesorn and Licensed under MIT License

# Requirements for Python

This repository and it associated chapter will be based on Python 2.7.x and SimPy 2.3.x.  As the scientific computing community migrates to use of Python 3.x this repository will include a parallel version of the examples developed using Python 3.x and SimPy 3.x so that the code remains up to date.  Additional versions may be developed to provide examples of using SimPy within the Java Virtual Machine (Jython) and the .Net environment (IronPython).  The Simpy 3 version is for Python 3.x and Simpy 3.x.  The R version requires the simmer library, which requires R >= 3.1.2.

As a scientific computing library, we will use along with SimPy several Python libraries that are generally considered the foundation of the Python scientific computing stack.  While we will use the Python Standard Libraries were possible, the scientific computing stack will allow for the use of statistical methods and graphical output.  These components are:

1.  Numeric Python (numpy) http://numpy.org
2.  Scientific Python (scipy) http://scipy.org
3.  Matplotlib (charting and graphing) http://matplotlib.sourceforge.net
4.  Statsmodels (statistics, basic plotting) ??  http://statsmodels.sourceforge.net

In addition, the ipython command line shell is generally found to be useful as well for scientific computing.

5.  ipython (advanced interactive shell) http://ipython.org
6.  Pandas (panel data analysis data structures)  http://pandas.pydata.org

All of these libraries are open source.  These libraries can be installed through methods that are discussed at each library's website.  In addition, scientific computing oriented Python distributions have been developed that include all of these libraries.  Examples of such distributions include Python (x,y)  http://code.google.com/p/pythonxy/ and Enthought Inc. Canopy https://www.enthought.com/products/canopy/

# R and simmer

The R version of the chapter requires the simmer simulation library.  https://cran.r-project.org/web/packages/simmer/index.html. In turn this requires Rcpp, R6, magrittr, methods, utils. Note that Rcpp requires the ability to compile C++ code on the machine (gcc included as part of Rcpp).
