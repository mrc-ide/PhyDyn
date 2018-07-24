# run: R -f genTrees.R --args <numTrees>
# R -f genTrees.R --args 2

# Description: Script that generates <numTrees> Newick files using
#              MASTER simulations

# Arguments:
# <numTrees> number of newick trees to be generated using MASTER

# Requires:
# sir2Master.xml must exist: MASTER model

# master <- "MASTER-5.0.0/master_linux.sh"
#master <- "master.sh"
master <- "beast"

require(phytools)

# Converts a MASTER generated Nexus file to Newick format.
# Writes new newick file.
# Taxa are annotated using the <NexusNr>_<stateName> format.
# Internal node ids are removed.
# Operation may fail: returns TRUE/FALSE
processNexus <- function(nexusFile, newickFile, numTips) {
  nexusString <- scan(nexusFile,what=list(character()))
  enwk <- nexusString[[1]][7]
  
  # replace annotations with _<type> e.g. I0 -> _I0
  nwk <- gsub("\\[&type=\"([^\"\']*?)[\"\'](.*?)\\]","_\\1",enwk)

  tree <- 0
  tryCatch(
     tree <- read.newick(text=nwk),
     error = function(e) 
     {
       msg <- paste("Caught error while reading Newick tree:",
       		     e$message)
       msg <- paste("Bad tree..trying again",msg,sep='\n')
       warning(msg)
     }
  )
  if (class(tree)!="phylo") {
    return(FALSE)
  }
  # Can collapse fail? - yes, it can
  cTree <- 0
  tryCatch(
  cTree <- collapse.singles(tree),
     error = function(e) 
     {
       msg <- paste("Caught error while collapsing tree:",
       		     e$message)
       msg <- paste("Bad tree..trying again",msg,sep='\n')
       warning(msg)
     }
  )
  if (class(cTree)!="phylo") {
    return(FALSE)
  }
  tree <- 0

  # check if we have enough number of tips
  if (length(cTree$tip.label) != numTips) {
    warning(paste("Bad tree...trying again: low number of tips:",
    			  length(cTree$tip.label)))
    return(FALSE)
  }
  cTree$node.label <- NULL
  # print(cTree)
  
  write.tree(cTree, newickFile )
  return(TRUE)
}


genMASTERTrees <- function(runMaster, modelName, numTrees,nexus=FALSE,json=FALSE) {
  modelFile <- paste(modelName,".xml",sep='')
  nexusFile <- paste(modelName,".nexus",sep='')
  jsonFile  <- paste(modelName,".json",sep='')

  if (!file.exists(modelFile)) {
     stop(paste("Couldn't find model file:",modelFile))
  }

  #numTips <- getNumberSamples(modelFile)
  numTips <- 200
  maxTries <- 10
  genTrees <- list()
  # Loop for each tree
  for(treeNumber in 1:numTrees) {
    newickFile <- paste(modelName,"_",treeNumber,".nwk",sep='')
    # execute MASTER
    if (file.exists(nexusFile)) {
       print("removing nexus")
      file.remove(file=nexusFile)
    }
    treeGenerated <- FALSE
    numTries <- 0
    while((!treeGenerated)&&(numTries<maxTries)) {
      masterError <- system(paste(runMaster,modelFile))
      numTries <- numTries+1
      if (file.exists(newickFile)) {
      	 file.remove(file=newickFile)
      }
      # Check if succesful 
      treeGenerated <- processNexus(nexusFile, newickFile,numTips)
    }
    if (numTries >= maxTries) {
      warning("Error while generating MASTER trees: number of tries exceded")
      return(genTrees)
    }
    genTrees <- c(genTrees,newickFile)
    # tree generation succesful
    # showFromJSON(jsonFile)
  }
  return(genTrees)
}


showFromJSON <- function(jsonFile) {
  print("Showing JSON")
  print(jsonFile)
  if (!file.exists(jsonFile)) return(FALSE)
  d <- fromJSON(file=jsonFile)
  names(d)
  plot(d$t, d$S, 'l', ylim=c(0, max(d$S)), col='blue')
  lines(d$t, d$I0, col='red')
  lines(d$t, d$I1, col='orange')

  return(TRUE)
} # end showFromJSON


clargs <- commandArgs(trailingOnly = TRUE)
if (length(clargs) != 1) {
   stop("Must enter number of trees")
}
modelName <- "sir2Master"
numTrees <- strtoi(clargs[1])
if (is.na(numTrees)) {
  stop(paste('Number of trees should be an integer:',clargs[1]))
}
 
fileNames <- genMASTERTrees(master,modelName,numTrees)
unlink("*.json")
unlink("*.nexus")
print(paste("Number of trees generated:",length(fileNames)))
print(fileNames)