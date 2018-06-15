
#library
library(ggplot2)
library(reshape2)
library(pROC)
library(calibrate)

par(mfrow=c(1, 2))

# melt data
r1 <- read.csv("/home/mehedi/Desktop/bic results/succ.csv")
r2 <- read.csv("/home/mehedi/Desktop/bic results/unsucc.csv")

# plot graph
plot(range(1,10), range(25000, 26500), type="n", ylab = "BIC", xlab = "# of hidden states in successful communications")
lines(r1$n, r1$bic, type="b", lwd=1.5, lty=1, col='black', pch=18)
points(x = 5, y = 25098.3168027, col = "red", pch = 1, cex = 2)
textxy(r1$n, r1$bic, labs=c("","25102","25122","25204","25098","25120",""), pos=3,  cex = 0.7)

# plot graph
plot(range(1,10), range(6000, 8000), type="n", ylab = "BIC", xlab = "# of hidden states in unsuccessful communications")
lines(r2$n, r2$bic, type="b", lwd=1.5, lty=1, col='black', pch=18)
points(x = 2, y = 6175.59727261, col = "red", pch = 1, cex = 2)
