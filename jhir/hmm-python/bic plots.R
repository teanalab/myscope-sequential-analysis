
#library
library(ggplot2)
library(reshape2)
library(pROC)

par(mfrow=c(1, 2))

# melt data
r1 <- read.csv("/home/mehedi/teana/projects/myscope-sequential-analysis/jhir/hmm-python/results_succ.csv")
r2 <- read.csv("/home/mehedi/teana/projects/myscope-sequential-analysis/jhir/hmm-python/results_unsucc.csv")

# plot graph
plot(r1$n, r1$bic, col='red', type="l", ylab = "BIC", xlab = "# of hidden states", cex.axis=1, cex.lab=1, lwd=1)

# plot graph
plot(r2$n, r2$bic, col='red', type="l", ylab = "BIC", xlab = "# of hidden states", cex.axis=1, cex.lab=1, lwd=1)

