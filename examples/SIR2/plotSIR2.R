args <- commandArgs(trailingOnly=TRUE )

csvFile <- args[1]
pngFile <- paste(csvFile,".png",sep='')

traj <- read.csv(csvFile)
x <- traj[,1]

png(pngFile)

ymax <- max(traj[,2],traj[,3],traj[,4])
plot(x,traj[,2], 'lh', ylim=c(0,ymax*1.20), col='blue', xlab='time',ylab='Population')
lines(x,traj[,3],  col='red')
lines(x,traj[,4],  col='green')

legend("topleft",
      inset=.05,
      cex = 0.8,
      title="Legend",
      c("I0","I1","S"),
      horiz=TRUE,
      lty=c(1,1),
      lwd=c(2,2),
      col=c("blue","red","green"),
      bg="grey96")


dev.off()
