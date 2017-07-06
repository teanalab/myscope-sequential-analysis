import numpy as np


# functions and classes go here
def fb_alg(A_mat, O_mat, observ):
    # set up
    k = observ.size
    (n, m) = O_mat.shape
    prob_mat = np.zeros((n, k))
    fw = np.zeros((n, k + 1))
    bw = np.zeros((n, k + 1))
    # forward part
    fw[:, 0] = 1.0 / n
    for obs_ind in xrange(k):
        f_row_vec = np.matrix(fw[:, obs_ind])
        fw[:, obs_ind + 1] = f_row_vec * \
                             np.matrix(A_mat) * \
                             np.matrix(np.diag(O_mat[:, observ[obs_ind]]))
        fw[:, obs_ind + 1] = fw[:, obs_ind + 1] / np.sum(fw[:, obs_ind + 1])
    # backward part
    bw[:, -1] = 1.0
    for obs_ind in xrange(k, 0, -1):
        b_col_vec = np.matrix(bw[:, obs_ind]).transpose()
        bw[:, obs_ind - 1] = (np.matrix(A_mat) *
                              np.matrix(np.diag(O_mat[:, observ[obs_ind - 1]])) *
                              b_col_vec).transpose()
        bw[:, obs_ind - 1] = bw[:, obs_ind - 1] / np.sum(bw[:, obs_ind - 1])
    # combine it
    prob_mat = np.array(fw) * np.array(bw)
    prob_mat = prob_mat / np.sum(prob_mat, 0)
    # get out
    return prob_mat, fw, bw


def baum_welch(num_states, num_obs, observ):
    # allocate
    A_mat = np.ones((num_states, num_states))
    A_mat = A_mat / np.sum(A_mat, 1)
    O_mat = np.ones((num_states, num_obs))
    O_mat = O_mat / np.sum(O_mat, 1)
    theta = np.zeros((num_states, num_states, observ.size))
    while True:
        old_A = A_mat
        old_O = O_mat
        A_mat = np.ones((num_states, num_states))
        O_mat = np.ones((num_states, num_obs))
        # expectation step, forward and backward probs
        P, F, B = fb_alg(old_A, old_O, observ)
        # need to get transitional probabilities at each time step too
        for a_ind in xrange(num_states):
            for b_ind in xrange(num_states):
                for t_ind in xrange(observ.size):
                    theta[a_ind, b_ind, t_ind] = \
                        F[a_ind, t_ind] * \
                        B[b_ind, t_ind + 1] * \
                        old_A[a_ind, b_ind] * \
                        old_O[b_ind, observ[t_ind]]
        # form A_mat and O_mat
        for a_ind in xrange(num_states):
            for b_ind in xrange(num_states):
                A_mat[a_ind, b_ind] = np.sum(theta[a_ind, b_ind, :]) / \
                                      np.sum(P[a_ind, :])
        A_mat = A_mat / np.sum(A_mat, 1)
        for a_ind in xrange(num_states):
            for o_ind in xrange(num_obs):
                right_obs_ind = np.array(np.where(observ == o_ind)) + 1
                O_mat[a_ind, o_ind] = np.sum(P[a_ind, right_obs_ind]) / \
                                      np.sum(P[a_ind, 1:])
        O_mat = O_mat / np.sum(O_mat, 1)
        # compare
        if np.linalg.norm(old_A - A_mat) < .00001 and np.linalg.norm(old_O - O_mat) < .00001:
            break
    # get out
    return A_mat, O_mat