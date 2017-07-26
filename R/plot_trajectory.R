#!/usr/bin/Rscript
# NOTE replace the preceding line with the correct path to Rscript or littler
#~  ./plot_trajectory.R -h
#~  ./plot_trajectory.R -i sn_crf02ag_structure1.1.traj -l 0 -u 35 -d msm
#~  ./plot_trajectory.R -i sn_crf02ag_structure1.1.traj -l 0 -u 35 -d msm --log FALSE -o sn_crf02ag_st1.1.msm.pdf
#~  ./plot_trajectory.R -i sn_crf02ag_structure1.1.traj -l 0 -u 35 -d gp --log FALSE -o sn_crf02ag_st1.1.gp.pdf

if (! ('ggplot2' %in% installed.packages()[,'Package'] ) ){
	cat('plot_trajectory depends on the ggplot2 package. Please install this first. Quitting.\n')
	q(status=1)
}

if (! ('getopt' %in% installed.packages()[,'Package'] ) ){
	cat('plot_trajectory depends on the getopt package. Please install this first.\n')
	install.packages('getopt')
}

require(getopt)
require(ggplot2)

spec <- matrix( c( 
	'help', 'h', 2, 'logical', 
	
	'input', 'i', 1, 'character', 
	'deme', 'd', 1, 'character', 
	'time_lowerBound', 'l', 1, 'double', 
	'time_upperBound', 'u', 1, 'double', 
	
	'output', 'o', 2, 'character',
	'log', NA, 2, 'logical',
	
	'burnin_percent', 'b', '2', 'double', 
	'time_res', 'r', 2, 'integer', 
	'n_samples', 'n', 2, 'integer'
), byrow=TRUE, ncol = 4)
opt = getopt(spec)

HELP<- '-i : input file name should be a trajectory log file \n
--deme should be the name of the population to be plotted \n
--log FALSE : will plot without log y axis \n
-l <time> : first time point on time axis \n
-u <time> : last time point on time axis \n
By default, will based CI on 200 trajectories and 100 interpolated time points and 20% burnin \n'
if ( !is.null(opt$help) ) {
  cat(getopt(spec, usage=TRUE))
  cat (HELP)
  q(status=1)
}


infn <- opt$input
deme <- opt$deme
t0 <- opt$time_lowerBound
t1 <- opt$time_upperBound

ofn <- ifelse( is.null( opt$output), 'plot.pdf', opt$output )
logy <- ifelse( is.null(opt$log), TRUE, opt$log )
burnin_pc <- ifelse( is.null(opt$burnin_percent), 20, opt$burnin_percent )
time_res <-  ifelse( is.null(opt$time_res), 100, opt$time_res )
n <- ifelse( is.null( opt$n_samples), 200, opt$n_samples)


#~ R --args sn_crf02ag_structure1.1.traj 20 100 0 35 100 msm test.png
#~ infn <- cargs[1] 
#~ burnin_pc <- as.numeric( cargs[2] )
#~ time_res <- as.numeric( cargs[3] ) 
#~ t0 <- as.numeric( cargs[4] )
#~ t1 <- as.numeric( cargs[5] )
#~ n <- as.numeric( cargs[6] )
#~ deme <- cargs[7]
#~ ofn <- cargs[8]
#~ logy <- cargs[9]

##
X<- read.table( infn , header=TRUE)
Xs <- unique( X$Sample )
Xs <- Xs[ floor(seq(floor( (burnin_pc/100) * (length(Xs)+1)), length(Xs), length.out = n ) ) ]
X <- X[ X$Sample %in% Xs, ]


taxis <- seq( t0, t1, length.out = time_res )
Y <- sapply( Xs, function(sid){
	i <- which( X$Sample == sid )
	approx( X$t[i] , X[[deme]][i] , rule =2, xout=taxis )$y
})

pldf <- data.frame( x = taxis
 , y = sapply( 1:nrow(Y), function(k) median(Y[k,] ) )
 , lb = sapply( 1:nrow(Y), function(k) quantile(Y[k,], prob=.025 ) )
 , ub = sapply( 1:nrow(Y), function(k) quantile(Y[k,], prob=.975 ) )
)

pl0 <- ggplot( aes(x=x, y = y) ,  data = pldf ) + geom_line() + 
geom_ribbon( aes(ymin=lb, ymax=ub),  fill = 'blue', alpha = .15 ) + 
ylab(deme) + xlab('Time' )

if (logy) pl0 <- pl0 + scale_y_log10() 

ggsave( pl0, file = ofn )
