package me.titan.blockpartyevents.util

import java.util.*

/******************************************************************************
 * File: AliasMethod.java
 * Author: Keith Schwarz (htiek@cs.stanford.edu)
 *
 * An implementation of the alias method implemented using Vose's algorithm.
 * The alias method allows for efficient sampling of random values from a
 * discrete probability distribution (i.e. rolling a loaded die) in O(1) time
 * each after O(n) preprocessing time.
 *
 * For a complete writeup on the alias method, including the intuition and
 * important proofs, please see the article "Darts, Dice, and Coins: Smpling
 * from a Discrete Distribution" at
 *
 * http://www.keithschwarz.com/darts-dice-coins/
 */
class AliasMethod @JvmOverloads constructor(probabilities: MutableList<Double>?, random: Random? = Random()) {
    /* The random number generator used to sample from the distribution. */
    private val random: Random

    /* The probability and alias tables. */
    private val alias: IntArray
    private val probability: DoubleArray

    /**
     * Constructs a new AliasMethod to sample from a discrete distribution and
     * hand back outcomes based on the probability distribution.
     *
     *
     * Given as input a list of probabilities corresponding to outcomes 0, 1,
     * ..., n - 1, along with the random number generator that should be used
     * as the underlying generator, this constructor creates the probability
     * and alias tables needed to efficiently sample from this distribution.
     *
     * @param probabilities The list of probabilities.
     * @param random The random number generator
     */
    /**
     * Constructs a new AliasMethod to sample from a discrete distribution and
     * hand back outcomes based on the probability distribution.
     *
     *
     * Given as input a list of probabilities corresponding to outcomes 0, 1,
     * ..., n - 1, this constructor creates the probability and alias tables
     * needed to efficiently sample from this distribution.
     *
     * @param probabilities The list of probabilities.
     */
    init {
        /* Begin by doing basic structural checks on the inputs. */
        var probabilities = probabilities
        if (probabilities == null || random == null) throw NullPointerException()
        require(probabilities.size != 0) { "Probability vector must be nonempty." }

        /* Allocate space for the probability and alias tables. */
        probability = DoubleArray(probabilities.size)
        alias = IntArray(probabilities.size)

        /* Store the underlying generator. */
        this.random = random

        /* Compute the average probability and cache it for later use. */
        val average = 1.0 / probabilities.size

        /* Make a copy of the probabilities list, since we will be making
         * changes to it.
         */
        probabilities = ArrayList(probabilities)

        /* Create two stacks to act as worklists as we populate the tables. */
        val small: Deque<Int> = ArrayDeque()
        val large: Deque<Int> = ArrayDeque()

        /* Populate the stacks with the input probabilities. */
        for (i in probabilities.indices) {
            /* If the probability is below the average probability, then we add
             * it to the small list; otherwise we add it to the large list.
             */
            if (probabilities[i] >= average) large.add(i)
            else small.add(i)
        }

        /* As a note: in the mathematical specification of the algorithm, we
         * will always exhaust the small list before the big list.  However,
         * due to floating point inaccuracies, this is not necessarily true.
         * Consequently, this inner loop (which tries to pair small and large
         * elements) will have to check that both lists aren't empty.
         */
        while (!small.isEmpty() && !large.isEmpty()) {
            /* Get the index of the small and the large probabilities. */
            val less = small.removeLast()
            val more = large.removeLast()

            /* These probabilities have not yet been scaled up to be such that
             * 1/n is given weight 1.0.  We do this here instead.
             */
            probability[less] = probabilities[less] * probabilities.size
            alias[less] = more

            /* Decrease the probability of the larger one by the appropriate
             * amount.
             */
            probabilities[more] = probabilities[more] + probabilities[less] - average

            /* If the new probability is less than the average, add it into the
             * small list; otherwise add it to the large list.
             */
            if (probabilities[more] >= 1.0 / probabilities.size) large.add(more)
            else small.add(more)
        }

        /* At this point, everything is in one list, which means that the
         * remaining probabilities should all be 1/n.  Based on this, set them
         * appropriately.  Due to numerical issues, we can't be sure which
         * stack will hold the entries, so we empty both.
         */
        while (!small.isEmpty()) probability[small.removeLast()] = 1.0
        while (!large.isEmpty()) probability[large.removeLast()] = 1.0
    }

    /**
     * Samples a value from the underlying distribution.
     *
     * @return A random value sampled from the underlying distribution.
     */
    fun next(): Int {
        /* Generate a fair die roll to determine which column to inspect. */
        val column = random.nextInt(probability.size)

        /* Generate a biased coin toss to determine which option to pick. */
        val coinToss = random.nextDouble() < probability[column]

        /* Based on the outcome, return either the column or its alias. */
        return if (coinToss) column else alias[column]
    }
}