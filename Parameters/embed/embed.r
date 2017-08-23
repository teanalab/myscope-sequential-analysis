#library
library(ggplot2)
library(reshape2)

# melt data
r1 <- read.csv("/home/mehedi/Desktop/Parameters/embed/results.csv")

#plot graph
plot(r1$x1, r1$x3, col='blue', type="l", ylim=c(0.53, 0.62), ylab = "Loss", xlab = "Embedding dimension", lwd=1)
lines(r1$x1, r1$x5, col='green', type="l", lwd=1)
lines(r1$x1, r1$x7, col='red', type="l", lwd=1)
legend('topright',c("val_loss", "test_loss", 'train_loss'), col=c('blue', 'green', 'red'), lwd=1)
