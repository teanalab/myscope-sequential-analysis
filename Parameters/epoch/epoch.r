#library
library(ggplot2)
library(reshape2)

# melt data
r1 <- read.csv("/home/mehedi/Desktop/Parameters/epoch/results.csv")

#plot graph
plot(r1$x1, r1$x3, col='blue', type="l", ylim=c(0.45, 0.70), ylab = "Loss", xlab = "Epoch", lwd=1)
lines(r1$x1, r1$x5, col='green', type="l", lwd=1)
lines(r1$x1, r1$x7, col='red', type="l", lwd=1)
legend('topright',c("val_loss", "test_loss", 'train_loss'), col=c('blue', 'green', 'red'), lwd=1)