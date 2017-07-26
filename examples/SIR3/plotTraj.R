

png("traj.png")

traj <- read.csv('trajSIR3.csv')
x <- traj[,1]
ymax <- max(traj[,2],traj[,3],traj[,4],traj[,5])
plot(x,traj[,2], 'lh', ylim=c(0,ymax*1.20), col='blue', xlab='time',ylab='Population')
lines(x,traj[,3],  col='red')
lines(x,traj[,4], col='green')
lines(x,traj[,5], col='cyan')  

legend("topright",
      inset=.05,
      cex = 1,
      title="Legend",
      c("I0","I1","I2","S"),
      horiz=TRUE,
      lty=c(1,1),
      lwd=c(2,2),
      col=c("blue","red","green","cyan"),
      bg="grey96")


dev.off()
